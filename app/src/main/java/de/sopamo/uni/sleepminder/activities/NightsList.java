package de.sopamo.uni.sleepminder.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.sopamo.uni.sleepminder.R;
import de.sopamo.uni.sleepminder.storage.FileHandler;

public class NightsList extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nights_list);

        File[] nights = FileHandler.listFiles();

        String[] datesArray = new String[nights.length];

        for(int i = 0; i < nights.length; i++) {
            String filename = nights[i].getName();
            String timestamp = filename.substring(10,20);
            long dv = Long.valueOf(timestamp)*1000;
            Date df = new java.util.Date(dv);
            datesArray[i] = new SimpleDateFormat("dd.MM.y HH:mm").format(df);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, datesArray);

        ListView listView = (ListView) findViewById(R.id.nightsList);
        listView.setAdapter(adapter);


    }
}
