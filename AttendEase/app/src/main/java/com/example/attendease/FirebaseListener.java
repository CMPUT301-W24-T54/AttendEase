package com.example.attendease;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.test.espresso.idling.CountingIdlingResource;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class FirebaseListener extends Service{
    //FirebaseFirestore db = FirebaseFirestore.getInstance();
    //CollectionReference collectionRef = db.collection("notifications");
    String deviceid;
    private CollectionReference eventsRef;
    private CollectionReference attendeeRef;
    private final Database database = Database.getInstance();
    private CollectionReference signInRef;
    CollectionReference collectionRef=database.getNotificationsRef();
    private CountingIdlingResource countingIdlingResource;
    private ArrayList<String> eventArray;
    private String TimeStamp;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private Date timeStampDate;




    @Override
    public void onCreate() {
        super.onCreate();
        deviceid=Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        countingIdlingResource = new CountingIdlingResource("FirebaseLoading");
        signInRef=database.getSignInsRef();
        attendeeRef=database.getAttendeesRef();
        countingIdlingResource = new CountingIdlingResource("FirebaseLoading");
        TimeStamp = retrieveTimeStamp();
        if (retrieveTimeStamp().isEmpty()){
            setTimeStamp();
        }


        collectionRef.whereGreaterThan("timestamp",TimeStamp).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("error", "Listen failed.", error);
                    return;
                }
                Log.d("success", "New document: " + "testing");

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        String event=dc.getDocument().getString("event");
                        if (event != null) {
                            Log.d("yope", event+deviceid);
                            signInRef.whereEqualTo("eventID",event).whereEqualTo("attendeeID",deviceid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(!queryDocumentSnapshots.isEmpty()){
                                        String documentTimestamp = dc.getDocument().getString("timestamp");
                                        // Convert documentTimestamp to Date object
                                        try {
                                            Date documentTimestampDate = sdf.parse(documentTimestamp);
                                            timeStampDate=sdf.parse(TimeStamp);
                                            if (timeStampDate.before(documentTimestampDate)) {
                                                // Update TimeStamp if document timestamp is greater
                                                TimeStamp = documentTimestamp;
                                                saveTimeStamp(TimeStamp);
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if(!dc.getDocument().getString("title").isEmpty()){
                                            Toast.makeText(getApplicationContext(), "Got new message with title: "+dc.getDocument().getString("title"), Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                }
                            });
                        }




                    }
                }

            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void setTimeStamp() {
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        TimeStamp = sdf.format(new Date(currentTimeMillis));

        // Save TimeStamp in SharedPreferences
        saveTimeStamp(TimeStamp);
    }

    private String retrieveTimeStamp() {
        // Retrieve TimeStamp from SharedPreferences
        return getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("TimeStamp", "");
    }

    private void saveTimeStamp(String timeStamp) {
        // Save TimeStamp in SharedPreferences
        getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().putString("TimeStamp", timeStamp).apply();
    }

    private void settime(){
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        TimeStamp = sdf.format(new Date(currentTimeMillis));

    }


    private void eventlist(){
        signInRef.whereEqualTo("attendeeID",deviceid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    eventArray.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        // Document found where fieldName is equal to desiredValue
                        eventArray.add(doc.getString("eventID"));



                    }





                }



            }
        });
    }
}
