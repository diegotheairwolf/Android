package com.myexample.app;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;


// *** start ***
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.location.Criteria;
// *** end ***

public class LocationActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        Log.i("***** start: ", "location attempt*****");

        LatLng coor = getLocation();

        Log.i("***** coors: ", "lat: " + coor.lat + "lon: " + coor.lon);

        Log.i("***** end: ", "location attempt *****");


    }


    // *** start ***
    public LatLng getLocation()
    {
        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat,lon;
        try {
            lat = location.getLatitude ();
            lon = location.getLongitude ();
            return new LatLng(lat, lon);
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

    class LatLng {

        Double lat;
        Double lon;

        LatLng(Double lat, Double lon) {
            this.lat = lat;
            this.lon = lon;
        }

    }
    // *** end ***



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LocationListener {

        public PlaceholderFragment() {
        }


        // *** start ***

        // double latitude;
        // double longitude;

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            /**
            this.latitude = latitude;
            this.longitude = longitude;
            **/
            //this.latitude = 123.456;
            //this.longitude = 456.123;

            Log.i("***** Geo_Location", "Latitude: " + latitude + ", Longitude: " + longitude + "*****");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
        // *** end ***




        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_location, container, false);

            TextView textView = (TextView) rootView.findViewById(R.id.putLocationHereId);

           // double latitude = location.getLatitude();
           // double longitude = location.getLongitude();
            double latitude = 1.234;
            double longitude = 2.345;

            String l = "not much: " + latitude + " and: " + longitude;

            textView.setText(l);


            return rootView;
        }




    }
}
