package edu.ncsu.soc.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

public class FlightAgent {
	private static final String fxml_host = "flightxml.flightaware.com";
	private static final String fxml_url = "/json/FlightXML2/";
	private static Context context;
	
	public static FlightAgent instance = null;
	
	private FlightAgent() {
	}
	
	public static FlightAgent getInstance() {
		if (instance == null)
			instance = new FlightAgent();
		return instance;
	}

	public static void setContext(Context context) {
		FlightAgent.context = context;
	}

	public Date getDepartureTime(String ident, int year, int month, int dayOfMonth) 
	{
    	SharedPreferences appPrefs = 
        		context.getSharedPreferences("edu.ncsu.soc.project_preferences", Context.MODE_PRIVATE);    	
        String username = appPrefs.getString("flightawareUsername", "");
        String apiKey = appPrefs.getString("flightawareApiKey", "");
        String airport = appPrefs.getString("airport", "RDU");

        if (username == null || username.length() == 0 || apiKey == null || apiKey.length() == 0)
        	return null;
        
        HttpHost targetHost = new HttpHost(fxml_host, 80, "http");

		HttpEntity entity = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();

		httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(null, -1),
                new UsernamePasswordCredentials(username, apiKey));

		HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
		    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
		        AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
		        CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
		                ClientContext.CREDS_PROVIDER);
		        HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		        
		        if (authState.getAuthScheme() == null) {
		            AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
		            Credentials creds = credsProvider.getCredentials(authScope);
		            if (creds != null) {
		                authState.setAuthScheme(new BasicScheme());
		                authState.setCredentials(creds);
		            }
		        }
		    }    
		};
		
		httpclient.addRequestInterceptor(preemptiveAuth, 0);
		HttpGet httpget = new HttpGet(fxml_url + "FlightInfoEx?ident="+ident+"&howMany=25&offset=0");
        
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
		 
		 {"FlightInfoExResult":{"next_offset":1,"flights":[{"faFlightID":"RPA4881-1364279166-airline-0045","ident":"RPA4881","aircrafttype":"DH8D","filed_ete":"00:50:00","filed_time":1364416998,"filed_departuretime":1364523000,"filed_airspeed_kts":234,"filed_airspeed_mach":"","filed_altitude":0,"route":"","actualdeparturetime":0,"estimatedarrivaltime":1364526600,"actualarrivaltime":0,"diverted":"","origin":"KIAD","destination":"KRDU","originName":"Washington Dulles Intl","originCity":"Washington, DC","destinationName":"Raleigh-Durham Intl","destinationCity":"Raleigh/Durham, NC"}]}}

		 */
		int estimatedArrivalTime = 0;
		int actualDepartureTime = 0;
		String filed_ete = null;
		Date departureTime = null;
		
		try {
			JSONObject jObject = new JSONObject(sb.toString());
			JSONObject flightInfoExResult = jObject.getJSONObject("FlightInfoExResult");
			JSONArray flights = flightInfoExResult.getJSONArray("flights");
			for (int i=0; i < flights.length(); i++) {
				JSONObject flight = flights.getJSONObject(i);
				
				String origin = flight.getString("origin");
				if (!origin.equals("K"+airport.toUpperCase()))
					continue;
				
				filed_ete = flight.getString("filed_ete");
				estimatedArrivalTime = flight.getInt("estimatedarrivaltime");
				actualDepartureTime = flight.getInt("actualdeparturetime");
			
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
				
			    Calendar cal = new GregorianCalendar();
			    cal.setTime(departureTime);
			    
				if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month && cal.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
					Log.d(getClass().getSimpleName(), "Departure time: " + departureTime.toString());
					return departureTime;
				}
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}
}
