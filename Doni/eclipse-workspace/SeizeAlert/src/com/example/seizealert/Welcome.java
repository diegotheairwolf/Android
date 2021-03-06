package com.example.seizealert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import com.getpebble.android.kit.PebbleKit;
import com.google.common.primitives.UnsignedInteger;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Welcome extends Activity {
	private static final UUID SEIZE_ALERT_APP_UUID = UUID.fromString("5f60123d-353f-421f-9882-1a3a71875a0e");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private PebbleKit.PebbleDataLogReceiver mDataLogReceiver = null;
    private static final int GET_SMS_LOC_REQUEST = 1; //The request code

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
        final Handler handler = new Handler();

        // To receive data logs, Android applications must register a "DataLogReceiver" to receive data.
        //
        // In this example, we're implementing a handler to receive unsigned integer data that was logged by a
        // corresponding watch-app. In the watch-app, three separate logs were created, one per animal. Each log was
        // tagged with a key indicating the animal to which the data corresponds. So, the tag will be used here to
        // look up the animal name when data is received.
        //
        // The data being received contains the seconds since the epoch (a timestamp) of when an ocean faring animal
        // was sighted. The "timestamp" indicates when the log was first created, and will not be used in this example.
        mDataLogReceiver = new PebbleKit.PebbleDataLogReceiver(SEIZE_ALERT_APP_UUID) {
        	String event = new String();
                       
            @Override
            public void receiveData(Context context, UUID logUuid, UnsignedInteger timestamp, UnsignedInteger tag,
            		UnsignedInteger secondsSinceEpoch) {
            	
            	//handler.notify();
            	event = SeizeAlert.fromInt(tag.intValue()).getName();
            	if ( event.equals("fall") ){
            		displayAlertMessage("Alert!!!", "Did you fall?");
            		
            		Intent intent = new Intent(LocationSMS.class);
                    startActivity(intent);
                    
            		sendSMS("5126690402", "Hello, the patient XXX XXX has fallen at Y Location");
            		
            		
            	} else if ( event.equals("seizure") ){
            		displayAlertMessage("Alert!!!", "Did you have a seizure?");
            		sendSMS("5126690402", "Hello, the patient XXX XXX had a seizure at Y Location");
            	}
            	
            	handler.post(new Runnable() {
            		@Override
            		public void run() {
            			//handleSeizureAlert();
            		}
            	});
            }
            
        };

        PebbleKit.registerDataLogReceiver(this, mDataLogReceiver);
        PebbleKit.requestDataLogsForApp(this, SEIZE_ALERT_APP_UUID);
    }

	/*
    private String getUintAsTimestamp(UnsignedInteger uint) {
        return DATE_FORMAT.format(new Date(uint.longValue() * 1000L)).toString();
    }
    */

	private static enum SeizeAlert {
        FALL(0x5),
        SEIZURE(0xd),
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
}