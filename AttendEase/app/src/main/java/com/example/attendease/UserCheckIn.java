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

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the check-in process for users in the AttendEase application.
 * This activity allows users to enter their name and optionally other details to check into an event,
 * leveraging Firebase Firestore for data storage.
 */
public class UserCheckIn extends AppCompatActivity {

    private final Database database = Database.getInstance();
    private CollectionReference attendeesRef;

    private EditText attendeeNameEditText;
    private Button checkInButton;
    private ImageButton backButton;

    private String deviceID;

    /**
     * Sets up the activity's user interface and initializes Firebase Firestore references.
     * Additionally, configures listeners for the UI elements to handle user interaction.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_check_in);

        attendeesRef = database.getAttendeesRef();

        attendeeNameEditText = findViewById(R.id.editText_name);
        checkInButton = findViewById(R.id.button_submit);
        backButton = findViewById(R.id.back_button);

        setupListeners();
    }

    /**
     * Configures onClick listeners for the check-in and back button.
     * The check-in button listener attempts to save the attendee's data,
     * while the back button simply finishes the current activity.
     */
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

    /**
     * Saves the attendee's data to Firebase Firestore.
     * Captures the attendee's name from the UI and uses the device's unique ID as the document key.
     *
     * @param name The name of the attendee to be saved.
     */
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

    /**
     * Navigates the user to the AttendeeDashboardActivity upon successful check-in.
     * Passes the device ID as an extra in the intent for further use.
     */
    private void navigateToAttendeeDashboard() {
        Intent intent = new Intent(UserCheckIn.this, AttendeeDashboardActivity.class);
        intent.putExtra("deviceID", deviceID);
        startActivity(intent);
        finish();
    }
}

