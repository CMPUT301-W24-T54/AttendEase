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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying the list of sign-ups for a specific event.
 * This class retrieves and displays attendee information associated with an event from Firestore.
 */
public class SignupsListActivity extends AppCompatActivity {
    private TextView eventName;
    private ListView signUpsListView;
    private TextView signUpsCount;
    private ImageButton backButton;

    private final Database database = Database.getInstance();
    private CollectionReference eventsRef;
    private CollectionReference signInsRef;
    private CollectionReference attendeesRef;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signups_list);

        // Initialize Firebase
        eventsRef = database.getEventsRef();
        signInsRef = database.getSignInsRef();
        attendeesRef = database.getAttendeesRef();

        // Initialize UI components
        eventName = findViewById(R.id.event_textview);
        signUpsListView = findViewById(R.id.signupslist);
        signUpsCount = findViewById(R.id.signupscount);
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
     * Sets up the event name TextView by retrieving the event title from Firestore.
     * Calls {@link #setUpSignUpsListView(String)} to initialize the list of sign-ups associated with that event.
     * @param eventDocID The document ID of the event in Firestore.
     */
    private void setUpEventName(String eventDocID) {
        eventsRef.document(eventDocID).get().addOnSuccessListener(documentSnapshot -> {
            String eventTitle = documentSnapshot.getString("title");
            eventName.setText(eventTitle);
        });
        setUpSignUpsListView(eventDocID);
    }

    /**
     * Sets up the ListView with the names of attendees who signed up for the specified event.
     * Retrieves attendee information from Firestore and updates the UI accordingly.
     * @param eventDocID The document ID of the event in Firestore.
     */
    private void setUpSignUpsListView(String eventDocID) {
        signInsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
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
                    signUpsListView.setAdapter(adapter);

                    // Updates the signupscount TextView with the count of attendees
                    String totalCountText = "Total: " + attendeeNames.size(); // OR GET THE COUNT FROM THE LISTVIEW ITSELF
                    signUpsCount.setText(totalCountText);
                });
            }
        });
    }

}
