/**
 * 
 */
package deco4mas.coordinate.interpreter.agent_state;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IBeliefListener;
import jadex.bdi.runtime.IBeliefSetListener;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IEventbase;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.IGoalbase;
import jadex.bdi.runtime.IInternalEventListener;
import jadex.bdi.runtime.IPlanListener;
import jadex.bdi.runtime.IPlanbase;
import jadex.bdi.runtime.impl.flyweights.BeliefbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.EventbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.ExternalAccessFlyweight;
import jadex.bdi.runtime.impl.flyweights.GoalbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanbaseFlyweight;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.rules.state.IOAVState;

import java.util.HashMap;

import deco.lang.dynamics.AgentElementType;
import deco.lang.dynamics.MASDynamics;
import deco.lang.dynamics.mechanism.AgentElement;
import deco4mas.coordinate.environment.CoordinationSpaceObject;
import deco4mas.coordinate.interpreter.coordination_information.CheckRole;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInfo;

/**
 * @author Ante Vilenica This component is called on Agent init and observes the agent. If an event occurs that is relevant for the coordination this event is dispatched to the
 *         "Coordination Event Publication".
 */
public class BDIBehaviorObservationComponent extends BehaviorObservationComponent {

	/**
	 * @param exta
	 *            The external access to the observed agent.
	 * @param masDynamics
	 *            the representation of the MASDynamics language
	 */
	public BDIBehaviorObservationComponent(IBDIExternalAccess exta, MASDynamics masDynamics) {
		super(exta, masDynamics);
	}

	/**
	 * Publish/Dispatch the occurred event to the "Coordination Event Publication".
	 * 
	 * @param dmlRealizationName2
	 * @param agentElementType
	 */
	private void publishEvent(Object value, HashMap<String, Object> parameterDataMappings, String agentElementName, AgentElementType agentElementType, String dmlRealizationName, IBDIInternalAccess bia) {
		CoordinationInfo coordInfo = createCoordinationInfo(value, parameterDataMappings, agentElementName, agentElementType, dmlRealizationName);
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, "BDI");

		IEnvironmentSpace space = (IEnvironmentSpace) bia.getBeliefbase().getBelief("env").getFact();
		eventPublication.publishEvent(coordInfo, space);

	}

	public void initBeliefListener(AgentElement agentElement, String mechanismRealizationId) {
		// System.out.println("#BDIBehaviorObservationComponent# init listener for belief: "
		// + agentElement.getElement_id());
		addValueToMap(agentEventDCMRealizationMappings, AgentElementType.BDI_BELIEF.toString() + "::" + agentElement.getElement_id(), mechanismRealizationId);

		ExternalAccessFlyweight extaFly = (ExternalAccessFlyweight) extAccess;
		IOAVState state = extaFly.getState();
		Object[] scope = AgentRules.resolveCapability(agentElement.getElement_id(), OAVBDIMetaModel.belief_type, extaFly.getScope(), state);
		Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if (state.containsKey(mscope, OAVBDIMetaModel.capability_has_beliefs, scope[0])) {
			IBeliefbase base = BeliefbaseFlyweight.getBeliefbaseFlyweight(state, scope[1]);
			base.getBelief(agentElement.getElement_id()).addBeliefListener(new IBeliefListener() {
				public void beliefChanged(AgentEvent ae) {
					checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEF);
				}
			});
		} else {
			throw new RuntimeException("No such belief: " + scope[0] + " in " + scope[1]);
		}

		// exta.getBeliefbase().getBelief(agentElement.getElement_id()).addBeliefListener(new
		// IBeliefListener()
		// {
		// public void beliefChanged(AgentEvent ae)
		// {
		// // getExternalAccess().getLogger().info("belief changed: "+ae);
		// //
		// System.out.println("#BDIBehaviorObservationComponent#belief changed: "
		// // + ae.getValue() + " - " + ae.getSource());
		// //
		// getExternalAccess().getBeliefbase().getBelief("bel").removeBeliefListener(this);
		//
		// // ------------------------- OLD
		// // ----------------------------------
		// //
		// // String nameOfElement = getNameOfAgentElement(ae,
		// // AgentElementType.BDI_BELIEF);
		// // //get all the DCM Realizations that have the current
		// // AgentEvent as initiator for a PUBLISH-Event
		// // for(String dmlRealizationName:
		// //
		// agentEventDCMRealizationMappings.get(AgentElementType.BDI_BELIEF.toString()
		// // + "::" + nameOfElement)){
		// // //Check whether role is active.
		// // HashMap<String, Object> parameterDataMappings =
		// // publishWhenApplicable(dmlRealizationName + "::" +
		// // nameOfElement , ae, AgentElementType.BDI_BELIEF);
		// // if (parameterDataMappings != null) {
		// // // publishEvent(ae, AgentElementType.BDI_BELIEF,
		// // CoordinationInfo.AGENT_ELEMENT_NAME,
		// // getToAgents(ae.getSource().toString(),
		// // AgentElementType.BDI_BELIEF), parameterDataMappings);
		// // publishEvent(ae.getValue(), parameterDataMappings,
		// // dmlRealizationName);
		// // } else {
		// //
		// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
		// // }
		// //
		// // // behObserver.getRoleDefinitions().put(perceptType + "::" +
		// // dci.getDml().getRealization() + "::" + ae.getElement_id() +
		// // "::" + ae.getAgentElementType(), dci.getRef());
		// // }
		// //
		// // // HashMap<String, Object> parameterDataMappings =
		// // publishWhenApplicable(ae, AgentElementType.BDI_BELIEF);
		// // // if (parameterDataMappings != null) {
		// // // publishEvent(ae, AgentElementType.BDI_BELIEF,
		// // CoordinationInfo.AGENT_ELEMENT_NAME,
		// // getToAgents(ae.getSource().toString(),
		// // AgentElementType.BDI_BELIEF), parameterDataMappings);
		// // // } else {
		// // //
		// //
		// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
		// // // }
		// //
		// //
		// // ------------------------- OLD
		// // ----------------------------------
		//
		// checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEF);
		// }
		// });
	}

	public void initBeliefSetListener(AgentElement agentElement, String mechanismRealizationId) {
		// System.out.println("#BDIBehaviorObservationComponent# init listener for beliefSet: "
		// + agentElement.getElement_id());
		addValueToMap(agentEventDCMRealizationMappings, AgentElementType.BDI_BELIEFSET.toString() + "::" + agentElement.getElement_id(), mechanismRealizationId);

		ExternalAccessFlyweight extaFly = (ExternalAccessFlyweight) extAccess;
		IOAVState state = extaFly.getState();
		Object[] scope = AgentRules.resolveCapability(agentElement.getElement_id(), OAVBDIMetaModel.beliefset_type, extaFly.getScope(), state);
		Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if (state.containsKey(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0])) {
			IBeliefbase base = BeliefbaseFlyweight.getBeliefbaseFlyweight(state, scope[1]);
			base.getBeliefSet(agentElement.getElement_id()).addBeliefSetListener(new IBeliefSetListener() {
				public void factAdded(AgentEvent ae) {
					checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEFSET);
				}

				public void factRemoved(AgentEvent ae) {
					checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEFSET);
				}

				public void factChanged(AgentEvent ae) {
					checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEFSET);
				}
			});
		} else {
			throw new RuntimeException("No such beliefset: " + scope[0] + " in " + scope[1]);
		}
		// try {
		// exta.getBeliefbase().getBeliefSet(agentElement.getElement_id()).addBeliefSetListener(new
		// IBeliefSetListener()
		// {
		// public void factAdded(AgentEvent ae)
		// {
		// //
		// System.out.println("#BDIBehaviorObservationComponent#beliefSet fact added: "
		// // + ae.getValue() + " - " + ae.getSource());
		//
		// // String nameOfElement = getNameOfAgentElement(ae,
		// // AgentElementType.BDI_BELIEFSET);
		// // //get all the DCM Realizations that have the current
		// // AgentEvent as initiator for a PUBLISH-Event
		// // for(String dmlRealizationName:
		// //
		// agentEventDCMRealizationMappings.get(AgentElementType.BDI_BELIEFSET.toString()
		// // + "::" + nameOfElement)){
		// // //Check whether role is active.
		// // HashMap<String, Object> parameterDataMappings =
		// // publishWhenApplicable(dmlRealizationName + "::" +
		// // nameOfElement , ae, AgentElementType.BDI_BELIEFSET);
		// // if (parameterDataMappings != null) {
		// // // publishEvent(ae, AgentElementType.BDI_BELIEF,
		// // CoordinationInfo.AGENT_ELEMENT_NAME,
		// // getToAgents(ae.getSource().toString(),
		// // AgentElementType.BDI_BELIEF), parameterDataMappings);
		// // publishEvent(ae.getValue(), parameterDataMappings,
		// // dmlRealizationName);
		// // } else {
		// //
		// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
		// // }
		// // // behObserver.getRoleDefinitions().put(perceptType + "::" +
		// // dci.getDml().getRealization() + "::" + ae.getElement_id() +
		// // "::" + ae.getAgentElementType(), dci.getRef());
		// // }
		// checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEFSET);
		// }
		//
		// public void factRemoved(AgentEvent ae)
		// {
		// //
		// System.out.println("#BDIBehaviorObservationComponent#beliefSet fact removed: "
		// // + ae.getValue() + " - " + ae.getSource());
		// // HashMap<String, Object> parameterDataMappings =
		// // publishWhenApplicable(ae, AgentElementType.BDI_BELIEFSET);
		// // if (parameterDataMappings != null) {
		// // publishEvent(ae, AgentElementType.BDI_BELIEFSET,
		// // CoordinationInfo.AGENT_ELEMENT_NAME,
		// // getToAgents(ae.getSource().toString(),
		// // AgentElementType.BDI_BELIEFSET),parameterDataMappings);
		// // } else {
		// //
		// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
		// // }
		// checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEFSET);
		// }
		//
		// public void factChanged(AgentEvent ae)
		// {
		// //
		// System.out.println("#BDIBehaviorObservationComponent#beliefSet fact changed: "
		// // + ae.getValue() + " - " + ae.getSource());
		// // HashMap<String, Object> parameterDataMappings =
		// // publishWhenApplicable(ae, AgentElementType.BDI_BELIEFSET);
		// // if (parameterDataMappings != null) {
		// // publishEvent(ae, AgentElementType.BDI_BELIEFSET,
		// // CoordinationInfo.AGENT_ELEMENT_NAME,
		// // getToAgents(ae.getSource().toString(),
		// // AgentElementType.BDI_BELIEFSET), parameterDataMappings);
		// // } else {
		// //
		// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
		// // }
		// checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEFSET);
		// }
		// });
		// } catch (Exception e) {
		// System.out.println("#BDIBehObser#" + e);
		// }
	}

	public void initGoalListener(AgentElement agentElement, String mechanismRealizationId) {
		// System.out.println("#BDIBehaviorObservationComponent# init listener for goal: "
		// + agentElement.getElement_id());
		// agentEventDCMRealizationMappings.put(AgentElementType.BDI_GOAL.toString()
		// + "::" + agentElement.getElement_id(), toAgents);
		addValueToMap(agentEventDCMRealizationMappings, AgentElementType.BDI_GOAL.toString() + "::" + agentElement.getElement_id(), mechanismRealizationId);

		ExternalAccessFlyweight extaFly = (ExternalAccessFlyweight) extAccess;
		IOAVState state = extaFly.getState();
		Object[] scope = AgentRules.resolveCapability(agentElement.getElement_id(), OAVBDIMetaModel.goal_type, extaFly.getScope(), state);
		Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if (state.containsKey(mscope, OAVBDIMetaModel.capability_has_goals, scope[0])) {
			IGoalbase base = GoalbaseFlyweight.getGoalbaseFlyweight(state, scope[1]);
			// TODO: Macht es Sinn dass eine Nachricht in beiden Fällen gesendet wird (goal added und goal finished)?
			base.addGoalListener(agentElement.getElement_id(), new IGoalListener() {
				public void goalAdded(AgentEvent ae) {
					checkAndPublishIfApplicable(ae, AgentElementType.BDI_GOAL);
				}

				public void goalFinished(AgentEvent ae) {
					checkAndPublishIfApplicable(ae, AgentElementType.BDI_GOAL);
				}
			});
		} else {
			throw new RuntimeException("No such goal event: " + scope[0] + " in " + scope[1]);
		}

		// exta.getGoalbase().addGoalListener(agentElement.getElement_id(), new
		// IGoalListener()
		// {
		// public void goalAdded(AgentEvent ae)
		// {
		// // System.out.println("#BDIBehaviorObservationComponent#goal added: "
		// // + ae.getValue() + " - " + ae.getSource());
		// // HashMap<String, Object> parameterDataMappings =
		// // publishWhenApplicable(ae, AgentElementType.BDI_GOAL);
		// // if (parameterDataMappings != null) {
		// // publishEvent(ae, AgentElementType.BDI_GOAL,
		// // CoordinationInfo.AGENT_ELEMENT_NAME,
		// // getToAgents(ae.getSource().toString(),
		// // AgentElementType.BDI_GOAL), parameterDataMappings);
		// // } else {
		// //
		// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
		// // }
		// checkAndPublishIfApplicable(ae, AgentElementType.BDI_GOAL);
		// }
		//
		// public void goalFinished(AgentEvent ae)
		// {
		// //
		// System.out.println("#BDIBehaviorObservationComponent#goal finished: "
		// // + ae.getValue() + " - " + ae.getSource());
		// // HashMap<String, Object> parameterDataMappings =
		// // publishWhenApplicable(ae, AgentElementType.BDI_GOAL);
		// // if (parameterDataMappings != null) {
		// // publishEvent(ae, AgentElementType.BDI_GOAL,
		// // CoordinationInfo.AGENT_ELEMENT_NAME,
		// // getToAgents(ae.getSource().toString(),
		// // AgentElementType.BDI_GOAL), parameterDataMappings);
		// // } else {
		// //
		// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
		// // }
		// checkAndPublishIfApplicable(ae, AgentElementType.BDI_GOAL);
		// }
		// });
	}

	public void initPlanListener(AgentElement agentElement, String mechanismRealizationId) {
		// System.out.println("#BDIBehaviorObservationComponent# init listener for plan: "
		// + agentElement.getElement_id());
		// agentEventDCMRealizationMappings.put(AgentElementType.BDI_PLAN.toString()
		// + "::" + agentElement.getElement_id(), toAgents);
		addValueToMap(agentEventDCMRealizationMappings, AgentElementType.BDI_PLAN.toString() + "::" + agentElement.getElement_id(), mechanismRealizationId);

		ExternalAccessFlyweight extaFly = (ExternalAccessFlyweight) extAccess;
		IOAVState state = extaFly.getState();
		Object[] scope = AgentRules.resolveCapability(agentElement.getElement_id(), OAVBDIMetaModel.plan_type, extaFly.getScope(), state);
		Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if (state.containsKey(mscope, OAVBDIMetaModel.capability_has_plans, scope[0])) {
			IPlanbase base = PlanbaseFlyweight.getPlanbaseFlyweight(state, scope[1]);
			base.addPlanListener(agentElement.getElement_id(), new IPlanListener() {

				public void planAdded(AgentEvent ae) {
					checkAndPublishIfApplicable(ae, AgentElementType.BDI_PLAN);
				}

				public void planFinished(AgentEvent ae)

				{
					checkAndPublishIfApplicable(ae, AgentElementType.BDI_PLAN);
				}
			});
		} else {
			throw new RuntimeException("No such Plan event: " + scope[0] + " in " + scope[1]);
		}

		// exta.getPlanbase().addPlanListener(agentElement.getElement_id(), new
		// IPlanListener()
		// {
		//
		// public void planAdded(AgentEvent ae)
		// {
		// // System.out.println("#BDIBehaviorObservationComponent#plan added: "
		// // + ae.getValue() + " - " + ae.getSource());
		// // HashMap<String, Object> parameterDataMappings =
		// // publishWhenApplicable(ae, AgentElementType.BDI_PLAN);
		// // if (parameterDataMappings != null) {
		// // publishEvent(ae, AgentElementType.BDI_PLAN,
		// // CoordinationInfo.AGENT_ELEMENT_NAME,
		// // getToAgents(ae.getSource().toString(),
		// // AgentElementType.BDI_PLAN), parameterDataMappings);
		// // } else {
		// //
		// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
		// // }
		// checkAndPublishIfApplicable(ae, AgentElementType.BDI_PLAN);
		// }
		//
		// public void planFinished(AgentEvent ae)
		//
		// {
		// //
		// System.out.println("#BDIBehaviorObservationComponent#plan finished: "
		// // + ae.getValue() + " - " + ae.getSource());
		// // HashMap<String, Object> parameterDataMappings =
		// // publishWhenApplicable(ae, AgentElementType.BDI_PLAN);
		// // if (parameterDataMappings != null) {
		// // publishEvent(ae, AgentElementType.BDI_PLAN,
		// // CoordinationInfo.AGENT_ELEMENT_NAME,
		// // getToAgents(ae.getSource().toString(),
		// // AgentElementType.BDI_PLAN), parameterDataMappings);
		// // } else {
		// //
		// System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not not published to medium.");
		// // }
		// checkAndPublishIfApplicable(ae, AgentElementType.BDI_PLAN);
		// }
		// });
	}

	public void initInternalEventListener(final AgentElement agentElement, String mechanismRealizationId) {

		addValueToMap(agentEventDCMRealizationMappings, AgentElementType.INTERNAL_EVENT.toString() + "::" + agentElement.getElement_id(), mechanismRealizationId);

		ExternalAccessFlyweight extaFly = (ExternalAccessFlyweight) extAccess;
		IOAVState state = extaFly.getState();
		Object[] scope = AgentRules.resolveCapability(agentElement.getElement_id(), OAVBDIMetaModel.internalevent_type, extaFly.getScope(), state);
		Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if (state.containsKey(mscope, OAVBDIMetaModel.capability_has_internalevents, scope[0])) {
			IEventbase base = EventbaseFlyweight.getEventbaseFlyweight(state, scope[1]);
			base.addInternalEventListener(agentElement.getElement_id(), new IInternalEventListener() {

				public void internalEventOccurred(AgentEvent ae) {
					checkAndPublishIfApplicable(ae, AgentElementType.INTERNAL_EVENT);
				}
			});
		} else {
			throw new RuntimeException("No such internal event: " + scope[0] + " in " + scope[1]);
		}
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
	private HashMap<String, Object> publishWhenApplicable(String key, AgentEvent ae, AgentElementType agentElementType, IBDIInternalAccess bia) {

		// behObserver.getRoleDefinitions().put(ae.getElement_id() + "::" +
		// ae.getAgentElementType(), erList);
		// ArrayList<ElementReference> roles;
		// String key = null;
		// if (agentElementType.equals(AgentElementType.BDI_BELIEFSET)) {
		// key =
		// ae.getSource().toString().substring(ae.getSource().toString().indexOf("BeliefSet(")
		// + 10, ae.getSource().toString().indexOf("-beliefset_"));
		// } else if (agentElementType.equals(AgentElementType.BDI_BELIEF)) {
		// key =
		// ae.getSource().toString().substring(ae.getSource().toString().indexOf("Belief(")
		// + 7, ae.getSource().toString().indexOf("-belief_"));
		// } else if (agentElementType.equals(AgentElementType.BDI_GOAL)) {
		// key =
		// ae.getSource().toString().substring(ae.getSource().toString().indexOf("Goal(")
		// + 5, ae.getSource().toString().indexOf("-goal_"));
		// } else if (agentElementType.equals(AgentElementType.BDI_PLAN)) {
		// key =
		// ae.getSource().toString().substring(ae.getSource().toString().indexOf("Plan(")
		// + 5, ae.getSource().toString().indexOf("-plan_"));
		// }

		return CheckRole.checkForPublish(roleDefinitionsForPublish.get(Constants.PUBLISH + "::" + key + "::" + agentElementType.toString()),
				parameterAndDataMappings.get(Constants.PUBLISH + "::" + key + "::" + agentElementType.toString()), ae, bia);
	}

	// /**
	// * Retrieves the right toAgents for an AgentEvent. Uses the HashMap where
	// the mappings have been stored.
	// *
	// * @return the Collection with the toAgents.
	// */
	// private Collection getToAgents(String source, AgentElementType
	// agentElementType) {
	//
	// if (agentElementType.equals(AgentElementType.BDI_BELIEFSET)) {
	// String key = source.substring(source.indexOf("BeliefSet(") + 10,
	// source.indexOf("-beliefset_"));
	// // System.out.println("EEEEEEEEEEEEEEEEEEEE: " +
	// AgentElementType.BDI_BELIEFSET.toString() + "::" + key);
	// return
	// agentElementCollection2HashMapCollection(agentEventDCMRealizationMappings.get(AgentElementType.BDI_BELIEFSET.toString()
	// + "::" + key));
	// } else if (agentElementType.equals(AgentElementType.BDI_BELIEF)) {
	// String key = source.substring(source.indexOf("Belief(") + 7,
	// source.indexOf("-belief_"));
	// // System.out.println("EEEEEEEEEEEEEEEEEEEE: " +
	// AgentElementType.BDI_BELIEF.toString() + "::" + key);
	// return
	// agentElementCollection2HashMapCollection(agentEventDCMRealizationMappings.get(AgentElementType.BDI_BELIEF.toString()
	// + "::" + key));
	// } else if (agentElementType.equals(AgentElementType.BDI_GOAL)) {
	// String key = source.substring(source.indexOf("Goal(") + 5,
	// source.indexOf("-goal_"));
	// // System.out.println("EEEEEEEEEEEEEEEEEEEE: " +
	// AgentElementType.BDI_GOAL.toString() + "::" + key);
	// return
	// agentElementCollection2HashMapCollection(agentEventDCMRealizationMappings.get(AgentElementType.BDI_GOAL.toString()
	// + "::" + key));
	// } else if (agentElementType.equals(AgentElementType.BDI_PLAN)) {
	// String key = source.substring(source.indexOf("Plan(") + 5,
	// source.indexOf("-plan_"));
	// // System.out.println("EEEEEEEEEEEEEEEEEEEE: " +
	// AgentElementType.BDI_Plan.toString() + "::" + key);
	// return
	// agentElementCollection2HashMapCollection(agentEventDCMRealizationMappings.get(AgentElementType.BDI_PLAN.toString()
	// + "::" + key));
	// }
	// return null;
	// }

	/**
	 * Retrieves the name of the agentElement which is part of the "ae" String. The name is used as a key for the agentEventDCMRealizationMappings.
	 * 
	 * @return the name of the AgentElement
	 */
	private String getNameOfAgentElement(AgentEvent ae, AgentElementType agentElementType) {

		// if (agentElementType.equals(AgentElementType.BDI_BELIEFSET))
		// {
		// return
		// ae.getSource().toString().substring(ae.getSource().toString().indexOf("BeliefSet(")
		// + 10,
		// ae.getSource().toString().indexOf("-beliefset_"));
		// } else if (agentElementType.equals(AgentElementType.BDI_BELIEF))
		// {
		// return
		// ae.getSource().toString().substring(ae.getSource().toString().indexOf("Belief(")
		// + 7,
		// ae.getSource().toString().indexOf("-belief_"));
		// } else if (agentElementType.equals(AgentElementType.BDI_GOAL))
		// {
		// return
		// ae.getSource().toString().substring(ae.getSource().toString().indexOf("Goal(")
		// + 5,
		// ae.getSource().toString().indexOf("-goal_"));
		// } else if (agentElementType.equals(AgentElementType.BDI_PLAN))
		// {
		// return
		// ae.getSource().toString().substring(ae.getSource().toString().indexOf("Plan(")
		// + 5,
		// ae.getSource().toString().indexOf("-plan_"));
		// } else if (agentElementType.equals(AgentElementType.INTERNAL_EVENT))
		// {
		// return
		// ae.getSource().toString().substring(ae.getSource().toString().indexOf("InternalEvent(")
		// + 14,
		// ae.getSource().toString().indexOf("-internalevent_"));
		// }

		if (agentElementType != null) {
			return ae.getSource().getModelElement().getName();
		}
		return null;
	}

	// /**
	// * Converts a Collection of AgentElement-Objects into a Collection of
	// HashMaps that contain the same information. Needed because
	// AgentElement-Object are not serializable and can therefore not be
	// processed by the TSpaceServer.
	// *
	// * @param agentElement
	// * @return CoordinationInfo
	// */
	//
	// private ArrayList<HashMap<String, Object>>
	// agentElementCollection2HashMapCollection(Collection agentElements) {
	//
	// ArrayList<HashMap<String, Object>> res = new ArrayList<HashMap<String,
	// Object>>();
	//
	// // convert AgentElement-Objects into a HashMap, i.e. "copy" the values.
	// for (int i = 0; i < agentElements.size(); i++) {
	// AgentElement agentElement = (AgentElement) ((ArrayList)
	// agentElements).get(i);
	// HashMap<String, Object> values = new HashMap<String, Object>();
	// values.put(CoordinationInfo.AGENT_TYPE, agentElement.getAgent_id());
	// values.put(CoordinationInfo.AGENT_ELEMENT_TYPE,
	// agentElement.getAgentElementType());
	// values.put(CoordinationInfo.AGENT_ELEMENT_NAME,
	// agentElement.getElement_id());
	//
	// res.add(values);
	// }
	// return res;
	// }

	// /**
	// * @param roleDefinitions
	// * the roleDefinitions to set
	// */
	// public void setRoleDefinitions(Map<String, ArrayList<ElementReference>>
	// roleDefinitions) {
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
	// public void setDecentralCoordInfoMapping(DecentralCoordinationInformation
	// decentralCoordInfoMapping) {
	// this.decentralCoordInfoMapping = decentralCoordInfoMapping;
	// }

	/**
	 * Check for each received event whether the role definition is active for ALL DCM that are interested in this event (i.e. that have registered an listener) and dispatch event to medium if role
	 * definition is satisfied.
	 */
	private void checkAndPublishIfApplicable(final AgentEvent ae, final AgentElementType agentElementType) {
		extAccess.scheduleStep(new IComponentStep() {

			@Override
			public Object execute(IInternalAccess ia) {
				IBDIInternalAccess bia = (IBDIInternalAccess) ia;
				String nameOfElement = getNameOfAgentElement(ae, agentElementType);
				// get all the DCM Realizations that have the current AgentEvent
				// as
				// initiator for a PUBLISH-Event
				for (String dmlRealizationName : agentEventDCMRealizationMappings.get(agentElementType.toString() + "::" + nameOfElement)) {
					// Check whether role is active.
					HashMap<String, Object> parameterDataMappings = publishWhenApplicable(dmlRealizationName + "::" + nameOfElement, ae, agentElementType, bia);
					if (parameterDataMappings != null) {
						publishEvent(ae.getValue(), parameterDataMappings, nameOfElement, agentElementType, dmlRealizationName, bia);
					} else {
						System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not published to medium or direct publish.");
					}
				}
				return null;
			}
		});
	}
}
