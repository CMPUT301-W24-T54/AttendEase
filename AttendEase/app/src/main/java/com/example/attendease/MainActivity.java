package com.example.attendease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Attendee newAttendee = new Attendee("testDevice", "notAaron", "111-111-1111", "notaarondom", "null");
        Intent startEditIntent = new Intent(this, EditProfileActivity.class);
        startEditIntent.putExtra("user", newAttendee);
        startActivity(startEditIntent);

        Button checkInButton = findViewById(R.id.check_in_button);
        Button createEventButton = findViewById(R.id.create_event_button);
        Button adminButton = findViewById(R.id.admin_button);

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement logic to navigate to the attendees
            }
        });

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement logic to navigate to the organizers
            }
        });

        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdminAccountActivity.class);
                startActivity(intent);}
        });

    }
}