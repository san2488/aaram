package edu.ncsu.soc.project;

import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView mTextView1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Initialize member textView1 so we can manipulate it later     
		mTextView1 = (TextView) findViewById(R.id.textView1); 
		
		// TODO a bunch of test code...
		mTextView1.setText("bla bla...");
		
		AlarmModel am = new AlarmModel();
		am.updateAlarms();   // update context status of all alarms and get alarm state
		
		am.addAlarmEvent(new StaticAlarmEvent("Alarm 1", new Date(), 0, 0));   // add a static alarm w/ no prep time
		am.addAlarmEvent(new StaticAlarmEvent("Alarm 2", DateUtils.addSeconds(new Date(), 1), 0, 0));   // add a static alarm at + 1 seconds
		AlarmEvent aEvent3 = new StaticAlarmEvent("Alarm 3", DateUtils.addSeconds(new Date(), 5*60 + 1), 5, 0);
		am.addAlarmEvent(aEvent3);   // add a static alarm at + 1 seconds w/ 5 mins prep time
		am.updateAlarms();   // update context status of all alarms and get alarm state
		
		try {
			Thread.sleep(2000);  // sleep 2 seconds 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		am.updateAlarms();   // update context status of all alarms and get alarm state
		
		aEvent3.hitSnooze(1);   // hit snooze on alarm 3
		am.updateAlarms();   // update context status of all alarms and get alarm state
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
