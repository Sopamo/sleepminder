package de.sopamo.uni.sleepminder.activities.support;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.sopamo.uni.sleepminder.R;

public class NightListAdapter extends ArrayAdapter<File> {

    public NightListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public NightListAdapter(Context context, int resource, List<File> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listrow_nightlist, null);
        }

        File night = getItem(position);

        if (night != null) {

            TextView tt = (TextView) v.findViewById(R.id.recordingStartDatetime);

            // If we have a TextView get the timestamp, convert it to a date and show it in the row
            if (tt != null) {

                String filename = night.getName();
                String timestamp = filename.substring(10,20);
                long dv = Long.valueOf(timestamp)*1000;
                Date df = new java.util.Date(dv);

                tt.setText(new SimpleDateFormat("dd.MM.y HH:mm").format(df));
            }
        }

        return v;

    }
}
