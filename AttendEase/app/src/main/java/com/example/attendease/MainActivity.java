package com.example.attendease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

/**
 * This class represents the First Screen where the user
 * can choose which actor they wish to proceed as
 */
public class MainActivity extends AppCompatActivity {

    private final Database database = Database.getInstance();
    private CollectionReference attendeesRef;
    private CollectionReference adminsRef;
    private String deviceID;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button checkInButton = findViewById(R.id.check_in_button);
        Button createEventButton = findViewById(R.id.create_event_button);
        Button adminButton = findViewById(R.id.admin_button);
        adminButton.setVisibility(View.INVISIBLE);
        adminButton.setClickable(false);

        attendeesRef = database.getAttendeesRef();
        adminsRef = database.getAdminRef();

        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Remove Admin button if not admin
        adminsRef.document(deviceID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        adminButton.setVisibility(View.VISIBLE);
                        adminButton.setClickable(true);
                    }
                }
            }
        });


        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference docRef = attendeesRef.document(deviceID);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // If the user already exists instantiate Attendee object for them
                            String name = documentSnapshot.getString("name");
                            String phone = documentSnapshot.getString("phone");
                            String email = documentSnapshot.getString("email");
                            String image = documentSnapshot.getString("image");
                            Attendee attendee = new Attendee(deviceID, name, phone, email, image, false);

                            Intent intent = new Intent(MainActivity.this, AttendeeDashboardActivity.class);
                            intent.putExtra("attendee", attendee);  // pass the serializable attendee object
                            startActivity(intent);
                        } else {
                            // If the user does not exist (no name) they have to enter their name
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
                Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });
    }
}