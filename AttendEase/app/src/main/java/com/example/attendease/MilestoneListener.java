package com.example.attendease;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MilestoneListener extends Service {
    String deviceid;
    private final Database database = Database.getInstance();
    public static ArrayList<String> eventslist=new ArrayList<String>();
    private CollectionReference checkInsRef= database.getCheckInsRef();
    private CollectionReference eventRef= database.getEventsRef();
    public static Boolean HasStarted=false;

    //private List<Attendee> attendeeList;

    @Override
    public void onCreate() {
        super.onCreate();
        HasStarted=true;
        Log.d("Service started","Hello");
        deviceid= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        checkInsRef.addSnapshotListener((queryDocumentSnapshotss, em) -> {
            if (em != null) {
                Log.e("AttendanceListActivity", "Error getting check-ins", em);
                return;
            }
            if (queryDocumentSnapshotss != null) {
                //attendeeList = new ArrayList<>();
                eventslist.clear();
                eventRef.whereEqualTo("organizerId",deviceid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Access data of each document using document.getData()
                                eventslist.add(document.getString("eventId"));

                            }
                            if(!eventslist.isEmpty()){
                                for (String event : eventslist) {
                                    Log.d("eventlist", event);
                                }
                                for (String item : eventslist) {
                                    checkInsRef.whereEqualTo("eventID", item).addSnapshotListener((queryDocumentSnapshots, e) -> {
                                        if (e != null) {
                                            Log.e("AttendanceListActivity", "Error getting check-ins", e);
                                            return;
                                        }
                                        if (queryDocumentSnapshots != null) {
                                            //queryDocumentSnapshots.size();
                                            eventRef.whereEqualTo("eventId", item).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        for (QueryDocumentSnapshot document : task.getResult()){
                                                            Double fbCount = document.getDouble("countAttendees");
                                                            Log.d("error",document.getData().toString());
                                                            if ((fbCount == null) || (fbCount < queryDocumentSnapshots.size())) {
                                                                //checkMilestone(queryDocumentSnapshots.size());
                                                                Toast.makeText(getApplicationContext(), "You have"+queryDocumentSnapshots.size()+" attendees", Toast.LENGTH_SHORT).show();
                                                                Map<String, Object> user = new HashMap<>();
                                                                user.put("countAttendees", queryDocumentSnapshots.size());
                                                                eventRef.document(item).update(user);
                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                            // Calculate check-in counts and create Attendee objects

                                        }
                                    });
                                }
                            }

                        }
                    }
                });


                // Calculate check-in counts and create Attendee objects

            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static void addEvents(String event){
        eventslist.add(event);    }
    private void checkMilestone(int totalUniqueAttendees) {
        // The milestones are hard-coded temporarily
        List<Integer> milestones = Arrays.asList(1, 3, 5, 10, 25, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000);
        if (milestones.contains(totalUniqueAttendees)) {
            showMilestoneDialog(totalUniqueAttendees);
        }
    }


    private void showMilestoneDialog(int attendeeCount) {
        Context applicationContext = getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
        LayoutInflater inflater = LayoutInflater.from(applicationContext);
        View view = inflater.inflate(R.layout.milestone_dialog, null);
        builder.setView(view);
        Button okayButton = view.findViewById(R.id.okay_button);

        TextView milestoneTextView = view.findViewById(R.id.milestoneTextView);
        milestoneTextView.setText("Congratulations! Your Event Has Reached " + attendeeCount + " Attendees!");
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

    public Boolean getinstance(){
        return HasStarted;
    }
    public Boolean changeinstance(){
        HasStarted=true;
        return HasStarted;
    }
}
