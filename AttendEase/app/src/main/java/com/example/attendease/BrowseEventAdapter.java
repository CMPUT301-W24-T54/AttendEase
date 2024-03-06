package com.example.attendease;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class BrowseEventAdapter extends ArrayAdapter<Event> {
    public BrowseEventAdapter(@NonNull Context context, ArrayList<Event> Events) {
        super(context, 0,Events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.browse_event_content,
                    parent, false);
        } else {
            view = convertView;
        }
        Event event = getItem(position);
        ImageView image=view.findViewById(R.id.Poster);
        TextView event_name=view.findViewById(R.id.Event_name);
        TextView event_info=view.findViewById(R.id.event_info);
        event_name.setText(event.getTitle());
        Log.d("EventAdapter", "Event name: " + event.getTitle());
        String info=event.getLocation().toString()+"\n"+event.getDateTime().toDate().toString();
        event_info.setText(info);


        return view;
    }
}
