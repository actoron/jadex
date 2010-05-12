/**
 * 
 */
package deco4mas.coordinate.interpreter.coordination_information;

import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.impl.BeliefFlyweight;
import jadex.bdi.runtime.impl.BeliefSetFlyweight;
import jadex.bdi.runtime.impl.ElementFlyweight;
import jadex.bdi.runtime.impl.GoalFlyweight;
import jadex.bdi.runtime.impl.InternalEventFlyweight;
import jadex.bdi.runtime.impl.PlanFlyweight;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import deco.lang.dynamics.AgentElementType;
import deco.lang.dynamics.mechanism.AgentElement;
import deco.lang.dynamics.properties.AgentReference;
import deco.lang.dynamics.properties.ElementReference;
import deco4mas.annotation.agent.DataMapping;
import deco4mas.annotation.agent.ParameterMapping;
import deco4mas.helper.Constants;
import deco4mas.helper.SJavaParser;

/**
 * Checks, whether the agent is within the role, which is necessary to publish/perceive the specified element(i.e. goal, belief, beliefset)
 * 
 * @author Ante Vilenica
 * 
 */
public class CheckRole {

	// /**
	// * Check, whether role is active for the specified element.
	// *
	// * @param exta
	// * @param nameOfElement
	// * @param agentElementType
	// * @return
	// */
	// public static boolean doCheck(IExternalAccess exta, String agentType) {
	// Map<String, Object> decom4MasMap = (Map<String, Object>) exta.getBeliefbase().getBelief(Constants.DECO4MAS_BELIEF_NAME).getFact();
	// HashMap<String, ArrayList<ElementReference>> roleMappings = ((HashMap<String, ArrayList<ElementReference>>) decom4MasMap.get(Constants.ROLE_DEFINITIONS_FOR_PUBLISH));
	// return check(roleMappings.get(Constants.PERCEIVE + "::" + agentType), exta);
	// }

	// /**
	// * Check, whether role is active for the specified element.
	// *
	// * @param roles
	// * @param exta
	// * @return
	// */
	// public static boolean check(ArrayList<ElementReference> roles, IExternalAccess exta) {
	//
	// if (roles != null) {
	// // Check whether the role is active...
	// for (ElementReference elementRef : roles) {
	// // dci.getRef().getElements().get(2).getAgent_element_type() --> INTERNAL_EVENT
	// // dci.getRef().getElements().get(2).getElement_id(); --> action
	// if (elementRef.getAgent_element_type().equals(AgentElementType.BDI_BELIEFSET)) {
	// System.out.println("#CheckRole# Trying to check role for belief_set. TODO!");
	// return true;
	// } else if (elementRef.getAgent_element_type().equals(AgentElementType.BDI_BELIEF)) {
	// System.out.println("#CheckRole# Trying to check role for belief. TODO!");
	// return true;
	// } else if (elementRef.getAgent_element_type().equals(AgentElementType.BDI_GOAL)) {
	// System.out.println("#CheckRole# Trying to check role for goal...");
	// try {
	// IGoal[] goals = exta.getGoalbase().getGoals(elementRef.getElement_id());
	// if (goals != null && goals.length > 0) {
	// return true;
	// }
	// } catch (RuntimeException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	// return false;
	// }

	/**
	 * Used to check the role when coordination events should be "PUBLISHED" (PUBLICATION).
	 * 
	 * Check, whether the role is active for the specified element. Maps the parameter and data associated with this role, if role is active.
	 * 
	 * @param agentReference
	 * @param exta
	 * @param agentEvent
	 * @param ae
	 * @return if return=null, then check_false, i.e. the role is not active.
	 */
	public static HashMap<String, Object> checkForPublish(AgentReference agentReference, AgentElement ae, AgentEvent agentEvent, IBDIExternalAccess exta) {

		HashMap<String, Object> parameters = null;

		if (checkCondition(agentReference, exta) && checkInhibitions(agentReference, exta)) {

			// Map<String, Object> decom4MasMap = (Map<String, Object>) exta.getBeliefbase().getBelief(Constants.DECO4MAS_BELIEF_NAME).getFact();
			// Map<String,AgentElement> paramDataMappings = (Map<String, AgentElement>) decom4MasMap.get(Constants.PARAMETER_DATA_MAPPING);

			// get parameter and data mappings
			if (agentEvent.getSource() instanceof PlanFlyweight) { // handle plan-events
				PlanFlyweight plan = (PlanFlyweight) agentEvent.getSource();

				parameters = new HashMap<String, Object>();
				for (ParameterMapping pm : ae.getParameter_mappings()) {
					parameters.put(pm.getRef(), plan.getParameter(pm.getLocalName()).getValue());
				}
				for (DataMapping dm : ae.getData_mappings()) { // check data mappings
					parameters.put(dm.getRef(), getMappedAgentData(dm, exta));
				}
				return parameters;

			} else if (agentEvent.getSource() instanceof GoalFlyweight) { // handle goal-events
				GoalFlyweight goal = (GoalFlyweight) agentEvent.getSource();

				parameters = new HashMap<String, Object>();
				for (ParameterMapping pm : ae.getParameter_mappings()) {
					parameters.put(pm.getRef(), goal.getParameter(pm.getLocalName()).getValue());
				}
				for (DataMapping dm : ae.getData_mappings()) { // check data mappings
					parameters.put(dm.getRef(), getMappedAgentData(dm, exta));
				}
				return parameters;

			} else if (agentEvent.getSource() instanceof InternalEventFlyweight) { // handle internal-events
				InternalEventFlyweight internalEvent = (InternalEventFlyweight) agentEvent.getSource();

				parameters = new HashMap<String, Object>();
				for (ParameterMapping pm : ae.getParameter_mappings()) {
					parameters.put(pm.getRef(), internalEvent.getParameter(pm.getLocalName()).getValue());
				}
				return parameters;

			} else if (agentEvent.getSource() instanceof BeliefFlyweight) { // handle belief-events
				BeliefFlyweight belief = (BeliefFlyweight) agentEvent.getSource();

				parameters = new HashMap<String, Object>();
				for (ParameterMapping pm : ae.getParameter_mappings()) {
					if (pm.getLocalName().equalsIgnoreCase(Constants.BELIEF_UPDATE_IDENTIFIER)) {
						parameters.put(pm.getRef(), belief.getFact()); // "content" matches to the content of the belief.
					}
				}
				for (DataMapping dm : ae.getData_mappings()) { // check data mappings
					parameters.put(dm.getRef(), getMappedAgentData(dm, exta));
				}
				return parameters;
			} else if (agentEvent.getSource() instanceof BeliefSetFlyweight) { // handle beliefset-events
				BeliefSetFlyweight b = (BeliefSetFlyweight) agentEvent.getSource();

				parameters = new HashMap<String, Object>();
				for (ParameterMapping pm : ae.getParameter_mappings()) {
					if (pm.getLocalName().equals(Constants.BELIEFSET_UPDATE_LAST_IDENTIFIER)) {
						Object[] facts = b.getFacts();
						parameters.put(pm.getRef(), facts[facts.length - 1]);
					}
					if (pm.getLocalName().equals(Constants.BELIEFSET_UPDATE_ALL_IDENTIFIER)) {
						Object[] facts = b.getFacts();
						parameters.put(pm.getRef(), facts);
					}
				}
				for (DataMapping dm : ae.getData_mappings()) { // check data mappings
					parameters.put(dm.getRef(), getMappedAgentData(dm, exta));
				}
				return parameters;
			}
		}
		return parameters;
	}

	/**
	 * Used to check the role when coordination events should be "PERCEIVED".
	 * 
	 * Check, whether the role is active for the specified element.
	 * 
	 * @param agentReference
	 * @param exta
	 */
	public static boolean checkForPerceive(AgentReference agentReference, IBDIExternalAccess exta) {

		return (checkCondition(agentReference, exta) && checkInhibitions(agentReference, exta));

	}

	/**
	 * * Check the conditions associated with the role
	 * 
	 * @param agentReference
	 * @param exta
	 * @return
	 */
	private static boolean checkCondition(AgentReference agentReference, IBDIExternalAccess exta) {
		// checking constraints:

		if (agentReference.hasConstraints()) { // checking condition
			// if (agentReference.getContraints().getCondition().getExpression() != null && agentReference.getContraints().getCondition().getExpression().length() > 0) {
			// IExpression exp = exta.getExpressionbase().createExpression(agentReference.getContraints().getCondition().getExpression(), null, null);
			// Object val = exp.getValue();
			// if ( val instanceof Boolean) {
			// Boolean bval = (Boolean) val;
			// if (!bval) { // next annotation if
			// System.out.println(agent.getAgentName() + ":");
			// System.out.println("\t Coordination stopped due to not valid condition:" + applicable_ca.getConditionName() + "/" + applicable_ca.getConditionExpression() + ":" + val );
			// break;
			// }
			// }
			// }

			// TODO: HACK! Implement the ExpressionChecker
			if (agentReference.getContraints().getCondition().getExpression() != null && agentReference.getContraints().getCondition().getExpression().length() > 0) {
				
				
//				Map<String, Object> decom4MasMap = (Map<String, Object>) exta.getBeliefbase().getBelief(Constants.DECO4MAS_BELIEF_NAME).getFact();
//				IOAVState state = (IOAVState) decom4MasMap.get(Constants.IOAV_STATE);
//				Object rCapability = decom4MasMap.get(Constants.R_CAPABILITY);
				
				
								
				IOAVState state = ((ElementFlyweight) exta).getState();
				Object rCapability = ((ElementFlyweight) exta).getScope();
				
				
				
				String expression = agentReference.getContraints().getCondition().getExpression();
				
				// Evaluate condition/expression
				OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rCapability);
				Object val = SJavaParser.evaluateExpression(expression, fetcher);

				if (val instanceof Boolean) {
					if ((Boolean) val) {
						return true;
					}
				} else {
					System.out.println("#CheckRole#" + exta.getAgentName() + ":");
					System.out.println("\t Could not evaluate rolue due invalid condition:" + expression);
				}
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * Check the inhibitions associated with the role
	 * 
	 * @param agentReference
	 * @param exta
	 * @return boolean
	 */
	private static boolean checkInhibitions(AgentReference agentReference, IBDIExternalAccess exta) {
		// check inhibitions:
		if (agentReference.hasConstraints()) { // inhibitions exist

			if (agentReference.getContraints().getInhibitions() != null) {

				for (ElementReference inhib : agentReference.getContraints().getInhibitions()) { // check each inhibition

					if (inhib.getAgent_element_type().toString().equalsIgnoreCase(AgentElementType.BDI_GOAL.toString())) {
						IGoal[] gs = exta.getGoalbase().getGoals(); // fetch goals:
						for (IGoal g : gs) {
							if (g.getType().equals(inhib.getElement_id())) {
								if (g.isActive()) { // TODO: check only active???
									return false;
								}
							}
						}
					}

					if (inhib.getAgent_element_type().toString().equalsIgnoreCase(AgentElementType.BDI_PLAN.toString())) {
						IPlan[] pl = exta.getPlanbase().getPlans(); // fetch plans:
						for (IPlan p : pl) {
							if (p.getType().equals(inhib.getElement_id())) {
								return false;
							}
						}
					}

					if (inhib.getAgent_element_type().toString().equalsIgnoreCase(AgentElementType.BDI_BELIEF.toString())) {
						String[] bs = exta.getBeliefbase().getBeliefNames(); // fetch beliefs:
						for (String b : bs) {
							if (b.equals(inhib.getElement_id())) {
								if (exta.getBeliefbase().getBelief(b).getFact() != null) {
									return false;
								}
							}
						}
					}

					if (inhib.getAgent_element_type().toString().equalsIgnoreCase(AgentElementType.BDI_BELIEFSET.toString())) {
						String[] bs = exta.getBeliefbase().getBeliefSetNames(); // fetch beliefSets:
						for (String b : bs) {
							if (b.equals(inhib.getElement_id())) {
								if (exta.getBeliefbase().getBeliefSet(b).getFacts().length > 0) {
									return false;
								}
							}
						}
					}
					//						
					// if (inhib.getElement_type().equalsIgnoreCase(AgentElementType.BDI_PLAN.toString())) {
					//							
					// PlanFlyweight[] ps = agent.getPlanbase().getPlans(); // fetch goals:
					// for (PlanFlyweight p : ps) {
					// if (p.getModelElement().getName().equals(inhib.getElement_name())) { //check type
					// // if (g.isActive() || g.isInProcess()) {
					// break;
					// // }
					// }
					// }
					// }
					//						
					// if (inhib.getElement_type().equalsIgnoreCase(AgentElementType.BDI_BELIEF.toString())) {
					//							
					// String[] bs = agent.getBeliefbase().getBeliefNames();
					// for (String b : bs) {
					// if (b.equals(inhib.getElement_name())) { //check type
					// IBelief belief = agent.getBeliefbase().getBelief(inhib.getElement_name());
					// if (belief.getFact() != null) {
					// break;
					// }
					// }
					// }
					// }
					//						
					// if (inhib.getElement_type().equalsIgnoreCase(AgentElementType.BDI_BELIEFSET.toString())) {
					//							
					// String[] bs = agent.getBeliefbase().getBeliefSetNames();
					// for (String b : bs) {
					// if (b.equals(inhib.getElement_name())) { //check type
					// IBeliefSet bliefSet = agent.getBeliefbase().getBeliefSet(inhib.getElement_name());
					// if (bliefSet.getFacts().length > 0) {
					// break;
					// }
					// }
					// }
					// }
					//
					// return true;
				}

				// } else {

				// }
				// }
				//
				// return false;
			}
		}
		return true;
	}

	/**
	 * Extract agent data for a given data mapping
	 * 
	 * @param dm
	 * @return
	 */
	private static Object getMappedAgentData(DataMapping dm, IBDIExternalAccess exta) {
		if (dm.getElementType().equals(AgentElementType.BDI_BELIEF.toString())) { // handle belief mapping
			if (dm.getData_type().equalsIgnoreCase(Constants.BELIEF_UPDATE_IDENTIFIER)) { // when the right content type ("content") has been specified
				IBelief b = exta.getBeliefbase().getBelief(dm.getElement_name());
				return b.getFact();
			} // else ignore as beliefs have not parameters.
		}

		if (dm.getElementType().equals(AgentElementType.BDI_GOAL.toString())) { // handle goal mapping
			for (IGoal g : exta.getGoalbase().getGoals()) {
				if (g.getType().equalsIgnoreCase(dm.getElement_name())) {
					if (g.hasParameter(dm.getData_type())) {
						return g.getParameter(dm.getData_type()).getValue();
					}
				}
			} // else ignore as beliefs have not parameters.
		}

		if (dm.getElementType().equals(AgentElementType.BDI_PLAN.toString())) { // handle plan mapping
			for (IPlan p : exta.getPlanbase().getPlans()) {
				if (p.getType().equalsIgnoreCase(dm.getElement_name())) {
					if (p.hasParameter(dm.getData_type())) {
						return p.getParameter(dm.getData_type()).getValue();
					}
				}
			} // else ignore as beliefs have not parameters.
		}

		return null;
	}
}
