package com.mohit.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

    }

    public void playMusic(View view) {
        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);
        Toast.makeText(this, "Playing Music", Toast.LENGTH_SHORT).show();
    }

    public void stopMusic(View view) {
        Intent serviceIntent = new Intent(this, MusicService.class);
        stopService(serviceIntent);
        Toast.makeText(this, "Stopping Music", Toast.LENGTH_SHORT).show();
    }
}
