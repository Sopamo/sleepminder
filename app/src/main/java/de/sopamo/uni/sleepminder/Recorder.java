package de.sopamo.uni.sleepminder;

import android.content.Context;
import android.util.Log;

import de.sopamo.uni.sleepminder.detectors.LightRecorder;

/**
 * This class is the interface for starting and stopping the tracker.
 * Just call start to start tracking and stop when you want to stop tracking.
 *
 */
public class Recorder {

    private LightRecorder lightRecorder = null;
    private String data = "";

    /**
     * Start tracking
     */
    public void start(Context context) {
        // Set the current timestamp to the data string
        this.data = String.valueOf(System.currentTimeMillis() / 1000L);

        lightRecorder = new LightRecorder();
        lightRecorder.start(context);

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
                            data += " " + String.valueOf(lightRecorder.getCurrentLux().intValue());
                            Log.e("foo", data);
                        }
                        customHandler.postDelayed(this, 5000);
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
                lightRecorder.stop(context);
                // Cleanup
                lightRecorder = null;
            }
        }
    }
}