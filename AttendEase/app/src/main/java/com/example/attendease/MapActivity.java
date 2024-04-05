package com.example.attendease;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This activity displays a map with markers representing check-in locations for a specific event.
 * It retrieves check-in locations from Firestore, converts them to GeoPoints, and adds markers to the map accordingly.
 */
public class MapActivity extends AppCompatActivity {
    private final Database database = Database.getInstance();
    public CollectionReference checkInsRef;
    public CollectionReference attendeesRef;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private IMapController controller;
    private MyLocationNewOverlay locationOverlay;
    private Event event;
    private ArrayList<Marker> markerList = new ArrayList<>();

    // OpenAI, ChatGPT, Refactor code (check previous commit)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeComponents();
        populateMapWithMarkers(checkInsRef, attendeesRef);
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void initializeComponents() {
        event = getIntent().getParcelableExtra("event");
        checkInsRef = database.getCheckInsRef();
        attendeesRef = database.getAttendeesRef();

        initializeMap();
        initializeLocationOverlay();
    }

    public void initializeMap() {
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_map);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);
        map.getLocalVisibleRect(new Rect());
        map.setMaxZoomLevel(20.0);
        map.setMinZoomLevel(3.0);

        GeoPoint myLocationGeoPoint = new GeoPoint(0, 0);
        controller = map.getController();
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);

        locationOverlay.enableMyLocation();
        locationOverlay.setDrawAccuracyEnabled(false);

        controller.setCenter(myLocationGeoPoint);
        controller.animateTo(myLocationGeoPoint);
        controller.setZoom(1);

        map.getOverlays().add(locationOverlay);
    }

    public void initializeLocationOverlay() {
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        locationOverlay.enableMyLocation();
        locationOverlay.setDrawAccuracyEnabled(false);
        map.getOverlays().add(locationOverlay);
    }

    /**
     * Populates the map with markers representing check-in locations.
     */
    // OpenAI, ChatGPT, 2024, Refactor populateMapWithMarkers for better mock testing
    public void populateMapWithMarkers(CollectionReference checkInsRef, CollectionReference attendeesRef) {
        checkInsRef
                .whereEqualTo("eventID", event.getEventId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            processCheckInDocuments(attendeesRef, task.getResult());
                        }
                    }
                });
    }

    public void processCheckInDocuments(CollectionReference attendeesRef, QuerySnapshot querySnapshot) {
        for (QueryDocumentSnapshot document : querySnapshot) {
            GeoPoint geoPoint = convertFirebaseGeoPoint(document.getGeoPoint("geoPoint"));
            if (geoPoint != null) {
                Marker marker = createMarker(geoPoint);
                map.getOverlays().add(marker);
                fetchAttendeeName(attendeesRef, document.getString("attendeeID"), marker);
            }
        }
    }

    public Marker createMarker(GeoPoint geoPoint) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        return marker;
    }

    public void fetchAttendeeName(CollectionReference attendees, String attendeeID, Marker marker) {
        attendees.document(attendeeID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            marker.setTitle(documentSnapshot.getString("name"));
                            Log.d("DEBUG", "onSuccess: marker.setTitle() is called");
                        }
                    }
                });
    }

    public GeoPoint convertFirebaseGeoPoint(com.google.firebase.firestore.GeoPoint firebaseGeoPoint) {
        if (firebaseGeoPoint != null) {
            double latitude = firebaseGeoPoint.getLatitude();
            double longitude = firebaseGeoPoint.getLongitude();
            return new org.osmdroid.util.GeoPoint(latitude, longitude);
        } else {
            return null; // Or handle null case as needed
        }
    }
}
