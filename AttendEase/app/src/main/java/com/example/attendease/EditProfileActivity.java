package com.example.attendease;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * This class represents the Edit Profile screen for an Attendee
 */
public class EditProfileActivity extends AppCompatActivity {

    // Database side declarations
    private FirebaseFirestore db;
    private CollectionReference attendeesRef;
    private static final String ATTENDEE_COLLECTION = "attendees";
    private static final String NAME_KEY = "name";
    private static final String PHONE_KEY = "phone";
    private static final String EMAIL_KEY = "email";
    private static final String IMAGE_KEY = "image";

    // View declarations
    private CircleImageView profileImage;
    private Button uploadProfileImage;
    private Button removeProfileImage;
    private EditText editProfileName;
    private EditText editProfilePhone;
    private EditText editProfileEmail;
    private Button saveChanges;

    // Attendee User declaration
    private Attendee user;

    // ActivityLauncher to get image from gallery
    private ActivityResultLauncher<String> mGetContent;
    private Uri profileUri;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_edit_profile);

        db = FirebaseFirestore.getInstance();
        attendeesRef = db.collection(ATTENDEE_COLLECTION);

        // Jeremy Logan, 2009, Stack Overflow, Bundle Args in intent
        // https://stackoverflow.com/questions/768969/passing-a-bundle-on-startactivity
        user = (Attendee) Objects.requireNonNull(getIntent().getExtras()).get("user");

        profileImage = findViewById(R.id.edit_profile_pic);
        uploadProfileImage = findViewById(R.id.edit_profile_upload_button);
        removeProfileImage = findViewById(R.id.edit_profile_remove_button);
        editProfileName = findViewById(R.id.edit_profile_name);
        editProfilePhone = findViewById(R.id.edit_profile_phone);
        editProfileEmail = findViewById(R.id.edit_profile_email);
        saveChanges = findViewById(R.id.edit_profile_save_changes);


        // OpenAI, 2024, ChatGPT, User upload profile pic
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            profileImage.setImageURI(result);
                            profileUri = result;
                        }
                    }
                });

        // Add button listeners
        addListeners();
    }


    /**
     * This methods adds listeners to Button views
     */
    private void addListeners(){
        saveChanges.setOnClickListener(v -> {
            updateProfile();
        });

        uploadProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        removeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImage.setImageURI(null);
            }
        });
    }


    /**
     * This method updates the Attendee user's profile locally and remotely
     */
    private void updateProfile() {
        String name = editProfileName.getText().toString();
        String email = editProfileEmail.getText().toString();
        String phone = editProfilePhone.getText().toString();
        Log.d("DEBUG", String.format("%s %s %s", name, email, phone));

        if (name.equals("") || email.equals("") || phone.equals("")) {
            Toast.makeText(this, "Invalid input. Please check your entries and try again", Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle all the contact information data
        HashMap<String, Object> data = new HashMap<>();
        data.put(NAME_KEY, name);
        data.put(EMAIL_KEY, email);
        data.put(PHONE_KEY, phone);

        // OpenAI, 2024, ChatGPT, Upload Profile Pic as PNG
        // Handle the profile image
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + user.getDeviceID() + "/profile.png");
        String downloadUri;

        byte[] pngImageData = getImagePng();

        if (pngImageData != null) {
            imageRef.putBytes(pngImageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            data.put(IMAGE_KEY, uri.toString());
                            Log.d("DEBUG", "onSuccess: image url should show up in doc");
                            saveChangesInDatabase(data);
                        }
                    });
                }
            });
        }
        else {
            saveChangesInDatabase(data);
        }
    }

    private void saveChangesInDatabase(HashMap<String, Object> data){
        attendeesRef.document(user.getDeviceID())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DEBUG", "Changes Saved");
                    }
                });
    }

    private byte[] getImagePng() {
        // OpenAI, 2024, ChatGPT, Upload Profile Pic as PNG
        try {
            // Load the original image into a Bitmap
            InputStream inputStream = getContentResolver().openInputStream(profileUri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

            // Create a ByteArrayOutputStream to hold the PNG image data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Compress the Bitmap into PNG format with 100% quality
            originalBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            // Convert the ByteArrayOutputStream to byte array
            return outputStream.toByteArray();
        } catch (IOException e) {
            Log.e("ERROR", "Error converting image to PNG: " + e.getMessage());
            Toast.makeText(this, "Error converting image to PNG", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
