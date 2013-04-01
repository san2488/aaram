package edu.ncsu.soc.project;

import java.util.Date;

import edu.ncsu.soc.project.UserActivityService.UserActivityBinder;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AdaptiveAlarmActivity extends Activity {
	private static Handler uiCallback;
	static final int UI_TIMER_MSG = 1;
	static final int EVENT_TIMER_MSG = 2;

	private AlarmModel am;
	
	// child activity ids
	static final int SETUP_SIMPLE_ACTIVITY = 0;
	static final int SETUP_FLIGHT_ACTIVITY = 1;
	static final int SETUP_TRAVEL_ACTIVITY = 2;
	
	// period of timer that checks for event context changes (in minutes)
	static final int eventTimerPeriod = 1;
	
	// timer threads
	Thread uiUpdateThread, eventUpdateThread;
	
	// widgets
	private TextView textView1;

	private TextView textViewSetTime1, textViewSetTime2, textViewSetTime3, textViewSetTime4;
	private TextView textViewInit1, textViewInit2, textViewInit3, textViewInit4;
	private TextView textViewStatus1, textViewStatus2, textViewStatus3, textViewStatus4;

	private Button snoozeButton;
	private Button offButton;
	
	
	private Spinner spinner1, spinner2, spinner3, spinner4;
	static final int SPINNER_ID_INACTIVE = 0;
	static final int SPINNER_ID_SIMPLE = 1;
	static final int SPINNER_ID_FLIGHT = 2;
	static final int SPINNER_ID_TRAVEL = 3;

	private Boolean blink = false;  // active alarm blink indicator
	int setupEventIndex;   
	

	
	/** create the AdaptiveAlarmActivity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adaptive_alarm_activity);

		// set context on the flight agent so it can access the preferences
		FlightAgent.setContext(this);
		UserAgent.setContext(this);

        Button btnPreferences = (Button) findViewById(R.id.btnPreferences);
        btnPreferences.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent("edu.ncsu.soc.project.AppPreferenceActivity");
               startActivity(i);
            }
        });

		// Initialize widgets     
		textView1 = (TextView) findViewById(R.id.textView1); 

		textViewSetTime1 = (TextView) findViewById(R.id.textView_setTime1); 
		textViewSetTime2 = (TextView) findViewById(R.id.textView_setTime2); 
		textViewSetTime3 = (TextView) findViewById(R.id.textView_setTime3); 
		textViewSetTime4 = (TextView) findViewById(R.id.textView_setTime4);
		
		textViewInit1 = (TextView) findViewById(R.id.textView_init1); 
		textViewInit2 = (TextView) findViewById(R.id.textView_init2); 
		textViewInit3 = (TextView) findViewById(R.id.textView_init3); 
		textViewInit4 = (TextView) findViewById(R.id.textView_init4); 
		
		textViewStatus1 = (TextView) findViewById(R.id.textView_status1); 
		textViewStatus2 = (TextView) findViewById(R.id.textView_status2); 
		textViewStatus3 = (TextView) findViewById(R.id.textView_status3); 
		textViewStatus4 = (TextView) findViewById(R.id.textView_status4); 

		// thread to update context info
		am = new AlarmModel();
		
		// handler to process periodic callbacks
		uiCallback = new Handler () {
		    public void handleMessage (Message msg) {
		        // update the displayed time
				if (msg.what == UI_TIMER_MSG) {
					textView1.setText("Current time is " + DateUtils.toSimpleTime(new Date()));  // update displayed current time
					
					textViewSetTime1.setText(am.getCurrentAlarmTimeString(1));  // update current time for alarm 1
					textViewSetTime2.setText(am.getCurrentAlarmTimeString(2));  // update current time for alarm 2
					textViewSetTime3.setText(am.getCurrentAlarmTimeString(3));  // update current time for alarm 3
					textViewSetTime4.setText(am.getCurrentAlarmTimeString(4));  // update current time for alarm 4

					// toggle bg color of active alarms
					blink = !blink;
					if (am.alarmActive(1) && blink) textViewSetTime1.setBackgroundColor(0xFFFF0000);  // update background time for alarm 1 if active
					else textViewSetTime1.setBackgroundColor(0x00FFFFFF);
					if (am.alarmActive(2) && blink) textViewSetTime2.setBackgroundColor(0xFFFF0000);  // update background time for alarm 2 if active
					else textViewSetTime2.setBackgroundColor(0x00FFFFFF);
					if (am.alarmActive(3) && blink) textViewSetTime3.setBackgroundColor(0xFFFF0000);  // update background time for alarm 3 if active
					else textViewSetTime3.setBackgroundColor(0x00FFFFFF);
					if (am.alarmActive(4) && blink) textViewSetTime4.setBackgroundColor(0xFFFF0000);  // update background time for alarm 4 if active
					else textViewSetTime4.setBackgroundColor(0x00FFFFFF);
					
					textViewStatus1.setText(am.getCurrentChangeReason(1));  // update change reason for alarm 1
					textViewStatus2.setText(am.getCurrentChangeReason(2));  // update change reason for alarm 2
					textViewStatus3.setText(am.getCurrentChangeReason(3));  // update change reason for alarm 3
					textViewStatus4.setText(am.getCurrentChangeReason(4));  // update change reason for alarm 4

					textViewInit1.setText("Was " + am.getInitialAlarmTimeString(1));  // update initial time for alarm 1
					textViewInit2.setText("Was " + am.getInitialAlarmTimeString(2));  // update initial time for alarm 2
					textViewInit3.setText("Was " + am.getInitialAlarmTimeString(3));  // update initial time for alarm 3
					textViewInit4.setText("Was " + am.getInitialAlarmTimeString(4));  // update initial time for alarm 4
				}
				else if (msg.what == EVENT_TIMER_MSG) {
					am.updateAlarms();   // update context status of all alarms and get alarm state
				}
		    }
		};
		
		// create separate threads for periodic updates
		uiUpdateThread = runTimeUpdateTimer();  // timer for current time update
		eventUpdateThread = runAlarmUpdateTimer(eventTimerPeriod);  // timer for event context updates  TODO - should use AlarmManager vs a handler here
		
		// add listeners to buttons, spinners
		addListenerOnSnoozeButton();
		addListenerOnOffButton();
		addListenerOnSpinnerItemSelection(1, spinner1, R.id.spinner1);
		addListenerOnSpinnerItemSelection(2, spinner2, R.id.spinner2);
		addListenerOnSpinnerItemSelection(3, spinner3, R.id.spinner3);
		addListenerOnSpinnerItemSelection(4, spinner4, R.id.spinner4);
		
	}
	
	/** destroy the AdaptiveAlarmActivity - close down threads by interrupting
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		uiUpdateThread.interrupt();
		eventUpdateThread.interrupt();
		UserAgent.getInstance().stopUserActivityService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/** process return info from setup activities and create appropriate alarm events
	 * 
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Log.d(getClass().getSimpleName(), "--- return callback req=" + requestCode + " rc=" + resultCode);   
		if (resultCode == RESULT_OK) {
			
			Bundle extras = data.getExtras();  // get info returned from setup activities
			if (extras != null) {
				// create a simple alarm event
				if (requestCode == SETUP_SIMPLE_ACTIVITY) {                 
					int alarmTimeHour = extras.getInt("alarmTimeHour");
					int alarmTimeMinute = extras.getInt("alarmTimeMinute");
					int snoozeLimit = extras.getInt("snoozeLimit");
					//Log.d(getClass().getSimpleName(), "--- returning time=" + alarmTimeHour + ":" + alarmTimeMinute + " snooze=" + snoozeLimit);   
					// check for valid time and snooze limit value  
					Date initDate = DateUtils.getNewDateFromTime(alarmTimeHour, alarmTimeMinute);
					// create a simple alarm event based in inputs  (prep time is set to max snooze time)
					AlarmEvent aEvent = new StaticAlarmEvent("Simple Alarm", DateUtils.addMinutes(initDate, snoozeLimit), snoozeLimit, 0); 
	                Toast.makeText(getBaseContext(), "New Simple Alarm Created", Toast.LENGTH_LONG).show();
					am.addAlarmEvent(setupEventIndex, aEvent);
				}
				// create a flight alarm event
				else if (requestCode == SETUP_FLIGHT_ACTIVITY) {                 
					// TODO extract return info and create a flight event
					int flightTimeYear = extras.getInt("flightTimeYear");  
					int flightTimeMonth = extras.getInt("flightTimeMonth");  
					int flightTimeDay = extras.getInt("flightTimeDay");  
					int alarmTimeHour = extras.getInt("flightTimeHour");  
					int flightTimeHour = extras.getInt("flightTimeMinute");  
					String flightNumber = extras.getString("flightNumber");  
					int prepTime = extras.getInt("prepTime");  
					int minPrepTime = extras.getInt("minPrepTime");  
					// recreate the flight time
					Date flightTime = new Date();
					flightTime.setYear(flightTimeYear); 
					flightTime.setMonth(flightTimeMonth);  
					flightTime.setDate(flightTimeDay);  
					flightTime.setHours(alarmTimeHour);  
					flightTime.setMinutes(flightTimeHour); 
					//Log.d(getClass().getSimpleName(), "--- returning time=" + flightTime.toString());   
					// create the alarm
					AlarmEvent aEvent = new FlightAlarmEvent("Flight Alarm", flightTime, prepTime, minPrepTime, flightNumber);
	                Toast.makeText(getBaseContext(), "New Flight Alarm Created", Toast.LENGTH_LONG).show();
					am.addAlarmEvent(setupEventIndex, aEvent);   
				}
				// create a travel alarm event
				if (requestCode == SETUP_TRAVEL_ACTIVITY) {                 
					// TODO extract return info and create a travel event
					//AlarmEvent aEvent = new TravelAlarmEvent("Alarm y", DateUtils.addHours(new Date(), 10), 30, 30, "From address", "To address");
					//am.addAlarmEvent(alarmEventIndex, aEvent);   // add a drive alarm event in 2 hrs w/ 30 mins prep time and no snooze allowed
				}
			}
		
		}
	}
	
	/** create a thread that periodically wakes for update of current time display 
	 * 
	 */
	public Thread runTimeUpdateTimer() {
		// create a thread to handle time display updates
		Thread timer = new Thread() {
		    public void run () {
		    	boolean sleepInterrupt = false;
		    	while (!Thread.currentThread().isInterrupted() && !sleepInterrupt) {
		            // do stuff in a separate thread
		            uiCallback.sendEmptyMessage(UI_TIMER_MSG);
		            try {
						Thread.sleep(1000);  // 1 second update
					} catch (InterruptedException e) {
						sleepInterrupt=true;
						   //throw new RuntimeException("UI Update Timer Interrupted",e);  // throw an unchecked exc to exit run
					}    
		        }
				Log.d(getClass().getSimpleName(), "--- exiting ui update thread");   

		    }
		};
		timer.start();
		return timer;
	}
	
		
	/** create a thread that periodically wakes for update of current time display 
	 * @param int period - timer period in minutes
	 */
	private Thread runAlarmUpdateTimer(final int period) {
		// create a thread to handle time display updates
		Thread timer = new Thread() {
		    public void run () {
		    	boolean sleepInterrupt = false;
		        while (!Thread.currentThread().isInterrupted() && !sleepInterrupt) {
		            // do stuff in a separate thread
		            uiCallback.sendEmptyMessage(EVENT_TIMER_MSG);
		            try {
						Thread.sleep(period*60*1000);  
					} catch (InterruptedException e) {
						sleepInterrupt=true;
						   //throw new RuntimeException("Event Update Timer Interrupted",e);  // throw an unchecked exc to exit run
					}    
		        }
				Log.d(getClass().getSimpleName(), "--- exiting event update thread");   

		    }
		};
		timer.start();
		return timer;
	}
	
	public void addListenerOnSnoozeButton() {
		snoozeButton = (Button) findViewById(R.id.snooze_button);
		snoozeButton.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				 Log.d(getClass().getSimpleName(), "--- snooze button clicked");   
				 am.hitSnooze();
	             Toast.makeText(getBaseContext(), "All active alarms snoozed", 
	                        Toast.LENGTH_SHORT).show();
			}
 
		});
 
	}
	
	public void addListenerOnOffButton() {
		offButton = (Button) findViewById(R.id.off_button);
		offButton.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				 Log.d(getClass().getSimpleName(), "--- off button clicked");   
				 am.hitOff();
	             Toast.makeText(getBaseContext(), "All active alarms deactivated", 
	                        Toast.LENGTH_SHORT).show();
			}
 
		});
 
	}
	
	  public void addListenerOnSpinnerItemSelection(final int alarmEventIndex, Spinner spinner, int id) {
			spinner = (Spinner) findViewById(id);
			
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	                this, R.array.alarm_type_array, android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(adapter);
	        
	        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            
	            public void onItemSelected(AdapterView<?> arg0, View arg1,
	                    int arg2, long arg3) {
	                
	                int alarmEventType = arg0.getSelectedItemPosition();
	                
	               //String[] sel = getResources().getStringArray(R.array.alarm_type_array);	                    
	                //Toast.makeText(getBaseContext(), "You have selected : " +sel[alarmEventType], 
	                //        Toast.LENGTH_SHORT).show();
	                
	                if (alarmEventType==SPINNER_ID_INACTIVE) {  // Inactive 
	                	if (am.alarmExists(alarmEventIndex)) {
		                	am.deactivateAlarm(alarmEventIndex);
		                    Toast.makeText(getBaseContext(), "Alarm " + alarmEventIndex + " deactivated", 
		                            Toast.LENGTH_SHORT).show();
	                	}
	                }
	                // only create a new alarm event if one doesnt exist
	                else if (!am.alarmExists(alarmEventIndex)) {
	                	setupEventIndex = alarmEventIndex;
	                    if (alarmEventType==SPINNER_ID_SIMPLE) {  // Simple alarm 
	                        	Intent myIntent = new Intent(AdaptiveAlarmActivity.this, SetupSimpleActivity.class);  // go to simple alarm screen
	                            AdaptiveAlarmActivity.this.startActivityForResult(myIntent, SETUP_SIMPLE_ACTIVITY);	                	
	                            }
	                    else if (alarmEventType==SPINNER_ID_FLIGHT) {  // Flight alarm 
	                        Intent myIntent = new Intent(AdaptiveAlarmActivity.this, SetupFlightActivity.class);  // go to flight alarm screen
	                        AdaptiveAlarmActivity.this.startActivityForResult(myIntent, SETUP_FLIGHT_ACTIVITY);	                	
	                        }
	                    else if (alarmEventType==SPINNER_ID_TRAVEL) {  // Travel alarm 
	                        Intent myIntent = new Intent(AdaptiveAlarmActivity.this, SetupTravelActivity.class);  // go to travel alarm screen
	                        AdaptiveAlarmActivity.this.startActivityForResult(myIntent, SETUP_TRAVEL_ACTIVITY);	                	
	                        }
	                }
	                
	            }

	            public void onNothingSelected(AdapterView<?> arg0) {
	                
	            }
	                      
	        });     
		  }

}
