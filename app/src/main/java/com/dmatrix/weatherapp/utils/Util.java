package com.dmatrix.weatherapp.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class Util {

    public static ArrayList<HashMap<String, String>> getJsonArrayList(String jsonString) {

        try {
            JSONArray jsonArray  = new JSONArray(jsonString);
            ArrayList<HashMap<String, String>> mapArrayList = new ArrayList<HashMap<String, String>>();


            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, String> keyValueHashMap = new HashMap<String, String>();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Iterator key = jsonObject.keys();

                while (key.hasNext()) {
                    String k = key.next().toString();
                    keyValueHashMap.put(k, jsonObject.getString(k));

                }
                mapArrayList.add(keyValueHashMap);

            }

            return mapArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String convertToDayofWeek(int millis){
        Date date = new Date(millis * 1000L);
        //SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.ENGLISH);

        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static String convertKelvinsToCelsius(Double kelvins){
        double cel = kelvins - 273.15d;
        String celStr = String.format("%.1f",cel);
        return celStr.concat(" ").concat(Constants.CELSIUS_SYMBOL);
    }

    public static String convertKelvinsToFahrenheit(Double kelvins){
        String celStr = String.format("%.1f",((kelvins - 273.15) * 1.8) + 32);
        return celStr.concat(" ").concat(Constants.FAHRENHEIT_SYMBOL);
    }

}
