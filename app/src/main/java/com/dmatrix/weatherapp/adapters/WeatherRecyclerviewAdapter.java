package com.dmatrix.weatherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmatrix.weatherapp.R;
import com.dmatrix.weatherapp.models.Forecast;

import java.util.List;

public class WeatherRecyclerviewAdapter extends RecyclerView.Adapter<WeatherRecyclerviewAdapter.WeatherViewHolder> {

    private List<Forecast> forecastList;
    private Context context;

    public WeatherRecyclerviewAdapter(List<Forecast> forecastList, Context context) {
        this.forecastList = forecastList;
        this.context = context;
    }


    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item,parent,false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        holder.textDay.setText(forecastList.get(position).getDay());
        holder.textTemp.setText(forecastList.get(position).getMaxTemp().toString());
        holder.textOutlook.setText(forecastList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder{
        TextView textDay, textTemp, textOutlook;


        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            textDay = itemView.findViewById(R.id.textDay);
            textTemp = itemView.findViewById(R.id.textTemp);
            textOutlook = itemView.findViewById(R.id.textOutlook);
        }
    }
}
