package com.dmatrix.weatherapp.activities;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dmatrix.weatherapp.R;
import com.dmatrix.weatherapp.adapters.WeatherRecyclerviewAdapter;
import com.dmatrix.weatherapp.utils.Constants;
import com.dmatrix.weatherapp.utils.TaskInfo;
import com.dmatrix.weatherapp.utils.Util;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, TaskInfo {

    private GoogleMap googleMap;
    private static final String TAG = "MapsActivity";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;

    private LatLng defaultLocation = new LatLng(-26.0979984,27.8140565);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    private List<Address> addresses;
    private TextView textLocation;
    String city, weatherPosition;

    private  RecyclerView recyclerView;
    private   WeatherRecyclerviewAdapter recyclerViewAdapter;
    private   RecyclerView.LayoutManager recyclerViewLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        textLocation = findViewById(R.id.textLocation);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerViewLayoutManager = new LinearLayoutManager(this);


        getDeviceLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDeviceLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(defaultLocation);
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        this.googleMap.moveCamera(center);
        this.googleMap.animateCamera(zoom);

        if (weatherPosition == null && city == null){
            weatherPosition = "Weather Postion";
            city = "Drag to new position";
        }

        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });

        this.googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                LatLng position=marker.getPosition();

                Log.d(getClass().getSimpleName(),
                        String.format("Dragging to %f:%f",
                                position.latitude,
                                position.longitude));
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                LatLng position=marker.getPosition();

                Log.d(getClass().getSimpleName(), String.format("Drag from %f:%f",
                        position.latitude,
                        position.longitude));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng position=marker.getPosition();

                Log.d(getClass().getSimpleName(), String.format("##### Dragged to %f:%f",
                        position.latitude,
                        position.longitude));
                getLocationAddress(position.latitude,position.longitude);
            }
        });

        addMarker(this.googleMap, weatherPosition, city);
    }

    private void addMarker(GoogleMap map, String title, String snippet) {
        map.addMarker(new MarkerOptions().position(defaultLocation)
                .title(title)
                .snippet(snippet)
                .draggable(true));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_geolocate:

                 pickCurrentPlace();
                 return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onTaskDone(String output) {
        Log.d("### OUTPUT", output);
        recyclerViewAdapter = new WeatherRecyclerviewAdapter(Util.getJsonArrayList(output),getApplicationContext());
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void getLocationPermission() {
        locationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                Log.d(TAG, "Latitude: " + lastKnownLocation.getLatitude());
                                Log.d(TAG, "Longitude: " + lastKnownLocation.getLongitude());
                                getLocationAddress(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())).title(addresses.get(0).getLocality() + " ," + addresses.get(0).getCountryName()));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }else{
                                //TODO - enable Location settings
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            googleMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0);
        city = addresses.get(0).getLocality();
        weatherPosition = addresses.get(0).getPostalCode();


        String url =  String.valueOf(new StringBuilder(Constants.BASE_URL)
                .append("?")
                .append(Constants.LAT)
                .append(latitude)
                .append(Constants.LON)
                .append(longitude)
                .append(Constants.EXCLUDE)
                .append(Constants.API_KEY));

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                parseJsonData(string);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
        textLocation.setText(address);
    }

    void parseJsonData(String jsonString) {

        ArrayList<HashMap<String, String>> dataMapArrayList;
        HashMap<String, String> resultMap = new HashMap<>();

        try {
            JSONObject object = new JSONObject(jsonString);
            JSONArray dailyWeatherArray = null;
            try {
                dailyWeatherArray = object.getJSONArray("daily");


                dataMapArrayList = Util.getJsonArrayList(dailyWeatherArray.toString());

                for (int i=0; i<5; i++){
                    resultMap = dataMapArrayList.get(i);


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void pickCurrentPlace() {
        if (googleMap == null) {
            return;
        }

        if (locationPermissionGranted) {
            getDeviceLocation();
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            googleMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(defaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));
            getLocationPermission();
        }
    }

}