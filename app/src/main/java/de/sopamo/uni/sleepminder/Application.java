package de.sopamo.uni.sleepminder;

import android.content.Context;

public class Application extends android.app.Application {

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
