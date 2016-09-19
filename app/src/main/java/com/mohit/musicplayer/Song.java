package com.mohit.musicplayer;

/**
 * Created by Mohit on 19-09-2016.
 */
public class Song {

    private final long songID;
    private final String songTitle;
    private final String songArtist;

    public Song(long songID, String songTitle, String songArtist) {
        this.songID = songID;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
    }



    public long getSongID() {
        return songID;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }
}
