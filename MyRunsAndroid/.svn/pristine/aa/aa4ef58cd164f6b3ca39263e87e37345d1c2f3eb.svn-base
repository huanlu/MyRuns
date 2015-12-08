package com.example.huanlu.myruns.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.huanlu.myruns.Globals;
import com.example.huanlu.myruns.MapViewActivity;
import com.example.huanlu.myruns.ml.FFT;
import com.example.huanlu.myruns.ml.WekaClassifier;

public class SensorsService extends Service implements SensorEventListener {
    private final static String TAG = "SensorsService: ";

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private OnSensorChangedTask mAsyncTask;

    private static ArrayBlockingQueue<Double> mAccBuffer;

    private double double_activity_type;
    private int activity_type;

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;

    private final Messenger mMessenger = new Messenger(
            new IncomingMessageHandler());

    // Keeps track of all current registered clients.
    private List<Messenger> mClients = new ArrayList<Messenger>();

    private static boolean isRunning = false;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate(): SensorsService Started.");
        super.onCreate();
        isRunning = true;
        mAccBuffer = new ArrayBlockingQueue<Double>(
                Globals.ACCELEROMETER_BUFFER_CAPACITY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand(): Received start id " + startId + ": "
                + intent);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);

        mAsyncTask = new OnSensorChangedTask();
        mAsyncTask.execute();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "S:onBind() - return mMessenger.getBinder()");
        return mMessenger.getBinder();
    }

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "S:onDestroy():Service Stopped");

        mAsyncTask.cancel(true);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
        Log.i("","");

        isRunning = false;

        super.onDestroy();
    }

    private class OnSensorChangedTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {

            int blockSize = 0;
            FFT fft = new FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY);
            double[] accBlock = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
            double[] re = accBlock;
            double[] im = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];

            Double[] block = new Double[Globals.ACCELEROMETER_BLOCK_CAPACITY + 1];

            double max = Double.MIN_VALUE;

            while (true) {
                try {
                    // need to check if the AsyncTask is cancelled or not in the while loop
                    if (isCancelled () == true)
                    {
                        return null;
                    }

                    // Dumping buffer
                    accBlock[blockSize++] = mAccBuffer.take().doubleValue();

                    if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0;

                        // time = System.currentTimeMillis();
                        max = .0;
                        for (double val : accBlock) {
                            if (max < val) {
                                max = val;
                            }
                        }

                        fft.fft(re, im);

                        for (int i = 0; i < re.length; i++) {
                            double mag = Math.sqrt(re[i] * re[i] + im[i]
                                    * im[i]);
                            block[i] = mag;
                            im[i] = .0; // Clear the field
                        }

                        // Append max after frequency component
                        block[Globals.ACCELEROMETER_BLOCK_CAPACITY] = max;
                        double_activity_type = WekaClassifier.classify(block);
                        Log.i("classifier", "activity_type = " + double_activity_type);
                        activity_type = (int)double_activity_type;

                        sendMessageToUI();
                        Thread.sleep(500);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onSensorChanged(SensorEvent event) {
        //Log.d(TAG, "onSensorChanged() is called");


        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            double m = Math.sqrt(event.values[0] * event.values[0]
                    + event.values[1] * event.values[1] + event.values[2]
                    * event.values[2]);

            // Inserts the specified element into this queue if it is possible
            // to do so immediately without violating capacity restrictions,
            // returning true upon success and throwing an IllegalStateException
            // if no space is currently available. When using a
            // capacity-restricted queue, it is generally preferable to use
            // offer.

            try {
                mAccBuffer.add(new Double(m));
            } catch (IllegalStateException e) {

                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(
                        mAccBuffer.size() * 2);

                mAccBuffer.drainTo(newBuf);
                mAccBuffer = newBuf;
                mAccBuffer.add(new Double(m));
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void sendMessageToUI() {
        Log.d(TAG, "S:sendMessageToUI");
        Iterator<Messenger> messengerIterator = mClients.iterator();
        while (messengerIterator.hasNext()) {
            Messenger messenger = messengerIterator.next();
            try {
                Log.d(TAG, "S:TX MSG_ACTIVITY_TYPE_UPDATE");

                Message msg = Message.obtain(null, MapViewActivity.MSG_ACTIVITY_TYPE_UPDATE);
                msg.arg1 = activity_type;
                messenger.send(msg);

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list.
                mClients.remove(messenger);
            }
        }
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
