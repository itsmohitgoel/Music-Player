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
import android.widget.ListView;

import com.mohit.musicplayer.MusicService.MusicBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Song> mSongList;
    private ListView mSongsView;
    private MusicService mMusicService;
    private Intent mPlayIntent;
    private boolean mIsServiceBound;

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
}
