package com.example.attendease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * This class represents the Event details screen
 * where an attendee can view the details of an event
 * and sign up or check in
 */
public class EventDetails extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference signUpsRef;
    private CollectionReference checkInsRef;
    private Intent intent;
    private TextView titleText;
    private TextView locationText;
    private TextView descriptionText;
    private TextView dateText;
    private ImageButton backButton;
    private Button interactButton;
    private String deviceID;
    private String eventID;
    private boolean canCheckIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_browsed_event);

        intent = getIntent();

        titleText = findViewById(R.id.EventTitle);
        descriptionText = findViewById(R.id.description);
        locationText = findViewById(R.id.Location);
        dateText = findViewById(R.id.DateTime);

        titleText.setText(intent.getStringExtra("title"));
        descriptionText.setText(intent.getStringExtra("description"));
        locationText.setText(intent.getStringExtra("location"));
        dateText.setText(intent.getStringExtra("dateTime"));

        backButton = findViewById(R.id.imageButton);
        interactButton = findViewById(R.id.signup_or_checkin); // This becomes a check in button if QR Code Scanned

        deviceID = intent.getStringExtra("deviceID");
        eventID = intent.getStringExtra("eventID");
        canCheckIn = intent.getBooleanExtra("canCheckIn", false);

        if (canCheckIn) {
            interactButton.setText("Check In");
        }

        db = FirebaseFirestore.getInstance();
        signUpsRef = db.collection("signIns");
        checkInsRef = db.collection("checkIns");

        addListeners();
        hideSignUp();
    }

    /**
     * Sets up listeners for UI elements in the Event Details activity.
     * - The backButton listener finishes the current activity when clicked.
     * - The interactButton listener handles interactions such as check-in or sign-up for the event.
     *   It captures the current time, generates a unique ID, and writes relevant data (event ID, attendee ID,
     *   timestamp) to Firestore based on whether the attendee can check in or sign up for the event.
     *   Upon successful completion, it hides the interactButton and displays a toast message accordingly.
     */
    private void addListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        interactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to take name from attendee class
                long currentTimeMillis = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String dateString = sdf.format(new Date(currentTimeMillis));

                String unique_id = UUID.randomUUID().toString();
                HashMap<String, String> data = new HashMap<>();
                data.put("eventID", eventID);
                data.put("attendeeID",deviceID);
                data.put("timeStamp", dateString);

                if (canCheckIn) {
                    checkInsRef.document(unique_id).set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("Firestore", "DocumentSnapshot successfully written!");
                                    interactButton.setVisibility(View.INVISIBLE);
                                    interactButton.setClickable(false);
                                    Toast.makeText(EventDetails.this, "Check In successful!", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    signUpsRef.document(unique_id).set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("Firestore", "DocumentSnapshot successfully written!");
                                    interactButton.setVisibility(View.INVISIBLE);
                                    interactButton.setClickable(false);
                                    Toast.makeText(EventDetails.this, "Sign Up successful!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    /**
     * Hides the sign-up button if the attendee has already signed up for the event.
     * This method checks if the attendee has signed up for the event by querying Firestore
     * for sign-up documents matching the attendee's device ID and the event ID. If such documents
     * exist, indicating that the attendee has already signed up, the sign-up button is hidden
     * and made unclickable.
     */
    private void hideSignUp() {
        if (!canCheckIn) {
            signUpsRef
                .whereEqualTo("attendeeID", deviceID)
                .whereEqualTo("eventID", eventID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            interactButton.setVisibility(View.INVISIBLE);
                            interactButton.setClickable(false);
                        }
                    }
                });
        }
    }
}