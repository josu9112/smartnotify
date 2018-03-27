package snotify;

import java.io.IOException;
import java.util.TimerTask;

import org.joda.time.LocalTime;
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
				arr = obj.getJSONObject("JourneyDetail").getJSONArray("Stop");
			}
			int whatStop = pt.getCurrentStop();
			if(whatStop == 0) {
				checkCancelled();
			}
			while(!arr.getJSONObject(whatStop).has("rtDepTime") && !arr.getJSONObject(whatStop).has("rtArrTime")) {
				if(arr.getJSONObject(whatStop).getInt("routeIdx") == obj.getJSONObject("JourneyDetail").getJSONObject("JourneyType").getInt("routeIdxTo")) {
					pt.printJourney();	//Journey ended, prints it out
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
		JSONObject obj = null;
		try {
			obj = trip.executeRequest().getJSONObject("TripList");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		JSONArray arr = obj.getJSONArray("Trip");
		for(int i = 0; i < arr.length(); i++) {
			try {
				obj = arr.getJSONObject(i).getJSONObject("Leg");
			}catch(Exception e) {
				JSONArray arr2 = arr.getJSONObject(i).getJSONArray("Leg");
				for(int j = 0; j < arr2.length(); j++) {
					if(arr2.getJSONObject(j).has("id") && 
							arr2.getJSONObject(j).getString("id").equals(pt.getJourneyid())) {
						if(arr2.getJSONObject(j).has("cancelled") && 
								arr.getJSONObject(j).getBoolean("cancelled") == true) {
							pt.printJourney();
							this.cancel();
							return;
						}
						else if(arr2.getJSONObject(j).getJSONObject("Leg").getJSONObject("Origin").has("Notes")) {
							String message = arr2.getJSONObject(j).getJSONObject("Leg").getJSONObject("Origin").getJSONObject("Notes").getJSONObject("Note").getString("$");
							pt.setNote(message);
						}
						else if(arr2.getJSONObject(j).getJSONObject("Leg").getJSONObject("Destination").has("Notes")) {
							String message = arr2.getJSONObject(j).getJSONObject("Leg").getJSONObject("Destination").getJSONObject("Notes").getJSONObject("Note").getString("$");
							pt.setNote(message);
						}
						JourneyDetail temp = new JourneyDetail(pt.getJourneyDetail().getToken(),arr2.getJSONObject(i)
								.getJSONObject("JourneyDetailRef").getString("ref"));
						try {
							pt = new PublicTransportation(temp);
						} catch (JSONException | IOException d) {
							System.out.println("Tried new link, doesn't work");
							System.out.println(d.getMessage());
							this.cancel();
							return;
						}
						return;
					}
				}
				continue;
			}
			if(arr.getJSONObject(i).getJSONObject("Leg").getString("id").equals(pt.getJourneyid())) {
				if(arr.getJSONObject(i).getJSONObject("Leg").has("cancelled") && 
						arr.getJSONObject(i).getJSONObject("Leg").getBoolean("cancelled") == true) {
					pt.printJourney();
					this.cancel();
					return;
				}
				else if(arr.getJSONObject(i).getJSONObject("Leg").getJSONObject("Origin").has("Notes")) {
					String message = arr.getJSONObject(i).getJSONObject("Leg").getJSONObject("Origin").getJSONObject("Notes").getJSONObject("Note").getString("$");
					pt.setNote(message);
				}
				else if(arr.getJSONObject(i).getJSONObject("Leg").getJSONObject("Destination").has("Notes")) {
					String message = arr.getJSONObject(i).getJSONObject("Leg").getJSONObject("Destination").getJSONObject("Notes").getJSONObject("Note").getString("$");
					pt.setNote(message);
				}
				JourneyDetail temp = new JourneyDetail(pt.getJourneyDetail().getToken(),arr.getJSONObject(i)
						.getJSONObject("Leg").getJSONObject("JourneyDetailRef").getString("ref"));
				try {
					pt = new PublicTransportation(temp);
				} catch (JSONException | IOException e) {
					System.out.println("Tried new link, doesn't work");
					System.out.println(e.getMessage());
					this.cancel();
					return;
				}
				break;
			}
		}
	}
	
	private void checkCancelled() {
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
		JSONObject obj = null;
		try {
			obj = trip.executeRequest().getJSONObject("TripList");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		JSONArray arr = obj.getJSONArray("Trip");
		for(int i = 0; i < arr.length(); i++) {
			try {
				obj = arr.getJSONObject(i).getJSONObject("Leg");
			}catch(Exception e) {
				JSONArray arr2 = arr.getJSONObject(i).getJSONArray("Leg");
				for(int j = 0; j < arr2.length(); j++) {
					if(arr2.getJSONObject(j).has("id") && 
							arr2.getJSONObject(j).getString("id").equals(pt.getJourneyid())) {
						if(arr2.getJSONObject(j).has("cancelled") && 
								arr2.getJSONObject(j).getBoolean("cancelled") == true) {
							pt.printJourney();
							this.cancel();
						}
						else if(arr2.getJSONObject(j).getJSONObject("Leg").getJSONObject("Origin").has("Notes")) {
							String message = arr2.getJSONObject(j).getJSONObject("Leg").getJSONObject("Origin").getJSONObject("Notes").getJSONObject("Note").getString("$");
							pt.setNote(message);
						}
						else if(arr2.getJSONObject(j).getJSONObject("Leg").getJSONObject("Destination").has("Notes")) {
							String message = arr2.getJSONObject(j).getJSONObject("Leg").getJSONObject("Destination").getJSONObject("Notes").getJSONObject("Note").getString("$");
							pt.setNote(message);
						}
						return;
					}
				}
				continue;
			}
			if(arr.getJSONObject(i).getJSONObject("Leg").getString("id").equals(pt.getJourneyid())) {
				if(arr.getJSONObject(i).getJSONObject("Leg").has("cancelled") && 
						arr.getJSONObject(i).getJSONObject("Leg").getBoolean("cancelled") == true) {
					pt.printJourney();
					this.cancel();
				}
				else if(arr.getJSONObject(i).getJSONObject("Leg").getJSONObject("Origin").has("Notes")) {
					String message = arr.getJSONObject(i).getJSONObject("Leg").getJSONObject("Origin").getJSONObject("Notes").getJSONObject("Note").getString("$");
					pt.setNote(message);
				}
				else if(arr.getJSONObject(i).getJSONObject("Leg").getJSONObject("Destination").has("Notes")) {
					String message = arr.getJSONObject(i).getJSONObject("Leg").getJSONObject("Destination").getJSONObject("Notes").getJSONObject("Note").getString("$");
					pt.setNote(message);
				}
				return;
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
