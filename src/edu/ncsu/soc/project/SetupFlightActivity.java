package edu.ncsu.soc.project;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetupFlightActivity extends Activity {
	// widgets
	private DatePicker datePicker1;
	private EditText flightNumber, minPreflightTime, optPreflightTime;

	/** create the SetupSimpleActivity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_flight_activity);
		
		// set context on the flight agent so it can access the preferences
		FlightAgent.setContext(this);

		datePicker1 = (DatePicker) findViewById(R.id.datePicker1);   // TODO - still need to set up datepicker. what date is agent assuming??
		minPreflightTime = (EditText) findViewById(R.id.minPreflightTime);
		optPreflightTime = (EditText) findViewById(R.id.optPreflightTime);
		flightNumber = (EditText) findViewById(R.id.flightNumber);

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
                	// get input values
            		String flightNumberString = flightNumber.getText().toString();
            		int prepTime = Integer.valueOf(optPreflightTime.getText().toString());
            		int minPrepTime = Integer.valueOf(minPreflightTime.getText().toString());
            		// get flight time from agent
                	//Date flightTime = DateUtils.addHours(new Date(), 4);  // TODO debug value
                	Date flightTime = agent.getDepartureTime(flightNumberString, datePicker1.getYear(), datePicker1.getMonth(), datePicker1.getDayOfMonth());
					//Log.d(getClass().getSimpleName(), "--- sending date=" + flightTime.toString());   

                	// if this is a valid flight so return with info
                	if (flightTime != null) {
                		msg = flightTime.toString();
                		
                		Intent i = getIntent();
                		i.putExtra("flightTimeYear", flightTime.getYear());  // break up date to pass it back
                		i.putExtra("flightTimeMonth", flightTime.getMonth());  
                		i.putExtra("flightTimeDay", flightTime.getDate());  
                		i.putExtra("flightTimeHour", flightTime.getHours());  
                		i.putExtra("flightTimeMinute", flightTime.getMinutes());  
                		i.putExtra("flightNumber", flightNumberString);  
                		i.putExtra("prepTime", prepTime);  
                		i.putExtra("minPrepTime", minPrepTime);  
                		setResult(RESULT_OK, i);
                		
                		finish();
                	}
                }
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

	}
}
