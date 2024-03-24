package com.example.attendease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
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
    private CollectionReference attendeesRef;
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
    private GeoPoint geoPoint;
    private FusedLocationProviderClient fusedLocationClient;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_browsed_event);

        intent = getIntent();
        attendee = (Attendee) Objects.requireNonNull(getIntent().getExtras()).get("attendee");
        Log.d("DEBUG", String.format("onCreate: %s", attendee.getDeviceID()));

        if (attendee.isGeoTrackingEnabled()) {
            Log.d("DEBUG", String.format("onCreate: %s", "YIPPEE it works"));
        }


        eventID = intent.getStringExtra("eventID");
        prevActivity = intent.getStringExtra("prevActivity");

        signUpsRef = database.getSignInsRef();
        checkInsRef = database.getCheckInsRef();
        eventsRef = database.getEventsRef();
        attendeesRef = database.getAttendeesRef();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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


        if (Objects.equals(prevActivity, "QRScannerActivity")) {
            // Check if they have signed up first
            setListener(eventID, true);
        }


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
                data.put("attendeeID", attendee.getDeviceID());
                data.put("timeStamp", dateString);

                // TODO : Check Geo Location enabled


                // TODO : Add Geo Point data

                checkInsRef.document(unique_id).set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Firestore", "DocumentSnapshot successfully written!");
                                toggleInteractButton(false);
                                Toast.makeText(EventDetailsAttendee.this, "Check In successful!", Toast.LENGTH_SHORT).show();
                                checkAndGetGeoPoint(unique_id);
                            }
                        });
            }
        });
    }

    private void checkAndGetGeoPoint(String docID) {
        attendeesRef.document(attendee.getDeviceID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (Boolean.TRUE.equals(document.getBoolean("geoTrackingEnabled"))) {
                        getGeoPoint(docID);
                    }
                }
            }
        });
    }

    private void getGeoPoint(String docID) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            Log.d("DEBUG", String.format("onLocationChanged: %f %f", latitude, longitude));
                            geoPoint = new GeoPoint(latitude, longitude);
                            addGeoPoint(docID);
                        } else {
                            Log.d("DEBUG", String.format("onLocationChanged: can't really get a emulator location"));
                        }
                    }
                });
    }

    private void addGeoPoint(String docID) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("geoPoint", geoPoint);
        checkInsRef.document(docID).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DEBUG", "onSuccess: Location worked woopee");
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