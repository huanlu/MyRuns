package com.example.huanlu.myruns.data;

/**
 * Created by huanlu on 2/2/15.
 */
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Calendar;

import com.example.huanlu.myruns.StartFragment;
import com.google.android.gms.maps.model.LatLng;


public class ExerciseEntry {
    private static final String TAG = "ExerciseEntry: ";

    private Long id;
    private int mInputType;        // Manual, GPS or automatic
    private int mActivityType;     // Running, cycling etc.
    private long mDateTime;    // When does this entry happen, date and time in millisecond
    private int mDuration;         // Exercise duration in seconds
    private double mDistance;      // Distance traveled. Either in meters or feet.
    private double mAvgPace;       // Average pace
    private double mAvgSpeed;      // Average speed
    private int mCalorie;          // Calories burnt
    private double mClimb;         // Climb. Either in meters or feet.
    private int mHeartRate;        // Heart rate
    private String mComment;       // Comments
    private int mPrivacy;          // Privacy
    private ArrayList<LatLng> mLocationList; // Location list

    public ExerciseEntry() {
        Calendar mDateAndTime;
        mDateAndTime = Calendar.getInstance();
        //this.id = id;
        this.mInputType = StartFragment.INPUT_TYPE_MANUAL_ENTRY;
        this.mActivityType = 0;
        this.mDateTime = mDateAndTime.getTimeInMillis();
        this.mDuration = 0;
        this.mDistance = 0;
        this.mAvgPace = 0;
        this.mAvgSpeed = 0;
        this.mCalorie = 0;
        this.mClimb = 0;
        this.mHeartRate = 0;
        this.mComment = "";
        this.mPrivacy = 0;
        this.mLocationList = new ArrayList<LatLng>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getmInputType() {
        return mInputType;
    }

    public void setmInputType(int mInputType) {
        this.mInputType = mInputType;
    }

    public int getmActivityType() {
        return mActivityType;
    }

    public void setmActivityType(int mActivityType) {
        this.mActivityType = mActivityType;
    }

    public long getmDateTime() {
        return mDateTime;
    }

    public void setmDateTime(long mDateTime) {
        this.mDateTime = mDateTime;
    }

    public int getmDuration() {
        return mDuration;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public double getmDistance() {
        return mDistance;
    }

    public void setmDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    public double getmAvgPace() {
        return mAvgPace;
    }

    public void setmAvgPace(double mAvgPace) {
        this.mAvgPace = mAvgPace;
    }

    public double getmAvgSpeed() {
        return mAvgSpeed;
    }

    public void setmAvgSpeed(double mAvgSpeed) {
        this.mAvgSpeed = mAvgSpeed;
    }

    public int getmCalorie() {
        return mCalorie;
    }

    public void setmCalorie(int mCalorie) {
        this.mCalorie = mCalorie;
    }

    public double getmClimb() {
        return mClimb;
    }

    public void setmClimb(double mClimb) {
        this.mClimb = mClimb;
    }

    public int getmHeartRate() {
        return mHeartRate;
    }

    public void setmHeartRate(int mHeartRate) {
        this.mHeartRate = mHeartRate;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public int getmPrivacy() {
        return mPrivacy;
    }

    public void setmPrivacy(int mPrivacy) {
        this.mPrivacy = mPrivacy;
    }

    public ArrayList<LatLng> getmLocationList() {
        return mLocationList;
    }

    public void setmLocationList(ArrayList<LatLng> mLocationList) {
        this.mLocationList = mLocationList;
    }

    public byte[] getByteArrayFromLocationList() {
        Log.d(TAG, "mLocationList.size() = " + mLocationList.size());
        int[] intArray = new int[mLocationList.size() * 2];

        for (int i = 0; i < mLocationList.size(); i++) {
            intArray[i * 2] = (int) (mLocationList.get(i).latitude * 1E6);
            intArray[(i * 2) + 1] = (int) (mLocationList.get(i).longitude * 1E6);
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length
                * Integer.SIZE);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(intArray);

        return byteBuffer.array();
    }


    // Convert byte array to Location ArrayList
    public void setLocationListFromByteArray(byte[] bytePointArray) {
        mLocationList = new ArrayList<LatLng>();

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytePointArray);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();

        int[] intArray = new int[bytePointArray.length / Integer.SIZE];
        intBuffer.get(intArray);

        int locationNum = intArray.length / 2;

        for (int i = 0; i < locationNum; i++) {
            LatLng latLng = new LatLng((double) intArray[i * 2] / 1E6F,
                    (double) intArray[i * 2 + 1] / 1E6F);
            mLocationList.add(latLng);
        }
    }
}
