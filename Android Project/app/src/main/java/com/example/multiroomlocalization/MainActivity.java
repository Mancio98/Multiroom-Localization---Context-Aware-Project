package com.example.multiroomlocalization;

import static com.example.multiroomlocalization.Bluetooth.BluetoothUtility.BT_CONNECT_AND_SCAN;
import static com.example.multiroomlocalization.LoginActivity.btUtility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.multiroomlocalization.Bluetooth.BluetoothUtility;
import com.example.multiroomlocalization.Bluetooth.ScanBluetoothService;
import com.example.multiroomlocalization.localization.ReferencePoint;
import com.example.multiroomlocalization.messages.connection.MessageLogin;
import com.example.multiroomlocalization.messages.connection.MessageSuccessfulLogin;
import com.example.multiroomlocalization.messages.localization.MessageEndMappingPhase;
import com.example.multiroomlocalization.messages.localization.MessageEndScanReferencePoint;
import com.example.multiroomlocalization.messages.localization.MessageFingerprint;
import com.example.multiroomlocalization.messages.localization.MessageNewReferencePoint;
import com.example.multiroomlocalization.messages.localization.MessageStartMappingPhase;
import com.example.multiroomlocalization.messages.localization.MessageStartScanReferencePoint;
import com.example.multiroomlocalization.socket.ClientSocket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ServiceConnection {


    private Activity activity;

    private ImageView playPause;

    private final Handler mHandler = new Handler();
    private ScanService scanService;
    private Canvas canvas;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText labelRoom;
    private Button cancel, save, upload;
    private ImageView imageView;
    private Bitmap mutableBitmap;
    private int imageViewHeight;
    private int imageViewWidth;
    private boolean first = true;
    private boolean newImage = true;
    private boolean passwordEmpty = true;
    private boolean nameEmpty = true;
    private String imageMap;
    private int len;
    private byte[] bb;

    private int intervalScan = 30000;

    private int timerScanTraining = 5 * 60000;

    private FloatingActionButton fab;

    protected ClientSocket clientSocket;
    private CropView cv;

    private final ArrayList<ReferencePoint> referencePoints = new ArrayList<ReferencePoint>();

    private final ArrayList<com.example.multiroomlocalization.localization.ScanResult> scanResultArrayList = new ArrayList<com.example.multiroomlocalization.localization.ScanResult>();

    private ReferencePointListAdapter adapterReferencePointList;

    private ScanBluetoothService scanBluetoothService;
    private boolean isBound=false;

    private ArrayList<Map> mapArrayList;

    public interface BluetoothPermCallback {
        void onGranted();
    }

    public static BluetoothPermCallback btPermissionCallback;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        setContentView(R.layout.activity_main);
        clientSocket = LoginActivity.client;

        fab = findViewById(R.id.fab);
        fab.setEnabled(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, 1);
            }
        });


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("CLOSE&#95;ALL");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MainActivity.this.finish();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        activity = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mapArrayList = extras.getParcelableArrayList("listMap");
        }

        imageView = (ImageView) findViewById(R.id.map);

        if (imageView.getDrawable() == null) {
            dialogBuilder = new AlertDialog.Builder(this);
            final View popup = getLayoutInflater().inflate(R.layout.popup_upload_map, null);
            upload = (Button) popup.findViewById(R.id.upload);

            dialogBuilder.setView(popup);
            dialog = dialogBuilder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();

            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // create an instance of the
                    // intent of the type image
                    Intent i = new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);

                    someActivityResultLauncher.launch(i);

                }
            });

        } else {

            imageView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

            imageView.setOnTouchListener(touchListener);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        btUtility = new BluetoothUtility(this, activity);

        Intent intent = new Intent(this, ScanBluetoothService.class);

        bindService(intent, this, Context.BIND_AUTO_CREATE);

    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x1 = (int) event.getX();
            int y1 = (int) event.getY();

            float tempx = ((float) x1 / (float) imageViewWidth) * 100;
            float tempy = ((float) y1 / (float) imageViewHeight) * 100;

            createDialog((int) tempx, (int) tempy);
            return false;
        }
    };

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            imageViewHeight = imageView.getHeight();
            imageViewWidth = imageView.getWidth();

            // don't forget to remove the listener to prevent being called again
            // by future layout events:
            if (first || newImage) {

                first = false;
                newImage = false;
                Bitmap bitmap = Bitmap.createScaledBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap(), imageViewWidth, imageViewHeight, true);
                mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                canvas = new Canvas(mutableBitmap);
            }
        }
    };

    private final Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            scanService.startScan();
            mHandler.postDelayed(scanRunnable, intervalScan);

        }
    };

    private void stopScan() {
        if (scanService!=null) scanService.registerReceiver(null);
        mHandler.removeCallbacks(scanRunnable);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri image = data.getData();
                        //Uri destImage = null;

                        if (null != image) {

                            dialog.cancel();
                            cv = new CropView(MainActivity.this, image);
                            cv.setCanceledOnTouchOutside(false);
                            cv.show();
                            cv.setTouchButton(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                                        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 3);
                                    }
                                    return false;
                                }
                            });
                        }



                    }
                }
            });

    private void startPopup() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popup = getLayoutInflater().inflate(R.layout.popup_text, null);
        Button next = (Button) popup.findViewById(R.id.buttonPopup);
        TextView text = (TextView) popup.findViewById(R.id.textPopup);

        final boolean[] firstTime = {true};

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstTime[0]) {
                    text.setText(getString(R.string.addReferencePointpopupSecond));
                    next.setText("Start");
                    firstTime[0] = false;
                } else {
                    imageView.setImageDrawable(new BitmapDrawable(getResources(), mutableBitmap));
                    dialog.cancel();
                }
            }
        });
        text.setText(getString(R.string.addReferencePointpopup));

        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            switch (requestCode) {
                case 1:
                    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 2);
                    break;
                case 2:
                    ClientSocket.Callback<String> callback = new ClientSocket.Callback<String>() {
                        @Override
                        public void onComplete(String result) {
                            ClientSocket.Callback<String> callback1 = new ClientSocket.Callback<String>() {
                                @Override
                                public void onComplete(String result) {
                                    fab.setEnabled(true);
                                    createPopupStartTraining();
                                }
                            };

                            clientSocket.sendImage(bb, callback1);
                        }
                    };


                    Gson gson = new Gson();

                    MessageStartMappingPhase message = new MessageStartMappingPhase(len);
                    String json = gson.toJson(message);

                    clientSocket.sendMessageStartMappingPhase(callback, json);
                    fab.setEnabled(false);

                    break;
                case 3:
                    String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), cv.getCropImageView().getCroppedImage(), "IMG_" + Calendar.getInstance().getTime(), null);


                    imageView.setImageURI(Uri.parse(path));
                    imageView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
                    imageView.setOnTouchListener(touchListener);

                    newImage = true;

                    cv.cancel();
                    t.start();

                    startPopup();
                    break;
            }
        }
    }


    private void createDialog(int x, int y) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popup = getLayoutInflater().inflate(R.layout.popup_room, null);
        labelRoom = (EditText) popup.findViewById(R.id.labelRoom);
        cancel = (Button) popup.findViewById(R.id.buttonCancel);
        save = (Button) popup.findViewById(R.id.buttonSave);

        save.setEnabled(false);

        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        labelRoom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                save.setEnabled(charSequence.toString().trim().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempX = (x * imageViewWidth) / 100;
                int tempY = (y * imageViewHeight) / 100;

                Paint mPaint = new Paint();
                mPaint.setColor(Color.RED);
                canvas.drawCircle(tempX, tempY, 20, mPaint);
                imageView.setImageDrawable(new BitmapDrawable(getResources(), mutableBitmap));
                ReferencePoint ref = new ReferencePoint(x, y, labelRoom.getText().toString());
                referencePoints.add(ref);
                Toast.makeText(getApplicationContext(), "Stanza aggiunta correttamente", Toast.LENGTH_LONG).show();
                fab.setEnabled(true);
                dialog.cancel();
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case BT_CONNECT_AND_SCAN:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if(grantResults.length > 1){
                        if(grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                            btPermissionCallback.onGranted();

                        }
                    } else {
                        btPermissionCallback.onGranted();

                    }
                } else {
                    Toast.makeText(this, "BT Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 2);
                } else {
                    Toast.makeText(MainActivity.this, "PROBLEM", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ClientSocket.Callback<String> callback = new ClientSocket.Callback<String>() {
                        @Override
                        public void onComplete(String result) {
                            ClientSocket.Callback<String> callback1 = new ClientSocket.Callback<String>() {
                                @Override
                                public void onComplete(String result) {
                                    fab.setEnabled(true);
                                    createPopupStartTraining();
                                }
                            };

                            clientSocket.sendImage(bb, callback1);
                        }
                    };
                    Gson gson = new Gson();

                    MessageStartMappingPhase message = new MessageStartMappingPhase(len);
                    String json = gson.toJson(message);
                    clientSocket.sendMessageStartMappingPhase(callback, json);
                    fab.setEnabled(false);
                } else {
                    Toast.makeText(MainActivity.this, "PROBLEM", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                    String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), cv.getCropImageView().getCroppedImage(), "IMG_" + Calendar.getInstance().getTime(), null);

                    imageView.setImageURI(Uri.parse(path));
                    imageView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
                    imageView.setOnTouchListener(touchListener);

                    newImage = true;

                    cv.cancel();
                    t.start();

                    startPopup();

                    break;
        }
    }

    Thread t = new Thread(){

        @Override
        public void run() {
            super.run();
            imageView.buildDrawingCache();
            Bitmap bmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bb = bos.toByteArray();
            imageMap = Base64.encodeToString(bb, 0);

            len = bb.length;
        }
    };

    private void createPopupRoomTraining(ReferencePoint point, int index) {
        dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        final View popup = getLayoutInflater().inflate(R.layout.layout_scan_training, null);
        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Button buttonNext = popup.findViewById(R.id.buttonTraining);

        TextView textRoom = (TextView) popup.findViewById(R.id.textRoom);
        textRoom.setText(point.getId());

        TextView timer = (TextView) popup.findViewById(R.id.timer);
        timer.setText("seconds remaining: 05:00");

        MessageNewReferencePoint message = new MessageNewReferencePoint(point.getX(), point.getY(), point);
        Gson gson = new Gson();
        String json = gson.toJson(message);
        clientSocket.sendMessageNewReferencePoint(json);

        CountDownTimer countDownTimer = new CountDownTimer(timerScanTraining, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("seconds remaining: " + new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished)));
            }

            public void onFinish() {

                buttonNext.setEnabled(true);
                buttonNext.setText("STOP");
                buttonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        timer.setText("Stanza completata");
                        mHandler.removeCallbacks(scanRunnable);

                        if (index + 1 < referencePoints.size()) {
                            buttonNext.setText("Next");
                            buttonNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.cancel();
                                    ClientSocket.Callback<String> callback = new ClientSocket.Callback<String>() {
                                        @Override
                                        public void onComplete(String result) {
                                            createPopupRoomTraining(referencePoints.get(index + 1), index + 1);
                                        }
                                    };
                                    Gson gson = new Gson();
                                    MessageEndScanReferencePoint message = new MessageEndScanReferencePoint();
                                    String json = gson.toJson(message);
                                    clientSocket.sendMessageEndScanReferencePoint(json, callback);
                                }
                            });
                        } else {
                            buttonNext.setText("Finish");
                            buttonNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    ClientSocket.Callback<String> callback = new ClientSocket.Callback<String>() {
                                        @Override
                                        public void onComplete(String result) {

                                            dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                            final View popup = getLayoutInflater().inflate(R.layout.popup_text, null);
                                            dialogBuilder.setView(popup);

                                            Button next = (Button) popup.findViewById(R.id.buttonPopup);
                                            TextView text = (TextView) popup.findViewById(R.id.textPopup);

                                            text.setText(R.string.settings);
                                            next.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    dialog.cancel();
                                                    dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                                    final View popup = getLayoutInflater().inflate(R.layout.referencepointlist_view, null);
                                                    dialogBuilder.setView(popup);

                                                    RecyclerView recyclerView = (RecyclerView) popup.findViewById(R.id.recyclerViewReferencePoint);

                                                    adapterReferencePointList = new ReferencePointListAdapter(referencePoints, getApplicationContext(), scanBluetoothService);

                                                    recyclerView.setAdapter(adapterReferencePointList);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                                                    Button buttonConferma = (Button) popup.findViewById(R.id.buttonConfermaSettings);

                                                    buttonConferma.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            adapterReferencePointList.closeBluetoothScan();
                                                            ArrayList<Settings> arrListSettings = new ArrayList<>();

                                                            for (int i = 0; i < referencePoints.size(); i++) {
                                                                arrListSettings.add(new Settings(referencePoints.get(i).getId(), referencePoints.get(i).getSpeaker(), referencePoints.get(i).getDnd()));
                                                            }

                                                            dialog.cancel();
                                                            dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                                            final View popup = getLayoutInflater().inflate(R.layout.layout_password_map, null);
                                                            dialogBuilder.setView(popup);
                                                            dialog = dialogBuilder.create();
                                                            dialog.setCanceledOnTouchOutside(false);
                                                            dialog.setCancelable(false);

                                                            Button button = popup.findViewById(R.id.buttonSendPassword);
                                                            button.setEnabled(false);
                                                            EditText name = popup.findViewById(R.id.editTextInputMapName);
                                                            EditText password = popup.findViewById(R.id.passwordInputMap);

                                                            password.addTextChangedListener(new TextWatcher() {
                                                                @Override
                                                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                                }

                                                                @Override
                                                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                                    if (charSequence.toString().trim().length() == 0) {
                                                                        passwordEmpty = true;
                                                                        button.setEnabled(false);
                                                                    } else {
                                                                        passwordEmpty = false;
                                                                        if (!nameEmpty) {
                                                                            button.setEnabled(true);
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void afterTextChanged(Editable editable) {

                                                                }
                                                            });

                                                            name.addTextChangedListener(new TextWatcher() {
                                                                @Override
                                                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                                }

                                                                @Override
                                                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                                    if (charSequence.toString().trim().length() == 0) {
                                                                        nameEmpty = true;
                                                                        button.setEnabled(false);
                                                                    } else {
                                                                        nameEmpty = false;
                                                                        if (!passwordEmpty) {
                                                                            button.setEnabled(true);
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void afterTextChanged(Editable editable) {

                                                                }
                                                            });

                                                            button.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    button.setEnabled(false);

                                                                    ClientSocket.Callback<String> callback = new ClientSocket.Callback<String>() {
                                                                        @Override
                                                                        public void onComplete(String result) {
                                                                            ClientSocket.Callback<String> callbackSuccessful = new ClientSocket.Callback<String>() {
                                                                                @Override
                                                                                public void onComplete(String result) {
                                                                                    Gson gson = new Gson();
                                                                                    ArrayList<Map> accountMap = gson.fromJson(result, MessageSuccessfulLogin.class).getMapList();
                                                                                    Intent changeActivity;
                                                                                    changeActivity = new Intent(MainActivity.this, ListMapActivity.class);
                                                                                    changeActivity.putExtra("Map", accountMap);
                                                                                    finish();
                                                                                    dialog.cancel();
                                                                                    startActivity(changeActivity);
                                                                                }
                                                                            };

                                                                            Gson gson = new Gson();
                                                                            MessageLogin message = new MessageLogin(LoginActivity.currentUser);
                                                                            String json = gson.toJson(message);
                                                                            clientSocket.sendMessageLogin(callbackSuccessful, null, json);

                                                                        }
                                                                    };

                                                                    String encoded = java.util.Base64.getEncoder().encodeToString(password.getText().toString().getBytes());

                                                                    Gson gson = new Gson();
                                                                    MessageEndMappingPhase message = new MessageEndMappingPhase(encoded, arrListSettings, name.getText().toString());
                                                                    String json = gson.toJson(message);
                                                                    clientSocket.sendMessageEndMappingPhase(callback, json);

                                                                }
                                                            });
                                                            dialog.show();
                                                        }
                                                    });

                                                    dialog = dialogBuilder.create();
                                                    dialog.setCanceledOnTouchOutside(false);
                                                    dialog.setCancelable(false);
                                                    dialog.show();

                                                }
                                            });

                                            dialog = dialogBuilder.create();
                                            dialog.setCanceledOnTouchOutside(false);
                                            dialog.setCancelable(false);
                                            dialog.show();


                                        }
                                    };

                                    Gson gson = new Gson();
                                    MessageEndScanReferencePoint message = new MessageEndScanReferencePoint();
                                    String json = gson.toJson(message);
                                    clientSocket.sendMessageEndScanReferencePoint(json, callback);
                                    dialog.cancel();
                                }

                            });
                        }
                    }
                });
            }
        };

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanService = new ScanService(getApplicationContext());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!scanService.getWifiManager().isScanThrottleEnabled()) {
                        intervalScan = 5000;
                    } else {
                        intervalScan = 30000;

                    }
                }

                mHandler.postDelayed(scanRunnable, 0);
                scanService.registerReceiver(broadcastReceiverScan);

                scanResultArrayList.clear();
                countDownTimer.start();

                Gson gson = new Gson();
                MessageStartScanReferencePoint message = new MessageStartScanReferencePoint();
                String json = gson.toJson(message);
                clientSocket.sendMessageStartScanReferencePoint(json);
                buttonNext.setEnabled(false);
            }
        });

        dialog.show();
    }

    private void createPopupStartTraining() {
        dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        final View popup = getLayoutInflater().inflate(R.layout.popup_text, null);
        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();

        Button next = (Button) popup.findViewById(R.id.buttonPopup);
        TextView text = (TextView) popup.findViewById(R.id.textPopup);

        text.setText(getString(R.string.trainingText));
        next.setText("Next");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                int i = 0;
                createPopupRoomTraining(referencePoints.get(i), i);
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    private final BroadcastReceiver broadcastReceiverScan = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                scanSuccess();
            } else {
                scanFailure();
            }
        }

        private void scanSuccess() {
            List<android.net.wifi.ScanResult> results = scanService.getWifiManager().getScanResults();
            List<com.example.multiroomlocalization.localization.ScanResult> listScan = new ArrayList<>();
            for (android.net.wifi.ScanResult res : results) {
                com.example.multiroomlocalization.localization.ScanResult scan = new com.example.multiroomlocalization.localization.ScanResult(res.BSSID, res.SSID, res.level);
                listScan.add(scan);
            }

            Gson gson = new Gson();

            MessageFingerprint message = new MessageFingerprint(listScan, System.currentTimeMillis());
            String json = gson.toJson(message);
            if(clientSocket != null )  clientSocket.sendMessageFingerprint(json);
        }

        private void scanFailure() {

            List<android.net.wifi.ScanResult> results = scanService.getWifiManager().getScanResults();
            List<com.example.multiroomlocalization.localization.ScanResult> listScan = new ArrayList<>();
            for ( android.net.wifi.ScanResult res : results ) {
                com.example.multiroomlocalization.localization.ScanResult scan = new com.example.multiroomlocalization.localization.ScanResult(res.BSSID,res.SSID,res.level);
                listScan.add(scan);
            }

            Gson gson = new Gson();
            MessageFingerprint message = new MessageFingerprint(listScan, System.currentTimeMillis());
            String json = gson.toJson(message);
            if(clientSocket != null )  clientSocket.sendMessageFingerprint(json);
       }


   };



    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(this);
            isBound = false;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {


        ScanBluetoothService.LocalBinder binder = (ScanBluetoothService.LocalBinder) service;
        scanBluetoothService = binder.getService();
        scanBluetoothService.setContext(getApplicationContext());

        isBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isBound = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
        stopScan();
        clientSocket = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent changeActivity;
        changeActivity = new Intent(this,ListMapActivity.class);
        changeActivity.putExtra("Map",mapArrayList);
        startActivity(changeActivity);
        finish();
    }
}
