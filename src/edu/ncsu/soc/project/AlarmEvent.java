package edu.ncsu.soc.project;

import java.util.Date;

import android.util.Log;

/**
 * context aware alarm event    
 * @author snellenbach
 *
 */
public abstract class AlarmEvent {

	// setup info for this event
	protected String eventName;           // name of this event alarm
	protected Date initialEventTime;      // start time predicted for this event 
	protected Integer initialPrepTime;    // optimal preparation time needed before any travel in minutes
	protected Integer minPrepTime;        // minimal preparation time needed (for snooze disable)
	
	// current info for this event based on context info
	protected Date currentAlarmTime;      // context-aware alarm time predicted for this event 
	protected Date initialAlarmTime;      // initial alarm time predicted for this event 
	protected Integer currentPrepTime;    // current preparation time allocated for event in minutes
	protected Date lastUpdateTime;        // time context info for this event was last updated
	protected String alarmChangeReason;   // reason for a change from initialAlarmTime to currentAlarmTime (null if no change)
	
	// constructor
	/** create a new adaptive alarm event
	 */
	public AlarmEvent(String eventName, Date initialEventTime, Integer initialPrepTime, Integer minPrepTime) {
		this.eventName = eventName;
		this.initialEventTime = initialEventTime;
		this.initialPrepTime = initialPrepTime;
		this.minPrepTime = minPrepTime;
		
		this.currentAlarmTime = null;               // this will be set in child constructors
		this.initialAlarmTime = null;               // this will be set in child constructors
		this.currentPrepTime = initialPrepTime;     // prep time starts with the optimal value
		this.lastUpdateTime = new Date();           // last update time is init time
		this.alarmChangeReason = "";                // no update so far
		
	}	
	
	// protected methods
	
	/**
	 * set a new current alarm time for this event along with a reason for the change
	 */
	protected void setCurrentAlarmTime(Date currentAlarmTime, String alarmChangeReason) {
		this.currentAlarmTime = currentAlarmTime;  
		this.alarmChangeReason = alarmChangeReason;  
	}
	
	// public methods
	
	/**
	 * return a text string containing reason for a change in current alarm 
	 */
	public String getEventName() {
		return this.eventName;
	}
	
	/**
	 * return the currently set alarm time for this event 
	 */
	public Date getCurrentAlarmTime() {
		return this.currentAlarmTime;
	}
	
	/**
	 * return the initially set alarm time for this event 
	 */
	public Date getInitialAlarmTime() {
		return this.initialAlarmTime;
	}
	
	/**
	 * return a text string containing reason for a change in current alarm 
	 */
	public String getCurrentChangeReason() {
		return this.alarmChangeReason;
	}
	
	/**
	 * update the currently set alarm time (can be overridden for context) 
	 */
	public abstract void updateCurrentAlarmTime();
	
	/**
	 * return true if alarm is active (when present time is beyond currentAlarmTime)
	 */
	public Boolean alarmActive() {
		Date dNow = new Date( );   // get current date/time
		Boolean retAlarm = currentAlarmTime.before(dNow);
		return retAlarm;
	}
		
	/**
	 * delay the alarm by specified number of minutes if sufficient prep time exists
	 */
	public void hitSnooze(Integer snoozeMins) {
		// if delay decreases prep time by an allowable amount, then change prep time and alarm time
		if (this.currentPrepTime - snoozeMins > minPrepTime) {
			this.currentPrepTime = this.currentPrepTime -snoozeMins;
			Date newTime = DateUtils.addMinutes(this.currentAlarmTime, snoozeMins);    // calculate delayed time
			setCurrentAlarmTime(newTime, "Snooze button was hit");  // delay the alarm by snooze amount
		}
		else {
			UserAgent ua = UserAgent.getInstance();
			ua.takeSnoozeLimitAction();
		}
	}
	
	/** create a new adaptive alarm event
	 */
	public void show() {
		Log.d(getClass().getSimpleName(), "--- Alarm event, name=" + this.eventName);  
		
		Log.d(getClass().getSimpleName(), "    initial event time=" + this.initialEventTime.toString());
		Log.d(getClass().getSimpleName(), "    initial prep time=" + this.initialPrepTime);
		Log.d(getClass().getSimpleName(), "    min prep time=" + this.minPrepTime);
		
		if (this.initialAlarmTime == null) Log.d(getClass().getSimpleName(), "    ** initial alarm time is null **"); 
		else Log.d(getClass().getSimpleName(), "    initial alarm time=" + this.initialAlarmTime.toString());
		
		if (this.currentAlarmTime == null) Log.d(getClass().getSimpleName(), "    ** current alarm time is null **"); 
		else Log.d(getClass().getSimpleName(), "    current alarm time=" + this.currentAlarmTime.toString());
		
		Log.d(getClass().getSimpleName(), "    current prep time=" + this.currentPrepTime);
				
	}	

}
