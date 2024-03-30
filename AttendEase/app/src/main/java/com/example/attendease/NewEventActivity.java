package com.example.attendease;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class NewEventActivity extends AppCompatActivity {
    private String eventName;
    private String eventID;
    private Event newEvent;
    private String posterUrl;
    private Uri eventPosterUri = null;

    /**
     * Initializes the activity, setting the content view and configuring UI interactions.
     * This includes setting up date and time pickers, handling the creation of new events,
     * and navigating within the app through the bottom navigation view.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event);

        ImageButton buttonGoBack = findViewById(R.id.buttonGoBack);
        ImageView ivCoverPhoto = findViewById(R.id.ivCoverPhoto);
        Button btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        Button btnRemovePhoto = findViewById(R.id.btnRemovePhoto);

        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewEventActivity.this, OrganizerDashboardActivity.class);
                // Clears activity from the stack before returning to previous screen
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.tvEventDate).setOnClickListener(view -> showDatePickerDialog());
        findViewById(R.id.tvEventTime).setOnClickListener(view -> showTimePickerDialog());

        ActivityResultLauncher<String> getContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    ivCoverPhoto.setImageURI(uri);
                    eventPosterUri = uri;
                });

        btnUploadPhoto.setOnClickListener(v -> getContent.launch("image/*"));

        btnRemovePhoto.setOnClickListener(v -> {
            ivCoverPhoto.setImageResource(0); // Remove the image from the ImageView
            eventPosterUri = null; // Clear the image URI
        });

        Button btnGenerate = findViewById(R.id.btnGenerateEvent);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventPosterUri != null) {
                    uploadEventPoster(eventPosterUri, eventID, new UploadCallback() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            createEvent(imageUrl);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(NewEventActivity.this, "Failed to upload image, please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // No image to upload, proceed with event creation without an image
                    createEvent(null);
                }
            }
        });

    }

    /**
     * Displays a DatePickerDialog for selecting the event date.
     */

    void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    TextView tvEventDate = findViewById(R.id.tvEventDate);
                    tvEventDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    /**
     * Displays a TimePickerDialog for selecting the event time.
     */

    void showTimePickerDialog() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    TextView tvEventTime = findViewById(R.id.tvEventTime);
                    tvEventTime.setText(hourOfDay + ":" + minute);
                },
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true)
                .show();
    }

    /**
     * Captures user input from various UI elements, validates them, and creates a new event.
     * It then saves the event data to Firebase Firestore and handles QR code generation
     * and uploading to Firebase Storage.
     */

    private void createEvent(@Nullable String imageUrl) {
        // Capture data from EditTexts, CheckBoxes, etc.
        eventID = generateEventId();
        eventName = ((EditText) findViewById(R.id.etEventName)).getText().toString();
        String eventAbout = ((EditText) findViewById(R.id.etEventAbout)).getText().toString();
        String ownerID = getOwnerId();
        String eventDate = getEventDate();
        String eventTime = getEventTime();

        if (eventDate == null || eventTime == null) {
            Toast.makeText(this, "Invalid input. Events must have a date and time.", Toast.LENGTH_SHORT).show();
            return;
        }

        Timestamp dateTime = createTimestamp(eventDate, eventTime);
        String eventLocation = ((EditText) findViewById(R.id.etEventLocation)).getText().toString();

        if (imageUrl != null) {
            posterUrl = imageUrl;
        } else {
            posterUrl = "null";
        }

        boolean isGeoTrackingEnabled = ((CheckBox) findViewById(R.id.cbGeoTracking)).isChecked();
        int maxAttendees = getMaxAttendees();

        if (eventName.equals("") || eventLocation.equals("")) {
            Toast.makeText(this, "Invalid input. Events must have a name and location", Toast.LENGTH_SHORT).show();
            return;
        }

        newEvent = new Event(eventID, eventName, eventAbout, ownerID, dateTime, eventLocation, "promoQR_placeholder", "checkInQR_placeholder", posterUrl, isGeoTrackingEnabled, maxAttendees);

        // Save the new event to Firestore.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventID)
                .set(newEvent)
                .addOnSuccessListener(aVoid -> {
                    // Once the event is successfully added, generate and upload the QR code.
                    Bitmap qrCodeBitmap = generateCheckInQRCode(eventID);
                    if (qrCodeBitmap != null) {
                        writeQRtoDatabase(qrCodeBitmap, eventID);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to add event", e);
                    Toast.makeText(this, "Failed to create event", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Retrieves the device ID to be used as the owner ID for the event.
     * @return A String representing the Android device ID.
     */

    private String getOwnerId() {
        // Get the Android device ID
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * Generates a unique event data using UUID and the current system time.
     * @return A String representing the unique event date.
     */

    private String getEventDate() {
        TextView tvEventDate = findViewById(R.id.tvEventDate);
        String date = tvEventDate.getText().toString();
        if ("Select Date".equals(date)) {
            return null;
        }
        return date;
    }

    /**
     * Retrieves the selected event time from the user interface.
     * If the user has not selected a time, it returns null.
     *
     * @return A String representing the selected event time, or null if no time has been selected.
     */

    private String getEventTime() {
        TextView tvEventTime = findViewById(R.id.tvEventTime);
        String time = tvEventTime.getText().toString();
        if ("Select Time".equals(time)) {
            return null;
        }
        return time;
    }

    /**
     * Creates a Timestamp object based on the provided event date and time strings.
     * This method combines the date and time into a single Timestamp useful for Firebase operations.
     *
     * @param eventDate A String representing the event's date in "dd/MM/yyyy" format.
     * @param eventTime A String representing the event's time in "HH:mm" format.
     * @return A Timestamp representing the combined date and time.
     * @throws RuntimeException If parsing the date and time fails.
     */
    private Timestamp createTimestamp(String eventDate, String eventTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(eventDate + " " + eventTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // Converts the Date object into a Timestamp
        return new Timestamp(parsedDate);
    }

    /**
     * Retrieves the maximum number of attendees allowed for the event from the user interface.
     * If the input field is empty, it defaults to Integer.MAX_VALUE, indicating no limit.
     *
     * @return An Integer representing the maximum number of attendees, or Integer.MAX_VALUE if no limit is set.
     */
    private Integer getMaxAttendees() {
        EditText etSignUpLimit = findViewById(R.id.etSignUpLimit);
        String maxAttendeesStr = etSignUpLimit.getText().toString();
        // Check if the EditText is empty or not
        if (maxAttendeesStr.isEmpty()) {
            return Integer.MAX_VALUE;
        } else {
            try {
                return Integer.parseInt(maxAttendeesStr);
            } catch (NumberFormatException e) {
                return Integer.MAX_VALUE;
            }
        }
    }

    /**
     * Generates a unique identifier for a new event using a combination of UUID and the current system time.
     *
     * @return A String representing the unique event ID.
     */
    private String generateEventId() {
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
    }

    /**
     * Generates a QR code bitmap for event check-in based on the provided event ID.
     * This QR code can be scanned by attendees to check into the event.
     *
     * @param eventId The event ID for which to generate the QR code.
     * @return A Bitmap of the generated QR code, or null if generation fails.
     */
    private Bitmap generateCheckInQRCode(String eventId) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            // QR code width and height
            int width = 500;
            int height = 500;

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            // Create a QR Code from the eventId
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(eventId, BarcodeFormat.QR_CODE, width, height, hints);

            // Create a Bitmap from the BitMatrix
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            // Convert the BitMatrix to a Bitmap
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Uploads the generated QR code bitmap to Firebase Storage and updates the corresponding event document in Firestore with the QR code URL.
     *
     * @param bitmap The QR code bitmap to upload.
     * @param eventId The event ID associated with the QR code.
     */
    private void writeQRtoDatabase(Bitmap bitmap, String eventId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference qrCodeRef = storageRef.child("qr_codes/" + eventId + "/check_in_qr.png");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = qrCodeRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String qrUrl = uri.toString();
                    updateFirestoreWithQrUrl(qrUrl, eventId); // Update Firestore with QR URL
                }))
                .addOnFailureListener(e -> Log.e("ERROR", "Uploading QR code failed", e));
    }

    /**
     * Updates the Firestore event document with the URL of the uploaded QR code.
     *
     * @param qrUrl The URL of the uploaded QR code image.
     * @param eventId The event ID associated with the QR code.
     */
    private void updateFirestoreWithQrUrl(String qrUrl, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        newEvent.setCheckInQR(qrUrl);
        // Query the collection for the document with the matching eventId
        db.collection("events")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the document reference
                        DocumentReference eventDocRef = queryDocumentSnapshots.getDocuments().get(0).getReference();

                        // Update the document with the QR code URL.
                        eventDocRef.update("checkInQR", qrUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("DEBUG", "Document successfully updated with QR URL");
                                    fetchAndShowQRCode(eventId);
                                })
                                .addOnFailureListener(e -> Log.e("ERROR", "Error updating document with QR URL", e));
                    } else {
                        // Handles the case where no matching document is found.
                        Log.e("ERROR", "No matching document found for eventId: " + eventId);
                    }
                })
                .addOnFailureListener(e -> Log.e("ERROR", "Error querying for event document", e));
    }


    /**
     * Navigates back to the details of the event, clearing the current activity from the stack.
     */
    private void navigateToDashboard() {
        Intent intent = new Intent(NewEventActivity.this, EventDetailsOrganizer.class);
        intent.putExtra("event", newEvent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Fetches the URL for the event's check-in QR code from Firebase Storage and displays it.
     *
     * @param eventId The event ID for which to fetch and show the QR code.
     */
    private void fetchAndShowQRCode(String eventId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference qrCodeRef = storageRef.child("qr_codes/" + eventId + "/check_in_qr.png");

        qrCodeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Uri contains the URL to the QR code image
                downloadAndDisplayQR(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FirebaseStorage", "Failed to load QR code", e);
                Toast.makeText(NewEventActivity.this, "Failed to load QR code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Downloads the QR code image from a URL and displays it in a dialog along with the event name.
     *
     * @param url The URL from which to download the QR code image.
     */
    private void downloadAndDisplayQR(String url) {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        showQRCodeDialog(resource, eventName);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Toast.makeText(NewEventActivity.this, "Failed to download QR code", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Displays a dialog containing the QR code image and the event name.
     * Provides an option to navigate back to the dashboard.
     *
     * @param bitmap The QR code image to display.
     * @param eventName The name of the event associated with the QR code.
     * @throws IllegalArgumentException If the eventName is null.
     * @throws IllegalStateException If the TextView for the event name is not found in the layout.
     */
    private void showQRCodeDialog(Bitmap bitmap, String eventName) {
        // Ensure eventName is not null
        if (eventName == null) {
            throw new IllegalArgumentException("eventName must not be null");
        }

        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.qr_code_dialog, null);

        // Find the TextView in the dialog layout and set its text to the event name
        TextView textViewEventName = view.findViewById(R.id.textView5);
        if (textViewEventName != null) {
            textViewEventName.setText(eventName); // Set the text to the event name
        } else {
            throw new IllegalStateException("TextView not found in the layout");
        }

        // Set the QR code bitmap to the ImageView
        ImageView imageViewQRCode = view.findViewById(R.id.imageView2);
        imageViewQRCode.setImageBitmap(bitmap);

        // Find the button in the dialog layout
        Button viewEventDetailsButton = view.findViewById(R.id.button);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        Dialog dialog = builder.create();

        // Make dialog non-cancelable
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        viewEventDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog and navigate to my events
                dialog.dismiss();
                navigateToDashboard();
            }
        });

        dialog.show();
    }

    interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }

    private void uploadEventPoster(Uri posterUri, String eventID, UploadCallback callback) {
        StorageReference posterRef = FirebaseStorage.getInstance().getReference("images/" + eventID + "/eventposter.png");

        posterRef.putFile(posterUri).addOnSuccessListener(taskSnapshot -> posterRef.getDownloadUrl().addOnSuccessListener(uri -> {
            posterUrl = uri.toString();
            callback.onSuccess(posterUrl);
        })).addOnFailureListener(e -> callback.onFailure(e));
    }
}
