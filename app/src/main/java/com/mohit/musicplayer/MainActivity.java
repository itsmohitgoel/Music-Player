package com.mohit.musicplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController;

import com.mohit.musicplayer.MusicService.MusicBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl{
    private ArrayList<Song> mSongList;
    private ListView mSongsView;
    private MusicService mMusicService;
    private Intent mPlayIntent;
    private boolean mIsServiceBound;
    private MusicController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mSongsView = (ListView) findViewById(R.id.listview_songs);
        mSongList = new ArrayList<Song>();
        super.onCreate(savedInstanceState);
        makeSongsList();

        Collections.sort(mSongList, new SongComparator());
        SongAdapter  adapter = new SongAdapter(this, mSongList);
        mSongsView.setAdapter(adapter);

        setController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, MusicService.class);
            MusicConnection musicConnection = new MusicConnection();
            bindService(mPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(mPlayIntent);
        }
    }

    private void makeSongsList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get column indices
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                long songId = musicCursor.getLong(idColumn);
                String songTitle = musicCursor.getString(titleColumn);
                String songArtist = musicCursor.getString(artistColumn);
                mSongList.add(new Song(songId, songTitle, songArtist));
            } while (musicCursor.moveToNext());
        }
    }

    public void songPicked(View view) {
        mMusicService.setSong((Integer) view.getTag(R.id.TAG_KEY_POSITION));
        mMusicService.playMusic();
        mController.show(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                mMusicService.setShuffle();
                break;
            case R.id.action_end:
                stopService(mPlayIntent);
                mMusicService = null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(mPlayIntent);
        mMusicService = null;
        super.onDestroy();
    }

    public void setController() {
        mController = new MusicController(this);

        mController.setPrevNextListeners(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 playNext();
                                             }
                                         },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPrevious();
                    }
                });

        mController.setMediaPlayer(this);
        mController.setAnchorView(mSongsView);
        mController.setEnabled(true);
    }
    @Override
    public void start() {
        mMusicService.go();
    }

    @Override
    public void pause() {
        mMusicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (mMusicService != null && mIsServiceBound && mMusicService.isPlaying()) {
            return  mMusicService.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mMusicService != null && mIsServiceBound && mMusicService.isPlaying()) {
            return mMusicService.getPostn();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        mMusicService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (mMusicService != null && mIsServiceBound) {
            return mMusicService.isPlaying();
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public void playPrevious(){
        mMusicService.playPrev();
        mController.show(0);
    }

    public void playNext() {
        mMusicService.playNext();
        mController.show(0);
    }

    /* * Helper Class for songs comparison * */
    private class SongComparator implements Comparator<Song> {

        @Override
        public int compare(Song lhs, Song rhs) {
            String title1 = lhs.getSongTitle();
            String title2 = rhs.getSongTitle();

            int result = title1.compareTo(title2);
            return result;
        }
    }

    //To bind the service, ServiceConnection instance is needed
    private class MusicConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder myBinder = (MusicBinder) service;
            mMusicService = myBinder.getService();
            mMusicService.setSongsList(mSongList);
            mIsServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsServiceBound = false;
        }
    };

    private class MusicController extends MediaController{

        public MusicController(Context context) {
            super(context);
        }

        @Override
        public void hide() {

        }
    }
}
