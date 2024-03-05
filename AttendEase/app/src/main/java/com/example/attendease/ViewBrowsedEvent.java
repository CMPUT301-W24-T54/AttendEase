package com.example.attendease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class ViewBrowsedEvent extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference signupRef;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_browsed_event);
        intent=getIntent();
        TextView event_title=findViewById(R.id.EventTitle);
        event_title.setText(intent.getStringExtra("Title"));
        TextView description=findViewById(R.id.description);
        description.setText(intent.getStringExtra("description"));
        TextView location=findViewById(R.id.Location);
        location.setText(intent.getStringExtra("location"));
        TextView Date=findViewById(R.id.DateTime);
        Date.setText(intent.getStringExtra("dateTime"));


        db = FirebaseFirestore.getInstance();
        signupRef = db.collection("signIns");


    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageButton back=findViewById(R.id.imageButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button signup=findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventID= intent.getStringExtra("eventID");
                //need to take name from attendee class
                String attendeeID="atharva";
                long currentTimeMillis = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String dateString = sdf.format(new Date(currentTimeMillis));
                String unique_id=UUID.randomUUID().toString();
                HashMap<String, String> data = new HashMap<>();
                data.put("eventID", eventID);
                data.put("attendeeID",attendeeID);
                data.put("timeStamp", dateString);
                signupRef.document(unique_id).set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Firestore", "DocumentSnapshot successfully written!");
                            }
                        });

            }
        });

    }
}