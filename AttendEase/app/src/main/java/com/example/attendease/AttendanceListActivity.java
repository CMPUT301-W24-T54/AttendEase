package com.example.attendease;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.Arrays;
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

    private final Database database = Database.getInstance();
    private CollectionReference checkInsRef;
    private CollectionReference attendeesRef;
    private Intent intent;
    private String eventDocID;
    private Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_list);

        // Initialize Firebase Collections
        checkInsRef = database.getCheckInsRef();
        attendeesRef = database.getAttendeesRef();

        // Initialize UI components
        eventName = findViewById(R.id.event_textview);
        attendanceListView = findViewById(R.id.attendancelist);
        attendanceCount = findViewById(R.id.attendancecount);
        backButton = findViewById(R.id.back_button);

        // Call the function
        intent = getIntent();
        event = intent.getParcelableExtra("event");
        eventDocID = event.getEventId();
        setUpEventName(event, eventDocID);

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
    private void setUpEventName(Event event, String eventDocID) {
        String eventTitle = event.getTitle();
        eventName.setText(eventTitle);
        setUpCheckInsListView(eventDocID);
    }

    /**
     * Sets up the ListView with the names of attendees who signed up for the specified event.
     * Retrieves attendee information from Firestore and updates the UI accordingly.
     * @param eventDocID The document ID of the event in Firestore.
     */

    private void setUpCheckInsListView(String eventDocID) {
        checkInsRef.whereEqualTo("eventID", eventDocID).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e("AttendanceListActivity", "Error getting check-ins", e);
                return;
            }
            if (queryDocumentSnapshots != null) {
                Map<String, Integer> attendeeCheckInCounts = new HashMap<>();
                List<String> attendeeInfoList = new ArrayList<>();

                // Retrieves the attendee IDs associated with the event
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String attendeeID = document.getString("attendeeID");
                    attendeeCheckInCounts.put(attendeeID, attendeeCheckInCounts.getOrDefault(attendeeID, 0) + 1);
                }

                // Retrieves the attendee names associated with the attendee IDs and update the UI
                for (Map.Entry<String, Integer> entry : attendeeCheckInCounts.entrySet()) {
                    String attendeeID = entry.getKey();
                    int checkInCount = entry.getValue();

                    // Retrieves attendee name from Firestore
                    attendeesRef.document(attendeeID).get().addOnSuccessListener(attendeeDocument -> {
                        String attendeeName = attendeeDocument.getString("name");

                        // Appends check-in count after attendee name
                        String attendeeInfo = attendeeName + " (Check-ins: " + checkInCount + ")";
                        attendeeInfoList.add(attendeeInfo);

                        // Updates the ListView with attendee names and check-in counts
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AttendanceListActivity.this, android.R.layout.simple_list_item_1, attendeeInfoList);
                        attendanceListView.setAdapter(adapter);

                        // Updates the attendanceCount TextView with the count of attendees
                        String totalCountText = "Total: " + attendeeInfoList.size();
                        attendanceCount.setText(totalCountText);

                        // Checks for milestone
                        checkMilestone(attendeeInfoList.size());
                    });
                }
            }

        });
    }

    private void checkMilestone(int attendeeCount) {
        // The milestones are hard-coded temporarily
        List<Integer> milestones = Arrays.asList(1, 5, 10, 25, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000);
        if (milestones.contains(attendeeCount)) {
            showMilestoneDialog(attendeeCount);
        }

    }

    private void showMilestoneDialog(int attendeeCount) {
        if (!isFinishing()) {
            View view = LayoutInflater.from(this).inflate(R.layout.milestone_dialog, null);
            Button okayButton = view.findViewById(R.id.okay_button);

            TextView milestoneTextView = view.findViewById(R.id.milestoneTextView);
            milestoneTextView.setText("Congratulations! Your Event Has Reached " + attendeeCount + " Attendees!");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            Dialog dialog = builder.create();
            okayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}