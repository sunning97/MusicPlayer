package com.example.giangnguyen.musicplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Giang Nguyen on 14/11/2017.
 */

public class SongAdapter extends ArrayAdapter {
    MediaManager mediaManager;
    private Context context;
    private int res;
    List<Song> list;
    public SongAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context = context;
        this.list = objects;
        this.res = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.song_item_layout,parent,false);
            viewHolder.tv_songName = convertView.findViewById(R.id.tv_songName);
            viewHolder.tv_songArtist = convertView.findViewById(R.id.tv_songArtist);
            viewHolder.tv_songDuration = convertView.findViewById(R.id.tv_songDuration);

            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();

        mediaManager = new MediaManager(this.context);
        Song s = list.get(position);
        viewHolder.tv_songName.setText(s.getName());
        viewHolder.tv_songArtist.setText(s.getArtist());
        viewHolder.tv_songDuration.setText(mediaManager.getTimeSong(position));

        return convertView;
    }
    private class ViewHolder
    {
        TextView tv_songName, tv_songDuration, tv_songArtist;
    }
}
