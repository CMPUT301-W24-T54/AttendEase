package com.example.attendease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.lang.ref.Reference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Attendee_Notifications extends AppCompatActivity implements ViewMsgDialog.AddMsgDialogListener {
    private ArrayList<Msg> dataList;
    private ListView MsgList;
    private ArrayAdapter<Msg> MsgAdapter;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private DocumentReference attendee_Ref;
    private ArrayList<String> stringArray;
    private boolean Array_set=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_notification);

        Intent intent=getIntent();
        //Attendee attendee= (Attendee) getIntent().getSerializableExtra("Attendee");
        //need to implements Serializable in Attendee class
        //attendee.getsignupids
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("notifications");
        attendee_Ref=db.collection("attendees").document("atharva");






        MsgList=findViewById(R.id.Msg_list);
        String[] Title = {};
        String[] Messages = {};
        dataList = new ArrayList<Msg>();
        /*for (int i = 0; i < Title.length; i++) {
            dataList.add(new Msg(Title[i], Messages[i]));
        }*/
        MsgAdapter = new Msg_adapter(this, dataList);
        MsgList.setAdapter(MsgAdapter);



    }
    public void deleteMsg(Msg message, int position){
        MsgAdapter.remove(message);
        MsgAdapter.notifyDataSetChanged();
        /*HashMap<String, Object> updates = new HashMap<>();
        ArrayList<String> newArray = new ArrayList<>();
        newArray.add(message.getUnique_id());
        updates.put("notification_deleted", newArray);*/
        attendee_Ref.update("notification_deleted", FieldValue.arrayUnion(message.getUnique_id()));


    }
    public void addMsg(Msg message){
        MsgAdapter.add(message);
        MsgAdapter.notifyDataSetChanged();
    }




    @Override
    protected void onResume() {
        super.onResume();

        attendee_Ref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document exists in the database
                            stringArray = (ArrayList<String>) documentSnapshot.get("notification_deleted");
                            eve();
                            // Now 'variableValue' contains the value of the variable from the Firestore
                        } else {
                            stringArray=null;
                            eve();
                            // Document does not exist
                        }
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
            Intent intent= new Intent(Attendee_Notifications.this, View_Msg.class);
            intent.putExtra("Title",Title);
            intent.putExtra("Message",Message);
            startActivity(intent);
            new ViewMsgDialog(selectedMsg,position).show(getSupportFragmentManager(), "View Message");
            /*Bundle bundle = new Bundle();
            bundle.putString("selectedMsg",selectedMsg.getMessage());
            bundle.putString("selectedTitle", selectedMsg.getTitle());
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);*/


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
                                String Unique_id=doc.getDocument().getId().toString();


                                if(stringArray!=null){
                                    if (stringArray.contains(Unique_id)){
                                        test_array.remove(Unique_id);
                                        break;
                                    }
                                }
                                //String sent_by= doc.getDocument().getString("sentBy");
                                Log.d("Firestore", String.format("City(%s, %s) fetched", Title,
                                        Notification));
                                Msg add_Msg=new Msg(Title, Notification);
                                add_Msg.setUnique_id(Unique_id);
                                dataList.add(add_Msg);
                                break;
                                /*case REMOVED:
                                    Log.d(TAG, "Removed document: " + dc.getDocument().getData());
                                    break;*/
                        }

                    }
                    for (String test : test_array){
                        attendee_Ref.update("notification_deleted",FieldValue.arrayRemove(test));
                    }

                    //addCitiesInit();
                    MsgAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}