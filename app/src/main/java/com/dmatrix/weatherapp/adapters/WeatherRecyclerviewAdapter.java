package com.dmatrix.weatherapp.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmatrix.weatherapp.R;

public class WeatherRecyclerviewAdapter extends RecyclerView.Adapter<WeatherRecyclerviewAdapter.WeatherViewHolder> {



    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder{
        TextView textDay, textTemp, textOutlook;


        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            textDay = itemView.findViewById(R.id.textDay);
            textTemp = itemView.findViewById(R.id.textTemp);
            textOutlook = itemView.findViewById(R.id.textOutlook);
        }
    }
}
