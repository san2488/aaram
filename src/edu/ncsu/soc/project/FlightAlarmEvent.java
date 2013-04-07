package edu.ncsu.soc.project;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.text.format.DateFormat;
import android.util.Log;

public class FlightAlarmEvent extends AlarmEvent {
	private String flightNumber = null;         // airline/flight number string, eg DL400
    private Boolean useActualAgent = true;    // if true use calls to flight service otherwise generate a response for debug
    
    private final String TAG = "FlightAlarmEvent";
    
	public FlightAlarmEvent(String eventName, Date initialFlightTime,
			Integer initialPrepTime, Integer minPrepTime, String flightNumber) {
		super(eventName, initialFlightTime, initialPrepTime, minPrepTime);  // set initialEventTime to initialFlightTime
		this.flightNumber = flightNumber;
		this.updateCurrentAlarmTime();              // initialize the alarm time
		this.initialAlarmTime = this.currentAlarmTime;   // store the initially predicted alarm time so changes can be tracked
	}

	@Override
	public void updateCurrentAlarmTime() {
		// get the updated flight time
		Date currentFlightTime;
		if (useActualAgent) {
			FlightAgent agent = FlightAgent.getInstance();
		    Calendar cal = new GregorianCalendar();
		    cal.setTime(this.initialEventTime);			
			currentFlightTime = agent.getDepartureTime(flightNumber, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));			
		}
		else {
		    currentFlightTime = DateUtils.addHours(new Date(), 4);  // for debug, just add 4 hrs to the current time
		}
		//Log.d(getClass().getSimpleName(), "init=" + this.initialEventTime + ", prep=" + this.currentPrepTime);   // TODO 

		// adjust the alarm time
		Date oldAlarmTime = this.currentAlarmTime;
		this.currentAlarmTime = DateUtils.addMinutes(currentFlightTime, this.currentPrepTime * -1);  // subtract prep time to set alarm
		

		UserAgent ua = UserAgent.getInstance();
		
		// don't bother setting alarm to later time if user has had enough sleep
		if(oldAlarmTime != null && (this.currentAlarmTime.after(oldAlarmTime) && !ua.isSleepDeprived())) {
			Log.i(TAG, "No need to delay alarm. Has had enough sleep.");
			this.currentAlarmTime = oldAlarmTime;
		}
			// if a time change more than 2 minutes then set reason
		if (!DateUtils.datesRoughlyEqual(oldAlarmTime, this.currentAlarmTime, 1)) {
			this.alarmChangeReason = "Flight time change";
		}
	}

}
