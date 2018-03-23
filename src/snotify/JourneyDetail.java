package snotify;

import java.io.IOException;

import org.json.JSONObject;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class JourneyDetail {

	private Token token;
	private String refLink;
	
	public JourneyDetail(Token token) {
		this.token = token;
	}
	
	public JourneyDetail(Token token, String refLink) {
		this.token = token;
		this.refLink = refLink;
	}
	
	public void setJourneyDetailRefLink(String refLink) {
		this.refLink = refLink;
	}
	
	public String getJourneyDetailRefLink() {
		return this.refLink;
	}
	
	public Token getToken() {
		return token;
	}
	
	public JSONObject executeRequest() throws IOException {
		if(this.refLink.equals(""))
			return null;
		
		HttpTransport trans = new NetHttpTransport();
		
		HttpRequestFactory requestFact = trans.createRequestFactory(new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) {
				request.getHeaders().setAuthorization("Bearer " + token.getAccessToken());
			}
		});
		
		HttpRequest req = requestFact.buildGetRequest(new GenericUrl(this.refLink));
		req.setHeaders(new HttpHeaders().setAuthorization("Bearer " + this.token.getAccessToken()));
		req.setConnectTimeout(30000);
		req.setReadTimeout(30000);
		HttpResponse resp;
		try{
			resp = req.execute();
		}catch(Exception e) {
			this.token.renewToken();
			req.setHeaders(new HttpHeaders().setAuthorization("Bearer " + this.token.getAccessToken()));
			resp = req.execute();
		}
		
		
		return new JSONObject(resp.parseAsString());
	}
	
	
}
