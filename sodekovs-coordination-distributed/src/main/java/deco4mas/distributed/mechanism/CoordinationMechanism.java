/**
 * 
 */
package deco4mas.distributed.mechanism;

import deco.distributed.lang.dynamics.mechanism.MechanismConfiguration;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;

/**
 * All implemented mechanisms should use this abstract class.
 * 
 * @author Ante Vilenica & Thomas Preisler
 */
public abstract class CoordinationMechanism implements ICoordinationMechanism {

	/* the default realisation name */
	public static String DEFAULT_REALISATIONNAME = "default";

	// -- attributes ---
	protected CoordinationSpace space;

	protected MechanismConfiguration mechanismConfiguration;

	protected String realisationName;
	
	//Used for distributed case: denotes the context of a distributed application.
	protected String coordinationContextID ;

	// -- constructor ---
	/**
	 * Contruct a coordinationMechanism with the given {@link CoordinationSpace}
	 */
	public CoordinationMechanism(CoordinationSpace space) {
		this.space = space;
	}

	// -- methods ---

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