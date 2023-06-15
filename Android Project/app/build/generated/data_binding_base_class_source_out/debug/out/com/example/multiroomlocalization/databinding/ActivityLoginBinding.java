// Generated by view binder compiler. Do not edit!
package com.example.multiroomlocalization.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.multiroomlocalization.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityLoginBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView RegistrationText;

  @NonNull
  public final Button buttonRegistration;

  @NonNull
  public final EditText editMailRegistration;

  @NonNull
  public final EditText editPasswordRegistration;

  @NonNull
  public final TextView passwordText;

  @NonNull
  public final TextView textDnd;

  @NonNull
  public final TextView textRegistration;

  @NonNull
  public final TextView textViewLink;

  @NonNull
  public final TextView usernameText;

  private ActivityLoginBinding(@NonNull LinearLayout rootView, @NonNull TextView RegistrationText,
      @NonNull Button buttonRegistration, @NonNull EditText editMailRegistration,
      @NonNull EditText editPasswordRegistration, @NonNull TextView passwordText,
      @NonNull TextView textDnd, @NonNull TextView textRegistration, @NonNull TextView textViewLink,
      @NonNull TextView usernameText) {
    this.rootView = rootView;
    this.RegistrationText = RegistrationText;
    this.buttonRegistration = buttonRegistration;
    this.editMailRegistration = editMailRegistration;
    this.editPasswordRegistration = editPasswordRegistration;
    this.passwordText = passwordText;
    this.textDnd = textDnd;
    this.textRegistration = textRegistration;
    this.textViewLink = textViewLink;
    this.usernameText = usernameText;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_login, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityLoginBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.RegistrationText;
      TextView RegistrationText = ViewBindings.findChildViewById(rootView, id);
      if (RegistrationText == null) {
        break missingId;
      }

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

      id = R.id.passwordText;
      TextView passwordText = ViewBindings.findChildViewById(rootView, id);
      if (passwordText == null) {
        break missingId;
      }

      id = R.id.textDnd;
      TextView textDnd = ViewBindings.findChildViewById(rootView, id);
      if (textDnd == null) {
        break missingId;
      }

      id = R.id.textRegistration;
      TextView textRegistration = ViewBindings.findChildViewById(rootView, id);
      if (textRegistration == null) {
        break missingId;
      }

      id = R.id.textViewLink;
      TextView textViewLink = ViewBindings.findChildViewById(rootView, id);
      if (textViewLink == null) {
        break missingId;
      }

      id = R.id.usernameText;
      TextView usernameText = ViewBindings.findChildViewById(rootView, id);
      if (usernameText == null) {
        break missingId;
      }

      return new ActivityLoginBinding((LinearLayout) rootView, RegistrationText, buttonRegistration,
          editMailRegistration, editPasswordRegistration, passwordText, textDnd, textRegistration,
          textViewLink, usernameText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
