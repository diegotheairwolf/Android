package com.example.seizealert;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.widget.Button;

import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.location.Criteria;
import android.location.Geocoder;


public class LocationSMS extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get current address
		String str = getAddress();
		
		// Get username currently on preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String username = prefs.getString("username", "");
				
		sendSMS("5129445248", "Hello, the patient " + username + 
				" is having a seizure at the location: " + str);
		
		Log.i("***** breakpoint", " ");
	}
	
	
	//---sends an SMS message to another device---
	private void sendSMS(String phoneNumber, String message){
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);
	} 
	
	// Gets the location coordinates and translates them into GeoCoordinates
	private String getAddress() {
		
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
	    locationManager.requestLocationUpdates(
	    		LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
	    	        @Override
	    	        public void onStatusChanged(String provider, int status, Bundle extras) {
	    	        }
	    	        @Override
	    	        public void onProviderEnabled(String provider) {
	    	        }
	    	        @Override
	    	        public void onProviderDisabled(String provider) {
	    	        }
	    	        @Override
	    	        public void onLocationChanged(final Location location) {
	    	        	Log.i("***** location changed", "" + location.getLatitude() + " " + location.getLongitude());
	    	        	Log.i("", "");
	    	        }
	    		});
	    
		Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		List<Address> addresses = null;
		
		try {
			addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String str = addresses.get(0).getAddressLine(0);
		Log.i("***** address:", str);
		return str;
	}
	
}