package com.example.attendease;

import static com.example.attendease.R.id.admin_bottom_nav;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * This activity displays detailed information about an event for administrators.
 * Administrators can view event details, delete events, and remove event cover photos.
 */
public class EventDetailsAdmin extends AppCompatActivity {
    private TextView eventName;
    private TextView aboutDescription;
    private TextView locationView;
    private TextView dateandtimeView;
    private ImageView eventCover;
    private ImageView qrImage;
    private Button removeCoverButton;
    private ImageButton backButton;
    private ImageButton trashButton;

    private Event event;

    private final Database database = Database.getInstance();
    private final StorageReference storageRef = database.getStorageRef();
    private final CollectionReference eventsRefs = database.getEventsRef();
    private final CollectionReference checkInsRef = database.getCheckInsRef();
    private final CollectionReference signInsRef = database.getSignInsRef();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_details);

        // Initialize UI components
        eventName = findViewById(R.id.event_name);
        aboutDescription = findViewById(R.id.about_content);
        locationView = findViewById(R.id.location);
        dateandtimeView = findViewById(R.id.date_time);
        eventCover = findViewById(R.id.event_cover);
        qrImage = findViewById(R.id.qr_code_image);
        removeCoverButton = findViewById(R.id.remove_cover_button);
        backButton = findViewById(R.id.nav_left);
        trashButton = findViewById(R.id.trash);

        // Get event details from intent
        Intent intent = getIntent();
        event = intent.getParcelableExtra("event");

        // Populate UI with event details
        populateUIWithEvent(event);

        // Set click listeners
        backButton.setOnClickListener(v -> finish());
        trashButton.setOnClickListener(v -> deleteEvent());
        removeCoverButton.setOnClickListener(v -> removeEventCover());

        BottomNavigationView bottomNavAttendeesAdmin = findViewById(admin_bottom_nav);
        bottomNavAttendeesAdmin.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(EventDetailsAdmin.this, AdminDashboardActivity.class));
                    return true;
                } else if (id == R.id.nav_events) {
                    startActivity(new Intent(EventDetailsAdmin.this, BrowseAllEventsAdmin.class));
                    return true;
                } else if (id == R.id.nav_image) {
                    startActivity(new Intent(EventDetailsAdmin.this, BrowseAllImages.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(EventDetailsAdmin.this, BrowseAllAttendees.class));
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Populates the UI with event details.
     *
     * @param event The event object containing details to populate the UI.
     */
    private void populateUIWithEvent(Event event) {
        if (event != null) {
            eventName.setText(event.getTitle());
            aboutDescription.setText(event.getDescription());
            locationView.setText(event.getLocation());
            if (event.getDateTime() != null) {
                dateandtimeView.setText(event.getDateTime().toDate().toString());
            } else {
                dateandtimeView.setText("No date provided");
            }
            /**
            String coverImageUrl = event.getImageURL();
            if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
                Glide.with(this)
                        .load(coverImageUrl)
                        .override(500, 500)
                        .into(eventCover);
            }*/
            // Check if the event has a valid poster URL and load it; otherwise set a placeholder
            if (event.getPosterUrl() != null && !event.getPosterUrl().equals("null") && !event.getPosterUrl().equals("")) {
                int image_size=100;
                Bitmap CoverPhoto = RandomImageGenerator.generateProfilePicture(event.getPosterUrl(), image_size);
                eventCover.setImageBitmap(CoverPhoto);
            } else {
                eventCover.setImageResource(R.drawable.splash);
            }
        }
    }

    /**
     * Deletes the event.
     * TODO: Implement deletion logic.
     */
    private void deleteEvent() {
        eventsRefs.document(event.getEventId()).delete().addOnSuccessListener(aVoid -> {
            Log.d("EventDetailsAdmin", "Success to delete event");
        }).addOnFailureListener(e -> {
            Log.e("EventDetailsAdmin", "Failed to delete event: " + e.getMessage());
        });

        signInsRef.whereEqualTo("eventID", event.getEventId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                deleteReference(documentSnapshot);
                            }
                        }
                    }
                });

        checkInsRef.whereEqualTo("eventID", event.getEventId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                deleteReference(documentSnapshot);
                            }
                        }
                    }
                });

        View view = LayoutInflater.from(EventDetailsAdmin.this).inflate(R.layout.event_removed_dialog, null);
        Button okayButton = view.findViewById(R.id.eventRokay);
        AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailsAdmin.this);
        builder.setView(view);
        Dialog dialog = builder.create();
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailsAdmin.this, BrowseAllEventsAdmin.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private void deleteReference(DocumentSnapshot documentSnapshot) {
        documentSnapshot.getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("DEBUG", "onComplete: Deleted signin");
                }
            }
        });
    }

    /**
     * Removes the event cover photo.
     * TODO: Implement logic to remove the cover photo.
     */
    private void removeEventCover() {
        if (event.getPosterUrl() != null && !event.getPosterUrl().equals("null")) {
            // StackOverflow, https://stackoverflow.com/questions/42930619/how-to-delete-image-from-firebase-storage
            // Delete image from firestore storage
            StorageReference photoRef = database.getStorage().getReferenceFromUrl(event.getPosterUrl());
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    Log.d("DEBUG", "onSuccess: deleted file");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // Uh-oh, an error occurred!
                    Log.d("DEBUG", "onFailure: did not delete file");
                }
            });

            HashMap<String, Object> data = new HashMap<>();
            data.put("posterUrl", "null");
            eventsRefs.document(event.getEventId()).update(data).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Poster deleted successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Log.e("EventDetailsAdmin", "Failed to delete poster: " + e.getMessage());
            });

            eventCover.setImageResource(R.drawable.item_removed_successfully); //placeholder image
            Toast.makeText(this, "Event cover photo removed", Toast.LENGTH_SHORT).show();
        }
    }
}
