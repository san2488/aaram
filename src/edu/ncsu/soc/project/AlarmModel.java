package edu.ncsu.soc.project;

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
	
	// user sleep state
	private Boolean sleepDeprived = false;   // indication that user needs sleep / use userActivityAgent to set this
	private Boolean currentlyActive = false;   // indication that  user is active / use userActivityAgent to set this

	private LinkedList<AlarmEvent> activeAlarmEvents = new LinkedList<AlarmEvent>();     // list of active alarms

	// TODO run a thread that periodically updates all alarms based on context, checks alarm state
	// TODO also periodically invoke userActivityAgent to determine if mode sleep is needed and if event prep times should be adjusted

	/** add a new alarm event to the list 
	 * 
	 */
	public void addAlarmEvent(AlarmEvent eAlarm) {
		activeAlarmEvents.add(eAlarm);
	}	
	
	/** update alarm times for each active event alarm based on context
	 * 
	 */
	public void updateAlarms() {
		Iterator<AlarmEvent> iter = activeAlarmEvents.iterator();
		Log.d(getClass().getSimpleName(), "-----");   // TODO 
		while (iter.hasNext()) {
			AlarmEvent alarm = iter.next();
			alarm.updateCurrentAlarmTime();   // call appropriate context update for this AlarmEvent
			Log.d(getClass().getSimpleName(), "Alarm " + alarm.getEventName() + ", active=" + alarm.alarmActive());   // TODO fix output/display
		}
	}
	
}
