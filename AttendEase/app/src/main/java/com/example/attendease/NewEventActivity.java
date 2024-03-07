package com.example.attendease;

import static com.example.attendease.R.id.organizer_bottom_nav;

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

/**
 * Represents activity for creating a new event and generating QR codes for check-in.
 */
public class NewEventActivity extends AppCompatActivity {
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event);

        ImageButton buttonGoBack = findViewById(R.id.buttonGoBack);
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

        Button btnGenerate = findViewById(R.id.btnGenerateEvent);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.organizer_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, OrganizerDashboardActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_events) {
                Intent intent = new Intent(this, OrganizerMyEventsActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
        // TODO add onClick to upload and remove images for event poster

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
     * Creates a new event based on user input and saves it to Firestore.
     * Generates QR codes for check-in and promo, and uploads them to Firebase Storage.
     */
    private void createEvent() {
        // Capture data from EditTexts, CheckBoxes, etc.
        String eventID = generateEventId();
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

        // TODO add image upload functionality to the upload image button in onCreate and then fetch the image here
        String posterUrl = "null";

        boolean isGeoTrackingEnabled = ((CheckBox) findViewById(R.id.cbGeoTracking)).isChecked();
        int maxAttendees = getMaxAttendees();

        if (eventName.equals("") || eventLocation.equals("")) {
            Toast.makeText(this, "Invalid input. Events must have a name and location", Toast.LENGTH_SHORT).show();
            return;
        }

        Event newEvent = new Event(eventID, eventName, eventAbout, ownerID, dateTime, eventLocation, "promoQR_placeholder", "checkInQR_placeholder", posterUrl, isGeoTrackingEnabled, maxAttendees);

        // Save the new event to Firestore.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventID)
                .set(newEvent)
                .addOnSuccessListener(aVoid -> {
                    // Once the event is successfully added, generate and upload the QR code.
                    Bitmap qrCodeBitmap = generateCheckInQRCode(eventID);
                    // TODO generate promoQR
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
     * Retrieves the Android device ID to use as the event owner ID.
     * @return The device ID as a String.
     */
    private String getOwnerId() {
        // Get the Android device ID
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * Retrieves the selected event date from the TextView.
     * @return The selected event date as a String.
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
     * Retrieves the selected event time from the TextView.
     * @return The selected event time as a String.
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
     * Creates a Timestamp object from the selected event date and time.
     * @param eventDate The selected event date.
     * @param eventTime The selected event time.
     * @return The Timestamp object representing the combined date and time.
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
     * Retrieves the maximum number of attendees from the EditText.
     * @return The maximum number of attendees as an Integer.
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
     * Generates a unique event ID.
     * @return The generated event ID as a String.
     */
    private String generateEventId() {
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
    }

    /**
     * Generates a QR code bitmap for check-in using the ZXing library.
     * @param eventId The unique identifier for the event.
     * @return The generated QR code bitmap.
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
     * Writes the generated QR code bitmap to Firebase Storage.
     * @param bitmap  The QR code bitmap to be uploaded.
     * @param eventId The unique identifier for the event.
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
     * Updates the Firestore document with the QR code URL.
     * @param qrUrl   The URL of the uploaded QR code.
     * @param eventId The unique identifier for the event.
     */
    private void updateFirestoreWithQrUrl(String qrUrl, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    private void navigateToDashboard() {
        Intent intent = new Intent(NewEventActivity.this, OrganizerDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Fetches and displays the check-in QR code from Firebase Storage.
     * @param eventId The unique identifier for the event.
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
     * Downloads and displays the check-in QR code using the Glide library.
     * @param url The URL of the check-in QR code in Firebase Storage as a string.
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
     * Shows a dialog containing the event name and check-in QR code.
     * @param bitmap   The check-in QR code bitmap.
     * @param eventName The name of the event.
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

    /**
     * Encodes data as a QR code bitmap using the ZXing library.
     * @param data   The data to be encoded as a QR code.
     * @param width  The width of the QR code bitmap.
     * @param height The height of the QR code bitmap.
     * @return The generated QR code bitmap.
     */
    private Bitmap endcodeAsBitmap(String data, int width, int height) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
            }
        }

        return bitmap;
    }

    /**
     * Converts a Bitmap into a byte array in PNG format.
     * @param bitmap The Bitmap to be converted.
     * @return The byte array representing the PNG image.
     */
    public byte[] getImagePng(Bitmap bitmap) {
        // Create a ByteArrayOutputStream to hold the PNG image data
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Compress the Bitmap into PNG format with 100% quality
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        // Convert the ByteArrayOutputStream to byte array
        return outputStream.toByteArray();
    }
// TODO add functions to upload and remove images for event poster

// TODO add functions to generate QR codes
}