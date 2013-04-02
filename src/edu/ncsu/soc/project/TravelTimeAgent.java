package edu.ncsu.soc.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TravelTimeAgent {
	private static final String fxml_host = "maps.googleapis.com";
	private static final String fxml_url = "/maps/api/directions/";
	private static Context context;
	
	public static TravelTimeAgent instance = null;
	
	private TravelTimeAgent() {
	}
	
	public static TravelTimeAgent getInstance() {
		if (instance == null)
			instance = new TravelTimeAgent();
		return instance;
	}

	public static void setContext(Context context) {
		TravelTimeAgent.context = context;
	}

	/** return travel time in minutes from start to finish location
	 * 
	 * @param startLocation
	 * @param endLocation
	 * @return
	 */
	public Integer getTravelTime(String startLocation, String endLocation) 
	{
        
        HttpHost targetHost = new HttpHost(fxml_host, 80, "http");

		HttpEntity entity = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		HttpGet httpget = new HttpGet(fxml_url + "json?origin=Raleigh,NC&destination=Durham,NC");
        
        Log.d(getClass().getSimpleName(), "executing request: " + targetHost + httpget.getRequestLine());
        
		StringBuilder sb = null;
		try {
			HttpResponse response = httpclient.execute(targetHost, httpget);
			entity = response.getEntity();
			
			InputStream inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
			    sb.append(line + "\n");
			}
			entity.consumeContent();
		} catch (Exception e) {
			e.printStackTrace();
			return 5; // TODO return null;
		} finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
		}
		
		Log.d(getClass().getSimpleName(), sb.toString());
		
		/*
		 Sample JSON output:  TODO - fix json parse / old code follows
		 

		 */
		int estimatedArrivalTime = 0;
		int actualDepartureTime = 0;
		String filed_ete = null;
		
		try {
			JSONObject jObject = new JSONObject(sb.toString());
			JSONObject flightInfoExResult = jObject.getJSONObject("FlightInfoExResult");  // cleanup
			JSONArray flights = flightInfoExResult.getJSONArray("flights");
			JSONObject flight = flights.getJSONObject(0);
			filed_ete = flight.getString("filed_ete");
			estimatedArrivalTime = flight.getInt("estimatedarrivaltime");
			actualDepartureTime = flight.getInt("actualdeparturetime");
		}
		catch (JSONException e) {
			e.printStackTrace();
			return 10;   // TODO return null;
		}
		
		Date departureTime = null;
		if (actualDepartureTime > 0) {
			// flight already left!
			departureTime = new Date((((long)actualDepartureTime)*1000));			
			Log.d(getClass().getSimpleName(), "Actual departure time: " + departureTime.toString());
		}
		else if (estimatedArrivalTime > 0 && filed_ete != null && filed_ete.indexOf(":") == 2) 
		{
			// estimate the departure time by taking the estimated arrival time minus the estimated time enroute
			int filed_ete_hrs = Integer.parseInt(filed_ete.substring(0,2));
			int filed_ete_mins = Integer.parseInt(filed_ete.substring(3,5));
			
			departureTime = new Date((((long)estimatedArrivalTime)*1000) - (filed_ete_hrs*60*60*1000) - (filed_ete_mins*60*1000));
			Log.d(getClass().getSimpleName(), "Estimated departure time: " + departureTime.toString());
		}

		return 15;  // TODO 
	}
}
