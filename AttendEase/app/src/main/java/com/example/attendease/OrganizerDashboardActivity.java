package com.example.attendease;

import static com.example.attendease.EventAdapter.TYPE_LARGE;
import static com.example.attendease.EventAdapter.TYPE_SMALL;
import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrganizerDashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerViewUpcomingEvent;
    private RecyclerView recyclerViewMyEvents;
    private EventAdapter adapterLarge;
    private EventAdapter adapterSmall;
    private ArrayList<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_dashboard);

        recyclerViewUpcomingEvent = findViewById(R.id.rvUpcomingEvent);
        recyclerViewMyEvents = findViewById(R.id.rvMyEvents);

        recyclerViewUpcomingEvent.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMyEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterLarge = new EventAdapter(this, eventList, TYPE_LARGE);
        adapterSmall = new EventAdapter(this, eventList, TYPE_SMALL);

        recyclerViewUpcomingEvent.setAdapter(adapterLarge);
        recyclerViewMyEvents.setAdapter(adapterSmall);

        loadEventsFromFirestore();
        setUpFabCreateEvent();
    }

    private void loadEventsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String organizerId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Query for the closest upcoming event
        db.collection("events")
                .whereEqualTo("organizerId", organizerId)
                .orderBy("dateTime")
                .limit(1) // Ensures only one result is returned for the upcoming event
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Event upcomingEvent = task.getResult().getDocuments().get(0).toObject(Event.class);
                        eventList.clear(); // Clear the list to ensure only one event is in there
                        eventList.add(upcomingEvent); // Add the single upcoming event
                        adapterLarge.notifyDataSetChanged(); // Notify the adapter for the upcoming event

                        // Now query for the next three events
                        db.collection("events")
                                .whereEqualTo("organizerId", organizerId)
                                .orderBy("dateTime")
                                .startAfter(upcomingEvent.getDateTime()) // Skip the upcoming event
                                .limit(3) // Limit the next events to three (gives 'see all' button purpose)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        List<Event> myEvents = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task2.getResult()) {
                                            Event event = document.toObject(Event.class);
                                            myEvents.add(event); // Add to the list of next three events
                                        }
                                        adapterSmall.setEventList(myEvents); // Pass the next three events to the adapter
                                        adapterSmall.notifyDataSetChanged(); // Notify the adapter for the other events
                                    } else {
                                        Log.w(TAG, "Error getting documents.", task2.getException());
                                    }
                                });
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void setUpFabCreateEvent() {
        ImageButton fabCreateEvent = findViewById(R.id.fabCreateEvent);
        fabCreateEvent.setOnClickListener(view -> {
            Intent intent = new Intent(OrganizerDashboardActivity.this, NewEventActivity.class);
            startActivity(intent);
        });
    }
}