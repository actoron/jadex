package deco4mas.distributed.coordinate.interpreter.agent_state;

import jadex.bridge.IExternalAccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import deco.distributed.lang.dynamics.AgentElementType;
import deco.distributed.lang.dynamics.MASDynamics;
import deco.distributed.lang.dynamics.mechanism.AgentElement;
import deco.distributed.lang.dynamics.properties.AgentReference;
import deco4mas.distributed.coordinate.environment.CoordinationSpaceObject;
import deco4mas.distributed.helper.Constants;
import deco4mas.distributed.mechanism.CoordinationInfo;

public abstract class BehaviorObservationComponent {

	/** The external access to the observed agent. */
	protected IExternalAccess extAccess;

	/** The event publication */
	protected CoordinationEventPublication eventPublication;

	/** Maps the roles that are used within "PUBLISH". */
	protected Map<String, AgentReference> roleDefinitionsForPublish = new HashMap<String, AgentReference>();

	/**
	 * Maps the roles that are used within "PERCEIVE". The String is the name of the DCM. The array holds the corresponding DecentralCoordinationInformation (position 0) and AgentElement (position 1).
	 */
	protected Map<String, Set<Object[]>> roleDefinitionsForPerceive = new HashMap<String, Set<Object[]>>();

	/**
	 * This mapping contains the parameter and data mappings of the deco-link-realization.
	 */
	protected Map<String, AgentElement> parameterAndDataMappings = new HashMap<String, AgentElement>();

	/**
	 * Contains the mapping from an event inside an agent, i.e. a goal is dispatched, a beliefset has changed , and maps these events to those DCM Realizations, that should trigger a publish, when
	 * these event has appeared using their specific medium. Other explanation: which agentEvent is references within which DCM-Realization!
	 */
	protected Map<String, ArrayList<String>> agentEventDCMRealizationMappings = new HashMap<String, ArrayList<String>>();

	public BehaviorObservationComponent(IExternalAccess extAccess, MASDynamics masDynamics) {
		this.extAccess = extAccess;
		eventPublication = new CoordinationEventPublication();
	}

	/**
	 * Get the role definitions that are used within the "PUBLICATION"
	 * 
	 * @return the roleDefinitions
	 */
	public Map<String, AgentReference> getRoleDefinitionsForPublish() {
		return roleDefinitionsForPublish;
	}

	/**
	 * Get the role definitions that are used within the "PERCEIVE". The String is the name of the DCM. The array holds the corresponding DecentralCoordinationInformation (position 0) and AgentElement
	 * (position 1).
	 * 
	 * @return the roleDefinitions
	 */
	public Map<String, Set<Object[]>> getRoleDefinitionsForPerceive() {
		return roleDefinitionsForPerceive;
	}

	/**
	 * @return the parameterAndDataMappings
	 */
	public Map<String, AgentElement> getParameterAndDataMappings() {
		return parameterAndDataMappings;
	}

	/**
	 * Helper method, in order to add values to an ArrayList inside a HashMap
	 * 
	 * @param hashMap
	 * @param key
	 * @param value
	 */
	protected void addValueToMap(Map<String, ArrayList<String>> hashMap, String key, String value) {
		if (hashMap.get(key) == null) {
			ArrayList<String> newList = new ArrayList<String>();
			newList.add(value);
			hashMap.put(key, newList);
		} else {
			hashMap.get(key).add(value);
		}
	}

	protected CoordinationInfo createCoordinationInfo(Object value, HashMap<String, Object> parameterDataMappings, String agentElementName, AgentElementType agentElementType, String dmlRealizationName) {
		CoordinationInfo coordInfo = new CoordinationInfo();
		coordInfo.setName("MediumCoordInfo-" + new Date().getTime());
		coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_NAME, agentElementName);
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, agentElementType.toString());
		coordInfo.addValue(Constants.VALUE, value);
		coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING, parameterDataMappings);
		coordInfo.addValue(Constants.DML_REALIZATION_NAME, dmlRealizationName);
		coordInfo.addValue(Constants.SENDER_AGENT, extAccess.getComponentIdentifier());

		return coordInfo;
	}
}
