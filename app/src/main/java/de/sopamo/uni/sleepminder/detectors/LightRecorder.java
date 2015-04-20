package de.sopamo.uni.sleepminder.detectors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import de.sopamo.uni.sleepminder.AudioView;

public class LightRecorder {

    private Float currentLux = null;
    private SensorEventListener sensorListener = null;

    /**
     * Start tracking the brightness
     *
     * @param context
     * @return
     */
    public boolean start(Context context) {

        // Get the light sensor
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Check if we have a light sensor and if we do start tracking
        if(light != null) {
            sensorListener =
                    new SensorEventListener() {
                        @Override
                        public void onSensorChanged(SensorEvent event) {
                            LightRecorder.this.currentLux = event.values[0];
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int accuracy) {

                        }
                    };
            sensorManager.registerListener(sensorListener,light,SensorManager.SENSOR_DELAY_NORMAL);
            return true;
        }
        else {
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
