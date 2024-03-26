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
    private TextView totalCountTextView; // Rename total_count to totalCountTextView
    private RecyclerView rvProfiles;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference attendeesRef = db.collection("attendees");
    private AttendeeAdapter attendeeAdapter;
    private ArrayList<Attendee> attendeeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_all_profiles);

        totalCountTextView = findViewById(R.id.total_count); // Use the correct ID for the TextView
        rvProfiles = findViewById(R.id.rv_profiles);
        rvProfiles.setLayoutManager(new GridLayoutManager(this, 1));
        attendeeAdapter = new AttendeeAdapter(this, attendeeList);
        rvProfiles.setAdapter(attendeeAdapter);

        loadAttendeesFromFirestore();
    }

    private void loadAttendeesFromFirestore() {
        attendeesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                attendeeList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Attendee attendee = document.toObject(Attendee.class);
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
