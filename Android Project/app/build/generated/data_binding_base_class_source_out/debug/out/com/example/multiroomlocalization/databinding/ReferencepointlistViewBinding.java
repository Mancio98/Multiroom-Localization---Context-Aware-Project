// Generated by view binder compiler. Do not edit!
package com.example.multiroomlocalization.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.multiroomlocalization.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ReferencepointlistViewBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button buttonConfermaSettings;

  @NonNull
  public final RecyclerView recyclerViewReferencePoint;

  private ReferencepointlistViewBinding(@NonNull ConstraintLayout rootView,
      @NonNull Button buttonConfermaSettings, @NonNull RecyclerView recyclerViewReferencePoint) {
    this.rootView = rootView;
    this.buttonConfermaSettings = buttonConfermaSettings;
    this.recyclerViewReferencePoint = recyclerViewReferencePoint;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ReferencepointlistViewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ReferencepointlistViewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.referencepointlist_view, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ReferencepointlistViewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonConfermaSettings;
      Button buttonConfermaSettings = ViewBindings.findChildViewById(rootView, id);
      if (buttonConfermaSettings == null) {
        break missingId;
      }

      id = R.id.recyclerViewReferencePoint;
      RecyclerView recyclerViewReferencePoint = ViewBindings.findChildViewById(rootView, id);
      if (recyclerViewReferencePoint == null) {
        break missingId;
      }

      return new ReferencepointlistViewBinding((ConstraintLayout) rootView, buttonConfermaSettings,
          recyclerViewReferencePoint);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
