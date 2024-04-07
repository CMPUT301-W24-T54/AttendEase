package com.example.attendease;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.protobuf.StringValue;

import java.util.ArrayList;

/**
 * This represents adding a new message (notification).
 * This activity allows users to create and post a new message with a title, body, and associated event.
 */
public class MsgAdd extends AppCompatActivity {
    public ArrayList<String> eventIDs=new ArrayList<>();
    public ArrayList<String> eventslist=new ArrayList<>();

    public String event;
    public String event_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_add);
        Intent intent=getIntent();
        eventIDs=intent.getStringArrayListExtra("eventIDs");
        eventslist=intent.getStringArrayListExtra("eventslist");
        Spinner events_opt=findViewById(R.id.Event);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventslist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        events_opt.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.organizer_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.nav_notifications);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, OrganizerDashboardActivity.class));
                return true;
            } else if (id == R.id.nav_events) {
                startActivity(new Intent(this, OrganizerMyEventsActivity.class));
                return true;
            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(this, OrganizerNotifications.class));
            }
            return false;
        });
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
        Spinner events_opt=findViewById(R.id.Event);
        events_opt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                event = eventIDs.get(position);
                event_name=eventslist.get(position);
                Log.d("error", String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button post=findViewById(R.id.post_button);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText Title=findViewById(R.id.Title);
                //EditText Events=findViewById(R.id.Event);
                EditText Body=findViewById(R.id.body).findViewById(R.id.body_text);
                String title=Title.getText().toString();

                //String events=Events.getText().toString();
                String body=Body.getText().toString();
                // Create an intent to return data to the first activity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Title", title);
                returnIntent.putExtra("Events", event);
                returnIntent.putExtra("Body", body);
                returnIntent.putExtra("EventName", event_name);
                setResult(Activity.RESULT_OK, returnIntent);
                View view = LayoutInflater.from(MsgAdd.this).inflate(R.layout.notification_posted_dialog, null);
                Button okayButton = view.findViewById(R.id.okayButton);

                AlertDialog.Builder builder = new AlertDialog.Builder(MsgAdd.this);
                builder.setView(view);
                Dialog dialog = builder.create();
                okayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
                    }
                });
                dialog.show();
            }
        });
    }
}