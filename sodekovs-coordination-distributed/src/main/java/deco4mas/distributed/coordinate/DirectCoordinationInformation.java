package deco4mas.distributed.coordinate;

import deco.distributed.lang.dynamics.causalities.DirectLink;
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
public class DirectCoordinationInformation extends CoordinationInformation {

	//-------- attributes ----------

	/** The coordination link. */
	private DirectLink directLink;
	
	//-------- constructors -------------
	
	public DirectCoordinationInformation(DirectionType direction,
			AgentReference ref, DirectLink dl) {
		super();
		this.direction = direction;
		this.ref = ref;
		this.directLink = dl;
		this.coordinationType = dl.getType();
	}
	
	public DirectCoordinationInformation() {
		super();
	}

	//-------- methods -------------
	
	public DirectLink getDirectLink() {
		return directLink;
	}

	public void setDirectLink(DirectLink directLink) {
		this.directLink = directLink;
	}

}
