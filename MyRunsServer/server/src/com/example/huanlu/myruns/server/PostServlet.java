package com.example.huanlu.myruns.server;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.huanlu.myruns.serverdata.ExerciseEntry;
import com.example.huanlu.myruns.serverdata.ExerciseEntryDatastore;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class PostServlet extends HttpServlet {
	private static final Logger mLogger = Logger
			.getLogger(PostServlet.class.getName());
	
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String msg = req.getParameter("msg_string_json");
		try {
			JSONObject json = new JSONObject(msg);
			mLogger.log(Level.INFO, "json_string = " + json.toString());
			
			ExerciseEntry entry = new ExerciseEntry(json);
			//mLogger.log(Level.INFO, "input type = " + entry.mInputType);
			ExerciseEntryDatastore.add(entry);
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		doPost(req, resp);
	}
}
