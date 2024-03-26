package com.example.attendease;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity for displaying the list of check ins' for a specific event.
 * This class retrieves and displays attendee information associated with an event from Firestore.
 */
public class AttendanceListActivity extends AppCompatActivity {
    private MapView mapView;
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

//    private int[] milestones = {1, 5, 10, 50, 100}; // Define milestones
//    private boolean[] milestoneReached = new boolean[milestones.length];
    private List<Attendee> attendeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_list);

        // Initialize Firebase Collections
        checkInsRef = database.getCheckInsRef();
        attendeesRef = database.getAttendeesRef();

        // Initialize UI components
        mapView = findViewById(R.id.mapView);
        eventName = findViewById(R.id.event_textview);
        attendanceListView = findViewById(R.id.attendancelist);
        attendanceCount = findViewById(R.id.attendancecount);
        backButton = findViewById(R.id.back_button);

        // Call the function
        intent = getIntent();
        event = intent.getParcelableExtra("event");
        eventDocID = event.getEventId();
        setUpEventName(event, eventDocID);
        setUpMap();
//        showMilestoneDialogIfNeeded();

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
                attendeeList = new ArrayList<>();

                // Calculate check-in counts and create Attendee objects
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String attendeeID = document.getString("attendeeID");
                    int checkInCount = calculateCheckInCount(queryDocumentSnapshots, attendeeID);
                    attendeesRef.document(attendeeID).get().addOnSuccessListener(attendeeDocument -> {
                        String attendeeName = attendeeDocument.getString("name");
                        String url = attendeeDocument.getString("image");

                        // Check if the attendee is already in the list
                        boolean attendeeExists = false;
                        for (Attendee attendee : attendeeList) {
                            if (attendee.getName().equals(attendeeName)) {
                                // Update the existing attendee's check-in count
                                attendee.setCheckInCount(checkInCount);
                                attendeeExists = true;
                                break;
                            }
                        }
                        if (!attendeeExists) {
                            // Create a new Attendee object if the attendee is not in the list
                            Attendee attendee = new Attendee(attendeeName, checkInCount, url);
                            attendeeList.add(attendee);
                        }

                        ArrayAdapter<Attendee> adapter = new AttendanceListAdapter(this, R.layout.list_item_attendance, attendeeList);
                        attendanceListView.setAdapter(adapter);

                        String totalCountText = "Total: " + attendeeList.size();
                        attendanceCount.setText(totalCountText);

                    });
                }
            }
        });
    }

    // Calculate check-in count for a specific attendee
    private int calculateCheckInCount(QuerySnapshot queryDocumentSnapshots, String attendeeID) {
        int count = 0;
        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
            String id = document.getString("attendeeID");
            if (id != null && id.equals(attendeeID)) {
                count++;
            }
        }
        return count;
    }
//    private void showMilestoneDialogIfNeeded() {
//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        boolean milestoneDialogShown = sharedPreferences.getBoolean("milestoneDialogShown", false);
//
//        if (!milestoneDialogShown) {
//            int numberOfAttendees = attendeeList.size();
//
//            for (int i = 0; i < milestones.length; i++) {
//                if (numberOfAttendees >= milestones[i] && !milestoneReached[i]) {
//                    milestoneReached[i] = true;
//                    showMilestoneDialog(milestones[i]);
//                }
//            }
//
//            // Check if all milestones are reached, mark that milestone dialog has been shown
//            boolean allMilestonesReached = true;
//            for (boolean reached : milestoneReached) {
//                if (!reached) {
//                    allMilestonesReached = false;
//                    break;
//                }
//            }
//
//            if (allMilestonesReached) {
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putBoolean("milestoneDialogShown", true);
//                editor.apply();
//            }
//        }
//    }
//
//    private void showMilestoneDialog(int milestone) {
////        // Implement the logic to show the milestone dialog for the specified milestone
////        // For example, you can create a custom dialog
////        Dialog dialog = new Dialog(this);
////        dialog.setContentView(R.layout.milestone_dialog);
////        TextView milestoneTextView = dialog.findViewById(R.id.milestoneTextView);
////        milestoneTextView.setText("Congratulations! Your Event Has Reached " + milestone + " Attendees!");
////        dialog.show();
////    }


    private void setUpMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setTilesScaledToDpi(true);
        mapView.getLocalVisibleRect(new Rect());
        IMapController controller = mapView.getController();
        controller.setZoom(3);

        // StackOverflow, NeoKerd, Click listener in open street maps
        // https://stackoverflow.com/questions/67850072/set-onclicklistener-for-mapview-using-open-street-map

        mapView.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Intent mapIntent = new Intent(AttendanceListActivity.this, MapActivity.class);
                mapIntent.putExtra("event", event);
                startActivity(mapIntent);
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        }));
    }
}