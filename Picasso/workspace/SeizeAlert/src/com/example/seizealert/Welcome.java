package com.example.seizealert;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import com.getpebble.android.kit.PebbleKit;
import com.google.common.primitives.UnsignedInteger;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Welcome extends Activity {
	private static final UUID SEIZE_ALERT_APP_UUID = UUID.fromString("5f60123d-353f-421f-9882-1a3a71875a0e");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private PebbleKit.PebbleDataLogReceiver mDataLogReceiver = null;
	private static final int GET_SMS_LOC_REQUEST = 1; //The request code
	private MediaPlayer mp = null;
	private ImageView seize_alert_logo;
	private TextView seize_alert_motto;
	private TextView pebble_status;
	private TextView pebble_app_status;

	// Automatic email/SMS strings
	private static final String username = "seizealert@gmail.com";
	private static final String password = "seizealert1";
	private String alert_heading = new String("SeizeAlert!!!");
	private String alert_start = new String("SeizeAlert has detected that ");
	private String alert_fall_body = new String(" has fallen ");	
	private String alert_seizure_body = new String(" has just had a seizure ");
	private String alert_location = new String("at the following location: http://maps.google.com/?q=");
	private String alert_end = new String(" Please try to communicate with this person " +
			"immediately and make sure he/she is fine.");
	private double Lat = 0.00;
	private double Lng = 0.00;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Open SeizeAlert Application on the Pebble (if available)
		if (PebbleKit.isWatchConnected(this)){
			PebbleKit.startAppOnPebble(this, SEIZE_ALERT_APP_UUID);
		}
		
		//set up notitle 
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
				//Log.i(getLocalClassName(), "Pebble connected!");
			    pebble_status.setText("  Pebble Status: Connected  ");
		 		pebble_status.setBackgroundColor(Color.parseColor("#99FF66"));
			}			  
		});

		PebbleKit.registerPebbleDisconnectedReceiver(getApplicationContext(), new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				//Log.i(getLocalClassName(), "Pebble disconnected!");
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
			unregisterReceiver(mDataLogReceiver);
			mDataLogReceiver = null;
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

				//handler.notify();
				event = SeizeAlert.fromInt(tag.intValue()).getName();
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				String username = prefs.getString("username", "");
				if ( event.equals("fall") ){
					displayAlertMessage("Alert!!!", "Did you fall?");

					// Location SMS
					Intent intentlocationsms = new Intent(context, LocationSMS.class);
					startService(intentlocationsms);

					// Play Audio
					Intent intentplayaudio = new Intent(context, PlayAudio.class);
					startService(intentplayaudio);

					// Email
					// location URL composed as http://maps.google.com/?q=<lat>,<lng>
					sendMail("kratos9000@hotmail.com", alert_heading, alert_start + username + 
							alert_fall_body + alert_location + Lat + "," + Lng + "." + alert_end);

					// SMS
					//sendSMS("5126690402", alert_start + username + alert_fall_body + 
							//alert_location + Lat + "," + Lng + "." + alert_end);

				} else if ( event.equals("seizure") ){
					displayAlertMessage("Alert!!!", "Did you have a seizure?");

					// Location SMS
					Intent intentlocationsms = new Intent(context, LocationSMS.class);
					startService(intentlocationsms);

					// Play Audio
					Intent intentplayaudio = new Intent(context, PlayAudio.class);
					startService(intentplayaudio);

					// Email
					// location URL composed as http://maps.google.com/?q=<lat>,<lng>
					sendMail("kratos9000@hotmail.com", alert_heading, alert_start + username + 
							alert_seizure_body + alert_location + alert_end);

					// SMS
					//sendSMS("5126690402", alert_start + username + alert_seizure_body + 
							//alert_location + Lat + "," + Lng + "." + alert_end);

				} else if ( event.equals("countdown") ){
					displayAlertMessage("Alert!!!", "Is this a false alert?");

					// Play Sound
					Intent intentplaysound = new Intent(context, PlaySound.class);
					startService(intentplaysound);
					
					// Vibrate for 1500 milliseconds
					Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
					v.vibrate(1500);
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
	 * Inputs:
	 * title - a string to be displayed as title of warning box.
	 * mymessage - the message to be displayed on text box.
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
	 * Sends an SMS message to another device
	 */  
	private void sendSMS(String phoneNumber, String message){
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);
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



	/*
	 * Automatic Email Service
	 */
	private void sendMail(String email, String subject, String messageBody) {
		Session session = createSessionObject();

		try {
			Message message = createMessage(email, subject, messageBody, session);
			new SendMailTask().execute(message);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private Message createMessage(String email, String subject, String messageBody, 
			Session session) throws MessagingException, UnsupportedEncodingException {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("SeizeAlert.com", "SeizeAlert Team"));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
		message.setSubject(subject);
		message.setText(messageBody);
		return message;
	}

	private Session createSessionObject() {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		return Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	}

	private class SendMailTask extends AsyncTask<Message, Void, Void> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(Welcome.this, "Please wait", "Sending mail", 
					true, false);
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			progressDialog.dismiss();
		}

		@Override
		protected Void doInBackground(Message... messages) {
			try {
				Transport.send(messages[0]);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}