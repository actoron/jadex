package deco4mas.distributed.coordinate.interpreter.agent_state;

import jadex.extension.envsupport.environment.IEnvironmentSpace;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationInformation;

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