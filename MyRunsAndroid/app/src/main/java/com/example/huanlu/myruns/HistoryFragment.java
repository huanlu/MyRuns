package com.example.huanlu.myruns;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.huanlu.myruns.adapter.ActivityEntriesAdapter;
import com.example.huanlu.myruns.data.ExerciseEntriesDataSource;
import com.example.huanlu.myruns.data.ExerciseEntry;

import java.util.List;

public class HistoryFragment extends Fragment {
    private static String TAG = "HistoryFragment: ";

    public static String STRING_UNIT_PREFERENCE_MILES = "Miles";
    public static String STRING_UNIT_PREFERENCE_KILOMETERS = "Kilometers";

    public static int UNIT_PREFERENCE_MILES = 0;
    public static int UNIT_PREFERENCE_KILOMETERS = 1;

    public static final String EXTRA_EXERCISE_ENTRY_ID = "_id";

    public static int unit_preference = UNIT_PREFERENCE_MILES;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        final List<ExerciseEntry> values = Globals.datasource.getAllExerciseEntries();


        ListView mListView = (ListView) view.findViewById(R.id.historyList);

        Globals.adapter = new ActivityEntriesAdapter(getActivity(), values, unit_preference);
        mListView.setAdapter(Globals.adapter);

        AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                //Toast.makeText(getApplicationContext(),
                //        ((TextView) view).getText() + " is an awesome prof!",
                //        Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                i.putExtra(EXTRA_EXERCISE_ENTRY_ID, values.get(position).getId());
                Log.d(TAG, "id = " + values.get(position).getId());
                if(values.get(position).getmInputType() == StartFragment.INPUT_TYPE_MANUAL_ENTRY) {
                    i.setClass(getActivity(), DisplayEntryActivity.class);
                    startActivity(i);
                }
                else{
                    i.setClass(getActivity(), MapViewActivity.class);
                    i.putExtra(MapViewActivity.EXTRA_FROM_WHERE,
                            MapViewActivity.FROM_HISTORY_FRAGMENT);
                    startActivity(i);
                }
            }
        };

        mListView.setOnItemClickListener(mListener);

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume() is called");

        final List<ExerciseEntry> values = Globals.datasource.getAllExerciseEntries();
        Globals.adapter.clear();
        Globals.adapter.addAll(values);
        Globals.adapter.notifyDataSetChanged();
    }
}
