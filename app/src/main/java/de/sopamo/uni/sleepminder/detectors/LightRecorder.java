package de.sopamo.uni.sleepminder.detectors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import de.sopamo.uni.sleepminder.AudioView;

public class LightRecorder {
    public boolean sensorExists(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor light =sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(light != null) {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float lux = event.values[0];
                    AudioView.lux = lux;
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            },light,SensorManager.SENSOR_DELAY_NORMAL);
            return true;
        }
        else {
            return false;
        }
    }
}
