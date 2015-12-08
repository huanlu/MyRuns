package com.example.huanlu.myruns.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.huanlu.myruns.MapViewActivity;
import com.example.huanlu.myruns.data.ExerciseEntry;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class TrackingService extends Service {
    private static final String TAG = "TrackingService: ";

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;

    private final Messenger mMessenger = new Messenger(
            new IncomingMessageHandler()); // Target we publish for clients to
    // send messages to IncomingHandler.

    private Location currentLocation;
    private Location lastLocation;

    private NotificationManager mNotificationManager;
    // Keeps track of all current registered clients.
    private List<Messenger> mClients = new ArrayList<Messenger>();

    private static boolean isRunning = false;
    private ExerciseEntry entry;

    private LatLng latLng;
    private int dDuration = 0;
    private double dAvgSpeed = 0.0;
    private double dCurSpeed = 0.0;
    private double dClimb = 0.0;
    private int dCalorie = 0;
    private double dDistance = 0.0; //in meters

    private long startTime;
    private long currentTime;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate(): TrackingService Started.");
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand(): Received start id " + startId + ": "
                + intent);

        currentTime = startTime = Calendar.getInstance().getTimeInMillis();

        if(intent == null)
            stopSelf();

        entry = new ExerciseEntry();
        setNotification();startLocationUpdate();
        updateExerciseEntry();

        return START_STICKY; // Run until explicitly stopped.
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "S:onBind() - return mMessenger.getBinder()");

        // getBinder()
        // Return the IBinder that this Messenger is using to communicate with
        // its associated Handler; that is, IncomingMessageHandler().

        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "S:onDestroy():Service Stopped");
        super.onDestroy();
        mNotificationManager.cancelAll(); // Cancel the persistent notification.
        isRunning = false;
    }

    private void setNotification(){
        Intent intent = new Intent(this, MapViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(this.getString(com.example.huanlu.myruns.R.string.tracking_service_label))
                .setContentText(
                        getResources().getString(com.example.huanlu.myruns.R.string.tracking_service_started))
                .setSmallIcon(com.example.huanlu.myruns.R.drawable.dart_d)
                .setContentIntent(contentIntent).build();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags = notification.flags
                | Notification.FLAG_ONGOING_EVENT;
        //notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(0, notification);
    }

    private void startLocationUpdate(){
        LocationManager locationManager;
        String svcName= Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        String provider = locationManager.getBestProvider(criteria, true);

        currentLocation = locationManager.getLastKnownLocation(provider);

        locationManager.requestLocationUpdates(provider, 2000, 1,
                locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged() is called");

            lastLocation = currentLocation;
            currentLocation = location;

            updateExerciseEntry();
            sendMessageToUI();
        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {}
    };

    public static boolean isRunning() {
        return isRunning;
    }

    public static LatLng fromLocationToLatLng(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void updateExerciseEntry(){
        currentTime = Calendar.getInstance().getTimeInMillis();
        dDuration = (int)((currentTime - startTime) / 1000); //in seconds
        entry.setmDuration(dDuration);

        if(currentLocation != null && lastLocation != null)
            dDistance += (double)currentLocation.distanceTo(lastLocation);
        entry.setmDistance(dDistance);

        double distanceInMiles = dDistance / (1000 * 1.60934);
        dAvgSpeed = distanceInMiles / ((double)dDuration / 3600);
        entry.setmAvgPace(dAvgSpeed);
        entry.setmAvgSpeed(dAvgSpeed);

        if(currentLocation != null && lastLocation != null)
            dClimb += (double)(currentLocation.getAltitude() - lastLocation.getAltitude());
        entry.setmClimb(dClimb);

        dCalorie = (int)(dDistance / 15);
        entry.setmCalorie(dCalorie);

        latLng = fromLocationToLatLng(currentLocation);
        entry.getmLocationList().add(latLng);
        Log.d(TAG, entry.getmLocationList().size() + " items in mLocationList");
    }

    private void sendMessageToUI() {
        Log.d(TAG, "S:sendMessageToUI");
        Iterator<Messenger> messengerIterator = mClients.iterator();
        while (messengerIterator.hasNext()) {
            Messenger messenger = messengerIterator.next();
            try {
                Log.d(TAG, "S:TX MSG_LOCATION_UPDATE");

                Message msg = Message.obtain(null, MapViewActivity.MSG_LOCATE_UPDATE);
                msg.setData(createBundleFromExerciseEntry());
                messenger.send(msg);

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list.
                mClients.remove(messenger);
            }
        }
    }

    private Bundle createBundleFromExerciseEntry(){
        Bundle bundle = new Bundle();

        bundle.putInt(MapViewActivity.INT_DURATION, dDuration);
        bundle.putDouble(MapViewActivity.DOUBLE_DISTANCE, dDistance); //distance in meters
        bundle.putDouble(MapViewActivity.DOUBLE_AVERAGE_SPEED, dAvgSpeed);
        bundle.putDouble(MapViewActivity.DOUBLE_CURRENT_SPEED, currentLocation.getSpeed());
        bundle.putDouble(MapViewActivity.DOUBLE_CLIMB, dClimb);
        bundle.putInt(MapViewActivity.INT_CALORIE, dCalorie);
        bundle.putByteArray(MapViewActivity.BYTE_ARRAY_LOCATION,
                entry.getByteArrayFromLocationList());

        return bundle;
    }

    private class IncomingMessageHandler extends Handler { // Handler of

        // incoming messages
        // from clients.
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "S:handleMessage: " + msg.what);
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    Log.d(TAG, "S: RX MSG_REGISTER_CLIENT:mClients.add(msg.replyTo) ");
                    mClients.add(msg.replyTo);

                    Log.d(TAG, "S:TX MSG_LOCATION_UPDATE");
                    updateExerciseEntry();
                    sendMessageToUI();

                    break;
                case MSG_UNREGISTER_CLIENT:
                    Log.d(TAG, "S: RX MSG_REGISTER_CLIENT:mClients.remove(msg.replyTo) ");
                    mClients.remove(msg.replyTo);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
