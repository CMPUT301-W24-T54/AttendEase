package com.example.attendease;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private FirebaseFirestore db;
    private Intent intent;
    private CollectionReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_event_dashboard);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        // Get Intent For Single Event
        intent = getIntent();
        String eventDocID = intent.getStringExtra("eventDocumentId");


        // Initialize UI components
        eventName = findViewById(R.id.eventName);
        aboutDescription = findViewById(R.id.detailsAboutContent);
        locationView = findViewById(R.id.location);
        dateandtimeView = findViewById(R.id.dateandtime);
        QRCodeImage = findViewById(R.id.QRCodeImage);
        signUpsSeeAll = findViewById(R.id.signUpsSeeAllButton);
        attendanceSeeAll = findViewById(R.id.attendanceSeeAllButton);
        backButton = findViewById(R.id.back_button);

        // Fill in the views with information of the event  (TEMP)
        eventsRef.document(eventDocID).get().addOnSuccessListener(documentSnapshot -> {
            String eventTitle = documentSnapshot.getString("title");
            eventName.setText(eventTitle);
            String aboutDesc = documentSnapshot.getString("description");
            aboutDescription.setText(aboutDesc);
            String location = documentSnapshot.getString("location");
            locationView.setText(location);
            Timestamp dateTime = documentSnapshot.getTimestamp("dateTime");
            if (dateTime != null) {
                dateandtimeView.setText(dateTime.toDate().toString());
            } else {
                // Handle null case, maybe hide the view or set a default text
                dateandtimeView.setText("No date provided");
            }
            String qrCodeImageUrl = documentSnapshot.getString("checkInQR");

            runOnUiThread(() -> {
                // Load QR code image into ImageView using URL and BitmapFactory
                if (qrCodeImageUrl != null && !qrCodeImageUrl.isEmpty()) {
                    new Thread(() -> {
                        try {
                            URL url = new URL(qrCodeImageUrl);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(input);

                            runOnUiThread(() -> QRCodeImage.setImageBitmap(bitmap));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get Intent For Single Event
        intent = getIntent();
        String eventDocID = intent.getStringExtra("eventDocumentId");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signUpsSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EventDetailsOrganizer.this, SignupsListActivity.class);
                intent.putExtra("eventDocumentId", eventDocID);
                startActivity(intent);
            }
        });

        attendanceSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EventDetailsOrganizer.this, AttendanceListActivity.class);
                intent.putExtra("eventDocumentId", eventDocID);
                startActivity(intent);
            }
        });

    }
}