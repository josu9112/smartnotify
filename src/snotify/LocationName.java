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
 * Class LocationName is used to retrive a list of possible stops, stations, points of intersets or addresses from given input (if any exists).
 */
public class LocationName {

	private Token token;
	private final String baseRequestAddress = "https://api.vasttrafik.se/bin/rest.exe/v2/location.name";
	private String locationName;
	
	/**
	 * Constructs a LocationName. The locationName is initialized as null. A locationName is required before request.
	 * @param token Needs a access-token from Västtrafik.
	 */
	public LocationName(Token token) {
		this.token = token;
		this.locationName = null;
	}
	
	/**
	 * @param locationName Name of a location.
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
	/**
	 * Creates a JSONArray with possible matches from given locatioName. locationName is required!
	 * @return A JSONArray with possible matches of stops, stations, points of interset or addresses.
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
		if(this.locationName == null)
			return null;
		
		return baseRequestAddress + "?input=" + this.locationName + "&format=json";
	}
	
	
}
