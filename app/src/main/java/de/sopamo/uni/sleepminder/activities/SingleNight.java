package de.sopamo.uni.sleepminder.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.animation.AnimationEasing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.sopamo.uni.sleepminder.R;
import de.sopamo.uni.sleepminder.storage.FileHandler;

public class SingleNight extends AppCompatActivity {

    // Key for the filename
    public static String EXTRA_FILE = "file";

    private File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_night);

        String filename = getIntent().getExtras().getString(EXTRA_FILE);

        setDateTitle(filename);

        // Get the file we want to view from the given path
        file = new File(filename);

        String content = FileHandler.readFile(file);

        String[] parts = content.split(";");
        String start = parts[0];

        CombinedChart chart = (CombinedChart)findViewById(R.id.chart);

        ArrayList<Entry> lightStageEntries = new ArrayList<>();

        ArrayList<BarEntry> sleepStageEntries = new ArrayList<>();

        ArrayList<String> xVals = new ArrayList<>();

        int j = 0;

        int movements = 0;

        // 30 minute intervals
        int[] intervals = new int[(int)Math.ceil(parts.length/300f)];
        int[] lightIntervals = new int[(int)Math.ceil(parts.length/300f)];

        int sleepEvents = 0;
        int movementEvents = 0;
        int snoreEvents = 0;

        int awake = 0;
        int sleep = 0;

        int nightLight = 0;
        int dawnLight = 0;
        int dayLight = 0;

        ArrayList<Integer> lightsIntensities = new ArrayList<>();

        for(int i = 2;i<parts.length;i++) {
            String[] values = parts[i].split(" ");
            j++;
            if(values[1].equals("0")) {
                sleepEvents++;
            }
            if(values[1].equals("1")) {
                snoreEvents++;
            }
            if(values[1].equals("2")) {
                movementEvents++;
                movements++;
            }
            int lightIntensity = Integer.parseInt(values[0]);
            if(lightIntensity <= 20) {
                nightLight++;
            } else if(lightIntensity <= 100) {
                dawnLight++;
            } else {
                dayLight++;
            }

            lightsIntensities.add(lightIntensity);


            if(i % 300 == 0) {
                // Add the movement interval
                if(movements > 1) {
                    intervals[(int) (i / 300f)] = movements;
                    awake++;
                } else {
                    sleep++;
                }
                movements = 0;

                // Add the light interval
                int lightSum = 0;
                for(Integer intensity: lightsIntensities) {
                    lightSum += intensity;
                }
                lightIntervals[(int) (i / 300f)] = lightSum / lightsIntensities.size();
                lightsIntensities.clear();
            }
        }

        int phases = 0;
        boolean isSleeping = false;

        for(int i = 0;i<intervals.length;i++) {
            // Set x value
            long dv = (Long.valueOf(start) + 5 * (i*300)) * 1000;
            Date df = new java.util.Date(dv);
            xVals.add(new SimpleDateFormat("HH:mm").format(df));

            int movementAmount = 0;
            if(intervals[i] > 2) {
                movementAmount = intervals[i];
            }

            sleepStageEntries.add(new BarEntry(movementAmount,i));
            if(movementAmount > 2) {
                if(isSleeping) {
                    phases++;
                    isSleeping = false;
                }
            } else {
                if(!isSleeping) {
                    isSleeping = true;
                }
            }

            lightStageEntries.add(new Entry(lightIntervals[i],i));

        }

        int qualityLight = 1;
        // If one hour of the sleep was during daylight consider the light quality bad
        if(dayLight >= 36000) {
            qualityLight = -1;
        } else if(dawnLight+dayLight >= 54000) {
            // If one hour of the sleep was during dawn, consider the light quality medium
            qualityLight = 0;
        }

        int qualityPhases = 1;
        // Too much phases are no good sign
        if(phases > 10 || phases < 4) {
            qualityPhases = 0;
        }

        int qualitySleep = -1;
        if(parts.length >= 0.1 * 60*60*7) {
            // At least 7 hours of sleep
            qualitySleep = 1;
        } else if(parts.length >= 0.1 * 60*60*5.5) {
            // At least 5.5 hours of sleep
            qualitySleep = 0;
        }

        int averageQuality = (int)((qualityPhases + qualitySleep + qualityLight) / 3f);
        TextView qualityIndicator = (TextView)findViewById(R.id.qualityindicator);
        switch (averageQuality) {
            case -1:
                qualityIndicator.setText("\uD83D\uDE21");
                break;
            case 0:
                qualityIndicator.setText("\uD83D\uDE12");
                break;
            case 1:
                qualityIndicator.setText("☺");
                break;
        }


        BarDataSet sleepStagesSet = new BarDataSet(sleepStageEntries, "Movement");
        sleepStagesSet.setColor(Color.parseColor("#334D5B"));
        sleepStagesSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        sleepStagesSet.setDrawValues(false);

        LineDataSet lightStagesSet = new LineDataSet(lightStageEntries, "Lux value");
        lightStagesSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lightStagesSet.setColor(Color.parseColor("#EDC55B"));
        lightStagesSet.setDrawValues(false);
        lightStagesSet.setCircleColor(Color.parseColor("#EDC55B"));
        lightStagesSet.setCircleColorHole(Color.parseColor("#d7b351"));
        lightStagesSet.setCircleSize(2);
        lightStagesSet.setLineWidth(2);
        lightStagesSet.setFillColor(Color.parseColor("#EDC55B"));
        lightStagesSet.setFillAlpha(80);
        lightStagesSet.setDrawFilled(true);

        LineData lineDates = new LineData();
        lineDates.addDataSet(lightStagesSet);

        BarData barData = new BarData();
        barData.addDataSet(sleepStagesSet);

        CombinedData data = new CombinedData(xVals);

        data.setData(lineDates);
        data.setData(barData);

        chart.setHardwareAccelerationEnabled(true);
        chart.setData(data);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.parseColor("#344D5B"));
        leftAxis.setAxisMaxValue(sleepStagesSet.getYMax());
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTextColor(Color.parseColor("#EDC65C"));
        rightAxis.setAxisMaxValue(lightStagesSet.getYMax());
        rightAxis.setAxisMinValue(0);
        rightAxis.setDrawGridLines(false);

        chart.setDescription("");

        //chart.invalidate(); // refresh
        chart.animateY(1000);

        setupSleepStagesChart(sleep,awake);
        setupSleepEventsChart(sleepEvents,movementEvents,snoreEvents);
        setupLightChart(nightLight, dawnLight, dayLight);


    }

    private void setupSleepStagesChart(int sleep, int awake) {

        PieChart pieChart = (PieChart) findViewById(R.id.sleepstages);

        ArrayList<Entry> pieComp1 = new ArrayList<Entry>();
        Entry c1e1 = new Entry(sleep, 0);
        pieComp1.add(c1e1);
        Entry c1e2 = new Entry(awake, 1);
        pieComp1.add(c1e2);

        PieDataSet pieDataSet = new PieDataSet(pieComp1, "Sleep stages");
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);

        ArrayList<String> xPieVals = new ArrayList<String>();
        xPieVals.add("Deep");
        xPieVals.add("Light");

        PieData pieData = new PieData(xPieVals,pieDataSet);
        pieData.setValueTextSize(14);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);

        Legend pieLegend = pieChart.getLegend();
        pieLegend.setEnabled(false);

        pieChart.setUsePercentValues(true);
        pieChart.setDescription("Sleep stages");
        pieChart.setHardwareAccelerationEnabled(true);
        pieChart.setBackgroundColor(Color.parseColor("#52B19D"));

        pieChart.setHoleColor(Color.parseColor("#52B09C"));

        pieChart.invalidate();
    }

    private void setupSleepEventsChart(int sleep, int movement, int snore) {

        HorizontalBarChart pieChart = (HorizontalBarChart) findViewById(R.id.sleepevents);

        ArrayList<BarEntry> pieComp1 = new ArrayList<BarEntry>();
        BarEntry c1e1 = new BarEntry(sleep, 0);
        pieComp1.add(c1e1);
        BarEntry c1e2 = new BarEntry(movement, 1);
        pieComp1.add(c1e2);
        BarEntry c1e3 = new BarEntry(snore, 1);
        pieComp1.add(c1e3);

        BarDataSet pieDataSet = new BarDataSet(pieComp1, "Sleep events");
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        pieDataSet.setDrawValues(false);


        ArrayList<String> xPieVals = new ArrayList<String>();
        xPieVals.add("None");
        xPieVals.add("Movement");
        xPieVals.add("Snore");

        BarData pieData = new BarData(xPieVals,pieDataSet);
        pieData.setValueTextSize(14);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);

        Legend pieLegend = pieChart.getLegend();
        pieLegend.setEnabled(false);

        pieChart.getAxisLeft().setEnabled(false);
        pieChart.getAxisRight().setEnabled(false);
        pieChart.setGridBackgroundColor(Color.parseColor("#52B19D"));

        pieChart.setDescription("Sleep events");
        pieChart.setHardwareAccelerationEnabled(true);
        pieChart.setDrawBorders(false);

        pieChart.invalidate();
    }



    private void setupLightChart(int night, int dawn, int day) {

        PieChart pieChart = (PieChart) findViewById(R.id.lightquality);

        ArrayList<Entry> pieComp1 = new ArrayList<Entry>();
        Entry c1e1 = new Entry(night, 0);
        pieComp1.add(c1e1);
        Entry c1e2 = new Entry(dawn, 1);
        pieComp1.add(c1e2);
        Entry c1e3 = new Entry(day, 2);
        pieComp1.add(c1e3);

        PieDataSet pieDataSet = new PieDataSet(pieComp1, "Light quality");
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);

        ArrayList<String> xPieVals = new ArrayList<String>();
        xPieVals.add("Night");
        xPieVals.add("Dawn");
        xPieVals.add("Day");

        PieData pieData = new PieData(xPieVals,pieDataSet);
        pieData.setValueTextSize(14);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);

        Legend pieLegend = pieChart.getLegend();
        pieLegend.setEnabled(false);

        pieChart.setUsePercentValues(true);
        pieChart.setDescription("Light quality");
        pieChart.setHardwareAccelerationEnabled(true);

        pieChart.setHoleColor(Color.parseColor("#52B09C"));

        pieChart.invalidate();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.night_list_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(share, "Share Recording"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDateTitle(String filename) {
        String timestamp = filename.substring(filename.length()-14,filename.length()-4);
        long dv = Long.valueOf(timestamp)*1000;
        Date df = new java.util.Date(dv);

        setTitle(new SimpleDateFormat("dd.MM.y HH:mm").format(df));
    }
}
