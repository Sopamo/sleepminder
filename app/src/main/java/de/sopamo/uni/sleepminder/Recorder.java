package de.sopamo.uni.sleepminder;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import de.sopamo.uni.sleepminder.recorders.AudioRecorder;
import de.sopamo.uni.sleepminder.recorders.LightRecorder;
import de.sopamo.uni.sleepminder.storage.FileHandler;

/**
 * This class is the interface for starting and stopping the tracker.
 * Just call start to start tracking and stop when you want to stop tracking.
 *
 */
public class Recorder {

    private LightRecorder lightRecorder = null;
    private AudioRecorder audioRecorder = null;
    private String data = "";
    private String startTime = "";
    private PowerManager.WakeLock wakeLock;
    private boolean running = false;

    /**
     * Start tracking
     */
    public void start(Context context) {
        running = true;

        // Set the current timestamp to the data string
        this.data = this.startTime = String.valueOf(System.currentTimeMillis() / 1000L);
        this.data += ";";

        lightRecorder = new LightRecorder();
        lightRecorder.start(context);

        audioRecorder = new AudioRecorder();
        audioRecorder.run();

        PowerManager mgr = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SleepMinderLock");
        wakeLock.acquire();

        // Add the lux information every 5 seconds
        final android.os.Handler customHandler = new android.os.Handler();
        Runnable updateTimerThread = new Runnable()
        {
            public void run()
            {
                synchronized (Recorder.this) {
                    if (lightRecorder != null) {

                        // We have to check if we already have a "current" lux. In the first call we might not have gotten a sensor change event.
                        if(lightRecorder.getCurrentLux() != null) {
                            data += String.valueOf(lightRecorder.getCurrentLux().intValue());
                        } else {
                            data += "-1";
                            Log.e("foo","current lux null");
                        }
                        if(MyApplication.noiseModel != null) {
                            data += " " + String.valueOf(MyApplication.noiseModel.getEvent());
                        } else {
                            data += " " + "-1";
                            Log.e("foo","Noise model not initiated");
                            Toast toast = Toast.makeText(MyApplication.context, "Noise model not initiated", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        data += ";";
                        // Dump the data to the text file if we accumulated "enough"
                        if(data.length() > 20) {
                            dumpData();
                        }

                        customHandler.postDelayed(this, 5000);
                    } else {
                        Log.e("foo","light recorder null");
                    }
                }
            }
        };

        customHandler.postDelayed(updateTimerThread, 0);
    }

    /**
     * Stop tracking
     */
    public void stop(Context context) {
        synchronized (this) {
            if (lightRecorder != null) {
                // Stop all recorders
                lightRecorder.stop(context);
                // Cleanup
                lightRecorder = null;
            }

            wakeLock.release();

            // Write the data to a file
            dumpData();
            startTime = "";
            running = false;
        }
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Dumps the currently accumulated data in the textfile
     */
    private void dumpData() {
        FileHandler.saveFile(data,"recording-" + startTime + ".txt");
        data = "";
    }
}