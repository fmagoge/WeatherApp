package com.dmatrix.weatherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmatrix.weatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WeatherRecyclerviewAdapter extends RecyclerView.Adapter<WeatherRecyclerviewAdapter.WeatherViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> dataMapArrayList;
    private HashMap<String, String> resultMap = new HashMap<>();

    public WeatherRecyclerviewAdapter(ArrayList<HashMap<String, String>> dataMapArrayList, Context context) {
        this.context = context;
        this.dataMapArrayList = dataMapArrayList;
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
        resultMap = dataMapArrayList.get(position);
        holder.textDay.setText(resultMap.get("dt_txt"));

        String tempkey = "temp";
        String weatherKey = "weather";

        JSONObject jsonObjectTemp = null;
        String min_temp ="min";
        String max_temp = "max";

        try {
            jsonObjectTemp = new JSONObject(tempkey);
            min_temp = jsonObjectTemp.getString("min");
            max_temp = jsonObjectTemp.getString("max");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.textTemp.setText(max_temp);

        String description = "";
        try {
            JSONArray jsonArray = new JSONArray(weatherKey);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            description = jsonObject.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.textOutlook.setText(description);
    }

    @Override
    public int getItemCount() {
        if (dataMapArrayList==null)
            return 0;
        return dataMapArrayList.size();
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
