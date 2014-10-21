package com.theweatherhere;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;

public class HandleJSON {

    private String name = "name";
    private String main = "main";
    private String temp = "temp";
    private String sunrise = "sunrise";
    private String sunset = "sunset";
    private String humidity = "humidity";
    private String wind = "wind";
    private String cloudiness = "cloudiness";
    private String urlString = null;

    public volatile boolean parsingComplete = true;
    public HandleJSON(String url){
        this.urlString = url;
    }
    public String getName(){
        return name;
    }
    public String getMain(){
        return main;
    }
    public String getTemp(){
        return temp;
    }
    public String getSunrise(){
        return sunrise;
    }
    public String getSunset(){
        return sunset;
    }
    public String getHumidity(){
        return humidity;
    }
    public String getWind(){
        return wind;
    }
    public String getCloudiness(){
        return cloudiness;
    }

    @SuppressLint("NewApi")
    public void readAndParseJSON(String in) {
        try {
            JSONObject reader = new JSONObject(in);

            name = reader.getString("name");

            //JSONArray jArray = new JSONArray(in);
            //JSONObject json_data = jArray.getJSONObject(0);
            //main = json_data.getString("main");

            JSONObject mainApi  = reader.getJSONObject("main");
            temp = mainApi.getString("temp");
            humidity = mainApi.getString("humidity");

            JSONObject sysApi  = reader.getJSONObject("sys");
            sunrise = sysApi.getString("sunrise");
            sunset = sysApi.getString("sunset");

            JSONObject windApi  = reader.getJSONObject("wind");
            wind = windApi.getString("speed");

            JSONObject cloudsApi  = reader.getJSONObject("clouds");
            cloudiness = cloudsApi.getString("all");

            parsingComplete = false;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public void fetchJSON(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    String data = convertStreamToString(stream);

                    readAndParseJSON(data);
                    stream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
