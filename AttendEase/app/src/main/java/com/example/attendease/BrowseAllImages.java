package com.example.attendease;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BrowseAllImages extends AppCompatActivity {
    private RecyclerView rvImages;
    private final Database database = Database.getInstance();
    private CollectionReference imagesRef;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_all_images);

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
        imagesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imageList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Image image = document.toObject(Image.class);
                    imageList.add(image);
                }
                imageAdapter.notifyDataSetChanged();
            } else {
                Log.d("BrowseAllImages", "Error getting images: ", task.getException());
            }
        });
    }

    private void deleteImageFromFirestore(int position) {
        Image image = imageList.get(position);
        imagesRef.document(image.getImageUrl()).delete();
        imageAdapter.deleteImage(position);
    }
}
