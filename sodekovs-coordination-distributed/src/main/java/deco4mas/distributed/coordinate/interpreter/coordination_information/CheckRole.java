/**
 * 
 */
package deco4mas.distributed.coordinate.interpreter.coordination_information;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.impl.flyweights.BeliefFlyweight;
import jadex.bdi.runtime.impl.flyweights.BeliefSetFlyweight;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.impl.flyweights.GoalFlyweight;
import jadex.bdi.runtime.impl.flyweights.InternalEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.micro.MicroAgent;
import jadex.rules.state.IOAVState;

import java.lang.reflect.Field;
import java.util.HashMap;

import deco.distributed.lang.dynamics.AgentElementType;
import deco.distributed.lang.dynamics.mechanism.AgentElement;
import deco.distributed.lang.dynamics.properties.AgentReference;
import deco.distributed.lang.dynamics.properties.ElementReference;
import deco4mas.distributed.annotation.agent.DataMapping;
import deco4mas.distributed.annotation.agent.ParameterMapping;
import deco4mas.distributed.helper.Constants;
import deco4mas.distributed.helper.SJavaParser;

/**
 * Checks, whether the agent is within the role, which is necessary to publish/perceive the specified element(i.e. goal, belief, beliefset)
 * 
 * @author Ante Vilenica & Thomas Preisler
 * 
 */
public class CheckRole {

	/**
	 * Used to check the role when coordination events should be "PUBLISHED" (PUBLICATION).
	 * 
	 * Check, whether the role is active for the specified element. Maps the parameter and data associated with this role, if role is active.
	 * 
	 * @param agentReference
	 * @param bia
	 * @param agentEvent
	 * @param ae
	 * @return if return=null, then check_false, i.e. the role is not active.
	 */
	public static HashMap<String, Object> checkForPublish(AgentReference agentReference, AgentElement ae, AgentEvent agentEvent, IBDIInternalAccess bia) {

		HashMap<String, Object> parameters = null;

		if (checkCondition(agentReference, bia) && checkInhibitions(agentReference, bia)) {
			// get parameter and data mappings
			if (agentEvent.getSource() instanceof PlanFlyweight) { // handle
																	// plan-events
				PlanFlyweight plan = (PlanFlyweight) agentEvent.getSource();

				parameters = new HashMap<String, Object>();
				for (ParameterMapping pm : ae.getParameter_mappings()) {
					parameters.put(pm.getRef(), plan.getParameter(pm.getLocalName()).getValue());
				}
				for (DataMapping dm : ae.getData_mappings()) { // check data
																// mappings
					parameters.put(dm.getRef(), getMappedAgentData(dm, bia));
				}
				return parameters;

			} else if (agentEvent.getSource() instanceof GoalFlyweight) { // handle
																			// goal-events
				GoalFlyweight goal = (GoalFlyweight) agentEvent.getSource();

				parameters = new HashMap<String, Object>();
				for (ParameterMapping pm : ae.getParameter_mappings()) {
					parameters.put(pm.getRef(), goal.getParameter(pm.getLocalName()).getValue());
				}
				for (DataMapping dm : ae.getData_mappings()) { // check data
																// mappings
					parameters.put(dm.getRef(), getMappedAgentData(dm, bia));
				}
				return parameters;

			} else if (agentEvent.getSource() instanceof InternalEventFlyweight) { // handle
																					// internal-events
				InternalEventFlyweight internalEvent = (InternalEventFlyweight) agentEvent.getSource();

				parameters = new HashMap<String, Object>();
				for (ParameterMapping pm : ae.getParameter_mappings()) {
					parameters.put(pm.getRef(), internalEvent.getParameter(pm.getLocalName()).getValue());
				}
				return parameters;

			} else if (agentEvent.getSource() instanceof BeliefFlyweight) { // handle
																			// belief-events
				BeliefFlyweight belief = (BeliefFlyweight) agentEvent.getSource();

				parameters = new HashMap<String, Object>();
				for (ParameterMapping pm : ae.getParameter_mappings()) {
					if (pm.getLocalName().equalsIgnoreCase(Constants.BELIEF_UPDATE_IDENTIFIER)) {
						parameters.put(pm.getRef(), belief.getFact()); // "content"
																		// matches
																		// to
																		// the
																		// content
																		// of
																		// the
																		// belief.
					}
				}
				for (DataMapping dm : ae.getData_mappings()) { // check data
																// mappings
					parameters.put(dm.getRef(), getMappedAgentData(dm, bia));
				}
				return parameters;
			} else if (agentEvent.getSource() instanceof BeliefSetFlyweight) { // handle
																				// beliefset-events
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
				for (DataMapping dm : ae.getData_mappings()) { // check data
																// mappings
					parameters.put(dm.getRef(), getMappedAgentData(dm, bia));
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
	 * @param ia
	 */
	public static boolean checkForPerceive(AgentReference agentReference, IInternalAccess ia) {

		if (ia instanceof IBDIInternalAccess) {
			IBDIInternalAccess bia = (IBDIInternalAccess) ia;
			return (checkCondition(agentReference, bia) && checkInhibitions(agentReference, bia));
		} else if (ia instanceof MicroAgent) {
			MicroAgent ma = (MicroAgent) ia;
			return checkCondition(agentReference, ma);
		}

		return true;
	}

	/**
	 * * Check the conditions associated with the role
	 * 
	 * @param agentReference
	 * @param bia
	 * @return
	 */
	private static boolean checkCondition(AgentReference agentReference, IBDIInternalAccess bia) {
		// checking constraints:

		if (agentReference.hasConstraints()) {
			if (agentReference.getContraints().getCondition().getExpression() != null && agentReference.getContraints().getCondition().getExpression().length() > 0) {

				IOAVState state = ((ElementFlyweight) bia).getState();
				Object rCapability = ((ElementFlyweight) bia).getScope();

				String expression = agentReference.getContraints().getCondition().getExpression();

				// Evaluate condition/expression
				OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rCapability);
				Object val = SJavaParser.evaluateExpression(expression, fetcher);

				if (val instanceof Boolean) {
					if ((Boolean) val) {
						return true;
					}
				} else {
					System.out.println("#CheckRole#" + bia.getComponentIdentifier().getName() + ":");
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
	 * @param bia
	 * @return boolean
	 */
	private static boolean checkInhibitions(final AgentReference agentReference, final IBDIInternalAccess bia) {
		// check inhibitions:

		Boolean result = true;
		if (agentReference.hasConstraints()) { // inhibitions exist

			if (agentReference.getContraints().getInhibitions() != null) {

				for (ElementReference inhib : agentReference.getContraints().getInhibitions()) { // check
																									// each
																									// inhibition

					if (inhib.getAgent_element_type().toString().equalsIgnoreCase(AgentElementType.BDI_GOAL.toString())) {
						IGoal[] gs = bia.getGoalbase().getGoals(); // fetch
																	// goals:
						for (IGoal g : gs) {
							if (g.getType().equals(inhib.getElement_id())) {
								if (g.isActive()) { // TODO: check only
													// active???
									result = false;
								}
							}
						}
					}

					if (inhib.getAgent_element_type().toString().equalsIgnoreCase(AgentElementType.BDI_PLAN.toString())) {
						IPlan[] pl = bia.getPlanbase().getPlans(); // fetch
																	// plans:
						for (IPlan p : pl) {
							if (p.getType().equals(inhib.getElement_id())) {
								result = false;
							}
						}
					}

					if (inhib.getAgent_element_type().toString().equalsIgnoreCase(AgentElementType.BDI_BELIEF.toString())) {
						String[] bs = bia.getBeliefbase().getBeliefNames(); // fetch
																			// beliefs:
						for (String b : bs) {
							if (b.equals(inhib.getElement_id())) {
								if (bia.getBeliefbase().getBelief(b) != null) {
									result = false;
								}
							}
						}
					}

					if (inhib.getAgent_element_type().toString().equalsIgnoreCase(AgentElementType.BDI_BELIEFSET.toString())) {
						String[] bs = bia.getBeliefbase().getBeliefSetNames(); // fetch
																				// beliefSets:
						for (String b : bs) {
							if (b.equals(inhib.getElement_id())) {
								if ((Integer) bia.getBeliefbase().getBeliefSetNames().length < 0) {
									result = false;
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Extract agent data for a given data mapping
	 * 
	 * @param dm
	 * @return
	 */
	private static Object getMappedAgentData(final DataMapping dm, final IBDIInternalAccess bia) {
		Object result = new Object();

		if (dm.getElementType().equals(AgentElementType.BDI_BELIEF.toString())) { // handle
																					// belief
																					// mapping
			if (dm.getData_type().equalsIgnoreCase(Constants.BELIEF_UPDATE_IDENTIFIER)) { // when
																							// the
																							// right
																							// content
																							// type
																							// ("content")
																							// has
																							// been
																							// specified
				IBelief b = bia.getBeliefbase().getBelief(dm.getElement_name());
				result = b.getFact();
			} // else ignore as beliefs have not parameters.
		}

		if (dm.getElementType().equals(AgentElementType.BDI_GOAL.toString())) { // handle
																				// goal
																				// mapping
			IGoal[] goals = bia.getGoalbase().getGoals();
			for (IGoal g : goals) {
				if (g.getType().equalsIgnoreCase(dm.getElement_name())) {
					if (g.hasParameter(dm.getData_type())) {
						result = g.getParameter(dm.getData_type()).getValue();
					}
				}
			} // else ignore as beliefs have not parameters.
		}

		if (dm.getElementType().equals(AgentElementType.BDI_PLAN.toString())) { // handle
																				// plan
																				// mapping
			IPlan[] plans = bia.getPlanbase().getPlans();
			for (IPlan p : plans) {
				if (p.getType().equalsIgnoreCase(dm.getElement_name())) {
					if (p.hasParameter(dm.getData_type())) {
						result = p.getParameter(dm.getData_type()).getValue();
					}
				}
			} // else ignore as beliefs have not parameters.
		}
		return result;
	}

	/**
	 * Used to check the role when coordination events should be "PUBLISHED" (PUBLICATION).
	 * 
	 * Check, whether the role is active for the specified element. Maps the parameter and data associated with this role, if role is active. *
	 * 
	 * @param agentReference
	 * @param agentElement
	 * @param runStep
	 * @param ma
	 * @return
	 */
	public static HashMap<String, Object> checkForPublishMicro(AgentReference agentReference, AgentElement agentElement, IComponentStep runStep, MicroAgent ma) {

		HashMap<String, Object> parameters = null;

		if (checkCondition(agentReference, ma)) {
			parameters = new HashMap<String, Object>();

			for (ParameterMapping pm : agentElement.getParameter_mappings()) {
				Class<? extends IComponentStep> clazz = runStep.getClass();
				try {
					Field field = clazz.getDeclaredField(pm.getLocalName());
					Object value = field.get(runStep);

					parameters.put(pm.getRef(), value);

				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		return parameters;
	}

	/**
	 * Used to check constraints for given {@link AgentReference} and {@link MicroAgent}.
	 * 
	 * @param agentReference
	 * @param ma
	 * @return Currently this method only returns <code>true</code> because the constraint support for the micro agents still needs to be implemented
	 */
	private static boolean checkCondition(AgentReference agentReference, MicroAgent ma) {
		// TODO Implement constraint support for micro agents
		return true;
	}
}
