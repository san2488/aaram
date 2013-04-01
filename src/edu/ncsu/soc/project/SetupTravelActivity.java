package edu.ncsu.soc.project;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetupTravelActivity extends Activity {
	
	/** create the SetupSimpleActivity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_travel_activity);

		// set context on the flight agent so it can access the preferences
		FlightAgent.setContext(this);

        Button btnPreferences = (Button) findViewById(R.id.btnPreferences);
        btnPreferences.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent("edu.ncsu.soc.project.AppPreferenceActivity");
               startActivity(i);
            }
        });

        Button btnDisplayFlight = (Button) findViewById(R.id.btnCreateAlarm);
        btnDisplayFlight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String msg = null;
            	SharedPreferences appPrefs = 
                		getSharedPreferences("edu.ncsu.soc.project_preferences", Context.MODE_PRIVATE);    	
                String username = appPrefs.getString("flightawareUsername", "");
                String apiKey = appPrefs.getString("flightawareApiKey", "");            	
                if (username == null || username.length() == 0 || apiKey == null || apiKey.length() == 0) {
                	msg = "Please set your Preferences";
                }
                else {
                	msg = "No Results";
                	FlightAgent agent = FlightAgent.getInstance();
                	Date flightTime = agent.getDepartureTime(((EditText)findViewById(R.id.flightNumber)).getText().toString());
                	if (flightTime != null) {
                		msg = flightTime.toString();
                	}
                }
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

	}
}
