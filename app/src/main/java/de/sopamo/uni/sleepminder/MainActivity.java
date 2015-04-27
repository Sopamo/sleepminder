package de.sopamo.uni.sleepminder;

import android.app.*;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

import de.sopamo.uni.sleepminder.activities.NightsList;
import de.sopamo.uni.sleepminder.activities.SingleNight;
import de.sopamo.uni.sleepminder.storage.FileHandler;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*LightRecorder light = new LightRecorder();
        Log.e("light", light.sensorExists(getApplicationContext()) + "");*/
        //setContentView(new AudioView(this));

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
        });

        findViewById(R.id.showNight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, SingleNight.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
