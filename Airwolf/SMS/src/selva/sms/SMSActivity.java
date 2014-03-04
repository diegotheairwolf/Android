package selva.sms;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.widget.Button;



//import com.google.android.gms.common.GooglePlayServicesUtil;


//*** start ***
import android.location.GpsStatus.Listener;
import android.location.*;
import android.util.Log;
import android.location.Criteria;
//*** end ***

public class SMSActivity extends Activity {
	
	Button btnSendSMS;
	LatLng coor;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// *** start ***
        Log.i("***** start: ", "location attempt *****");

        this.coor = getLocation();	

        Log.i("***** coors: ", "lat: " + coor.lat + "lon: " + coor.lon);

        Log.i("***** end: ", "location attempt *****");
        // *** end ***

		setContentView(R.layout.main);
		btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
		
		btnSendSMS.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				sendSMS("5129445248", "Hello, the patient Andoni Mendoza is having a seizure at" +
						"the location: x");
			}
		});
		
	}
	//---sends an SMS message to another device---

private void sendSMS(String phoneNumber, String message){
	SmsManager sms = SmsManager.getDefault();
	sms.sendTextMessage(phoneNumber, null, message, null, null);
} 

//*** start ***
public LatLng getLocation()
{
    // Get the location manager
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    
    
   // GooglePlayServicesUtil.isGooglePlayServicesAvailable();
    
    
    Log.i("***** status:", "*****");    
   
    List<String> provs = locationManager.getAllProviders();
    
    int index = 0;
    for(int i = 0; i < provs.size(); i++) {
    	Log.i("*** this one: ", provs.get(i));
    	Location location = locationManager.getLastKnownLocation(provs.get(i));
    	if ( location != null) {
    		
    		//Log.i("*****this: ", "" + location.getLatitude() );
    		Log.i("****not null!", "found!");
    		
    	}
    	else {
    		Log.i("*** shit mayne", "****");
    	}
    }
    
    //Location location = locationManager.getLastKnownLocation(provs.get(index));
    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    
    Log.i("***** end", "*****");
    
    
    /*
    Criteria criteria = new Criteria();
    String bestProvider = locationManager.getBestProvider(criteria, false);
    Location location = locationManager.getLastKnownLocation(bestProvider);
    */
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