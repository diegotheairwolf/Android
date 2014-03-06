package com.example.seizealert;


import java.io.IOException;

import android.app.IntentService;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;


public class PlayAudio extends IntentService {
    
    final int playTime = 15;
 
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "Emitting Audio", Toast.LENGTH_SHORT).show();
	    return super.onStartCommand(intent,flags,startId);
	}

  /**
   * A constructor is required, and must call the super IntentService(String)
   * constructor with a name for the worker thread.
   */
  public PlayAudio() {
      super("PlayAudio");
  }

  /**
   * The IntentService calls this method from the default worker thread with
   * the intent that started the service. When this method returns, IntentService
   * stops the service, as appropriate.
   */
  @Override
  protected void onHandleIntent(Intent intent) {
	  MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.hotdreams); 
	  mp.start();
	  
	  // Sleep for 5 seconds.
	  long endTime = System.currentTimeMillis() + playTime*1000;
      while (System.currentTimeMillis() < endTime) {
          synchronized (this) {
              try {
                  wait(endTime - System.currentTimeMillis());
              } catch (Exception e) {
              }
          }
      }
      
      mp.stop();
  }
  
  
}