/**
 * 
 */
package deco4mas.mechanism;

import deco.lang.dynamics.mechanism.MechanismConfiguration;
import deco4mas.coordinate.environment.CoordinationSpace;

/**
 * All implemented mechanisms should use this abstract class.
 * 
 * @author Ante Vilenica & Thomas Preisler
 */
public abstract class ICoordinationMechanism {

	/* the default realisation name */
	public static String DEFAULT_REALISATIONNAME = "default";

	// -- attributes ---
	protected CoordinationSpace space;

	protected MechanismConfiguration mechanismConfiguration;

	protected String realisationName;

	// -- constructor ---
	/**
	 * Contruct a coordinationMechanism with the given {@link CoordinationSpace}
	 */
	public ICoordinationMechanism(CoordinationSpace space) {
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
	 * Used to consume and pass Coordination Events, that are produced by the Agent State Interpreter. These events are processed by the coordination medium which may cause the publication of
	 * percepts.
	 * 
	 * @param obj
	 *            the object
	 */
	public abstract void perceiveCoordinationEvent(Object obj);

	/**
	 * @return the mechanismConfiguration
	 */
	public MechanismConfiguration getMechanismConfiguration() {
		return mechanismConfiguration;
	}

	/**
	 * @param mechanismConfiguration
	 *            the mechanismConfiguration to set
	 */
	public void setMechanismConfiguration(MechanismConfiguration mechanismConfiguration) {
		this.mechanismConfiguration = mechanismConfiguration;
	}

	/**
	 * @return the space
	 */
	public CoordinationSpace getSpace() {
		return space;
	}

	/**
	 * @param space
	 *            the space to set
	 */
	public void setSpace(CoordinationSpace space) {
		this.space = space;
	}

	/**
	 * @param realisationName
	 *            the realisationName to set
	 */
	public void setRealisationName(String realisationName) {
		this.realisationName = realisationName;
	}

	/**
	 * @return the realisationName
	 */
	public String getRealisationName() {
		return realisationName;
	}
}