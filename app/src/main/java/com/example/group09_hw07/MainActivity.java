package com.example.group09_hw07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements JogsFragment.IJogsFragment {

    private static final int REQUEST_CODE_FINE_LOCATION = 1234;
    Button buttonStartJog;
    Button buttonEndJogging;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerView, new LoginFragment())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerView, new JogsFragment())
                    .commit();
        }
        /*loadMap();
        buttonStartJog = findViewById(R.id.buttonStartJog);
        buttonEndJogging = findViewById(R.id.buttonEndJogging);
        buttonStartJog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.mapContainer);
                mapsFragment.startLocationUpdates();
            }
        });
        buttonEndJogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.mapContainer);
                mapsFragment.stopLocationUpdates();
            }
        });*/
    }

    @Override
    public void viewJog(Jog mJog) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerView, ViewJogFragment.newInstance(mJog))
                .addToBackStack("null")
                .commit();
    }

    /*void loadMap() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mapContainer, new MapsFragment())
                .commit();
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_FINE_LOCATION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Granted
                MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.mapsFragmentContainter);
                mapsFragment.zoomToUserLocation();

            } else {
                //Permission NOT granted
                if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //This block here means PERMANENTLY DENIED PERMISSION
                    new AlertDialog.Builder(this)
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
                    MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.mapsFragmentContainter);
                    mapsFragment.zoomToUserLocation();
                }
            }
        }
    }

    private void gotoApplicationSettings() {

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getOpPackageName(), null);
        intent.setData(uri);
        startActivity(intent);

    }


}