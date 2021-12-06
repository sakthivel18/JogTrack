package com.example.group09_hw07;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;

public class ViewJogMapsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private Jog mJog;
    private GoogleMap mMap;

    public ViewJogMapsFragment() {

    }

    public static ViewJogMapsFragment newInstance(Jog jog) {
        ViewJogMapsFragment fragment = new ViewJogMapsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, jog);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mJog = (Jog) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            /*LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
            mMap = googleMap;
            Jog trip = mJog;

            PolylineOptions options = new PolylineOptions();
            options.clickable(true);
            options.color(Color.BLUE);

            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();

            for(GeoPoint point : trip.points) {
                options.add(new LatLng(point.getLatitude(), point.getLongitude()));
                boundsBuilder.include(new LatLng(point.getLatitude(), point.getLongitude()));
            }

            Polyline polyline = mMap.addPolyline(options);

            LatLng startPoint = new LatLng(trip.points.get(0).getLatitude(), trip.points.get(0).getLongitude());
            LatLng endPoint = new LatLng(trip.points.get(trip.points.size()-1).getLatitude(),trip.points.get(trip.points.size()-1).getLongitude());

            mMap.addMarker(new MarkerOptions().position(startPoint).title("Start point"));
            mMap.addMarker(new MarkerOptions().position(endPoint).title("End point"));

            LatLngBounds bounds = boundsBuilder.build();

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 5));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 15));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_jog_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("View Jog");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }



    }
}