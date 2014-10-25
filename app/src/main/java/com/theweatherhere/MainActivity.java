package com.theweatherhere;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends Activity {

    // GPSTracker class
    GPSTracker gps;

    private String url = "http://api.openweathermap.org/data/2.5/weather?lat=";
    private ImageView icon;
    private ImageLoader loader;
    private DisplayImageOptions options;
    private TextView name,main,temp,sunrise,sunset,humidity,wind,cloudiness;
    private HandleJSON obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (TextView) findViewById(R.id.name);
        icon = (ImageView) findViewById(R.id.imgIcon);
        main = (TextView) findViewById(R.id.main);
        temp = (TextView) findViewById(R.id.temp);
        sunrise = (TextView) findViewById(R.id.sunrise);
        sunset = (TextView) findViewById(R.id.sunset);
        humidity = (TextView) findViewById(R.id.humidity);
        wind = (TextView) findViewById(R.id.wind);
        cloudiness = (TextView) findViewById(R.id.cloudiness);

        if (isInternetConnected()) { // chack status internet
            getData();
        } else {
            showAlertNoNet(); // chack status internet
        }

    }

    //----------------------------------get Data from JSON-----------------------------------

    public void getData() {

        // create class object
        gps = new GPSTracker(MainActivity.this);
        // check if GPS enabled
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            String lat = String.format("%.2f", latitude);
            String lon = String.format("%.2f", longitude);
            String and = "&lon=";

            Log.e("lat",lat);
            Log.e("lng", lon);

            String finalUrl = url + lat + and + lon;
            obj = new HandleJSON(finalUrl);
            obj.fetchJSON();

            while(obj.parsingComplete);

            DateFormat sdf = new SimpleDateFormat("HH:mm");
            TimeZone tz = TimeZone.getDefault();
            sdf.setTimeZone(tz);

            //name
            name.setText(obj.getName());

            //main
            String urlStr = "http://openweathermap.org/img/w/" + obj.getIcon() + ".png";
            Log.e("url",urlStr);

            loader = ImageLoader.getInstance();
            loader.init(ImageLoaderConfiguration
                    .createDefault(getApplicationContext()));
            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.ic_launcher)
                    .showImageOnFail(R.drawable.ic_launcher)
                    .resetViewBeforeLoading(true).cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300)).build();
            loader.displayImage(urlStr, icon, options);

            main.setText(obj.getMain());

            //temperature
            String kelvin = obj.getTemp();
            double kel = Double.valueOf(kelvin).doubleValue() - 273.15;
            String temperature = String.format("%.2f", kel);
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
            String windStr = String.format("%.2f", Double.valueOf(obj.getWind()).doubleValue());
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

    //----------------------------------check internet-----------------------------------
    private boolean isInternetConnected(){
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }
    private void showAlertNoNet(){
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);

        alertDlg.setMessage("Please connect to working Internet connection.")
                .setTitle("Internet Connection Error")
                .setIcon(R.drawable.ic_action_error)
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                        startActivityForResult(gpsOptionsIntent, 0);
                    }
                });

        alertDlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDlg.create();
        alert.show();
    }

    //----------------------------------menu-----------------------------------

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
        if (id == R.id.action_share) {

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Here is the share content body";
            //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

            return true;
        } else if (id == R.id.action_refresh) {

            if (isInternetConnected()) { // chack status internet
                getData();
            } else {
                showAlertNoNet(); // chack status internet
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}