package deco4mas.distributed.coordinate;

import deco.distributed.lang.dynamics.causalities.DecentralMechanismLink;
import deco.distributed.lang.dynamics.properties.AgentReference;
import deco4mas.distributed.annotation.agent.CoordinationAnnotation.DirectionType;

/**
 * Intermediate data format. 
 * 
 * Stores the information about the coordination of agent elements.
 * This data structure is used only inside this package.
 * 
 * @author Jan Sudeikat
 *
 */
public class DecentralCoordinationInformation extends CoordinationInformation {

	//----------attributes----------
		
	/** The coordination link. */
	private DecentralMechanismLink dml;
	
	//----------constructors--------
	
	public DecentralCoordinationInformation(DirectionType direction, AgentReference ref,
			DecentralMechanismLink dml) {
		super();
		this.direction = direction;
		this.ref = ref;
		this.dml = dml;
		this.coordinationType = dml.getType();
	}

	//----------methods-------------

	public DecentralMechanismLink getDml() {
		return dml;
	}

	public void setDml(DecentralMechanismLink dml) {
		this.dml = dml;
	}
	
}
