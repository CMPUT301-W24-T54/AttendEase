package com.example.attendease;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * This class represents the Edit Profile screen for an Attendee.
 * It allows users to edit their profile information and upload a profile picture.
 */
public class EditProfileActivity extends AppCompatActivity {

    // TODO : This is the main reason why we want to pass an Attendee
    // TODO : UI relies on database too much, GUI butter knife is useless here

    // Database side declarations
    private final Database database = Database.getInstance();
    private CollectionReference attendeesRef;
    private StorageReference storageRef;
    private static final String NAME_KEY = "name";
    private static final String PHONE_KEY = "phone";
    private static final String EMAIL_KEY = "email";
    private static final String IMAGE_KEY = "image";
    private static final String TRACKING = "geoTrackingEnabled";

    // View declarations
    private CircleImageView profileImage;
    private Button uploadProfileImage;
    private Button removeProfileImage;
    private EditText editProfileName;
    private EditText editProfilePhone;
    private EditText editProfileEmail;
    private CheckBox geoTrackingCheckBox;
    private Button saveChanges;

    // Attendee User declaration
    private Attendee attendee;
    private String deviceID;

    // ActivityLauncher to get image from gallery
    private ActivityResultLauncher<String> mGetContent;
    private Uri profileUri;
    private BottomNavigationView bottomNav;
    private Boolean ImagePresent=false;
    private Boolean ImageRemoved=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_edit_profile);

        // Refactored this to use Database class
        // No longer instantiates new FirebaseFirestore or FiresbaseFirestore object
        attendeesRef = database.getAttendeesRef();
        storageRef = database.getStorageRef();

        // Jeremy Logan, 2009, Stack Overflow, Bundle Args in intent
        // https://stackoverflow.com/questions/768969/passing-a-bundle-on-startactivity
        attendee = (Attendee) Objects.requireNonNull(getIntent().getExtras()).get("attendee");
        deviceID = attendee.getDeviceID();

        profileImage = findViewById(R.id.edit_profile_pic);
        uploadProfileImage = findViewById(R.id.edit_profile_upload_button);
        removeProfileImage = findViewById(R.id.edit_profile_remove_button);
        editProfileName = findViewById(R.id.edit_profile_name);
        editProfilePhone = findViewById(R.id.edit_profile_phone);
        editProfileEmail = findViewById(R.id.edit_profile_email);
        geoTrackingCheckBox = findViewById(R.id.geo_tracking_check);
        saveChanges = findViewById(R.id.edit_profile_save_changes);

        populateViews();


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

        BottomNavigationView bottomNavAdminDashboard = findViewById(R.id.attendee_bottom_nav);
        bottomNavAdminDashboard.setSelectedItemId(R.id.nav_profile);
        bottomNavAdminDashboard.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(EditProfileActivity.this, AttendeeDashboardActivity.class));
                    return true;
                } else if (id == R.id.nav_events) {
                    startActivity(new Intent(EditProfileActivity.this, BrowseAllEvents.class));
                    return true;
                } else if (id == R.id.nav_bell) {
                    startActivity(new Intent(EditProfileActivity.this, AttendeeNotifications.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    // Already on the AttendeeEditProfileActivity, no need to start a new instance
                    return true;
                }
                return false;
            }
        });
    }


    /**
     * This method adds listeners to Button views for user interactions.
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
                profileUri=null;
                //StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference imageRef = storageRef.child("images/" + deviceID + "/profile.png");
                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "Image deleted successfully");

                    }
                });
                int image_size=100;

                // Generate profile picture and set it to the ImageView
                String profileName = editProfileName.getText().toString(); // Example profile name
                Bitmap profilePicture = RandomImageGenerator.generateProfilePicture(profileName, image_size);
                profileImage.setImageBitmap(profilePicture);
                ImagePresent=false;
                ImageRemoved=true;
            }
        });
    }

    /**
     * This method retrieves the profile information of the current Attendee user and populates the corresponding views.
     */
    private void populateViews() {
        attendeesRef.document(deviceID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.get(NAME_KEY) != null ? String.valueOf(documentSnapshot.get(NAME_KEY)) : "";
                    String phone = documentSnapshot.get(PHONE_KEY) != null ? String.valueOf(documentSnapshot.get(PHONE_KEY)) : "";
                    String email = documentSnapshot.get(EMAIL_KEY) != null ? String.valueOf(documentSnapshot.get(EMAIL_KEY)) : "";
                    boolean geoTracking = Boolean.TRUE.equals(documentSnapshot.getBoolean(TRACKING));

                    editProfileName.setText(name);
                    editProfilePhone.setText(phone);
                    editProfileEmail.setText(email);
                    geoTrackingCheckBox.setChecked(geoTracking);
                }
            }
        });

        StorageReference imageRef = storageRef.child("images/" + deviceID + "/profile.png");

        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Successfully downloaded data to bytes array
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // Set the bitmap to ImageView
                profileImage.setImageBitmap(bitmap);
                ImagePresent=true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int image_size=100;

                // Generate profile picture and set it to the ImageView
                String profileName = editProfileName.getText().toString(); // Example profile name
                Bitmap profilePicture = RandomImageGenerator.generateProfilePicture(profileName, image_size);
                profileImage.setImageBitmap(profilePicture);
            }
        });
    }


    /**
     * This method updates the Attendee user's profile remotely.
     * It collects user input data, handles profile image uploading, and saves changes to the database.
     */
    private void updateProfile() {
        String name = editProfileName.getText().toString();
        String email = editProfileEmail.getText().toString();
        String phone = editProfilePhone.getText().toString();
        boolean geoTrackingEnabled = geoTrackingCheckBox.isChecked();
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
        data.put(TRACKING, geoTrackingEnabled);

        // OpenAI, 2024, ChatGPT, Upload Profile Pic as PNG
        // Handle the profile image
        StorageReference imageRef = storageRef.child("images/" + deviceID + "/profile.png");
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
                            SaveChanges(data);
                        }
                    });
                }
            });
        }
        else {
            if(ImageRemoved){
                data.put(IMAGE_KEY, "");
            }
            SaveChanges(data);
            if(!ImagePresent){
                int image_size=100;

                // Generate profile picture and set it to the ImageView
                //String profileName = editProfileName.getText().toString(); // Example profile name
                Bitmap profilePicture = RandomImageGenerator.generateProfilePicture(name, image_size);
                profileImage.setImageBitmap(profilePicture);
            }

        }
    }

    /**
     * This method saves the changes made to the Attendee user's profile data in the database.
     * It updates the document corresponding to the user's device ID with the new profile data.
     *
     * @param data A HashMap containing the updated profile data to be saved in the database.
     */
    private void SaveChanges(HashMap<String, Object> data){
//        attendee.setGeoTrackingEnabled(geoTrackingCheckBox.isChecked());

        attendeesRef.document(deviceID)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DEBUG", "Changes Saved");
                        Toast.makeText(EditProfileActivity.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * This method retrieves the PNG image data from the selected profile picture and converts it into a byte array.
     * It handles the conversion process and error handling.
     *
     * @return The PNG image data as a byte array, or null if there was an error.
     */
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
        } catch (NullPointerException e) {
            return null;
        }
    }
}
