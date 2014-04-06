/*
* SeizeAlert - A Seizure Notification and Detection System.
*
* Copyright © 2014 Pablo S. Campos, Diego Moreno, and Andoni Mendoza.
*
* No part of this website may be reproduced without Pablo S. Campos's express consent.
*
*/


package com.example.seizealert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;

import com.example.seizealert.GPSTracker;
import com.getpebble.android.kit.PebbleKit;
import com.google.common.primitives.UnsignedInteger;

import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

public class Welcome extends Activity {
	
	private static final UUID SEIZE_ALERT_APP_UUID = UUID.fromString("5f60123d-353f-421f-9882-1a3a71875a0e");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private PebbleKit.PebbleDataLogReceiver mDataLogReceiver = null;
	private MediaPlayer mp = null;
	private long[] vibPattern = { 0, 500, 500,
									 500, 500,
									 500, 500,
									 500, 500,
									 500, 500,
									 500, 500,
									 500, 500,
									 500, 500,
									 500, 500,
									 500, 500};
	
	// Welcome activity text views and images
	private ImageView seize_alert_logo;
	private TextView seize_alert_motto;
	private TextView pebble_status;
	private TextView pebble_app_status;
	private UnsignedInteger last_timestamp;
	
	// Email Service Class
	EmailService email;
	
	// GPSTracker Class
	GPSTracker gps;
	public final static String EXTRA_LATITUDE = "com.example.seizealert.LATITUDE";
	public final static String EXTRA_LONGITUDE = "com.example.seizealert.LONGITUDE";
	double latitude;
	double longitude;

	// Automated email/SMS strings
	private String alert_heading = new String("SeizeAlert!!!");
	private String alert_start = new String("SeizeAlert has detected that ");
	private String alert_fall_body = new String(" has fallen ");	
	private String alert_seizure_body = new String(" has just had a seizure ");
	private String alert_location = new String("at the following location: http://maps.google.com/?q=");
	private String alert_end = new String(" Please try to communicate with this person " +
			"immediately and make sure he/she is fine.");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Open SeizeAlert Application on the Pebble (if available)
		if (PebbleKit.isWatchConnected(this)){
			PebbleKit.startAppOnPebble(this, SEIZE_ALERT_APP_UUID);
		}
		
		//set up no-title 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//set up full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);
		DATE_FORMAT.setTimeZone(TimeZone.getDefault());

		// Sound welcome song
		playSound(R.raw.welcome);
		
		// Update status of Pebble (connected/disconnected)
		pebble_status = (TextView) findViewById(R.id.pebble_status);
		if (PebbleKit.isWatchConnected(this)){
	 		pebble_status.setText("  Pebble Status: Connected  ");
	 		pebble_status.setBackgroundColor(Color.parseColor("#99FF66"));
	 	} else {
	 		pebble_status.setText("  Pebble Status: Disconnected  ");
	 		pebble_status.setBackgroundColor(Color.parseColor("#FF9966"));
	 	}
		
		PebbleKit.registerPebbleConnectedReceiver(getApplicationContext(), new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
			    pebble_status.setText("  Pebble Status: Connected  ");
		 		pebble_status.setBackgroundColor(Color.parseColor("#99FF66"));
			}			  
		});

		PebbleKit.registerPebbleDisconnectedReceiver(getApplicationContext(), new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				pebble_status.setText("  Pebble Status: Disconnected  ");
				pebble_status.setBackgroundColor(Color.parseColor("#FF9966"));
			}
		});
	 	
	 	// Logo & motto fade in
	 	Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
	 	Animation animationFadeInSlower = AnimationUtils.loadAnimation(this, R.anim.fadein_slower);
	 	seize_alert_logo = (ImageView)findViewById(R.id.seize_alert_logo);
	 	seize_alert_motto = (TextView)findViewById(R.id.seize_alert_motto);
	 	pebble_app_status = (TextView)findViewById(R.id.pebble_app_status);

	 	seize_alert_logo.startAnimation(animationFadeIn);
	 	seize_alert_motto.startAnimation(animationFadeInSlower);
	 	pebble_status.startAnimation(animationFadeIn);
	 	pebble_app_status.startAnimation(animationFadeIn);
	 		 	
	 	// create class object
        gps = new GPSTracker(Welcome.this);
	 	if(gps.canGetLocation()){        	
        	latitude = gps.getLatitude();
        	longitude = gps.getLongitude();
        	
        	// \n is for new line
        	Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + 
        											"\nLong: " + longitude, Toast.LENGTH_LONG).show();	
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
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	
	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			this.displaySettings(item.getActionView());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	public void displaySettings(View view ) {
		Intent intent = new Intent(this, Settings.class);
		startActivity(intent);
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		// Stop animation if application goes in background
		seize_alert_logo.clearAnimation();
		seize_alert_motto.clearAnimation();
		pebble_status.clearAnimation();
		
		// Handle Pebble data logging
		if (mDataLogReceiver != null) {
			//unregisterReceiver(mDataLogReceiver);
			//mDataLogReceiver = null;
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		mDataLogReceiver = new PebbleKit.PebbleDataLogReceiver(SEIZE_ALERT_APP_UUID) {
			String event = new String();

			@Override
			public void receiveData(Context context, UUID logUuid, UnsignedInteger timestamp, 
					UnsignedInteger tag, UnsignedInteger secondsSinceEpoch) {

				if (timestamp != last_timestamp){	// Check if this is new data
					last_timestamp = timestamp;
					event = SeizeAlert.fromInt(tag.intValue()).getName();
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
					String username = prefs.getString("username", "");
					gps = new GPSTracker(Welcome.this);
					email = new EmailService(Welcome.this);

					if(gps.canGetLocation()){		// check if GPS enabled	
						latitude = gps.getLatitude();
						longitude = gps.getLongitude();

						// \n is for new line
						Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + 
								"\nLong: " + longitude, Toast.LENGTH_LONG).show();	

					}else{
						// can't get location
						// GPS or Network is not enabled
						// Ask user to enable GPS/network in settings
						gps.showSettingsAlert();
					}

					if ( event.equals("fall") ){
						displayAlertMessage("SeizeAlert!!!", "A notification has been sent to all of your contacts.");

						// Email
						// location URL composed as http://maps.google.com/?q=<lat>,<lng>
						email.sendMail("seizealert@gmail.com", alert_heading, alert_start + username + 
								alert_fall_body + alert_location + latitude + "," + longitude + "." + alert_end);

						/*
							String message = alert_start + username + alert_seizure_body + 
								alert_location + latitude + "," + longitude + ". " + alert_end;
						*/

						// Location SMS
						Intent intentlocationsms = new Intent(context, LocationSMS.class);
						// TODO: instead of passing lat and lng, pass one string (the entire message
						// as we are doing on the email. That will make thinks easier.
						intentlocationsms.putExtra(EXTRA_LATITUDE, latitude);
						intentlocationsms.putExtra(EXTRA_LONGITUDE, longitude);
						startService(intentlocationsms);

						// Play Audio
						Intent intentplayaudio = new Intent(context, PlayAudio.class);
						startService(intentplayaudio);



					} else if ( event.equals("seizure") ){
						displayAlertMessage("SeizeAlert!!!", "A notification has been sent to all of your contacts.");

						// Email
						// location URL composed as http://maps.google.com/?q=<lat>,<lng>
						email.sendMail("seizealert@gmail.com", alert_heading, alert_start + username + 
								alert_seizure_body + alert_location + latitude + "," + longitude + "." + alert_end);

						/*
							String message = alert_start + username + alert_seizure_body + 
								alert_location + latitude + "," + longitude + ". " + alert_end;
						*/

						// Location SMS
						Intent intentlocationsms = new Intent(context, LocationSMS.class);
						// TODO: instead of passing lat and lng, pass one string (the entire message
						// as we are doing on the email. That will make thinks easier.
						intentlocationsms.putExtra(EXTRA_LATITUDE, latitude);
						intentlocationsms.putExtra(EXTRA_LONGITUDE, longitude);
						startService(intentlocationsms);

						// Play Audio
						Intent intentplayaudio = new Intent(context, PlayAudio.class);
						startService(intentplayaudio);



					} else if ( event.equals("countdown") ){
						displayAlertMessage("Alert!!!", "Is this a false alert? Please press the SELECT button on your Pebble.");

						// Play Sound for 10 seconds
						Intent intentplaysound = new Intent(context, PlaySound.class);
						startService(intentplaysound);

						// Vibrate for 10000 milliseconds - 10 seconds
						Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
						v.vibrate(vibPattern, -1);
					}
				}		
			}            
		};

		PebbleKit.registerDataLogReceiver(this, mDataLogReceiver);
		PebbleKit.requestDataLogsForApp(this, SEIZE_ALERT_APP_UUID);
	}


	private static enum SeizeAlert {
		FALL(0x5),
		SEIZURE(0xd),
		COUNTDOWN(0xe),
		UNKNOWN(0xff);

		public final int id;

		private SeizeAlert(final int id) {
			this.id = id;
		}

		public static SeizeAlert fromInt(final int id) {
			for (SeizeAlert event : values()) {
				if (event.id == id) {
					return event;
				}
			}
			return UNKNOWN;
		}

		public String getName() {
			return name().toLowerCase();
		}
	}    


	/*
	 * This function displays an alert message!!!
	 */
	public void displayAlertMessage(String title, String mymessage) {
		new AlertDialog.Builder(this)
		.setMessage(mymessage)
		.setTitle(title)
		.setCancelable(true)
		.setNeutralButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton){}
		})
		.show();
	}
	

	/*
	 * Plays a Sound
	 */    
	private void playSound(int sFile){
		//set up MediaPlayer   
		final int medFile = sFile;

		Thread thread = new Thread(new Runnable() {

			public void run() {
				mp = MediaPlayer.create(getApplicationContext(), medFile);
				mp.start();
			}
		});
		thread.start();
	}
}