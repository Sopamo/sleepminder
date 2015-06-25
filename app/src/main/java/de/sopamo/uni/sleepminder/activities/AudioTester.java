package de.sopamo.uni.sleepminder.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import de.sopamo.uni.sleepminder.AudioView;


public class AudioTester extends ActionBarActivity {

    AudioView audioView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*LightRecorder light = new LightRecorder();
        Log.e("light", light.sensorExists(getApplicationContext()) + "");*/

        /*
        setContentView(R.layout.activity_main);


        File[] files = FileHandler.listFiles();
        for(int i = 0;i<files.length;i++) {
            File file = files[i];
            Log.e("foo", FileHandler.readFile(file));
        }

        findViewById(R.id.showNotification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the tracking service
                Intent trackingIntent = new Intent(MainActivity.this, RecordingService.class);
                MainActivity.this.startService(trackingIntent);

                //MainActivity.this.startActivity(new Intent(MainActivity.this, SingleNight.class));
            }
        });

        findViewById(R.id.showList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, NightsList.class));
            }
        });

        findViewById(R.id.stopRecording).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordingService.instance.stopSelf();
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        audioView = new AudioView(this);
        setContentView(audioView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        audioView.stop();
    }
}
