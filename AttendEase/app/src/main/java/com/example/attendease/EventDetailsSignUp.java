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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class EventDetailsSignUp extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference signUpsRef;
    private CollectionReference checkInsRef;
    private Intent intent;
    private TextView titleText;
    private TextView locationText;
    private TextView descriptionText;
    private TextView dateText;
    private ImageButton backButton;
    private Button signupButton;
    private String deviceID;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_browsed_event);

        intent = getIntent();

        titleText = findViewById(R.id.EventTitle);
        descriptionText = findViewById(R.id.description);
        locationText = findViewById(R.id.Location);
        dateText = findViewById(R.id.DateTime);

        titleText.setText(intent.getStringExtra("Title"));
        descriptionText.setText(intent.getStringExtra("description"));
        locationText.setText(intent.getStringExtra("location"));
        dateText.setText(intent.getStringExtra("dateTime"));

        backButton = findViewById(R.id.imageButton);
        signupButton = findViewById(R.id.signup);

        deviceID = intent.getStringExtra("deviceID");
        eventID = intent.getStringExtra("eventID");

        db = FirebaseFirestore.getInstance();
        signUpsRef = db.collection("signIns");
        checkInsRef = db.collection("checkIns");

        addListeners();
        hideSignUp();
    }

    private void addListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
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
                signUpsRef.document(unique_id).set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Firestore", "DocumentSnapshot successfully written!");
                                signupButton.setVisibility(View.INVISIBLE);
                                signupButton.setClickable(false);
                                Toast.makeText(EventDetailsSignUp.this, "Sign Up successful!", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }

    private void hideSignUp() {
        signUpsRef
                .whereEqualTo("attendeeID", deviceID)
                .whereEqualTo("eventID", eventID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            signupButton.setVisibility(View.INVISIBLE);
                            signupButton.setClickable(false);
                        }
                    }
                });
    }
}