package com.example.attendease;

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
import android.widget.TextView;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.ArrayList;

/**
 * This class represents an Admin user's dashboard
 */
public class AdminDashboardActivity extends AppCompatActivity {
    private RecyclerView rvAllEvents;
    private RecyclerView rvAllAttendees;
    private RecyclerView rvAllImages;
    private TextView seeAll;
    private TextView seeAll2;
    private TextView seeAll3;
    private final Database database = Database.getInstance();
    private CollectionReference eventsRef;
    private CollectionReference attendeesRef;
    private CollectionReference imagesRef;
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

        eventAdapter = new EventAdapter(this, eventList, TYPE_SMALL);
        attendeeAdapter = new AttendeeAdapter(this, attendeeList);
        imageAdapter = new ImageAdapter(this, imageList);

        if (rvAllEvents != null && eventAdapter != null) {
            rvAllEvents.setAdapter(eventAdapter);
        }

        if (rvAllAttendees != null && attendeeAdapter != null) {
            rvAllAttendees.setAdapter(attendeeAdapter);
        }

        if (rvAllImages != null && imageAdapter != null) {
            rvAllImages.setAdapter(imageAdapter);
        }

        eventsRef = database.getEventsRef();
        attendeesRef = database.getAttendeesRef();
        imagesRef = database.getImagesRef();
        loadEventsFromFirestore();
        loadAttendeesFromFirestore();
        loadImagesFromFirestore();

        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, BrowseAllEventsAdmin.class);
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

        BottomNavigationView bottomNavAdminDashboard = findViewById(R.id.admin_bottom_nav);
        bottomNavAdminDashboard.setSelectedItemId(R.id.nav_home);
        bottomNavAdminDashboard.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    // Already on the AdminDashboardActivity, no need to start a new instance
                    return true;
                } else if (id == R.id.nav_events) {
                    startActivity(new Intent(AdminDashboardActivity.this, BrowseAllEventsAdmin.class));
                    return true;
                } else if (id == R.id.nav_image) {
                    startActivity(new Intent(AdminDashboardActivity.this, BrowseAllImages.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(AdminDashboardActivity.this, BrowseAllAttendees.class));
                    return true;
                }
                return false;
            }
        });

        eventAdapter.setOnItemClickListener((view, position) -> {
            Event event = eventList.get(position);
            Intent intent = new Intent(AdminDashboardActivity.this, EventDetailsAdmin.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });
    }


    /**
     * Helper function that loads events from database
     */
    private void loadEventsFromFirestore() {
        eventsRef.orderBy("dateTime").limit(4).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                eventList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // TODO : FIX THIS LINE OF CODE
                    Event event = document.toObject(Event.class);
                    eventList.add(event);
                }
                eventAdapter.notifyDataSetChanged();
            } else {
                Log.d("AdminDashboardActivity", "Error getting events: ", task.getException());
            }
        });
    }

    /**
     * Helper function that loads users from database
     */
    private void loadAttendeesFromFirestore() {
        attendeesRef.limit(4).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                attendeeList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getId();
                    String name = document.getString("name");
                    String phone = document.getString("phone");
                    String email = document.getString("email");
                    String image = document.getString("image");
                    boolean geoTrackingEnabled = Boolean.TRUE.equals(document.getBoolean("geoTrackingEnabled"));
                    Attendee attendee = new Attendee(id, name, phone, email, image, geoTrackingEnabled);
                    attendeeList.add(attendee);
                }
                attendeeAdapter.notifyDataSetChanged();
            } else {
                Log.d("AdminDashboardActivity", "Error getting attendees: ", task.getException());
            }
        });
    }

    /**
     * Helper function that loads images from database
     */
    private void loadImagesFromFirestore() {

        eventsRef.limit(3).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String imageUrl = documentSnapshot.getString("posterUrl");
                        if (imageUrl != null && !imageUrl.equals("null") && !imageUrl.equals("")) {
                            Image image = new Image(imageUrl);
                            imageList.add(image);
                        }
                        imageAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("DEBUG", "onComplete: Could not load event posters");
                }
            }
        });

        attendeesRef.limit(3).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String imageUrl = documentSnapshot.getString("image");
                        if (imageUrl != null && !imageUrl.equals("null") && !imageUrl.equals("")) {
                            Image image= new Image(imageUrl);
                            imageList.add(image);
                        }
                        imageAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("DEBUG", "onComplete: Could not load attendee images");
                }
            }
        });
    }
}
