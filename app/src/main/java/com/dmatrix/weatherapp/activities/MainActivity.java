package com.dmatrix.weatherapp.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dmatrix.weatherapp.R;
import com.dmatrix.weatherapp.models.Daily;
import com.dmatrix.weatherapp.models.Forecast;
import com.dmatrix.weatherapp.models.WeatherApi;
import com.dmatrix.weatherapp.utils.Constants;
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
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{

    private GoogleMap googleMap;
    private static final String TAG = "MapsActivity";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;

    private LatLng defaultLocation = new LatLng(-26.0979984,27.8140565);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    private List<Address> addresses;
    String city, weatherPosition;
    private List<Forecast> forecasts;
    private boolean hasForecast;

    private SearchView search_input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageGeolocate = findViewById(R.id.ic_geolocate);
        search_input = findViewById(R.id.search_input);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //Alternate better option but requires Billing enabled on Places Api
        /*Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteSupportFragment.setTypeFilter(TypeFilter.ADDRESS);

        autocompleteSupportFragment.setLocationBias(RectangularBounds.newInstance(
                                new LatLng(-26.2711247, 28.1122661),
                                new LatLng( -26.0991893, 27.8011599)));

        autocompleteSupportFragment.setCountries("ZA");

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID,
                com.google.android.libraries.places.api.model.Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
               getLocationAddress(place.getLatLng().latitude, place.getLatLng().longitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("########",status.getStatusMessage());
            }
        });*/

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getDeviceLocation();

        imageGeolocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickCurrentPlace();
            }
        });

        search_input.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = search_input.getQuery().toString();
                List<Address> addressList = null;

                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList.size()>0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(location).draggable(true));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        getLocationAddress(address.getLatitude(), address.getLongitude());

                        search_input.setQuery("", false);
                        search_input.clearFocus();
                    }else {
                        Toast.makeText(MainActivity.this, "Cannot find Location : "+search_input.getQuery().toString(),Toast.LENGTH_LONG).show();
                        search_input.setQuery("", false);
                        search_input.clearFocus();
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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

        //Zoom in-out
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        //Compass
        this.googleMap.getUiSettings().setCompassEnabled(true);

        if (weatherPosition == null && city == null){
            weatherPosition = "Weather Postion";
            city = "Drag to new position";
        }


        this.googleMap.setOnMapClickListener(latLng -> googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng)));

        this.googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                googleMap.addMarker(new MarkerOptions().position(latLng).title(city).draggable(true));
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
                forecasts = new ArrayList<>();
                hasForecast = true;
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
        forecasts = new ArrayList<>();
        Toast.makeText(MainActivity.this, "Press down and drag RED pin to required location, or just search", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
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

        StringRequest request = new StringRequest(url, string -> parseJsonData(string, address), volleyError -> {
            Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
        });

        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
    }

    private void parseJsonData(String jsonString, String address) {
        Gson gson = new Gson();
        WeatherApi weatherApi = gson.fromJson(jsonString, WeatherApi.class);

        List<Daily> dailyList = weatherApi.getDaily();

        for (Daily daily: dailyList){
            Forecast forecast = new Forecast();
            //Day
            forecast.setDay(daily.getDt());
            //Min Temp
            forecast.setMinTemp(daily.getTemp().getMin());
            //Max Temp
            forecast.setMaxTemp(daily.getTemp().getMax());
            //Main
            forecast.setMainDesc(daily.getWeather().get(0).getMain());
            //Description
            forecast.setDescription(daily.getWeather().get(0).getDescription());
            //Icon
            forecast.setIcon(daily.getWeather().get(0).getIcon());

            forecasts.add(forecast);
        }

        if (hasForecast) {
            Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
            intent.putExtra("address", address);
            intent.putParcelableArrayListExtra("forecasts", (ArrayList<? extends Parcelable>) forecasts);
            startActivity(intent);
            hasForecast = false;
        }
    }


    private void pickCurrentPlace() {
        if (googleMap == null) {
            return;
        }

        if (locationPermissionGranted) {
            hasForecast = true;
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