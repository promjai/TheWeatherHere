package com.theweatherhere;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends Activity {

    // GPSTracker class
    GPSTracker gps;

    private String url = "http://api.openweathermap.org/data/2.5/weather?lat=";
    private TextView name,main,temp,sunrise,sunset,humidity,wind,cloudiness;
    private HandleJSON obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (TextView) findViewById(R.id.name);
        main = (TextView) findViewById(R.id.main);
        temp = (TextView) findViewById(R.id.temp);
        sunrise = (TextView) findViewById(R.id.sunrise);
        sunset = (TextView) findViewById(R.id.sunset);
        humidity = (TextView) findViewById(R.id.humidity);
        wind = (TextView) findViewById(R.id.wind);
        cloudiness = (TextView) findViewById(R.id.cloudiness);

        // create class object
        gps = new GPSTracker(MainActivity.this);
        // check if GPS enabled
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            String lat = String.format("%.2f", latitude);
            String lon = String.format("%.2f", longitude);
            String and = "&lon=";

            //Log.e("lat",lat);
            //Log.e("lng",lon);

            String finalUrl = url + lat + and + lon;
            obj = new HandleJSON(finalUrl);
            obj.fetchJSON();

            while(obj.parsingComplete);

            DateFormat sdf = new SimpleDateFormat("HH:mm");
            TimeZone tz = TimeZone.getTimeZone("Asia/Bangkok");
            sdf.setTimeZone(tz);

            //name
            name.setText(obj.getName());

            //main
            main.setText(obj.getMain());

            //temperature
            String kelvin = obj.getTemp();
            double kel = Double.valueOf(kelvin).doubleValue() - 272.15;
            String temperature = Double.toString(kel);
            temp.setText(temperature + "°");

            //sunrise
            long timestampsunrise = Long.parseLong(obj.getSunrise()) * 1000;
            Date netDatesunrise = (new Date(timestampsunrise));
            sunrise.setText(" " + sdf.format(netDatesunrise) + " AM");

            //sunset
            long timestampsunset = Long.parseLong(obj.getSunset()) * 1000;
            Date netDatesunset = (new Date(timestampsunset));
            sunset.setText(" " + sdf.format(netDatesunset) + " PM");

            //humidity
            String humidityStr = obj.getHumidity();
            humidity.setText(" " + humidityStr + " %");

            //wind
            String windStr = obj.getWind();
            wind.setText(" " + windStr + " mps");

            //cloudiness
            String cloudinessStr = obj.getCloudiness();
            cloudiness.setText(" " + cloudinessStr + " %");

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }

}
