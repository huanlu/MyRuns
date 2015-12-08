package com.example.huanlu.myruns;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import com.example.huanlu.myruns.data.ExerciseEntriesDataSource;
import com.example.huanlu.myruns.data.ExerciseEntry;

import java.util.Calendar;

public class ListViewActivity extends ListActivity {

    static final String[] MANUAL_ENTRIES = new String[] {
            "Date", "Time", "Duration", "Distance",
            "Calories", "Heart Rate", "Comment" };

    public static final int LIST_ENTRY_ID_DATE = 0;
    public static final int LIST_ENTRY_ID_TIME = 1;
    public static final int LIST_ENTRY_ID_DURATION = 2;
    public static final int LIST_ENTRY_ID_DISTANCE = 3;
    public static final int LIST_ENTRY_ID_CALORIES = 4;
    public static final int LIST_ENTRY_ID_HEARTRATE = 5;
    public static final int LIST_ENTRY_ID_COMMENT = 6;



    private static final String[] TAGS = {"dialog_fragment_tag_photo_picker",
                                          "dialog_fragment_tag_duration",
                                          "dialog_fragment_tag_distance",
                                          "dialog_fragment_tag_calories",
                                          "dialog_fragment_tag_heart_rate",
                                          "dialog_fragment_tag_comment"};

    private Calendar mDateAndTime;
    public ExerciseEntry entry;
    private ExerciseEntriesDataSource datasource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't have to do this anymore
        setContentView(R.layout.activity_list_view);

        mDateAndTime = Calendar.getInstance();
        entry = new ExerciseEntry();
        datasource = new ExerciseEntriesDataSource(this);
        datasource.open();

        // Define a new adapter
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, MANUAL_ENTRIES);

        // Assign the adapter to ListView
        setListAdapter(mAdapter);

        // Define the listener interface
        OnItemClickListener mListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                /*Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText() + " is an awesome prof!",
                        Toast.LENGTH_SHORT).show();*/
                switch (position) {
                    case LIST_ENTRY_ID_DATE :
                        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                mDateAndTime.set(Calendar.YEAR, year);
                                mDateAndTime.set(Calendar.MONTH, monthOfYear);
                                mDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                //updateDateAndTimeDisplay();
                            }
                        };

                        new DatePickerDialog(ListViewActivity.this, mDateListener,
                                mDateAndTime.get(Calendar.YEAR),
                                mDateAndTime.get(Calendar.MONTH),
                                mDateAndTime.get(Calendar.DAY_OF_MONTH)).show();

                        break;

                    case LIST_ENTRY_ID_TIME:
                        TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mDateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                mDateAndTime.set(Calendar.MINUTE, minute);
                            }
                        };

                        new TimePickerDialog(ListViewActivity.this, mTimeListener,
                                mDateAndTime.get(Calendar.HOUR_OF_DAY),
                                mDateAndTime.get(Calendar.MINUTE), true).show();

                        break;

                    case LIST_ENTRY_ID_DURATION:
                        displayDialog(MyDialogFragment.DIALOG_ID_DURATION);
                        break;
                    case LIST_ENTRY_ID_DISTANCE:
                        displayDialog(MyDialogFragment.DIALOG_ID_DISTANCE);
                        break;
                    case LIST_ENTRY_ID_CALORIES:
                        displayDialog(MyDialogFragment.DIALOG_ID_CALORIES);
                        break;
                    case LIST_ENTRY_ID_HEARTRATE:
                        displayDialog(MyDialogFragment.DIALOG_ID_HEART_RATE);
                        break;
                    case LIST_ENTRY_ID_COMMENT:
                        displayDialog(MyDialogFragment.DIALOG_ID_COMMENT);
                        break;
                    default:
                        break;
                }
            }
        };

        // Get the ListView and wired the listener
        ListView listView = getListView();
        listView.setOnItemClickListener(mListener);

    }

    public void displayDialog(int id) {
        DialogFragment fragment = MyDialogFragment.newInstance(id);
        fragment.show(getFragmentManager(), TAGS[id]);
    }

    public void setDuration(int duration) {
        entry.setmDuration(duration);
    }

    public void setDistance(Double distance) {
        entry.setmDistance(distance);
    }

    public void setCalories(int calories) {
        entry.setmCalorie(calories);
    }

    public void setHeartRate(int heartRate) {
        entry.setmHeartRate(heartRate);
    }

    public void setComment(String comment) {
        entry.setmComment(comment);
    }


    public void onManualSaveClicked(View v) {
        int iActivityType = getIntent().getIntExtra(StartFragment.EXTRA_ACTIVITY_TYPE, -1);
        entry.setmActivityType(iActivityType);
        entry.setmDateTime(mDateAndTime.getTimeInMillis());
        //Toast.makeText(getApplicationContext(),
        //        " time is saved!",
        //        Toast.LENGTH_SHORT).show();
        entry = datasource.createExerciseEntry(entry);
        this.finish();
    }

    public void onManualCancelClicked(View v) {
        entry = null;
        this.finish();
    }

    /*@Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }*/

}
