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
import com.dmatrix.weatherapp.utils.Util;

import java.util.List;

public class WeatherRecyclerviewAdapter extends RecyclerView.Adapter<WeatherRecyclerviewAdapter.WeatherViewHolder> {

    private List<Forecast> forecastList;
    private Context context;

    public WeatherRecyclerviewAdapter(List<Forecast> forecastList, Context context) {
        this.forecastList = forecastList;
        this.context = context;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item,parent,false);
        WeatherViewHolder weatherViewHolder = new WeatherViewHolder(view);
        return weatherViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        holder.textDay.setText(Util.convertToDayofWeek(forecastList.get(position).getDay()));
        holder.textMinTemp.setText(Util.convertKelvinsToFahrenheit(forecastList.get(position).getMinTemp()));
        holder.textMaxTemp.setText(Util.convertKelvinsToFahrenheit(forecastList.get(position).getMaxTemp()));
        holder.textOutlook.setText(forecastList.get(position).getMainDesc());
        holder.textOutlookDesc.setText(forecastList.get(position).getDescription());
        holder.textIcon.setText(forecastList.get(position).getIcon());
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder{
        TextView textDay, textMinTemp,textMaxTemp, textOutlook, textOutlookDesc, textIcon;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            textDay = itemView.findViewById(R.id.textDay);
            textMinTemp = itemView.findViewById(R.id.textMinTemp);
            textMaxTemp = itemView.findViewById(R.id.textMaxTemp);
            textOutlook = itemView.findViewById(R.id.textOutlook);
            textOutlookDesc = itemView.findViewById(R.id.textOutlookDesc);
            textIcon = itemView.findViewById(R.id.textIcon);
        }
    }
}
