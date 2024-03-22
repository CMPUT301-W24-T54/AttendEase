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


/**
 * This class represents the Attendee's home dashboard activity
 */
public class AttendeeDashboardActivity extends AppCompatActivity {

    private ImageButton checkInButton;
    private BottomNavigationView bottomNav;
    private TextView seeAllEvents;
    private TextView seeMyEvents;
    private Attendee attendee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_dashboard);

        attendee = (Attendee) Objects.requireNonNull(getIntent().getExtras()).get("attendee");

        checkInButton = findViewById(R.id.scan_new_qr);
        bottomNav = findViewById(R.id.attendee_bottom_nav);

        seeMyEvents = findViewById(R.id.see_all);
        seeAllEvents = findViewById(R.id.see_all2);

        addListeners();
    }

    /**
     * Sets up listeners for various UI elements in the Attendee Dashboard activity.
     * These listeners handle user interactions such as button clicks and navigation item selections.
     * - The check-in button listener starts the QR Scanner activity to facilitate attendee check-in.
     * - The "See All Events" button listener navigates to the Browse All Events activity.
     * - The bottom navigation listener handles clicks on navigation items (Home, Events, Bell, Profile),
     *   logging debug information and initiating corresponding activities when items are clicked.
     */
    private void addListeners() {
        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO check if need to pass Attendee argument
                Intent intent = new Intent(AttendeeDashboardActivity.this, QRScannerActivity.class);
                intent.putExtra("attendee", attendee);
                startActivity(intent);
            }
        });

        seeAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboardActivity.this, BrowseAllEvents.class);
                intent.putExtra("attendee", attendee);
                startActivity(intent);
            }
        });

        seeMyEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboardActivity.this, BrowseMyEvent.class);
                intent.putExtra("attendee", attendee);
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
                    intent.putExtra("attendee", attendee);
                    startActivity(intent);

                }
                return true;
            }
        });


    }
}
