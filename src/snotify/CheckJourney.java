package snotify;

import java.io.IOException;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
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
			if(obj.getJSONObject("JourneyDetail").has("error")) {
				System.out.println(obj.getJSONObject("JourneyDetail").getString("error"));
			}
			if(obj.getJSONObject("JourneyDetail").has("errortext")) {
				System.out.println(obj.getJSONObject("JourneyDetail").getString("errortext"));
			}
			JSONArray arr;
			try {
				arr = obj.getJSONObject("JourneyDetail").getJSONArray("Stop");
			}catch(Exception e) {
				findNewLink();
				obj = pt.getJourneyDetail().executeRequest();
				try {
				arr = obj.getJSONObject("JourneyDetail").getJSONArray("Stop");
				}catch(Exception d) {
					System.out.println("Link couldn't be found: " + d.getMessage());
					this.cancel();
					return;
				}
			}
			int whatStop = pt.getCurrentStop();
			if(whatStop == 0) {
				checkCancelled();
			}
			while(!arr.getJSONObject(whatStop).has("rtDepTime") && !arr.getJSONObject(whatStop).has("rtArrTime")) {
				if(arr.getJSONObject(whatStop).getInt("routeIdx") == obj.getJSONObject("JourneyDetail").getJSONObject("JourneyType").getInt("routeIdxTo")) {
					pt.logJourneyToDB();	//Journey ended, to DB
					System.out.println("Logged");
					this.cancel();
					return;
				}
				whatStop++;
				pt.setCurrentStop(whatStop);
			}
			int delay = calcTimeDifference(arr.getJSONObject(whatStop));
			System.out.println(pt.getDirection() + ", Stop: "+ whatStop + " , Delay: " + delay);
			pt.setDelay(delay);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	private void findNewLink() {
		Trip trip = initLocalTrip();
		JSONObject jsonobj = null;
		try {
			jsonobj = trip.executeRequest().getJSONObject("TripList");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		Object objTrip = jsonobj.get("Trip");
		JSONArray  tripArray;
		JSONObject tripObject;
		if(objTrip instanceof JSONArray) {
			tripArray = (JSONArray)objTrip;
			for (int i = 0; i < tripArray.length(); i++) {
				Object objLeg = tripArray.getJSONObject(i).get("Leg");
				checkLegObj(objLeg,true);
			}
		}
		else if(objTrip instanceof JSONObject) {
			tripObject = (JSONObject)objTrip;
			Object objLeg = tripObject.get("Leg");
			checkLegObj(objLeg,true);
		}
		else {
			System.out.println("Ingen array eller object (Trip)");
		}
	}
	
	
	private void checkCancelled() {
		Trip trip = initLocalTrip();
		JSONObject jsonobj = null;
		try {
			jsonobj = trip.executeRequest().getJSONObject("TripList");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		Object objTrip = jsonobj.get("Trip");
		JSONArray  tripArray;
		JSONObject tripObject;
		if(objTrip instanceof JSONArray) {
			tripArray = (JSONArray)objTrip;
			for (int i = 0; i < tripArray.length(); i++) {
				Object objLeg = tripArray.getJSONObject(i).get("Leg");
				checkLegObj(objLeg,false);
			}
		}
		else if(objTrip instanceof JSONObject) {
			tripObject = (JSONObject)objTrip;
			Object objLeg = tripObject.get("Leg");
			checkLegObj(objLeg,false);
		}
		else {
			System.out.println("Ingen array eller object (Trip)");
		}
	}
	
	
	private Trip initLocalTrip() {
		Trip trip = new Trip(pt.getJourneyDetail().getToken());
		trip.setOriginId(pt.getStops().get(0).getStopId());
		trip.setDestId(pt.getStops().get(pt.getStops().size()-1).getStopId());
		trip.setDate(pt.getDate());
		trip.setTime(pt.getStartTime());
		trip.setUseBus(false);
		trip.setUseLongDistanceTrain(false);
		trip.setUseRegionalTrain(false);
		trip.setUseTram(false);
		trip.setMaxChanges(0);
		trip.setOriginWalk(false);
		trip.setDestWalk(false);
		return trip;
	}
	
	
	private void checkLegObj(Object objLeg, boolean findNewLink) {
		JSONArray  legArray;
		JSONObject legObject;
		if(objLeg instanceof JSONArray) {
			legArray = (JSONArray)objLeg;
			for (int j = 0; j < legArray.length(); j++) {
				checkLeg(legArray.getJSONObject(j),findNewLink);
			}
		}
		else if(objLeg instanceof JSONObject) {
			legObject = (JSONObject)objLeg;
			checkLeg(legObject,findNewLink);
		}
		else {
			System.out.println("Ingen array eller object (Leg)");
		}
	}
	
	private void checkLeg(JSONObject legObj, boolean findNewLink) {
		if(legObj.has("id") && legObj.getString("id").equals(pt.getJourneyid())) {
			if(legObj.has("cancelled") && legObj.getBoolean("cancelled") == true) {
				pt.printJourney();
				this.cancel();
			}
			checkNotes(legObj);
			if(findNewLink) {
				JourneyDetail temp = new JourneyDetail(pt.getJourneyDetail().getToken(),legObj.getJSONObject("JourneyDetailRef").getString("ref"));
				try {
					pt = new PublicTransportation(temp);
				} catch (JSONException | IOException e) {
					System.out.println("Tried new link, doesn't work");
					System.out.println(e.getMessage());
					this.cancel();
					return;
				}
			}
		}
	}
	
	
	private void checkNotes(JSONObject legObj) {
		if(legObj.getJSONObject("Origin").has("Notes")) {
			if(legObj.getJSONObject("Origin").getJSONObject("Notes").get("Note") instanceof JSONObject) {
				String message = legObj.getJSONObject("Origin").getJSONObject("Notes").getJSONObject("Note").getString("$");
				pt.setOriginNote(message);
			}
			else if(legObj.getJSONObject("Origin").getJSONObject("Notes").get("Note") instanceof JSONArray) {
				String message = "";
				JSONArray noteArr = legObj.getJSONObject("Origin").getJSONObject("Notes").getJSONArray("Note");
				for(int i = 0; i < noteArr.length(); i++) {
					message += "\n" + noteArr.getJSONObject(i).getString("$") ;
				}
				pt.setOriginNote(message);
			}
		}
		if(legObj.getJSONObject("Destination").has("Notes")) {
			if(legObj.getJSONObject("Destination").getJSONObject("Notes").get("Note") instanceof JSONObject) {
				String message = legObj.getJSONObject("Destination").getJSONObject("Notes").getJSONObject("Note").getString("$");
				pt.setDestNote(message);
			}
			else if(legObj.getJSONObject("Destination").getJSONObject("Notes").get("Note") instanceof JSONArray) {
				String message = "";
				JSONArray noteArr = legObj.getJSONObject("Destination").getJSONObject("Notes").getJSONArray("Note");
				for(int i = 0; i < noteArr.length(); i++) {
					message += "\n" + noteArr.getJSONObject(i).getString("$") ;
				}
				pt.setDestNote(message);
			}
		}
	}
	
	
	private int calcTimeDifference(JSONObject journeystop) {
		String time1;
		String time2;
		String date1;
		String date2;
		if(pt.getCurrentStop() != pt.getStops().size()-1) {
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
