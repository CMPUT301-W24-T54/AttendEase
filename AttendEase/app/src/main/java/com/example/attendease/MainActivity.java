package com.example.attendease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Attendee newAttendee = new Attendee("testDevice", "notAaron", "111-111-1111", "notaarondom", "null");
        Intent startEditIntent = new Intent(this, EditProfileActivity.class);
        startEditIntent.putExtra("user", newAttendee);
        startActivity(startEditIntent);
    }
}