package com.example.attendease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity for displaying the list of check ins' for a specific event.
 * This class retrieves and displays attendee information associated with an event from Firestore.
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
    private String eventDocID;
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
        eventDocID = intent.getStringExtra("eventDocumentId");
        setUpEventName(eventDocID);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Sets up the event name TextView by retrieving the event title from Firestore.
     * Calls {@link #setUpCheckInsListView(String)} to initialize the list of check-Ins.
     * @param eventDocID The document ID of the event in Firestore.
     */
    private void setUpEventName(String eventDocID) {
        eventsRef.document(eventDocID).get().addOnSuccessListener(documentSnapshot -> {
            String eventTitle = documentSnapshot.getString("title");
            eventName.setText(eventTitle);
        });
        setUpCheckInsListView(eventDocID);
    }

    /**
     * Sets up the ListView with the names of attendees who signed up for the specified event.
     * Retrieves attendee information from Firestore and updates the UI accordingly.
     * @param eventDocID The document ID of the event in Firestore.
     */
    private void setUpCheckInsListView(String eventDocID) {
        checkInsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            Map<String, Integer> attendeeCheckInCounts = new HashMap<>();

            // Retrieves the attendeeIDs associated with the event!
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String eventID = document.getString("eventID");
                if (eventDocID.equals(eventID)) {
                    String attendeeID = document.getString("attendeeID");
                    attendeeCheckInCounts.put(attendeeID, attendeeCheckInCounts.getOrDefault(attendeeID, 0) + 1);
                }
            }

            // Retrieves the attendee names associated with the attendeeIDs
            List<String> attendeeInfoList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : attendeeCheckInCounts.entrySet()) {
                String attendeeID = entry.getKey();
                int checkInCount = entry.getValue();

                attendeesRef.document(attendeeID).get().addOnSuccessListener(attendeeDocument -> {
                    String attendeeName = attendeeDocument.getString("name");
                    String attendeeInfo = attendeeName + " (Check-ins: " + checkInCount + ")";
                    attendeeInfoList.add(attendeeInfo);

                    // Updates the ListView with attendee names
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendeeInfoList);
                    attendanceListView.setAdapter(adapter);

                    // Updates the attendanceCount TextView with the count of attendees
                    String totalCountText = "Total: " + attendeeInfoList.size(); // OR GET THE COUNT FROM THE LISTVIEW ITSELF
                    attendanceCount.setText(totalCountText);
                });
            }
        });
    }

}
