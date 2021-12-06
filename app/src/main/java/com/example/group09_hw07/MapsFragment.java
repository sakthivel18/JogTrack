package com.example.group09_hw07;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment {
    private static final String TAG = "MapsFragment";
    GoogleMap mMap;
    private static final int REQUEST_CODE_FINE_LOCATION = 1234;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                zoomToUserLocation();
            } else {
                // permission denied
                requestPermission();
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    PolylineOptions polylineOptions;
    LatLng startLocation;
    LatLng endLocation;
    LatLng userLocation;
    ArrayList<GeoPoint> points = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        points.clear();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null)
                    return;


                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "onLocationResult: " + location);
                    LatLng latLng= new LatLng(location.getLatitude(), location.getLongitude());
                    points.add(new GeoPoint(location.getLatitude(), location.getLongitude()));
                    polylineOptions.add(latLng);
                    mMap.addPolyline(polylineOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                    endLocation = latLng;
                }
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        mMap.clear();
    }

    public void requestPermission() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is NOT granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext())
                        .setMessage("We need permission for fine location")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
            }

        } else {
            // Permission is Granted
            zoomToUserLocation();

        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_FINE_LOCATION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Granted
                zoomToUserLocation();
            } else {
                //Permission NOT granted
                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //This block here means PERMANENTLY DENIED PERMISSION
                    new AlertDialog.Builder(getContext())
                            .setMessage("You have permanently denied this permission, go to settings to enable this permission")
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    gotoApplicationSettings();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .setCancelable(false)
                            .show();


                } else {
                    //
                    //textView.setText("Permission NOt granted");
                    this.requestPermission();
                }
            }
        }
    }

    private void gotoApplicationSettings() {

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getOpPackageName(), null);
        intent.setData(uri);
        startActivity(intent);

    }


    public void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null)
                    return;
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
            }
        });

        startLocationUpdates();
    }

    public void startLocationUpdates() {
        try {
            points.clear();
            polylineOptions =  new PolylineOptions();
            polylineOptions.color(Color.BLUE);
            mMap.clear();
            LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest).build();
            SettingsClient client = LocationServices.getSettingsClient(getContext());

            Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

            locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                }
            });

            locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(e instanceof ResolvableApiException) {
                        ResolvableApiException apiException = (ResolvableApiException) e;
                        try {
                            apiException.startResolutionForResult(getActivity(), 1234);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Log.d(TAG, "startLocationUpdates: " + ex.getMessage());
        }

    }

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public void stopLocationUpdates(String title) {
        try {
            if(endLocation == null)
                return;
            mMap.addMarker(new MarkerOptions().position(new LatLng(points.get(0).getLatitude(),points.get(0).getLongitude())).title("Start location"));
            mMap.addMarker(new MarkerOptions().position(endLocation).title("End location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endLocation, 12));
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            FirebaseUser currentUser = mAuth.getCurrentUser();

            if(currentUser == null) return;

            Map<String, Object> jog = new HashMap<>();
            jog.put("title", title);
            jog.put("uid", currentUser.getUid());
            jog.put("username", currentUser.getDisplayName());
            jog.put("points", points);
            jog.put("createdAt", FieldValue.serverTimestamp());

            db.collection("jogs")
                    .add(jog)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                fm.popBackStack();
                            }
                            fm.beginTransaction()
                                    .replace(R.id.containerView, new JogsFragment())
                                    .commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        } catch (Exception ex) {
            Log.d(TAG, "stopLocationUpdates: " + ex.getMessage());
        } finally {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            fm.beginTransaction()
                    .replace(R.id.containerView, new JogsFragment())
                    .commit();
        }
    }

}