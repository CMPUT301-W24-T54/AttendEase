package com.example.attendease;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

public class EventDetailsAdmin extends AppCompatActivity {
    private TextView eventName;
    private TextView aboutDescription;
    private TextView locationView;
    private TextView dateandtimeView;
    private ImageView eventCover;
    private Button removeCoverButton;
    private ImageButton backButton;
    private ImageButton trashButton;

    private Event event;

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
    }

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
        }
    }

    private void deleteEvent() {
        //TODO
        Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void removeEventCover() {
        // Code to remove event cover image TODO
        eventCover.setImageResource(R.drawable.item_removed_successfully); //placeholder image
        Toast.makeText(this, "Event cover photo removed", Toast.LENGTH_SHORT).show();
    }
}
