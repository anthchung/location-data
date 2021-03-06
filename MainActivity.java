package com.example.anthony.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.location.LocationManager.NETWORK_PROVIDER;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Button buttonNetwork;
    private TextView textNetwork;
    private TextView textNetworkAcc;
    private LocationManager locationManager;
    private LocationListener listener;

    private Button buttonGPS;
    private TextView textGPS;
    private TextView textGPSAcc;
    private LocationManager locationManagerGPS;
    private LocationListener listenerGPS;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private Button buttonFused;
    private TextView textFused;
    private TextView textFusedAcc;
    private ArrayList<String> outputCoordinates = new ArrayList<String>();
    private Button startLog;
    private String curLocationData;
    private TextView statusLog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        setContentView(R.layout.activity_main);

        textNetwork = (TextView) findViewById(R.id.networkLocationData);
        buttonNetwork = (Button) findViewById(R.id.ButtonNetwork);
        textNetworkAcc = (TextView) findViewById(R.id.networkAcc);

        textGPS = (TextView) findViewById(R.id.gpsLocationData);
        buttonGPS = (Button) findViewById(R.id.ButtonGPS);
        textGPSAcc = (TextView) findViewById(R.id.gpsAcc);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManagerGPS = (LocationManager) getSystemService(LOCATION_SERVICE);

        buttonFused = (Button) findViewById(R.id.ButtonFused);
        textFused = (TextView) findViewById(R.id.fusedLocationData);
        textFusedAcc = (TextView) findViewById(R.id.fusedAcc);

        startLog = (Button) findViewById(R.id.startLog);
        statusLog = (TextView) findViewById(R.id.statusLog);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //String provider = location.getProvider();

                textNetwork.setText(location.getLongitude() + "," + location.getLatitude());
                textNetworkAcc.setText(Float.toString(location.getAccuracy()));
                mGoogleApiClient.connect();
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    textFused.setText(mLastLocation.getLongitude() + "," + mLastLocation.getLatitude());
                    textFusedAcc.setText(Float.toString(mLastLocation.getAccuracy()));
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        listenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                textGPS.setText(location.getLongitude() + "," + location.getLatitude());
                textGPSAcc.setText(Float.toString(location.getAccuracy()));
                mGoogleApiClient.connect();
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    textFused.setText(mLastLocation.getLongitude() + "," + mLastLocation.getLatitude());
                    textFusedAcc.setText(Float.toString(mLastLocation.getAccuracy()));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        buttonNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, listener);
            }
        });
        buttonGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listenerGPS);
            }
        });
        buttonFused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleApiClient.connect();
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    textFused.setText(mLastLocation.getLongitude() + "," + mLastLocation.getLatitude());
                    textFusedAcc.setText(Float.toString(mLastLocation.getAccuracy()));
                }
            }
        });
        startLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CountDownTimer(61000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        statusLog.setText("Started");
                        curLocationData = (String) textNetwork.getText() + "," + (String) textGPS.getText() + "," + (String) textFused.getText() + ","
                                + (String) textNetworkAcc.getText() + "," + (String) textGPSAcc.getText() + "," + (String) textFusedAcc.getText();
                        outputCoordinates.add(curLocationData);
                    }

                    public void onFinish() {
                        //System.out.println(outputCoordinates);
                        statusLog.setText("Finished");
                        FileOutputStream fos = null;
                        try {

                            File file = new File(Environment.getExternalStorageDirectory() + "/Thesis", "test.csv");
                            fos = new FileOutputStream(file);
                            fos.write(("Time, Network Longitude, Network Latitude, GPS Longitude, GPS Latitude, Fused Longitude, Fused Latitude, Network Accuracy, GPS Accuracy, Fused Accuracy" + "\n").getBytes());
                            String[] outputCoordinatesString = new String[outputCoordinates.size()];
                            for (int i = 0; i < outputCoordinates.size(); i++) {
                                outputCoordinatesString[i] = outputCoordinates.get(i).toString();
                                fos.write((i + "," + outputCoordinatesString[i] + "\n").getBytes());
                            }
                            fos.close();
                            //fos = openFileOutput("test.txt", MODE_PRIVATE);
                            //File file = new File(MainActivity.this.getFilesDir().getAbsolutePath() + File.separator + "test.txt");
                            //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "test.csv");

                            /*
                            ObjectOutputStream oos = new ObjectOutputStream(fos);
                            oos.writeObject(outputCoordinates);
                            System.out.println(MainActivity.this.getFilesDir().getAbsolutePath());
                            System.out.println(MainActivity.this.getFilesDir());
                            oos.close();
                            */

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }.start();
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


