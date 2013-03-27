package edu.ncsu.soc.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;  // TODO - use a hashmap instead?

import android.util.Log;

/** adaptive alarm model
 */
public class AlarmModel {
	
	// user preferences
	private Integer initialPrepTime = 30;           // optimal preparation time needed before any travel in minutes
	private Integer defaultMinPrepTime = 10;        // minimal preparation time needed in minutes (for snooze disable)
	private Integer defaultSnoozeTime = 5;         // snooze in minutes
	
	// user sleep state
	private Boolean sleepDeprived = false;   // indication that user needs sleep / use userActivityAgent to set this
	private Boolean currentlyActive = false;   // indication that  user is active / use userActivityAgent to set this

	private ArrayList<AlarmEvent> alarmEvents = new ArrayList<AlarmEvent>();     // list of active alarms

	/**
	 * 
	 */
	public AlarmModel() {
		// initialize to 4 inactive alarms
		alarmEvents.add(0, null);
		alarmEvents.add(1, null);
		alarmEvents.add(2, null);
		alarmEvents.add(3, null);
	}

	/** add a new alarm event to the arrraylist 
	 * 
	 */
	public void addAlarmEvent(int alarmNo, AlarmEvent eAlarm) {
		alarmEvents.set(alarmNo - 1, eAlarm);
	}	
	
	/**
	 * return a text string containing reason for a change in specified alarm event
	 */
	public String getCurrentChangeReason(int alarmEventIndex) {
		if (alarmEvents.get(alarmEventIndex-1)==null) return "";  // if an inactive alarm
		else return alarmEvents.get(alarmEventIndex-1).getCurrentChangeReason();
	}
	
	/**
	 * return a string containing the currently set alarm time for this alarm event 
	 */
	public String getCurrentAlarmTimeString(int alarmEventIndex) {
		if (alarmEvents.get(alarmEventIndex-1)==null) return "--:-- --";  // if an inactive alarm
		else return DateUtils.toSimpleTime(alarmEvents.get(alarmEventIndex-1).getCurrentAlarmTime());
	}
	
	/**
	 * return a string containing the initially set alarm time for this alarm event 
	 */
	public String getInitialAlarmTimeString(int alarmEventIndex) {
		if (alarmEvents.get(alarmEventIndex-1)==null) return "--:-- --";  // if an inactive alarm
		else return DateUtils.toSimpleTime(alarmEvents.get(alarmEventIndex-1).getInitialAlarmTime());
	}
	
	/**
	 * return true if specified alarm is active 
	 */
	public Boolean alarmActive(int alarmEventIndex) {
		if ((alarmEvents.get(alarmEventIndex-1)!=null) && (alarmEvents.get(alarmEventIndex-1).alarmActive())) return true;  // if an active alarm
		else return false;
	}
	
	/**
	 * kill an alarm event 
	 */
	public void deactivateAlarm(int alarmEventIndex) {
		alarmEvents.set(alarmEventIndex-1, null);  // kill this alarm
	}
	
	/** update alarm times for each active event alarm based on context
	 * 
	 */
	public void updateAlarms() {
		Iterator<AlarmEvent> iter = alarmEvents.iterator();
		Log.d(getClass().getSimpleName(), "----- alarm update");   // TODO 
		while (iter.hasNext()) {
			AlarmEvent alarm = iter.next();
			if (alarm != null) {
			   alarm.updateCurrentAlarmTime();   // call appropriate context update for this AlarmEvent
			   Log.d(getClass().getSimpleName(), "Alarm " + alarm.getEventName() + ", active=" + alarm.alarmActive());   // TODO fix output/display
			}
			else {
			   //Log.d(getClass().getSimpleName(), "Null Alarm - no update");   // TODO fix output/display
			}
		}
	}
	
	/** hit snooze for all active alarms
	 * 
	 */
	public void hitSnooze() {
		Iterator<AlarmEvent> iter = alarmEvents.iterator();
		while (iter.hasNext()) {
			AlarmEvent alarm = iter.next();
			if ((alarm != null) && alarm.alarmActive()) {
			   alarm.hitSnooze(defaultSnoozeTime);
			}
		}
	}
	
	/** deactivate all active alarms
	 * 
	 */
	public void hitOff() {
		for (int i = 0; i < alarmEvents.size(); i++) {
		    if((alarmEvents.get(i) != null && alarmEvents.get(i).alarmActive())){
		    	deactivateAlarm(i+1);  //disable this alarm
		    }
		}
	}

}
