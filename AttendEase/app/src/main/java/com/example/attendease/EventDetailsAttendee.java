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
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * This class represents the Event details screen
 * where an attendee can view the details of an event
 * and sign up or check in
 */
public class EventDetailsAttendee extends AppCompatActivity {
    private final Database database = Database.getInstance();
    private CollectionReference signUpsRef;
    private CollectionReference checkInsRef;
    private CollectionReference eventsRef;
    private Intent intent;
    private TextView titleText;
    private TextView locationText;
    private TextView descriptionText;
    private TextView dateText;
    private ImageButton backButton;
    private Button interactButton;
    private Attendee attendee;
    private String eventID;
    private String prevActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_browsed_event);

        intent = getIntent();
        attendee = (Attendee) Objects.requireNonNull(getIntent().getExtras()).get("attendee");
        Log.d("DEBUG", String.format("onCreate: %s", attendee.getDeviceID()));

        eventID = intent.getStringExtra("eventID");
        prevActivity = intent.getStringExtra("prevActivity");

        signUpsRef = database.getSignInsRef();
        checkInsRef = database.getCheckInsRef();
        eventsRef = database.getEventsRef();

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
        toggleInteractButton(false);  // Wait to see if they haven't signed up yet




        // TODO : If they have signed up and they came here through checkin, allow them to check in
        // TODO : If they haven't signed up and they came here through checkin, allow them to sign up
        if (Objects.equals(prevActivity, "QRScannerActivity")) {
            // Check if they have signed up first
            setListener(eventID, true);
        }


        // TODO : If they came here through all events and they have signed up, they shouldn't be able to do anything
        // TODO : If they came here through all events, and haven't signed up, they should be able to sign up.
        if (Objects.equals(prevActivity, "BrowseAllEvents")) {
            // Check if they have signed up first
            setListener(eventID, false);
        }



        addListeners();

    }

    private void setListener(String eventID, boolean canCheckIn) {
        signUpsRef
                .whereEqualTo("attendeeID", attendee.getDeviceID())
                .whereEqualTo("eventID", eventID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            if (canCheckIn) {
                                addCheckInListener();
                            }
                        } else {
                            addSignUpListener(canCheckIn);
                        }
                    }
                });
    }

    private void addCheckInListener() {
        interactButton.setText("Check In");
        toggleInteractButton(true);
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
                data.put("attendeeID",attendee.getDeviceID());
                data.put("timeStamp", dateString);

                checkInsRef.document(unique_id).set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Firestore", "DocumentSnapshot successfully written!");
                                toggleInteractButton(false);
                                Toast.makeText(EventDetailsAttendee.this, "Check In successful!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void addSignUpListener(boolean canCheckIn) {
        toggleInteractButton(true);
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
                data.put("attendeeID",attendee.getDeviceID());
                data.put("timeStamp", dateString);

                signUpsRef.document(unique_id).set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Firestore", "DocumentSnapshot successfully written!");
                                Toast.makeText(EventDetailsAttendee.this, "Sign Up successful!", Toast.LENGTH_SHORT).show();
                                if (canCheckIn) {
                                    addCheckInListener();
                                } else {
                                    toggleInteractButton(false);
                                }
                            }
                        });
            }
        });
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
    }

    private void toggleInteractButton(boolean interactable){
        if (interactable) {
            interactButton.setVisibility(View.VISIBLE);
            interactButton.setClickable(true);

        } else {
            interactButton.setVisibility(View.INVISIBLE);
            interactButton.setClickable(false);
        }
//        if (interactButton.getVisibility() == View.VISIBLE) {
//            interactButton.setVisibility(View.INVISIBLE);
//            interactButton.setClickable(false);
//        } else {
//            interactButton.setVisibility(View.VISIBLE);
//            interactButton.setClickable(true);
//        }
    }
}