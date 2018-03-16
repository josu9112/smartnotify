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
 * @author John Sundemo and Rustam Stanikzai
 * Class NearbyAdress is used to recieve a nearby address from given coordinates
 */
public class NearbyAddress {

	private Token token;
	private final String baseRequestAddress = "https://api.vasttrafik.se/bin/rest.exe/v2/location.nearbyaddress";
	private String coordLat;
	private String coordLong;
	
	
	/**
	 * Constructs NearbyAddress. Coordinations are initialized and set to null. Use set methods to give them valuse. 
	 * CoordLat and CoordLong are both required for requests!
	 * @param token Needs an access-token from Västtrafik
	 */
	public NearbyAddress(Token token) {
		this.token = token;
		coordLat = null;
		coordLong = null;
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
	 * Creates a JSONArray with nearby address after sending a request to Västtrafik given set coordinates.
	 * Required parameters before requests are coordLat and coordLong.
	 * @return A JSONArray with nearby addresses (if any exists).
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
		String trimmedstring = "{" + jsonresponse.substring(jsonresponse.indexOf("\"CoordLocation\""), jsonresponse.length()-1);
		
		return new JSONObject(trimmedstring).getJSONArray("CoordLocation");
	}
	
	/**
	 * This private function builds a url-string of set parameters to send as request.
	 * @return A String with the link of set parameters.
	 */
	private String buildRequestLink() {
		if(this.coordLong == null || this.coordLat == null)
			return null;
		
		return baseRequestAddress + "?originCoordLat=" + this.coordLat + "&originCoordLong=" + this.coordLong + "&format=json";
	}
	
}
