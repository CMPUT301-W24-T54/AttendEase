package com.example.attendease;

import static com.example.attendease.EventAdapter.TYPE_LARGE;
import static com.example.attendease.EventAdapter.TYPE_SMALL;
import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * This class represents the Attendee's home dashboard activity
 */
public class AttendeeDashboardActivity extends AppCompatActivity {

    private ImageButton checkInButton;
    private BottomNavigationView bottomNav;
    private TextView seeAllEvents;
    private TextView seeMyEvents;

    private Attendee attendee;


    private RecyclerView recyclerViewBrowseEvents;
    private RecyclerView recyclerViewMyEvents;
    private EventAdapter adapterLarge;
    private EventAdapter adapterSmall;
    private ArrayList<Event> eventList = new ArrayList<>();

    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_dashboard);

        attendee = (Attendee) Objects.requireNonNull(getIntent().getExtras()).get("attendee");

        checkInButton = findViewById(R.id.scan_new_qr);
        bottomNav = findViewById(R.id.attendee_bottom_nav);

        seeMyEvents = findViewById(R.id.see_all);
        seeAllEvents = findViewById(R.id.see_all2);

        recyclerViewBrowseEvents = findViewById(R.id.rvBrowseEvents);
        recyclerViewMyEvents = findViewById(R.id.rvMyEvents);

        recyclerViewMyEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewBrowseEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterLarge = new EventAdapter(this, eventList, TYPE_LARGE);
        adapterSmall = new EventAdapter(this, eventList, TYPE_SMALL);

        recyclerViewMyEvents.setAdapter(adapterLarge);
        recyclerViewBrowseEvents.setAdapter(adapterSmall);

        loadEventsFromFirestore();
        addListeners();
    }

    /**
     * Sets up listeners for various UI elements in the Attendee Dashboard activity.
     * These listeners handle user interactions such as button clicks and navigation item selections.
     * - The check-in button listener starts the QR Scanner activity to facilitate attendee check-in.
     * - The "See All Events" button listener navigates to the Browse All Events activity.
     * - The bottom navigation listener handles clicks on navigation items (Home, Events, Bell, Profile),
     *   logging debug information and initiating corresponding activities when items are clicked.
     */
    private void addListeners() {
        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO check if need to pass Attendee argument
                Intent intent = new Intent(AttendeeDashboardActivity.this, QRScannerActivity.class);
                intent.putExtra("attendee", attendee);
                startActivity(intent);
            }
        });

        seeAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboardActivity.this, BrowseAllEvents.class);
                intent.putExtra("attendee", attendee);
                startActivity(intent);
            }
        });

        seeMyEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboardActivity.this, BrowseMyEvent.class);
                intent.putExtra("attendee", attendee);
                startActivity(intent);
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Log.d("DEBUG", String.format("onNavigationItemSelected: %d", id));
                if (id == R.id.nav_home) {// Handle click on Home item
                    Log.d("DEBUG", "Home item clicked");
                } else if (id == R.id.nav_events) {// Handle click on Events item
                    Log.d("DEBUG", "Events item clicked");

                } else if (id == R.id.nav_bell) {// Handle click on Bell item
                    Log.d("DEBUG", "Bell item clicked");
                    Intent intent=new Intent(AttendeeDashboardActivity.this, AttendeeNotifications.class);
                    intent.putExtra("attendee", attendee);
                    startActivity(intent);
                } else if (id == R.id.nav_profile) {// Handle click on Profile item
                    Log.d("DEBUG", "Profile item clicked");
                    Intent intent = new Intent(AttendeeDashboardActivity.this, EditProfileActivity.class);
                    intent.putExtra("attendee", attendee);
                    startActivity(intent);

                }
                return true;
            }
        });
    }
    private void loadEventsFromFirestore() {
        collectEventIdsForUser();
        loadNewestEvents();
    }

    private void collectEventIdsForUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String attendeeId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        List<String> eventIds = new ArrayList<>();
        db.collection("signIns")
                .whereEqualTo("attendeeID", attendeeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventId = document.getString("eventID");
                            if (eventId != null && !eventId.isEmpty()) {
                                eventIds.add(eventId);
                            }
                        }
                        fetchEventsForIds(eventIds);
                    } else {
                        Log.w(TAG, "Error getting signed up events for user.", task.getException());
                    }
                });
    }

    private void fetchEventsForIds(List<String> eventIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Event> events = new ArrayList<>();

        if (eventIds.isEmpty()) {
            // Handles the case where the user is not signed up for any events
            adapterLarge.setEventList(events);
            adapterLarge.notifyDataSetChanged();
            return;
        }

        db.collection("events")
                .whereIn(FieldPath.documentId(), eventIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> MyEvents = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Event event = documentSnapshot.toObject(Event.class);
                            MyEvents.add(event);
                        }

                        adapterLarge.setEventList(MyEvents);
                        adapterLarge.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error fetching events for IDs.", task.getException());
                    }
                });
    }

    private void loadNewestEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> newestEvents = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            newestEvents.add(event);
                        }
                        adapterSmall.setEventList(newestEvents);
                        adapterSmall.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting events documents.", task.getException());
                    }
                });
    }

}
