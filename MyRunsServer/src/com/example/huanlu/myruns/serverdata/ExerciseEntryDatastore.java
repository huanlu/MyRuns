package com.example.huanlu.myruns.serverdata;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.huanlu.myruns.server.PostServlet;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class ExerciseEntryDatastore {
	
	private static final Logger mLogger = Logger
			.getLogger(ExerciseEntryDatastore.class.getName());
	
	private static final DatastoreService mDatastore = DatastoreServiceFactory
			.getDatastoreService();
	
	public static String ENTITY_PARENT_KIND_DEVICE = RegDatastore.ENTITY_KIND_DEVICE;
	public static String ENTITY_PARENT_DEVICE_KEY = ENTITY_PARENT_KIND_DEVICE;
	public static String ENTITY_KIND_EXERCISE_ENTRY = "ExerciseEntry";
	
	public static String FIELD_NAME_ID = "id";
	public static String FIELD_NAME_INPUT_TYPE = "inputType";
	public static String FIELD_NAME_ACTIVITY_TYPE = "activityType";
	public static String FIELD_NAME_DATE_TIME = "dateTime";
	public static String FIELD_NAME_DURATION = "duration";
	public static String FIELD_NAME_DISTANCE = "distance";
	public static String FIELD_NAME_AVG_PAGE = "avgPace";
	public static String FIELD_NAME_AVG_SPEED = "avgSpeed";
	public static String FIELD_NAME_CALORIE = "calorie";
	public static String FIELD_NAME_CLIMB = "climb";
	public static String FIELD_NAME_HEART_RATE = "heartRate";
	public static String FIELD_NAME_COMMENT = "comment";
	public static String FIELD_NAME_PRIVACY = "privacy";
	//public static String FIELD_NAME_LOCATION_LIST = "locationList";

	private static Key getParentKey() {
		return KeyFactory.createKey(ENTITY_PARENT_KIND_DEVICE, ENTITY_PARENT_DEVICE_KEY);
	}

	private static void createParentEntity() {
		Entity entity = new Entity(getParentKey());

		mDatastore.put(entity);
	}

	public static boolean add(ExerciseEntry entry) {
		mLogger.log(Level.INFO, "input type = " + entry.mInputType);
		Key parentKey = getParentKey();
		try {
			mDatastore.get(parentKey);
		} catch (Exception ex) {
			createParentEntity();
		}

		mDatastore.put(getEntityFromExerciseEntry(parentKey, entry));

		return true;
	}
	
	public static boolean delete(long id) {

		Filter filter = new FilterPredicate(FIELD_NAME_ID,
				FilterOperator.EQUAL, id);
		Query query = new Query(ENTITY_KIND_EXERCISE_ENTRY);
		query.setFilter(filter);

		// Use PreparedQuery interface to retrieve results
		PreparedQuery pq = mDatastore.prepare(query);

		Entity result = pq.asSingleEntity();
		boolean ret = false;
		if (result != null) {
			// delete
			mDatastore.delete(result.getKey());
			ret = true;
		}
		return ret;
	}

	public static ArrayList<ExerciseEntry> query() {
		ArrayList<ExerciseEntry> resultList = new ArrayList<ExerciseEntry>();

		Query query = new Query(ENTITY_KIND_EXERCISE_ENTRY);
		query.setFilter(null);
		query.setAncestor(getParentKey());
		//query.addSort(FIELD_NAME_ID, SortDirection.ASCENDING);
		PreparedQuery pq = mDatastore.prepare(query);

		for (Entity entity : pq.asIterable()) {
			resultList.add(getExerciseEntryFromEntity(entity));
		}
		return resultList;
	}
	
	public static void deleteAll() {
		Query query = new Query(ENTITY_KIND_EXERCISE_ENTRY);
		// get every record from datastore, no filter
		query.setFilter(null);
		// set query's ancestor to get strong consistency
		query.setAncestor(getParentKey());

		PreparedQuery pq = mDatastore.prepare(query);

		for (Entity entity : pq.asIterable()) {
			mDatastore.delete(entity.getKey());
		}
	}
	
	private static Entity getEntityFromExerciseEntry(Key parentKey, ExerciseEntry entry){
		if (entry == null) {
			return null;
		}
		
		Entity entity = new Entity(ENTITY_KIND_EXERCISE_ENTRY,
				entry.id, parentKey);
		
		mLogger.log(Level.INFO, "input type = " + entry.mInputType);

		entity.setProperty(FIELD_NAME_ID, entry.id);
		entity.setProperty(FIELD_NAME_INPUT_TYPE, entry.mInputType);
		entity.setProperty(FIELD_NAME_ACTIVITY_TYPE, entry.mActivityType);
		entity.setProperty(FIELD_NAME_DATE_TIME, entry.mDateTime);
		entity.setProperty(FIELD_NAME_DURATION, entry.mDuration);
		entity.setProperty(FIELD_NAME_DISTANCE, entry.mDistance);
		entity.setProperty(FIELD_NAME_AVG_PAGE, entry.mAvgPace);
		entity.setProperty(FIELD_NAME_AVG_SPEED, entry.mAvgSpeed);
		entity.setProperty(FIELD_NAME_CALORIE, entry.mCalorie);
		entity.setProperty(FIELD_NAME_CLIMB, entry.mClimb);
		entity.setProperty(FIELD_NAME_HEART_RATE, entry.mHeartRate);
		entity.setProperty(FIELD_NAME_COMMENT, entry.mComment);
		entity.setProperty(FIELD_NAME_PRIVACY, entry.mPrivacy);
		//entity.setProperty(FIELD_NAME_LOCATION_LIST, (byte[])entry.mLocationList);
		
		return entity;
	}
	
	private static ExerciseEntry getExerciseEntryFromEntity(Entity entity){
		if (entity == null) {
			return null;
		}
		
		ExerciseEntry entry = new ExerciseEntry();
		
		entry.id = (long)entity.getProperty(FIELD_NAME_ID);
		entry.mInputType = (int)(long)entity.getProperty(FIELD_NAME_INPUT_TYPE);
		entry.mActivityType = (int)(long)entity.getProperty(FIELD_NAME_ACTIVITY_TYPE);
		entry.mDateTime = (long)entity.getProperty(FIELD_NAME_DATE_TIME);
		entry.mDuration = (int)(long)entity.getProperty(FIELD_NAME_DURATION);
		entry.mDistance = (double)entity.getProperty(FIELD_NAME_DISTANCE);
		entry.mAvgPace = (double)entity.getProperty(FIELD_NAME_AVG_PAGE);
		entry.mAvgSpeed = (double)entity.getProperty(FIELD_NAME_AVG_SPEED);
		entry.mCalorie = (int)(long)entity.getProperty(FIELD_NAME_CALORIE);
		entry.mClimb = (double)entity.getProperty(FIELD_NAME_CLIMB);
		entry.mHeartRate = (int)(long)entity.getProperty(FIELD_NAME_HEART_RATE);
		entry.mComment = (String)entity.getProperty(FIELD_NAME_COMMENT);
		entry.mPrivacy = (int)(long)entity.getProperty(FIELD_NAME_PRIVACY);
		//entry.mLocationList = (byte[])entity.getProperty(FIELD_NAME_LOCATION_LIST);
		
		return entry;
	}
}
