package com.example.attendease;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class NewEventActivity extends AppCompatActivity {
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
                boolean eventCreated = createEvent();
                if (eventCreated) {
                    Intent intent = new Intent(NewEventActivity.this, OrganizerDashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish(); // Only finish if event creation was successful
                }
                // If eventCreated is false, do nothing, therefore staying on the current screen
            }
        });

        // TODO add onClick to upload and remove images for event poster

    }

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

    private boolean createEvent() {
        // Capture data from EditTexts, CheckBoxes, etc.
        String eventID = generateEventId();
        String eventName = ((EditText) findViewById(R.id.etEventName)).getText().toString();
        String eventAbout = ((EditText) findViewById(R.id.etEventAbout)).getText().toString();
        String ownerID = getOwnerId();
        String eventDate = getEventDate();
        String eventTime = getEventTime();

        if (eventDate == null || eventTime == null) {
            Toast.makeText(this, "Invalid input. Events must have a date and time.", Toast.LENGTH_SHORT).show();
            return false;
        }

        Timestamp dateTime = createTimestamp(eventDate, eventTime);
        String eventLocation = ((EditText) findViewById(R.id.etEventLocation)).getText().toString();
        // TODO generate promoQR
        String promoQR = "null";
        // TODO generate checkInQR
        String checkInQR = generateCheckInQRCode(eventID);
        // TODO add image upload functionality to the upload image button in onCreate and then fetch the image here
        String posterUrl = "null";
        boolean isGeoTrackingEnabled = ((CheckBox) findViewById(R.id.cbGeoTracking)).isChecked();
        int maxAttendees = getMaxAttendees();

        if (eventName.equals("") || eventLocation.equals("")) {
            Toast.makeText(this, "Invalid input. Events must have a name and location", Toast.LENGTH_SHORT).show();
            return false;
        }

        Event newEvent = new Event(eventID, eventName, eventAbout, ownerID, dateTime, eventLocation, promoQR, checkInQR, posterUrl, isGeoTrackingEnabled, maxAttendees);

        // Save to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").add(newEvent);
        return true;
    }

    private String getOwnerId() {
        // Get the Android device ID
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    private String getEventDate() {
        TextView tvEventDate = findViewById(R.id.tvEventDate);
        String date = tvEventDate.getText().toString();
        if ("Select Date".equals(date)) {
            return null;
        }
        return date;
    }

    private String getEventTime() {
        TextView tvEventTime = findViewById(R.id.tvEventTime);
        String time = tvEventTime.getText().toString();
        if ("Select Time".equals(time)) {
            return null;
        }
        return time;
    }

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

    private String generateEventId() {
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
    }

    // TODO add functions to upload and remove images for event poster

    // TODO add funtions to generate QR codes
    private String generateCheckInQRCode(String data) {
        // Set dimensions for the QR Code
        int width = 500;
        int height = 500;

        // Encode the string into a QR Code
        try {
            Bitmap bitmap = endcodeAsBitmap(data, width, height);
            writeQRtoDatabase(bitmap, data);
            return null;
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not generate QR Code. Please try again.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

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

    private void writeQRtoDatabase(Bitmap bitmap, String data) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("qr_codes/" + data + "/check_in_qr.png");
        String downloadUri;

        byte[] pngImageData = getImagePng(bitmap);

        if (pngImageData != null) {
            // Does Storage transaction asynchronously
            imageRef.putBytes(pngImageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            HashMap<String, Object> imageUri = new HashMap<>();
                            imageUri.put("checkInQR", uri.toString());
                            Query query = db.collection("events").whereEqualTo("eventId", data);
                            query.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Get the document reference
                                        DocumentReference eventDocRef = document.getReference();

                                        // Update the document with the imageUri
                                        eventDocRef.update(imageUri)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Document successfully updated
                                                    Log.d("DEBUG", "Document updated successfully");
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle failure
                                                    Log.e("ERROR", "Error updating document", e);
                                                });
                                    }
                                } else {
                                    // Handle unsuccessful query
                                    Log.e("ERROR", "Error getting documents: ", task.getException());
                                }
                            });
                            Log.d("DEBUG", "onSuccess: image url should show up in doc");
                        }
                    });
                }
            });
        }
    }

    public byte[] getImagePng(Bitmap bitmap) {
        // Create a ByteArrayOutputStream to hold the PNG image data
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Compress the Bitmap into PNG format with 100% quality
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        // Convert the ByteArrayOutputStream to byte array
        return outputStream.toByteArray();
    }


    private void showQRCodeDialog(Bitmap bitmap) {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        // View view = inflater.inflate(R.layout.dialog_new_event_qr_code, null);

        // Set the QR code bitmap to the ImageView
        // ImageView imageViewQRCode = view.findViewById(R.id.new_event_qr_code);
        // imageViewQRCode.setImageBitmap(bitmap);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();
    }


}
