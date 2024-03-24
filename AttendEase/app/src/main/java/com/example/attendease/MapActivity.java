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
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
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

public class MapActivity extends AppCompatActivity {
    private final Database database = Database.getInstance();
    private CollectionReference checkInsRef;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private IMapController controller;
    private MyLocationNewOverlay locationOverlay;
    private Event event;
    private ArrayList<Marker> markerList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        event = getIntent().getParcelableExtra("event");
        event = new Event();
        event.setEventId("0d7276f3-0353-4f3a-b05b-8de77a068f1c_1711067197366");
        checkInsRef = database.getCheckInsRef();

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //inflate and create the map
        setContentView(R.layout.activity_map);

        // Code borrowed from QRScannerActivity Re-purposed for location access
        // Check if user permission for camera access
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }

        // Create the map view
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);
        map.getLocalVisibleRect(new Rect());
        map.setMaxZoomLevel(20.0);
        map.setMinZoomLevel(3.0);


        GeoPoint myLocationGeoPoint = new GeoPoint(0, 0);
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        controller = map.getController();

        locationOverlay.enableMyLocation();
        locationOverlay.setDrawAccuracyEnabled(false);

        controller.setCenter(myLocationGeoPoint);
        controller.animateTo(myLocationGeoPoint);
        controller.setZoom(18);

        map.getOverlays().add(locationOverlay);
        populateMapWithMarkers();

    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }


    private void populateMapWithMarkers() {
        checkInsRef
                .whereEqualTo("eventID", event.getEventId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GeoPoint geoPoint = convertFirebaseGeoPoint(document.getGeoPoint("geoPoint"));
                                if (geoPoint != null) {
                                    Marker marker = new Marker(map);
                                    marker.setPosition(geoPoint);
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    map.getOverlays().add(marker);
                                }
                            }
                        }
                    }
                });
    }

    private static GeoPoint convertFirebaseGeoPoint(com.google.firebase.firestore.GeoPoint firebaseGeoPoint) {
        if (firebaseGeoPoint != null) {
            double latitude = firebaseGeoPoint.getLatitude();
            double longitude = firebaseGeoPoint.getLongitude();
            return new org.osmdroid.util.GeoPoint(latitude, longitude);
        } else {
            return null; // Or handle null case as needed
        }
    }
}
