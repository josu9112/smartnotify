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

public class CollectJourneys extends TimerTask {

	private ArrayList<DepartureBoard> departures;
	private Token token;

	public CollectJourneys(Token token) {
		this.token = token;
		departures = new ArrayList<DepartureBoard>();
		DepartureBoard gbg1 = new DepartureBoard(token);
		gbg1.setStartId("9021014008000000");
		gbg1.setStopId("9021014019110000");
		DepartureBoard gbg2 = new DepartureBoard(token);
		gbg2.setStartId("9021014008000000");
		gbg2.setStopId("9021014016611000");
		DepartureBoard gbg3 = new DepartureBoard(token);
		gbg3.setStartId("9021014008000000");
		gbg3.setStopId("9021014017510000");
		DepartureBoard gbg4 = new DepartureBoard(token);
		gbg4.setStartId("9021014008000000");
		gbg4.setStopId("9021014080802000");
		DepartureBoard kba = new DepartureBoard(token);
		kba.setStartId("9021014019110000");
		kba.setStopId("9021014008000000");
		DepartureBoard alvangen = new DepartureBoard(token);
		alvangen.setStartId("9021014016611000");
		alvangen.setStopId("9021014008000000");
		DepartureBoard alingsas = new DepartureBoard(token);
		alingsas.setStartId("9021014017510000");
		alingsas.setStopId("9021014008000000");
		DepartureBoard vanersborg = new DepartureBoard(token);
		vanersborg.setStartId("9021014080802000");
		vanersborg.setStopId("9021014008000000");

		departures.add(gbg1);
		departures.add(gbg2);
		departures.add(gbg3);
		departures.add(gbg4);
		departures.add(kba);
		departures.add(alvangen);
		departures.add(alingsas);
		departures.add(vanersborg);
	}

	@Override
	public void run() {
		for (int k = 0; k < departures.size(); k++) {
			DepartureBoard departure = departures.get(k);
			departure.setTime("00:00");
			departure.setUseLongDistanceTrain(false);
			departure.setUseRegionalTrain(false);
			departure.setUseBus(false);
			departure.setTimeSpan(1440);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, 1);
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			String date = format1.format(cal.getTime());
			departure.setDate(date);

			JSONObject obj = null;
			try {
				obj = departure.executeRequest().getJSONObject("DepartureBoard");
			} catch (JSONException | IOException e1) {
				break;
			}
			JSONArray arr = null;
			if (obj != null) {
				try {
					arr = obj.getJSONArray("Departure");
				} catch (Exception e) {
					obj = obj.getJSONObject("Departure");
				}
			}

			if (arr != null) {
				for (int j = 0; j < arr.length(); j++) {
					JSONObject tempObj = arr.getJSONObject(j);
					String dir = arr.getJSONObject(j).getString("direction");
					if (!tempObj.has("booking")) {
						try {
							PublicTransportation temp = new PublicTransportation(
									new JourneyDetail(token, tempObj.getJSONObject("JourneyDetailRef").getString("ref")));
							Timer timerTemp = new Timer();
							timerTemp.schedule(new CheckJourney(temp), getDate(temp), 60 * 1000);
						} catch (JSONException | IOException e) {
							e.printStackTrace();
						}
					}
				}
			} else if (obj != null) {
				if (!obj.has("booking")) {
					try {
						PublicTransportation temp = new PublicTransportation(
								new JourneyDetail(token, obj.getJSONObject("JourneyDetailRef").getString("ref")));
						Timer timerTemp = new Timer();
						timerTemp.schedule(new CheckJourney(temp), getDate(temp), 60 * 1000);
					} catch (JSONException | IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
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
