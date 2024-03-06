package com.example.attendease;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private static final String ATTENDEE_COLLECTION = "attendees";
    private String deviceID;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button checkInButton = findViewById(R.id.check_in_button);
        Button createEventButton = findViewById(R.id.create_event_button);
        Button adminButton = findViewById(R.id.admin_button);

        db = FirebaseFirestore.getInstance();

        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Check if Attendee user already exists (deviceID)
                DocumentReference docRef = db.collection(ATTENDEE_COLLECTION).document(deviceID);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Intent intent = new Intent(MainActivity.this, AttendeeDashboardActivity.class);
                            intent.putExtra("deviceID", deviceID);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, UserCheckIn.class);
                            startActivity(intent);
                        }
                    }
                });


            }
        });


        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, OrganizerDashboardActivity.class);
                startActivity(intent);}
        });

        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdminAccountActivity.class);
                startActivity(intent);}
        });

    }
}