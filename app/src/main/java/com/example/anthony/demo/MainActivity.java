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
    private ArrayList<String> outputCoordinatesNetwork = new ArrayList<String>();
    private ArrayList<String> outputCoordinatesGPS = new ArrayList<String>();
    private ArrayList<String> outputCoordinatesFused = new ArrayList<String>();
    private Button startLog;
    private String curLocationData;
    private TextView statusLog;
    private Button walkLog;
    private Button outputLog;
    private int startFlag = 0;
    private String curNetworkData;
    private String curGPSData;
    private String curFusedData;

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

        statusLog = (TextView) findViewById(R.id.statusLog);
        walkLog = (Button) findViewById(R.id.walkLog);
        outputLog = (Button) findViewById(R.id.outputLog);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //String provider = location.getProvider();

                textNetwork.setText(location.getLatitude() + "," + location.getLongitude());
                textNetworkAcc.setText(Float.toString(location.getAccuracy()));
                if (startFlag == 1) {
                    curNetworkData =location.getElapsedRealtimeNanos()/1e9 + "," + location.getLatitude() + "," + location.getLongitude() + "," + Float.toString(location.getAccuracy())+ "," + Float.toString(location.getSpeed());
                    outputCoordinatesNetwork.add(curNetworkData);
                }
                mGoogleApiClient.connect();
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    textFused.setText(mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
                    textFusedAcc.setText(Float.toString(mLastLocation.getAccuracy()));
                }
                locationManager.removeUpdates(listener);
                locationManager.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, listener);
                locationManager.requestSingleUpdate(NETWORK_PROVIDER,listener,null);
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
                textGPS.setText(location.getLatitude() + "," + location.getLongitude());
                textGPSAcc.setText(Float.toString(location.getAccuracy()));
                if (startFlag == 1) {
                    curGPSData =location.getElapsedRealtimeNanos()/1e9+ "," + location.getLatitude() + "," + location.getLongitude() + "," + Float.toString(location.getAccuracy())+ "," + Float.toString(location.getSpeed());
                    outputCoordinatesGPS.add(curGPSData);
                }
                mGoogleApiClient.connect();
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    textFused.setText(mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
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
                    textFused.setText(mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
                    textFusedAcc.setText(Float.toString(mLastLocation.getAccuracy()));
                }
            }
        });
        walkLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFlag = 1;
                statusLog.setText("Start");
            }
        });
        outputLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFlag = 0;
                statusLog.setText("Finished");
                FileOutputStream fos = null;
                try {
                    File file = new File(Environment.getExternalStorageDirectory() + "/Thesis", "network.csv");
                    fos = new FileOutputStream(file);
                    fos.write(("No, Time, Network Latitude, Network Longitude, Network Accuracy, Speed" + "\n").getBytes());
                    String[] outputCoordinatesString = new String[outputCoordinatesNetwork.size()];
                    for (int i = 0; i < outputCoordinatesNetwork.size(); i++) {
                        outputCoordinatesString[i] = outputCoordinatesNetwork.get(i).toString();
                        fos.write((i + "," + outputCoordinatesString[i] + "\n").getBytes());
                    }
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream fos2 = null;
                try {
                    File file = new File(Environment.getExternalStorageDirectory() + "/Thesis", "gps.csv");
                    fos2 = new FileOutputStream(file);
                    fos2.write(("No, Time, GPS Latitude, GPS Longitude, GPS Accuracy, Speed" + "\n").getBytes());
                    String[] outputCoordinatesString = new String[outputCoordinatesGPS.size()];
                    for (int i = 0; i < outputCoordinatesGPS.size(); i++) {
                        outputCoordinatesString[i] = outputCoordinatesGPS.get(i).toString();
                        fos2.write((i + "," + outputCoordinatesString[i] + "\n").getBytes());
                    }
                    fos2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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


