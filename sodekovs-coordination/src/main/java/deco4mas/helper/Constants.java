package deco4mas.helper;

/**
 * Helper class to keep the definition of constants at one place.
 * 
 * @author Ante Vilenica
 *
 */
public class Constants {

	/** The name of the belief that stores the Map with the information needed for the coordination.*/
	public static String DECO4MAS_BELIEF_NAME = "deco4masInfo";
	
	/** Used to store the role definitions for an AgentType for "PUBLISH".*/
	public static String ROLE_DEFINITIONS_FOR_PUBLISH = "roleDefintionsForPublish";
	
	/** Used to store the role definitions for an AgentType for "PERCEIVE".*/
	public static String ROLE_DEFINITIONS_FOR_PERCEIVE = "roleDefintionsForPerceive";
	
	/** Used to store the decentral coordination info mappings for an AgentType.*/
	public static String DECENTRAL_COORDINATION_INFO_MAPPING = "decentralInfoMapping";
	
	/** Used to name the "toAgents".*/
	public static String TO_AGENTS = "toAgents";
	
	/** Used to name the stored "value".*/
	public static String VALUE = "value";
	
	/** Used to store the IOAVState.*/
	public static String IOAV_STATE = "IOAVState";

	/** Used to store the RCapability.*/
	public static String R_CAPABILITY = "RCapability";
	
	/** Used to name the parameter and data mapping.*/
	public static String PARAMETER_DATA_MAPPING = "parameterDataMapping";
	
	/** Used to name a DML-Realization.*/
	public static String DML_REALIZATION_NAME = "dmlRealizationName";
	
	/** Perceive.*/
	public static String PERCEIVE = "perceive";
	
	/** Publish.*/
	public static String PUBLISH = "publish";
	
	
	//--- Constants from the old CoordinationProcessor
	
	/** The identifier of the content of a belief. */
	public static final String BELIEF_UPDATE_IDENTIFIER = "content";
	
	/** The identifier of the last element of a beliefset. */
	public static final String BELIEFSET_UPDATE_LAST_IDENTIFIER = "last";
	
	/** The identifier of all elements of a beliefset. */
	public static final String BELIEFSET_UPDATE_ALL_IDENTIFIER = "all";
	
	/** The identifier of the event that notifies about plan adoptions. */
	private static final String PLAN_ACTIVATION_EVENT = "BDI_planAdded";
	
	/** The identifier of the event that notifies about goal adoptions. */
	private static final String GOAL_ACTIVATION_EVENT = "BDI_goalAdded";
	
	//------------------------
}
