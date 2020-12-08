package com.dmatrix.weatherapp.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dmatrix.weatherapp.R;
import com.dmatrix.weatherapp.adapters.WeatherRecyclerviewAdapter;
import com.dmatrix.weatherapp.fragments.MeasurementsDialogFragment;
import com.dmatrix.weatherapp.models.Forecast;
import com.dmatrix.weatherapp.utils.Constants;
import com.dmatrix.weatherapp.utils.PrefereneManager;

import java.util.ArrayList;
import java.util.List;

public class ForecastActivity extends AppCompatActivity implements MeasurementsDialogFragment.MeasurementsDialogListener {

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
        PrefereneManager prefereneManager = new PrefereneManager(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            forecasts = bundle.getParcelableArrayList("forecasts");
            address = bundle.getString("address");
        }

        if (prefereneManager.getString("unit") == null){
            prefereneManager.putString("unit", Constants.CELSIUS_SYMBOL);
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


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_units_measurement:
                openDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openDialog(){
        MeasurementsDialogFragment measurementsDialogFragment = new MeasurementsDialogFragment();
        measurementsDialogFragment.show(getSupportFragmentManager(), "measurements dialog");
    }

    @Override
    public void applyMeasurements(boolean unitChosen) {
        if (unitChosen){
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }
}