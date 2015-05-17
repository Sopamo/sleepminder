package de.sopamo.uni.sleepminder;

import android.content.Context;

import de.sopamo.uni.sleepminder.detection.NoiseModel;

public class MyApplication extends android.app.Application {

    public static Context context;
    public static Recorder recorder;
    public static NoiseModel noiseModel;

    @Override
    public void onCreate()
    {
        super.onCreate();

        context = this;

        recorder = new Recorder();
        noiseModel = new NoiseModel();
    }
}
