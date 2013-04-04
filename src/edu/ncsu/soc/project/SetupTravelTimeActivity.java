package edu.ncsu.soc.project;

import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
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

public class SetupTravelTimeActivity extends Activity {

	private TimePicker timePicker1;
	
	private EditText startAddress, endAddress;
	private EditText minPreTravelTime, optPreTravelTime;
	
	int mHour = 0;
	int mMinute = 0;

	/** create the SetupTravelTimeActivity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_travel_time_activity);

		// set context on the travel time agent so it can access the preferences
		TravelTimeAgent.setContext(this);
		
		timePicker1 = (TimePicker) findViewById(R.id.timePicker1);   
		minPreTravelTime = (EditText) findViewById(R.id.minPreTravelTime);
		optPreTravelTime = (EditText) findViewById(R.id.optPreTravelTime);
		startAddress = (EditText) findViewById(R.id.startAddress);
		endAddress = (EditText) findViewById(R.id.endAddress);

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
            	// check all text inputs
            	String minPreTravelTimeString = minPreTravelTime.getText().toString();
            	String optPreTravelTimeString = optPreTravelTime.getText().toString();
            	String startAddressString = startAddress.getText().toString();
            	String endAddressString = endAddress.getText().toString();
            	
            	if (minPreTravelTimeString==null) {
                    Toast.makeText(getBaseContext(), "Enter a minimum pre-travel time." , Toast.LENGTH_LONG).show(); return;
            	}
            	if (optPreTravelTimeString==null) {
                    Toast.makeText(getBaseContext(), "Enter a pre-travel time." , Toast.LENGTH_LONG).show(); return;
            	}
            	if (startAddressString==null) {
                    Toast.makeText(getBaseContext(), "Enter a start address." , Toast.LENGTH_LONG).show(); return;
            	}
            	if (endAddressString==null) {
                    Toast.makeText(getBaseContext(), "Enter an end address." , Toast.LENGTH_LONG).show(); return;
            	}
        		// check for valid time
            	if ((timePicker1.getCurrentHour()==null) || (timePicker1.getCurrentMinute()==null)) {
                    Toast.makeText(getBaseContext(), "Select an alarm time" , Toast.LENGTH_LONG).show();return;	
            	}

            	// get the travel time in seconds
            	TravelTimeAgent agent = TravelTimeAgent.getInstance();
            	Integer travelTime = agent.getTravelTime(startAddressString, endAddressString);
            	
            	if ((travelTime != null) && (travelTime > 0)) {
                    Toast.makeText(getBaseContext(), "Travel time=" + travelTime.toString(), Toast.LENGTH_LONG).show();
                    // now compute the total pre travel times
                    Integer totalPrepTime = Integer.valueOf(optPreTravelTimeString) + travelTime;
                    Integer minTotalPrepTime = Integer.valueOf(minPreTravelTimeString) + travelTime;
                    // set return values
            		Intent i = getIntent();
            		i.putExtra("alarmTimeHour", timePicker1.getCurrentHour());  
            		i.putExtra("alarmTimeMinute", timePicker1.getCurrentMinute());  
            		i.putExtra("startAddress", startAddressString);  
            		i.putExtra("endAddress", endAddressString);  
            		i.putExtra("prepTime", totalPrepTime);  
            		i.putExtra("minPrepTime", minTotalPrepTime);  
            		setResult(RESULT_OK, i);
            		
            		finish();
            	}
            	else {
                    Toast.makeText(getBaseContext(), "Unable to get travel time", Toast.LENGTH_LONG).show();
            	}
            }
        });

	}
	
	/** create timepicker dialog
	 * 
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		return new TimePickerDialog(this, mTimeListener, mHour, mMinute, true);
	}
	
	private TimePickerDialog.OnTimeSetListener mTimeListener =
		    new TimePickerDialog.OnTimeSetListener() {
		        public void onTimeSet(TimePicker view, int hour, int minute) {
		            mHour = hour;
		            mMinute = minute;
		        	String timeString =  String.valueOf(hour) + ":" + String.valueOf(minute);
					Log.d(getClass().getSimpleName(), "--- time=" + timeString);   

		        }
		    };
}
