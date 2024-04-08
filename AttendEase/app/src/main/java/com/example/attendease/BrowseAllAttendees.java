package com.example.attendease;

import static com.example.attendease.R.id.admin_bottom_nav;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This activity represents browsing of all attendee profiles.
 * Retrieves attendee profiles from Firestore and displays them in a RecyclerView.
 */
public class BrowseAllAttendees extends AppCompatActivity {
    private TextView totalCountTextView;
    private RecyclerView rvProfiles;
    private final Database database = Database.getInstance();
    private CollectionReference attendeesRef;
    private AttendeeAdapter attendeeAdapter;
    private ArrayList<Attendee> attendeeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_all_profiles);

        totalCountTextView = findViewById(R.id.total_count);
        rvProfiles = findViewById(R.id.rv_profiles);
        rvProfiles.setLayoutManager(new GridLayoutManager(this, 1));
        attendeeAdapter = new AttendeeAdapter(this, attendeeList);
        rvProfiles.setAdapter(attendeeAdapter);

        attendeesRef = database.getAttendeesRef();
        loadAttendeesFromFirestore();

        BottomNavigationView bottomNavAttendeesAdmin = findViewById(admin_bottom_nav);
        bottomNavAttendeesAdmin.setSelectedItemId(R.id.nav_profile);
        bottomNavAttendeesAdmin.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(BrowseAllAttendees.this, AdminDashboardActivity.class));
                    return true;
                } else if (id == R.id.nav_events) {
                    startActivity(new Intent(BrowseAllAttendees.this, BrowseAllEventsAdmin.class));
                    return true;
                } else if (id == R.id.nav_image) {
                    startActivity(new Intent(BrowseAllAttendees.this, BrowseAllImages.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    // Already on the BrowseAllAttendees, no need to start a new instance
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Retrieves all attendee profiles from Firestore and populates the RecyclerView with the data.
     * Updates the total count of attendees displayed.
     */
    private void loadAttendeesFromFirestore() {
        attendeesRef.get().addOnCompleteListener(task -> {
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

                String totalCountText = String.valueOf(attendeeList.size());
                totalCountTextView.setText(totalCountText);
            } else {
                Log.d("BrowseAllAttendee", "Error getting attendees: ", task.getException());
            }
        });
    }
}
