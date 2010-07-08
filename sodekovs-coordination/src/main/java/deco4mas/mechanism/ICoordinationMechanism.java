/**
 * 
 */
package deco4mas.mechanism;

import deco4mas.coordinate.environment.CoordinationSpace;

/**
 * @author Ante Vilenica
 * 
 * All implemented mechanisms should use this interface.
 */
public abstract class ICoordinationMechanism {
	
	// -- attributes ---
	protected CoordinationSpace space;
	
	// -- constructor ---
	public ICoordinationMechanism(CoordinationSpace space){
		this.space = space;
		}
	
	// -- methods ---
	public abstract void start();
	
	public abstract void stop();
	
	public abstract void restart();
	
	public abstract void suspend();
	
	public abstract String getRealisationName();
	
	
	/**
	 * Used to consume and pass Coordination Events, that are produced by the Agent State
	 * Interpreter. These events are processed by the coordination medium which may cause the publication of percepts.
	 * 
	 * @param obj the object
	 */
	public abstract void perceiveCoordinationEvent(Object obj);
}
