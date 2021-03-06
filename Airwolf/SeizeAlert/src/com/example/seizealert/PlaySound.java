package com.example.seizealert;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PlaySound extends IntentService {
    // originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    // and modified by Steve Pomeroy <steve@staticfree.info>
    private final int duration = 10; // seconds
    private final int sampleRate = 8000;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final double freqOfTone = 440; // hz
    
    int originalVolume;

    private final byte generatedSnd[] = new byte[2 * numSamples];

    Handler handler = new Handler();
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("here", "PlaySound Service starts here...");
		Toast.makeText(this, "Emitting Sound", Toast.LENGTH_SHORT).show();
	    return super.onStartCommand(intent,flags,startId);
	}

  /**
   * A constructor is required, and must call the super IntentService(String)
   * constructor with a name for the worker thread.
   */
  public PlaySound() {
      super("PlaySound");
  }

  /**
   * The IntentService calls this method from the default worker thread with
   * the intent that started the service. When this method returns, IntentService
   * stops the service, as appropriate.
   */
  @Override
  protected void onHandleIntent(Intent intent) {

      // Use a new tread as this can take a while
      final Thread thread = new Thread(new Runnable() {
          public void run() {
              genTone();
              handler.post(new Runnable() {

                  public void run() {
                      playSound();
                  }
              });
          }
      });
      thread.start();
      }
  
  void genTone(){
      // fill out the array
      for (int i = 0; i < numSamples; ++i) {
          sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
      }

      // convert to 16 bit pcm sound array
      // assumes the sample buffer is normalised.
      int idx = 0;
      for (final double dVal : sample) {
          // scale to maximum amplitude
          final short val = (short) ((dVal * 32767));
          // in 16 bit wav PCM, first byte is the low order byte
          generatedSnd[idx++] = (byte) (val & 0x00ff);
          generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

      }
  }

  void playSound(){
	
     final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
             sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
             AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
             AudioTrack.MODE_STATIC);
     audioTrack.write(generatedSnd, 0, generatedSnd.length);
     audioTrack.play();
 
  }

  @Override  
  public void onDestroy() {
      super.onDestroy();  
      Log.i("here", "PlaySound Service ends here...");
  }	
}