package com.mohit.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Mohit on 14-09-2016.
 */
public class MusicService extends Service {
    private static final String LOG_TAG = MusicService.class.getSimpleName();
    MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, " Service created!");
        mediaPlayer = MediaPlayer.create(this, R.raw.the_greatest);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        Log.d(LOG_TAG, "Music started");
        if (!mediaPlayer.isLooping()) {
            Log.d(LOG_TAG, "Problem in playing music");
        }
        return 1;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Service destroyed");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
