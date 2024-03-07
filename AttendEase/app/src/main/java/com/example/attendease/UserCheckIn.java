package com.example.attendease;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID; //To generate Unique Device ID for time being

public class UserCheckIn extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference attendeesRef;
    private static final String ATTENDEE_COLLECTION = "attendees";

    private EditText attendeeNameEditText; // Assuming initialization in onCreate
    private Button checkInButton; // Assuming initialization in onCreate
    private ImageButton backButton; // For navigating back to MainActivity

    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_check_in); // Use your actual layout file name

        db = FirebaseFirestore.getInstance();
        attendeesRef = db.collection(ATTENDEE_COLLECTION);

        // Initialize UI components
        attendeeNameEditText = findViewById(R.id.editText_name); // Adjust the ID as needed
        checkInButton = findViewById(R.id.button_submit); // Adjust the ID as needed
        backButton = findViewById(R.id.back_button); // Make sure the ID matches your layout

        // Set OnClickListener for the Check-In button
        checkInButton.setOnClickListener(v -> checkInAttendee());

        // Set OnClickListener for the Back button
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserCheckIn.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Checks the user into the app
     */
    @SuppressLint("HardwareIds")
    private void checkInAttendee() {
        // TODO change this horrible name
        String name = attendeeNameEditText.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique deviceID using UUID
//        String deviceID = UUID.randomUUID().toString();
        // Get the Android device ID
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        // Placeholder values for phone, email, and image; adjust as needed
        String phone = ""; // Optional: Collect from user input
        String email = ""; // Optional: Collect from user input
        String image = ""; // Optional: Use a default or allow user to upload

        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("name", name);
        attendeeData.put("email", email);
        attendeeData.put("phone", phone);
        attendeeData.put("image", image);

        attendeesRef.document(deviceID) // Use the newly generated unique deviceID
                .set(attendeeData) // .set() to overwrite or create a new document
                .addOnSuccessListener(aVoid -> {
                    // Toast.makeText(UserCheckIn.this, "Check-in successful.", Toast.LENGTH_SHORT).show();
                    navigateToAttendeeDashboard();
                });
                //.addOnFailureListener(e -> Toast.makeText(UserCheckIn.this, "Failed to check in.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Starts the Attendee Dashboard activity page
     */
    private void navigateToAttendeeDashboard() {
        Intent intent = new Intent(UserCheckIn.this, AttendeeDashboardActivity.class);
        intent.putExtra("deviceID", deviceID);
        startActivity(intent);
        finish();
    }
}