package com.example.huanlu.myruns;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;


import android.content.Intent;
import android.widget.Button;

import com.example.huanlu.myruns.data.ExerciseEntry;
import com.example.huanlu.myruns.gcm.ServerUtilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartFragment extends Fragment {
    public static final String TAG = "StartFragment: ";

    private Spinner sInputType;
    private Spinner sActivityType;
    public Button bStart;
    private Button bSync;


    public static final String EXTRA_INPUT_TYPE = "extra_input_type";
    public static final String EXTRA_ACTIVITY_TYPE = "extra_activity_type";

    public static final String[] ACTIVITY_TYPES = {"Running", "Walking",
            "Standing", "Cycling", "Hiking", "Downhill Skiing",
            "Cross-Country Skiing", "Snowboarding", "Skating", "Swimming",
            "Mountain Biking", "Wheelchair", "Elliptical", "Other"};

    public static final int RUNNING = 0;
    public static final int WALKING = 1;
    public static final int STANDING = 2;
    public static final int CYCLING = 3;
    public static final int HIKING = 4;
    public static final int DOWNHILL_SKIING = 5;
    public static final int CROSS_COUNTRY_SKIING = 6;
    public static final int SNOWBOARDING = 7;
    public static final int SKATING = 8;
    public static final int SWIMMING = 9;
    public static final int MOUNTAIN_BIKING = 10;
    public static final int WHEELCHAIR = 11;
    public static final int ELLIPTICAL = 12;
    public static final int OTHER = 13;

    public static final int INPUT_TYPE_MANUAL_ENTRY = 0;
    public static final int INPUT_TYPE_GPS = 1;
    public static final int INPUT_TYPE_AUTOMATIC = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        sInputType = (Spinner)v.findViewById(R.id.spinnerInputType);
        sActivityType = (Spinner)v.findViewById(R.id.spinnerActivityType);

        bStart = (Button)v.findViewById(R.id.buttonStart);
        bSync = (Button)v.findViewById(R.id.buttonSync);

        bStart.setOnClickListener(onStartClickListener);
        bSync.setOnClickListener(onSyncClickListener);

        return v;
    }

    private View.OnClickListener onStartClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            int iInputType = sInputType.getSelectedItemPosition();
            int iActivityType = sActivityType.getSelectedItemPosition();
            intent.putExtra(EXTRA_INPUT_TYPE, iInputType);
            intent.putExtra(EXTRA_ACTIVITY_TYPE, iActivityType);
            //invalid when input_type == INPUT_TYPE_AUTOMATIC

            //entry = new ExerciseEntry();
            switch (iInputType) {
                case INPUT_TYPE_MANUAL_ENTRY:
                    intent.setClass(getActivity(), ListViewActivity.class);
                    break;
                case INPUT_TYPE_GPS:
                    intent.setClass(getActivity(), MapViewActivity.class);
                    intent.putExtra(MapViewActivity.EXTRA_FROM_WHERE,
                            MapViewActivity.FROM_START_FRAGMENT);
                    break;
                case INPUT_TYPE_AUTOMATIC:
                    intent.setClass(getActivity(), MapViewActivity.class);
                    intent.putExtra(MapViewActivity.EXTRA_FROM_WHERE,
                            MapViewActivity.FROM_START_FRAGMENT);
                    break;
                default:
                    break;
            }
            startActivity(intent);
        }
    };

    private View.OnClickListener onSyncClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Globals.deleteAllOnServer();
            final List<ExerciseEntry> values = Globals.datasource.getAllExerciseEntries();
            for(ExerciseEntry entry : values){
                JSONObject json = Globals.getJsonFromExerciseEntry(entry);
                Log.d(TAG, "onSyncClickListener: json string = " + json.toString());
                Globals.postMsg(json.toString());
            }

            Globals.refreshHistory();
        }
    };
}