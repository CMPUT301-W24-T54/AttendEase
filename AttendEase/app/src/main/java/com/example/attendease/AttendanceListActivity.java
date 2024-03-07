package com.example.attendease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the List of Attendees that have checked in to an event
 */
public class AttendanceListActivity extends AppCompatActivity {
    private TextView eventName;
    private ListView attendanceListView;
    private TextView attendanceCount;
    private ImageButton backButton;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference checkInsRef;
    private CollectionReference attendeesRef;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_list);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        checkInsRef = db.collection("checkIns");
        attendeesRef = db.collection("attendees");

        // Initialize UI components
        eventName = findViewById(R.id.event_textview);
        attendanceListView = findViewById(R.id.attendancelist);
        attendanceCount = findViewById(R.id.attendancecount);
        backButton = findViewById(R.id.back_button);

        // Call the function
        intent = getIntent();
        String eventDocID = intent.getStringExtra("eventDocumentId");
        setUpEventName(eventDocID);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Sets up the event name text view
     * @param eventDocID event ID on the database
     */
    private void setUpEventName(String eventDocID) {
        //String event = "FEPcR599noOVDLWK2lD9";
        eventsRef.document(eventDocID).get().addOnSuccessListener(documentSnapshot -> {
            String eventTitle = documentSnapshot.getString("title");
            eventName.setText(eventTitle);
        });
        setUpCheckInsListView(eventDocID);
    }

    /**
     * sets up the Array Adapter for the List View
     * @param eventDocID event ID on the database
     */
    private void setUpCheckInsListView(String eventDocID) {
        checkInsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<String> attendeeIDs = new ArrayList<>();

            // Retrieves the attendeeIDs associated with the event!
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String eventID = document.getString("eventID");
                if (eventDocID.equals(eventID)) {
                    String attendeeID = document.getString("attendeeID");
                    attendeeIDs.add(attendeeID);
                }
            }

            // Retrieves the attendee names associated with the attendeeIDs
            List<String> attendeeNames = new ArrayList<>();
            for (String attendeeID : attendeeIDs) {
                attendeesRef.document(attendeeID).get().addOnSuccessListener(attendeeDocument -> {
                    String attendeeName = attendeeDocument.getString("name");
                    attendeeNames.add(attendeeName);

                    // Updates the ListView with attendee names
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendeeNames);
                    attendanceListView.setAdapter(adapter);

                    // Updates the attendanceCount TextView with the count of attendees
                    String totalCountText = "Total: " + attendeeNames.size(); // OR GET THE COUNT FROM THE LISTVIEW ITSELF
                    attendanceCount.setText(totalCountText);
                });
            }
        });
    }

}
