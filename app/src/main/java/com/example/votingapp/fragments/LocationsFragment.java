package com.example.votingapp.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.adapters.LocationAdapter;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.Location;
import com.example.votingapp.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class LocationsFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private static final String ARG_ELECTION = "election";
    private static final String TAG = "LocationsFragment";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5;
    private static final float DEFAULT_ZOOM = 14.0f;

    Election election;
    static Context context;
    static List<Location> allLocations;
    static List<Location> filteredLocations;
    RecyclerView rvLocations;
    static LocationAdapter locationAdapter;
    boolean locationPermissionGranted;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView tvFilter;

    private SupportMapFragment mapFragment;
    public static GoogleMap map;
    private LocationRequest mLocationRequest;
    android.location.Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private final static String KEY_LOCATION = "location";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private android.location.Location lastKnownLocation;

    public LocationsFragment() {
        // Required empty public constructor
    }

    public static LocationsFragment newInstance(Context context1, Election election, List<Location> inLocations) {
        LocationsFragment fragment = new LocationsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ELECTION, Parcels.wrap(election));
        context = context1;
        allLocations = new ArrayList<>();
        allLocations.addAll(inLocations);
        Collections.sort(allLocations);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            election = Parcels.unwrap(getArguments().getParcelable(ARG_ELECTION));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_locations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvLocations = view.findViewById(R.id.rvLocations);
        filteredLocations = new ArrayList<>();
//        Collections.sort(allLocations);
        filteredLocations.addAll(allLocations);
        locationAdapter = new LocationAdapter(context, filteredLocations);
        rvLocations.setLayoutManager(new LinearLayoutManager(context));
        rvLocations.setAdapter(locationAdapter);
        tvFilter = view.findViewById(R.id.tvFilter);
        tvFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view);
            }
        });

        Network.getDistancesFrom(User.getAddress(ParseUser.getCurrentUser()), filteredLocations);

        getLocationPermission();

        // making the map!
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                    // Turn on the My Location layer and the related control on the map.
                    updateLocationUI();

                    // Get the current location of the device and set the position of the map.
                    getDeviceLocation();
                }
            });

        } else {
            Log.i(TAG, "Error - Map Fragment was null!!");
        }

    }
    public void showMenu(View anchor) {
        PopupMenu popup = new PopupMenu(context, anchor);
        popup.setOnMenuItemClickListener(this);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
        popup.show();
    }


    public static void sortLocations() {
        Collections.sort(allLocations);
        filteredLocations.clear();
        filteredLocations.addAll(allLocations);
        locationAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        rvLocations.smoothScrollToPosition(0);
        switch (item.getItemId()) {
            case R.id.action_polling:
                tvFilter.setText("Filter Locations :     Polling Locations");
                filterLocations("Polling Location");
                return true;
            case R.id.action_dropoff:
                tvFilter.setText("Filter Locations :     Drop Off Locations");
                filterLocations("Drop Off Location");
                return true;
            case R.id.action_early:
                tvFilter.setText("Filter Locations :     Early Voting Sites");
                filterLocations("Early Voting Site");
                return true;
            case R.id.action_all:
                tvFilter.setText("Filter Locations :     Any");
                showAllLocations();
                return true;
            default:
                return false;
        }
    }

    public void showAllLocations() {
        filteredLocations.clear();
        filteredLocations.addAll(allLocations);
        locationAdapter.notifyDataSetChanged();
        resetMarkers(true);
    }

    public void resetMarkers(boolean visibility) {
        for (Location location : allLocations) {
            location.getMarker().setVisible(visibility);
        }
    }

    public void filterLocations(String type) {
        filteredLocations.clear();
        resetMarkers(false);
        for (int i = 0 ; i< allLocations.size(); i++) {
            Location location = allLocations.get(i);
            if (location.getType().equals(type)) {
                filteredLocations.add(location);
                location.getMarker().setVisible(true);
            }
        }
        locationAdapter.notifyDataSetChanged();
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Log.i(TAG, "Map Fragment was loaded properly!");
            getCoords();
        } else {
            Log.i(TAG, "Map was null!!");
        }
    }

    public void getCoords() {
        Log.i(TAG, "getCoords");
        for (Location loc : allLocations) {
            Log.i(TAG, "Location: " + loc.getName());
            Network.getCoordinates(loc.getAddress(), loc);
        }
    }

    public static void addLatLng(double lat, double lng, Location loc) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(loc.getLatLng())
                .title(loc.getName())
                .icon(getMarkerIcon(context.getResources().getString(loc.getPillColor()))));
        loc.setMarker(marker);
    }
    public static BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<android.location.Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<android.location.Location>() {
                    @Override
                    public void onComplete(@NonNull Task<android.location.Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(allLocations.get(0).getLatLng(), DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


}