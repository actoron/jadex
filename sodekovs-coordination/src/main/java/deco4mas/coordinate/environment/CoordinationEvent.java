package deco4mas.coordinate.environment;

import jadex.extension.envsupport.environment.EnvironmentEvent;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * 
 */
public class CoordinationEvent extends EnvironmentEvent {
	/** Used to publish common events. */
	public static final String COORDINATE_BROADCAST = "coordination_publish_event_broadcast";

	public static final String COORDINATE_DIRECT = "coordination_publish_event_direct";

	/**
	 * 
	 */
	public CoordinationEvent(String type, IEnvironmentSpace space, ISpaceObject object, Object info) {
		super(type, space, object, null, info);
	}
}