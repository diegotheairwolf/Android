package selva.sms;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationActivity extends Service implements LocationListener {
	
	@Override
	 public void onCreate() {
	  //this.subscribeToLocationUpdates();
	 }

	@Override
	public void onLocationChanged(Location loc) {
		// TODO Auto-generated method stub
		
		Log.d("**** listener: ", loc.toString());
		
		
		/*
	     ContentValues values = new ContentValues();
	        values.put(GPSData.GPSPoint.LONGITUDE, loc.getLongitude());
	        values.put(GPSData.GPSPoint.LATITUDE, loc.getLatitude());
	        values.put(GPSData.GPSPoint.TIME, loc.getTime());
	     getContentResolver().insert(GPSDataContentProvider.CONTENT_URI, values);
	    */
		
		Log.d("**** listener result: ", "" + loc.getLatitude() + " " + loc.getLongitude());
		
		Log.d("*** breakpoint", "");
	     
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


}
