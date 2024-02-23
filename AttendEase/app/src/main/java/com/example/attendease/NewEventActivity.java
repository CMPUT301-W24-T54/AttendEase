package com.example.attendease;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class NewEventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event);

        ImageButton buttonGoBack = findViewById(R.id.buttonGoBack);
        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewEventActivity.this, OrganizerDashboardActivity.class);
                // Clears activity from the stack before returning to previous screen
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.tvEventDate).setOnClickListener(view -> showDatePickerDialog());
        findViewById(R.id.tvEventTime).setOnClickListener(view -> showTimePickerDialog());

        Button btnGenerate = findViewById(R.id.btnGenerateEvent);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean eventCreated = createEvent();
                if (eventCreated) {
                    Intent intent = new Intent(NewEventActivity.this, OrganizerDashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish(); // Only finish if event creation was successful
                }
                // If eventCreated is false, do nothing, therefore staying on the current screen
            }
        });

        // TODO add onClick to upload and remove images for event poster

    }

    void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    TextView tvEventDate = findViewById(R.id.tvEventDate);
                    tvEventDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    void showTimePickerDialog() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    TextView tvEventTime = findViewById(R.id.tvEventTime);
                    tvEventTime.setText(hourOfDay + ":" + minute);
                },
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true)
                .show();
    }

    private boolean createEvent() {
        // Capture data from EditTexts, CheckBoxes, etc.
        String eventID = generateEventId();
        String eventName = ((EditText) findViewById(R.id.etEventName)).getText().toString();
        String eventAbout = ((EditText) findViewById(R.id.etEventAbout)).getText().toString();
        String ownerID = getOwnerId();
        String eventDate = getEventDate();
        String eventTime = getEventTime();

        if (eventDate == null || eventTime == null) {
            Toast.makeText(this, "Invalid input. Events must have a date and time.", Toast.LENGTH_SHORT).show();
            return false;
        }

        Timestamp dateTime = createTimestamp(eventDate, eventTime);
        String eventLocation = ((EditText) findViewById(R.id.etEventLocation)).getText().toString();
        // TODO generate promoQR
        String promoQR = "null";
        // TODO generate checkInQR
        String checkInQR = "null";
        // TODO add image upload functionality to the upload image button in onCreate and then fetch the image here
        String posterUrl = "null";
        boolean isGeoTrackingEnabled = ((CheckBox) findViewById(R.id.cbGeoTracking)).isChecked();
        int maxAttendees = getMaxAttendees();

        if (eventName.equals("") || eventLocation.equals("")) {
            Toast.makeText(this, "Invalid input. Events must have a name and location", Toast.LENGTH_SHORT).show();
            return false;
        }

        Event newEvent = new Event(eventID, eventName, eventAbout, ownerID, dateTime, eventLocation, promoQR, checkInQR, posterUrl, isGeoTrackingEnabled, maxAttendees);

        // Save to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").add(newEvent);
        return true;
    }

    private String getOwnerId() {
        // Get the Android device ID
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    private String getEventDate() {
        TextView tvEventDate = findViewById(R.id.tvEventDate);
        String date = tvEventDate.getText().toString();
        if ("Select Date".equals(date)) {
            return null;
        }
        return date;
    }

    private String getEventTime() {
        TextView tvEventTime = findViewById(R.id.tvEventTime);
        String time = tvEventTime.getText().toString();
        if ("Select Time".equals(time)) {
            return null;
        }
        return time;
    }

    private Timestamp createTimestamp(String eventDate, String eventTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        // Parses the date and time strings into a Date object
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(eventDate + " " + eventTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // Convert the Date object into a Timestamp
        return new Timestamp(parsedDate);
    }

    private Integer getMaxAttendees() {
        EditText etSignUpLimit = findViewById(R.id.etSignUpLimit);
        String maxAttendeesStr = etSignUpLimit.getText().toString();
        // Check if the EditText is empty or not
        if (maxAttendeesStr.isEmpty()) {
            return Integer.MAX_VALUE; // Returns null if no input was entered
        } else {
            try {
                return Integer.parseInt(maxAttendeesStr);
            } catch (NumberFormatException e) {
                return Integer.MAX_VALUE; // Returns null if the input is not a valid integer
            }
        }
    }

    private String generateEventId() {
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
    }

    // TODO add functions to upload and remove images for event poster

}
