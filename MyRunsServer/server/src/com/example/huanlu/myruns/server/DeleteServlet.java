package com.example.huanlu.myruns.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.huanlu.myruns.serverdata.ExerciseEntry;
import com.example.huanlu.myruns.serverdata.ExerciseEntryDatastore;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;


public class DeleteServlet extends HttpServlet {
	
	private static final Logger mLogger = Logger
			.getLogger(DeleteServlet.class.getName());

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String id = req.getParameter("id");
		mLogger.log(Level.INFO, "id = " + id);
		
		if(id == null){
			ExerciseEntryDatastore.deleteAll();
		} else {
			ExerciseEntryDatastore.delete(Long.parseLong(id));
			getServletContext().getRequestDispatcher("/sendmsg.do").forward(req, resp);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		doGet(req, resp);
	}
}
