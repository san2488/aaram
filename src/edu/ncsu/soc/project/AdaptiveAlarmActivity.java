package edu.ncsu.soc.project;

import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AdaptiveAlarmActivity extends Activity {
	private Handler uiCallback;
	private AlarmModel am;
	
	// child activity ids
	static final int SET_SIMPLE_ACTIVITY = 0;
	static final int SET_FLIGHT_ACTIVITY = 0;
	static final int SET_TRAVEL_ACTIVITY = 0;
	
	// widgets
	private TextView textView1;

	private TextView textViewSetTime1, textViewSetTime2, textViewSetTime3, textViewSetTime4;
	private TextView textViewInit1, textViewInit2, textViewInit3, textViewInit4;
	private TextView textViewStatus1, textViewStatus2, textViewStatus3, textViewStatus4;

	private Button snoozeButton;
	private Button offButton;
	
	private Boolean showActiveAlarmBG1 = false;
	private Boolean showActiveAlarmBG2 = false;
	private Boolean showActiveAlarmBG3 = false;
	private Boolean showActiveAlarmBG4 = false;
	
	private Spinner spinner1, spinner2, spinner3, spinner4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adaptive_alarm_activity);
		
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
				if (msg.what == 1) {
					textView1.setText("Current time is " + DateUtils.toSimpleTime(new Date()));  // update displayed current time
					
					textViewSetTime1.setText(am.getCurrentAlarmTimeString(1));  // update current time for alarm 1
					textViewSetTime2.setText(am.getCurrentAlarmTimeString(2));  // update current time for alarm 2
					textViewSetTime3.setText(am.getCurrentAlarmTimeString(3));  // update current time for alarm 3
					textViewSetTime4.setText(am.getCurrentAlarmTimeString(4));  // update current time for alarm 4

					// toggle bg color of active alarms
					showActiveAlarmBG1 = am.alarmActive(1) && !showActiveAlarmBG1;
					showActiveAlarmBG2 = am.alarmActive(2) && !showActiveAlarmBG2;
					showActiveAlarmBG3 = am.alarmActive(3) && !showActiveAlarmBG3;
					showActiveAlarmBG4 = am.alarmActive(4) && !showActiveAlarmBG4;
					if (showActiveAlarmBG1) textViewSetTime1.setBackgroundColor(0xFFFF0000);  // update background time for alarm 1 if active
					else textViewSetTime1.setBackgroundColor(0xFFFFFFFF);
					if (showActiveAlarmBG2) textViewSetTime2.setBackgroundColor(0xFFFF0000);  // update background time for alarm 1 if active
					else textViewSetTime2.setBackgroundColor(0xFFFFFFFF);
					if (showActiveAlarmBG3) textViewSetTime3.setBackgroundColor(0xFFFF0000);  // update background time for alarm 1 if active
					else textViewSetTime3.setBackgroundColor(0xFFFFFFFF);
					if (showActiveAlarmBG4) textViewSetTime4.setBackgroundColor(0xFFFF0000);  // update background time for alarm 1 if active
					else textViewSetTime4.setBackgroundColor(0xFFFFFFFF);
					
					textViewStatus1.setText(am.getCurrentChangeReason(1));  // update change reason for alarm 1
					textViewStatus2.setText(am.getCurrentChangeReason(2));  // update change reason for alarm 2
					textViewStatus3.setText(am.getCurrentChangeReason(3));  // update change reason for alarm 3
					textViewStatus4.setText(am.getCurrentChangeReason(4));  // update change reason for alarm 4

					textViewInit1.setText("Was " + am.getInitialAlarmTimeString(1));  // update initial time for alarm 1
					textViewInit2.setText("Was " + am.getInitialAlarmTimeString(2));  // update initial time for alarm 2
					textViewInit3.setText("Was " + am.getInitialAlarmTimeString(3));  // update initial time for alarm 3
					textViewInit4.setText("Was " + am.getInitialAlarmTimeString(4));  // update initial time for alarm 4
				}
				else if (msg.what == 2) {
					am.updateAlarms();   // update context status of all alarms and get alarm state
				}
		    }
		};
		
		// create separate threads for periodic updates
		Thread th1 = runTimeUpdateTimer();  // timer for current time update
		Thread th2 = runAlarmUpdateTimer();  // timer for event context updates  TODO - should use AlarmManager vs a handler here
		
		// add listeners to buttons, spinners
		addListenerOnSnoozeButton();
		addListenerOnOffButton();
		addListenerOnSpinnerItemSelection(1, spinner1, R.id.spinner1);
		addListenerOnSpinnerItemSelection(2, spinner2, R.id.spinner2);
		addListenerOnSpinnerItemSelection(3, spinner3, R.id.spinner3);
		addListenerOnSpinnerItemSelection(4, spinner4, R.id.spinner4);
		
		/* TODO a bunch of test code creating alarms...
		am.addAlarmEvent(1, new StaticAlarmEvent("Alarm 1", new Date(), 0, 0));   // add a static alarm w/ no prep time
		am.addAlarmEvent(2, new StaticAlarmEvent("Alarm 2", DateUtils.addMinutes(new Date(), 1), 0, 0));   // add a static alarm at + 1 minutes
		AlarmEvent aEvent3 = new StaticAlarmEvent("Alarm 3", DateUtils.addSeconds(new Date(), 30*60 + 1), 30, 10);
		am.addAlarmEvent(3, aEvent3);   // add a static alarm at + 1 seconds w/ 30 mins prep time allowing snooze
		
		AlarmEvent aEvent4 = new FlightAlarmEvent("Alarm 4", DateUtils.addHours(new Date(), 10), 30, 10, "DL200", 0);
		am.addAlarmEvent(4, aEvent4);   // add a flight alarm event in 10 hrs w/ 30 mins prep time
		*/				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SET_SIMPLE_ACTIVITY) {
		
			if (resultCode == RESULT_OK) {                 
				//startActivity(new Intent(Intent.ACTION_VIEW, data));         // TODO temp code
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
		        for (;;) {
		            // do stuff in a separate thread
		            uiCallback.sendEmptyMessage(1);
		            try {
						Thread.sleep(1000);  // 1 second update
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}    
		        }
		    }
		};
		timer.start();
		return timer;
	}
	
		
	/** create a thread that periodically wakes for update of current time display 
	 * 
	 */
	private Thread runAlarmUpdateTimer() {
		// create a thread to handle time display updates
		Thread timer = new Thread() {
		    public void run () {
		        for (;;) {
		            // do stuff in a separate thread
		            uiCallback.sendEmptyMessage(2);
		            try {
						Thread.sleep(60*1000);  // 1 min update
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}    
		        }
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
				 Log.d(getClass().getSimpleName(), "--- snooze button clicked");   // TODO fix output/display
				 am.hitSnooze();
			}
 
		});
 
	}
	
	public void addListenerOnOffButton() {
		offButton = (Button) findViewById(R.id.off_button);
		offButton.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				 Log.d(getClass().getSimpleName(), "--- off button clicked");   // TODO fix output/display, also add verification of disable?
				 am.hitOff();
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
	                
	                if (alarmEventType==0) {  // Inactive 
	                	am.deactivateAlarm(alarmEventIndex);
	                }
	                else if (alarmEventType==1) {  // Simple alarm 
	                	// TODO - launch simple alarm event entry screen here
	                	//Intent myIntent = new Intent(AdaptiveAlarmActivity.this, NextActivity.class);
	                	//AdaptiveAlarmActivity.this.startActivityForResult(myIntent, SET_SIMPLE_ACTIVITY);	                	
	                	AlarmEvent aEvent = new StaticAlarmEvent("Alarm x", DateUtils.addSeconds(new Date(), 30*60 + 1), 30, 10);
	            		am.addAlarmEvent(alarmEventIndex, aEvent);   // add a static alarm at + 1 seconds w/ 30 mins prep time allowing snooze
	                }
	                else if (alarmEventType==2) {  // Flight alarm 
	                	// TODO - launch flight alarm event entry screen here
	            		AlarmEvent aEvent = new FlightAlarmEvent("Alarm z", DateUtils.addHours(new Date(), 10), 30, 10, "DL200", 0);
	            		am.addAlarmEvent(alarmEventIndex, aEvent);   // add a flight alarm event in 10 hrs w/ 30 mins prep time
	                }
	                else if (alarmEventType==3) {  // Travel alarm 
	                	// TODO - launch travel alarm event entry screen here
	            		AlarmEvent aEvent = new TravelAlarmEvent("Alarm y", DateUtils.addHours(new Date(), 10), 30, 30, "From address", "To address");
	            		am.addAlarmEvent(alarmEventIndex, aEvent);   // add a drive alarm event in 2 hrs w/ 30 mins prep time and no snooze allowed
	                }
	                
	            }

	            public void onNothingSelected(AdapterView<?> arg0) {
	                // TODO - if alarm is active then edit settings
	                
	            }
	                      
	        });     
		  }

}
