package com.tuxing.app.util;

import android.media.MediaPlayer;

import io.vov.vitamio.widget.MediaPlayerControl;

public interface EducAudioPlayListener {
	void onCompletion(MediaPlayer mp);
	void onPrepared(MediaPlayer mp);
	void onError(MediaPlayer mp);
	void onBufferingUpdate(int percent);
	void bufferingStart();
	void bufferingEnd();
	void bufferingChange(int percent);
	void setMediaPlayerControl(MediaPlayerControl mMediaPlayerControl);
	boolean isShowing();
}
