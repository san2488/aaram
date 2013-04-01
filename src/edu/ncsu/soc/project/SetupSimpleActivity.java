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
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetupSimpleActivity extends Activity {
	
	private TimePicker timePicker1;
	private EditText snoozeEditText;

	int mHour = 0;
	int mMinute = 0;
	
	/** create the SetupSimpleActivity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_simple_activity);

		// set context on the flight agent so it can access the preferences
		FlightAgent.setContext(this);

		timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
    	snoozeEditText = (EditText) findViewById(R.id.snoozeLimit);


        Button btnPreferences = (Button) findViewById(R.id.btnPreferences);
        btnPreferences.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent("edu.ncsu.soc.project.AppPreferenceActivity");
               startActivity(i);
            }
        });

        Button btnCreateAlarm = (Button) findViewById(R.id.btnCreateAlarm);
        btnCreateAlarm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String snoozeString = snoozeEditText.getText().toString();
				//Log.d(getClass().getSimpleName(), "--- snooze=<" + snoozeString + ">");   
				//Log.d(getClass().getSimpleName(), "--- hour=" + timePicker1.getCurrentHour().toString());   
				//Log.d(getClass().getSimpleName(), "--- min=" + timePicker1.getCurrentMinute().toString());   

            	if (snoozeString==null) {
                    Toast.makeText(getBaseContext(), "Enter a snooze time limit" , Toast.LENGTH_LONG).show();
            	}
            	else {
            		Integer snoozeLimit = Integer.valueOf(snoozeString);
            		// check for valid time
                	if ((timePicker1.getCurrentHour()==null) || (timePicker1.getCurrentMinute()==null)) {
                        Toast.makeText(getBaseContext(), "Select an alarm time" , Toast.LENGTH_LONG).show();	
                	}
                	else {
                		Intent i = getIntent();
                		i.putExtra("alarmTimeHour", timePicker1.getCurrentHour());  
                		i.putExtra("alarmTimeMinute", timePicker1.getCurrentMinute());  
                		i.putExtra("snoozeLimit", snoozeLimit);  
                		setResult(RESULT_OK, i);
                		finish();
                	}
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
