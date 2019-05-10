package com.nicolappli.go4lunch.Controllers.Fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.nicolappli.go4lunch.R;
import com.nicolappli.go4lunch.Utils.RefreshEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback {


    private static final String TAG = "MapFragment";

    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final float DEFAULT_ZOOM = 17f;

    //widgets
    RelativeLayout mRelativeLayoutGps;

    //vars
    public Boolean mLocationPermissionGranted = false;
    public GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SupportMapFragment mapFragment;

    public MapViewFragment() {
    }

    public static MapViewFragment newInstance() {
        return (new MapViewFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        mRelativeLayoutGps = view.findViewById(R.id.relative_layout_gps);

        this.getLocationPermission();

        mRelativeLayoutGps.setOnClickListener(v -> getDeviceLocation());

        return view;
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();

                        if (currentLocation != null) {
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM, "My Location");
                        }
                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(getActivity(), "Unable to get current location.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");

        if (mapFragment == null) {
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(MapViewFragment.this);
            }
            Log.d(TAG, "initMap: null");
        }
    }

    private void geoLocate(String query) {
        Log.d(TAG, "geoLocate: geolocating");

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(query, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            //Toast.makeText(getActivity(), address.toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            this.moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this.getActivity()).getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocationPermission: step 1");
            if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLocationPermission: step 2");
                mLocationPermissionGranted = true;
                this.initMap();
            } else {
                Log.d(TAG, "getLocationPermission: step 3");
                ActivityCompat.requestPermissions(this.getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            Log.d(TAG, "getLocationPermission: step 4");
            ActivityCompat.requestPermissions(this.getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getActivity(), "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Subscribe
    public void onEvent(RefreshEvent event) {
        this.geoLocate(event.getQuery());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
