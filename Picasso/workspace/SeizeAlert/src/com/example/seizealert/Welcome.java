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
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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

	// Automatic email vars
	private static final String username = "seizealert@gmail.com";
	private static final String password = "seizealert1";
	private String email_heading = new String("SeizeAlert!!!");
	private String email_start = new String("SeizeAlert has detected that ");
	private String email_fall_body = new String(" has fallen. ");
	private String email_seizure_body = new String(" has just had a seizure. ");
	private String email_end = new String("Please try to communicate with this person immediately and make sure he/she is fine.");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//set up notitle 
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		//set up full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);
		DATE_FORMAT.setTimeZone(TimeZone.getDefault());

		// Sound welcome song
		playSound(R.raw.welcome);
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




	/*
	 ********************* Here starts the Data Logging handlers *********************
	 */

	@Override
	protected void onPause() {
		super.onPause();
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
			public void receiveData(Context context, UUID logUuid, UnsignedInteger timestamp, UnsignedInteger tag,
					UnsignedInteger secondsSinceEpoch) {

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
					sendMail("kratos9000@hotmail.com", email_heading, email_start + username + email_fall_body + email_end);

					// SMS
					//sendSMS("5126690402", "Hello, the patient XXX XXX has fallen at Y Location");

				} else if ( event.equals("seizure") ){
					displayAlertMessage("Alert!!!", "Did you have a seizure?");

					// Location SMS
					Intent intentlocationsms = new Intent(context, LocationSMS.class);
					startService(intentlocationsms);

					// Play Audio
					Intent intentplayaudio = new Intent(context, PlayAudio.class);
					startService(intentplayaudio);

					// Email
					sendMail("kratos9000@hotmail.com", email_heading, email_start + username + email_seizure_body + email_end);

					// SMS
					//sendSMS("5126690402", "Hello, the patient XXX XXX had a seizure at Y Location");

				} else if ( event.equals("countdown") ){
					displayAlertMessage("Alert!!!", "Is this a false alert?");

					// Play Sound
					Intent intentplaysound = new Intent(context, PlaySound.class);
					startService(intentplaysound);
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

	private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
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
			progressDialog = ProgressDialog.show(Welcome.this, "Please wait", "Sending mail", true, false);
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