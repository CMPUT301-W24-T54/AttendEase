package com.example.attendease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
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
    private ImageView QRCodeImage;
    private ImageView eventPosterImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_browsed_event);

        intent = getIntent();
        attendee = (Attendee) Objects.requireNonNull(getIntent().getExtras()).get("attendee");
        Log.d("DEBUG", String.format("onCreate: %s", attendee.getDeviceID()));


        eventID = intent.getStringExtra("eventID");

        if (eventID == null) {
            Toast.makeText(this, "Invalid Event", Toast.LENGTH_SHORT).show();
            finish();
        }

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
        QRCodeImage = findViewById(R.id.QRCodeImageAtt);
        eventPosterImageView = findViewById(R.id.imageView2);

        titleText.setText(intent.getStringExtra("title"));
        descriptionText.setText(intent.getStringExtra("description"));
        locationText.setText(intent.getStringExtra("location"));
        dateText.setText(intent.getStringExtra("dateTime"));

        // Load the event poster
        String posterUrl = intent.getStringExtra("posterUrl");
        if (posterUrl != null && !posterUrl.equals("null")) {
            int cornerRadius = 24;
            Glide.with(this)
                    .load(posterUrl)
                    .apply(new RequestOptions()
                            .transform(new CenterCrop(), new RoundedCorners(cornerRadius)))
                    .into(eventPosterImageView);
        } else {
            eventPosterImageView.setImageResource(R.drawable.splash);
        }

        // Load QR code image into ImageView
        String qrCodeImageUrl = intent.getStringExtra("QR");
        if (qrCodeImageUrl != null && !qrCodeImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(qrCodeImageUrl)
                    .override(350, 350)
                    .into(QRCodeImage);
        }

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

    /**
     * Sets up listeners for sign-up or check-in based on the event and attendee details.
     *
     * @param eventID      The ID of the event.
     * @param canCheckIn   A boolean flag indicating whether the attendee can check in.
     */
    private void setListener(String eventID, boolean canCheckIn) {
        FirebaseLoadingTestHelper.increment();
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
        FirebaseLoadingTestHelper.decrement();
    }

    /**
     * Adds a listener for the check-in button.
     */
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

                checkInsRef.document(unique_id).set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Firestore", "DocumentSnapshot successfully written!");
                                toggleInteractButton(false);
                                showRemovalDialog();
                                checkAndGetGeoPoint(unique_id);
                            }
                        });
            }
        });
    }

    private void showRemovalDialog() {
        if (!isFinishing()) {
            View view = LayoutInflater.from(this).inflate(R.layout.congratulations_dialog, null);
            Button okayButton = view.findViewById(R.id.button);

            TextView milestoneTextView = view.findViewById(R.id.congratulationsTextView);
            milestoneTextView.setText("Congratulations! You're Checked-In");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            Dialog dialog = builder.create();
            okayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /**
     * Checks if geo-tracking is enabled for the attendee and retrieves the current location.
     *
     * @param docID The ID of the document.
     */
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

    /**
     * Retrieves the geo-point based on the device's location.
     *
     * @param docID The ID of the document.
     */
    private void getGeoPoint(String docID) {

        // Check if user permission enabled
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
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
                            Toast.makeText(EventDetailsAttendee.this, "Could not get Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Adds the geo-point to the document.
     *
     * @param docID The ID of the document.
     */
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

    /**
     * Adds a listener for the sign-up button.
     *
     * @param canCheckIn A boolean flag indicating whether the attendee can check in.
     */
    private void addSignUpListener(boolean canCheckIn) {
        toggleInteractButton(true);
        interactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsRef.document(eventID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Long maxAttendees = documentSnapshot.getLong("maxAttendees");
                            signUpsRef.whereEqualTo("eventID", eventID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        int currentAttendeesCount = Objects.requireNonNull(task.getResult()).size();
                                        if (maxAttendees != null && currentAttendeesCount < maxAttendees) {
                                            // Allow sign up
                                            proceedWithSignUp();
                                        } else {
                                            // Prevent sign up
                                            Toast.makeText(EventDetailsAttendee.this, "Unsuccessful: Event is full.", Toast.LENGTH_SHORT).show();
                                            toggleInteractButton(false);
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }

            private void proceedWithSignUp() {
                long currentTimeMillis = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String dateString = sdf.format(new Date(currentTimeMillis));

                String unique_id = UUID.randomUUID().toString();
                HashMap<String, String> data = new HashMap<>();
                data.put("eventID", eventID);
                data.put("attendeeID", attendee.getDeviceID());
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

    /**
     * Toggles the visibility and clickability of the interactButton.
     *
     * @param interactable A boolean flag indicating whether the interactButton should be interactable.
     */
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