// Generated by view binder compiler. Do not edit!
package com.example.multiroomlocalization.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.multiroomlocalization.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RegistrationLayoutBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonRegistration;

  @NonNull
  public final EditText editMailRegistration;

  @NonNull
  public final EditText editPasswordRegistration;

  private RegistrationLayoutBinding(@NonNull LinearLayout rootView,
      @NonNull Button buttonRegistration, @NonNull EditText editMailRegistration,
      @NonNull EditText editPasswordRegistration) {
    this.rootView = rootView;
    this.buttonRegistration = buttonRegistration;
    this.editMailRegistration = editMailRegistration;
    this.editPasswordRegistration = editPasswordRegistration;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RegistrationLayoutBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RegistrationLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.registration_layout, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RegistrationLayoutBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonRegistration;
      Button buttonRegistration = ViewBindings.findChildViewById(rootView, id);
      if (buttonRegistration == null) {
        break missingId;
      }

      id = R.id.editMailRegistration;
      EditText editMailRegistration = ViewBindings.findChildViewById(rootView, id);
      if (editMailRegistration == null) {
        break missingId;
      }

      id = R.id.editPasswordRegistration;
      EditText editPasswordRegistration = ViewBindings.findChildViewById(rootView, id);
      if (editPasswordRegistration == null) {
        break missingId;
      }

      return new RegistrationLayoutBinding((LinearLayout) rootView, buttonRegistration,
          editMailRegistration, editPasswordRegistration);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
