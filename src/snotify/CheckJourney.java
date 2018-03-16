package snotify;

import java.io.IOException;
import java.util.TimerTask;

import org.joda.time.LocalTime;
import org.json.JSONArray;
import org.json.JSONObject;

public class CheckJourney extends TimerTask {

	private PublicTransportation pt;
	
	public CheckJourney(PublicTransportation pt) {
		this.pt = pt;
		pt.setTravels(true);
	}
	
	@Override
	public void run() {
		JSONObject obj = null;
		try {
			obj = pt.getJourneyDetail().executeRequest();
			JSONArray arr = obj.getJSONObject("JourneyDetail").getJSONArray("Stop");
			int whatStop = pt.getCurrentStop();
			while(!arr.getJSONObject(whatStop).has("rtDepTime") && !arr.getJSONObject(whatStop).has("rtArrTime")) {
				whatStop++;
				if(whatStop > obj.getJSONObject("JourneyDetail").getJSONObject("JourneyType").getInt("routeIdxTo")) {
					pt.printJourney();	//Journey ended, prints it out
					this.cancel();
					return;
				}
				pt.setCurrentStop(whatStop);
			}
			int delay = calcTimeDifference(arr.getJSONObject(whatStop));
			System.out.println(pt.getDirection() + ", Stop: "+ whatStop + " , Delay: " + delay);
			pt.setDelay(delay);
		} catch (IOException e) {
		}
	}
	
	private int calcTimeDifference(JSONObject journeystop) {
		String time1;
		String time2;
		String date1;
		String date2;
		if(journeystop.getInt("routeIdx") != pt.getStops().size()-1) {
			time1 = journeystop.getString("depTime");
			time2 = journeystop.getString("rtDepTime");
			date1 = journeystop.getString("depDate");
			date2 = journeystop.getString("rtDepDate");
		}
		else {
			time1 = journeystop.getString("arrTime");
			time2 = journeystop.getString("rtArrTime");
			date1 = journeystop.getString("arrDate");
			date2 = journeystop.getString("rtArrDate");
		}
		
		if(date1.equals(date2)) {
			int hours = Integer.parseInt(time2.substring(0, 2)) - Integer.parseInt(time1.substring(0,2));
			int minutes = Integer.parseInt(time2.substring(3, 5)) - Integer.parseInt(time1.substring(3,5));
			return (hours*60)+minutes;
		}
		else {
			int beforeMidnight = ((24 - Integer.parseInt(time2.substring(0, 2)))*60) - Integer.parseInt(time1.substring(3,5));
			int afterMidnight = Integer.parseInt(time2.substring(0, 2))*60 + Integer.parseInt(time1.substring(3,5));
			return beforeMidnight + afterMidnight;
		}
	}
}
