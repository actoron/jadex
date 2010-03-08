package deco4mas.coordinate.interpreter.agent_state;


import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import deco4mas.mechanism.CoordinationInformation;

/**
 * 
 * @author Ante Vilenica
 *
 */
public interface ICoordinationEventPublication {

	
	public void publishEvent(CoordinationInformation event, IEnvironmentSpace abstractSpace);
}
