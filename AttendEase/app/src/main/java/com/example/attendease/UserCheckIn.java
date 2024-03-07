package com.example.attendease;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserCheckIn extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference attendeesRef;
    private static final String ATTENDEE_COLLECTION = "attendees";

    private EditText attendeeNameEditText;
    private Button checkInButton;
    private ImageButton backButton;

    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_check_in);
        setContentView(R.layout.attendee_name); // Use your actual layout file name

        db = FirebaseFirestore.getInstance();
        attendeesRef = db.collection(ATTENDEE_COLLECTION);

        attendeeNameEditText = findViewById(R.id.editText_name);
        checkInButton = findViewById(R.id.button_submit);
        backButton = findViewById(R.id.back_button);

        setupListeners();
    }

    @SuppressLint("HardwareIds")
    private void setupListeners() {
        checkInButton.setOnClickListener(v -> {
            String name = attendeeNameEditText.getText().toString().trim();
            if (!name.isEmpty()) {
                saveAttendeeData(name);
            } else {
                Toast.makeText(UserCheckIn.this, "Name cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void saveAttendeeData(String name) {
        // Ideally, fetch these details through user input or your app's logic
        String phone = "";
        String email = "";
        String image = "";

        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("name", name);
        attendeeData.put("email", email);
        attendeeData.put("phone", phone);
        attendeeData.put("image", image);

        // Using Android's unique device ID as the document ID for simplicity
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        attendeesRef.document(deviceID)
                .set(attendeeData)
                .addOnSuccessListener(aVoid -> navigateToAttendeeDashboard())
                .addOnFailureListener(e -> Toast.makeText(UserCheckIn.this, "Failed to check in. Please try again.", Toast.LENGTH_SHORT).show());
    }

    private void navigateToAttendeeDashboard() {
        Intent intent = new Intent(UserCheckIn.this, AttendeeDashboardActivity.class);
        intent.putExtra("deviceID", deviceID);
        startActivity(intent);
        finish();
    }
}
