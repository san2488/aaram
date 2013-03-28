package edu.ncsu.soc.project;

import java.util.Date;

import android.text.format.DateFormat;
import android.util.Log;

public class FlightAlarmEvent extends AlarmEvent {
	private String flightNumber = null;         // airline/flight number string, eg DL400
	private Integer timeBeforeFlight = 0;     // airport arrival time before flight (also includes travel to airport?) in minutes 

	public FlightAlarmEvent(String eventName, Date initialFlightTime,
			Integer initialPrepTime, Integer minPrepTime, String flightNumber, Integer timeBeforeFlight) {
		super(eventName, initialFlightTime, initialPrepTime, minPrepTime);  // set initialEventTime to initialFlightTime
		this.flightNumber = flightNumber;
		this.timeBeforeFlight = timeBeforeFlight;
		this.updateCurrentAlarmTime();              // initialize the alarm time
		this.initialAlarmTime = this.currentAlarmTime;   // store the initially predicted alarm time so changes can be tracked
	}

	@Override
	public void updateCurrentAlarmTime() {
		Date currentFlightTime = DateUtils.addHours(new Date(), 10);  // TODO get actual value from flight Agent
		
		FlightAgent agent = FlightAgent.getInstance();
		Date estimatedFlightTime = agent.getDepartureTime(flightNumber);
		
		// TODO add call to flightAgent that returns currently scheduled time for specified flight number
		//Log.d(getClass().getSimpleName(), "init=" + this.initialEventTime + ", prep=" + this.currentPrepTime+ ", before=" + this.timeBeforeFlight);   // TODO fix output/display
		//Log.d(getClass().getSimpleName(), "init=" + DateUtils.toDateTime1(this.initialEventTime));   // TODO fix output/display

		this.currentAlarmTime = DateUtils.addMinutes(currentFlightTime, (this.currentPrepTime + this.timeBeforeFlight) * -1);  // subtract prep and airport time to set alarm
	}

}
