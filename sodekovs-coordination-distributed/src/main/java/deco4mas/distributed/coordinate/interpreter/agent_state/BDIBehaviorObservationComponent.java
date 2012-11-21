/**
 * 
 */
package deco4mas.distributed.coordinate.interpreter.agent_state;

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
import jadex.bdi.runtime.impl.flyweights.InternalEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanbaseFlyweight;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.rules.state.IOAVState;

import java.util.HashMap;
import java.util.Iterator;

import deco.distributed.lang.dynamics.AgentElementType;
import deco.distributed.lang.dynamics.MASDynamics;
import deco.distributed.lang.dynamics.mechanism.AgentElement;
import deco4mas.distributed.coordinate.environment.CoordinationSpaceObject;
import deco4mas.distributed.coordinate.interpreter.coordination_information.CheckRole;
import deco4mas.distributed.helper.Constants;
import deco4mas.distributed.mechanism.CoordinationInfo;

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

				// TODO: Distinguish between beliefSet removed, changed and added: Currently only "added" is supported.
				public void factRemoved(AgentEvent ae) {
					// checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEFSET);
				}

				// TODO: Distinguish between beliefSet removed, changed and added: Currently only "added" is supported.
				public void factChanged(AgentEvent ae) {
					// checkAndPublishIfApplicable(ae, AgentElementType.BDI_BELIEFSET);
				}
			});
		} else {
			throw new RuntimeException("No such beliefset: " + scope[0] + " in " + scope[1]);
		}
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
			base.addGoalListener(agentElement.getElement_id(), new IGoalListener() {
				// TODO Im Moment wird nur auf das Goal-Added-Event reagiert
				public void goalAdded(AgentEvent ae) {
					checkAndPublishIfApplicable(ae, AgentElementType.BDI_GOAL);
				}

				public void goalFinished(AgentEvent ae) {
					// checkAndPublishIfApplicable(ae, AgentElementType.BDI_GOAL);
				}
			});
		} else {
			throw new RuntimeException("No such goal event: " + scope[0] + " in " + scope[1]);
		}
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
					// System.out.println("BDIBehObsComp# Activated planAdded listener.");
					// ae.getSource().
					checkAndPublishIfApplicable(ae, AgentElementType.BDI_PLAN);
				}

				// TODO: Distinguish between plan finish and added in the MasDynamics. Currently only "plan added" is supported.
				public void planFinished(AgentEvent ae)

				{
					// System.out.println("BDIBehObsComp# Activated planFinished listener.");
					// checkAndPublishIfApplicable(ae, AgentElementType.BDI_PLAN);
				}
			});
		} else {
			throw new RuntimeException("No such Plan event: " + scope[0] + " in " + scope[1]);
		}

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
					// InternalEventFlyweight ev = (InternalEventFlyweight) ae.getSource();
					// Object ob = (ISpaceObject) ev.getParameter("latest_analyzed_target").getValue();

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
		return CheckRole.checkForPublish(roleDefinitionsForPublish.get(Constants.PUBLISH + "::" + key + "::" + agentElementType.toString()),
				parameterAndDataMappings.get(Constants.PUBLISH + "::" + key + "::" + agentElementType.toString()), ae, bia);
	}

	/**
	 * Retrieves the name of the agentElement which is part of the "ae" String. The name is used as a key for the agentEventDCMRealizationMappings.
	 * 
	 * @return the name of the AgentElement
	 */
	private String getNameOfAgentElement(AgentEvent ae, AgentElementType agentElementType) {
		if (agentElementType != null) {
			return ae.getSource().getModelElement().getName();
		}
		return null;
	}

	/**
	 * Check for each received event whether the role definition is active for ALL DCM that are interested in this event (i.e. that have registered an listener) and dispatch event to medium if role
	 * definition is satisfied.
	 */
	private void checkAndPublishIfApplicable(final AgentEvent ae, final AgentElementType agentElementType) {
		extAccess.scheduleStep(new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				IBDIInternalAccess bia = (IBDIInternalAccess) ia;
				String nameOfElement = getNameOfAgentElement(ae, agentElementType);
				// get all the DCM Realizations that have the current AgentEvent as initiator for a PUBLISH-Event
				for (String dmlRealizationName : agentEventDCMRealizationMappings.get(agentElementType.toString() + "::" + nameOfElement)) {
					// Check whether role is active.
					HashMap<String, Object> parameterDataMappings = publishWhenApplicable(dmlRealizationName + "::" + nameOfElement, ae, agentElementType, bia);
					if (parameterDataMappings != null) {
						// publishEvent(ae.getValue(), parameterDataMappings, nameOfElement, agentElementType, dmlRealizationName, bia);
						publishEvent(getValue(ae, agentElementType,parameterDataMappings), parameterDataMappings, nameOfElement, agentElementType, dmlRealizationName, bia);
					} else {
						System.out.println("#BDIBehaviorObservationComponent# Role inactive. Event not published to medium or direct publish.");
					}
				}
				return IFuture.DONE;
			}
		});
	}

	private Object getValue(final AgentEvent ae, final AgentElementType agentElementType, final HashMap<String, Object> parameterDataMappings) {
		Object value = null;

		if (agentElementType.equals(AgentElementType.BDI_BELIEF)) {
			value = ae.getValue();
		} else if (agentElementType.equals(AgentElementType.BDI_BELIEFSET)) {
			value = ae.getValue();
		} else if (agentElementType.equals(AgentElementType.BDI_GOAL)) {
			//TODO: Not tested yet!!!
			value = ae.getValue();
		} else if (agentElementType.equals(AgentElementType.BDI_PLAN)) {
 			//HACK: Gets only the "first" parameter that is used as "value". Is not working for multiple parameters.
 			Iterator<String> it  = parameterDataMappings.keySet().iterator();
 			String key = it.next();
			value = parameterDataMappings.get(key);
		}else if(agentElementType.equals(AgentElementType.INTERNAL_EVENT)){
// 			InternalEventFlyweight event   =  (InternalEventFlyweight) ae.getSource();
 			//HACK: Gets only the "first" parameter that is used as "value". Is not working for multiple parameters.
 			Iterator<String> it  = parameterDataMappings.keySet().iterator();
 			String key = it.next();
			value = parameterDataMappings.get(key); 			
 		}
		return value;
	}
}
