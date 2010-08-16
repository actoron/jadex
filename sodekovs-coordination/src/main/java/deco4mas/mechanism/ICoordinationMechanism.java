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
	
	/* the default realisation name */
	public static String DEFAULT_REALISATIONNAME = "default";
	
	// -- attributes ---
	protected CoordinationSpace space;
	
	// -- constructor ---
	/**
	 * Contruct a coordinationMechanism with the given {@link CoordinationSpace}
	 */
	public ICoordinationMechanism(CoordinationSpace space){
		this.space = space;
		}
	
	// -- methods ---
	/**
	 * start the coordinationMechanism
	 */
	public abstract void start();
	
	/**
	 * stop the coordinationMechanism
	 */
	public abstract void stop();
	
	/**
	 * restart the coordinationMechanism
	 */
	public abstract void restart();
	
	/**
	 * suspend the coordinationMechanism
	 */
	public abstract void suspend();
	
	/**
	 * Get the realisation name of the mechanism
	 * @return
	 */
	public abstract String getRealisationName();
	
	
	/**
	 * Used to consume and pass Coordination Events, that are produced by the Agent State
	 * Interpreter. These events are processed by the coordination medium which may cause the publication of percepts.
	 * 
	 * @param obj the object
	 */
	public abstract void perceiveCoordinationEvent(Object obj);
}
