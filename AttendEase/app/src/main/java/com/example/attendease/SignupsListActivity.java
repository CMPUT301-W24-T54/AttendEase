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

public class SignupsListActivity extends AppCompatActivity {
    private TextView eventName;
    private ListView signUpsListView;
    private TextView signUpsCount;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference signInsRef;
    private CollectionReference attendeesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signups_list);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        signInsRef = db.collection("signIns");
        attendeesRef = db.collection("attendees");

        // Initialize UI components
        eventName = findViewById(R.id.event_textview);
        signUpsListView = findViewById(R.id.signupslist);
        signUpsCount = findViewById(R.id.signupscount);

        // Call the function
        setUpEventName(); //MAYBE ADD A PARAMETER FOR A SPECIFIC EVENT VS HARDCODED IN THIS FUNC ITSELF
        //setUpSignUpsListView();

    }

    private void setUpEventName() {
        String event = "FEPcR599noOVDLWK2lD9";
        eventsRef.document(event).get().addOnSuccessListener(documentSnapshot -> {
            String eventTitle = documentSnapshot.getString("title");
            eventName.setText(eventTitle);
        });
        setUpSignUpsListView(event);
    }

    private void setUpSignUpsListView(String event) {
        signInsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
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
                    signUpsListView.setAdapter(adapter);

                    // Updates the signupscount TextView with the count of attendees
                    String totalCountText = "Total: " + attendeeNames.size(); // OR GET THE COUNT FROM THE LISTVIEW ITSELF
                    signUpsCount.setText(totalCountText);
                });
            }
        });
    }

}
