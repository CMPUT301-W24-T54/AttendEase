package com.example.attendease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class View_Msg extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_msg);
        Intent intent=getIntent();
        String Title=intent.getStringExtra("Title");
        String Message=intent.getStringExtra("Message");
        TextView TitleText=findViewById(R.id.Title);
        TitleText.setText(Title);
        TextView MessageText=findViewById(R.id.textview_third);
        MessageText.setText(Message);


    }
}