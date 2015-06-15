package de.sopamo.uni.sleepminder.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

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

public class SingleNight extends ActionBarActivity {

    // Key for the filename
    public static String EXTRA_FILE = "file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_night);

        // Get the file we want to view from the given path
        File file = new File(getIntent().getExtras().getString(EXTRA_FILE));

        String content = FileHandler.readFile(file);

        String[] parts = content.split(";");
        String start = parts[0];

        LineChart chart = (LineChart)findViewById(R.id.chart);

        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp2 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp3 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp4 = new ArrayList<Entry>();

        ArrayList<String> xVals = new ArrayList<String>();

        int j = 0;

        for(int i = 2;i<parts.length;i++) {
            /*if(parts[i-1].equals(parts[i])) {
                continue;
            }
            String[] values = parts[i-1].split(" ");
            addPoint(values[0],i-1,j,start,xVals,valsComp1);
            addPoint2(values[1], i - 1, j, start, xVals, valsComp2);
            j++;*/
            String[] values = parts[i].split(" ");
            addPoint(values[0], i, j, start, xVals, valsComp1);
            addPoint2(values[1], i, j, start, xVals, valsComp2);
            //addPoint2(values[2], i, j, start, xVals, valsComp3);
            //addPoint2(values[3], i, j, start, xVals, valsComp4);
            j++;
        }


        LineDataSet setComp1 = new LineDataSet(valsComp1, "Light");
        LineDataSet setComp2 = new LineDataSet(valsComp2, "Event");
        ////LineDataSet setComp3 = new LineDataSet(valsComp3, "RLH");
        ////LineDataSet setComp4 = new LineDataSet(valsComp4, "VAR");
        setComp2.setCircleColor(Color.RED);
        setComp2.setColor(Color.RED);

        ////setComp3.setCircleColor(Color.BLUE);
        ////setComp3.setColor(Color.BLUE);

        ////setComp4.setCircleColor(Color.YELLOW);
        ////setComp4.setColor(Color.YELLOW);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);
        //dataSets.add(setComp3);
        //dataSets.add(setComp4);

        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
        chart.invalidate(); // refresh

    }

    private void addPoint(String point, int timeshift, int position, String start, ArrayList<String> xVals, ArrayList<Entry> valsComp1) {
        Entry c1e1 = new Entry(Float.parseFloat(point), position);
        valsComp1.add(c1e1);
        long dv = (Long.valueOf(start) + 5 * timeshift) * 1000;
        Date df = new java.util.Date(dv);
        xVals.add(new SimpleDateFormat("HH:mm").format(df));
    }
    private void addPoint2(String point, int timeshift, int position, String start, ArrayList<String> xVals, ArrayList<Entry> valsComp2) {
        Entry c1e1 = new Entry(Float.parseFloat(point), position);
        valsComp2.add(c1e1);
    }
}
