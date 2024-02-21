package com.example.attendease;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Objects;


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

    // View declarations
    private Button uploadProfileImage;
    private Button removeProfileImage;
    private EditText editProfileName;
    private EditText editProfilePhone;
    private EditText editProfileEmail;
    private Button saveChanges;

    // Attendee User declaration
    private Attendee user;


    // Jeremy Logan, 2024, Stack Overflow, Bundle Args in intent
    // https://stackoverflow.com/questions/768969/passing-a-bundle-on-startactivity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_edit_profile);

        db = FirebaseFirestore.getInstance();
        attendeesRef = db.collection(ATTENDEE_COLLECTION);

        user = (Attendee) Objects.requireNonNull(getIntent().getExtras()).get("user");

        uploadProfileImage = findViewById(R.id.edit_profile_upload_button);
        removeProfileImage = findViewById(R.id.edit_profile_remove_button);
        editProfileName = findViewById(R.id.edit_profile_name);
        editProfilePhone = findViewById(R.id.edit_profile_phone);
        editProfileEmail = findViewById(R.id.edit_profile_email);
        saveChanges = findViewById(R.id.edit_profile_save_changes);

        addListeners();
    }


    private void addListeners(){
        saveChanges.setOnClickListener(v -> {
            updateProfile();
        });
    }

    private void updateProfile() {
        String name = editProfileName.getText().toString();
        String email = editProfileEmail.getText().toString();
        String phone = editProfilePhone.getText().toString();
        Log.d("DEBUG", String.format("%s %s %s", name, email, phone));

        if (name.equals("") || email.equals("") || phone.equals("")) {
            Toast.makeText(this, "Invalid input. Please check your entries and try again", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put(NAME_KEY, name);
        data.put(EMAIL_KEY, email);
        data.put(PHONE_KEY, phone);

        attendeesRef.document(user.getDeviceID())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DEBUG", "Changes Saved");
                    }
                });
    }
}
