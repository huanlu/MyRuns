package com.example.huanlu.myruns.serverdata;

import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class ExerciseEntry {
    private static final Logger mLogger = Logger
			.getLogger(ExerciseEntry.class.getName());
	
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

    public long id;
    public int mInputType;        // Manual, GPS or automatic
    public int mActivityType;     // Running, cycling etc.
    public long mDateTime;    // When does this entry happen, date and time in millisecond
    public int mDuration;         // Exercise duration in seconds
    public double mDistance;      // Distance traveled. Either in meters or feet.
    public double mAvgPace;       // Average pace
    public double mAvgSpeed;      // Average speed
    public int mCalorie;          // Calories burnt
    public double mClimb;         // Climb. Either in meters or feet.
    public int mHeartRate;        // Heart rate
    public String mComment;       // Comments
    public int mPrivacy;          // Privacy
    //public byte[] mLocationList;  // Location list as byte array
    
    public ExerciseEntry() {
    	this.id = new Random().nextLong();
        this.mInputType = 0;
        this.mActivityType = 0;
        this.mDateTime = Calendar.getInstance().getTimeInMillis();
        this.mDuration = 0;
        this.mDistance = 0.0;
        this.mAvgPace = 0.0;
        this.mAvgSpeed = 0.0;
        this.mCalorie = 0;
        this.mClimb = 0.0;
        this.mHeartRate = 0;
        this.mComment = "";
        this.mPrivacy = 0;
        //this.mLocationList = null;
    }
    
    public ExerciseEntry(long id, int mInputType, int mActivityType, 
    						long mDateTime, int mDuration, double mDistance, 
    						double mAvgPace, double mAvgSpeed, int mCalorie, 
    						double mClimb, int mHeartRate, String mComment, 
    						int mPrivacy/*, byte[] mLocationList*/) {
        this.id = id;
        this.mInputType = mInputType;
        this.mActivityType = mActivityType;
        this.mDateTime = mDateTime;
        this.mDuration = mDuration;
        this.mDistance = mDistance;
        this.mAvgPace = mAvgPace;
        this.mAvgSpeed = mAvgSpeed;
        this.mCalorie = mCalorie;
        this.mClimb = mClimb;
        this.mHeartRate = mHeartRate;
        this.mComment = mComment;
        this.mPrivacy = mPrivacy;
        //this.mLocationList = mLocationList;
    }
    
    public ExerciseEntry(JSONObject json){
        if(json == null){
        	mLogger.log(Level.SEVERE, "json object is null");
        }
        
        try {
	        this.id = json.getLong(KEY_ID);
	        this.mInputType = json.getInt(KEY_INPUT_TYPE);
	        this.mActivityType = json.getInt(KEY_ACTIVITY_TYPE);
	        this.mDateTime = json.getLong(KEY_DATE_TIME);
	        this.mDuration = json.getInt(KEY_DURATION);
	        this.mDistance = json.getDouble(KEY_DISTANCE);
	        this.mAvgPace = json.getDouble(KEY_AVG_PACE);
	        this.mAvgSpeed = json.getDouble(KEY_AVG_SPEED);
	        this.mCalorie = json.getInt(KEY_CALORIES);
	        this.mClimb = json.getDouble(KEY_CLIMB);
	        this.mHeartRate = json.getInt(KEY_HEART_RATE);
	        this.mComment = json.getString(KEY_COMMENT);
	        this.mPrivacy = json.getInt(KEY_PRIVACY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
