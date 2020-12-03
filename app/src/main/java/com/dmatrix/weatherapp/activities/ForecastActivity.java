package com.dmatrix.weatherapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.dmatrix.weatherapp.R;
import com.dmatrix.weatherapp.adapters.WeatherRecyclerviewAdapter;
import com.dmatrix.weatherapp.models.Forecast;

import java.util.ArrayList;
import java.util.List;

public class ForecastActivity extends AppCompatActivity {

    private List<Forecast> forecasts;

    private RecyclerView recyclerView;
    private WeatherRecyclerviewAdapter recyclerViewAdapter;
    private  RecyclerView.LayoutManager recyclerViewLayoutManager;

    private TextView textLocation;
    private String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        forecasts = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            forecasts = bundle.getParcelableArrayList("forecasts");
            address = bundle.getString("address");
        }

        textLocation = findViewById(R.id.textLocation);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(ForecastActivity.this);

        textLocation.setText(address);

        recyclerViewAdapter = new WeatherRecyclerviewAdapter(forecasts, getApplicationContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }
}