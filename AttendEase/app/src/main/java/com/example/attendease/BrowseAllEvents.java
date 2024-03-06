package com.example.attendease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class BrowseAllEvents extends AppCompatActivity {
    private ArrayList<Event> dataList;
    private ListView EventList;
    private ArrayAdapter<Event> EventAdapter;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_all_events);

        deviceID = (String) Objects.requireNonNull(getIntent().getExtras()).get("deviceID");

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        EventList=findViewById(R.id.Event_list);
        dataList=new ArrayList<Event>();
        EventAdapter=new BrowseEventAdapter(this,dataList);
        EventList.setAdapter(EventAdapter);



    }

    @Override
    protected void onResume() {
        super.onResume();
        update_datalist();
        EventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event=dataList.get(position);
                Intent intent=new Intent(BrowseAllEvents.this, EventDetailsSignUp.class);
                intent.putExtra("deviceID", deviceID);
                intent.putExtra("eventID", event.getEventId());
                intent.putExtra("Title",event.getTitle());
                intent.putExtra("QR",event.getCheckInQR());
                intent.putExtra("description",event.getDescription());
                intent.putExtra("dateTime",event.getDateTime().toDate().toString());
                intent.putExtra("location",event.getLocation());
                intent.putExtra("posterUrl",event.getPosterUrl());
                startActivity(intent);




            }
        });
    }

    private void update_datalist(){
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {

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
                                Boolean isGeoTrackingEnabled=doc.getDocument().getBoolean("isGeoTrackingEnabled");
                                //not able to import this?
                                int maxAttendees=0;
                                //doc.getDocument().getLong("maxAttendees").intValue();


                                //String sent_by= doc.getDocument().getString("sentBy");
                                Log.d("Firestore", String.format("Event(%s, %s) fetched", title,
                                        description));
                                Event new_event= new Event(eventId,title,description,organizerId,dateTime,location,null,qr,posterUrl,isGeoTrackingEnabled,0);
                                dataList.add(new_event);

                                break;
                                /*case REMOVED:
                                    Log.d(TAG, "Removed document: " + dc.getDocument().getData());
                                    break;*/
                        }

                    }

                    //addCitiesInit();
                    EventAdapter.notifyDataSetChanged();
                }
            }
        });
    }


}