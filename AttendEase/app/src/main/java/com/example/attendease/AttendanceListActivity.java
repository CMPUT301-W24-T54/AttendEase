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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private CollectionReference eventsRef;
    private Intent intent;
    private String eventDocID;
    private Event event;

    private List<Attendee> attendeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_list);

        // Initialize Firebase Collections
        checkInsRef = database.getCheckInsRef();
        attendeesRef = database.getAttendeesRef();
        eventsRef = database.getEventsRef();

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
//        if (event != null) {
//            eventDocID = event.getEventId();
//            setUpEventName(event, eventDocID);
//            setUpMap();
//        } else {
//            Log.e("AttendanceListActivity", "Event object is null.");
//        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.organizer_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.nav_events);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, OrganizerDashboardActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_events) {
                Intent intent = new Intent(this, OrganizerMyEventsActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_notifications) {
                Intent intent = new Intent(this, OrganizerNotifications.class);
                startActivity(intent);
            }
            return false;
        });

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
                Set<String> uniqueAttendees = new HashSet<>();
                attendeeList = new ArrayList<>();

                // Calculate check-in counts and create Attendee objects
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String attendeeID = document.getString("attendeeID");
                    uniqueAttendees.add(attendeeID); // Add attendee to set to track uniqueness
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
                eventsRef.whereEqualTo("eventId", eventDocID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Double fbCount = document.getDouble("countAttendees");
                                if ((fbCount == null) || (fbCount < uniqueAttendees.size())) {
                                    checkMilestone(uniqueAttendees.size());
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("countAttendees", uniqueAttendees.size());
                                    eventsRef.document(eventDocID).update(user);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * Calculate check-in count for a specific attendee.
     * @param queryDocumentSnapshots The QuerySnapshot containing check-in data.
     * @param attendeeID The ID of the attendee to calculate the check-in count for.
     * @return The check-in count for the specified attendee.
     */
    private int calculateCheckInCount(QuerySnapshot queryDocumentSnapshots, String attendeeID) {
        int count = 0;
//        if (queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments() != null) {
//            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                String id = document.getString("attendeeID");
//                if (id != null && id.equals(attendeeID)) {
//                    count++;
//                }
//            }
//        }
        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
            String id = document.getString("attendeeID");
            if (id != null && id.equals(attendeeID)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if the total number of unique attendees has reached a milestone and displays a dialog if it has.
     * @param totalUniqueAttendees The total number of unique attendees for the event.
     */
    private void checkMilestone(int totalUniqueAttendees) {
        // The milestones are hard-coded temporarily
        List<Integer> milestones = Arrays.asList(1, 3, 5, 10, 25, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000);
        if (milestones.contains(totalUniqueAttendees)) {
            showMilestoneDialog(totalUniqueAttendees);
        }
    }

    /**
     * Displays a milestone dialog congratulating the event organizer on reaching a milestone.
     * @param attendeeCount The number of attendees for the milestone reached.
     */
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

    /**
     * This function sets up the map view and it's settings for displaying the event location.
     */
    private void setUpMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setTilesScaledToDpi(true);
        mapView.getLocalVisibleRect(new Rect());
        IMapController controller = mapView.getController();
        controller.setZoom(3);

        GeoPoint myLocationGeoPoint = new GeoPoint(53.5409192657743, -113.47904085523885);
        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        controller.setCenter(myLocationGeoPoint);
        controller.animateTo(myLocationGeoPoint);
        controller.setZoom(12);
        mapView.getOverlays().add(locationOverlay);
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