package com.example.huanlu.myruns.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExerciseEntryHelper extends SQLiteOpenHelper {
    public static final String TAG = "ExerciseEntryHelper: ";

    public static final String TABLE_EXERCISE_ENTRIES = "exercise_entries";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_INPUT_TYPE = "mInputType";
    public static final String KEY_ACTIVITY_TYPE = "mActivityType";
    public static final String KEY_DATE_TIME = "mDateTime";
    public static final String KEY_DURATION = "mDuration";
    public static final String KEY_DISTANCE = "mDistance";
    public static final String KEY_AVG_PACE = "mAvgPace";
    public static final String KEY_AVG_SPEED = "mAvgSpeed";
    public static final String KEY_CALORIES = "mCalorie";
    public static final String KEY_CLIMB = "mClimb";
    public static final String KEY_HEARTRATE = "mHeartRate";
    public static final String KEY_COMMENT = "mComment";
    public static final String KEY_PRIVACY = "mPrivacy";
    public static final String KEY_GPS_DATA = "mLocationList";

	private static final String DATABASE_NAME = "exercise_entries.db";
	private static final int DATABASE_VERSION = 3;

    //CREATE TABLE IF NOT EXISTS
	// Database creation sql statement
    public static final String CREATE_TABLE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
            + TABLE_EXERCISE_ENTRIES
            + " ("
            + KEY_ROWID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_INPUT_TYPE
            + " INTEGER NOT NULL, "
            + KEY_ACTIVITY_TYPE
            + " INTEGER NOT NULL, "
            + KEY_DATE_TIME
            + " DATETIME NOT NULL, "
            + KEY_DURATION
            + " INTEGER NOT NULL, "
            + KEY_DISTANCE
            + " FLOAT, "
            + KEY_AVG_PACE
            + " FLOAT, "
            + KEY_AVG_SPEED
            + " FLOAT,"
            + KEY_CALORIES
            + " INTEGER, "
            + KEY_CLIMB
            + " FLOAT, "
            + KEY_HEARTRATE
            + " INTEGER, "
            + KEY_COMMENT
            + " TEXT, "
            + KEY_PRIVACY
            + " INTEGER, "
            + KEY_GPS_DATA
            + " BLOB " + ");";

	public ExerciseEntryHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d(TAG, "onCreate() is called");
        //Log.d(TAG, CREATE_TABLE_ENTRIES);
        database.execSQL(CREATE_TABLE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(ExerciseEntryHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_ENTRIES);
		onCreate(db);
	}

}