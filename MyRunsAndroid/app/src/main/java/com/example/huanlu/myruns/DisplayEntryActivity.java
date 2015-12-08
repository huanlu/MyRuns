package com.example.huanlu.myruns;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.huanlu.myruns.data.ExerciseEntriesDataSource;
import com.example.huanlu.myruns.data.ExerciseEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DisplayEntryActivity extends Activity {
    public static final String DATE_TIME_FORMAT = "hh:mm:ss MMM dd yyyy";

    private ExerciseEntriesDataSource datasource;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_entry);

        datasource = new ExerciseEntriesDataSource(this);
        datasource.open();

        id = getIntent().getLongExtra(HistoryFragment.EXTRA_EXERCISE_ENTRY_ID, -1);
        ExerciseEntry entry = datasource.getExerciseEntryById(id);

        EditText mEditText;
        mEditText = (EditText) findViewById(R.id.editTextActivityType);
        mEditText.setText(StartFragment.ACTIVITY_TYPES[entry.getmActivityType()]);

        mEditText = (EditText) findViewById(R.id.editTextDateAndTime);
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(entry.getmDateTime());
        mEditText.setText(formatter.format(calendar.getTime()));

        mEditText = (EditText) findViewById(R.id.editTextDuration);
        //int min = entry.getmDuration() / 60;
        //int sec = entry.getmDuration() % 60;
        mEditText.setText(Integer.toString(entry.getmDuration()) + "mins "
                        + Integer.toString(0) + "secs");

        mEditText = (EditText) findViewById(R.id.editTextDistance);
        mEditText.setText(Double.toString(entry.getmDistance()) + " Miles");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (itemId == R.id.action_delete) {
            datasource.deleteExerciseEntryById(id);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
