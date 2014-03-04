package selva.sms;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.widget.Button;

import android.location.Address;
//*** start ***
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.location.Criteria;
import android.location.Geocoder;
//*** end ***

public class SMSActivity extends Activity {
	
	Button btnSendSMS;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
		btnSendSMS.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				
				
				/*
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
				List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
				*/
				
				String str = getAddress();
				
				sendSMS("5129445248", "Hello, the patient XXXXXXXXX is having a seizure at" +
						"the location: x " + str);
				Log.i("***** breakpoint", " ");
			}
		});
	}
	//---sends an SMS message to another device---
	
	
	
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
	
	
	
	
	static final int GET_ADDRESS_REQUEST = 1;  // The request code

	private void pickContact() {
	    Intent getAddress = new Intent(Intent.ACTION_PICK);
	    getAddress.setType(null); // Show user only contacts w/ phone numbers
	    startActivityForResult(getAddress, GET_ADDRESS_REQUEST);
	}
	
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request it is that we're responding to
	    if (requestCode == GET_ADDRESS_REQUEST) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	        	
	        	
	        	
	        	
	            // Get the URI that points to the selected contact
	            // Uri contactUri = data.getData(); // *** commented out
	            // We only need the NUMBER column, because there will be only one row in the result
	            String[] projection = {Phone.NUMBER};

	            // Perform the query on the contact to get the NUMBER column
	            // We don't need a selection or sort order (there's only one result for the given URI)
	            // CAUTION: The query() method should be called from a separate thread to avoid blocking
	            // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
	            // Consider using CursorLoader to perform the query.
	            Cursor cursor = getContentResolver()
	                    .query(contactUri, projection, null, null, null);
	            cursor.moveToFirst();

	            // Retrieve the phone number from the NUMBER column
	            int column = cursor.getColumnIndex(Phone.NUMBER);
	            String number = cursor.getString(column);

	            // Do something with the phone number...
	        }
	    }
	}
	
	
	
	
	
	
	
	
	

	private void sendSMS(String phoneNumber, String message){
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);
	} 


/*
//Acquire a reference to the system Location Manager
LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

//Define a listener that responds to location updates
LocationListener locationListener = new LocationListener() {
 public void onLocationChanged(Location location) {
   // Called when a new location is found by the network location provider.
   makeUseOfNewLocation(location);
 }

 public void onStatusChanged(String provider, int status, Bundle extras) {}

 public void onProviderEnabled(String provider) {}

 public void onProviderDisabled(String provider) {}
};

//Register the listener with the Location Manager to receive location updates
locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
*/
}