package com.example.attendease;

import static com.example.attendease.EventAdapter.TYPE_LARGE;
import static com.example.attendease.EventAdapter.TYPE_SMALL;
import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents the dashboard activity for event organizers in the AttendEase application.
 * This activity displays upcoming events and provides options for event management,
 * including the creation of new events.
 */
public class OrganizerDashboardActivity extends AppCompatActivity {
    private ImageButton createEventButton;
    private RecyclerView recyclerViewUpcomingEvent;
    private RecyclerView recyclerViewMyEvents;
    private EventAdapter adapterLarge;
    private EventAdapter adapterSmall;
    private ArrayList<Event> eventList = new ArrayList<>();
    private final Database database = Database.getInstance();
    private CollectionReference eventsRef;
    private ArrayList<Event> eventList2 = new ArrayList<>();


    /**
     * Initializes the activity, setting up the user interface components and loading events
     * from Firestore to display in the RecyclerViews.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down, this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_dashboard);
        /*if(!MilestoneListener.HasStarted){
            Intent serviceIntent = new Intent(this, MilestoneListener.class);
            startService(serviceIntent);
            MilestoneListener.HasStarted=true;

        }*/


        createEventButton = findViewById(R.id.createEvent);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerDashboardActivity.this, NewEventActivity.class);
                startActivity(intent);
            }
        });
        recyclerViewUpcomingEvent = findViewById(R.id.rvUpcomingEvent);
        recyclerViewMyEvents = findViewById(R.id.rvMyEvents);

        recyclerViewUpcomingEvent.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMyEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterLarge = new EventAdapter(this, eventList, TYPE_LARGE);
        adapterSmall = new EventAdapter(this, eventList2, TYPE_SMALL);

        recyclerViewUpcomingEvent.setAdapter(adapterLarge);
        recyclerViewMyEvents.setAdapter(adapterSmall);

        eventsRef = database.getEventsRef();
        loadEventsFromFirestore();
        setUpCreateEvent();

        BottomNavigationView bottomNavOrganizerDashboard = findViewById(R.id.organizer_bottom_nav);
        bottomNavOrganizerDashboard.setSelectedItemId(R.id.nav_home);
        bottomNavOrganizerDashboard.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    // Already on the OrganizerDashboardActivity, no need to start a new instance
                    return true;
                } else if (id == R.id.nav_events) {
                    Intent intent = new Intent(OrganizerDashboardActivity.this, OrganizerMyEventsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_notifications) {
                    String organizerId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    Intent intent = new Intent(OrganizerDashboardActivity.this, OrganizerNotifications.class);
                    intent.putExtra("deviceId", organizerId);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        adapterLarge.setOnItemClickListener(new EventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Event event = eventList.get(position);
                Intent intent = new Intent(OrganizerDashboardActivity.this, EventDetailsOrganizer.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });

        adapterSmall.setOnItemClickListener(new EventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Event event = eventList2.get(position);
                Intent intent = new Intent(OrganizerDashboardActivity.this, EventDetailsOrganizer.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });
    }

    /**
     * Loads events from Firestore, filtering by the organizer's ID to display
     * their specific events. It populates both the upcoming event view and
     * the list of the next three events horizontally.
     */
    private void loadEventsFromFirestore() {
        String organizerId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Query for the closest upcoming event
        eventsRef
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


                        eventsRef
                                .whereEqualTo("organizerId", organizerId)
                                .orderBy("dateTime")
                                .startAfter(upcomingEvent.getDateTime()) // Skip the upcoming event
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        eventList2.clear();
                                        for (QueryDocumentSnapshot document : task2.getResult()) {
                                            Event event = document.toObject(Event.class);
                                            eventList2.add(event); // Add to the list of next three events
                                        }
                                        adapterSmall.setEventList(eventList2); // Pass the next three events to the adapter
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

    /**
     * Sets up the Button for creating new events. Tapping this button
     * navigates to the NewEventActivity where the organizer can enter event details.
     */
    private void setUpCreateEvent() {
        ImageButton fabCreateEvent = findViewById(R.id.createEvent);
        fabCreateEvent.setOnClickListener(view -> {
            Intent intent = new Intent(OrganizerDashboardActivity.this, NewEventActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEventsFromFirestore();
    }
}
