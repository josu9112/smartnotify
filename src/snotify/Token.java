package snotify;


import com.google.api.client.auth.oauth2.TokenResponse;

import java.io.IOException;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;


/**
 * @author John Sundemo & Rustam Stanikzai
 *	Class Token is used to create and recieve an access-token from Västtrafik.
 */
public class Token {

	private TokenResponse response;
	private String client_id;
	private String client_secret;
	
	
	/**
	 * Constructs a Token.
	 * @param client_id from vasttrafik encoded with Base64
	 * @param client_secret from vasttrafik encoded with Base64
	 */
	public Token(String client_id, String client_secret) {
		this.client_id = client_id;
		this.client_secret = client_secret;
		
		try {
			response = new AuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
					new GenericUrl("https://api.vasttrafik.se/token"), "POST")
							.set("redirect_uri", "https://api.vasttrafik.se/token").set("client_id", client_id)
							.set("client_secret", client_secret).set("grant_type", "client_credentials")
							.set("Content-type", "application/x-www-form-urlencoded").execute();
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}

	/**
	 * Get acces-token in a String
	 * @return Returns the access-token
	 */
	public String getAccessToken() {
		return response.getAccessToken();
	}
	
	
	/**
	 * Renews access-token
	 */
	public void renewToken() {
		try {
			response = new AuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
					new GenericUrl("https://api.vasttrafik.se/token"), "POST")
							.set("redirect_uri", "https://api.vasttrafik.se/token").set("client_id", client_id)
							.set("client_secret", client_secret).set("grant_type", "client_credentials")
							.set("Content-type", "application/x-www-form-urlencoded").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
