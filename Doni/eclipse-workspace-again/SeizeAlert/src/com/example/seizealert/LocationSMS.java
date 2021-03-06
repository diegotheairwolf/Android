package com.example.seizealert;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class LocationSMS extends IntentService {
	
		double latitude;
		double longitude;
	
		public int onStartCommand(Intent intent, int flags, int startId) {
			Log.i("here", "SMS Service starts here...");
			Toast.makeText(this, "SMS Sent", Toast.LENGTH_SHORT).show();
		    return super.onStartCommand(intent,flags,startId);
		}

	  /**
	   * A constructor is required, and must call the super IntentService(String)
	   * constructor with a name for the worker thread.
	   */
	  public LocationSMS() {
	      super("LocationSMS");
	  }

	  /**
	   * The IntentService calls this method from the default worker thread with
	   * the intent that started the service. When this method returns, IntentService
	   * stops the service, as appropriate.
	   */
	  @Override
	  protected void onHandleIntent(Intent intent) {
		// Get current address
		//String address = getAddress();
		  
			
		// Get username currently on preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String username = prefs.getString("username", "");
		
		latitude = intent.getDoubleExtra(Welcome.EXTRA_LATITUDE, 0.0);
		longitude = intent.getDoubleExtra(Welcome.EXTRA_LONGITUDE, 0.0);
		
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		List<Address> addresses = null;
			
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		String address = addresses.get(0).getAddressLine(0);
		
			
		// Use the default value if username is null.	
			
		sendSMS("5129445248", "Hello, the patient " + username + 
				" is having a seizure at the location: " + address);
		
			
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
	
	
	
	@Override  
    public void onDestroy() {  
          super.onDestroy();  
          Log.i("here", "SMS Service ends here...");
    }	
}