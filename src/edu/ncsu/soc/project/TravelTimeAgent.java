package edu.ncsu.soc.project;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/** query bing maps directions api and extract drive time with traffic
 *  for a source/destination address pair
 * @author snellenbach
 *
 */
public class TravelTimeAgent {

	private static final String fxml_host = "dev.virtualearth.net";  // dev.virtualearth.net 65.55.119.205
	private static final String fxml_url = "/REST/V1/";
	private static final String ms_key = "AlpIQEbzA3hOraCgTM-IAFY-mF6om9-UFss2849dWQJtrgICy4TMlmFqZlts9y5S";
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
        // TODO - check for valid start/end Location strings here
		startLocation = startLocation.trim();
		startLocation = startLocation.replaceAll("\\s+", "%20");   // translate spaces
		endLocation = endLocation.trim();
		endLocation = endLocation.replaceAll("\\s+", "%20");   // translate spaces

        HttpHost targetHost = new HttpHost(fxml_host, 80, "http");

		HttpEntity entity = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		HttpGet httpget = new HttpGet(fxml_url + "Routes?wp.0=" + startLocation + "&wp.1=" + endLocation + "&optmz=timeWithTraffic&key=" + ms_key);
        
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
			return null; 
		} finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
		}
		
		Log.d(getClass().getSimpleName(), sb.toString());
		
		/*
		 Sample JSON output:  
		 */
		
		String travelDurationString = null;
		Integer travelDuration = null;
		
		try {
			JSONObject jObject = new JSONObject(sb.toString());
			JSONObject responseResult = jObject.getJSONObject("Response");  
			JSONObject resourceSetsResult = responseResult.getJSONObject("ResourceSets");
			JSONArray resourceSetResult = resourceSetsResult.getJSONArray("ResourceSet");
			JSONObject resourceSetResult0 = resourceSetResult.getJSONObject(0);
			JSONObject routeResult = resourceSetResult0.getJSONObject("Route");  
			travelDurationString = routeResult.getString("TravelDuration");  
			Log.d(getClass().getSimpleName(), "Travel duration: " + travelDurationString);
			if (travelDurationString != null) {
				travelDuration = Integer.valueOf(travelDurationString);
                if (travelDuration > 0) travelDuration = (Integer) travelDuration/60;  // convert to mins
                else travelDuration = null;
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
			return null; 
		}

		return travelDuration;  
	}
}
