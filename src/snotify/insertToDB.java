package snotify;

import java.io.FileNotFoundException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class insertToDB {
	private static Statement stmt;
	private static PreparedStatement prpSt;
	private static PreparedStatement prpSt2;
	private static String sql;
	private static String query;
	
	/**
	 * Use this method if all the stops are inserted
	 * and you only need to insert to Vasttrafik table
	 * and TripStops table
	 */
	public static void insertInit(int distance, int totalTime, int nrStops, ArrayList<String> stopName) throws FileNotFoundException, SQLException {
		connectDB.connectToDB();
		query = "INSERT INTO VasttrafikTrip (distance, totalTime, nrOfStops)" + " values (?,?,?)";
		prpSt = connectDB.connection.prepareStatement(query);
		prpSt.setInt(1, distance);
		prpSt.setInt(2, totalTime);
		prpSt.setInt(3, nrStops);
		prpSt.executeQuery();
		
		query = "SELECT LAST_INSERT_ID()";
		prpSt = connectDB.connection.prepareStatement(query);
		ResultSet rs = prpSt.executeQuery();
		int tripID = 0;
		while(rs.next()) {
			tripID = rs.getInt(1);
		}
		query = "INSERT INTO TripStops (VasttrafikTripID, stopID, number)" + " values(?,?,?)";
		prpSt = connectDB.connection.prepareStatement(query);
		for(int i=0; i<stopName.size(); i++) {
			sql = "SELECT stopID FROM Stop WHERE stopName = " + "'" + stopName.get(i) + "'";
			prpSt2 = connectDB.connection.prepareStatement(sql);
			ResultSet stopID = prpSt2.executeQuery();
			while(stopID.next()) {
				prpSt.setInt(2, stopID.getInt(1));
			}
			prpSt.setInt(1,tripID);
			prpSt.setInt(3, i+1);
			//prpSt = connectDB.connection.prepareStatement(query);
			prpSt.executeQuery();
		}
		connectDB.connection.close();
	}
	
	public static void insertToVasttrafikTrip(int distance, int totalTime) throws FileNotFoundException, SQLException {
		connectDB.connectToDB();
		query = "INSERT INTO VasttrafikTrip (distance, totalTime)" + " values (?,?)";
		prpSt = connectDB.connection.prepareStatement(query);
		prpSt.setInt(1, distance);
		prpSt.setInt(2, totalTime);
		prpSt.executeQuery();
		//query = "SELECT LAST_INSERT_ID()";
		prpSt = connectDB.connection.prepareStatement(query);
		//prpSt.executeQuery();
		//ResultSet rs = prpSt.executeQuery();
		//int tripID;
		//while(rs.next()) {
			//tripID = rs.getInt(1);
		//}
		
		connectDB.connection.close();
	}
	
	public static void insertToTripStops(int number) throws FileNotFoundException, SQLException {
		connectDB.connectToDB();
		query = "INSERT INTO TripStops (number)" + " values (?)";
		prpSt = connectDB.connection.prepareStatement(query);
		prpSt.setInt(1, number);
		prpSt.executeQuery();
		connectDB.connection.close();
	}
	
	public static void insertToStop(String stopName) throws FileNotFoundException, SQLException {
		connectDB.connectToDB();
		query = "INSERT INTO Stop(stopName)" + " values (?)";
		prpSt = connectDB.connection.prepareStatement(query);
		prpSt.setString(1, stopName);
		prpSt.executeQuery();
		connectDB.connection.close();
	}
	
	public static void insertToDelay(String time, int delay) throws FileNotFoundException, SQLException, ParseException {
		connectDB.connectToDB();
		query = "INSERT INTO Delay (JourneyID, stopID, time, delay)" + " values(?,?,?,?)";
		prpSt = connectDB.connection.prepareStatement(query);
		prpSt.setTime(1, convertToTime(time));
		prpSt.setInt(2, delay);
		prpSt.executeQuery();
		connectDB.connection.close();
	}
	
	public static void insertToJourney(PublicTransportation PT) throws FileNotFoundException, SQLException, ParseException {
		connectDB.connectToDB();
		query = "INSERT INTO Journey (VasttrafikTripID, date, timeStart, timeEnd, weekDay, delayed, journeyNote, journeyCanceled)" + " values(?,?,?,?,?,?,?.?)";
		prpSt = connectDB.connection.prepareStatement(query);
		prpSt.setDate(2, convertToDate(PT.getDate()));
		prpSt.setTime(3, convertToTime(PT.getStartTime()));
		prpSt.setTime(4, convertToTime(PT.getEndTime()));
		prpSt.setString(5, PT.getWeekday());
		prpSt.setInt(6, PT.isDelayed());
		String note = "";
		if(!PT.getOriginNote().equals("")) 
			note = "Origin note: " + PT.getOriginNote() + " /";
		if(!PT.getDestNote().equals(""))
			note += ", Destination note: " + PT.getDestNote(); 
		prpSt.setString(7, note);
		if(PT.getCancelled())
			prpSt.setInt(8, 1);
		else
			prpSt.setInt(8, 0);
		sql = "SELECT VasttrafikTrip.VasttrafikTripID FROM VasttrafikTrip INNER JOIN TripStops ON "
				+ "(TripStops.VasttrafikTripID = VasttrafikTrip.VasttrafikTripID) WHERE "
				+ "VasttrafikTrip.totalTime = " + PT.getTotalTime() +
				" AND VasttrafikTrip.nrOfStops = " + PT.getStops().size() + " AND (TripStops.stopID = (SELECT stopID FROM Stop WHERE stopName = '" + PT.getStartStopName()
				+ "') AND TripStops.number = 1) OR (TripStops.stopID = (SELECT stopID FROM Stop WHERE stopName = '" + PT.getEndStopName() + "')"
				+ " AND TripStops.number = " + PT.getStops().size() + ") HAVING COUNT(TripStops.stopID) = 2;";
		prpSt2 = connectDB.connection.prepareStatement(sql);
		ResultSet tripID = prpSt2.executeQuery();
		while(tripID.next()) {
			prpSt.setInt(1, tripID.getInt(1));
		}
		prpSt.executeQuery();
		//Om något fuckas är det detta
		query = "SELECT LAST_INSERT_ID()";
		prpSt = connectDB.connection.prepareStatement(query);
		ResultSet rs = prpSt.executeQuery();
		int JourID = 0;
		while(rs.next()) {
			JourID = rs.getInt(1);
		}
		insertDelays(JourID, PT.getStops());
		connectDB.connection.close();
	}
	
	private static void insertDelays(int JourID, ArrayList<Stop> Stops) throws SQLException, ParseException{
		query = "INSERT INTO Delay (JourneyID, stopID, time, delay)" + " values(?,?,?,?)";
		prpSt = connectDB.connection.prepareStatement(query);
		prpSt.setInt(1, JourID);
		for(int i = 0; i < Stops.size(); i++) {
			sql = "SELECT stopID FROM Stop WHERE stopName = " + Stops.get(i).getStopName();
			prpSt2 = connectDB.connection.prepareStatement(sql);
			ResultSet stopID = prpSt2.executeQuery();
			while(stopID.next()) {
				prpSt.setInt(2, stopID.getInt(1));
			}
			prpSt.setTime(3, convertToTime(Stops.get(i).getTime()));
			prpSt.setInt(4, Stops.get(i).getDelay());
			prpSt.executeQuery();
		}
	}
	
	public static Time convertToTime(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		long t = sdf.parse(s).getTime();
		Time time = new Time(t);
		return time;
	}
	
	public static Date convertToDate(String s) {
		java.sql.Date d = java.sql.Date.valueOf(s);
		return d;
	}
}
