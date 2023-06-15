// Generated by view binder compiler. Do not edit!
package com.example.multiroomlocalization.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.canhub.cropper.CropImageView;
import com.example.multiroomlocalization.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class CropViewBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CropImageView cropImageView;

  @NonNull
  public final Button salvaCrop;

  private CropViewBinding(@NonNull ConstraintLayout rootView, @NonNull CropImageView cropImageView,
      @NonNull Button salvaCrop) {
    this.rootView = rootView;
    this.cropImageView = cropImageView;
    this.salvaCrop = salvaCrop;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static CropViewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static CropViewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.crop_view, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static CropViewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cropImageView;
      CropImageView cropImageView = ViewBindings.findChildViewById(rootView, id);
      if (cropImageView == null) {
        break missingId;
      }

      id = R.id.salvaCrop;
      Button salvaCrop = ViewBindings.findChildViewById(rootView, id);
      if (salvaCrop == null) {
        break missingId;
      }

      return new CropViewBinding((ConstraintLayout) rootView, cropImageView, salvaCrop);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
