package com.example.playservicesmaps;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {
    static final LatLng HAMBURG = new LatLng(-34.608280, -58.370244);
    static final LatLng KIEL = new LatLng(53.551, 9.993);

    private GoogleMap mMap;

    private void setupGoogleMap() {
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getView().setVisibility(View.VISIBLE);

        mMap = fragment.getMap();

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return getLayoutInflater().inflate(R.layout.marker_view, null);
            }
        });

        mMap.addMarker(new MarkerOptions().position(HAMBURG)
                .title("Hamburg")).showInfoWindow();
        mMap.addMarker(new MarkerOptions()
                .position(KIEL)
                .title("Kiel")
                .snippet("Kiel is cool")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));



//        // Move the camera instantly to hamburg with a zoom of 15.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 10));
//
//        // Zoom in, animating the camera.
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        LatLng SYDNEY = new LatLng(-33.88,151.21);
        LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 14));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 3000, null);
        mMap.setOnCameraChangeListener(new RotationListener());

        /*
        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(MOUNTAIN_VIEW)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
    }

    private class RotationListener implements GoogleMap.OnCameraChangeListener {
        private int bearing = 45;

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            CameraPosition p = new CameraPosition.Builder()
                    .target(HAMBURG)
                    .zoom(18)
                    .bearing(bearing)
                    .tilt(90)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            bearing += 45;
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(p), 5000, null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getView().setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (statusCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, this, 0);
                dialog.setCancelable(false);
                dialog.show();
            } else {
                // we should do some checks here to determine a resolution or alert user about if
                // whether will be able to user the app or not.
            }
        } else if (mMap == null) {
            setupGoogleMap();
        }
    }
}
