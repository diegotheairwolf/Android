package com.example.seizealert;


import java.io.IOException;

import android.app.IntentService;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;


public class PlayAudio extends IntentService {
    
    final int playTime = 15;
 
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("here", "PlayAudio Service starts here...");
		//Toast.makeText(this, "Emitting Audio", Toast.LENGTH_SHORT).show();
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
	// Set smartphone's volume to the max
	AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	int originalVolume = audioManager.getStreamVolume(audioManager.STREAM_MUSIC);
	//Log.i("here", originalVolume + "   -    ORIGINAL VOLUME BEFORE THE AUDIO");
	int maxVolume = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC);
	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
	//Log.i("here", maxVolume + "    -   AUDIO VOLUME AFTER INCREASING IT");
	  
	 MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.alertmessage); 
	 mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
      
      
     audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
     //Log.i("here", audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + " AUDIO VOLUME AT THE END");
     
     mp.stop();
     mp.reset();
     mp.release();
       
  }
  
  @Override  
  public void onDestroy() {  
        super.onDestroy();
        Log.i("here", "PlayAudio Service ends here...");
  }
}