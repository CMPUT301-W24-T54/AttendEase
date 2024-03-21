package com.example.attendease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.idling.CountingIdlingResource;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This class represents the Browse Events screen where
 * an Attendee can browse events they have signed up for
 */
public class BrowseMyEvent extends AppCompatActivity {
    private ArrayList<Event> dataList;
    private ListView EventList;
    private ArrayAdapter<Event> EventAdapter;
    private final Database database = Database.getInstance();
    private CollectionReference eventsRef;
    private CollectionReference signInRef;
    private String eventID;

    private String deviceID;
    private CountingIdlingResource countingIdlingResource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_my_event);
        //Intent intent=getIntent();
        //intent.getStringExtra("deviceID");
        deviceID = (String) Objects.requireNonNull(getIntent().getExtras()).get("deviceID");
        //deviceID="fa405bfc7d76417d";
        signInRef = database.getSignInsRef();
        eventsRef = database.getEventsRef();
        countingIdlingResource = new CountingIdlingResource("FirebaseLoading");



        EventList=findViewById(R.id.Event_list);
        dataList=new ArrayList<Event>();
        EventAdapter=new BrowseEventAdapter(this,dataList);
        EventList.setAdapter(EventAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDatalist();
        EventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event=dataList.get(position);
                Intent intent=new Intent(BrowseMyEvent.this, EventDetailsAttendee.class);
                intent.putExtra("eventID", event.getEventId());
                intent.putExtra("Title",event.getTitle());
                intent.putExtra("QR",event.getCheckInQR());
                intent.putExtra("description",event.getDescription());
                intent.putExtra("dateTime",event.getDateTime().toDate().toString());
                intent.putExtra("location",event.getLocation());
                intent.putExtra("posterUrl",event.getPosterUrl());
                intent.putExtra("canCheckIn",false);
                startActivity(intent);
            }
        });

    }


    /**
     * Updates the event list array adapter with the events on the attendee has signed up for
     */
    private void updateDatalist(){
        signInRef.whereEqualTo("attendeeID",deviceID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    countingIdlingResource.increment();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        // Document found where fieldName is equal to desiredValue
                        eventID = doc.getString("eventID");
                        //Log.d("error",eventID);

                        eventsRef.document(eventID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot eventDocument = task.getResult();
                                String Title = eventDocument.getString("title");
                                String eventId=eventDocument.getId().toString();
                                String QR = eventDocument.getString("checkInQR");
                                String description = eventDocument.getString("description");

                                String organizerId=eventDocument.getString("organizerId");
                                Timestamp dateTime=eventDocument.getTimestamp("dateTime");

                                String location=eventDocument.getString("location");
                                String posterUrl=eventDocument.getString("posterUrl");
                                Boolean isGeoTrackingEnabled=eventDocument.getBoolean("isGeoTrackingEnabled");
                                //not able to import this?
                                int maxAttendees=0;
                                Event new_event= new Event(eventId,Title,description,organizerId,dateTime,location,null,QR,posterUrl,false,0);
                                dataList.add(new_event);
                                EventAdapter.notifyDataSetChanged();

                            }
                        });


                    }
                }
                countingIdlingResource.decrement();

            }
        });
    }


}