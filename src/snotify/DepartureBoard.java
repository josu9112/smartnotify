package snotify;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;


/**
 * @author John Sundemo & Rustam Stanikzai
 * Class DepartureBoard is used to get the information from Västtrafik regarding Departures from different locations.
 */
public class DepartureBoard {

	private Token token;
	private final String baseRequestAddress = "https://api.vasttrafik.se/bin/rest.exe/v2/departureBoard";
	private String startId;
	private String stopId;
	private String date;
	private String time;
	private int useVasttag;
	private int useLongDistanceTrain;
	private int useRegionalTrain;
	private int useBus;
	private int useBoat;
	private int useTram;
	private int excludeDR;
	private int timeSpan;
	private int maxDeparturesPerLine;
	private int needJourneyDetail;
	
	
	/**
	 * Constructs a Departureboard and inits the parameters do default settings (All types of transportation used. Busses, trains, trams etc.)
	 * Requiered parameters before sending request are stopId, date and Time. If parameter timeSpan is not set then it'll show the next 20 departures as default. 
	 * @param Access-token to Västtrafik.
	 */
	public DepartureBoard(Token token) {
		this.token = token;
		resetAllParameters();
	}
	
	
	/**
	 * This function can be used to set all parameters at once.
	 * @param startId Where to departure from.
	 * @param date Departure date.
	 * @param time Departure time.
	 * @param useVasttag Use of Västtåg. true == yes, false == not.
	 * @param useLongDistanceTrain Use of long distance trains. true == yes, false == not.
	 * @param useRegionalTrain Use of regional trains. true == yes, false == not.
	 * @param useBus Use of busses. true == yes, false == not.
	 * @param useBoat Use of boats. true == yes, false == not.
	 * @param useTram Use of trams. true == yes, false == not.
	 * @param excludeDR Exclude journeys which require tel. registration. To exclude, set to false.
	 * @param timeSpan Departures in a timespan given in minutes.
	 * @param maxDeparturesPerLine Max Departures per Line in result.
	 * @param needJourneyDetail Journeydetails provides a link with a detailed plan over the journey. It shows data like stops, arrival-/departuretimes at every stop and real-time data. true == yes, false == not.
	 * @param stopId Where the journey/trip will end/arrive.
	 */
	public void setAllParameters(String startId, String date, String time, boolean useVasttag, boolean useLongDistanceTrain, boolean useRegionalTrain,
			boolean useBus, boolean useBoat, boolean useTram, boolean excludeDR, int timeSpan, int maxDeparturesPerLine, boolean needJourneyDetail,
			String stopId) {
		this.startId = startId;
		this.date = date;
		this.time = time;
		this.stopId = stopId;
		this.stopId = stopId;
		this.timeSpan = timeSpan;
		this.maxDeparturesPerLine = maxDeparturesPerLine;
		if(useVasttag) {this.useVasttag = 1;} else {this.useVasttag = 0;}
		if(useLongDistanceTrain) {this.useLongDistanceTrain = 1;} else {this.useLongDistanceTrain = 0;}
		if(useRegionalTrain) {this.useRegionalTrain = 1;} else {this.useRegionalTrain = 0;}
		if(useBus) {this.useBus = 1;} else {this.useBus = 0;}
		if(useBoat) {this.useBoat = 1;} else {this.useBoat = 0;}
		if(useTram) {this.useTram = 1;} else {this.useTram = 0;}
		if(excludeDR) {this.excludeDR = 1;} else {this.excludeDR = 0;}
		if(needJourneyDetail) {this.needJourneyDetail = 1;} else {this.needJourneyDetail = 0;}
	}
	
	
	/**
	 *Resets all parameters to default values (All types of transportation used. Busses, trains, trams etc.) 
	 */
	public void resetAllParameters() {
		this.startId = null;
		this.date = null;
		this.time = null;
		this.stopId = null;
		this.timeSpan = 0;
		this.maxDeparturesPerLine = 0;
		this.useVasttag = 1;
		this.useLongDistanceTrain = 1;
		this.useRegionalTrain = 1;
		this.useBus = 1;
		this.useTram = 1;
		this.excludeDR = 1;
		this.needJourneyDetail = 1;
	}
	 
	
	/**
	 * Sets the startId where to departure from.
	 * @param startId Where to departure from.
	 */
	public void setStartId(String startId){
		this.startId = startId;
	}
	
	/**
	 * Sets the stopId where the journey/trip will end/arrive.
	 * @param stopId Where the journey/trip will end/arrive.
	 */
	public void setStopId(String stopId){
		this.stopId = stopId;
	}
	
	/**
	 * Sets the departure date given in format YYYY-MM-DD.
	 * @param date Departure date given in format YYYY-MM-DD.
	 */
	public void setDate(String date){
		this.date = date;
	}
	
	/**
	 * Sets the departure time give in format HH:MM.
	 * @param time Departure time given in format HH:MM.
	 */
	public void setTime(String time){
		this.time = time;
	}
	
	/**
	 * Sets the timespan for departures given in minutes.
	 * @param timeSpan Departures in a timespan given in minutes.
	 */
	public void setTimeSpan(int timeSpan) {
		this.timeSpan = timeSpan;
	}
	
	/**
	 * Sets the maximum departures per line in the result.
	 * @param maxDeparturesPerLine Max Departures per Line in the result.
	 */
	public void setMaxDeparturesPerLine(int maxDeparturesPerLine) {
		this.maxDeparturesPerLine = maxDeparturesPerLine;
	}
	
	/**
	 * Sets the use of Västtåg.
	 * @param useVasttag Use of Västtåg. true == yes, false == not.
	 */
	public void setUseVasttag(boolean useVasttag) {
		this.useVasttag = (useVasttag) ? 1 : 0;
	}
	
	
	/**
	 * Sets the use of long distance trains.
	 * @param useLongDistanceTrain Use of long distance trains. true == yes, false == not.
	 */
	public void setUseLongDistanceTrain(boolean useLongDistanceTrain) {
		this.useLongDistanceTrain = (useLongDistanceTrain) ? 1 : 0;
	}
	
	/**
	 * Sets the use of regional trains.
	 * @param useRegionalTrain Use of regional trains. true == yes, false == not.
	 */
	public void setUseRegionalTrain(boolean useRegionalTrain) {
		this.useRegionalTrain = (useRegionalTrain) ? 1 : 0;
	}
	
	/**
	 * Sets the use of busses.
	 * @param useBus Use of busses. true == yes, false == not.
	 */
	public void setUseBus(boolean useBus) {
		this.useBus = (useBus) ? 1 : 0;
	}
	
	/**
	 * Sets the use of boats.
	 * @param useBoat Use of boats. true == yes, false == not.
	 */
	public void setUseBoat(boolean useBoat) {
		this.useBoat = (useBoat) ? 1 : 0;
	}
	
	
	/**
	 * Sets the use of trams.
	 * @param useTram Use of trams. true == yes, false == not.
	 */
	public void setUseTram(boolean useTram) {
		this.useTram = (useTram) ? 1 : 0;
	}
	
	
	/**
	 * Sets use of journeys that requires tel. registration.
	 * @param excludeDR Exclude journeys which require tel. registration. To exclude, set to false.
	 */
	public void setExcludeDR(boolean excludeDR) {
		this.excludeDR = (excludeDR) ? 1 : 0;
	}
	
	/**
	 * Sets the need of Journeydetals. Journeydetails provides a link with a detailed plan over the journey. It shows data like stops, arrival-/departuretimes at every stop and real-time data
	 * @param true == yes, false == not.
	 */
	public void setNeedJourneyDetail(boolean needJourneyDetail) {
		this.needJourneyDetail = (needJourneyDetail) ? 1 : 0;
	}
	
	/**
	 * Creates an JSONArray with departures from given parameters. Required parameters are stopId, date and time!
	 * @return A JSONArray containing Departures from given parameters. Returns null if any of stopId, date or time parameters are null. These are required!
	 * @throws IOException
	 */
	public JSONObject executeRequest() throws IOException {
		String requestLink = buildRequestLink();
		if(requestLink == null)
			return null;
		
		HttpTransport trans = new NetHttpTransport();
		
		HttpRequestFactory requestFact = trans.createRequestFactory(new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) {
				request.getHeaders().setAuthorization("Bearer " + token.getAccessToken());
			}
		});
		
		HttpRequest req = requestFact.buildGetRequest(new GenericUrl(requestLink));
		req.setHeaders(new HttpHeaders().setAuthorization("Bearer " + this.token.getAccessToken()));
		req.setConnectTimeout(30000);
		req.setReadTimeout(300000);
		HttpResponse resp = req.execute();
		

		return new JSONObject(resp.parseAsString());
	}
	
	
	
	/**
	 * This private function builds a url-string of set parameters to send as request.
	 * @return A String with the link of set parameters.
	 */
	private String buildRequestLink() {
		if(this.startId == null || this.date == null || this.time == null)
			return null;
		
		String requestLink = baseRequestAddress + "?id=" + this.startId + "&date=" + this.date + "&time=" + this.time;
		
		if(this.stopId != null)
			requestLink = requestLink + "&direction=" + this.stopId;
		if(this.useVasttag == 0)
			requestLink = requestLink + "&useVas=0";
		if(this.useLongDistanceTrain == 0)
			requestLink = requestLink + "&useLDTrain=0";
		if(this.useRegionalTrain == 0)
			requestLink = requestLink + "&useRegTrain=0";
		if(this.useBus == 0)
			requestLink = requestLink + "&useBus=0";
		if(this.useBoat == 0)
			requestLink = requestLink + "&useBoat=0";
		if(this.useTram == 0)
			requestLink = requestLink + "&useTram=0";
		if(this.excludeDR == 0)
			requestLink = requestLink + "&excludeDR=0";
		if(this.needJourneyDetail == 0)
			requestLink = requestLink + "&needJourneyDetail=0";
		if(this.timeSpan != 0)
			requestLink = requestLink + "&timeSpan=" + this.timeSpan;
		if(this.maxDeparturesPerLine != 0)
			requestLink = requestLink + "&maxDeparturesPerLine=" + this.maxDeparturesPerLine;
		
		return requestLink + "&format=json";
	}
}
