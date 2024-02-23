package com.example.attendease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class Attendee_Notifications extends AppCompatActivity implements ViewMsgDialog.AddMsgDialogListener {
    private ArrayList<Msg> dataList;
    private ListView MsgList;
    private ArrayAdapter<Msg> MsgAdapter;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_notification);
        Intent intent=getIntent();
        //Attendee attendee= (Attendee) getIntent().getSerializableExtra("Attendee");
        //need to implements Serializable in Attendee class
        //attendee.getsignupids
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");


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
    }

    //for organizers
    public void addMsg(Msg message){
        MsgAdapter.add(message);
        MsgAdapter.notifyDataSetChanged();
        String Title= message.getTitle().toString();
        String Message= message.getMessage().toString();
        HashMap<String, String> data = new HashMap<>();
        data.put("title", Title);
        data.put("notifications",Message);
        eventsRef.document(Title).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        //for organizers

        ImageButton adds=findViewById(R.id.AddButton);
        adds.setOnClickListener(v -> {
            new ViewMsgDialog().show(getSupportFragmentManager(), "Add Message");

        });

        //forattendee only
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
                                String Title = doc.getDocument().getString("title");
                                String Notification = doc.getDocument().getString("notifications");
                                Log.d("Firestore", String.format("City(%s, %s) fetched", Title,
                                        Notification));
                                dataList.add(new Msg(Title, Notification));
                                break;
                            /*case REMOVED:
                                Log.d(TAG, "Removed document: " + dc.getDocument().getData());
                                break;*/
                        }

                    }
                    //addCitiesInit();
                    MsgAdapter.notifyDataSetChanged();
                }
            }
        });


    }

}