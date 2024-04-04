package com.example.attendease;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.attendease.EventAdapter.TYPE_LARGE;
import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * This activity displays the list of events organized by the user.
 * It retrieves event data from Firestore and populates a RecyclerView with the events.
 * The user can navigate back to the OrganizerDashboardActivity from the bottom navigation.
 */
public class BrowseAllEventsAdmin extends AppCompatActivity {
    private static final int TYPE_LARGE = 0;
    private RecyclerView recyclerViewMyEvents;
    private EventAdapter adapterLarge;
    private ArrayList<Event> eventList = new ArrayList<>();
    private final Database database = Database.getInstance();
    private CollectionReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_my_events);

        recyclerViewMyEvents = findViewById(R.id.rvMyEvents);

        recyclerViewMyEvents.setLayoutManager(new LinearLayoutManager(this));

        adapterLarge = new EventAdapter(this, eventList, TYPE_LARGE);

        recyclerViewMyEvents.setAdapter(adapterLarge);

        eventsRef = database.getEventsRef();

        loadEventsFromFirestore();

        BottomNavigationView bottomNavigationView = findViewById(R.id.organizer_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.nav_events);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, OrganizerDashboardActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_events) {
                return true;
            }
            return false;
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        adapterLarge.setOnItemClickListener(new EventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Event event = eventList.get(position);
                Intent intent = new Intent(BrowseAllEventsAdmin.this, EventDetailsAdmin.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });
    };

    /**
     * Fetches events from Firestore based on the organizer's ID and populates the eventList.
     */
    private void loadEventsFromFirestore() {
        eventsRef
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        eventList.clear(); // Clear the list to avoid duplicating events
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            eventList.add(event); // Add the event to the list
                        }
                        adapterLarge.notifyDataSetChanged(); // Notify the adapter of data changes
                    } else {
                        // Handle the error or the case where the task is not successful
                        Log.w(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}
