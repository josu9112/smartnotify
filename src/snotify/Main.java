package snotify;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

	private static ArrayList<PublicTransportation> pt;

	public static void main(String[] args) throws IOException {
		Scanner scan = new Scanner(new FileReader("C:\\Users\\John\\Desktop\\vast.txt"));
		String id = scan.nextLine();
		id = id.substring(id.indexOf(" ") + 1);
		String secret = scan.nextLine();
		secret = secret.substring(secret.indexOf(" ") + 1);
		scan.close();
		Token token = new Token(id, secret);

//		pt = new ArrayList<PublicTransportation>();
		
		Timer cj = new Timer();
		cj.schedule(new CollectJourneys(token), 1440*1000);


		/* Kod för att få ut alla linjer från en station, alla veckodagar */

		
//		ArrayList<DepartureBoard> departures = new ArrayList<DepartureBoard>();
//		DepartureBoard gbg1 = new DepartureBoard(token);
//		gbg1.setStartId("9021014008000000");
//		gbg1.setStopId("9021014019110000");
//		DepartureBoard gbg2 = new DepartureBoard(token);
//		gbg2.setStartId("9021014008000000");
//		gbg2.setStopId("9021014016611000");
//		DepartureBoard gbg3 = new DepartureBoard(token);
//		gbg3.setStartId("9021014008000000");
//		gbg3.setStopId("9021014017510000");
//		DepartureBoard gbg4 = new DepartureBoard(token);
//		gbg4.setStartId("9021014008000000");
//		gbg4.setStopId("9021014080802000");
//		DepartureBoard kba = new DepartureBoard(token);
//		kba.setStartId("9021014019110000");
//		kba.setStopId("9021014008000000");
//		DepartureBoard alvangen = new DepartureBoard(token);
//		alvangen.setStartId("9021014016611000");
//		alvangen.setStopId("9021014008000000");
//		DepartureBoard alingsas = new DepartureBoard(token);
//		alingsas.setStartId("9021014017510000");
//		alingsas.setStopId("9021014008000000");
//		DepartureBoard vanersborg = new DepartureBoard(token);
//		vanersborg.setStartId("9021014080802000");
//		vanersborg.setStopId("9021014008000000");
//		
//		departures.add(gbg1);
//		departures.add(gbg2);
//		departures.add(gbg3);
//		departures.add(gbg4);
//		departures.add(kba);
//		departures.add(alvangen);
//		departures.add(alingsas);
//		departures.add(vanersborg);
		
		
//		ArrayList<String> stopids = new ArrayList<String>();
//		stopids.add("9021014008000000"); // GBG
//		stopids.add("9021014019110000"); // KBA
//		stopids.add("9021014016611000"); // Älvängen
//		stopids.add("9021014017510000"); // Alingsås
//		stopids.add("9021014080802000"); // Vänersborg
//
//		DepartureBoard dep = new DepartureBoard(token);

//		for (int k = 0; k < departures.size(); k++) {
//			DepartureBoard departure = departures.get(k);
//			departure.setTime("00:00");
//			departure.setUseLongDistanceTrain(false);
//			departure.setUseRegionalTrain(false);
//			departure.setUseBus(false);
//			departure.setTimeSpan(1440);
//			String date = "2018-03-19";
//			int day = 19;
//			System.out.println("Station k = " + k);
//			for (int i = 19; i <= 25; i++) {
//				System.out.println("day i = " + i);
//				date = date.substring(0, date.length() - 2) + day;
//				departure.setDate(date);
//
//				JSONObject obj = departure.executeRequest().getJSONObject("DepartureBoard");
//				JSONArray arr = null;
//				if (obj != null) {
//					try {
//						arr = obj.getJSONArray("Departure");
//					} catch (Exception e) {
//						obj = obj.getJSONObject("Departure");
//					}
//				}
//
//				if (arr != null) {
//					for (int j = 0; j < arr.length(); j++) {
//						JSONObject tempObj = arr.getJSONObject(j);
//						String dir = arr.getJSONObject(j).getString("direction");
//							if (!tempObj.has("booking")) {
//								PublicTransportation temp = new PublicTransportation(new JourneyDetail(token,
//										tempObj.getJSONObject("JourneyDetailRef").getString("ref")));
//								addPT(temp, date);
//							}
//					}
//				} else if (obj != null) {
//					if (!obj.has("booking")) {
//						PublicTransportation temp = new PublicTransportation(
//								new JourneyDetail(token, obj.getJSONObject("JourneyDetailRef").getString("ref")));
//						addPT(temp, date);
//					}
//				}
//				day++;
//			}
//		}
//
//		ArrayList<PublicTransportation> trips = new ArrayList<PublicTransportation>();
//
//		for (PublicTransportation a : pt) {
//			Boolean exists = false;
//			for (PublicTransportation b : trips) {
//				if (a.getStartStopName().equals(b.getStartStopName()) && a.getEndStopName().equals(b.getEndStopName())
//						&& a.getTotalTime() == b.getTotalTime()) {
//					exists = true;
//					break;
//				}
//			}
//			if (!exists)
//				trips.add(a);
//		}
//
//		ArrayList<String> allstops = new ArrayList<String>();
//		PrintWriter writer = new PrintWriter("vasttaglinjer.csv");
//		writer.print("Totaltime," + "Distance," + "nrOfStops," + "[Stops]");
//		for (PublicTransportation a : trips) {
//			writer.println();
//			writer.print(a.getTotalTime());
//			writer.print("," + ((int) a.getDistance()));
//			writer.print("," + a.getStops().size() + ",");
//			ArrayList<Stop> stops = a.getStops();
//			for (int i = 0; i < stops.size(); i++) {
//				if (!allstops.contains(stops.get(i).getStopName()))
//					allstops.add(stops.get(i).getStopName());
//
//				if (i == 0)
//					writer.print("[\"" + stops.get(i).getStopName() + "\",");
//				else if (i == stops.size() - 1) {
//					writer.print("\"" + stops.get(i).getStopName() + "\"]");
//				} else
//					writer.print("\"" + stops.get(i).getStopName() + "\",");
//			}
//		}
//		writer.close();
//
//		PrintWriter writerstops = new PrintWriter("allstops.csv");
//		for (String a : allstops)
//			writerstops.println(a);
//		writerstops.close();

		// JSONObject obj = dep.executeRequest();
		// JSONArray arr = null;
		// if(obj != null) {
		// try {
		// arr = obj.getJSONArray("Departure");
		// }catch(Exception e) {
		// obj = obj.getJSONObject("Departure");
		// }
		// }
		// else {
		// System.out.println("No objects!");
		// }
		//
		// if(arr != null) {
		// for(int i = 0; i < arr.length(); i ++)
		// System.out.println(arr.getJSONObject(i).toString());
		// }
		// else if(obj != null) {
		// System.out.println(obj.toString());
		// }

		/* Loggning */

		// Brunnsparken 9021014001760000
		// GBG C 9021014008000000
		// KBA 9021014019110000
		// Älvängen 9021014016611000
		// Alingsås 9021014017510000
		// Vänersborg 9021014080802000

//		ArrayList<String> stops = new ArrayList<String>();
//		stops.add("9021014008000000"); // GBG
//		stops.add("9021014019110000"); // KBA
//		stops.add("9021014016611000"); // Älvängen
//		stops.add("9021014017510000"); // Alingsås
//		stops.add("9021014080802000"); // Vänersborg

//		ArrayList<Timer> timers = new ArrayList<Timer>();
//
//		for (int i = 0; i < stops.size(); i++) {
//			DepartureBoard dep = new DepartureBoard(token);
//			dep.setStartId(stops.get(i));
//			LocalTime time = new LocalTime();
//			dep.setTime(time.getHourOfDay() + ":" + (time.getMinuteOfHour() + 3));
//			dep.setUseLongDistanceTrain(false);
//			dep.setUseRegionalTrain(false);
//			dep.setUseBus(false);
//			dep.setDate("2018-03-20");
//			dep.setTimeSpan(540);
//			JSONObject obj = dep.executeRequest();
//			JSONArray arr = null;
//			try {
//				arr = obj.getJSONObject("DepartureBoard").getJSONArray("Departure");
//			} catch (Exception e) {
//				System.out.println("Inga åkturer");
//			}
//			for (int j = 0; j < arr.length(); j++) {
//				if (i == 0) {
//					String dir = arr.getJSONObject(j).getString("direction");
//					if (dir.equals("Kungsbacka") || dir.equals("Alingsås") || dir.equals("Älvängen")
//							|| dir.equals("Vänersborg")) {
//						JourneyDetail jd = new JourneyDetail(token,
//								arr.getJSONObject(j).getJSONObject("JourneyDetailRef").get("ref").toString());
//						String startDepTime = jd.executeRequest().getJSONObject("JourneyDetail").getJSONArray("Stop")
//								.getJSONObject(0).getString("depTime");
//						if (compareTime(startDepTime, time.getHourOfDay() + ":" + time.getMinuteOfHour())) {
//							PublicTransportation temp = new PublicTransportation(jd);
//							Timer timerTemp = new Timer();
//							timerTemp.schedule(new CheckJourney(temp), getDate(temp), 60 * 1000);
//							timers.add(timerTemp);
//						}
//					}
//				} else {
//					if (arr.getJSONObject(j).getString("direction").equals("Göteborg")) {
//
//						JourneyDetail jd = new JourneyDetail(token,
//								arr.getJSONObject(j).getJSONObject("JourneyDetailRef").get("ref").toString());
//						String startDepTime = jd.executeRequest().getJSONObject("JourneyDetail").getJSONArray("Stop")
//								.getJSONObject(0).getString("depTime");
//						if (compareTime(startDepTime, time.getHourOfDay() + ":" + time.getMinuteOfHour())) {
//							PublicTransportation temp = new PublicTransportation(jd);
//							Timer timerTemp = new Timer();
//							timerTemp.schedule(new CheckJourney(temp), getDate(temp), 60 * 1000);
//							timers.add(timerTemp);
//						}
//					}
//				}
//			}
//		}

		/* Sålla stationer */
		// Scanner scan = new Scanner(new
		// FileReader("C:\\Users\\John\\workspace\\snotify\\allstops3.txt"));
		// StringBuilder builder = new StringBuilder();
		// while(scan.hasNextLine())
		// builder.append(scan.nextLine());
		// scan.close();
		// String trimmedstring = builder.toString();
		//
		// JSONArray arr = new JSONObject(trimmedstring).getJSONArray("StopLocation");
		// JSONArray noDuplicates = new JSONArray();
		//
		// PrintWriter writer2 = new PrintWriter("allstops1.txt");
		// writer2.print("{\"StopLocation\":[");
		// for(int i = 0; i < arr.length(); i++) {
		// JSONObject obj = arr.getJSONObject(i);
		//
		// if(!obj.has("track")) {
		// if(i != 0)
		// writer2.print("," + arr.get(i).toString());
		// else
		// writer2.print(arr.get(i).toString());
		// }
		// }
		// writer2.print("]}");
		// writer2.close();

	}

	private static void addPT(PublicTransportation toBeChecked, String date) {

		for (PublicTransportation a : pt) {
			if (a.compareTo(toBeChecked)) {
				a.setWeekdayDay(date);
				return;
			}
		}
		toBeChecked.setWeekdayDay(date);
		pt.add(toBeChecked);
	}

	public static double calcDistance(JSONArray geometry) {
		GeodeticCalculator geoCalc = new GeodeticCalculator();
		Ellipsoid reference = Ellipsoid.WGS84;
		double distance = 0;
		for (int i = 0; i < geometry.length() - 1; i++) {
			JSONObject temp = geometry.getJSONObject(i);
			JSONObject temp2 = geometry.getJSONObject(i + 1);
			GlobalPosition pointA = new GlobalPosition(temp.getDouble("lat"), temp.getDouble("lon"), 0.0); // Point A
			GlobalPosition userPos = new GlobalPosition(temp2.getDouble("lat"), temp2.getDouble("lon"), 0.0); // Point B
			distance += geoCalc.calculateGeodeticCurve(reference, userPos, pointA).getEllipsoidalDistance(); // Distance between Point A and Point B
		}
		return distance;
	}

	public static int calcJourneyTime(JSONArray journeystops) {
		String timeStart = journeystops.getJSONObject(0).getString("depTime");
		String timeFinish = journeystops.getJSONObject(journeystops.length() - 1).getString("arrTime");

		if (journeystops.getJSONObject(0).get("depDate").toString()
				.equals(journeystops.getJSONObject(journeystops.length() - 1).get("arrDate").toString())) {
			int hours = Integer.parseInt(timeFinish.substring(0, 2)) - Integer.parseInt(timeStart.substring(0, 2));
			int minutes = Integer.parseInt(timeFinish.substring(3, 5)) - Integer.parseInt(timeStart.substring(3, 5));
			return (hours * 60) + minutes;
		} else {
			int beforeMidnight = ((24 - Integer.parseInt(timeStart.substring(0, 2))) * 60)
					- Integer.parseInt(timeStart.substring(3, 5));
			int afterMidnight = Integer.parseInt(timeFinish.substring(0, 2)) * 60
					+ Integer.parseInt(timeFinish.substring(3, 5));
			return beforeMidnight + afterMidnight;
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

	public static boolean compareTime(String depTime, String otherTime) {
		int depTimeHour = Integer.parseInt(depTime.substring(0, 2));
		int depTimeMinute = Integer.parseInt(depTime.substring(3, 5));
		int otherTimeHour = Integer.parseInt(otherTime.substring(0, otherTime.indexOf(":")));
		int otherTimeMinute = Integer.parseInt(otherTime.substring(otherTime.indexOf(":") + 1, otherTime.length()));

		if (otherTimeHour < depTimeHour)
			return true;
		else if (otherTimeHour == depTimeHour && otherTimeMinute < depTimeMinute)
			return true;
		else
			return false;
	}
}
