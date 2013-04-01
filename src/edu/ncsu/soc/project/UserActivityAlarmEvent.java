package edu.ncsu.soc.project;

import java.util.Date;

public class UserActivityAlarmEvent extends AlarmEvent {

	public UserActivityAlarmEvent(String eventName, Date initialEventTime,
			Integer initialPrepTime, Integer minPrepTime) {
		super(eventName, initialEventTime, initialPrepTime, minPrepTime);
	
	}

	@Override
	public void updateCurrentAlarmTime() {
		if (this.currentPrepTime < minPrepTime) {
			UserAgent ua = UserAgent.getInstance();
			ua.takeSnoozeLimitAction();
		}

	}

}
