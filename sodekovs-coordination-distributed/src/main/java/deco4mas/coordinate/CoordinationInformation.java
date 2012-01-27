package deco4mas.coordinate;

import deco.lang.dynamics.properties.AgentReference;
import deco4mas.annotation.agent.CoordinationAnnotation.CoordinationType;
import deco4mas.annotation.agent.CoordinationAnnotation.DirectionType;

/**
 * Intermediate data format. 
 * 
 * Stores the information about the coordination of agent elements.
 * This data structure is used only inside this package.
 * 
 * @author Jan Sudeikat
 *
 */
public class CoordinationInformation {
	
	//-------- attributes ----------

	/** The direction type. */
	protected DirectionType direction;
	
	/** The agent reference. */
	protected AgentReference ref;
	
	/** The type of coordination (+/-). */
	protected CoordinationType coordinationType;

	//-------- constructors -------------
	
	public CoordinationInformation() {
		super();
	}

	//-------- methods -------------
	
	public DirectionType getDirection() {
		return direction;
	}

	public void setDirection(DirectionType direction) {
		this.direction = direction;
	}

	public AgentReference getRef() {
		return ref;
	}

	public void setRef(AgentReference ref) {
		this.ref = ref;
	}

	public CoordinationType getCoordinationType() {
		return coordinationType;
	}

	public void setCoordinationType(CoordinationType coordinationType) {
		this.coordinationType = coordinationType;
	}

}