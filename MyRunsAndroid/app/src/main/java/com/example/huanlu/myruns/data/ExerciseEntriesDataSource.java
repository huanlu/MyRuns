package com.example.huanlu.myruns.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ExerciseEntriesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private ExerciseEntryHelper dbHelper;
	private String[] allColumns = { ExerciseEntryHelper.KEY_ROWID,
                                    ExerciseEntryHelper.KEY_INPUT_TYPE,
                                    ExerciseEntryHelper.KEY_ACTIVITY_TYPE,
                                    ExerciseEntryHelper.KEY_DATE_TIME,
			                        ExerciseEntryHelper.KEY_DURATION,
                                    ExerciseEntryHelper.KEY_DISTANCE,
                                    ExerciseEntryHelper.KEY_AVG_PACE,
                                    ExerciseEntryHelper.KEY_AVG_SPEED,
                                    ExerciseEntryHelper.KEY_CALORIES,
                                    ExerciseEntryHelper.KEY_CLIMB,
                                    ExerciseEntryHelper.KEY_HEARTRATE,
                                    ExerciseEntryHelper.KEY_COMMENT,
                                    ExerciseEntryHelper.KEY_PRIVACY,
                                    ExerciseEntryHelper.KEY_GPS_DATA};

	private static final String TAG = "DB";

	public ExerciseEntriesDataSource(Context context) {
		dbHelper = new ExerciseEntryHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public ExerciseEntry createExerciseEntry(ExerciseEntry entry) {
		ContentValues values = new ContentValues();
		values.put(ExerciseEntryHelper.KEY_INPUT_TYPE, entry.getmInputType());
        values.put(ExerciseEntryHelper.KEY_ACTIVITY_TYPE, entry.getmActivityType());
        values.put(ExerciseEntryHelper.KEY_DATE_TIME, entry.getmDateTime());
        values.put(ExerciseEntryHelper.KEY_DURATION, entry.getmDuration());
        values.put(ExerciseEntryHelper.KEY_DISTANCE, entry.getmDistance());
        values.put(ExerciseEntryHelper.KEY_AVG_PACE, entry.getmAvgPace());
        values.put(ExerciseEntryHelper.KEY_AVG_SPEED, entry.getmAvgSpeed());
        values.put(ExerciseEntryHelper.KEY_CALORIES, entry.getmCalorie());
        values.put(ExerciseEntryHelper.KEY_CLIMB, entry.getmClimb());
        values.put(ExerciseEntryHelper.KEY_HEARTRATE, entry.getmHeartRate());
        values.put(ExerciseEntryHelper.KEY_COMMENT, entry.getmComment());
        values.put(ExerciseEntryHelper.KEY_PRIVACY, entry.getmPrivacy());
        values.put(ExerciseEntryHelper.KEY_GPS_DATA, entry.getByteArrayFromLocationList());

		long insertId = database.insert(ExerciseEntryHelper.TABLE_EXERCISE_ENTRIES, null,
				values);
		Cursor cursor = database.query(ExerciseEntryHelper.TABLE_EXERCISE_ENTRIES,
				allColumns, ExerciseEntryHelper.KEY_ROWID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		ExerciseEntry newEntry = cursorToEntry(cursor);

		// Log the comment stored
		Log.d(TAG, "exercise entry = " + cursorToEntry(cursor).toString()
                + " insert ID = " + insertId);

		cursor.close();
		return newEntry;
	}

    public ExerciseEntry getExerciseEntryById(long id) {
        Cursor cursor = database.query(ExerciseEntryHelper.TABLE_EXERCISE_ENTRIES,
                allColumns, ExerciseEntryHelper.KEY_ROWID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        ExerciseEntry newEntry = cursorToEntry(cursor);

        // Log the comment stored
        Log.d(TAG, "get exercise entry = " + cursorToEntry(cursor).toString()
                + " with ID = " + id);

        cursor.close();
        return newEntry;
    }

	public void deleteExerciseEntryById(long id) {
		Log.d(TAG, "delete exercise entry = " + id);
		System.out.println("exercise entry deleted with id: " + id);
		database.delete(ExerciseEntryHelper.TABLE_EXERCISE_ENTRIES, ExerciseEntryHelper.KEY_ROWID
				+ " = " + id, null);
	}
	
	public List<ExerciseEntry> getAllExerciseEntries() {
		List<ExerciseEntry> entries = new ArrayList<ExerciseEntry>();

		Cursor cursor = database.query(ExerciseEntryHelper.TABLE_EXERCISE_ENTRIES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
            ExerciseEntry entry = cursorToEntry(cursor);
			Log.d(TAG, "get entry = " + cursorToEntry(cursor).toString());
			entries.add(entry);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return entries;
	}

	private ExerciseEntry cursorToEntry(Cursor cursor) {
        ExerciseEntry entry = new ExerciseEntry();
        entry.setId(cursor.getLong(0));
        entry.setmInputType(cursor.getInt(1));
        entry.setmActivityType(cursor.getInt(2));
        entry.setmDateTime(cursor.getLong(3));
        entry.setmDuration(cursor.getInt(4));
        entry.setmDistance(cursor.getDouble(5));
        entry.setmAvgPace(cursor.getDouble(6));
        entry.setmAvgSpeed(cursor.getDouble(7));
        entry.setmCalorie(cursor.getInt(8));
        entry.setmClimb(cursor.getDouble(9));
        entry.setmHeartRate(cursor.getInt(10));
        entry.setmComment(cursor.getString(11));
        entry.setmPrivacy(cursor.getInt(12));
        entry.setLocationListFromByteArray(cursor.getBlob(13));

		return entry;
	}
}