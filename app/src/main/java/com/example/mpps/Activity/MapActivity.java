package com.example.mpps.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mpps.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;

public class MapActivity extends AppCompatActivity {

    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_map);

        map = findViewById(R.id.map);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(12.0);

        // Center the map on a default location (e.g., a city center)
        GeoPoint startPoint = new GeoPoint(54.6872, 25.2797); // Example coordinates
        mapController.setCenter(startPoint);

        // Add sample store locations
        addStoreMarker("Maxima", 54.6872, 25.2797);
        addStoreMarker("IKI", 54.6892, 25.2827);
    }

    private void addStoreMarker(String storeName, double lat, double lon) {
        GeoPoint storeLocation = new GeoPoint(lat, lon);
        Marker marker = new Marker(map);
        marker.setPosition(storeLocation);
        marker.setTitle(storeName);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setOnMarkerClickListener((clickedMarker, mapView) -> {
            // Handle marker click
            Intent returnIntent = new Intent();
            returnIntent.putExtra("storeName", storeName);
            returnIntent.putExtra("storeLat", lat);
            returnIntent.putExtra("storeLng", lon);
            setResult(RESULT_OK, returnIntent);
            finish();
            return true;
        });

        map.getOverlays().add(marker);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDetach();  // Properly release the map when done
    }
}
