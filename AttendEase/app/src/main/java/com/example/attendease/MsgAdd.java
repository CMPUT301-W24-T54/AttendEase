package com.example.attendease;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.protobuf.StringValue;

import java.util.ArrayList;

public class MsgAdd extends AppCompatActivity {
    public ArrayList<String> eventIDs=new ArrayList<>();
    public ArrayList<String> eventslist=new ArrayList<>();

    public String event;

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
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}