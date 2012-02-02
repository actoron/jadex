package deco4mas.distributed.coordinate.environment;


import jadex.commons.SimplePropertyObject;
import deco4mas.distributed.mechanism.CoordinationInformation;

public class CoordinationSpaceObject extends SimplePropertyObject{

	/** The supported agent architecture */
	public final static String AGENT_ARCHITECTURE = "agent_architecture";
		 	
	/** The 'part' of the Agent that will be influenced by the coordination. */
	public final static String AGENT_ELEMENT_TYPE = "agent_element_type";
	
	/** The coordination type: (+/-). */
	public final static String COORDINATION_TYPE = "coordination_type";
	
	/** The coordination type: (+/-). */
	public final static String COORDINATION_INFORMATION_TYPE = "coordination_space_object";
	
	
	public CoordinationSpaceObject(){
		super();
	}
	
	public CoordinationSpaceObject(CoordinationInformation coordInfo){
		super();
		this.setProperties(coordInfo.getValues());
	}
}
