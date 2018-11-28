package com.stellar.xueyingbai.travelandentertainmentsearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.Map;

import com.google.gson.Gson;

public class PlacesResults {
    private String name;
    private String address;
    private String icon;
    private String place_id;

    public PlacesResults () {

    }

    public PlacesResults(String name, String address, String icon, String place_id) {
        this.name = name;
        this.address = address;
        this.icon = icon;
        this.place_id = place_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setIcon_url(String icon_url) {
        this.icon = icon_url;
    }

    public String getIcon() {
        return icon;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void clickHeart(Context context, String place_id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        java.util.Map<String, ?> pref = preferences.getAll();
        //Gson gson = new Gson();
//        for (Map.Entry entry: pref.entrySet()) {
//            String placeid = entry.getKey().toString();
//            String value = preferences.getString(placeid, "");
//            PlacesResults row = gson.fromJson(value, PlacesResults.class);
//            System.out.println("row:"+row.getName());
//        }
        //already in the favorites list
        SharedPreferences.Editor editor = preferences.edit();
        if (preferences.contains(place_id)) {
            editor.remove(place_id);
            editor.commit();
        } else {
            Gson gson = new Gson();
            String value = gson.toJson(this);
            System.out.println(value);

            editor.putString(place_id, value);
            editor.commit();
        }
    }
}
