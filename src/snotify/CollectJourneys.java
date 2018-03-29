package snotify;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CollectJourneys {

	private Trip temp;
	private ArrayList<Trip> trips;
	private ArrayList<PublicTransportation> transports;
	private Token token;
	private String date;

	public CollectJourneys(Token token) {
		this.token = token;
		this.trips = new ArrayList<Trip>();
		Trip gbg1 = new Trip(token);
		gbg1.setOriginId("9021014008000000");
		gbg1.setDestId("9021014019110000");
		Trip gbg2 = new Trip(token);
		gbg2.setOriginId("9021014008000000");
		gbg2.setDestId("9021014016611000");
		Trip gbg3 = new Trip(token);
		gbg3.setOriginId("9021014008000000");
		gbg3.setDestId("9021014017510000");
		Trip gbg4 = new Trip(token);
		gbg4.setOriginId("9021014008000000");
		gbg4.setDestId("9021014080802000");
		Trip kba = new Trip(token);
		kba.setOriginId("9021014019110000");
		kba.setDestId("9021014008000000");
		Trip alvangen = new Trip(token);
		alvangen.setOriginId("9021014016611000");
		alvangen.setDestId("9021014008000000");
		Trip alingsas = new Trip(token);
		alingsas.setOriginId("9021014017510000");
		alingsas.setDestId("9021014008000000");
		Trip vanersborg = new Trip(token);
		vanersborg.setOriginId("9021014080802000");
		vanersborg.setDestId("9021014008000000");

		trips.add(gbg1);
		trips.add(gbg2);
		trips.add(gbg3);
		trips.add(gbg4);
		trips.add(kba);
		trips.add(alvangen);
		trips.add(alingsas);
		trips.add(vanersborg);
		
		runMethod();
	}

	public void runMethod() {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		date = format1.format(cal.getTime());

		transports = new ArrayList<PublicTransportation>();

		for (int k = 0; k < trips.size(); k++) {
			temp = trips.get(k);
			temp.setDate(date);
			temp.setTime("00:00");
			temp.setUseBus(false);
			temp.setUseLongDistanceTrain(false);
			temp.setUseRegionalTrain(false);
			temp.setUseTram(false);
			temp.setMaxChanges(0);

			Boolean notAllTrips = true;
			while (notAllTrips) {
				JSONObject jsonobj = null;
				try {
					jsonobj = temp.executeRequest().getJSONObject("TripList");
				} catch (JSONException | IOException e) {
					break;
				}
				Object trip = null;
				try {
					trip = jsonobj.get("Trip");
				}catch(Exception e) {
					System.out.println(e.getMessage());
					break;
				}
				JSONArray  tripArray;
				JSONObject tripObject;
				if(trip instanceof JSONArray) {
					tripArray = (JSONArray)trip;
					for (int i = 0; i < tripArray.length(); i++) {
						if(!checkJSONObject(tripArray.getJSONObject(i))) {
							notAllTrips = false;
							break;
						}
					}
				}
				else if(trip instanceof JSONObject) {
					tripObject = (JSONObject)trip;
					if(!checkJSONObject(tripObject))
						break;
				}
				else {
					System.out.println("Ingen array eller object");
					break;
				}
			}
		}

		for (PublicTransportation a : transports) {
			Timer timer = new Timer();
			timer.schedule(new CheckJourney(a), getDate(a), 60 * 1000);
		}
		System.out.println("Journeys collected");
	}

	private boolean checkJSONObject(JSONObject tripObject) {
		if (!tripObject.getJSONObject("Leg").getJSONObject("Origin").getString("date")
				.equals(this.date)) {
			return false;
		} else {
			try {
				transports.add(new PublicTransportation(new JourneyDetail(this.token, tripObject.getJSONObject("Leg")
						.getJSONObject("JourneyDetailRef").getString("ref"))));
			} catch (JSONException | IOException e) {
				System.out.println(e.getMessage());
			}
		}
		String time[] = tripObject.getJSONObject("Leg").getJSONObject("Origin")
				.getString("time").split(":");
		int hours = Integer.parseInt(time[0]);
		int minutes = Integer.parseInt(time[1]);
		int totalTime = (hours * 60) + minutes + 1;
		hours = (totalTime / 60) % 24;
		minutes = totalTime % 60;
		if (minutes < 10)
			temp.setTime(hours + ":0" + minutes);
		else
			temp.setTime(hours + ":" + minutes);
		return true;
	}
	
	
	
	
	public static Date getDate(PublicTransportation pt) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(Integer.parseInt(pt.getDate().substring(0, 4)), Integer.parseInt(pt.getDate().substring(5, 7)) - 1,
				Integer.parseInt(pt.getDate().substring(8, 10)), Integer.parseInt(pt.getStartTime().substring(0, 2)),
				Integer.parseInt(pt.getStartTime().substring(3, 5)), 00);
		return cal.getTime();
	}

}
