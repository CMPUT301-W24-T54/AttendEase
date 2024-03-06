package com.example.attendease;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Objects;

public class AttendeeDashboardActivity extends AppCompatActivity {

    private ImageButton checkInButton;
    private BottomNavigationView bottomNav;
    private TextView seeAllEvents;
    private TextView seeMyEvents;

    private String deviceID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_dashboard);

        deviceID = (String) Objects.requireNonNull(getIntent().getExtras()).get("deviceID");

        checkInButton = findViewById(R.id.scan_new_qr);
        bottomNav = findViewById(R.id.attendee_bottom_nav);

        seeMyEvents = findViewById(R.id.see_all);
        seeAllEvents = findViewById(R.id.see_all2);

        addListeners();
    }

    private void addListeners() {
        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO check if need to pass Attendee argument
                Intent intent = new Intent(AttendeeDashboardActivity.this, QRScannerActivity.class);
                startActivity(intent);
            }
        });

        seeAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboardActivity.this, BrowseAllEvents.class);
                intent.putExtra("deviceID", deviceID);
                startActivity(intent);
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Log.d("DEBUG", String.format("onNavigationItemSelected: %d", id));
                if (id == R.id.nav_home) {// Handle click on Home item
                    Log.d("DEBUG", "Home item clicked");
                } else if (id == R.id.nav_events) {// Handle click on Events item
                    Log.d("DEBUG", "Events item clicked");
                } else if (id == R.id.nav_bell) {// Handle click on Bell item
                    Log.d("DEBUG", "Bell item clicked");
                } else if (id == R.id.nav_profile) {// Handle click on Profile item
                    Log.d("DEBUG", "Profile item clicked");
                    Intent intent = new Intent(AttendeeDashboardActivity.this, EditProfileActivity.class);
                    intent.putExtra("deviceID", deviceID);
                    startActivity(intent);

                }
                return true;
            }
        });


    }
}
