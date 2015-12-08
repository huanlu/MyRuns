package com.example.huanlu.myruns.data;

/**
 * Created by huanlu on 2/22/15.
 */
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class Message {
    private static final String KEY_COLLAPSEKEY = "collapse_key";
    private static final String KEY_DELAYWHILEIDLE = "delay_while_idle";
    private static final String KEY_TIMETOLIVE = "time_to_live";
    private static final String KEY_DATA = "data";
    private static final String KEY_REGIDS = "registration_ids";

    private String mCollapseKey;
    private boolean mDelayWhileIdle;
    private int mTimeToLive;
    private JSONObject mData;
    private ArrayList<String> mRegidList;

    public Message(String collapseKey, boolean delayWhileIdle, int timeToLive,
                   List<String> regidList) {
        mCollapseKey = collapseKey;
        mDelayWhileIdle = delayWhileIdle;
        mData = new JSONObject();
        mTimeToLive = timeToLive;
        mRegidList = new ArrayList<String>(regidList);
    }

    public Message(List<String> regidList) {
        mCollapseKey = "default";
        mDelayWhileIdle = true;
        mTimeToLive = 30;
        mData = new JSONObject();
        mRegidList = new ArrayList<String>(regidList);
    }

    /**
     * add payload data
     */
    public void addData(String key, String value) {
        try {
            mData.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the payload data
     */
    public String getData(String key) {
        String value = null;
        try {
            value = (String) mData.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return value;
    }


    /**
     * Gets the collapse key.
     */
    public String getCollapseKey() {
        return mCollapseKey;
    }

    /**
     * Gets the delayWhileIdle flag.
     */
    public Boolean isDelayWhileIdle() {
        return mDelayWhileIdle;
    }

    /**
     * Gets the time to live (in seconds).
     */
    public Integer getTimeToLive() {
        return mTimeToLive;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();

        try {
            json.put(KEY_COLLAPSEKEY, mCollapseKey);
            json.put(KEY_DELAYWHILEIDLE, mDelayWhileIdle);
            json.put(KEY_TIMETOLIVE, mTimeToLive);
            json.put(KEY_DATA, mData);
            json.put(KEY_REGIDS, mRegidList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

}
