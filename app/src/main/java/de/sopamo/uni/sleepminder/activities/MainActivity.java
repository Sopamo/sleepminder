package de.sopamo.uni.sleepminder.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import de.sopamo.uni.sleepminder.MyApplication;
import de.sopamo.uni.sleepminder.R;
import de.sopamo.uni.sleepminder.activities.support.NightListAdapter;
import de.sopamo.uni.sleepminder.RecordingService;
import de.sopamo.uni.sleepminder.storage.FileHandler;

public class MainActivity extends Activity {

    private NightListAdapter nightListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initialize start/stop button
        synchronizeStartButtonState(MyApplication.recorder.isRunning());

        setupNightList();

        findViewById(R.id.toggleRecording).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.recorder.isRunning()) {

                    // Stop the tracking service
                    RecordingService.instance.stopSelf();
                    synchronizeStartButtonState(false);
                    Snackbar
                            .make(findViewById(R.id.main_layout), R.string.recording_complete, Snackbar.LENGTH_SHORT)
                            .show();

                    // Update the nights list
                    updateNightList();

                } else {

                    // Start the tracking service
                    Intent trackingIntent = new Intent(MainActivity.this, RecordingService.class);
                    MainActivity.this.startService(trackingIntent);
                    synchronizeStartButtonState(true);
                    Snackbar
                            .make(findViewById(R.id.main_layout), R.string.recording_started, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

        findViewById(R.id.start_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, AudioTester.class));
            }
        });
    }

    /**
     * Synchronizes the start/stop button image with the current recorder state
     *
     * @param running Decides whether to show the play or stop icon
     */
    private void synchronizeStartButtonState(boolean running) {
        ImageView button = (ImageView) findViewById(R.id.toggleRecording);
        if(running) {
            button.setImageResource(R.drawable.ic_action_stop);
        } else {
            button.setImageResource(R.drawable.ic_action_play);
        }
    }

    private void setupNightList() {
        ArrayList<File> nights = new ArrayList<>(Arrays.asList(FileHandler.listFiles()));
        nightListAdapter = new NightListAdapter(this, android.R.layout.simple_list_item_1, nights);

        final ListView listView = (ListView) findViewById(R.id.nights_list);
        listView.setAdapter(nightListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                File file = (File)listView.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, SingleNight.class);
                intent.putExtra(SingleNight.EXTRA_FILE,file.getAbsolutePath());
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void updateNightList() {
        ArrayList<File> nights = new ArrayList<>(Arrays.asList(FileHandler.listFiles()));
        for(File night:nights) {
            Log.e("foo", night.getPath());
        }
        nightListAdapter.clear();
        nightListAdapter.addAll(nights);
        nightListAdapter.notifyDataSetChanged();
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
