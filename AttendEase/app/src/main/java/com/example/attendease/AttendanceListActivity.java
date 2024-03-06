package com.example.attendease;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AttendanceListActivity extends AppCompatActivity {
    private TextView eventName;
    private ListView attendanceListView;
    private TextView attendanceCount;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference checkInsRef;
    private CollectionReference attendeesRef;

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

        // Call the function
        setUpEventName(); //MAYBE ADD A PARAMETER FOR A SPECIFIC EVENT VS HARDCODED IN THIS FUNC ITSELF
        //setUpCheckInsListView();

    }

    private void setUpEventName() {
        String event = "FEPcR599noOVDLWK2lD9";
        eventsRef.document(event).get().addOnSuccessListener(documentSnapshot -> {
            String eventTitle = documentSnapshot.getString("title");
            eventName.setText(eventTitle);
        });
        setUpCheckInsListView(event);
    }

    private void setUpCheckInsListView(String event) {
        checkInsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<String> attendeeIDs = new ArrayList<>();

            // Retrieves the attendeeIDs associated with the event!
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String eventID = document.getString("eventID");
                if (event.equals(eventID)) {
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
