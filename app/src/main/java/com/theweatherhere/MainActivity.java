package com.theweatherhere;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


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

            name.setText(obj.getName());
            main.setText(obj.getMain());
            temp.setText(obj.getTemp());
            sunrise.setText(obj.getSunrise());
            sunset.setText(obj.getSunset());
            humidity.setText(obj.getHumidity());
            wind.setText(obj.getWind());
            cloudiness.setText(obj.getCloudiness());

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
