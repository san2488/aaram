package edu.ncsu.soc.project;

import java.util.Date;

import com.google.android.maps.GeoPoint;

/** event with (car) travel from source to event destination
 */
public class TravelAlarmEvent extends AlarmEvent {
	private GeoPoint startLocation;     // start location for this event (location when alarm activates)
	private GeoPoint endLocation;       // end location for this event

	public TravelAlarmEvent(String eventName, Date initialEventTime, Integer initialPrepTime,
			Integer minPrepTime, GeoPoint startLocation, GeoPoint endLocation) {
		super(eventName, initialEventTime, initialPrepTime, minPrepTime);
		this.startLocation = startLocation;
		this.endLocation = endLocation;
	}
	
	@Override
	public void updateCurrentAlarmTime() {
		Integer transitTime = 0;  // TODO get from transitAgent
		// TODO add call to transitAgent that adjusts alarm time according to current travel time from startLocation to endLocation
		this.currentAlarmTime = DateUtils.addMinutes(this.initialEventTime, (this.currentPrepTime + transitTime) * -1);  // subtract prep and transit time to set alarm
	}

}