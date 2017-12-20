package citic.hz.mpos.service.event;

import citic.hz.phio.stdmsg.PhioMsg;

public class Event {
	
	private String eventName;
	private PhioMsg eventMsg;
	
	public Event(String eventName,PhioMsg eventMsg) {
		this.eventName = eventName;
		this.eventMsg = eventMsg;
	}
	
	public String getEventName() {
		return eventName;
	}
	
	public PhioMsg getEventMsg() {
		return eventMsg;
	}
	
	
}
