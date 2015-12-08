package com.example.huanlu.myruns.server;

public class Globals {
	// API key is defined in here
	public static final String GCMAPIKEY = "AIzaSyBMGIu-xGagLBiqwRLFP-YLGUuyx_a2ii8";

	public static final int INPUT_TYPE_MANUAL_ENTRY = 0;
	public static final int INPUT_TYPE_GPS = 1;
	public static final int INPUT_TYPE_AUTOMATIC = 2;
	public static final String[] INPUT_TYPES = {"Manual Entry", "GPS", "Automatic"};
	public static final String[] ACTIVITY_TYPES = {"Running", "Walking",
            "Standing", "Cycling", "Hiking", "Downhill Skiing",
            "Cross-Country Skiing", "Snowboarding", "Skating", "Swimming",
            "Mountain Biking", "Wheelchair", "Elliptical", "Other"};
	public static final String[] AUTOMATIC_ACTIVITY_TYPES = 
    		{"Standing", "Walking", "Running", "Unknown"};
}
