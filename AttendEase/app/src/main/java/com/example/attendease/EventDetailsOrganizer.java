package com.example.attendease;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


/**
 * Activity for displaying detailed information about a single event for organizers.
 * This class retrieves event details from Firestore and populates the UI components with the information.
 * Organizers can view event details, QR code, and navigate to sign-up and attendance lists.
 */
public class EventDetailsOrganizer extends AppCompatActivity {
    private TextView eventName;
    private TextView aboutDescription;
    private TextView locationView;
    private TextView dateandtimeView;
    private ImageView QRCodeImage;
    private Button signUpsSeeAll;
    private Button attendanceSeeAll;
    private ImageButton backButton;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_event_dashboard);

        // Get Intent For Single Event
        intent = getIntent();

        // Initialize UI components
        eventName = findViewById(R.id.eventName);
        aboutDescription = findViewById(R.id.detailsAboutContent);
        locationView = findViewById(R.id.location);
        dateandtimeView = findViewById(R.id.dateandtime);
        QRCodeImage = findViewById(R.id.QRCodeImage);
        signUpsSeeAll = findViewById(R.id.signUpsSeeAllButton);
        attendanceSeeAll = findViewById(R.id.attendanceSeeAllButton);
        backButton = findViewById(R.id.back_button);

        Event event = intent.getParcelableExtra("event");

        populateUIWithEvent(event);

        backButton.setOnClickListener(v -> finish());

        signUpsSeeAll.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsOrganizer.this, SignupsListActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        attendanceSeeAll.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsOrganizer.this, AttendanceListActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });
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

            String qrCodeImageUrl = event.getCheckInQR();
            // Load QR code image into ImageView
            if (qrCodeImageUrl != null && !qrCodeImageUrl.isEmpty()) {
                Glide.with(this)
                        .load(qrCodeImageUrl)
                        .override(500, 500) // Adjust the size as per your requirement
                        .into(QRCodeImage);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}