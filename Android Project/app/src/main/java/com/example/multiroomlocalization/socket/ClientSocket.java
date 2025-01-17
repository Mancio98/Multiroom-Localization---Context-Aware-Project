package com.example.multiroomlocalization.socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.multiroomlocalization.ScanService;
import com.example.multiroomlocalization.messages.connection.MessageAcknowledge;
import com.example.multiroomlocalization.messages.connection.MessageConnectionClose;
import com.example.multiroomlocalization.messages.connection.MessageImageFinish;
import com.example.multiroomlocalization.messages.connection.MessageKeepAlive;
import com.example.multiroomlocalization.messages.connection.MessageMappingPhaseFinish;
import com.example.multiroomlocalization.messages.connection.MessageReferencePointFinish;
import com.example.multiroomlocalization.messages.connection.MessageRegistrationSuccessful;
import com.example.multiroomlocalization.messages.connection.MessageRegistrationUnsuccessful;
import com.example.multiroomlocalization.messages.connection.MessageSubscriptionSuccessful;
import com.example.multiroomlocalization.messages.connection.MessageSubscriptionUnsuccessful;
import com.example.multiroomlocalization.messages.connection.MessageSuccessfulLogin;
import com.example.multiroomlocalization.messages.connection.MessageUnsuccessfulLogin;
import com.example.multiroomlocalization.messages.connection.MessageUpdateMapList;
import com.example.multiroomlocalization.messages.localization.MessageImage;
import com.example.multiroomlocalization.messages.localization.MessageMapDetails;
import com.example.multiroomlocalization.messages.music.MessageRequestPlaylist;
import com.example.multiroomlocalization.messages.speaker.MessageChangeReferencePoint;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClientSocket extends Thread {
    private Socket socket;
    private static DataInputStream dataIn;
    private static DataOutputStream dataOut;
    private final Handler mHandler = new Handler();
    private ScanService scanService;


    private Context context;
    private final Gson gson = new Gson();
    private IncomingMsgHandler incomingMsgHandler;
    private Callback<String> reqPlaylistCallback;
    private Callback<String> startMappingPhaseCallback;
    private Callback<String> loginSuccessfulCallback;
    private Callback<String> loginUnsuccessfulCallback;
    private Callback<String> endMappingPhaseCallback;
    private Callback<String> registrationSuccessfulCallback;
    private Callback<String> registrationUnsuccessfulCallback;
    private Callback<String> imageCallback;
    private Callback<String> endReferencePointCallback;
    private Callback<String> callbackChangeReferencepoint;
    private Callback<String> mapDetailsCallback;
    private Callback<String> callbackSettings;
    private Callback<String> callbackSubscriptionSuccessful;
    private Callback<String> callbackSubscriptionUnsuccessful;

    byte[] bb;
    private boolean blockSenderFingerprint = false;

    public interface Callback<R> {
        void onComplete(R result);
    }

    private class IncomingMsgHandler extends Thread {

        Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void run() {
            super.run();

            while(!isInterrupted()){
                try {
                    String msg = dataIn.readUTF();

                    String messageType = gson.fromJson(msg, JsonObject.class).get("type").getAsString();

                    if (messageType.equals(MessageImage.type)) {
                        try {
                            dataOut.writeUTF(gson.toJson(new MessageAcknowledge()));
                            dataOut.flush();

                            setBb(new byte[gson.fromJson(msg,MessageImage.class).getNByte()]);
                            if (bb.length > 0) {
                                dataIn.readFully(bb, 0, bb.length); // read the message
                            }
                            sendMessage(gson.toJson(new MessageImageFinish()), false, null);
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    msgHandler(msg);
                }
                catch(SocketTimeoutException ste){
                    Gson gson = new Gson();
                    MessageKeepAlive message = new MessageKeepAlive();
                    String json = gson.toJson(message);
                    sendMessage(json,false,null);

                }
                catch (IOException e) {

                    e.printStackTrace();
                    Intent intent1 = new Intent("CLOSE&#95;ALL");
                    context.sendBroadcast(intent1);

                }
            }
        }

        public IncomingMsgHandler() {}

        private void msgHandler(String msg){
            String messageType = gson.fromJson(msg, JsonObject.class).get("type").getAsString();

            if(messageType.equals(MessageChangeReferencePoint.type)){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callbackChangeReferencepoint.onComplete(msg);

                    }
                });
            }
            else if(messageType.equals(MessageRequestPlaylist.type)){

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        reqPlaylistCallback.onComplete(msg);
                    }
                });

            }

            else if(messageType.equals(MessageAcknowledge.type)){
                handler.post(new Runnable() {
                    @Override
                    public void run() { startMappingPhaseCallback.onComplete(msg);
                    }
                });
            }

            else if(messageType.equals(MessageSuccessfulLogin.type)){
                handler.post(new Runnable() {
                    @Override
                    public void run() { loginSuccessfulCallback.onComplete(msg);
                    }
                });
            }
            else if (messageType.equals(MessageUnsuccessfulLogin.type)){
                handler.post(new Runnable() {
                    @Override
                    public void run() { loginUnsuccessfulCallback.onComplete(msg);
                    }
                });
            }
            else if (messageType.equals(MessageRegistrationSuccessful.type)){
                handler.post(new Runnable() {
                    @Override
                    public void run() { registrationSuccessfulCallback.onComplete(msg);
                    }
                });
            }
            else if (messageType.equals(MessageRegistrationUnsuccessful.type)){
                handler.post(new Runnable() {
                    @Override
                    public void run() { registrationUnsuccessfulCallback.onComplete(msg);
                    }
                });
            }
            else if (messageType.equals((MessageImageFinish.type))){
                handler.post(new Runnable() {
                    @Override
                    public void run() { imageCallback.onComplete(msg);
                    }
                });
            }
            else if(messageType.equals(MessageMappingPhaseFinish.type)){
                handler.post(new Runnable() {

                    @Override
                    public void run() { endMappingPhaseCallback.onComplete(msg);
                    }
                });
            }
            else if(messageType.equals(MessageReferencePointFinish.type)){
                handler.post(new Runnable() {
                    @Override
                    public void run() { endReferencePointCallback.onComplete(msg);
                    }
                });
            }

            else if(messageType.equals(MessageMapDetails.type)){
                handler.post(new Runnable() {
                    @Override
                    public void run() { mapDetailsCallback.onComplete(msg);
                    }
                });
            }
            else if(messageType.equals(MessageUpdateMapList.type)){
                handler.post(new Runnable() {
                    @Override
                    public void run() { callbackSettings.onComplete(msg);
                    }
                });
            }
            else if(messageType.equals(MessageSubscriptionSuccessful.type)){
                handler.post(new Runnable() {
                    @Override
                    public void run() { callbackSubscriptionSuccessful.onComplete(msg);
                    }
                });
            }
            else if(messageType.equals(MessageSubscriptionUnsuccessful.type)){
                handler.post(new Runnable() {
                    @Override
                    public void run() { callbackSubscriptionUnsuccessful.onComplete(msg);
                    }
                });
            }

        }
    }

    public ClientSocket(Context context) { this.context = context; }

    public static OutputStream getDataOutputStream() {
        return dataOut;
    }

    public void setBb(byte[] bb) { this.bb = bb; }

    public byte[] getBb() { return bb; }


    @Override
    public void run() {
        super.run();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            String ip = "172.20.10.2";
            int port = 50000;
            socket = new Socket(ip, port);
            socket.setSoTimeout(60000);
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Socket Connesso", Toast.LENGTH_LONG).show();
                }
            });

        }
        catch(Exception e){
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            e.printStackTrace();
        }

        scanService = new ScanService(context);

        startIncomingMsgHandler();
    }

    private void startIncomingMsgHandler(){
        incomingMsgHandler = new IncomingMsgHandler();
        incomingMsgHandler.start();
    }

    public boolean isMessageHandlerRunning(){
        if(incomingMsgHandler == null)
            return false;
        else
            return incomingMsgHandler.isAlive();
    }


    public void sendImage(byte[] bb,Callback<String> callback){
        imageCallback = callback;
        sendMessage(null,true,bb);
    }

    public void sendMessageSettings(String message,Callback<String> callback){
        callbackSettings=callback;
        sendMessage(message,false,null);
    }

    public void setCallbackChangeReferencePoint(Callback<String> callback){
        callbackChangeReferencepoint = callback;
    }


    public void sendMessageReqPlaylist(Callback<String> callback){
        reqPlaylistCallback = callback;
        String message = gson.toJson(new MessageRequestPlaylist());
        sendMessage(message,false,null);

    }

    public void sendMessageMapSubscription(String message,Callback<String> subscriptionSuccessful,Callback<String> subscriptionUnsuccessful){
        callbackSubscriptionSuccessful = subscriptionSuccessful;
        callbackSubscriptionUnsuccessful = subscriptionUnsuccessful;
        sendMessage(message, false, null);
    }

    public void sendMessageMapRequest(Callback<String> callback, String message){
        mapDetailsCallback = callback;
        sendMessage(message,false,null);
    }
    public void sendMessageStartMappingPhase(Callback<String> callback, String message){
        startMappingPhaseCallback = callback;
        sendMessage(message,false,null);
    }

    public void sendMessageLogin(Callback<String> callback,Callback<String> callback2, String message){
        loginSuccessfulCallback = callback;
        loginUnsuccessfulCallback = callback2;
        sendMessage(message,false,null);
    }

    public void sendMessageEndMappingPhase(Callback<String> callback,String message){
        endMappingPhaseCallback = callback;
        sendMessage(message,false,null);
    }

    public void sendMessageStartScanReferencePoint(String message){sendMessage(message,false,null);}

    public void sendMessageNewReferencePoint(String message){sendMessage(message,false,null);}

    public void sendMessageEndScanReferencePoint(String message,Callback<String>callback){
        endReferencePointCallback = callback;
        sendMessage(message,false,null);
    }

    public void sendMessageFingerprint(String message){
        if(!blockSenderFingerprint)
            sendMessage(message,false,null);
    }

    public void sendMessageRegistration(Callback<String> callback,Callback<String> callback2,String message){
        registrationSuccessfulCallback = callback;
        registrationUnsuccessfulCallback = callback2;
        sendMessage(message,false,null);
    }


    public void setSenderFingerprint(boolean set){
        blockSenderFingerprint = set;
    }

    public void setContext(Context context){
        this.context = context;
    }

    private void sendMessage(String message,Boolean image,byte[] bb){

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (image) {
                    try {
                        dataOut.write(bb);
                        dataOut.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        dataOut.writeUTF(message);
                        dataOut.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });

    }


    private final Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            scanService.startScan();

        }
    };

    private void stopScan() {
        mHandler.removeCallbacks(scanRunnable);
    }



    public void closeConnection() {
        Executor executor = Executors.newSingleThreadExecutor();
        MessageConnectionClose msg = new MessageConnectionClose();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String json = gson.toJson(msg);
                    if(dataOut != null && socket != null) {
                        dataOut.writeUTF(json);
                        dataOut.flush();
                        socket.close();
                        dataIn.close();
                        dataOut.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        if(incomingMsgHandler != null) {
            if(incomingMsgHandler.isAlive())
                incomingMsgHandler.interrupt();
        }

        interrupt();
    }
}
