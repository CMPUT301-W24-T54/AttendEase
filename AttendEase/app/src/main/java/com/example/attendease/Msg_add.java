package com.example.attendease;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Msg_add extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_add);
        Intent intent=getIntent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageButton back=findViewById(R.id.Back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button post=findViewById(R.id.post_button);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText Title=findViewById(R.id.Title);
                EditText Events=findViewById(R.id.Event);
                EditText Body=findViewById(R.id.body).findViewById(R.id.body_text);
                String title=Title.getText().toString();
                String events=Events.getText().toString();
                String body=Body.getText().toString();
                // Create an intent to return data to the first activity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Title", title);
                returnIntent.putExtra("Events", events);
                returnIntent.putExtra("Body", body);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}