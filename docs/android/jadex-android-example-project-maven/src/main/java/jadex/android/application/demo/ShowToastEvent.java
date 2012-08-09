package jadex.android.application.demo;

import jadex.bridge.service.types.context.IJadexAndroidEvent;

public class ShowToastEvent implements IJadexAndroidEvent {

	private String message;
	
	public static final String TYPE = "showToast";
	
	public String getType() {
		return TYPE;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
