package snotify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;

public class PublicTransportation {

	private String journeyid;
	private String type;
	private String linje;
	private String journeyDetailRef;
	private String direction;
	private ArrayList<String> stops;
	private ArrayList<Integer> delays;
	private boolean travels;
	private int currentStop;
	private String startTime;
	private double distance;
	private String date;
	private String weekday;
	private int totalTime;
	private JourneyDetail journeyDetail;
	private ArrayList<String> days;

	
	public PublicTransportation(String type, String linje, String journeyid, String journeyDetailRef) {
		this.type = type;
		this.linje = linje;
		this.journeyid = journeyid;
		this.journeyDetailRef = journeyDetailRef;
	}
	
	/**
	 * @param type bus/tram/train
	 * @param linje number
	 * @param journeyid
	 * @param journeyDetailRef
	 * @param direction lastStop
	 * @param startTime starttime of trip
	 * @param distance total distance of trip in meters
	 * @param date date of trip
	 * @param totalTime totaltime of trip in minutes
	 */
	public PublicTransportation(String type, String linje, String journeyid, 
			String journeyDetailRef, String direction, String startTime, double distance, String date, int totalTime) {
		days = new ArrayList<String>();
		
	}
	
	public PublicTransportation(String type, String linje, String journeyid, String journeyDetailRef, String weekday) {
		this.type = type;
		this.linje = linje;
		this.journeyid = journeyid;
		this.journeyDetailRef = journeyDetailRef;
		stops = new ArrayList<String>();
		this.direction = direction;
		this.currentStop = 0;
		delays = new ArrayList<Integer>();
		this.startTime = startTime;
		this.distance = distance;
		this.date = date;
		this.weekday = determineWeekday(date);
		this.totalTime = totalTime;
	}
	
	public PublicTransportation(JourneyDetail journeyDetail) throws JSONException, IOException {
		this.journeyDetail = journeyDetail;
		
		JSONObject ob = journeyDetail.executeRequest();
		String gemRef = ob.getJSONObject("JourneyDetail").getJSONObject("GeometryRef").getString("ref");
		Geometry gem = new Geometry(journeyDetail.getToken());
		gem.setGeometryRefLink(gemRef);
		
		this.type = ob.getJSONObject("JourneyDetail").getJSONObject("JourneyType").getString("type");
		this.linje = ob.getJSONObject("JourneyDetail").getJSONObject("JourneyName").getString("name");
		this.direction = ob.getJSONObject("JourneyDetail").getJSONObject("Direction").getString("$");
		this.journeyid = ob.getJSONObject("JourneyDetail").getJSONObject("JourneyId").getString("id");
		this.stops = new ArrayList<String>();
		this.setStops(ob.getJSONObject("JourneyDetail").getJSONArray("Stop"));
		this.date = ob.getJSONObject("JourneyDetail").getJSONArray("Stop").getJSONObject(0).getString("depDate");
		this.distance = calcDistance(gem.executeRequest().getJSONObject("Geometry").getJSONObject("Points").getJSONArray("Point"));
		this.startTime = ob.getJSONObject("JourneyDetail").getJSONArray("Stop").getJSONObject(0).getString("depTime");
		this.currentStop = 0;
		this.delays = new ArrayList<Integer>();
		this.totalTime = calcJourneyTime(ob.getJSONObject("JourneyDetail").getJSONArray("Stop"));
		days = new ArrayList<String>();
		days.add(weekday);
	}
		
	
	public boolean compareTo(PublicTransportation pt) {
		return (this.journeyid.equals(pt.journeyid)) ? true : false;
	}
	
	
//	public void setWeekdayDay(String date) {
//		String weekday = determineWeekday(date);
//		
//		for(String a : this.days) {
//			if(a.equals(weekday))
//				return;
//		}
//		this.days.add(weekday);
//	}
	
	
	public void setWeekdayDay(String date) {
		String weekday = determineWeekday(date);
		
		for(String a : this.days) {
			if(a.equals(weekday))
				return;
		}
		this.days.add(weekday);
	}

	public String getJourneyid() {
		return journeyid;
	}

	public String getType() {
		return type;
	}

	public String getLinje() {
		return linje;
	}

	public ArrayList<String> getDays() {
		return days;
	}

	public String getJourneyDetailRef() {
		return journeyDetailRef;
	}
	
	public String getDirection() {
		return direction;
	}
	
	public void setCurrentStop(int stopnr) {
		this.currentStop = stopnr;
	}
	
	public int getCurrentStop() {
		return currentStop;
	}
	
	public void setStops(JSONArray stops) {
		for(int i = 0; i < stops.length(); i++) {
			this.stops.add(stops.getJSONObject(i).getString("name"));
		}
	}
	
	public ArrayList<String> getStops(){
		return stops;
	}
	
	public void setTravels(boolean travels) {
		this.travels = travels;
	}
	
	public boolean getTravels() {
		return travels;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public double getDistance() {
		return distance;
	}
	 
	
	public void setDelay(int delay) {
		try {
			delays.set(currentStop, delay);
		}catch(Exception e) {
			delays.add(delay);
		}
	}
	
	public ArrayList<Integer> getDelays(){
		return delays;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getWeekday() {
		return weekday;
	}
	
	public int getTotalTime() {
		return totalTime;
	}
	
	public JourneyDetail getJourneyDetail() {
		return journeyDetail;
	}
	
	public void printJourney() {
		try {
			PrintWriter writer = new PrintWriter(new File(this.journeyid + ".txt"));
			writer.println("Station\t" + "Försening");
			for(int i = 0; i < stops.size(); i++)
				writer.println(stops.get(i).toString() + "\t" + delays.get(i).toString());
			writer.close();
		} catch (FileNotFoundException e) {
		}
	}
	
	
	private static String determineWeekday(String date){
		SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
		Date dt1 = null;
		try {
			dt1 = format1.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		DateFormat format2=new SimpleDateFormat("EEEE"); 
		return format2.format(dt1);
	}
	
	public static double calcDistance(JSONArray geometry) {
		GeodeticCalculator geoCalc = new GeodeticCalculator();
		Ellipsoid reference = Ellipsoid.WGS84;  
		double distance = 0;
		for(int i = 0; i < geometry.length()-1; i++) {
			JSONObject temp = geometry.getJSONObject(i);
			JSONObject temp2 = geometry.getJSONObject(i+1);
			GlobalPosition pointA = new GlobalPosition(temp.getDouble("lat"), temp.getDouble("lon"), 0.0); // Point A
			GlobalPosition userPos = new GlobalPosition(temp2.getDouble("lat"), temp2.getDouble("lon"), 0.0); // Point B
			distance += geoCalc.calculateGeodeticCurve(reference, userPos, pointA).getEllipsoidalDistance(); // Distance between Point A and Point B
		}
		return distance;
	}
	
	public static int calcJourneyTime(JSONArray journeystops) {
		String timeStart = journeystops.getJSONObject(0).getString("depTime");
		String timeFinish = journeystops.getJSONObject(journeystops.length()-1).getString("arrTime");
		
		if(journeystops.getJSONObject(0).get("depDate").toString()
				.equals(journeystops.getJSONObject(journeystops.length()-1).get("arrDate").toString())) {
			int hours = Integer.parseInt(timeFinish.substring(0, 2)) - Integer.parseInt(timeStart.substring(0,2));
			int minutes = Integer.parseInt(timeFinish.substring(3, 5)) - Integer.parseInt(timeStart.substring(3,5));
			return (hours*60)+minutes;
		}
		else {
			int beforeMidnight = ((24 - Integer.parseInt(timeStart.substring(0, 2)))*60) - Integer.parseInt(timeStart.substring(3,5));
			int afterMidnight = Integer.parseInt(timeFinish.substring(0, 2))*60 + Integer.parseInt(timeFinish.substring(3,5));
			return beforeMidnight + afterMidnight;
		}
	}
	
}
