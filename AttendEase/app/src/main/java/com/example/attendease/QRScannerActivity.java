package com.example.attendease;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.Collections;

public class QRScannerActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private String deviceID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        Intent intent = getIntent();
        deviceID = intent.getStringExtra("deviceID");


        // OpenAI, 2024, ChatGPT, ScanQR code using zxing IntentIntegrator

        // Check if user permission for camera access
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startQRScanner();
        }
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Get the result of the initiateScan ActivityLauncher
        // Return scanned result if not null
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedData = result.getContents();
                landOnEventDetails(scannedData);
                // Handle the scanned QR code data as needed
                // Toast.makeText(this, "Scanned: " + scannedData, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

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

                                Intent intent = new Intent(QRScannerActivity.this, EventDetails.class);
                                intent.putExtra("deviceID", deviceID);
                                intent.putExtra("eventID", eventID);
                                intent.putExtra("title",title);
                                intent.putExtra("description",description);
                                intent.putExtra("dateTime",dateTime.toDate().toString());
                                intent.putExtra("location",location);
                                intent.putExtra("canCheckIn", true);
                                startActivity(intent);
                            }
                        }
                    }
                });
    }
}
