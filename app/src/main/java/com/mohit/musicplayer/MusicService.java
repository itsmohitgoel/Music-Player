package com.mohit.musicplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
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
        if (mMediaPlayer.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    // Binder class for interaction to service from activity
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void playMusic(){
        mMediaPlayer.reset();
        Song playSong = mSongs.get(mSongPosition);
        long currentSongID = playSong.getSongID();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSongID);

        try {
            mMediaPlayer.setDataSource(getApplicationContext(),trackUri);
        } catch (IOException e) {
            Log.e(LOG_TAG, "error setting datasource", e);
        }
        mMediaPlayer.prepareAsync();
    }

    public void setSong(int songIndex) {
        mSongPosition = songIndex;
    }

    public int getPostn(){ return mMediaPlayer.getCurrentPosition();}

    public int getDuration(){return  mMediaPlayer.getDuration();}

    public boolean isPlaying(){return mMediaPlayer.isPlaying();}

    public void pausePlayer(){mMediaPlayer.pause();}

    public void seek(int postn) { mMediaPlayer.seekTo(postn); }

    public void go(){mMediaPlayer.start();}

    public void playPrev(){
        mSongPosition--;
        if (mSongPosition < 0) {
            mSongPosition = mSongs.size() -1;
        }
        playMusic();
    }

    public void playNext(){
        mSongPosition++;
        if (mSongPosition >= mSongs.size()) {
            mSongPosition = 0;
        }
        playMusic();
    }
}
