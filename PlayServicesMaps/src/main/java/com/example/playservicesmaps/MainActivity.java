package com.example.playservicesmaps;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseSpiceActivity {
    private SearchRequest mSearchRequest;
    private HashMap<Marker, Item> mMarkerMap;
    private GoogleMap mMap;

    private static HashMap<Marker, Item> addMarkers(GoogleMap map, List<Item> results) {
        HashMap<Marker, Item> markerMap = new HashMap<Marker, Item>();
        for (Item item : results) {
            Location l = item.getLocation();
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(l.getLatitude(), l.getLongitude())));

            markerMap.put(marker, item);
        }

        return markerMap;
    }


    private static LatLngBounds calculateMapBounds(HashMap<Marker, Item> markerMap) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Item item : markerMap.values()) {
            Location l = item.getLocation();
            builder.include(new LatLng(l.getLatitude(), l.getLongitude()));
        }

        return builder.build();
    }

    private void setupMap() {
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getView().setVisibility(View.VISIBLE);

        mMap = fragment.getMap();
        mMap.setOnInfoWindowClickListener(new ItemsInfoWindowClickListener());
        mMap.setInfoWindowAdapter(new ItemsInfoWindowAdapter());
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
            setupMap();
        }
    }

    private class SearchRequestListener implements RequestListener<SearchResult> {

        @Override
        public void onRequestSuccess(final SearchResult result) {
            mMarkerMap = addMarkers(mMap, result.filterResultsWithLocation());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(calculateMapBounds(mMarkerMap), 50);
            mMap.animateCamera(cameraUpdate);
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(MainActivity.this, "failure", Toast.LENGTH_SHORT).show();
        }
    }

    private class ItemsInfoWindowClickListener implements GoogleMap.OnInfoWindowClickListener {

        @Override
        public void onInfoWindowClick(Marker marker) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(marker.getPosition())
                    .zoom(18)
                    .bearing(45)
                    .tilt(45)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.setOnCameraChangeListener(new RotationListener(marker.getPosition()));
        }
    }

    private class ItemsInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private void setupLayout(ViewGroup view, Marker marker) {
            Item item = mMarkerMap.get(marker);

            // address.
            TextView addressText = (TextView) view.findViewById(R.id.address_line);
            addressText.setText(item.getLocation().getAddressLine());

            // location.
            TextView locationText = (TextView) view.findViewById(R.id.location);
            locationText.setText(item.getLocation().toString());

            // title.
            TextView titleText = (TextView) view.findViewById(R.id.title);
            titleText.setText(item.getTitle());

            // price.
            TextView priceText = (TextView) view.findViewById(R.id.price);
            priceText.setText(item.getPrice());

            // phone.
            ViewGroup phoneContainer = (ViewGroup) view.findViewById(R.id.phone_container);
            if (item.getAddress().getPhone() != null) {
                phoneContainer.setVisibility(View.VISIBLE);
                TextView phoneText = (TextView) view.findViewById(R.id.phone);
                phoneText.setText(item.getAddress().getPhone());
            } else {
                phoneContainer.setVisibility(View.GONE);
            }
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View view =  getLayoutInflater().inflate(R.layout.marker_view, null);
            setupLayout((ViewGroup) view, marker);
            return view;
        }
    }

    private class RotationListener implements GoogleMap.OnCameraChangeListener {
        private static final int BEARING_INCREMENT = 90;
        private LatLng mPosition;
        private int mBearing = BEARING_INCREMENT;

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
                    .tilt(45)
                    .build();
            mBearing += BEARING_INCREMENT;
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(p), 5000, new RotationCancellableCallback());
        }

        private class RotationCancellableCallback implements GoogleMap.CancelableCallback {
            @Override
            public void onFinish() {}

            @Override
            public void onCancel() {
                mMap.setOnCameraChangeListener(null);
            }
        }
    }
}
