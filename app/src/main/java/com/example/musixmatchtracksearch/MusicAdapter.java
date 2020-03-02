package com.example.musixmatchtracksearch;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MusicAdapter extends ArrayAdapter<Tracks> {

    public MusicAdapter(@NonNull Context context, int resource, @NonNull List<Tracks> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Tracks music = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_view, parent, false);
        }

        TextView tvTrack = convertView.findViewById(R.id.tvTitleTrackData);
        TextView tvArtist = convertView.findViewById(R.id.tvTrackPriceData);
        TextView tvAlbum = convertView.findViewById(R.id.tvArtistNameData);
        TextView tvDate = convertView.findViewById(R.id.tvDateData);

        tvTrack.setText(music.track_name);
        tvArtist.setText(music.artist_name);
        tvAlbum.setText(music.album_name);
        tvDate.setText(music.date);

        return convertView;
    }
}
