/**
 * 
 */
package deco4mas.coordinate.interpreter.agent_state;


import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBeliefListener;
import jadex.bdi.runtime.IBeliefSetListener;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.IInternalEventListener;
import jadex.bdi.runtime.IPlanListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import deco.lang.dynamics.AgentElementType;
import deco.lang.dynamics.mechanism.AgentElement;
import deco.lang.dynamics.properties.AgentReference;
import deco4mas.coordinate.DecentralCoordinationInformation;
import deco4mas.coordinate.environment.CoordinationSpaceObject;
import deco4mas.coordinate.interpreter.coordination_information.CheckRole;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInfo;

/**
 * @author Ante Vilenica 
 * This component is called on Agent init and observes the agent. If an event occurs that is relevant for the coordination this event is dispatched to the "Coordination Event Publication".
 * 
 */
public class BDIBehaviorObservationComponent {

	/** The external access to the observed agent. */
	private IBDIExternalAccess exta;
	private BDICoordinationEventPublication eventPublication;
	/**
	 * Contains the mapping from an event inside an agent, i.e. a goal is dispatched, a beliefset has changed , and maps these events to those DCM Realizations, that should trigger a publish, when these event has appeared using their specific medium.
	 * Other explanation: which agentEvent is references within which DCM-Realization!
	 * 
	 * */

	private Map<String, ArrayList<String>> agentEventDCMRealizationMappings = new HashMap<String, ArrayList<String>>();
	/** Maps the roles that are used within "PUBLISH". */
	private Map<String, AgentReference> roleDefinitionsForPublish = new HashMap<String, AgentReference>();

	/** Maps the roles that are used within "PERCEIVE". The String is the name of the DCM. The array holds the corresponding DecentralCoordinationInformation (position 0) and AgentElement (position 1). */
	private Map<String, Set<Object[]>> roleDefinitionsForPerceive = new HashMap<String, Set<Object[]>>();

	/** This mapping contains the parameter and data mappings of the deco-link-realization. */
	private Map<String, AgentElement> parameterAndDataMappings = new HashMap<String, AgentElement>();

	// /** Maps the AgentType to its DecentralCoordinationInformation */
	// private DecentralCoordinationInformation decentralCoordInfoMapping;

	/**
	 * 
	 * @param exta
	 *            The external access to the observed agent.
	 */
	public BDIBehaviorObservationComponent(IBDIExternalAccess exta) {
		this.exta = exta;
		eventPublication = new BDICoordinationEventPublication();
		// initListeners();
	}

	/**
	 * Publish/Dispatch the occurred event to the "Coordination Event Publication".
	 */
	// private void publishEvent(AgentEvent ae, AgentElementType agentElementType, String agentElementName, Collection toAgents, HashMap<String, Object> parameterDataMappings) {
	private void publishEvent(Object value, HashMap<String, Object> parameterDataMappings, String dmlRealizationName) {

		CoordinationInfo coordInfo = new CoordinationInfo();
		coordInfo.setName("Test-Coord-Info-" + new Date().getTime());
		coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, "BDI");
		coordInfo.addValue(Constants.VALUE, value);
		coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING, parameterDataMappings);
		coordInfo.addValue(Constants.DML_REALIZATION_NAME, dmlRealizationName);

		eventPublication.publishEvent(coordInfo, (IEnvironmentSpace) exta.getBeliefbase().getBelief("env").getFact());
	}

	public void initBeliefListener(AgentElement agentElement, String mechanismRealizationId) {
		// System.out.println("#BDIBehaviorObservationComponent# init listener for belief: " + agentElement.getElement_id());
		addValueToMap(agentEventDCMRealizationMappings, AgentElementType.BDI_BELIEF.toString() + "::" + agentElement.getElement_id(), mechanismRealizationId);
		exta.getBeliefbase().getBelief(agentElement.getElement_id()).addBeliefListener(new IBeliefListener() {
			public void beliefChanged(AgentEvent ae) {
				// getExternalAccess().getLogger().info("belief changed: "+ae);
				// System.out.println("#BDIBehaviorObservationComponent#belief changed: " + ae.getValue() + " - " + ae.getSource());
				// getExternalAccess().getBeliefbase().getBelief("bel").removeBeliefListener(this);

				// ------------------------- OLD ----------------------------------
				//				
				// String nameOfElement = getNameOfAgentElement(ae, AgentElementType.BDI_BELIEF);
				// //get all the DCM Realizations that have the current AgentEvent as initiator for a PUBLISH-Event
				// for(String dmlRealizationName: agentEventDCMRealizationMappings.get(AgentElementType.BDI_BELIEF.toString() + "::" + nameOfElement)){
				// //Check whether role is active.
				// HashMap<String, Object> parameterDataMappings = publishWhenApplicable(dmlRealizationName + "::" + nameOfElement , ae, AgentElementType.BDI_BELIEF);
				// if (parameterDataMappings != null) {
				// // publishEvent(ae, AgentElementType.BDI_BELIEF, CoordinationInfo.AGENT_ELEMENT_NAME, getToAgents(ae.getSource().toString(), AgentElementType.BDI_BELIEF), parameterDataMappings);
				// publishEvent(ae.getValue(), parameterDataMappings, dmlRealizationName);
				// } else {
				// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
				// }
				//					
				// // behObserver.getRoleDefinitions().put(perceptType + "::" + dci.getDml().getRealization() + "::" + ae.getElement_id() + "::" + ae.getAgentElementType(), dci.getRef());
				// }
				//
				// // HashMap<String, Object> parameterDataMappings = publishWhenApplicable(ae, AgentElementType.BDI_BELIEF);
				// // if (parameterDataMappings != null) {
				// // publishEvent(ae, AgentElementType.BDI_BELIEF, CoordinationInfo.AGENT_ELEMENT_NAME, getToAgents(ae.getSource().toString(), AgentElementType.BDI_BELIEF), parameterDataMappings);
				// // } else {
				// // System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
				// // }
				//				
				//				
				// ------------------------- OLD ----------------------------------

				checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEF);
			}
		});
	}

	public void initBeliefSetListener(AgentElement agentElement, String mechanismRealizationId) {
		// System.out.println("#BDIBehaviorObservationComponent# init listener for beliefSet: " + agentElement.getElement_id());
		addValueToMap(agentEventDCMRealizationMappings, AgentElementType.BDI_BELIEFSET.toString() + "::" + agentElement.getElement_id(), mechanismRealizationId);
		// try {
		exta.getBeliefbase().getBeliefSet(agentElement.getElement_id()).addBeliefSetListener(new IBeliefSetListener() {
			public void factAdded(AgentEvent ae) {
				// System.out.println("#BDIBehaviorObservationComponent#beliefSet fact added: " + ae.getValue() + " - " + ae.getSource());

				// String nameOfElement = getNameOfAgentElement(ae, AgentElementType.BDI_BELIEFSET);
				// //get all the DCM Realizations that have the current AgentEvent as initiator for a PUBLISH-Event
				// for(String dmlRealizationName: agentEventDCMRealizationMappings.get(AgentElementType.BDI_BELIEFSET.toString() + "::" + nameOfElement)){
				// //Check whether role is active.
				// HashMap<String, Object> parameterDataMappings = publishWhenApplicable(dmlRealizationName + "::" + nameOfElement , ae, AgentElementType.BDI_BELIEFSET);
				// if (parameterDataMappings != null) {
				// // publishEvent(ae, AgentElementType.BDI_BELIEF, CoordinationInfo.AGENT_ELEMENT_NAME, getToAgents(ae.getSource().toString(), AgentElementType.BDI_BELIEF), parameterDataMappings);
				// publishEvent(ae.getValue(), parameterDataMappings, dmlRealizationName);
				// } else {
				// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
				// }
				// // behObserver.getRoleDefinitions().put(perceptType + "::" + dci.getDml().getRealization() + "::" + ae.getElement_id() + "::" + ae.getAgentElementType(), dci.getRef());
				// }
				checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEFSET);
			}

			public void factRemoved(AgentEvent ae) {
				// System.out.println("#BDIBehaviorObservationComponent#beliefSet fact removed: " + ae.getValue() + " - " + ae.getSource());
				// HashMap<String, Object> parameterDataMappings = publishWhenApplicable(ae, AgentElementType.BDI_BELIEFSET);
				// if (parameterDataMappings != null) {
				// publishEvent(ae, AgentElementType.BDI_BELIEFSET, CoordinationInfo.AGENT_ELEMENT_NAME, getToAgents(ae.getSource().toString(), AgentElementType.BDI_BELIEFSET),parameterDataMappings);
				// } else {
				// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
				// }
				checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEFSET);
			}

			public void factChanged(AgentEvent ae) {
				// System.out.println("#BDIBehaviorObservationComponent#beliefSet fact changed: " + ae.getValue() + " - " + ae.getSource());
				// HashMap<String, Object> parameterDataMappings = publishWhenApplicable(ae, AgentElementType.BDI_BELIEFSET);
				// if (parameterDataMappings != null) {
				// publishEvent(ae, AgentElementType.BDI_BELIEFSET, CoordinationInfo.AGENT_ELEMENT_NAME, getToAgents(ae.getSource().toString(), AgentElementType.BDI_BELIEFSET), parameterDataMappings);
				// } else {
				// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
				// }
				checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEFSET);
			}
		});
		// } catch (Exception e) {
		// System.out.println("#BDIBehObser#" + e);
		// }
	}

	public void initGoalListener(AgentElement agentElement, String mechanismRealizationId) {
		// System.out.println("#BDIBehaviorObservationComponent# init listener for goal: " + agentElement.getElement_id());
		// agentEventDCMRealizationMappings.put(AgentElementType.BDI_GOAL.toString() + "::" + agentElement.getElement_id(), toAgents);
		addValueToMap(agentEventDCMRealizationMappings, AgentElementType.BDI_GOAL.toString() + "::" + agentElement.getElement_id(), mechanismRealizationId);
		exta.getGoalbase().addGoalListener(agentElement.getElement_id(), new IGoalListener() {
			public void goalAdded(AgentEvent ae) {
				// System.out.println("#BDIBehaviorObservationComponent#goal added: " + ae.getValue() + " - " + ae.getSource());
				// HashMap<String, Object> parameterDataMappings = publishWhenApplicable(ae, AgentElementType.BDI_GOAL);
				// if (parameterDataMappings != null) {
				// publishEvent(ae, AgentElementType.BDI_GOAL, CoordinationInfo.AGENT_ELEMENT_NAME, getToAgents(ae.getSource().toString(), AgentElementType.BDI_GOAL), parameterDataMappings);
				// } else {
				// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
				// }
				checkAndPublishIfApplicable(ae, AgentElementType.BDI_GOAL);
			}

			public void goalFinished(AgentEvent ae) {
				// System.out.println("#BDIBehaviorObservationComponent#goal finished: " + ae.getValue() + " - " + ae.getSource());
				// HashMap<String, Object> parameterDataMappings = publishWhenApplicable(ae, AgentElementType.BDI_GOAL);
				// if (parameterDataMappings != null) {
				// publishEvent(ae, AgentElementType.BDI_GOAL, CoordinationInfo.AGENT_ELEMENT_NAME, getToAgents(ae.getSource().toString(), AgentElementType.BDI_GOAL), parameterDataMappings);
				// } else {
				// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
				// }
				checkAndPublishIfApplicable(ae, AgentElementType.BDI_GOAL);
			}
		});
	}

	public void initPlanListener(AgentElement agentElement, String mechanismRealizationId) {
		// System.out.println("#BDIBehaviorObservationComponent# init listener for plan: " + agentElement.getElement_id());
		// agentEventDCMRealizationMappings.put(AgentElementType.BDI_PLAN.toString() + "::" + agentElement.getElement_id(), toAgents);
		addValueToMap(agentEventDCMRealizationMappings, AgentElementType.BDI_PLAN.toString() + "::" + agentElement.getElement_id(), mechanismRealizationId);
		exta.getPlanbase().addPlanListener(agentElement.getElement_id(), new IPlanListener() {

			public void planAdded(AgentEvent ae) {
				// System.out.println("#BDIBehaviorObservationComponent#plan added: " + ae.getValue() + " - " + ae.getSource());
				// HashMap<String, Object> parameterDataMappings = publishWhenApplicable(ae, AgentElementType.BDI_PLAN);
				// if (parameterDataMappings != null) {
				// publishEvent(ae, AgentElementType.BDI_PLAN, CoordinationInfo.AGENT_ELEMENT_NAME, getToAgents(ae.getSource().toString(), AgentElementType.BDI_PLAN), parameterDataMappings);
				// } else {
				// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
				// }
				checkAndPublishIfApplicable(ae, AgentElementType.BDI_PLAN);
			}

			public void planFinished(AgentEvent ae) {
				// System.out.println("#BDIBehaviorObservationComponent#plan finished: " + ae.getValue() + " - " + ae.getSource());
				// HashMap<String, Object> parameterDataMappings = publishWhenApplicable(ae, AgentElementType.BDI_PLAN);
				// if (parameterDataMappings != null) {
				// publishEvent(ae, AgentElementType.BDI_PLAN, CoordinationInfo.AGENT_ELEMENT_NAME, getToAgents(ae.getSource().toString(), AgentElementType.BDI_PLAN), parameterDataMappings);
				// } else {
				// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
				// }
				checkAndPublishIfApplicable(ae, AgentElementType.BDI_PLAN);
			}
		});
	}

	public void initInternalEventListener(AgentElement agentElement, String mechanismRealizationId) {

		addValueToMap(agentEventDCMRealizationMappings, AgentElementType.INTERNAL_EVENT.toString() + "::" + agentElement.getElement_id(), mechanismRealizationId);
		exta.getEventbase().addInternalEventListener(agentElement.getElement_id(), new IInternalEventListener() {

			public void internalEventOccurred(AgentEvent ae) {
				checkAndPublishIfApplicable(ae, AgentElementType.INTERNAL_EVENT);
			}
		});
	}

	/**
	 * Check the role condition for this Event. If the Agent has the specified role than publish the event to the coordination medium.
	 * 
	 * @param ae
	 * @param agentElementType
	 * @param key
	 *            partial key name
	 * @return if !=null then applicable. contains then a map of the parameter and data mappings
	 */
	private HashMap<String, Object> publishWhenApplicable(String key, AgentEvent ae, AgentElementType agentElementType) {

		// behObserver.getRoleDefinitions().put(ae.getElement_id() + "::" + ae.getAgentElementType(), erList);
		// ArrayList<ElementReference> roles;
		// String key = null;
		// if (agentElementType.equals(AgentElementType.BDI_BELIEFSET)) {
		// key = ae.getSource().toString().substring(ae.getSource().toString().indexOf("BeliefSet(") + 10, ae.getSource().toString().indexOf("-beliefset_"));
		// } else if (agentElementType.equals(AgentElementType.BDI_BELIEF)) {
		// key = ae.getSource().toString().substring(ae.getSource().toString().indexOf("Belief(") + 7, ae.getSource().toString().indexOf("-belief_"));
		// } else if (agentElementType.equals(AgentElementType.BDI_GOAL)) {
		// key = ae.getSource().toString().substring(ae.getSource().toString().indexOf("Goal(") + 5, ae.getSource().toString().indexOf("-goal_"));
		// } else if (agentElementType.equals(AgentElementType.BDI_PLAN)) {
		// key = ae.getSource().toString().substring(ae.getSource().toString().indexOf("Plan(") + 5, ae.getSource().toString().indexOf("-plan_"));
		// }

		return CheckRole.checkForPublish(roleDefinitionsForPublish.get(Constants.PUBLISH + "::" + key + "::" + agentElementType.toString()), parameterAndDataMappings.get(Constants.PUBLISH + "::"
				+ key + "::" + agentElementType.toString()), ae, exta);
	}

	// /**
	// * Retrieves the right toAgents for an AgentEvent. Uses the HashMap where the mappings have been stored.
	// *
	// * @return the Collection with the toAgents.
	// */
	// private Collection getToAgents(String source, AgentElementType agentElementType) {
	//
	// if (agentElementType.equals(AgentElementType.BDI_BELIEFSET)) {
	// String key = source.substring(source.indexOf("BeliefSet(") + 10, source.indexOf("-beliefset_"));
	// // System.out.println("EEEEEEEEEEEEEEEEEEEE: " + AgentElementType.BDI_BELIEFSET.toString() + "::" + key);
	// return agentElementCollection2HashMapCollection(agentEventDCMRealizationMappings.get(AgentElementType.BDI_BELIEFSET.toString() + "::" + key));
	// } else if (agentElementType.equals(AgentElementType.BDI_BELIEF)) {
	// String key = source.substring(source.indexOf("Belief(") + 7, source.indexOf("-belief_"));
	// // System.out.println("EEEEEEEEEEEEEEEEEEEE: " + AgentElementType.BDI_BELIEF.toString() + "::" + key);
	// return agentElementCollection2HashMapCollection(agentEventDCMRealizationMappings.get(AgentElementType.BDI_BELIEF.toString() + "::" + key));
	// } else if (agentElementType.equals(AgentElementType.BDI_GOAL)) {
	// String key = source.substring(source.indexOf("Goal(") + 5, source.indexOf("-goal_"));
	// // System.out.println("EEEEEEEEEEEEEEEEEEEE: " + AgentElementType.BDI_GOAL.toString() + "::" + key);
	// return agentElementCollection2HashMapCollection(agentEventDCMRealizationMappings.get(AgentElementType.BDI_GOAL.toString() + "::" + key));
	// } else if (agentElementType.equals(AgentElementType.BDI_PLAN)) {
	// String key = source.substring(source.indexOf("Plan(") + 5, source.indexOf("-plan_"));
	// // System.out.println("EEEEEEEEEEEEEEEEEEEE: " + AgentElementType.BDI_Plan.toString() + "::" + key);
	// return agentElementCollection2HashMapCollection(agentEventDCMRealizationMappings.get(AgentElementType.BDI_PLAN.toString() + "::" + key));
	// }
	// return null;
	// }

	/**
	 * Retrieves the name of the agentElement which is part of the "ae" String. The name is used as a key for the agentEventDCMRealizationMappings.
	 * 
	 * @return the name of the AgentElement
	 */
	private String getNameOfAgentElement(AgentEvent ae, AgentElementType agentElementType) {

		if (agentElementType.equals(AgentElementType.BDI_BELIEFSET)) {
			return ae.getSource().toString().substring(ae.getSource().toString().indexOf("BeliefSet(") + 10, ae.getSource().toString().indexOf("-beliefset_"));
		} else if (agentElementType.equals(AgentElementType.BDI_BELIEF)) {
			return ae.getSource().toString().substring(ae.getSource().toString().indexOf("Belief(") + 7, ae.getSource().toString().indexOf("-belief_"));
		} else if (agentElementType.equals(AgentElementType.BDI_GOAL)) {
			return ae.getSource().toString().substring(ae.getSource().toString().indexOf("Goal(") + 5, ae.getSource().toString().indexOf("-goal_"));
		} else if (agentElementType.equals(AgentElementType.BDI_PLAN)) {
			return ae.getSource().toString().substring(ae.getSource().toString().indexOf("Plan(") + 5, ae.getSource().toString().indexOf("-plan_"));
		} else if (agentElementType.equals(AgentElementType.INTERNAL_EVENT)) {
			return ae.getSource().toString().substring(ae.getSource().toString().indexOf("InternalEvent(") + 14, ae.getSource().toString().indexOf("-internalevent_"));
		}
		return null;
	}

	// /**
	// * Converts a Collection of AgentElement-Objects into a Collection of HashMaps that contain the same information. Needed because AgentElement-Object are not serializable and can therefore not be processed by the TSpaceServer.
	// *
	// * @param agentElement
	// * @return CoordinationInfo
	// */
	//
	// private ArrayList<HashMap<String, Object>> agentElementCollection2HashMapCollection(Collection agentElements) {
	//
	// ArrayList<HashMap<String, Object>> res = new ArrayList<HashMap<String, Object>>();
	//
	// // convert AgentElement-Objects into a HashMap, i.e. "copy" the values.
	// for (int i = 0; i < agentElements.size(); i++) {
	// AgentElement agentElement = (AgentElement) ((ArrayList) agentElements).get(i);
	// HashMap<String, Object> values = new HashMap<String, Object>();
	// values.put(CoordinationInfo.AGENT_TYPE, agentElement.getAgent_id());
	// values.put(CoordinationInfo.AGENT_ELEMENT_TYPE, agentElement.getAgentElementType());
	// values.put(CoordinationInfo.AGENT_ELEMENT_NAME, agentElement.getElement_id());
	//
	// res.add(values);
	// }
	// return res;
	// }

	/**
	 * Get the role definitions that are used within the "PUBLICATION"
	 * 
	 * @return the roleDefinitions
	 */
	public Map<String, AgentReference> getRoleDefinitionsForPublish() {
		return roleDefinitionsForPublish;
	}

	/**
	 * Get the role definitions that are used within the "PERCEIVE". The String is the name of the DCM. The array holds the corresponding DecentralCoordinationInformation (position 0) and AgentElement (position 1).
	 * 
	 * @return the roleDefinitions
	 */
	public Map<String, Set<Object[]>> getRoleDefinitionsForPerceive() {
		return roleDefinitionsForPerceive;
	}

	// /**
	// * @param roleDefinitions
	// * the roleDefinitions to set
	// */
	// public void setRoleDefinitions(Map<String, ArrayList<ElementReference>> roleDefinitions) {
	// this.roleDefinitions = roleDefinitions;
	// }
	//
	// /**
	// * @return the decentralCoordInfoMappings
	// */
	// public DecentralCoordinationInformation getDecentralCoordInfoMapping() {
	// return decentralCoordInfoMapping;
	// }
	//
	// /**
	// * @param decentralCoordInfoMapping
	// * the decentralCoordInfoMapping to set
	// */
	// public void setDecentralCoordInfoMapping(DecentralCoordinationInformation decentralCoordInfoMapping) {
	// this.decentralCoordInfoMapping = decentralCoordInfoMapping;
	// }

	/**
	 * Check for each received event whether the role definition is active for ALL DCM that are interested in this event (i.e. that have registered an listener) and dispatch event to medium if role definition is satisfied.
	 */
	private void checkAndPublishIfApplicable(AgentEvent ae, AgentElementType agentElementType) {

		String nameOfElement = getNameOfAgentElement(ae, agentElementType);
		// get all the DCM Realizations that have the current AgentEvent as initiator for a PUBLISH-Event
		for (String dmlRealizationName : agentEventDCMRealizationMappings.get(agentElementType.toString() + "::" + nameOfElement)) {
			// Check whether role is active.
			HashMap<String, Object> parameterDataMappings = publishWhenApplicable(dmlRealizationName + "::" + nameOfElement, ae, agentElementType);
			if (parameterDataMappings != null) {
				publishEvent(ae.getValue(), parameterDataMappings, dmlRealizationName);
			} else {
				System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
			}
		}
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
	private void addValueToMap(Map<String, ArrayList<String>> hashMap, String key, String value) {
		if (hashMap.get(key) == null) {
			ArrayList<String> newList = new ArrayList<String>();
			newList.add(value);
			hashMap.put(key, newList);
		} else {
			hashMap.get(key).add(value);
		}
	}
}
