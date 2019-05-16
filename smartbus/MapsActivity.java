package com.example.user.smartbus;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.nfc.Tag;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    String value;
    UserPosition userPosition=new UserPosition();
    private GoogleMap mMap;
    BusPosition busPosition=new BusPosition();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
       busPosition=(BusPosition)i.getSerializableExtra("busPosition");
       userPosition= (UserPosition) i.getSerializableExtra("userPosition");
       final TextView test= findViewById(R.id.testing);
        test.setText(userPosition.combine());
        Log.d("HERE::",busPosition.combine());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("LOCATION/Latitude");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                 value = dataSnapshot.getValue(String.class);
                Log.d("map", "Value is: " + value);
               // test.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("map", "Failed to read value.", error.toException());
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.parseDouble(busPosition.latitude), Double.parseDouble(busPosition.longitude));
        LatLng skudai = new LatLng(Double.parseDouble(userPosition.latitude), Double.parseDouble(userPosition.longitude));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions().position(skudai).title("Marker in skudai"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(busPosition.latitude), Double.parseDouble(busPosition.longitude)),17.0f));

    }


}

