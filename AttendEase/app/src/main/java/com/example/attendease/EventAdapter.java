package com.example.attendease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * This class represents a RecyclerView Adapter for displaying a list of events in either large or small card layouts.
 * This adapter provides the necessary methods to bind event data to the corresponding views.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    static final int TYPE_LARGE = 0;
    static final int TYPE_SMALL = 1;
    private List<Event> eventList;
    private Context context;
    private int viewType;
    private OnItemClickListener onItemClickListener;

    /**
     * Interface definition for a callback to be invoked when an item in the RecyclerView is clicked.
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * Sets the listener for item click events in the RecyclerView.
     * @param listener The listener to be set.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    
    /**
     * Constructs the EventAdapter.
     * @param context   The context in which the adapter is being used.
     * @param eventList The list of events to be displayed.
     * @param viewType  The type of view to be used for each row (TYPE_LARGE or TYPE_SMALL).
     */
    public EventAdapter(Context context, List<Event> eventList, int viewType) {
        this.context = context;
        this.eventList = eventList;
        this.viewType = viewType;
    }

    /**
     * Determines the view type of the item at the specified position.
     * @param position The position of the item.
     * @return The view type as an integer (TYPE_LARGE or TYPE_SMALL).
     */
    @Override
    public int getItemViewType(int position) {
        // Determines which layout to use for the row
        if (viewType == TYPE_LARGE) {
            return TYPE_LARGE;
        } else {
            return TYPE_SMALL;
        }
    }

    /**
     * Creates a new ViewHolder and initializes its views based on the view type.
     * @param parent   The parent that this view will eventually be attached to.
     * @param viewType The view type of the new View represented by an integer.
     * @return A new EventViewHolder.
     */
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

    /**
     * Binds the data at the specified position to the views in the ViewHolder.
     * @param holder   The ViewHolder to bind the data to.
     * @param position The position of the item in the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);

        // Set the click listener for the itemView
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, position);
            }
        });
    }


    /**
     * Returns the total number of items in the data set.
     * @return The total number of events - integer.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class for displaying event data in RecyclerView rows.
     */
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

    /**
     * Sets a new list of events for the adapter to display.
     * @param eventList The new list of events.
     */
    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }
}