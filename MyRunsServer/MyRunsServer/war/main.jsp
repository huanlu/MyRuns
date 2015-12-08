<%@page import="com.example.huanlu.myruns.serverdata.ExerciseEntry"%>
<%@page import="com.example.huanlu.myruns.server.Globals"%>
<%@page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Exercise Entry</title>
<style>
table, th, td {
    border: 1px solid black;
}
</style>
</head>
<body>
	<b>Exercise Entry</b>
	<table>
		<tr>
		    <th>ID</th>
		    <th>Input Type</th>
		    <th>Activity Type</th>
		    <th>Date Time</th>
		    <th>Duration</th>
		    <th>Distance</th>
		    <th>Average Speed</th>
		    <th>Calories</th>
		    <th>Climb</th>
		    <th>Heart Rate</th>
		    <th>Comment</th>
		    <th>  </th>
	    </tr>
	    <% 
	    ArrayList<ExerciseEntry> entryList = (ArrayList<ExerciseEntry>) request
							.getAttribute("entryList");
	    for (ExerciseEntry entry : entryList) {
	    	%> 
	    	<tr>
	    		<td><%=entry.id%></td>
	    		<td><%=Globals.INPUT_TYPES[entry.mInputType]%></td>
	    		<td><%=entry.mInputType==Globals.INPUT_TYPE_AUTOMATIC ? 
	    				Globals.AUTOMATIC_ACTIVITY_TYPES[entry.mActivityType] : 
	    				Globals.ACTIVITY_TYPES[entry.mActivityType]%></td>
	    		<% SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss MMM dd yyyy");
	    		Calendar calendar = Calendar.getInstance();
	            calendar.setTimeInMillis(entry.mDateTime); %>
	    		<td><%=formatter.format(calendar.getTime())%></td>
	    		<% int min = entry.mDuration / 60; 
	    		int sec = entry.mDuration % 60; 
	    		String duration = min + "mins " + sec + "secs ";%>
	    		<td><%=duration%></td>
	    		<% double dMiles = entry.mDistance / 1609.34;
	    		String sMiles = new Formatter().format("%.1f", dMiles) + "miles";%>
	    		<td><%=sMiles%></td>
	    		<% String speed = new Formatter().format("%.1f", entry.mAvgSpeed) + "";%>
	    		<td><%=speed%></td>
	    		<td><%=entry.mCalorie%></td>
	    		<% String climb = new Formatter().format("%.1f", entry.mClimb) + "";%>
	    		<td><%=climb%></td>
	    		<td><%=entry.mHeartRate%></td>
	    		<td><%=entry.mComment%></td>
	    		<td>
	    			<form name="input" action="/delete.do?id=<%=entry.id%>" method="post">
	    				<input type="submit" value="Delete">
					</form>
				</td>
			</tr>
			<%
	    }%>
	    <!-- <tr>
			<td>
				<form name="input" action="/post.do" method="post">
					<input type="text" name="comment">
					<input type="submit" value="Post">
				</form>
			</td>
		</tr> -->
	</table>

</body>
</html>
