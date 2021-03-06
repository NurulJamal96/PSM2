package com.example.user.smartbus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Where_My_Bus extends AppCompatActivity implements View.OnClickListener {
    UserPosition userPosition = new UserPosition(); //to hold user current location
    private static final String TAG = Where_My_Bus.class.getSimpleName();
    DatabaseReference myRef;
    BusPosition busPosition = new BusPosition();  // to hold current location of the bus
    private Button showLocationOption; // button to show list of option destination
    Button gps; // button to get current location of the user
    private Button qrCode; // option to scan qr code
    private TextView border; //border for spinner
    private Spinner optionLocation; //spinner to show list of destination

    @BindView(R.id.gps)
    Button btnStartUpdates;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    private Button getRoute;
    private ArrayList<String> list = new ArrayList<String>();
    private String selectedDestination;
    private int selectedPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //for action bar
        String dashboard = getString(R.string.dashboard);
        getSupportActionBar().setTitle(dashboard);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // logo for dashboard
        getSupportActionBar().setLogo(R.drawable.ic_directions_bus_black_24dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //instant and layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where__my__bus);
        // initialize the necessary libraries
        ButterKnife.bind(this);
        init();
        //initialize button, text and spinner
        getRoute = findViewById(R.id.getRoute);
        border = findViewById(R.id.border);
        gps = findViewById(R.id.gps);
        qrCode = findViewById(R.id.qrCode);
        optionLocation = findViewById(R.id.dropDownDestination);
        showLocationOption = findViewById(R.id.showLocationOption);
        //set visibility for gps and qr code
        gps.setVisibility(View.INVISIBLE);
        qrCode.setVisibility(View.INVISIBLE);
        //initialize array for spinner to use it
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Where_My_Bus.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.optionLocation));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //tell spinner to user myAdapter
        optionLocation.setAdapter(myAdapter);
        optionLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                list.get(position);
               selectedDestination= myAdapter.getItem(position);
                selectedPosition=position;
                Toast.makeText(getApplicationContext(),selectedDestination,Toast.LENGTH_SHORT).show();
                Log.d(TAG, "user select:"+selectedDestination+"in position:"+selectedPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

//        ArrayList<String> list = new ArrayList<String>();
        list.add(0,"1.56270219,103.639143");
        list.add(1,"1.56046070,103.641429");
        list.add(2,"1.55814413,103.640141");
        list.add(3,"1.55696439,103.637792");
        list.add(4,"1.55986547,103.634616");
        list.add(5,"1.56264320,103.636311");

//        Toast.makeText(getApplicationContext(),Toast.LENGTH_SHORT, optionLocation.getId()).show();
        //initialize firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("lOCATION");
        Log.d("before", " before");
        readData(new FirebaseCallBack() {
            @Override
            public void onCallback(BusPosition bus) {
                Log.d("before:", "sucees " + bus.combine());
            }
        });

        //set click for the button
        getRoute.setOnClickListener(this);
        showLocationOption.setOnClickListener(this);
        qrCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getRoute:
                Log.d("before::", busPosition.combine());
                if (busPosition.getLatitude() == null) {
                   // busPosition.setLatitude("1.559729");
                   // busPosition.setLongitude("103.637978");1.5391
                    //busPosition.setLatitude("1.5391");user
                    //busPosition.setLongitude("103.6339");user
                }

                if(userPosition.getLatitude() == null)
                {
                   Toast.makeText(getApplicationContext(), "Please choose click your location", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "here shafri"+userPosition.combine());
                }
                else {
                    Intent i = new Intent(this, Test.class);
                    i.putExtra("busPosition", busPosition);
                    i.putExtra("userPosition", userPosition);
                    i.putExtra("showDestinationName",selectedDestination);
                    i.putExtra("showDestinationLocation",list.get(selectedPosition));
                    startActivity(i);
                }
                break;
            case R.id.showLocationOption:
                if (gps.getVisibility() == View.VISIBLE) {
                    gps.setVisibility(View.GONE);
                    qrCode.setVisibility(View.GONE);
                    ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) border.getLayoutParams();
                    ConstraintLayout.LayoutParams pp = (ConstraintLayout.LayoutParams) optionLocation.getLayoutParams();
                    lp.setMargins(88, 320, 0, 0);
                    pp.setMargins(88, 320, 0, 0);
                    border.setLayoutParams(lp);
                    optionLocation.setLayoutParams(pp);
                } else {
                    gps.setVisibility(View.VISIBLE);
                    qrCode.setVisibility(View.VISIBLE);
                    ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) border.getLayoutParams();
                    ConstraintLayout.LayoutParams pp = (ConstraintLayout.LayoutParams) optionLocation.getLayoutParams();
                    lp.setMargins(88, 620, 0, 0);
                    pp.setMargins(88, 620, 0, 0);
                    border.setLayoutParams(lp);
                    optionLocation.setLayoutParams(pp);
                }
                break;
        }

    }

    private void readData(final FirebaseCallBack firebaseCallBack) {
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String latitude = Double.toString(dataSnapshot.child("Latitude").getValue(Double.class));
                String longitude = Double.toString(dataSnapshot.child("Longitude").getValue(Double.class));
                busPosition.setLatitude(latitude);
                busPosition.setLongitude(longitude);
                Log.d("before", "Value is in string: " + busPosition.combine());
                firebaseCallBack.onCallback(busPosition);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("before", "Failed to read value.", error.toException());
            }
        });
        Log.d("before", " done");
    }

    private interface FirebaseCallBack {
        void onCallback(BusPosition bus);
    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            userPosition.setLatitude(Double.toString(mCurrentLocation.getLatitude()));
            userPosition.setLongitude(Double.toString(mCurrentLocation.getLongitude()));
            Log.i("user position:", userPosition.combine());
        }
        toggleButtons();
    }

    private void toggleButtons() {
        if (mRequestingLocationUpdates) {
            btnStartUpdates.setEnabled(false);
        } else {
            btnStartUpdates.setEnabled(true);
        }
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(Where_My_Bus.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(Where_My_Bus.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    @OnClick(R.id.gps)
    public void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        toggleButtons();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        updateLocationUI();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }


}
