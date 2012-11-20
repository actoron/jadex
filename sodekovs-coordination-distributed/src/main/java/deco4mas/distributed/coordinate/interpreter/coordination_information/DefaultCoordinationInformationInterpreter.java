/**
 * 
 */
package deco4mas.distributed.coordinate.interpreter.coordination_information;

import jadex.bdi.model.IMPlan;
import jadex.bdi.model.IMPlanbase;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IEventbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalbase;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.IPlanbase;
import jadex.bdi.runtime.impl.flyweights.BeliefbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.EventbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.ExternalAccessFlyweight;
import jadex.bdi.runtime.impl.flyweights.GoalbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanbaseFlyweight;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.IValueFetcher;
import jadex.commons.SUtil;
import jadex.commons.SimplePropertyObject;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;
import jadex.micro.MicroAgent;
import jadex.rules.state.IOAVState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import deco.distributed.lang.dynamics.AgentElementType;
import deco.distributed.lang.dynamics.Causalities;
import deco.distributed.lang.dynamics.MASDynamics;
import deco.distributed.lang.dynamics.mechanism.AgentElement;
import deco.distributed.lang.dynamics.mechanism.DecentralizedCausality;
import deco4mas.distributed.annotation.agent.CoordinationAnnotation.CoordinationType;
import deco4mas.distributed.annotation.agent.ParameterMapping;
import deco4mas.distributed.coordinate.CoordinationInformation;
import deco4mas.distributed.helper.Constants;

/**
 * The default coordination information interpreter interprets received perceptions from the coordination space and maps them to the according agent elements.
 * 
 * @author Ante Vilenica & Jan Sudeikat & Thomas Preisler
 */
public class DefaultCoordinationInformationInterpreter extends SimplePropertyObject implements ICoordinationInformationInterpreter {

	// -------- constants --------

	/** The percept types property. */
	public static String PROPERTY_PERCEPTTYPES = "percepttypes";

	/** The add action. */
	public static String ADD = "add";

	/** The remove action. */
	public static String REMOVE = "remove";

	/**
	 * The remove_outdated action (checks all entries in the belief set, if they should be seen, but are no longer there).
	 */
	public static String REMOVE_OUTDATED = "remove_outdated";

	/** The set action. */
	public static String SET = "set";

	/** The unset action (sets a belief fact to null). */
	public static String UNSET = "unset";

	/**
	 * The start coordination action or usual coordination event(sets a belief fact to null).
	 */
	public static String COORDINATE_INFO = "coordinate";

	/** The init coordination participants action. */
	public static String COORDINATE_INIT_PARTICIPANTS = "coordinate:Init_Participants";

	/** The maxrange property. */
	public static String PROPERTY_MAXRANGE = "range";

	/** The maxrange property. */
	public static String PROPERTY_RANGE = "range_property";

	// -------- attributes --------

	/** The percepttypes infos. */
	protected Map<String, String[][]> percepttypes;

	/**
	 * The reference to the MASDynamics language
	 */
	private MASDynamics masDyn = null;

	/**
	 * Constructor.
	 * 
	 * @param masDyn
	 *            reference to the MASDynamics language
	 */
	public DefaultCoordinationInformationInterpreter(MASDynamics masDyn) {
		this.masDyn = masDyn;
	}

	// -------- methods --------

	@Override
	public void processPercept(final IEnvironmentSpace space, final String type, final Object percept, final IComponentDescription agent, final ISpaceObject avatar) {
		boolean invoke = false;
		final String[][] metainfos = getMetaInfos(type);
		for (int i = 0; !invoke && metainfos != null && i < metainfos.length; i++) {
			invoke = ADD.equals(metainfos[i][0]) || REMOVE.equals(metainfos[i][0]) || SET.equals(metainfos[i][0]) || UNSET.equals(metainfos[i][0]) || REMOVE_OUTDATED.equals(metainfos[i][0])
					&& percept.equals(avatar) || COORDINATE_INFO.equals(metainfos[i][0]) || COORDINATE_INIT_PARTICIPANTS.equals(metainfos[i][0]);
		}

		if (invoke) {
			// IAMS ams = (IAMS) ((IApplicationContext)
			// space.getContext()).getPlatform().getService(IComponentManagementService.class);
			IComponentManagementService cms = (IComponentManagementService) SServiceProvider.getServiceUpwards(space.getExternalAccess().getServiceProvider(), IComponentManagementService.class).get(
					new ThreadSuspendable());
			IFuture<IExternalAccess> result = cms.getExternalAccess(agent.getName());
			result.addResultListener(new DefaultResultListener<IExternalAccess>() {

				@Override
				public void resultAvailable(final IExternalAccess exta) {
					exta.scheduleStep(new IComponentStep<Void>() {

						@Override
						public IFuture<Void> execute(IInternalAccess ia) {
							try {
								IBDIInternalAccess bia = null;
								if (ia instanceof IBDIInternalAccess) {
									bia = (IBDIInternalAccess) ia;
								}

								for (int i = 0; i < metainfos.length; i++) {
									IParsedExpression cond = metainfos[i].length == 2 ? null : (IParsedExpression) getProperty(metainfos[i][2]);
									SimpleValueFetcher fetcher = null;
									if (cond != null) {
										fetcher = new SimpleValueFetcher();
										fetcher.setValue("$space", space);
										fetcher.setValue("$percept", percept);
										fetcher.setValue("$avatar", avatar);
										fetcher.setValue("$type", type);
										fetcher.setValue("$aid", agent);
										fetcher.setValue("$scope", exta);
									}

									if (ADD.equals(metainfos[i][0])) {
										// System.out.println("#BDICoordinationInterpreter # Trying to store belief with meta infos: ");
										IBeliefSet belset = bia.getBeliefbase().getBeliefSet(metainfos[i][1]);
										if (cond != null)
											fetcher.setValue("$facts", belset.getFacts());
										if (!belset.containsFact(percept) && (cond == null || evaluate(cond, fetcher))) {
											belset.addFact(percept);
											System.out.println("added: " + percept + " to: " + belset);
										}// the "normal" coordination event
									} else if (COORDINATE_INFO.equals(metainfos[i][0])) {
										ISpaceObject coordinationSpaceObj = (ISpaceObject) percept;
										@SuppressWarnings("unchecked")
										Map<String, Map<String, Set<Object[]>>> applicableAgentTypes = (Map<String, Map<String, Set<Object[]>>>) coordinationSpaceObj
												.getProperty(Constants.ROLE_DEFINITIONS_FOR_PERCEIVE);
										if (applicableAgentTypes.get(getAgentType(exta)) != null) {
											Map<String, Set<Object[]>> applicablePerceiveRoles = applicableAgentTypes.get(getAgentType(exta));
											String dcmName = (String) coordinationSpaceObj.getProperty(Constants.DML_REALIZATION_NAME);
											if (applicablePerceiveRoles.get(dcmName) != null) {
												Set<Object[]> agentDataSet = applicablePerceiveRoles.get(dcmName);
												Iterator<Object[]> agentDataIterator = agentDataSet.iterator();
												while (agentDataIterator.hasNext()) {
													Object[] agentData = agentDataIterator.next();
													CoordinationInformation dci = (CoordinationInformation) agentData[0];
													final AgentElement ae = (AgentElement) agentData[1];
													if (CheckRole.checkForPerceive(dci.getRef(), bia)) {
														String elementType = ae.getAgentElementType();
														final String elementId = ae.getElement_id();
														String agentId = ae.getAgent_id();

														String coordSpaceElementType = null;
														String coordSpaceElementName = null;
														Causalities causalities = masDyn.getCausalities();
														DecentralizedCausality dc = causalities.getRealizationByName(dcmName);
														for (AgentElement agentElement : dc.getTo_agents()) {
															if (agentElement.getAgent_id().equals(agentId)) {
																coordSpaceElementType = agentElement.getAgentElementType();
																coordSpaceElementName = agentElement.getElement_id();
															}
														}

														if (coordSpaceElementType == null || coordSpaceElementType.equals(elementType)) { // when
															if (coordSpaceElementName == null || coordSpaceElementName.equals(elementId)) {
																@SuppressWarnings("unchecked")
																final HashMap<String, Object> receivedParamDataMappings = (HashMap<String, Object>) coordinationSpaceObj
																		.getProperty(Constants.PARAMETER_DATA_MAPPING);
																if (elementType.equals(AgentElementType.BDI_BELIEFSET.toString())) {
																	processBDIBeliefSet(dci, bia, elementId, exta, coordinationSpaceObj);
																} else if (elementType.equals(AgentElementType.BDI_BELIEF.toString())) {
																	processBDIBelief(dci, bia, elementId, exta, coordinationSpaceObj);																
																} else if (elementType.equals(AgentElementType.BDI_GOAL.toString())) {
																	processBDIGoal(dci, bia, elementId, exta, coordinationSpaceObj, ae, receivedParamDataMappings);
																} else if (elementType.equals(AgentElementType.BDI_PLAN.toString())) {
																	processBDIPlan(dci, bia, elementId, exta, coordinationSpaceObj, ae, receivedParamDataMappings);
																	System.out.println("#BDICoordInfInterpreter# Error!!! RECEIVED PLAN TO MANIPULATE:  " + elementId + " for "
																			+ exta.getComponentIdentifier().getName() + " Currently unable to process IPlan due limits of used jadex-version");
																} else if (elementType.equals(AgentElementType.INTERNAL_EVENT.toString())) {
																	processBDIInternalEvent(dci, bia, elementId, exta, coordinationSpaceObj, ae, receivedParamDataMappings);
																} else if (elementType.equals(AgentElementType.MICRO_STEP.toString())) {
																	processMicroStep(dci, (MicroAgent) ia, elementId, ae, receivedParamDataMappings);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							return IFuture.DONE;
						}
					});

				}
			});
		}
	}

	/**
	 * Process the perception for an {@link AgentElementType#INTERNAL_EVENT}.
	 * 
	 * @param dci
	 * @param bia
	 * @param elementId
	 * @param exta
	 * @param coordinationSpaceObj
	 * @param ae
	 * @param receivedParamDataMappings
	 */
	private void processBDIInternalEvent(CoordinationInformation dci, IBDIInternalAccess bia, String elementId, IExternalAccess exta, ISpaceObject coordinationSpaceObj, AgentElement ae,
			HashMap<String, Object> receivedParamDataMappings) {
		if (dci.getCoordinationType().equals(CoordinationType.NEGATIVE)) {
			System.out.println(exta.getComponentIdentifier().getName() + ":");
			System.out.println("\t ERROR: can not remove internal events from execution context.");
			// TODO make exception for this incident.
		} else {
			ExternalAccessFlyweight extaFly = (ExternalAccessFlyweight) exta;
			IOAVState state = extaFly.getState();
			Object[] scope = AgentRules.resolveCapability(elementId, OAVBDIMetaModel.internalevent_type, extaFly.getScope(), state);
			Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
			if (state.containsKey(mscope, OAVBDIMetaModel.capability_has_internalevents, scope[0])) {
				IEventbase base = EventbaseFlyweight.getEventbaseFlyweight(state, scope[1]);
				IInternalEvent ie = base.createInternalEvent(elementId);
				for (ParameterMapping pm : ae.getParameter_mappings()) {
					ie.getParameter(pm.getLocalName()).setValue(receivedParamDataMappings.get(pm.getRef()));
				}
				base.dispatchInternalEvent(ie);
			} else {
				throw new RuntimeException("No such belief: " + scope[0] + " in " + scope[1]);
			}
		}
	}

	/**
	 * Process the perception for an {@link AgentElementType#BDI_GOAL}.
	 * 
	 * @param dci
	 * @param bia
	 * @param elementId
	 * @param exta
	 * @param coordinationSpaceObj
	 * @param ae
	 * @param receivedParamDataMappings
	 */
	private void processBDIGoal(CoordinationInformation dci, IBDIInternalAccess bia, String elementId, IExternalAccess exta, ISpaceObject coordinationSpaceObj, AgentElement ae,
			HashMap<String, Object> receivedParamDataMappings) {
		if (dci.getCoordinationType().equals(CoordinationType.NEGATIVE)) {
			IGoal[] goals = bia.getGoalbase().getGoals(elementId);
			goals[goals.length - 1].drop();
			// TODO: heuristic for selection
			System.out.println("#BDICoordInfInterpreter# Removed GOAL from GOALBASE : " + elementId + " for " + exta.getComponentIdentifier().getName());
		} else {
			ExternalAccessFlyweight extaFly = (ExternalAccessFlyweight) exta;
			IOAVState state = extaFly.getState();
			Object[] scope = AgentRules.resolveCapability(elementId, OAVBDIMetaModel.internalevent_type, extaFly.getScope(), state);
			Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
			if (state.containsKey(mscope, OAVBDIMetaModel.capability_has_goals, scope[0])) {
				IGoalbase base = GoalbaseFlyweight.getGoalbaseFlyweight(state, scope[1]);
				IGoal g = base.createGoal(elementId);
				for (ParameterMapping pm : ae.getParameter_mappings()) {
					g.getParameter(pm.getLocalName()).setValue(receivedParamDataMappings.get(pm.getRef()));
				}
				base.dispatchTopLevelGoal(g);

			} else {
				throw new RuntimeException("No such belief: " + scope[0] + " in " + scope[1]);
			}
		}
	}

	/**
	 * Process the perception for an {@link AgentElementType#BDI_PLAN}.
	 * 
	 * @param dci
	 * @param bia
	 * @param elementId
	 * @param exta
	 * @param coordinationSpaceObj
	 * @param ae
	 * @param receivedParamDataMappings
	 */
	private void processBDIPlan(CoordinationInformation dci, IBDIInternalAccess bia, String elementId, IExternalAccess exta, ISpaceObject coordinationSpaceObj, AgentElement ae,
			HashMap<String, Object> receivedParamDataMappings) {
		if (dci.getCoordinationType().equals(CoordinationType.NEGATIVE)) {
			IPlan[] plans = bia.getPlanbase().getPlans(elementId);
			plans[plans.length - 1].abortPlan();

			System.out.println("#BDICoordInfInterpreter# Removed PLAN from PLANBASE : " + elementId + " for " + exta.getComponentIdentifier().getName());
		} else {
			ExternalAccessFlyweight extaFly = (ExternalAccessFlyweight) exta;
			IOAVState state = extaFly.getState();
			Object[] scope = AgentRules.resolveCapability(elementId, OAVBDIMetaModel.internalevent_type, extaFly.getScope(), state);
			Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
			if (state.containsKey(mscope, OAVBDIMetaModel.capability_has_plans, scope[0])) {
				// IGoalbase base = GoalbaseFlyweight.getGoalbaseFlyweight(state, scope[1]);
				PlanbaseFlyweight base = (PlanbaseFlyweight) PlanbaseFlyweight.getPlanbaseFlyweight(state, scope[1]);
//				IPlan p = base.createPlan(elementId);
				
				IMPlan mplan = ((IMPlanbase)base.getModelElement()).getPlan("startplan");
				IPlan plan = ((IPlanbase) base).createPlan(mplan); 
				
//				IMPlan p2 = ((IMPlanbase)base).getPlan("test");
//				((IMPlanbase)base).
//				IMPlan myPlan = PlanbaseFlyweight.get
//				base.get
				
//				IMPlan mplan = ((PlanbaseFlyweight) base).getModelElement()).getPlan("startplan");
//				IPlan plan = getPlanbase().createPlan(mplan);
//				plan.startPlan();


				// TODO: Continue here!!!!
				// IPlan p = base. createPlan(elementId);
				// for (ParameterMapping pm : ae.getParameter_mappings()) {
				// g.getParameter(pm.getLocalName()).setValue(receivedParamDataMappings.get(pm.getRef()));
				// }
				// base.dispatchTopLevelGoal(g);

			} else {
				throw new RuntimeException("No such belief: " + scope[0] + " in " + scope[1]);
			}
		}
	}

	/**
	 * Process the perception for an {@link AgentElementType#BDI_BELIEF}.
	 * 
	 * @param dci
	 * @param bia
	 * @param elementId
	 * @param exta
	 * @param coordinationSpaceObj
	 */
	private void processBDIBelief(CoordinationInformation dci, IBDIInternalAccess bia, String elementId, IExternalAccess exta, ISpaceObject coordinationSpaceObj) {
		if (dci.getCoordinationType().equals(CoordinationType.NEGATIVE)) {
			// IBelief bel = exta.getBeliefbase().getBelief(metainfos[i][1]);
			IBelief bel = bia.getBeliefbase().getBelief(elementId);
			bel.setFact(new Object());
			// TODO better default value...
			System.out.println("#BDICoordInfInterpreter# Removed fact from BELIEF : " + elementId);
		} else {
			ExternalAccessFlyweight extaFly = (ExternalAccessFlyweight) exta;
			IOAVState state = extaFly.getState();
			Object[] scope = AgentRules.resolveCapability(elementId, OAVBDIMetaModel.internalevent_type, extaFly.getScope(), state);
			Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
			if (state.containsKey(mscope, OAVBDIMetaModel.capability_has_beliefs, scope[0])) {
				
				IBeliefbase base = BeliefbaseFlyweight.getBeliefbaseFlyweight(state, scope[1]);
				IBelief bel = base.getBelief(elementId);
				bel.setFact(coordinationSpaceObj.getProperty(Constants.VALUE));															
			} else {
				throw new RuntimeException("No such belief: " + scope[0] + " in " + scope[1]);
			}
			System.out.println("#BDICoordInfInterpreter# Added " + coordinationSpaceObj.getProperty(Constants.VALUE).toString() + " to BELIEF : " + elementId + " for "
					+ exta.getComponentIdentifier().getName());
		}
	}

	/**
	 * Process the perception for an {@link AgentElementType#BDI_BELIEFSET}.
	 * 
	 * @param dci
	 * @param bia
	 * @param elementId
	 * @param exta
	 * @param coordinationSpaceObj
	 */
	private void processBDIBeliefSet(CoordinationInformation dci, IBDIInternalAccess bia, String elementId, IExternalAccess exta, ISpaceObject coordinationSpaceObj) {
		if (dci.getCoordinationType().equals(CoordinationType.NEGATIVE)) {

			IBeliefSet belset = bia.getBeliefbase().getBeliefSet(elementId);
			Object[] facts = belset.getFacts();
			belset.removeFact(facts[facts.length - 1]); // remove
			System.out.println("#BDICoordInfInterpreter# Removed last fact from BELIEF_SET: " + elementId + " for " + exta.getComponentIdentifier().getName());
		} else {
			ExternalAccessFlyweight extaFly = (ExternalAccessFlyweight) exta;
			IOAVState state = extaFly.getState();
			Object[] scope = AgentRules.resolveCapability(elementId, OAVBDIMetaModel.internalevent_type, extaFly.getScope(), state);
			Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
			if (state.containsKey(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0])) {
				IBeliefbase base = BeliefbaseFlyweight.getBeliefbaseFlyweight(state, scope[1]);
				IBeliefSet belset = base.getBeliefSet(elementId);
				belset.addFact(coordinationSpaceObj.getProperty(Constants.VALUE));
			} else {
				throw new RuntimeException("No such belief: " + scope[0] + " in " + scope[1]);
			}
			System.out.println("#BDICoordInfInterpreter# Added " + coordinationSpaceObj.getProperty(Constants.VALUE).toString() + " to BELIEF_SET: " + elementId + " for "
					+ exta.getComponentIdentifier().getName());
		}
	}

	/**
	 * Process the perception of an {@link AgentElementType#MICRO_STEP}.
	 * 
	 * @param dci
	 * @param ma
	 * @param elementId
	 * @param ae
	 * @param receivedParamDataMappings
	 */
	private void processMicroStep(CoordinationInformation dci, MicroAgent ma, final String elementId, final AgentElement ae, final HashMap<String, Object> receivedParamDataMappings) {
		if (dci.getCoordinationType().equals(CoordinationType.NEGATIVE)) {
			System.out.println(ma.getComponentDescription().getName() + ":");
			System.out.println("\t ERROR: can not remove micro agent steps from execution context.");
			// TODO make exception for this incident
		} else {
			try {
				Class<?> clazz = ma.getClass();
				boolean found = false;

				while (!found && clazz != null) {
					Class<?>[] classes = clazz.getDeclaredClasses();

					for (Class<?> c : classes) {
						if (c.getSimpleName().equals(elementId)) {
							found = true;
							Constructor<?> constructor = c.getConstructor(clazz);

							IComponentStep step = (IComponentStep) constructor.newInstance(ma);

							for (ParameterMapping pm : ae.getParameter_mappings()) {
								try {
									Field field = c.getField(pm.getLocalName());
									field.set(step, receivedParamDataMappings.get(pm.getRef()));
								} catch (Exception e) {
									throw new RuntimeException("No such field: " + pm.getLocalName() + " in " + c);
								}
							}

							ma.scheduleStep(step);
						}
					}

					clazz = clazz.getSuperclass();
				}

				if (!found) {
					throw new RuntimeException("No such step: " + elementId + " in " + clazz);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	protected String[][] getMetaInfos(String percepttype) {
		if (percepttypes == null) {
			this.percepttypes = new HashMap<String, String[][]>();
			Object[] percepttypes = getPerceptTypes();
			for (int i = 0; i < percepttypes.length; i++) {
				String[] per = (String[]) percepttypes[i];
				String[][] newmis = per.length == 3 ? new String[][] { { per[1], per[2] } } : new String[][] { { per[1], per[2], per[3] } };
				String[][] oldmis = this.percepttypes.get(per[0]);
				if (oldmis != null)
					newmis = (String[][]) SUtil.joinArrays(oldmis, newmis);
				this.percepttypes.put(per[0], newmis);
			}
		}
		return percepttypes.get(percepttype);
	}

	/**
	 * Evaluate a condition.
	 * 
	 * @param exp
	 *            The expression.
	 * @param fetcher
	 *            The value fetcher.
	 */
	protected boolean evaluate(IParsedExpression exp, IValueFetcher fetcher) {
		boolean ret = false;
		try {
			ret = ((Boolean) exp.getValue(fetcher)).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Get the percept types defined for this generator.
	 * 
	 * @return The percept types.
	 */
	protected Object[] getPerceptTypes() {
		return (Object[]) getProperty(PROPERTY_PERCEPTTYPES);
	}

	private String getAgentType(IExternalAccess exta) {
		return exta.getModel().getName();
	}
}