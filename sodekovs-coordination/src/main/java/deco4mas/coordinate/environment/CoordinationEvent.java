package deco4mas.coordinate.environment;

import jadex.application.space.envsupport.environment.EnvironmentEvent;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;



/**
 * 
 */
public class CoordinationEvent extends EnvironmentEvent
{
	/** Used to publish common events.*/
	public static final String COORDINATE_START = "coordination_publish_event";
//	public static final String COORDINATE_START = "coordination_start_env_event";

	/** Used to initialize the coordination participants.*/
	public static final String COORDINATE_INIT_PARTICIPANTS = "coordination_init_participants";
	
	/**
	 * 
	 */
	public CoordinationEvent(String type, IEnvironmentSpace space, ISpaceObject object, Object info)
	{
		//TODO: change to:
//		super(type, space, object, info, null);????
		super(type, space, object, null, info);
//		super(type,space,object,info);
	}	
}
