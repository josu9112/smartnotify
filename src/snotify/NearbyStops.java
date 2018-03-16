package snotify;

import java.io.IOException;

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
 * Class NearbyStops is used to retrive information about nearby stops in Västtrafik given coordinates.
 */
public class NearbyStops {

	private Token token;
	private final String baseRequestAddress = "https://api.vasttrafik.se/bin/rest.exe/v2/location.nearbystops";
	private String coordLat;
	private String coordLong;
	private int maxNo;
	private int maxDist;
	
	
	/**
	 * Constructs NearbyStops. All parameters are intialized as null or zero. Use set-functions to give them values.
	 * Required parameters before requests are coordLat and coordLon.
	 * @param token Needs access-token from Västtrafik
	 */
	public NearbyStops(Token token) {
		this.token = token;
		this.coordLat = null;
		this.coordLong = null;
		this.maxNo = 0;
		this.maxDist = 0;
	}
	
	/**
	 * Sets the latitude coordinate in the WGS84 system
	 * @param coordLat Latitude coordinate in the WGS84 system
	 */
	public void setCoordLat(String coordLat) {
		this.coordLat = coordLat;
	}
	
	/**
	 * Sets the longitude coordinate in the WGS84 system
	 * @param coordLong Longitude coordinate in the WGS84 system
	 */
	public void setCoordLong(String coordLong) {
		this.coordLong = coordLong;
	}
	
	/**
	 * Sets max number of matches in result
	 * @param maxNo Max number of matches in result
	 */
	public void setMaxNo(int maxNo) {
		this.maxNo = maxNo;
	}
	
	/**
	 * Sets the max distance from center of coordinates
	 * @param maxDist Max distance from center of coordinates
	 */
	public void setMaxDist(int maxDist) {
		this.maxDist = maxDist;
	}
	
	/**
	 * Creates a JSONArray with nearby stops after sending a request to Västtrafik given set parameters.
	 * Required parameters before requests are coordLat and coordLong.
	 * @return A JSONArray with nearby stops (if any exists).
	 * @throws IOException
	 */
	public JSONArray executeRequest() throws IOException {
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
		req.setReadTimeout(30000);
		HttpResponse resp = req.execute();
		
		String jsonresponse = resp.parseAsString();
		String trimmedstring = "{" + jsonresponse.substring(jsonresponse.indexOf("\"StopLocation\""), jsonresponse.length()-1);
		
		return new JSONObject(trimmedstring).getJSONArray("StopLocation");
	}
	
	/**
	 * This private function builds a url-string of set parameters to send as request.
	 * @return A String with the link of set parameters.
	 */
	private String buildRequestLink() {
		if(this.coordLong == null || this.coordLat == null)
			return null;
		
		String stringRequest = baseRequestAddress + "?originCoordLat=" + this.coordLat + "&originCoordLong=" + this.coordLong;
		
		if(this.maxNo != 0)
			stringRequest = stringRequest + "&maxNo=" + maxNo;
		if(this.maxDist != 0)
			stringRequest = stringRequest + "&maxDist=" + maxDist;
		
		return stringRequest + "&format=json";
	}
	
}
