package jadex.android.applications.demos.event;

import jadex.bridge.service.types.context.JadexAndroidEvent;

public class ShowToastEvent extends JadexAndroidEvent {

	private String message;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
