package com.theweatherhere;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends Activity {

    // GPSTracker class
    GPSTracker gps;

    private String url = "http://api.openweathermap.org/data/2.5/weather?lat=18.7992741&lon=98.9742345";
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

            String lat = Double.toString(latitude);
            String lon = Double.toString(longitude);

            String finalUrl = url;
            obj = new HandleJSON(finalUrl);
            obj.fetchJSON();

            while(obj.parsingComplete);

            DateFormat sdf = new SimpleDateFormat("HH:mm");
            TimeZone tz = TimeZone.getTimeZone("Asia/Bangkok");
            sdf.setTimeZone(tz);

            name.setText(obj.getName());
            main.setText(obj.getMain());

            //temperature
            String kelvin = obj.getTemp();
            double kel = Double.valueOf(kelvin).doubleValue() - 272.15;
            String temperature = Double.toString(kel);
            temp.setText(temperature + "°");

            //sunrise
            long timestampsunrise = Long.parseLong(obj.getSunrise()) * 1000;
            Date netDatesunrise = (new Date(timestampsunrise));
            sunrise.setText(" " + sdf.format(netDatesunrise) + "AM");

            //sunset
            long timestampsunset = Long.parseLong(obj.getSunset()) * 1000;
            Date netDatesunset = (new Date(timestampsunset));
            sunset.setText(" " + sdf.format(netDatesunset) + "PM");

            //humidity
            String humidityStr = obj.getHumidity();
            humidity.setText(" " + humidityStr + "%");

            //wind
            String windStr = obj.getWind();
            wind.setText(windStr + " mps");

            //cloudiness
            String cloudinessStr = obj.getCloudiness();
            cloudiness.setText("" + cloudinessStr + "%");

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
