package deco4mas.coordinate.interpreter.agent_state;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.mechanism.CoordinationInformation;

/**
 * 
 * @author Ante Vilenica
 */
public class CoordinationEventPublication implements ICoordinationEventPublication {

	/**
	 * Default constructor.
	 */
	public CoordinationEventPublication() {
		super();
	}

	public void publishEvent(CoordinationInformation event, IEnvironmentSpace abstractSpace) {
		((CoordinationSpace) abstractSpace).perceiveCoordinationEvent(event);
	}
}