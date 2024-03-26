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
    private RecyclerView rvAllProfiles;
    private RecyclerView rvAllImages;
    private TextView seeAll;
    private TextView seeAll2;
    private TextView seeAll3;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventsRef = db.collection("events");
    private CollectionReference profilesRef = db.collection("profiles");
    private CollectionReference imagesRef = db.collection("images");
    private EventAdapter eventAdapter;
    private ProfileAdapter profileAdapter;
    private ImageAdapter imageAdapter;
    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Profile> profileList = new ArrayList<>();
    private ArrayList<Image> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        rvAllEvents = findViewById(R.id.rvAllEvents);
        rvAllProfiles = findViewById(R.id.rvAllProfiles);
        rvAllImages = findViewById(R.id.rvAllImages);

        seeAll = findViewById(R.id.see_all);
        seeAll2 = findViewById(R.id.see_all2);
        seeAll3 = findViewById(R.id.see_all3);

        rvAllEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAllProfiles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAllImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        eventAdapter = new EventAdapter(this, eventList, EventAdapter.TYPE_SMALL);
        profileAdapter = new ProfileAdapter(this, profileList);
        imageAdapter = new ImageAdapter(this, imageList);

        rvAllEvents.setAdapter(eventAdapter);
        rvAllProfiles.setAdapter(profileAdapter);
        rvAllImages.setAdapter(imageAdapter);

        loadEventsFromFirestore();
        loadProfilesFromFirestore();
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
                Intent intent = new Intent(AdminDashboardActivity.this, AllProfilesActivity.class);
                startActivity(intent);
            }
        });

        seeAll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AllImagesActivity.class);
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
            } else if (id == R.id.nav_profiles) {
                Intent intent = new Intent(this, BrowseAllProfiles.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_images) {
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

        profileAdapter.setOnItemClickListener((view, position) -> {
            Profile profile = profileList.get(position);
            Intent intent = new Intent(AdminDashboardActivity.this, AdminProfileDetailsActivity.class);
            intent.putExtra("profile", profile);
            startActivity(intent);
        });
    }

    private void loadEventsFromFirestore() {
        eventsRef.get().addOnCompleteListener(task -> {
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

    private void loadProfilesFromFirestore() {
        profilesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                profileList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Profile profile = document.toObject(Profile.class);
                    profileList.add(profile);
                }
                profileAdapter.notifyDataSetChanged();
            } else {
                Log.d("AdminDashboardActivity", "Error getting profiles: ", task.getException());
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
