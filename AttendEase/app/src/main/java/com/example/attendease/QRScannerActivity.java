package com.example.attendease;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Collections;
import java.util.Objects;

/**
 * This class represents the QR Code Scanner screen that the user
 * must use to check into an event
 */
public class QRScannerActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private final Database database = Database.getInstance();
    private CollectionReference eventsRef;
    private Attendee attendee;
    private String prevActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventsRef = database.getEventsRef();

        Intent intent = getIntent();
        prevActivity = intent.getStringExtra("prevActivity");
        if (Objects.equals(prevActivity, "AttendeeDashboardActivity")) {
            attendee = (Attendee) Objects.requireNonNull(intent.getExtras()).getSerializable("attendee");
        }

        // OpenAI, 2024, ChatGPT, ScanQR code using zxing IntentIntegrator

        // Check if user permission for camera access
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startQRScanner();
        }
    }

    /**
     * Starts the QR Code scanner capture activity from the IntentIntegrator
     */
    public void startQRScanner() {
        // OpenAI, 2024, ChatGPT, Initiate scan for QR Codes in Portrait mode
        // Using zxing library for IntentIntegrator
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(true); // Lock orientation to portrait
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0);  // Use the rear camera
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortraitMode.class); // Use the default capture activity
        integrator.initiateScan(Collections.singletonList("QR_CODE")); // Specify QR_CODE format
    }

    /**
     * Gets the eventID (currently the attribute not the docID) of an event after scanning the QR code
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Get the result of the initiateScan ActivityLauncher
        // Return scanned result if not null
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedData = result.getContents().replaceAll("[./#$\\[\\]]", "_");
                checkEventExists(scannedData);
                // Handle the scanned QR code data as needed
                // Toast.makeText(this, "Scanned: " + scannedData, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Requests camera permission from the user
     * @param requestCode The request code passed in
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Request camera permission to initiate scan activity
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Start the Scanner once permission is given
                startQRScanner();
            } else {
                Toast.makeText(this, "Camera permission required to scan QR code", Toast.LENGTH_SHORT).show();
                finish();  // Close activity if user doesn't want to scan. what's the point
            }
        }
    }


    /**
     * Helper function that looks at the event details page of the scanned qr code
     * @param docID document ID of the scanned qr code
     */
    private void landOnEventDetails(String docID) {
        // Scanned data should be the eventID
        eventsRef.whereEqualTo("eventId", docID)
                .limit(1) // Limit the number of documents
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // Loop through the result set if needed
                                Log.d("DEBUG", String.format("onComplete: %s", docID));
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String title = document.getString("title");
                                String description = document.getString("description");
                                String location = document.getString("location");
                                String eventID = document.getId();
                                Timestamp dateTime = document.getTimestamp("dateTime");
                                String QR = document.getString("checkInQR");
                                String posterUrl = document.getString("posterUrl");

                                Intent intent = new Intent(QRScannerActivity.this, EventDetailsAttendee.class);
                                intent.putExtra("attendee", attendee);
                                intent.putExtra("eventID", eventID);
                                intent.putExtra("title",title);
                                intent.putExtra("description",description);
                                intent.putExtra("dateTime",dateTime.toDate().toString());
                                intent.putExtra("location",location);
                                intent.putExtra("QR",QR);
                                intent.putExtra("posterUrl",posterUrl);
                                intent.putExtra("prevActivity", "QRScannerActivity");
                                startActivity(intent);
                            }
                        }
                    }
                });
    }

    /**
     * Checks if the event exists in the Firestore database.
     * If it exists, it redirects the user to the event details page.
     * If not, it either displays a message or returns the event ID as a result.
     * @param eventID The ID of the event scanned from the QR code.
     */
    private void checkEventExists(String eventID) {
        eventsRef.document(eventID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if (Objects.equals(prevActivity, "AttendeeDashboardActivity")) {
                                    landOnEventDetails(eventID);
                                    Log.d("DEBUG", "onComplete: not an event");

                                    finish();
                                } else {
                                    Toast.makeText(QRScannerActivity.this, "QR Code already in use", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
                                if (Objects.equals(prevActivity, "AttendeeDashboardActivity")) {
                                    Log.d("DEBUG", "onComplete: not an event");
                                    Toast.makeText(getApplicationContext(), "Not an event", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("result", eventID);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                }
                            }
                        }
                    }
                });
    }
}
