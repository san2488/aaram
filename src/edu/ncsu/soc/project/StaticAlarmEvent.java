package edu.ncsu.soc.project;

import java.util.Date;

/** static alarm event (no travel context)
 */
public class StaticAlarmEvent extends AlarmEvent {
	
	public StaticAlarmEvent(String eventName, Date initialEventTime, Integer initialPrepTime, Integer minPrepTime) {
		super(eventName, initialEventTime, initialPrepTime, minPrepTime);  
		this.updateCurrentAlarmTime();              // initialize the alarm time
		this.initialAlarmTime = currentAlarmTime;   // store the initially predicted alarm time so changes can be tracked

	}

	@Override
	public void updateCurrentAlarmTime() {
		// no context update for StaticAlarmEvent
		this.currentAlarmTime = DateUtils.addMinutes(this.initialEventTime, this.currentPrepTime * -1);  // subtract prep time to set alarm
	}

}
