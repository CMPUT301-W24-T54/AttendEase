package com.example.attendease;

import static com.example.attendease.R.id.admin_bottom_nav;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents an activity for browsing all images stored in Firestore.
 * Images are retrieved from Firestore and displayed in a RecyclerView.
 */
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

        BottomNavigationView bottomNavImagesAdmin = findViewById(admin_bottom_nav);
        bottomNavImagesAdmin.setSelectedItemId(R.id.nav_image);
        bottomNavImagesAdmin.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(BrowseAllImages.this, AdminDashboardActivity.class));
                    return true;
                } else if (id == R.id.nav_events) {
                    startActivity(new Intent(BrowseAllImages.this, BrowseAllEventsAdmin.class));
                    return true;
                } else if (id == R.id.nav_image) {
                    // Already on the BrowseAllImages, no need to start a new instance
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(BrowseAllImages.this, BrowseAllAttendees.class));
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Retrieves images from Firestore and populates the RecyclerView.
     */
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

    /**
     * Deletes an image from Firestore and updates the RecyclerView.
     *
     * @param position The position of the image in the RecyclerView.
     */
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
                showRemovalDialog();
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

    /**
     * Helper to parse image url to get event ID and event poster name
     * @param imageUrl Url to be parsed
     * @return String array with event ID and poster name
     */
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

    /**
     * Pop-up dialog for image removal
     */
    private void showRemovalDialog() {
        if (!isFinishing()) {
            View view = LayoutInflater.from(this).inflate(R.layout.photo_removed_dialog, null);
            Button okayButton = view.findViewById(R.id.button);

            TextView milestoneTextView = view.findViewById(R.id.photoRemovedTextView);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
    }
}