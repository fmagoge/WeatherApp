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


    public static String convertToDayofWeek(Integer millis){
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }


}
