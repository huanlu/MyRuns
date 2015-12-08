package com.example.huanlu.myruns.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.huanlu.myruns.serverdata.ExerciseEntry;
import com.example.huanlu.myruns.serverdata.ExerciseEntryDatastore;

public class QueryServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		ArrayList<ExerciseEntry> entryList = ExerciseEntryDatastore.query();
		
		req.setAttribute("entryList", entryList);
		
		getServletContext().getRequestDispatcher("/main.jsp").forward(req, resp);
		
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		doPost(req, resp);
	}
}
