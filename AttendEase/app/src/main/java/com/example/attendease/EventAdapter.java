package com.example.attendease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    static final int TYPE_LARGE = 0;
    static final int TYPE_SMALL = 1;
    private List<Event> eventList;
    private Context context;
    private int viewType;

    public EventAdapter(Context context, List<Event> eventList, int viewType) {
        this.context = context;
        this.eventList = eventList;
        this.viewType = viewType;
    }

    @Override
    public int getItemViewType(int position) {
        // Determines which layout to use for the row
        if (viewType == TYPE_LARGE) {
            return TYPE_LARGE;
        } else {
            return TYPE_SMALL;
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        if (viewType == TYPE_LARGE) {
            view = inflater.inflate(R.layout.event_card_large, parent, false);
        } else {
            view = inflater.inflate(R.layout.event_card_small, parent, false);
        }

        return new EventViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView locationTextView;

        EventViewHolder(View itemView, int viewType) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.textView3);
            if (viewType == TYPE_LARGE) {
                locationTextView = itemView.findViewById(R.id.textView4);
            } else {
                locationTextView = null;
            }
        }

        void bind(Event event) {
            titleTextView.setText(event.getTitle());
            if (locationTextView != null) {
                locationTextView.setText(event.getLocation());
            }
            // Other bindings potentially
        }
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }
}