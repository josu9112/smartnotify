package snotify;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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
import org.json.JSONException;
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
		
//		Timer timer = new Timer();
//		timer.schedule(new CollectJourneys(token), 1440*60*1000);

		ArrayList<Trip> trips = new ArrayList<Trip>();
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

		ArrayList<PublicTransportation> transports = new ArrayList<PublicTransportation>();

		for (int k = 0; k < trips.size(); k++) {
//			System.out.println("Station: " + k);
			Trip temp = trips.get(k);
//			for (int j = 0; j < 7; j++) {
//				System.out.println("Dag: " + j);
//				Calendar cal = Calendar.getInstance();
//				cal.add(Calendar.DATE, -1 + j);
//				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				String date = "2018-03-27";
				temp.setDate(date);
				temp.setTime("22:30");
				temp.setUseBus(false);
				temp.setUseLongDistanceTrain(false);
				temp.setUseRegionalTrain(false);
				temp.setUseTram(false);
				temp.setMaxChanges(0);

				Boolean notAllTrips = true;
				while (notAllTrips) {
					JSONObject obj = null;
					try {
						obj = temp.executeRequest().getJSONObject("TripList");
					} catch (JSONException | IOException e1) {
						System.out.println(e1.getMessage());
					}
					try {
						JSONArray arr = obj.getJSONArray("Trip");
						for (int i = 0; i < arr.length(); i++) {
							if (!arr.getJSONObject(i).getJSONObject("Leg").getJSONObject("Origin").getString("date")
									.equals(date)) {
								notAllTrips = false;
								break;
							} else {
								try {
									PublicTransportation pt1 = new PublicTransportation(
											new JourneyDetail(token, arr.getJSONObject(i).getJSONObject("Leg")
													.getJSONObject("JourneyDetailRef").getString("ref")));
//									LocalTime lTime = new LocalTime();
//									if(compareTime(pt1.getStartTime(), lTime.getHourOfDay() + ":" + lTime.getMinuteOfHour()))
										transports.add(pt1);
								} catch (JSONException | IOException e) {
									System.out.println(e.getMessage());
								}
							}
							if (i == arr.length() - 1) {
								String time[] = arr.getJSONObject(i).getJSONObject("Leg").getJSONObject("Origin")
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
							}
						}
						if (!notAllTrips)
							break;
					} catch (Exception e) {
						try {
						obj = obj.getJSONObject("Trip");
						}catch(Exception e3) {
							break;
						}
						if (!obj.getJSONObject("Leg").getJSONObject("Origin").getString("date").equals(date)) {
							notAllTrips = false;
							break;
						} else {
							try {
								PublicTransportation pt1 = new PublicTransportation(
										new JourneyDetail(token, obj.getJSONObject("Leg")
												.getJSONObject("JourneyDetailRef").getString("ref")));
//								LocalTime lTime = new LocalTime();
//								if(compareTime(pt1.getStartTime(), lTime.getHourOfDay() + ":" +lTime.getMinuteOfHour()))
									transports.add(pt1);
							} catch (JSONException | IOException e2) {
								System.out.println(e2.getMessage());
							}
						}
						String time[] = obj.getJSONObject("Leg").getJSONObject("Origin").getString("time").split(":");
						int hours = Integer.parseInt(time[0]);
						int minutes = Integer.parseInt(time[1]);
						int totalTime = (hours * 60) + minutes + 1;
						hours = (totalTime / 60) % 24;
						minutes = totalTime % 60;
						if (minutes < 10)
							temp.setTime(hours + ":0" + minutes);
						else
							temp.setTime(hours + ":" + minutes);
					}
					if (!notAllTrips)
						break;
				}
//			}
		}

		for(PublicTransportation a : transports) {
//			Timer timer = new Timer();
//			timer.schedule(new CheckJourney(a), getDate(a), 60*1000);
			a.logJourneyToDB();
		}
		
//		ArrayList<String> allstops = new ArrayList<String>();
//		ArrayList<PublicTransportation> added = new ArrayList<PublicTransportation>();
//		for (PublicTransportation a : transports) {
//			Boolean exists = false;
//			for(int i = 0; i < added.size(); i++) {
//				if(added.get(i).getTotalTime() == a.getTotalTime() && added.get(i).getStops().size() == a.getStops().size()
//						&& added.get(i).getStops().get(0).getStopName().equals(a.getStops().get(0).getStopName()) &&
//						added.get(i).getStops().get(added.get(i).getStops().size()-1).getStopName().equals(a.getStops().get(a.getStops().size()-1).getStopName())) {
//					exists = true;
//					break;
//				}
//			}
//			if(exists)
//				continue;
//			
//			added.add(a);
//			try {
//				insertToDB.insertInit((int)a.getDistance(), a.getTotalTime(), a.getStops());
//			} catch (SQLException e) {
//				System.out.println(e.getMessage());
//			}
//		}

		// for(PublicTransportation a : transports) {
		// System.out.println(a.getLinje() + "\t" + a.getStartStopName() + "\t" +
		// a.getEndStopName() + "\t" + a.getStartTime());
		// }

		// ArrayList<PublicTransportation> transportsDep = new
		// ArrayList<PublicTransportation>();
		// DepartureBoard kba = new DepartureBoard(token);
		// kba.setStartId("9021014019110000");
		// kba.setStopId("9021014008000000");
		// kba.setTime("15:45");
		// kba.setUseLongDistanceTrain(false);
		// kba.setUseRegionalTrain(false);
		// kba.setUseBus(false);
		// kba.setTimeSpan(60);
		// kba.setDate("2018-03-23");
		// JSONObject obj2 = kba.executeRequest();
		// JSONArray arr2 =
		// obj2.getJSONObject("DepartureBoard").getJSONArray("Departure");
		//
		// for(int i = 0; i < arr2.length(); i++) {
		// PublicTransportation temp = new PublicTransportation(new
		// JourneyDetail(token,arr2.getJSONObject(i).getJSONObject("JourneyDetailRef").getString("ref")));
		// Timer timerTemp = new Timer();
		// timerTemp.schedule(new CheckJourney(temp), getDate(temp), 60 * 1000);

		// if(transports.size() == transportsDep.size())
		// System.out.println("true");

		// for(int i = 0; i < transports.size(); i++) {
		// PublicTransportation fromTrip = transports.get(i);
		// PublicTransportation fromDep = transportsDep.get(i);
		//
		// if(!transports.get(i).getJourneyDetail().getJourneyDetailRefLink().equals(transportsDep.get(i).getJourneyDetail().getJourneyDetailRefLink()))
		// System.out.println("true");
		//
		// if(transports.get(i).getJourneyDetail().getJourneyDetailRefLink().equals(transportsDep.get(i).getJourneyDetail().getJourneyDetailRefLink()))
		// System.out.println("true");
		// }

		// pt = new ArrayList<PublicTransportation>();

		// Timer cj = new Timer();
		// cj.schedule(new CollectJourneys(token), 1440*1000);

		// DepartureBoard kba = new DepartureBoard(token);
		// kba.setStartId("9021014019110000");
		// kba.setTime("09:00");
		// kba.setUseLongDistanceTrain(false);
		// kba.setUseRegionalTrain(false);
		// kba.setUseBus(false);
		// departure.setTimeSpan(1440);
		// kba.setDate("2018-03-22");
		// JSONObject obj = kba.executeRequest();
		// JSONArray arr =
		// obj.getJSONObject("DepartureBoard").getJSONArray("Departure");
		//
		// for(int i = 0; i < arr.length(); i++)
		// System.out.println(arr.getJSONObject(i).toString());

		// JourneyDetail detail = new
		// JourneyDetail(token,"https://api.vasttrafik.se/bin/rest.exe/v2/journeyDetail?ref=299994%2F118802%2F969204%2F384604%2F80%3Fdate%3D2018-03-22%26station_evaId%3D19110001%26station_type%3Ddep%26format%3Djson%26");
		// JSONObject temp = detail.executeRequest();
		// System.out.println(temp.toString());

		/* Kod för att få ut alla linjer från en station, alla veckodagar */

		// ArrayList<DepartureBoard> departures = new ArrayList<DepartureBoard>();
		// DepartureBoard gbg1 = new DepartureBoard(token);
		// gbg1.setStartId("9021014008000000");
		// gbg1.setStopId("9021014019110000");
		// DepartureBoard gbg2 = new DepartureBoard(token);
		// gbg2.setStartId("9021014008000000");
		// gbg2.setStopId("9021014016611000");
		// DepartureBoard gbg3 = new DepartureBoard(token);
		// gbg3.setStartId("9021014008000000");
		// gbg3.setStopId("9021014017510000");
		// DepartureBoard gbg4 = new DepartureBoard(token);
		// gbg4.setStartId("9021014008000000");
		// gbg4.setStopId("9021014080802000");
		// DepartureBoard kba = new DepartureBoard(token);
		// kba.setStartId("9021014019110000");
		// kba.setStopId("9021014008000000");
		// DepartureBoard alvangen = new DepartureBoard(token);
		// alvangen.setStartId("9021014016611000");
		// alvangen.setStopId("9021014008000000");
		// DepartureBoard alingsas = new DepartureBoard(token);
		// alingsas.setStartId("9021014017510000");
		// alingsas.setStopId("9021014008000000");
		// DepartureBoard vanersborg = new DepartureBoard(token);
		// vanersborg.setStartId("9021014080802000");
		// vanersborg.setStopId("9021014008000000");
		//
		// departures.add(gbg1);
		// departures.add(gbg2);
		// departures.add(gbg3);
		// departures.add(gbg4);
		// departures.add(kba);
		// departures.add(alvangen);
		// departures.add(alingsas);
		// departures.add(vanersborg);
		//
		//
		// ArrayList<String> stopids = new ArrayList<String>();
		// stopids.add("9021014008000000"); // GBG
		// stopids.add("9021014019110000"); // KBA
		// stopids.add("9021014016611000"); // Älvängen
		// stopids.add("9021014017510000"); // Alingsås
		// stopids.add("9021014080802000"); // Vänersborg
		//
		// DepartureBoard dep = new DepartureBoard(token);
		//
		// for (int k = 0; k < departures.size(); k++) {
		// DepartureBoard departure = departures.get(k);
		// departure.setTime("00:00");
		// departure.setUseLongDistanceTrain(false);
		// departure.setUseRegionalTrain(false);
		// departure.setUseBus(false);
		// departure.setExcludeDR(false);
		// departure.setTimeSpan(1440);
		// String date = "2018-03-19";
		// int day = 19;
		// System.out.println("Station k = " + k);
		// for (int i = 19; i <= 25; i++) {
		// System.out.println("day i = " + i);
		// date = date.substring(0, date.length() - 2) + day;
		// departure.setDate(date);
		//
		// JSONObject obj = departure.executeRequest().getJSONObject("DepartureBoard");
		// JSONArray arr = null;
		// if (obj != null) {
		// try {
		// arr = obj.getJSONArray("Departure");
		// } catch (Exception e) {
		// obj = obj.getJSONObject("Departure");
		// }
		// }
		//
		// if (arr != null) {
		// for (int j = 0; j < arr.length(); j++) {
		// JSONObject tempObj = arr.getJSONObject(j);
		// String dir = arr.getJSONObject(j).getString("direction");
		// if (!tempObj.has("booking")) {
		// PublicTransportation temp = new PublicTransportation(new JourneyDetail(token,
		// tempObj.getJSONObject("JourneyDetailRef").getString("ref")));
		// addPT(temp, date);
		// }
		// }
		// } else if (obj != null) {
		// if (!obj.has("booking")) {
		// PublicTransportation temp = new PublicTransportation(
		// new JourneyDetail(token,
		// obj.getJSONObject("JourneyDetailRef").getString("ref")));
		// addPT(temp, date);
		// }
		// }
		// day++;
		// }
		// }
		//
		// ArrayList<PublicTransportation> trips = new
		// ArrayList<PublicTransportation>();
		//
		// for (PublicTransportation a : pt) {
		// Boolean exists = false;
		// for (PublicTransportation b : trips) {
		// if (a.getStartStopName().equals(b.getStartStopName()) &&
		// a.getEndStopName().equals(b.getEndStopName())
		// && a.getTotalTime() == b.getTotalTime()) {
		// exists = true;
		// break;
		// }
		// }
		// if (!exists)
		// trips.add(a);
		// }
		//
		// ArrayList<String> allstops = new ArrayList<String>();
		// PrintWriter writer = new PrintWriter("vasttaglinjer.csv");
		// writer.print("Totaltime," + "Distance," + "nrOfStops," + "[Stops]");
		// for (PublicTransportation a : trips) {
		// writer.println();
		// writer.print(a.getTotalTime());
		// writer.print("," + ((int) a.getDistance()));
		// writer.print("," + a.getStops().size() + ",");
		// ArrayList<Stop> stops = a.getStops();
		// for (int i = 0; i < stops.size(); i++) {
		// if (!allstops.contains(stops.get(i).getStopName()))
		// allstops.add(stops.get(i).getStopName());
		//
		// if (i == 0)
		// writer.print("[\"" + stops.get(i).getStopName() + "\",");
		// else if (i == stops.size() - 1) {
		// writer.print("\"" + stops.get(i).getStopName() + "\"]");
		// } else
		// writer.print("\"" + stops.get(i).getStopName() + "\",");
		// }
		// }
		// writer.close();
		//
		// PrintWriter writerstops = new PrintWriter("allstops.csv");
		// for (String a : allstops)
		// writerstops.println(a);
		// writerstops.close();

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

		// ArrayList<String> stops = new ArrayList<String>();
		// stops.add("9021014008000000"); // GBG
		// stops.add("9021014019110000"); // KBA
		// stops.add("9021014016611000"); // Älvängen
		// stops.add("9021014017510000"); // Alingsås
		// stops.add("9021014080802000"); // Vänersborg

		// ArrayList<Timer> timers = new ArrayList<Timer>();
		//
		// for (int i = 0; i < departures.size(); i++) {
		// DepartureBoard dep = departures.get(i);
		// dep.setStartId(stops.get(i));
		// LocalTime time = new LocalTime();
		// dep.setTime(time.getHourOfDay() + ":" + (time.getMinuteOfHour() + 5));
		// dep.setUseLongDistanceTrain(false);
		// dep.setUseRegionalTrain(false);
		// dep.setUseBus(false);
		// dep.setDate("2018-03-22");
		// dep.setTimeSpan(780);
		// JSONObject obj = dep.executeRequest();
		// JSONArray arr = null;
		// try {
		// arr = obj.getJSONObject("DepartureBoard").getJSONArray("Departure");
		// } catch (Exception e) {
		// System.out.println("Inga åkturer");
		// }
		// for (int j = 0; j < arr.length(); j++) {
		// if (i == 0) {
		// String dir = arr.getJSONObject(j).getString("direction");
		// if (dir.equals("Kungsbacka") || dir.equals("Alingsås") ||
		// dir.equals("Älvängen")
		// || dir.equals("Vänersborg")) {
		// JourneyDetail jd = new JourneyDetail(token,
		// arr.getJSONObject(j).getJSONObject("JourneyDetailRef").get("ref").toString());
		// String startDepTime =
		// jd.executeRequest().getJSONObject("JourneyDetail").getJSONArray("Stop")
		// .getJSONObject(0).getString("depTime");
		// if (compareTime(startDepTime, time.getHourOfDay() + ":" +
		// time.getMinuteOfHour())) {
		// PublicTransportation temp = new PublicTransportation(jd);
		// Timer timerTemp = new Timer();
		// timerTemp.schedule(new CheckJourney(temp), getDate(temp), 60 * 1000);
		// timers.add(timerTemp);
		// }
		// }
		// } else {
		// if (arr.getJSONObject(j).getString("direction").equals("Göteborg")) {
		//
		// JourneyDetail jd = new JourneyDetail(token,
		// arr.getJSONObject(j).getJSONObject("JourneyDetailRef").get("ref").toString());
		// String startDepTime =
		// jd.executeRequest().getJSONObject("JourneyDetail").getJSONArray("Stop")
		// .getJSONObject(0).getString("depTime");
		// if (compareTime(startDepTime, time.getHourOfDay() + ":" +
		// time.getMinuteOfHour())) {
		// PublicTransportation temp = new PublicTransportation(jd);
		// Timer timerTemp = new Timer();
		// timerTemp.schedule(new CheckJourney(temp), getDate(temp), 60 * 1000);
		// timers.add(timerTemp);
		// }
		// }
		// }
		// }
		// }

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

	private static long checkDate(PublicTransportation temp) {
		// TODO Auto-generated method stub
		return 0;
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
			distance += geoCalc.calculateGeodeticCurve(reference, userPos, pointA).getEllipsoidalDistance(); // Distance
																												// between
																												// Point
																												// A and
																												// Point
																												// B
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
