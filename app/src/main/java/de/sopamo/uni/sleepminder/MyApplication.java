package de.sopamo.uni.sleepminder;

import android.content.Context;

import de.sopamo.uni.sleepminder.lib.Recorder;

public class MyApplication extends android.app.Application {

    public static Context context;
    public static Recorder recorder;

    @Override
    public void onCreate()
    {
        super.onCreate();

        context = this;

        recorder = new Recorder();
    }
}
