package com.example.attendease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.idling.CountingIdlingResource;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This class represents the Browse Events screen where
 * an Attendee can browse all events in the future
 */
public class BrowseAllEvents extends AppCompatActivity {
    private ArrayList<Event> dataList;
    private ListView eventList;
    private ArrayAdapter<Event> eventArrayAdapter;
    private final Database database = Database.getInstance();
    private CollectionReference eventsRef;
    private String deviceID;
    private CountingIdlingResource countingIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_all_events);
        countingIdlingResource = new CountingIdlingResource("FirebaseLoading");

        deviceID = (String) Objects.requireNonNull(getIntent().getExtras()).get("deviceID");

        eventsRef = database.getEventsRef();
        countingIdlingResource = new CountingIdlingResource("FirebaseLoading");


        eventList =findViewById(R.id.Event_list);
        dataList=new ArrayList<Event>();
        eventArrayAdapter =new BrowseEventAdapter(this,dataList);
        eventList.setAdapter(eventArrayAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDatalist();
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event=dataList.get(position);
                Intent intent=new Intent(BrowseAllEvents.this, EventDetailsAttendee.class);
                intent.putExtra("deviceID", deviceID);
                intent.putExtra("eventID", event.getEventId());
                intent.putExtra("title",event.getTitle());
                intent.putExtra("QR",event.getCheckInQR());
                intent.putExtra("description",event.getDescription());
                intent.putExtra("dateTime",event.getDateTime().toDate().toString());
                intent.putExtra("location",event.getLocation());
                intent.putExtra("posterUrl",event.getPosterUrl());
                intent.putExtra("canCheckIn", false);
                startActivity(intent);
            }
        });
    }

    /**
     * Updates the data list by listening to changes in the Firestore events collection.
     * This method attaches a snapshot listener to the events collection, which triggers
     * whenever there are changes to the documents in the collection. The method retrieves
     * relevant information about each event such as title, description, date and time, location,
     * and other details. It then constructs Event objects and adds them to the data list.
     * Finally, it notifies the adapter to update the UI with the new data.
     */
    private void updateDatalist(){
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    countingIdlingResource.increment();

                    //cityDataList.clear();
                    for (DocumentChange doc: querySnapshots.getDocumentChanges()) {
                        switch (doc.getType()) {
                            case ADDED:
                            case MODIFIED:

                                String title = doc.getDocument().getString("title");
                                String eventId=doc.getDocument().getId().toString();
                                String qr = doc.getDocument().getString("checkInQR");
                                String description = doc.getDocument().getString("description");

                                String organizerId=doc.getDocument().getString("organizerId");
                                Timestamp dateTime=doc.getDocument().getTimestamp("dateTime");

                                String location=doc.getDocument().getString("location");
                                String posterUrl=doc.getDocument().getString("posterUrl");
                                //Boolean isGeoTrackingEnabled=doc.getDocument().getBoolean("isGeoTrackingEnabled");
                                Boolean isGeoTrackingEnabled=false;
                                //not able to import this?
                                int maxAttendees=0;
                                //doc.getDocument().getLong("maxAttendees").intValue();


                                //String sent_by= doc.getDocument().getString("sentBy");
                                Log.d("Firestore", String.format("Event(%s, %s) fetched", title,
                                        description));
                                Event new_event= new Event(eventId,title,description,organizerId,dateTime,location,null,qr,posterUrl,false,0);
                                dataList.add(new_event);

                                break;
                                /*case REMOVED:
                                    Log.d(TAG, "Removed document: " + dc.getDocument().getData());
                                    break;*/
                        }

                    }

                    //addCitiesInit();

                    eventArrayAdapter.notifyDataSetChanged();
                }
                countingIdlingResource.decrement();
                Log.d("debug","helppppp");
            }
        });
    }

    public ArrayList<Event> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<Event> dataList) {
        this.dataList = dataList;
    }

    public void updateDataList() {
        updateDatalist();
    }


}