package de.sopamo.uni.sleepminder;

import android.content.Context;

import de.sopamo.uni.sleepminder.detectors.LightRecorder;
import de.sopamo.uni.sleepminder.storage.FileHandler;

/**
 * This class is the interface for starting and stopping the tracker.
 * Just call start to start tracking and stop when you want to stop tracking.
 *
 */
public class Recorder {

    private LightRecorder lightRecorder = null;
    private String data = "";
    private String startTime = "";

    /**
     * Start tracking
     */
    public void start(Context context) {
        // Set the current timestamp to the data string
        this.data = this.startTime = String.valueOf(System.currentTimeMillis() / 1000L);

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
                // Stop all recorders
                lightRecorder.stop(context);
                // Cleanup
                lightRecorder = null;
            }

            // Write the data to a file
            FileHandler.saveFile(data,"recording-" + startTime + ".txt");
            startTime = "";
        }
    }
}