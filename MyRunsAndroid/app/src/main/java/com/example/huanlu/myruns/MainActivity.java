package com.example.huanlu.myruns;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.huanlu.myruns.adapter.ActionTabsViewPagerAdapter;
import com.example.huanlu.myruns.data.ExerciseEntriesDataSource;
import com.example.huanlu.myruns.data.ExerciseEntry;
import com.example.huanlu.myruns.gcm.ServerUtilities;
import com.example.huanlu.myruns.view.SlidingTabLayout;


public class MainActivity extends Activity {
    private static String TAG = "MainActivity: ";
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private ActionTabsViewPagerAdapter myViewPageAdapter;
    //private ExerciseEntriesDataSource datasource;

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console.
     */
    private String SENDER_ID = "972792616543";
    private GoogleCloudMessaging gcm;
    private String regid = "";

    private IntentFilter mMessageIntentFilter;
    private BroadcastReceiver mMessageUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("message");
            if (msg != null && msg.equals("delete")) {
                long id = intent.getLongExtra("id", -1);
                Globals.datasource.deleteExerciseEntryById(id);
                Globals.refreshHistory();
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() is called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Globals.context = getApplicationContext();
        Globals.activity = this;

        Globals.datasource = new ExerciseEntriesDataSource(this);
        Globals.datasource.open();

        mMessageIntentFilter = new IntentFilter();
        mMessageIntentFilter.addAction("GCM_NOTIFY");

        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        if (checkPlayServices()) {
            Log.d(TAG, "checkPlayServices succeed");
            gcm = GoogleCloudMessaging.getInstance(this);
            //regid = getRegistrationId(Globals.context);

            if (regid.isEmpty()) {
                Log.d(TAG, "regid is empty");
                registerInBackground();
            } else { Log.d(TAG, "regid = " + regid); }
        } else {
            Log.d(TAG, "checkPlayServices failed");
        }

        // Define SlidingTabLayout (shown at top)
        // and ViewPager (shown at bottom) in the layout.
        // Get their instances.
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // create a fragment list in order.
        fragments = new ArrayList<Fragment>();
        fragments.add(new StartFragment());
        fragments.add(new HistoryFragment());
        fragments.add(new SettingsFragment());

        // use FragmentPagerAdapter to bind the slidingTabLayout (tabs with different titles)
        // and ViewPager (different pages of fragment) together.
        myViewPageAdapter =new ActionTabsViewPagerAdapter(getFragmentManager(),
                fragments);
        viewPager.setAdapter(myViewPageAdapter);
        // make sure the tabs are equally spaced.
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setOnPageChangeListener(pageChangeListener);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener(){
        @Override
        public void onPageSelected(int position)
        {
            if(position == ActionTabsViewPagerAdapter.HISTORY) {
                Toast.makeText(getApplicationContext(), "onPageSelected gets called", Toast.LENGTH_SHORT).show();

                SharedPreferences myPreference= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                String myUnitPreference = myPreference.getString("list_preference", "Miles");
                Log.d(TAG, "myUnitPreference = " + myUnitPreference);
                if(myUnitPreference.equals(HistoryFragment.STRING_UNIT_PREFERENCE_KILOMETERS))
                    HistoryFragment.unit_preference = HistoryFragment.UNIT_PREFERENCE_KILOMETERS;
                else HistoryFragment.unit_preference = HistoryFragment.UNIT_PREFERENCE_MILES;

                final List<ExerciseEntry> values = Globals.datasource.getAllExerciseEntries();
                Globals.adapter.setUnitPreference(HistoryFragment.unit_preference);
                Globals.adapter.clear();
                Globals.adapter.addAll(values);
                Globals.adapter.notifyDataSetChanged();
            }
        }
        @Override
        public void onPageScrollStateChanged(int state)
        {
        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
        }
    };

    @Override
    protected void onResume() {
        registerReceiver(mMessageUpdateReceiver, mMessageIntentFilter);
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mMessageUpdateReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy(){
        Globals.datasource.close();
        super.onDestroy();
    }


    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(Globals.context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over
                    // HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your
                    // app.
                    // The request to your server should be authenticated if
                    // your app
                    // is using accounts.
                    ServerUtilities.sendRegistrationIdToBackend(Globals.context, regid);

                    // For this demo: we don't need to send it because the
                    // device
                    // will send upstream messages to a server that echo back
                    // the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(Globals.context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, "gcm register msg: " + msg);
            }
        }.execute(null, null, null);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context
     *            application's context.
     * @param regId
     *            registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

}