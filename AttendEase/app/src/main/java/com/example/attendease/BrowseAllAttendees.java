package com.example.attendease;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BrowseAllAttendees extends AppCompatActivity {
    private TextView totalCountTextView;
    private RecyclerView rvProfiles;
    private final Database database = Database.getInstance();
    private CollectionReference attendeesRef;
    private AttendeeAdapter attendeeAdapter;
    private ArrayList<Attendee> attendeeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_all_profiles);

        totalCountTextView = findViewById(R.id.total_count);
        rvProfiles = findViewById(R.id.rv_profiles);
        rvProfiles.setLayoutManager(new GridLayoutManager(this, 1));
        attendeeAdapter = new AttendeeAdapter(this, attendeeList);
        rvProfiles.setAdapter(attendeeAdapter);

        attendeesRef = database.getAttendeesRef();
        loadAttendeesFromFirestore();
    }

    private void loadAttendeesFromFirestore() {
        attendeesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                attendeeList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getId();
                    String name = document.getString("name");
                    String phone = document.getString("phone");
                    String email = document.getString("email");
                    String image = document.getString("image");
                    boolean geoTrackingEnabled = Boolean.TRUE.equals(document.getBoolean("geoTrackingEnabled"));
                    Attendee attendee = new Attendee(id, name, phone, email, image, geoTrackingEnabled);
                    attendeeList.add(attendee);
                }
                attendeeAdapter.notifyDataSetChanged();

                String totalCountText = String.valueOf(attendeeList.size());
                totalCountTextView.setText(totalCountText);
            } else {
                Log.d("BrowseAllAttendee", "Error getting attendees: ", task.getException());
            }
        });
    }
}
