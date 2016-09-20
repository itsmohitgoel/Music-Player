package com.mohit.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Mohit on 14-09-2016.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener{
    private static final String LOG_TAG = MusicService.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mSongs;
    private int mSongPosition;
    private final IBinder mBinder = new MusicBinder();

    private void initMusicPlayer() {
        //set player properties
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, " Service created!");
        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    public void setSongsList(ArrayList<Song> list) {
        this.mSongs = list;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    // Binder class for interaction to service from activity
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }



}
