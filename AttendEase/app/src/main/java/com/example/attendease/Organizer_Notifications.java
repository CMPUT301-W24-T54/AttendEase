package com.example.attendease;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;



public class Organizer_Notifications extends AppCompatActivity implements ViewMsgDialog.AddMsgDialogListener {
    private ActivityResultLauncher<Intent> addMsgLauncher;
    private ArrayList<Msg> dataList;
    private ListView MsgList;
    private ArrayAdapter<Msg> MsgAdapter;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_notifications);
        addMsgLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String title = result.getData().getStringExtra("Title");
                        String events = result.getData().getStringExtra("Events");
                        String body = result.getData().getStringExtra("Body");

                        // Now you have the data, you can do whatever you want with it.
                        Msg message = new Msg(title, body, events);
                        addMsg(message);
                    }
                });
        Intent intent=getIntent();
        ImageView imageview=findViewById(R.id.backgroundimageview);
        TextView textview=findViewById(R.id.textView7);
        TextView textview2=findViewById(R.id.textView8);

        imageview.setVisibility(View.INVISIBLE);
        textview.setVisibility(View.INVISIBLE);
        textview2.setVisibility(View.INVISIBLE);
        //Attendee attendee= (Attendee) getIntent().getSerializableExtra("Attendee");
        //need to implements Serializable in Attendee class
        //attendee.getsignupids
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("notifications");


        MsgList=findViewById(R.id.Msg_list);
        String[] Title = {};
        String[] Messages = {};
        dataList = new ArrayList<Msg>();
        eventsRef
                .whereEqualTo("sentBy", "name")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                // Document found where fieldName is equal to desiredValue
                                String Title = doc.getString("title");
                                String Notification = doc.getString("message");
                                Msg notif=new Msg(Title, Notification,"name");
                                notif.setUnique_id(doc.getId());
                                dataList.add(notif);

                            }
                            MsgAdapter = new Msg_adapter(Organizer_Notifications.this, dataList);
                            MsgList.setAdapter(MsgAdapter);
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        makeinvisible();
                    }
                });
        /*for (int i = 0; i < Title.length; i++) {
            dataList.add(new Msg(Title[i], Messages[i]));
        }*/




    }
    public void deleteMsg(Msg message, int position){
        MsgAdapter.remove(message);
        MsgAdapter.notifyDataSetChanged();
        eventsRef.document(message.getUnique_id()).delete();
        makeinvisible();
    }

    //for organizers
    public void addMsg(Msg message){
        MsgAdapter.add(message);
        MsgAdapter.notifyDataSetChanged();
        makeinvisible();
        String Title= message.getTitle().toString();
        String Message= message.getMessage().toString();
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateString = sdf.format(new Date(currentTimeMillis));

        HashMap<String, String> data = new HashMap<>();
        data.put("title", Title);
        data.put("message",Message);
        data.put("timestamp", dateString);
        //need to get name
        data.put("sentBy",Title);
        eventsRef.document(message.getUnique_id()).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
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
            Intent intent= new Intent(Organizer_Notifications.this, View_Msg_Organizer.class);
            intent.putExtra("Title",Title);
            intent.putExtra("Message",Message);
            startActivity(intent);
            //new ViewMsgDialog(selectedMsg,position).show(getSupportFragmentManager(), "View Message");
            /*Bundle bundle = new Bundle();
            bundle.putString("selectedMsg",selectedMsg.getMessage());
            bundle.putString("selectedTitle", selectedMsg.getTitle());
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);*/

        });
        //for organizers

        ImageButton adds=findViewById(R.id.AddButton);
        adds.setOnClickListener(v -> {
            //new ViewMsgDialog().show(getSupportFragmentManager(), "Add Message");
            Intent intent= new Intent(Organizer_Notifications.this,Msg_add.class);
            addMsgLauncher.launch(intent);

            /*Bundle extras = getIntent().getExtras();
            String Title=extras.getString("Title");
            String Events=extras.getString("Events");
            String Body=extras.getString("Body");
            Msg message=new Msg(Title,Body,Events);
            addMsg(message);*/


        });






    }


}