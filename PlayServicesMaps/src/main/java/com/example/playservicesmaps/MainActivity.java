package com.example.playservicesmaps;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.playservicesmaps.dto.Item;
import com.example.playservicesmaps.dto.Location;
import com.example.playservicesmaps.dto.SearchResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseSpiceActivity implements GoogleMap.OnInfoWindowClickListener {
    private SearchRequest mSearchRequest;
    private GoogleMap mMap;

    private void setupGoogleMap() {
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getView().setVisibility(View.VISIBLE);

        mMap = fragment.getMap();
        mMap.setOnInfoWindowClickListener(this);
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
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // Zoom in, animating the camera.
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 3000, null);
//        mMap.setOnCameraChangeListener(new RotationListener(marker.getPosition()));

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(marker.getPosition())       // Sets the center of the map to Mountain View
                .zoom(18)                           // Sets the zoom
                .bearing(45)                        // Sets the orientation of the camera to east
                .tilt(45)                           // Sets the tilt of the camera to 30 degrees
                .build();                           // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private class RotationListener implements GoogleMap.OnCameraChangeListener {
        private LatLng mPosition;
        private int mBearing = 45;

        public RotationListener(LatLng position) {
            super();
            mPosition = position;
        }

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            CameraPosition p = new CameraPosition.Builder()
                    .target(mPosition)
                    .zoom(18)
                    .bearing(mBearing)
                    .tilt(90)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mBearing += 45;
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(p), 5000, null);
        }
    }

    private void createMarkers(List<Item> results) {
        List<LatLng> positions = new ArrayList<LatLng>();
        for (Item item : results) {
            Location loc = item.getLocation();
            LatLng position = new LatLng(loc.getLatitude(), loc.getLongitude());
            positions.add(position);
            mMap.addMarker(new MarkerOptions().position(position)
                    .title(loc.toString()));
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(getMapBounds(positions), 50);
        mMap.animateCamera(cameraUpdate);
    }


    private LatLngBounds getMapBounds(List<LatLng> positions) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng position : positions) {
            builder.include(position);
        }

        return builder.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getView().setVisibility(View.GONE);

        mSearchRequest = new SearchRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSpiceManager().execute(mSearchRequest, "results", DurationInMillis.ONE_MINUTE, new SearchRequestListener());
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

    public final class SearchRequestListener implements RequestListener<SearchResult> {

        @Override
        public void onRequestSuccess(final SearchResult result) {
            List<Item> filteredResults = result.getResultsWithLocation();
            createMarkers(filteredResults);
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(MainActivity.this, "failure", Toast.LENGTH_SHORT).show();
        }
    }
}
