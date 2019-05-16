package com.example.user.smartbus;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

// classes needed to initialize map
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

// classes needed to add the location component
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;

// classes needed to add a marker
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

// classes to calculate a route
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

// classes needed to launch navigation UI
import android.view.View;
import android.widget.Button;

import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;


public class Test extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {
    // variables for adding location layer
    private MapView mapView;
    private MapboxMap mapboxMap;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "Test";
    private NavigationMapRoute navigationMapRoute;
    // variables needed to initialize navigation
    private Button button;
    BusPosition busPosition = new BusPosition();
    UserPosition userPosition = new UserPosition();
    private Double distance;
    private Double duration;
    private TextView showDuration;
    private TextView showDistance;
    private  TextView showPassenger;
    private String getDestinationName;
    private String getDestinationLocation;
    private Point destinationPoint;
    private String sourceid="destination-source-id";
    private String userId="user-id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_test);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        Intent i = getIntent();
        busPosition = (BusPosition) i.getSerializableExtra("busPosition");
        userPosition = (UserPosition) i.getSerializableExtra("userPosition");
        getDestinationName = (String) i.getSerializableExtra("showDestinationName");
        getDestinationLocation = (String) i.getSerializableExtra("showDestinationLocation") ;
        Toast.makeText(getApplicationContext(), getDestinationName, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), getDestinationLocation, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCreate: "+getDestinationLocation.split(",")[0]);
        destinationPoint=Point.fromLngLat(Double.parseDouble(getDestinationLocation.split(",")[1]),Double.parseDouble(getDestinationLocation.split(",")[0]));
        Log.d(TAG, "onCreate: p"+destinationPoint);
        showDuration = findViewById(R.id.duration);
        showDistance = findViewById(R.id.distance);
        showPassenger= findViewById(R.id.textPassenger);
        showPassenger.setVisibility(View.INVISIBLE);
        showDistance.setVisibility(View.INVISIBLE);
        showDuration.setVisibility(View.INVISIBLE);
        DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("fire/users");
        Query query = db.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Log.d("User key", child.getKey());
                    Log.d("User val", child.child("temp").getValue().toString());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
       });
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                Point userPoint = Point.fromLngLat(103.629703, 1.546763);
                // Add the marker image to map
                style.addImage("marker-icon-id",
                        BitmapFactory.decodeResource(
                                Test.this.getResources(), R.drawable.mapbox_marker_icon_default));

                GeoJsonSource geoJsonSource = new GeoJsonSource("source-id", Feature.fromGeometry(
                        Point.fromLngLat(userPoint.longitude(), userPoint.latitude())));
                style.addSource(geoJsonSource);

                SymbolLayer symbolLayer = new SymbolLayer("layer-id", "source-id");
                symbolLayer.withProperties(
                        PropertyFactory.iconImage("marker-icon-id")
                );
                style.addLayer(symbolLayer);

//                enableLocationComponent(style);

                addDestinationIconSymbolLayer(style,sourceid);

                Point originPoint = Point.fromLngLat(103.635679,
                        1.540231);
                GeoJsonSource source = mapboxMap.getStyle().getSourceAs(sourceid);
                if (source != null) {
                    source.setGeoJson(Feature.fromGeometry(destinationPoint));
                }


                LatLng latLng = new LatLng(1.541475, 103.634402);
                LatLng dest = new LatLng(1.540231, 103.635679);
                Double a = latLng.distanceTo(dest);
                Toast.makeText(getApplicationContext(), a.toString(), Toast.LENGTH_SHORT).show();
                getRoute(originPoint, destinationPoint);

//                mapboxMap.addOnMapClickListener(Test.this);

                button = findViewById(R.id.startButton);
                button.setEnabled(true);
                button.setBackgroundResource(R.color.BgColorSubtitle2);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean simulateRoute = true;

                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(currentRoute)
                                .shouldSimulateRoute(simulateRoute)
                                .build();
                        // Call this method with Context from within an Activity
                        NavigationLauncher.startNavigation(Test.this, options);
                    }
                });
            }
        });
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle,String id) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource(id);
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }



    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);
                        distance = response.body().routes().get(0).distance();
                        duration = response.body().routes().get(0).duration();
                        showDuration.setText("Duration:" + duration);
                        showDistance.setText("Distance:" + distance);
                        showDistance.setVisibility(View.VISIBLE);
                        showDuration.setVisibility(View.VISIBLE);
                        Log.d(TAG, "distance: " + distance);
                        Log.d(TAG, "duration: " + duration);
                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}