package edu.ncsu.soc.project;

import java.util.Date;

import com.google.android.maps.GeoPoint;

/** event with (car) travel from source to event destination
 */
public class TravelTimeAlarmEvent extends AlarmEvent {
	private String startLocation;     // start location for this event (location when alarm activates)
	private String endLocation;       // end location for this event

	public TravelTimeAlarmEvent(String eventName, Date initialEventTime, Integer initialPrepTime,
			Integer minPrepTime, String startLocation, String endLocation) {
		super(eventName, initialEventTime, initialPrepTime, minPrepTime);
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.updateCurrentAlarmTime();              // initialize the alarm time
		this.initialAlarmTime = currentAlarmTime;   // store the initially predicted alarm time so changes can be tracked

	}
	
	@Override
	public void updateCurrentAlarmTime() {
		// get the current travel time
		TravelTimeAgent agent = TravelTimeAgent.getInstance();
		Integer currentTransitTime = agent.getTravelTime(startLocation, endLocation);
		// normal create/update case
		if (currentTransitTime != null) {
			// adjust the alarm time
			Date oldAlarmTime = null;
			if (this.currentAlarmTime != null) oldAlarmTime = this.currentAlarmTime;
			this.currentAlarmTime = DateUtils.addMinutes(this.initialEventTime, (currentTransitTime + this.currentPrepTime) * -1);  // subtract prep time to set alarm

			// if a time change more than a minute then set reason
			if (!DateUtils.datesRoughlyEqual(oldAlarmTime, this.currentAlarmTime, 1)) {
				this.alarmChangeReason = "Travel time change";
			}
		}
		
	}

}
