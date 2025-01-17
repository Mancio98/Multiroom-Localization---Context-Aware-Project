// Generated by view binder compiler. Do not edit!
package com.example.multiroomlocalization.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.multiroomlocalization.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutLiveActivityBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final CoordinatorLayout activityLiveLayout;

  @NonNull
  public final TextView audioTime;

  @NonNull
  public final ImageButton closePlaylistButton;

  @NonNull
  public final LinearLayout controllerButtons;

  @NonNull
  public final ImageView mapView;

  @NonNull
  public final ImageButton nexttrack;

  @NonNull
  public final ImageButton openplaylist;

  @NonNull
  public final ListView playlistView;

  @NonNull
  public final ImageButton playpause;

  @NonNull
  public final ImageButton previoustrack;

  @NonNull
  public final SeekBar seekBar;

  @NonNull
  public final FloatingActionButton settingsButton;

  @NonNull
  public final LinearLayout timeControl;

  private LayoutLiveActivityBinding(@NonNull CoordinatorLayout rootView,
      @NonNull CoordinatorLayout activityLiveLayout, @NonNull TextView audioTime,
      @NonNull ImageButton closePlaylistButton, @NonNull LinearLayout controllerButtons,
      @NonNull ImageView mapView, @NonNull ImageButton nexttrack, @NonNull ImageButton openplaylist,
      @NonNull ListView playlistView, @NonNull ImageButton playpause,
      @NonNull ImageButton previoustrack, @NonNull SeekBar seekBar,
      @NonNull FloatingActionButton settingsButton, @NonNull LinearLayout timeControl) {
    this.rootView = rootView;
    this.activityLiveLayout = activityLiveLayout;
    this.audioTime = audioTime;
    this.closePlaylistButton = closePlaylistButton;
    this.controllerButtons = controllerButtons;
    this.mapView = mapView;
    this.nexttrack = nexttrack;
    this.openplaylist = openplaylist;
    this.playlistView = playlistView;
    this.playpause = playpause;
    this.previoustrack = previoustrack;
    this.seekBar = seekBar;
    this.settingsButton = settingsButton;
    this.timeControl = timeControl;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutLiveActivityBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutLiveActivityBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_live_activity, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutLiveActivityBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      CoordinatorLayout activityLiveLayout = (CoordinatorLayout) rootView;

      id = R.id.audio_time;
      TextView audioTime = ViewBindings.findChildViewById(rootView, id);
      if (audioTime == null) {
        break missingId;
      }

      id = R.id.closePlaylistButton;
      ImageButton closePlaylistButton = ViewBindings.findChildViewById(rootView, id);
      if (closePlaylistButton == null) {
        break missingId;
      }

      id = R.id.controller_buttons;
      LinearLayout controllerButtons = ViewBindings.findChildViewById(rootView, id);
      if (controllerButtons == null) {
        break missingId;
      }

      id = R.id.mapView;
      ImageView mapView = ViewBindings.findChildViewById(rootView, id);
      if (mapView == null) {
        break missingId;
      }

      id = R.id.nexttrack;
      ImageButton nexttrack = ViewBindings.findChildViewById(rootView, id);
      if (nexttrack == null) {
        break missingId;
      }

      id = R.id.openplaylist;
      ImageButton openplaylist = ViewBindings.findChildViewById(rootView, id);
      if (openplaylist == null) {
        break missingId;
      }

      id = R.id.playlist_view;
      ListView playlistView = ViewBindings.findChildViewById(rootView, id);
      if (playlistView == null) {
        break missingId;
      }

      id = R.id.playpause;
      ImageButton playpause = ViewBindings.findChildViewById(rootView, id);
      if (playpause == null) {
        break missingId;
      }

      id = R.id.previoustrack;
      ImageButton previoustrack = ViewBindings.findChildViewById(rootView, id);
      if (previoustrack == null) {
        break missingId;
      }

      id = R.id.seekBar;
      SeekBar seekBar = ViewBindings.findChildViewById(rootView, id);
      if (seekBar == null) {
        break missingId;
      }

      id = R.id.settingsButton;
      FloatingActionButton settingsButton = ViewBindings.findChildViewById(rootView, id);
      if (settingsButton == null) {
        break missingId;
      }

      id = R.id.time_control;
      LinearLayout timeControl = ViewBindings.findChildViewById(rootView, id);
      if (timeControl == null) {
        break missingId;
      }

      return new LayoutLiveActivityBinding((CoordinatorLayout) rootView, activityLiveLayout,
          audioTime, closePlaylistButton, controllerButtons, mapView, nexttrack, openplaylist,
          playlistView, playpause, previoustrack, seekBar, settingsButton, timeControl);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
