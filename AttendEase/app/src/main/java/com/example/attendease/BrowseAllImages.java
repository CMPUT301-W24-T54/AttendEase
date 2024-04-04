package com.example.attendease;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrowseAllImages extends AppCompatActivity {
    private RecyclerView rvImages;
    private final Database database = Database.getInstance();
    private CollectionReference imagesRef;
    private CollectionReference eventsRef;
    private CollectionReference attendeesRef;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_all_images);

        eventsRef = database.getEventsRef();
        attendeesRef = database.getAttendeesRef();

        rvImages = findViewById(R.id.rv_images);
        rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        imageAdapter = new ImageAdapter(this, imageList);
        rvImages.setAdapter(imageAdapter);

        imagesRef = database.getImagesRef();
        loadImagesFromFirestore();

        imageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteImageFromFirestore(position);
            }
        });
    }

    private void loadImagesFromFirestore() {
        eventsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String imageUrl = documentSnapshot.getString("posterUrl");
                        if (imageUrl != null && !imageUrl.equals("null") && !imageUrl.equals("")) {
                            Image image = new Image(imageUrl);
                            imageList.add(image);
                            Log.d("DEBUG", String.format("IMAGE : %s %s ", parseImageUrl(imageUrl)[0], parseImageUrl(imageUrl)[1]));
                        }
                        imageAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("DEBUG", "onComplete: Could not load event posters");
                }
            }
        });

        attendeesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String imageUrl = documentSnapshot.getString("image");
                        if (imageUrl != null && !imageUrl.equals("null") && !imageUrl.equals("")) {
                            Image image= new Image(imageUrl);
                            imageList.add(image);
                            Log.d("DEBUG", String.format("IMAGE : %s %s ", parseImageUrl(imageUrl)[0], parseImageUrl(imageUrl)[1]));
                        }
                        imageAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("DEBUG", "onComplete: Could not load attendee images");
                }
            }
        });
    }

    private void deleteImageFromFirestore(int position) {
        Image image = imageList.get(position);
        String imageUrl = image.getImageUrl();
        String[] imageInfo = parseImageUrl(imageUrl);
        String docId = imageInfo[0];
        String imageType = imageInfo[1];

        HashMap<String, Object> data = new HashMap<>();

        if (Objects.equals(imageType, "profile.png")) {
            Log.d("IMAGE DELETE ATTENDEE", "deleteImageFromFirestore: came here");
            data.put("image", "");
            attendeesRef.document(docId).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("DEBUG", "onComplete: image removed from attendee");
                    }
                }
            });
        } else if (Objects.equals(imageType, "eventposter.png")) {
            data.put("image", "null");
            eventsRef.document(docId).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("DEBUG", "onComplete: image removed from event");
                    }
                }
            });
        }

        StorageReference photoRef = database.getStorage().getReferenceFromUrl(imageUrl);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d("DEBUG", "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // Uh-oh, an error occurred!
                Log.d("DEBUG", "onFailure: did not delete file");
            }
        });


        imageAdapter.deleteImage(position);
    }

    public static String[] parseImageUrl(String imageUrl) {
        String[] result = new String[2];
        Pattern pattern = Pattern.compile("%2F([a-zA-Z0-9_-]+)%2F([^?]+)");
        Matcher matcher = pattern.matcher(imageUrl);

        if (matcher.find()) {
            result[0] = matcher.group(1); // Document ID
            result[1] = matcher.group(2); // Image Name
        }

        return result;
    }
}