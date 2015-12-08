package com.example.huanlu.myruns;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huanlu.myruns.data.ExerciseEntriesDataSource;
import com.example.huanlu.myruns.data.ExerciseEntry;
import com.example.huanlu.myruns.service.SensorsService;
import com.example.huanlu.myruns.service.TrackingService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Calendar;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;


public class MapViewActivity extends FragmentActivity{
    private static final String TAG = "MapViewActivity: ";

    public static final String BYTE_ARRAY_LOCATION = "location";
    public static final String INT_DURATION = "duration";
    public static final String DOUBLE_AVERAGE_SPEED = "avg_speed";
    public static final String DOUBLE_CURRENT_SPEED = "current_speed";
    public static final String DOUBLE_CLIMB = "climb";
    public static final String INT_CALORIE = "calorie";
    public static final String DOUBLE_DISTANCE = "distance";

    public static final String EXTRA_FROM_WHERE = "extra_from_where";
    public static final int FROM_START_FRAGMENT = 0;
    public static final int FROM_NOTIFICATION = 1;
    public static final int FROM_HISTORY_FRAGMENT = 2;

    private long id;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public Marker startMarker;
    public Marker endMarker;

    private final static int SERVICE_ID_TRACKING = 0;
    private final static int SERVICE_ID_SENSORS = 1;

    private Messenger mTrackingServiceMessenger = null;
    private Messenger mSensorsServiceMessenger = null;

    static boolean mIsTrackingBound;
    static boolean mIsSensorBound;
    private boolean showMenu;

    private final Messenger mMessenger = new Messenger(
            new IncomingMessageHandler());

    public static final int MSG_LOCATE_UPDATE = 1;
    public static final int MSG_ACTIVITY_TYPE_UPDATE = 2;

    private ServiceConnection mTrackingConnection;
    private ServiceConnection mSensorConnection;

    private static ExerciseEntry entry;
    private double dCurrentSpeed;

    private TextView mTVDuration;
    private TextView mTVTypeStats;
    private TextView mTVAvgSpeed;
    private TextView mTVCurSpeed;
    private TextView mTVClimb;
    private TextView mTVCalorie;
    private TextView mTVDistance;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(TAG, "C:onCreate");
        setContentView(R.layout.activity_map);

        initTextInformation();
        setUpMapIfNeeded();

        /* Note: If the activity is already running in a task and the activity
         * is not on top of the activity stack in that task, the task will be
         * brought to the foreground. That's all. The Intent will not be delivered.
         * This means when we go back to MapViewActivity from the notification,
         * neither onCreate() nor onNewIntent() will be called
         */
        final int from_where = getIntent().getIntExtra(EXTRA_FROM_WHERE, -1);
        //Toast.makeText(this, "EXTRA_FROM_WHERE = " + from_where, Toast.LENGTH_SHORT).show();

        showMenu = false;

        switch (from_where){
            case FROM_HISTORY_FRAGMENT:
                findViewById(R.id.btnSave).setVisibility(View.INVISIBLE);
                findViewById(R.id.btnCancel).setVisibility(View.INVISIBLE);
                showMenu = true;

                ExerciseEntriesDataSource datasource = new ExerciseEntriesDataSource(this);
                datasource.open();
                id = getIntent().getLongExtra(HistoryFragment.EXTRA_EXERCISE_ENTRY_ID, -1);
                entry = datasource.getExerciseEntryById(id);
                datasource.close();

                updateUIWithNewLocation();
                break;

            case FROM_START_FRAGMENT:

                findViewById(R.id.btnSave).setVisibility(View.VISIBLE);
                findViewById(R.id.btnCancel).setVisibility(View.VISIBLE);

                initExerciseEntry();

                Log.d(TAG, "init connections");
                initConnections();

                Log.d(TAG, "start TrackingService");
                startService(new Intent(MapViewActivity.this, TrackingService.class));
                doBindService(SERVICE_ID_TRACKING);

                if(entry.getmInputType() == StartFragment.INPUT_TYPE_AUTOMATIC) {
                    Log.d(TAG, "start SensorsService");
                    startService(new Intent(MapViewActivity.this, SensorsService.class));
                    doBindService(SERVICE_ID_SENSORS);
                }

                break;

            case FROM_NOTIFICATION:
            default:
                automaticBind();
                break;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(from_where != FROM_HISTORY_FRAGMENT) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateDuration();
                        }
                    });
                }
            }
        }, 0, 1000);

        invalidateOptionsMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "EXTRA_FROM_WHERE = " +
                        getIntent().getIntExtra(EXTRA_FROM_WHERE, -1),
                Toast.LENGTH_SHORT).show();

        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "C:onDestroy()");
        try {
            doUnbindService(SERVICE_ID_TRACKING);
            stopService(new Intent(MapViewActivity.this, TrackingService.class));
            doUnbindService(SERVICE_ID_SENSORS);
            stopService(new Intent(MapViewActivity.this, SensorsService.class));
        } catch (Throwable t) {
            Log.e(TAG, "Failed to unbind from the service", t);
        }
    }

    private void initConnections(){
        mTrackingConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mTrackingServiceMessenger = new Messenger(service);
                //textStatus.setText("Attached.");
                try {
                    Message msg = Message.obtain(null, TrackingService.MSG_REGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    Log.d(TAG, "C: TX MSG_REGISTER_CLIENT");
                    mTrackingServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    // In this case the service has crashed before we could even do
                    // anything with it
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mTrackingServiceMessenger = null;
            }
        };

        mSensorConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mSensorsServiceMessenger = new Messenger(service);
                //textStatus.setText("Attached.");
                try {
                    Message msg = Message.obtain(null, SensorsService.MSG_REGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    Log.d(TAG, "C: TX MSG_REGISTER_CLIENT");
                    mSensorsServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    // In this case the service has crashed before we could even do
                    // anything with it
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mSensorsServiceMessenger = null;
            }
        };
    }

    private void doBindService(int service_id) {
        Log.d(TAG, "C:doBindService(): service_id = " + service_id);
        switch (service_id){
            case SERVICE_ID_TRACKING:
                bindService(new Intent(this, TrackingService.class), mTrackingConnection,
                        Context.BIND_AUTO_CREATE);
                mIsTrackingBound = true;
                break;
            case SERVICE_ID_SENSORS:
                bindService(new Intent(this, SensorsService.class), mSensorConnection,
                        Context.BIND_AUTO_CREATE);
                mIsSensorBound = true;
                break;
            default:
                break;
        }
    }

    /**
     * Un-bind this Activity to TimerService
     */
    private void doUnbindService(int service_id) {
        Log.d(TAG, "C:doUnBindService(): service_id = " + service_id);
        switch (service_id){
            case SERVICE_ID_TRACKING:
                if (mIsTrackingBound) {
                    // If we have received the service, and hence registered with it,
                    // then now is the time to unregister.
                    if (mTrackingServiceMessenger != null) {
                        try {
                            Message msg = Message.obtain(null,
                                    TrackingService.MSG_UNREGISTER_CLIENT);
                            Log.d(TAG, "C: TX MSG_UNREGISTER_CLIENT");
                            msg.replyTo = mMessenger;
                            mTrackingServiceMessenger.send(msg);
                        } catch (RemoteException e) {
                            // There is nothing special we need to do if the service has
                            // crashed.
                        }
                    }
                    // Detach our existing connection.
                    unbindService(mTrackingConnection);
                    mIsTrackingBound = false;
                    //textStatus.setText("Unbinding.");
                }
                break;
            case SERVICE_ID_SENSORS:
                if (mIsSensorBound) {
                    // If we have received the service, and hence registered with it,
                    // then now is the time to unregister.
                    if (mSensorsServiceMessenger != null) {
                        try {
                            Message msg = Message.obtain(null,
                                    SensorsService.MSG_UNREGISTER_CLIENT);
                            Log.d(TAG, "C: TX MSG_UNREGISTER_CLIENT");
                            msg.replyTo = mMessenger;
                            mSensorsServiceMessenger.send(msg);
                        } catch (RemoteException e) {
                            // There is nothing special we need to do if the service has
                            // crashed.
                        }
                    }
                    // Detach our existing connection.
                    unbindService(mSensorConnection);
                    mIsSensorBound = false;
                    //textStatus.setText("Unbinding.");
                }
                break;
            default:
                break;
        }
    }

    /**
     * Check if the service is running. If the service is running when the
     * activity starts, we want to automatically bind to it.
     */
    private void automaticBind() {
        if (TrackingService.isRunning()) {
            Log.d(TAG, "C:TrackingService.isRunning: doBindService()");
            doBindService(SERVICE_ID_TRACKING);
        }
        if (SensorsService.isRunning()) {
            Log.d(TAG, "C:SensorsService.isRunning: doBindService()");
            doBindService(SERVICE_ID_SENSORS);
        }
    }

    private void initExerciseEntry(){
        int input_type = getIntent().getIntExtra(StartFragment.EXTRA_INPUT_TYPE, -1);
        Log.d(TAG, "input_type = " + input_type);
        //Toast.makeText(this, "input_type = " + input_type, Toast.LENGTH_SHORT).show();
        int activity_type = getIntent().getIntExtra(StartFragment.EXTRA_ACTIVITY_TYPE, -1);
        Log.d(TAG, "activity_type = " + activity_type);
        //Toast.makeText(this, "activity_type = " + activity_type, Toast.LENGTH_SHORT).show();

        entry = new ExerciseEntry();
        entry.setmInputType(input_type);
        if(input_type == StartFragment.INPUT_TYPE_AUTOMATIC)
            entry.setmActivityType(Globals.ACTIVITY_ID_UNKNOWN);
        else entry.setmActivityType(activity_type);
        entry.setmDateTime(Calendar.getInstance().getTimeInMillis());
    }

    private void initTextInformation(){
        if(mTVTypeStats == null){
            mTVDuration = (TextView) findViewById(R.id.text_view_time_elapsed);
            mTVDuration.setTextColor(getResources().getColor(R.color.black));

            mTVTypeStats = (TextView) findViewById(R.id.text_view_type_stats);
            mTVTypeStats.setTextColor(getResources().getColor(R.color.black));


            mTVAvgSpeed = (TextView) findViewById(R.id.text_view_avg_speed);
            mTVAvgSpeed.setTextColor(getResources().getColor(R.color.black));

            mTVCurSpeed = (TextView) findViewById(R.id.text_view_cur_speed);
            mTVCurSpeed.setTextColor(getResources().getColor(R.color.black));

            mTVClimb = (TextView) findViewById(R.id.text_view_climb);
            mTVClimb.setTextColor(getResources().getColor(R.color.black));

            mTVCalorie = (TextView) findViewById(R.id.text_view_calorie);
            mTVCalorie.setTextColor(getResources().getColor(R.color.black));

            mTVDistance = (TextView) findViewById(R.id.text_view_distance);
            mTVDistance.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void drawTraceOnMap(){
        Log.d(TAG, "drawTraceOnMap() is called");
        PolylineOptions rectOptions = new PolylineOptions();

        Iterator<LatLng> latLngIterator = entry.getmLocationList().iterator();
        while (latLngIterator.hasNext()) {
            LatLng point = latLngIterator.next();
            rectOptions.add(point);
        }

        rectOptions.color(Color.RED);
        mMap.addPolyline(rectOptions);

        LatLng startPoint = entry.getmLocationList().get(0);
        LatLng endPoint = entry.getmLocationList().get(entry.getmLocationList().size() - 1);

        if(startMarker != null)
            startMarker.remove();
        startMarker = mMap.addMarker(new MarkerOptions().position(startPoint).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        if(endMarker!=null)
            endMarker.remove();
        endMarker = mMap.addMarker(new MarkerOptions().position(endPoint).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endPoint, 17));
    }

    private void updateExerciseEntryWithNewLocation(Bundle data){
        int duration  = (int)((Calendar.getInstance().getTimeInMillis() - entry.getmDateTime())/1000);
        entry.setmDuration(duration);
        entry.setmDistance(data.getDouble(DOUBLE_DISTANCE));
        entry.setmAvgPace(entry.getmDistance()/duration);
        entry.setmAvgSpeed(entry.getmDistance() / duration);
        entry.setmCalorie(data.getInt(INT_CALORIE));
        entry.setmClimb(data.getDouble(DOUBLE_CLIMB));
        entry.setLocationListFromByteArray(data.getByteArray(BYTE_ARRAY_LOCATION));

        dCurrentSpeed = data.getDouble(DOUBLE_CURRENT_SPEED);
    }

    private void updateExerciseEntryWithNewActivityType(int activity_type){
        entry.setmActivityType(activity_type);
    }

    private void updateDuration(){
        int duration  = (int)((Calendar.getInstance().getTimeInMillis() - entry.getmDateTime())/1000);
        entry.setmDuration(duration);
        entry.setmAvgPace(entry.getmDistance() / duration);
        entry.setmAvgSpeed(entry.getmDistance() / duration);

        mTVDuration.setText("Duration: " + entry.getmDuration() + " s");
    }

    private void updateUIWithNewLocation() {
        Log.d(TAG, "updateUIWithNewLocation() is called");
        drawTraceOnMap();

        mTVDuration.setText("Duration: " + entry.getmDuration() + " s");

        if(entry.getmInputType() == StartFragment.INPUT_TYPE_AUTOMATIC)
            mTVTypeStats.setText("Type: " +
                    Globals.AUTOMATIC_ACTIVITY_TYPES[entry.getmActivityType()]);
        else mTVTypeStats.setText("Type: " +
                StartFragment.ACTIVITY_TYPES[entry.getmActivityType()]);

        mTVCurSpeed.setText("Cur speed: " + new Formatter().
                format("%.2f", dCurrentSpeed).toString() + " m/h");

        mTVClimb.setText("Climb: " + new Formatter().
                format("%.2f", entry.getmClimb()).toString() + "Miles");

        double distanceInMiles = entry.getmDistance() / (1000 * 1.60934);
        mTVDistance.setText("Distance: " + new Formatter().
                format("%.2f", distanceInMiles).toString() + "Miles");

        mTVAvgSpeed.setText("Avg speed: " + new Formatter().
                format("%.2f", entry.getmAvgSpeed()).toString() + "m/h");

        mTVCalorie.setText("Calorie: " + entry.getmCalorie());
    }

    private void updateUIWithNewActivityType(){
        mTVTypeStats.setText("Type: " + Globals.AUTOMATIC_ACTIVITY_TYPES[entry.getmActivityType()]);
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            Log.d(TAG, "setUpMapIfNeeded(): ");

            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                // Configure the map display options
            }
        }
    }
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "C:IncomingHandler:handleMessage");
            switch (msg.what) {
                case MSG_LOCATE_UPDATE:
                    Log.d(TAG, "C: RX MSG_LOCATE_UPDATE");
                    updateExerciseEntryWithNewLocation(msg.getData());
                    updateUIWithNewLocation();
                    break;
                case MSG_ACTIVITY_TYPE_UPDATE:
                    Log.d(TAG, "C:RX MSG_ACTIVITY_TYPE_UPDATE");
                    int activity_type = msg.arg1;
                    updateExerciseEntryWithNewActivityType(activity_type);
                    updateUIWithNewActivityType();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void onMapSaveClicked(View v) {
        doUnbindService(SERVICE_ID_TRACKING);
        stopService(new Intent(MapViewActivity.this, TrackingService.class));
        doUnbindService(SERVICE_ID_SENSORS);
        stopService(new Intent(MapViewActivity.this, SensorsService.class));

        ExerciseEntriesDataSource datasource = new ExerciseEntriesDataSource(this);
        datasource.open();
        Log.d(TAG, "insert exercise entry with MAP into db");
        datasource.createExerciseEntry(entry);
        datasource.close();

        this.finish();
    }

    public void onMapCancelClicked(View v) {
        doUnbindService(SERVICE_ID_TRACKING);
        stopService(new Intent(MapViewActivity.this, TrackingService.class));
        doUnbindService(SERVICE_ID_SENSORS);
        stopService(new Intent(MapViewActivity.this, SensorsService.class));
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_view, menu);
        if (!showMenu){
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }
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
            ExerciseEntriesDataSource datasource = new ExerciseEntriesDataSource(this);
            datasource.open();
            datasource.deleteExerciseEntryById(id);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
