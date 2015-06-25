package de.sopamo.uni.sleepminder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import de.sopamo.uni.sleepminder.R;
import de.sopamo.uni.sleepminder.activities.support.NightListAdapter;
import de.sopamo.uni.sleepminder.storage.FileHandler;

public class NightsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nights_list);

        ArrayList<File> nights = new ArrayList<>(Arrays.asList(FileHandler.listFiles()));
        NightListAdapter adapter = new NightListAdapter(this, android.R.layout.simple_list_item_1, nights);

        final ListView listView = (ListView) findViewById(R.id.nightsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                File file = (File)listView.getItemAtPosition(position);

                Intent intent = new Intent(NightsList.this, SingleNight.class);
                intent.putExtra(SingleNight.EXTRA_FILE,file.getAbsolutePath());
                NightsList.this.startActivity(intent);
            }
        });


    }
}
