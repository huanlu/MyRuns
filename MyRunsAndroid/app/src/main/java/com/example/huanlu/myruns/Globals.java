package com.example.huanlu.myruns;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.huanlu.myruns.adapter.ActivityEntriesAdapter;
import com.example.huanlu.myruns.data.ExerciseEntriesDataSource;
import com.example.huanlu.myruns.data.ExerciseEntry;
import com.example.huanlu.myruns.gcm.ServerUtilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * Created by huanlu on 2/14/15.
 */
public abstract class Globals {

    // Debugging tag
    public static final String TAG = "Globals: ";

    public static final int ACCELEROMETER_BUFFER_CAPACITY = 2048;
    public static final int ACCELEROMETER_BLOCK_CAPACITY = 64;

    public static final int ACTIVITY_ID_STANDING = 0;
    public static final int ACTIVITY_ID_WALKING = 1;
    public static final int ACTIVITY_ID_RUNNING = 2;
    public static final int ACTIVITY_ID_OTHER = 2;
    public static final int ACTIVITY_ID_UNKNOWN = 3;

    public static final String[] AUTOMATIC_ACTIVITY_TYPES = {"Standing", "Walking", "Running", "Unknown"};

    public static final String KEY_ID = "id";
    public static final String KEY_INPUT_TYPE = "mInputType";
    public static final String KEY_ACTIVITY_TYPE = "mActivityType";
    public static final String KEY_DATE_TIME = "mDateTime";
    public static final String KEY_DURATION = "mDuration";
    public static final String KEY_DISTANCE = "mDistance";
    public static final String KEY_AVG_PACE = "mAvgPace";
    public static final String KEY_AVG_SPEED = "mAvgSpeed";
    public static final String KEY_CALORIES = "mCalorie";
    public static final String KEY_CLIMB = "mClimb";
    public static final String KEY_HEART_RATE = "mHeartRate";
    public static final String KEY_COMMENT = "mComment";
    public static final String KEY_PRIVACY = "mPrivacy";

    public static ExerciseEntriesDataSource datasource;
    public static ActivityEntriesAdapter adapter;

    public static Context context;
    public static Activity activity;

    public static void postMsg(String msg) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... msg) {
                String url = context.getString(R.string.server_addr) + "/post.do";
                String res = "";
                Map<String, String> params = new HashMap<String, String>();
                params.put("msg_string_json", msg[0]);
                try {
                    res = ServerUtilities.post(url, params);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String res) {
            }

        }.execute(msg);
    }

    public static void deleteAllOnServer(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... arg0){
                String url = context.getString(R.string.server_addr) + "/delete.do";
                String res = "";
                Map<String, String> params = new HashMap<String, String>();
                try {
                    res = ServerUtilities.post(url, params);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return res;
            }

            @Override
            protected void onPostExecute(String res) {
            }
        }.execute();
    }

    public static void refreshHistory() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... arg0) {
                final List<ExerciseEntry> values = Globals.datasource.getAllExerciseEntries();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Globals.adapter.clear();
                        Globals.adapter.addAll(values);
                        Globals.adapter.notifyDataSetChanged();
                    }
                });
                return "";
            }

            @Override
            protected void onPostExecute(String res) {
            }

        }.execute();
    }



    public static JSONObject getJsonFromExerciseEntry(ExerciseEntry entry){
        if(entry == null){
            Log.d(TAG, "getJsonFromExerciseEntry: entry == null");
            return null;
        }

        JSONObject json = new JSONObject();

        try {
            json.put(KEY_ID, entry.getId());
            json.put(KEY_INPUT_TYPE, entry.getmInputType());
            json.put(KEY_ACTIVITY_TYPE, entry.getmActivityType());
            json.put(KEY_DATE_TIME, entry.getmDateTime());
            json.put(KEY_DURATION, entry.getmDuration());
            json.put(KEY_DISTANCE, entry.getmDistance());
            json.put(KEY_AVG_PACE, entry.getmAvgPace());
            json.put(KEY_AVG_SPEED, entry.getmAvgSpeed());
            json.put(KEY_CALORIES, entry.getmCalorie());
            json.put(KEY_CLIMB, entry.getmClimb());
            json.put(KEY_HEART_RATE, entry.getmHeartRate());
            json.put(KEY_COMMENT, entry.getmComment());
            json.put(KEY_PRIVACY, entry.getmPrivacy());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}

