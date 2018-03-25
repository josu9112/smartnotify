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
	private ArrayList<Stop> stops;
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
	private boolean cancelled;

	public PublicTransportation(JourneyDetail journeyDetail) throws JSONException, IOException  {
		this.journeyDetail = journeyDetail;
		
		JSONObject ob = journeyDetail.executeRequest();
		String gemRef = ob.getJSONObject("JourneyDetail").getJSONObject("GeometryRef").getString("ref");
		Geometry gem = new Geometry(journeyDetail.getToken());
		gem.setGeometryRefLink(gemRef);
		
		this.type = ob.getJSONObject("JourneyDetail").getJSONObject("JourneyType").getString("type");
		this.linje = ob.getJSONObject("JourneyDetail").getJSONObject("JourneyName").getString("name");
		this.direction = ob.getJSONObject("JourneyDetail").getJSONObject("Direction").getString("$");
		this.journeyid = ob.getJSONObject("JourneyDetail").getJSONObject("JourneyId").getString("id");
		this.stops = new ArrayList<Stop>();
		this.setStops(ob.getJSONObject("JourneyDetail").getJSONArray("Stop"));
		this.date = ob.getJSONObject("JourneyDetail").getJSONArray("Stop").getJSONObject(0).getString("depDate");
		this.distance = calcDistance(gem.executeRequest().getJSONObject("Geometry").getJSONObject("Points").getJSONArray("Point"));
		this.startTime = ob.getJSONObject("JourneyDetail").getJSONArray("Stop").getJSONObject(0).getString("depTime");
		this.currentStop = 0;
		this.delays = new ArrayList<Integer>();
		this.totalTime = calcJourneyTime(ob.getJSONObject("JourneyDetail").getJSONArray("Stop"));
		days = new ArrayList<String>();
		days.add(ob.getJSONObject("JourneyDetail").getJSONArray("Stop").getJSONObject(0).getString("depDate"));
		this.weekday = determineWeekday(this.date);
		this.cancelled = false;
	}
		
	
	public boolean compareTo(PublicTransportation pt) {
		return (this.journeyid.equals(pt.journeyid)) ? true : false;
	}
	
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
	
	public String getStartStopName() {
		return this.stops.get(0).getStopName();
	}
	
	public String getEndStopName() {
		return this.stops.get(this.stops.size()-1).getStopName();
	}
	
	public void setCurrentStop(int stopnr) {
		this.currentStop = stopnr;
	}
	
	public int getCurrentStop() {
		return currentStop;
	}
	
	public void setStops(JSONArray stops) {
		for(int i = 0; i < stops.length(); i++) {
			if(i < stops.length()-1)
				this.stops.add(new Stop(stops.getJSONObject(i).getString("depTime"), stops.getJSONObject(i).getString("name")));
			else
				this.stops.add(new Stop(stops.getJSONObject(i).getString("arrTime"), stops.getJSONObject(i).getString("name")));
		}
	}
	
	public ArrayList<Stop> getStops(){
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
		this.stops.get(currentStop).setDelay(delay);
		
//		try {
//			delays.set(currentStop, delay);
//		}catch(Exception e) {
//			delays.add(delay);
//		}
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
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public void printJourney() {
		try {
			PrintWriter writer = new PrintWriter(new File("C:\\Users\\John\\Desktop\\resor\\" + this.journeyid + System.currentTimeMillis() +  ".txt"));
			if(!cancelled) {
			writer.println("Station\t" + "Försening\t" + this.journeyid);
			for(int i = 0; i < stops.size(); i++)
				writer.println(stops.get(i).getStopName() + "\t" + delays.get(i));
			}
			else {
				writer.println(this.journeyid);
				writer.println("Cancelled");
			}
			writer.close();
		} catch (FileNotFoundException e) {
		}
	}
	
	public int getDelayed() {
		if(delays.get(delays.size()-1) > 0)
			return 1;
		else {
			for(Integer a : delays) {
				if(a>0)
					return -1;
			}
			return 0;
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
