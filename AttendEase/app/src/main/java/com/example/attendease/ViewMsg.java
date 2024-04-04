package com.example.attendease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Represents an Activity for displaying a single message.
 */
public class ViewMsg extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_msg);
        Intent intent=getIntent();
        String Title=intent.getStringExtra("Title");
        String Message=intent.getStringExtra("Message");
        String event=intent.getStringExtra("event");
        String event_name=intent.getStringExtra("event_name");
        TextView TitleText=findViewById(R.id.Title);
        TitleText.setText(Title);
        TextView MessageText=findViewById(R.id.body);
        MessageText.setText(Message);
        TextView sentByText=findViewById(R.id.event_name);
        sentByText.setText(event_name);



    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageButton back=findViewById(R.id.Back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent=new Intent(ViewMsg.this, AttendeeNotifications.class);
                startActivity(intent);*/
                finish();
            }
        });
    }
}