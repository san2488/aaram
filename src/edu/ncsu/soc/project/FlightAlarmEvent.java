package edu.ncsu.soc.project;

import java.util.Date;

import android.text.format.DateFormat;
import android.util.Log;

public class FlightAlarmEvent extends AlarmEvent {
	private String flightNumber = null;         // airline/flight number string, eg DL400
    private Boolean useActualAgent = true;    // if true use calls to flight service otherwise generate a response for debug
    
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
			currentFlightTime = agent.getDepartureTime(flightNumber);			
		}
		else {
		    currentFlightTime = DateUtils.addHours(new Date(), 4);  // for debug, just add 4 hrs to the current time
		}
		//Log.d(getClass().getSimpleName(), "init=" + this.initialEventTime + ", prep=" + this.currentPrepTime);   // TODO 

		// adjust the alarm time
		Date oldAlarmTime = this.currentAlarmTime;
		this.currentAlarmTime = DateUtils.addMinutes(currentFlightTime, this.currentPrepTime * -1);  // subtract prep time to set alarm
		
		// if a time change more than 2 minutes then set reason
		if (!DateUtils.datesRoughlyEqual(oldAlarmTime, this.currentAlarmTime, 1)) {
			this.alarmChangeReason = "Flight time change";
		}
	}

}
