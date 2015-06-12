package de.sopamo.uni.sleepminder.lib.recorders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class LightRecorder {

    private Float currentLux = null;
    private SensorEventListener sensorListener = null;
    private Context context = null;

    /**
     * Start tracking the brightness
     *
     * @param context
     * @return boolean if the sensor was registered correctly
     */
    public boolean start(Context context) {

        this.context = context;

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                    // Restart light tracking in 2 seconds
                    new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                LightRecorder.this.registerSensorListener(LightRecorder.this.context);
                            }
                        },
                        2000);

                }
            }
        }, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        return registerSensorListener(context);
    }

    /**
     * Registers a new light sensor listener
     * Registers a new one if one is already active
     *
     * @param context
     * @return
     */
    private boolean registerSensorListener(Context context) {

        // Get the light sensor
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if(light != null) {

            // We already have an active sensorListener -> Remove it first and register a new one
            if(sensorListener != null) {
                sensorManager.unregisterListener(sensorListener);
            }

            // Create & register the sensor
            sensorListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    LightRecorder.this.currentLux = event.values[0];
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            return sensorManager.registerListener(sensorListener, light, SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            return false;
        }
    }

    /**
     * Returns the current lux
     *
     * @return
     */
    public Float getCurrentLux() {
        return currentLux;
    }

    /**
     * Stop listening for light changes
     *
     * @param context
     */
    public void stop(Context context) {

        // Make sure we don't have any filled variables hanging around
        this.currentLux = null;

        // If we don't have a listener active we don't nee to unregister it
        if(this.sensorListener == null) return;

        // Unregister the listener
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(sensorListener);

        this.sensorListener = null;
    }
}
