package com.mohit.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mohit on 20-09-2016.
 */
public class SongAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<Song> mSongsList;

    @Override
    public int getCount() {
        return mSongsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_song, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        Song currentSong = mSongsList.get(position);
        holder.tvTitle.setText(currentSong.getSongTitle());
        holder.tvArtist.setText(currentSong.getSongArtist());
        return convertView;
    }

    public SongAdapter(Context context, ArrayList<Song> songs) {
        this.mContext = context;
        this.mSongsList = songs;
    }
   public static class ViewHolder{
//        @BindView(R.id.textview_song_title) TextView tvTitle;
//        @BindView(R.id.textview_song_artist) TextView tvArtist;

        final TextView tvTitle;
        final TextView tvArtist;
        public ViewHolder(View view) {
//            ButterKnife.bind(view);
            tvTitle = (TextView) view.findViewById(R.id.textview_song_title);
            tvArtist = (TextView) view.findViewById(R.id.textview_song_artist);
        }
    }
}
