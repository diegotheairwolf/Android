package com.example.seizealert;

import java.util.UUID;

import com.getpebble.android.kit.PebbleKit;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Welcome extends Activity {
	private static final UUID SEIZE_ALERT_APP_UUID = UUID.fromString("5f60123d-353f-421f-9882-1a3a71875a0e");
    private final StringBuilder mDisplayText = new StringBuilder();
    private PebbleKit.PebbleDataLogReceiver mDataLogReceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
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
}