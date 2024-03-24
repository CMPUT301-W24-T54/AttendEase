package com.example.attendease;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.idling.CountingIdlingResource;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This class represents the Attendees Notifications page where an Attendee can see relevant information
 * about the events they have signed up for
 */
public class AttendeeNotifications extends AppCompatActivity implements ViewMsgDialog.AddMsgDialogListener {
    private ArrayList<Msg> dataList;
    private ListView MsgList;
    private ArrayAdapter<Msg> MsgAdapter;

    private CollectionReference eventsRef;
    private DocumentReference attendee_Ref;
    private ArrayList<String> stringArray;

    private BottomNavigationView bottomNav;
    private String deviceID;
    private CollectionReference signInRef;

    private ArrayList<String> eventArray;
    private CollectionReference realeventsRef;
    private Attendee attendee;
    private CountingIdlingResource countingIdlingResource;
    private final Database database = Database.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_notification);
        countingIdlingResource = new CountingIdlingResource("FirebaseLoading");
        attendee = (Attendee) Objects.requireNonNull(getIntent().getExtras()).get("attendee");
        deviceID = attendee.getDeviceID();
        attendee_Ref=database.getAttendeesRef().document(deviceID);
        realeventsRef = database.getEventsRef();
        signInRef=database.getSignInsRef();
        eventsRef=database.getNotificationsRef();
        countingIdlingResource = new CountingIdlingResource("FirebaseLoading");
        bottomNav = findViewById(R.id.attendee_bottom_nav);
        ImageView imageview=findViewById(R.id.backgroundimageview);
        TextView textview=findViewById(R.id.textView7);
        TextView textview2=findViewById(R.id.textView8);

        imageview.setVisibility(View.INVISIBLE);
        textview.setVisibility(View.INVISIBLE);
        textview2.setVisibility(View.INVISIBLE);

        //Intent intent=getIntent();

        //Attendee attendee= (Attendee) getIntent().getSerializableExtra("Attendee");
        //need to implements Serializable in Attendee class
        //attendee.getsignupids
        /*db = FirebaseFirestore.getInstance();
        signInRef = db.collection("signIns");
        eventsRef = db.collection("notifications");
        realeventsRef = db.collection("events");
        Log.d("DEBUG", "Deviceid");
        attendee_Ref=db.collection("attendees").document(deviceID);*/
        //attendee_Ref=db.collection("attendees").document("atharva");
        eventArray=new ArrayList<>();






        MsgList=findViewById(R.id.Msg_list);
        String[] Title = {};
        String[] Messages = {};
        dataList = new ArrayList<Msg>();
        /*for (int i = 0; i < Title.length; i++) {
            dataList.add(new Msg(Title[i], Messages[i]));
        }*/
        MsgAdapter = new MsgAdapter(this, dataList);
        MsgList.setAdapter(MsgAdapter);
        getallnotifications();



    }
    public void deleteMsg(Msg message, int position){
        MsgAdapter.remove(message);
        MsgAdapter.notifyDataSetChanged();
        /*HashMap<String, Object> updates = new HashMap<>();
        ArrayList<String> newArray = new ArrayList<>();
        newArray.add(message.getUnique_id());
        updates.put("notification_deleted", newArray);*/
        attendee_Ref.update("notification_deleted", FieldValue.arrayUnion(message.getUnique_id()));
        makeinvisible();


    }
    public void addMsg(Msg message){
        MsgAdapter.add(message);
        MsgAdapter.notifyDataSetChanged();
    }

    public void makeinvisible(){
        if (dataList.isEmpty()){
            ImageView imageview=findViewById(R.id.backgroundimageview);
            TextView textview=findViewById(R.id.textView7);
            TextView textview2=findViewById(R.id.textView8);

            imageview.setVisibility(View.VISIBLE);
            textview.setVisibility(View.VISIBLE);
            textview2.setVisibility(View.VISIBLE);
        }
        else{
            ImageView imageview=findViewById(R.id.backgroundimageview);
            TextView textview=findViewById(R.id.textView7);
            TextView textview2=findViewById(R.id.textView8);

            imageview.setVisibility(View.INVISIBLE);
            textview.setVisibility(View.INVISIBLE);
            textview2.setVisibility(View.INVISIBLE);
        }
    }





    @Override
    protected void onResume() {
        super.onResume();

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Log.d("DEBUG", String.format("onNavigationItemSelected: %d", id));
                if (id == R.id.nav_home) {// Handle click on Home item
                    Log.d("DEBUG", "Home item clicked");
                } else if (id == R.id.nav_events) {// Handle click on Events item
                    Log.d("DEBUG", "Events item clicked");
                } else if (id == R.id.nav_bell) {// Handle click on Bell item
                    Log.d("DEBUG", "Bell item clicked");
                } else if (id == R.id.nav_profile) {// Handle click on Profile item
                    Log.d("DEBUG", "Profile item clicked");
                    Intent intent = new Intent(AttendeeNotifications.this, EditProfileActivity.class);
                    intent.putExtra("attendee", attendee);
                    startActivity(intent);

                }
                return true;
            }
        });



        MsgList.setOnItemLongClickListener((parent, views, position, id) ->{
            Msg selectedMsg = dataList.get(position);
            new ViewMsgDialog(selectedMsg,position).show(getSupportFragmentManager(), "View Message");
            return true;
            /*Bundle bundle = new Bundle();
            bundle.putString("selectedMsg",selectedMsg.getMessage());
            bundle.putString("selectedTitle", selectedMsg.getTitle());
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);*/

        });
        MsgList.setOnItemClickListener((parent, views, position, id) ->{
            Msg selectedMsg = dataList.get(position);
            String Title=selectedMsg.getTitle().toString();
            String Message=selectedMsg.getMessage().toString();
            String event=selectedMsg.getEvent().toString();
            Intent intent= new Intent(AttendeeNotifications.this, ViewMsg.class);
            intent.putExtra("Title",Title);
            intent.putExtra("Message",Message);
            intent.putExtra("event",event);
            startActivity(intent);
            //new ViewMsgDialog(selectedMsg,position).show(getSupportFragmentManager(), "View Message");
            /*Bundle bundle = new Bundle();
            bundle.putString("selectedMsg",selectedMsg.getMessage());
            bundle.putString("selectedTitle", selectedMsg.getTitle());
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);*/


        });








    }
    private void getallnotifications(){
        attendee_Ref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document exists in the database
                            if (documentSnapshot.contains("notification_deleted")) {
                                // 'notification_deleted' field exists in the document
                                stringArray = (ArrayList<String>) documentSnapshot.get("notification_deleted");
                            } else {
                                // 'notification_deleted' field doesn't exist in the document
                                stringArray = null;
                            }
                            eventlist();
                            // Now 'variableValue' contains the value of the variable from the Firestore
                        } else {
                            stringArray=null;
                            eventlist();
                            // Document does not exist
                        }
                    }
                });
    }
    private void eve(){
        //forattendee only
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    ArrayList <String> test_array=stringArray;
                    //cityDataList.clear();
                    for (DocumentChange doc: querySnapshots.getDocumentChanges()) {
                        switch (doc.getType()) {
                            case ADDED:
                            case MODIFIED:
                                String Title = doc.getDocument().getString("title");
                                String Notification = doc.getDocument().getString("message");
                                String event=doc.getDocument().getString("event");
                                String Unique_id=doc.getDocument().getId().toString();
                                if(!eventArray.contains(event)){
                                    break;
                                }


                                if(stringArray!=null){
                                    if (stringArray.contains(Unique_id)){
                                        test_array.remove(Unique_id);
                                        break;
                                    }
                                }
                                //String sent_by= doc.getDocument().getString("sentBy");
                                Log.d("Firestore", String.format("City(%s, %s) fetched", Title,
                                        Notification));
                                Msg add_Msg=new Msg(Title, Notification,event);
                                add_Msg.setUnique_id(Unique_id);
                                dataList.add(add_Msg);
                                break;
                                /*case REMOVED:
                                    Log.d(TAG, "Removed document: " + dc.getDocument().getData());
                                    break;*/
                        }

                    }
                    if(test_array!=null){
                        for (String test : test_array){
                            attendee_Ref.update("notification_deleted",FieldValue.arrayRemove(test));
                        }
                    }


                    makeinvisible();

                    //addCitiesInit();
                    MsgAdapter.notifyDataSetChanged();
                }
            }
        });

    }
    private void eventlist(){
        signInRef.whereEqualTo("attendeeID",deviceID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        // Document found where fieldName is equal to desiredValue
                        eventArray.add(doc.getString("eventID"));



                    }
                    eve();

                }



            }
        });
    }

}