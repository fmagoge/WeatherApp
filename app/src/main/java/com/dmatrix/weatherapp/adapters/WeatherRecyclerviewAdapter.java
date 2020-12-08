package com.dmatrix.weatherapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmatrix.weatherapp.R;
import com.dmatrix.weatherapp.models.Forecast;
import com.dmatrix.weatherapp.utils.Constants;
import com.dmatrix.weatherapp.utils.PrefereneManager;
import com.dmatrix.weatherapp.utils.Util;

import java.util.List;

public class WeatherRecyclerviewAdapter extends RecyclerView.Adapter<WeatherRecyclerviewAdapter.WeatherViewHolder> {

    private List<Forecast> forecastList;
    private Context context;
    private Typeface weatherFont;
    private PrefereneManager prefereneManager;

    public WeatherRecyclerviewAdapter(List<Forecast> forecastList, Context context) {
        this.forecastList = forecastList;
        this.context = context;
        weatherFont = Typeface.createFromAsset(context.getAssets(), "weather.ttf");
        prefereneManager = new PrefereneManager(context);
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
        Log.d("Millis Days: ",forecastList.get(position).getDay().toString());
        holder.textDay.setText(Util.convertToDayofWeek(forecastList.get(position).getDay()));
        if (prefereneManager.getString("unit").equals(Constants.CELSIUS_SYMBOL)){
            holder.textMinTemp.setText(Util.convertKelvinsToCelsius(forecastList.get(position).getMinTemp()));
            holder.textMaxTemp.setText(Util.convertKelvinsToCelsius(forecastList.get(position).getMaxTemp()));
        }else if (prefereneManager.getString("unit").equals(Constants.FAHRENHEIT_SYMBOL)){
            holder.textMinTemp.setText(Util.convertKelvinsToFahrenheit(forecastList.get(position).getMinTemp()));
            holder.textMaxTemp.setText(Util.convertKelvinsToFahrenheit(forecastList.get(position).getMaxTemp()));
        }

        holder.textOutlookDesc.setText(forecastList.get(position).getDescription());
        holder.textIcon.setTypeface(weatherFont);

        switch (forecastList.get(position).getIcon()){
            case "01d":
                holder.textIcon.setText(R.string.wi_day_sunny);
                //holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.czysteniebo));
                break;
            case "02d":
                holder.textIcon.setText(R.string.wi_cloudy_gusts);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.pochmurno));
                break;
            case "03d":
                holder.textIcon.setText(R.string.wi_cloud_down);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.pochmurno));
                break;
            case "04d":
                holder.textIcon.setText(R.string.wi_cloudy);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.pochmurno));
                break;
            case "04n":
                holder.textIcon.setText(R.string.wi_night_cloudy);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.pochmurno));
                break;
            case "10d":
                holder.textIcon.setText(R.string.wi_day_rain_mix);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.deszcz));
                break;
            case "11d":
                holder.textIcon.setText(R.string.wi_day_thunderstorm);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.burza));
                break;
            case "13d":
                holder.textIcon.setText(R.string.wi_day_snow);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.snieg));
                break;
            case "01n":
                holder.textIcon.setText(R.string.wi_night_clear);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.czystenoc));
                break;
            case "02n":
                holder.textIcon.setText(R.string.wi_night_cloudy);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.pochmurno));
                break;
            case "03n":
                holder.textIcon.setText(R.string.wi_night_cloudy_gusts);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.pochmurno));
                break;
            case "10n":
                holder.textIcon.setText(R.string.wi_night_cloudy_gusts);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.pochmurno));
                break;
            case "11n":
                holder.textIcon.setText(R.string.wi_night_rain);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.deszcznoc));
                break;
            case "13n":
                holder.textIcon.setText(R.string.wi_night_snow);
//                holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.sniegnoc));
                break;

        }
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder{
        TextView textDay, textMinTemp,textMaxTemp, textOutlookDesc, textIcon;
        private LinearLayout linearLayout;


        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearWeatherItem);
            textDay = itemView.findViewById(R.id.textDay);
            textMinTemp = itemView.findViewById(R.id.textMinTemp);
            textMaxTemp = itemView.findViewById(R.id.textMaxTemp);
            textOutlookDesc = itemView.findViewById(R.id.textOutlookDesc);
            textIcon = itemView.findViewById(R.id.textIcon);
        }
    }

}
