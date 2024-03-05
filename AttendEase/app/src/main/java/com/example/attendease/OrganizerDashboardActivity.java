package com.example.attendease;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrganizerDashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerViewUpcomingEvent;
    private RecyclerView recyclerViewMyEvents;
    private EventAdapter adapter; // TODO: create EventAdapter class
    private ArrayList<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_dashboard);

        recyclerViewUpcomingEvent = findViewById(R.id.rvUpcomingEvent); // Update with actual ID from your layout
        recyclerViewMyEvents = findViewById(R.id.rvMyEvents); // Update with actual ID from your layout

        recyclerViewUpcomingEvent.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMyEvents.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EventAdapter(eventList);
        recyclerViewUpcomingEvent.setAdapter(adapter);
        recyclerViewMyEvents.setAdapter(adapter);

        loadEventsFromFirestore();
        setUpFabCreateEvent();
    }

    private void loadEventsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String organizerId = "YourOrganizerId"; // Replace with actual organizer ID

        db.collection("events")
                .whereEqualTo("organizerId", organizerId)
                .orderBy("dateTime") // Make sure you've set the index in Firestore
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Event event = document.toObject(Event.class);
                                eventList.add(event);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void setUpFabCreateEvent() {
        FloatingActionButton fabCreateEvent = findViewById(R.id.fabCreateEvent);
        fabCreateEvent.setOnClickListener(view -> {
            Intent intent = new Intent(OrganizerDashboardActivity.this, NewEventActivity.class);
            startActivity(intent);
        });
    }
}