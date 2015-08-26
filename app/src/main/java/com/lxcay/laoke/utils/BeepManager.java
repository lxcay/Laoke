package com.lxcay.laoke.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lxcay.laoke.R;

import java.io.IOException;

public final class BeepManager {

	private static final String TAG = BeepManager.class.getSimpleName();

	private static final float BEEP_VOLUME = 0.10f;
	private static final long VIBRATE_DURATION = 200L;

	private final Context context;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private boolean vibrate;

	public static final String KEY_VIBRATE = "vibrator";
	public static final String KEY_PLAY_BEEP = "noice";

	public BeepManager(Context context) {
		this.context = context;
		this.mediaPlayer = null;
		updatePrefs();
	}

	public void updatePrefs() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		playBeep = shouldBeep(prefs, context);
		vibrate = prefs.getBoolean(KEY_VIBRATE, true);
		if (playBeep && mediaPlayer == null) {
			mediaPlayer = buildMediaPlayer(context);
		}
	}

	public void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private static boolean shouldBeep(SharedPreferences prefs, Context activity) {
		boolean shouldPlayBeep = prefs.getBoolean(KEY_PLAY_BEEP, true);
		if (shouldPlayBeep) {
			// See if sound settings overrides this
			AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
			if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
				shouldPlayBeep = false;
			}
		}
		return shouldPlayBeep;
	}

	private static MediaPlayer buildMediaPlayer(Context activity) {
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		// 播放完毕的监听
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer player) {
				player.seekTo(0);
			}
		});

		AssetFileDescriptor file = activity.getResources().openRawResourceFd(R.raw.beep);
		try {
			mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
			file.close();
			mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
			mediaPlayer.prepare();
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			mediaPlayer = null;
		}
		return mediaPlayer;
	}

}
