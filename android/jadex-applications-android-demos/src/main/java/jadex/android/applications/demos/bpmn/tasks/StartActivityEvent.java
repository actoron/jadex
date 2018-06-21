package jadex.android.applications.demos.bpmn.tasks;

import java.io.Serializable;
import java.util.HashMap;

import jadex.bridge.service.types.context.JadexAndroidEvent;

public class StartActivityEvent extends JadexAndroidEvent {
	
	private HashMap<String, Serializable> extras;
	private Class<?> activityClass;

	public StartActivityEvent() {
	}

	public StartActivityEvent(Class<?> activityClass, HashMap<String, Serializable> extras) {
		this.activityClass = activityClass;
		this.extras = extras;
	}

	public HashMap<String, Serializable> getExtras() {
		return extras;
	}

	public void setExtras(HashMap<String, Serializable> extras) {
		this.extras = extras;
	}

	public Class<?> getActivityClass() {
		return activityClass;
	}

	public void setActivityClass(Class<?> activityClass) {
		this.activityClass = activityClass;
	}
	
	
}
