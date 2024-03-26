package com.example.attendease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {
    private RecyclerView rvAllEvents;
    private RecyclerView rvAllAttendees;
    private RecyclerView rvAllImages;
    private TextView seeAll;
    private TextView seeAll2;
    private TextView seeAll3;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventsRef = db.collection("events");
    private CollectionReference attendeesRef = db.collection("attendees");
    private CollectionReference imagesRef = db.collection("images");
    private EventAdapter eventAdapter;
    private AttendeeAdapter attendeeAdapter;
    private ImageAdapter imageAdapter;
    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Attendee> attendeeList = new ArrayList<>();
    private ArrayList<Image> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        rvAllEvents = findViewById(R.id.rvAllEvents);
        rvAllAttendees = findViewById(R.id.rvAllProfiles);
        rvAllImages = findViewById(R.id.rvAllImages);

        seeAll = findViewById(R.id.see_all);
        seeAll2 = findViewById(R.id.see_all2);
        seeAll3 = findViewById(R.id.see_all3);

        rvAllEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAllAttendees.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAllImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        eventAdapter = new EventAdapter(this, eventList, EventAdapter.TYPE_SMALL);
        attendeeAdapter = new AttendeeAdapter(this, attendeeList);
        imageAdapter = new ImageAdapter(this, imageList);

        rvAllEvents.setAdapter(eventAdapter);
        rvAllAttendees.setAdapter(attendeeAdapter);
        rvAllImages.setAdapter(imageAdapter);

        loadEventsFromFirestore();
        loadAttendeesFromFirestore();
        loadImagesFromFirestore();

        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, BrowseAllEvents.class);
                startActivity(intent);
            }
        });

        seeAll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, BrowseAllAttendees.class);
                startActivity(intent);
            }
        });

        seeAll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, BrowseAllImages.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.admin_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_events) {
                Intent intent = new Intent(this, BrowseAllEvents.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(this, BrowseAllAttendees.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_image) {
                Intent intent = new Intent(this, BrowseAllImages.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        eventAdapter.setOnItemClickListener((view, position) -> {
            Event event = eventList.get(position);
            Intent intent = new Intent(AdminDashboardActivity.this, EventDetailsAdmin.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        /**
        attendeeAdapter.setOnItemClickListener((view, position) -> {
            Attendee attendee = attendeeList.get(position);
            Intent intent = new Intent(AdminDashboardActivity.this, AdminAttendeeDetailsActivity.class);
            intent.putExtra("attendee", attendee);
            startActivity(intent);
        });*/
    }

    private void loadEventsFromFirestore() {
        eventsRef.orderBy("dateTime").limit(4).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                eventList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    eventList.add(event);
                }
                eventAdapter.notifyDataSetChanged();
            } else {
                Log.d("AdminDashboardActivity", "Error getting events: ", task.getException());
            }
        });
    }

    private void loadAttendeesFromFirestore() {
        attendeesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                attendeeList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Attendee attendee = document.toObject(Attendee.class);
                    attendeeList.add(attendee);
                }
                attendeeAdapter.notifyDataSetChanged();
            } else {
                Log.d("AdminDashboardActivity", "Error getting attendees: ", task.getException());
            }
        });
    }

    private void loadImagesFromFirestore() {
        imagesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imageList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Image image = document.toObject(Image.class);
                    imageList.add(image);
                }
                imageAdapter.notifyDataSetChanged();
            } else {
                Log.d("AdminDashboardActivity", "Error getting images: ", task.getException());
            }
        });
    }
}
