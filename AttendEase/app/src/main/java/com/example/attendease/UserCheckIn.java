package com.example.attendease;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;


public class UserCheckIn extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_check_in); // Replace with the actual layout file name

        // If you want to add functionality to the EditText or the Button, do it here.
        // For example, if you want to react to the Button being pressed:
        Button submitButton = findViewById(R.id.button_submit);
        EditText nameEditText = findViewById(R.id.editText_name);


    }
}
