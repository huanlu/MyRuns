package com.example.huanlu.myruns.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.huanlu.myruns.DisplayEntryActivity;
import com.example.huanlu.myruns.Globals;
import com.example.huanlu.myruns.HistoryFragment;
import com.example.huanlu.myruns.StartFragment;
import com.example.huanlu.myruns.data.ExerciseEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;

/**
 * Created by huanlu on 2/2/15.
 */
public class ActivityEntriesAdapter extends ArrayAdapter<ExerciseEntry> {
    View v;
    int unit_preference;

    public ActivityEntriesAdapter(Context context, List<ExerciseEntry> values, int unit_preference) {
        super(context, android.R.layout.two_line_list_item, values);
        this.unit_preference = unit_preference;
    }

    public void setUnitPreference(int unit_preference){
        this.unit_preference = unit_preference;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.v = convertView;
        if(v == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(android.R.layout.two_line_list_item, null);
        }

        TextView textView1 = (TextView)v.findViewById(android.R.id.text1);
        TextView textView2 = (TextView)v.findViewById(android.R.id.text2);

        SimpleDateFormat formatter = new SimpleDateFormat(DisplayEntryActivity.DATE_TIME_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getItem(position).getmDateTime());

        ExerciseEntry entry = getItem(position);
        String sActivityType;
        if (entry.getmInputType() == StartFragment.INPUT_TYPE_AUTOMATIC) {
            sActivityType = Globals.AUTOMATIC_ACTIVITY_TYPES[entry.getmActivityType()];
        } else {
            sActivityType = StartFragment.ACTIVITY_TYPES[entry.getmActivityType()];
        }
        textView1.setText(sActivityType + ", " + formatter.format(calendar.getTime()));

        Double distanceInKilo = getItem(position).getmDistance() / 1000;
        if(unit_preference == HistoryFragment.UNIT_PREFERENCE_MILES) {
            double miles = distanceInKilo / 1.60934;
            textView2.setText(new Formatter().format("%.2f", miles).toString() + " Miles, "
                    + Integer.toString(getItem(position).getmDuration()) + "mins "
                    + Integer.toString(0) + "secs");
        } else {
            textView2.setText(new Formatter().format("%.2f", distanceInKilo).toString() + " Kilometers, "
                    + Integer.toString(getItem(position).getmDuration()) + "mins "
                    + Integer.toString(0) + "secs");
        }

        return v;
    }

}
