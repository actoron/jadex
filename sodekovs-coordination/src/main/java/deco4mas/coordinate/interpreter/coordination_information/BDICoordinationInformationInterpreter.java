/**
 * 
 */
package deco4mas.coordinate.interpreter.coordination_information;

import jadex.application.runtime.ISpace;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SimpleValueFetcher;
import java.util.HashMap;
import java.util.Map;
import deco.lang.dynamics.AgentElementType;
import deco.lang.dynamics.mechanism.AgentElement;
import deco4mas.annotation.agent.ParameterMapping;
import deco4mas.annotation.agent.CoordinationAnnotation.CoordinationType;
import deco4mas.coordinate.DecentralCoordinationInformation;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInfo;

/**
 * @author Ante Vilenica & Jan Sudeikat
 */
public class BDICoordinationInformationInterpreter extends SimplePropertyObject implements ICoordinationInformationInterpreter
{

	// -------- constants --------

	/** The percept types property. */
	public static String PROPERTY_PERCEPTTYPES = "percepttypes";

	/** The add action. */
	public static String ADD = "add";

	/** The remove action. */
	public static String REMOVE = "remove";

	/**
	 * The remove_outdated action (checks all entries in the belief set, if they
	 * should be seen, but are no longer there).
	 */
	public static String REMOVE_OUTDATED = "remove_outdated";

	/** The set action. */
	public static String SET = "set";

	/** The unset action (sets a belief fact to null). */
	public static String UNSET = "unset";

	/**
	 * The start coordination action or usual coordination event(sets a belief
	 * fact to null).
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
	protected Map percepttypes;

	// -------- methods --------

	/*
	 * (non-Javadoc)
	 * @seedeco4mas.coordinate.interpreter.coordination_information.
	 * ICoordinationInformationInterpreter
	 * #dispatchWhenApplicable(java.lang.String, java.lang.Object,
	 * jadex.bridge.IAgentIdentifier,
	 * jadex.adapter.base.envsupport.environment.ISpaceObject)
	 */
	// @Override
	// public void dispatchWhenApplicable(String type, Object data,
	// IAgentIdentifier agent, ISpaceObject avatars) {
	// TODO Auto-generated method stub
	// }
	/*
	 * (non-Javadoc)
	 * @seedeco4mas.coordinate.interpreter.coordination_information.
	 * ICoordinationInformationInterpreter#processPercept(jadex.bridge.ISpace,
	 * java.lang.String, java.lang.Object, jadex.bridge.IAgentIdentifier,
	 * jadex.adapter.base.envsupport.environment.ISpaceObject)
	 */
	// @Override

	public void processPercept(final IEnvironmentSpace space, final String type, final Object percept, final IComponentIdentifier agent,
		final ISpaceObject avatar)
	{
		boolean invoke = false;
		final String[][] metainfos = getMetaInfos(type);
		for (int i = 0; !invoke && metainfos != null && i < metainfos.length; i++)
		{
			invoke = ADD.equals(metainfos[i][0]) || REMOVE.equals(metainfos[i][0]) || SET.equals(metainfos[i][0])
				|| UNSET.equals(metainfos[i][0]) || REMOVE_OUTDATED.equals(metainfos[i][0]) && percept.equals(avatar)
				|| COORDINATE_INFO.equals(metainfos[i][0]) || COORDINATE_INIT_PARTICIPANTS.equals(metainfos[i][0]);
		}

		// the AgentType of the currently processed Agent
		// final String thisAgentType = ((IApplicationContext)
		// ((AbstractEnvironmentSpace) space).getContext()).getAgentType(agent);
		final String thisAgentType = ((AbstractEnvironmentSpace) space).getContext().getComponentType(agent);

		if (invoke)
		{
			// IAMS ams = (IAMS) ((IApplicationContext)
			// space.getContext()).getPlatform().getService(IComponentManagementService.class);
			IComponentManagementService ams = (IComponentManagementService) ((AbstractEnvironmentSpace) space).getContext()
				.getServiceContainer().getService(IComponentManagementService.class);
			ams.getExternalAccess(agent, new IResultListener()
			{
				public void exceptionOccurred(Exception exception)
				{
					// exception.printStackTrace();
				}

				public void resultAvailable(Object source, Object result)
				{
					final IBDIExternalAccess exta = (IBDIExternalAccess) result;
					exta.invokeLater(new Runnable()
					{
						public void run()
						{
							try
							{
								for (int i = 0; i < metainfos.length; i++)
								{
									IParsedExpression cond = metainfos[i].length == 2 ? null
										: (IParsedExpression) getProperty(metainfos[i][2]);
									SimpleValueFetcher fetcher = null;
									if (cond != null)
									{
										fetcher = new SimpleValueFetcher();
										fetcher.setValue("$space", space);
										fetcher.setValue("$percept", percept);
										fetcher.setValue("$avatar", avatar);
										fetcher.setValue("$type", type);
										fetcher.setValue("$aid", agent);
										fetcher.setValue("$scope", exta);
									}

									if (ADD.equals(metainfos[i][0]))
									{
										// System.out.println("#BDICoordinationInterpreter # Trying to store belief with meta infos: ");
										IBeliefSet belset = exta.getBeliefbase().getBeliefSet(metainfos[i][1]);
										if (cond != null)
											fetcher.setValue("$facts", belset.getFacts());
										if (!belset.containsFact(percept) && (cond == null || evaluate(cond, fetcher)))
										{
											belset.addFact(percept);
											System.out.println("added: " + percept + " to: " + belset);
										}// the "normal" coordination event
									} else if (COORDINATE_INFO.equals(metainfos[i][0]))
									{
										ISpaceObject coordinationSpaceObj = (ISpaceObject) percept;
										// Map<String, Object> decom4MasMap =
										// (Map<String, Object>)
										// exta.getBeliefbase().getBelief(Constants.DECO4MAS_BELIEF_NAME).getFact();
										// Map<String, Object[]>
										// applicablePerceiveRoles =
										// (Map<String, Object[]>)
										// decom4MasMap.get(Constants.ROLE_DEFINITIONS_FOR_PERCEIVE);
										Map<String, Map<String, Object[]>> applicableAgentTypes = (Map<String, Map<String, Object[]>>) coordinationSpaceObj
											.getProperty(Constants.ROLE_DEFINITIONS_FOR_PERCEIVE);
										// coordInfo.addValue(Constants.VALUE,
										// value);
										// //
										// coordInfo.addValue(Constants.TO_AGENTS,
										// toAgents);
										// coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING,
										// parameterDataMappings);
										// coordInfo.addValue(Constants.DML_REALIZATION_NAME,
										// dmlRealizationName);
										// Check whether there is a perceive
										// definition for this agentType
										if (applicableAgentTypes.get(getAgentType(exta)) != null)
										{
											Map<String, Object[]> applicablePerceiveRoles = applicableAgentTypes.get(getAgentType(exta));
											String dcmName = (String) coordinationSpaceObj.getProperty(Constants.DML_REALIZATION_NAME);
											// Check whether this percept should
											// be processed by this agent, i.e.
											// if it is applicable. Does this
											// agent have the received DCM as a
											// perceive-event?
											if (applicablePerceiveRoles.get(dcmName) != null)
											{
												Object[] agentData = applicablePerceiveRoles.get(dcmName);
												DecentralCoordinationInformation dci = (DecentralCoordinationInformation) agentData[0];
												AgentElement ae = (AgentElement) agentData[1];
												if (CheckRole.checkForPerceive(dci.getRef(), exta))
												{ // check role
													String elementType = ae.getAgentElementType();
													String elementId = ae.getElement_id();
													if ((coordinationSpaceObj.getProperty(CoordinationInfo.AGENT_ELEMENT_TYPE)).equals(elementType))
													{
														if (((String) coordinationSpaceObj.getProperty(CoordinationInfo.AGENT_ELEMENT_NAME))
															.equals(elementId))
														{
															HashMap<String, Object> receivedParamDataMappings = (HashMap<String, Object>) coordinationSpaceObj
																.getProperty(Constants.PARAMETER_DATA_MAPPING);

															// TODO:
															// ParameterMappings
															// for PERCEIVE

															// DecentralCoordinationInformation
															// decentralCoordInfo
															// =
															// ((DecentralCoordinationInformation)
															// decom4MasMap
															// .get(Constants.DECENTRAL_COORDINATION_INFO_MAPPING));
															//
															if (elementType.equals(AgentElementType.BDI_BELIEFSET.toString()))
															{
																// System.out.println("BDICoordInfInterpreter# RECEIVED BELIEF_SET TO MANIPULATE:  "
																// + elementId +
																// " for " +
																// exta.getAgentName());

																// check
																// direction
																// (POSITIVE ->
																// add fact to
																// beliefset /
																// NEGATIVE ->
																// remove fact
																// from
																// beliefset)
																if (dci.getCoordinationType().equals(CoordinationType.NEGATIVE))
																{

																	// IBeliefSet
																	// belset =
																	// exta.getBeliefbase().getBeliefSet(metainfos[i][1]);
																	IBeliefSet belset = exta.getBeliefbase().getBeliefSet(elementId);
																	Object[] facts = belset.getFacts();
																	belset.removeFact(facts[facts.length - 1]); // remove
																	// the
																	// last
																	// fact
																	System.out
																		.println("#BDICoordInfInterpreter# Removed last fact from BELIEF_SET: "
																			+ elementId + " for " + exta.getAgentName());
																} else
																{
																	// TODO:
																	// Distinguish
																	// between
																	// 'UPDATE
																	// Values'
																	// and 'ADD
																	// Values'
																	// System.out.println("#tmp# percept: "
																	// +
																	// percept);
																	// IBeliefSet
																	// belset =
																	// exta.getBeliefbase().getBeliefSet(metainfos[i][1]);
																	IBeliefSet belset = exta.getBeliefbase().getBeliefSet(elementId);
																	// belset.addFact(percept);
																	belset.addFact(coordinationSpaceObj.getProperty(Constants.VALUE)
																		.toString());
																	System.out.println("#BDICoordInfInterpreter# Added "
																		+ coordinationSpaceObj.getProperty(Constants.VALUE).toString()
																		+ " to BELIEF_SET: " + elementId + " for " + exta.getAgentName());
																}
															} else if (elementType.equals(AgentElementType.BDI_BELIEF.toString()))
															{
																// System.out.println("BDICoordInfInterpreter# RECEIVED BELIEF TO MANIPULATE:  "
																// +
																// nameOfElement
																// + " for " +
																// exta.getAgentName());

																// check
																// direction
																// (POSITIVE ->
																// add fact to
																// belief /
																// NEGATIVE ->
																// remove fact
																// from belief):
																if (dci.getCoordinationType().equals(CoordinationType.NEGATIVE))
																{
																	// IBelief
																	// bel =
																	// exta.getBeliefbase().getBelief(metainfos[i][1]);
																	IBelief bel = exta.getBeliefbase().getBelief(elementId);
																	bel.setFact(new Object()); // TODO
																	// better
																	// default
																	// value...
																	System.out
																		.println("#BDICoordInfInterpreter# Removed fact from BELIEF : "
																			+ elementId);
																} else
																{
																	IBelief bel = exta.getBeliefbase().getBelief(elementId);
																	bel.setFact(coordinationSpaceObj.getProperty(Constants.VALUE)
																		.toString());
																	System.out.println("#BDICoordInfInterpreter# Added "
																		+ coordinationSpaceObj.getProperty(Constants.VALUE).toString()
																		+ " to BELIEF : " + elementId + " for " + exta.getAgentName());
																}
															} else if (elementType.equals(AgentElementType.BDI_GOAL.toString()))
															{
																// System.out.println("BDICoordInfInterpreter# RECEIVED GOAL TO MANIPULATE:  "
																// +
																// nameOfElement
																// + " for " +
																// exta.getAgentName());

																// check
																// direction
																// (POSITIVE ->
																// dispatch goal
																// / NEGATIVE ->
																// remove goal
																if (dci.getCoordinationType().equals(CoordinationType.NEGATIVE))
																{
																	IGoal[] goals = exta.getGoalbase().getGoals(elementId);
																	goals[goals.length - 1].drop(); // TODO:
																	// heuristic
																	// for
																	// selection
																	System.out
																		.println("#BDICoordInfInterpreter# Removed GOAL from GOALBASE : "
																			+ elementId + " for " + exta.getAgentName());
																} else
																{
																	IGoal g = exta.getGoalbase().createGoal(elementId);
																	// TODO:
																	// Implement
																	// the add
																	// parameters:
																	// add
																	// parameters:
																	for (ParameterMapping pm : ae.getParameter_mappings())
																	{
																		g.getParameter(pm.getLocalName()).setValue(
																			receivedParamDataMappings.get(pm.getRef()));
																	}
																	exta.getGoalbase().dispatchTopLevelGoal(g);
																	System.out.println("#BDICoordInfInterpreter# Dispatched new GOAL: "
																		+ elementId + " for " + exta.getAgentName());
																}

															} else if (elementType.equals(AgentElementType.BDI_PLAN.toString()))
															{
																System.out
																	.println("#BDICoordInfInterpreter# Error!!! RECEIVED PLAN TO MANIPULATE:  "
																		+ elementId
																		+ " for "
																		+ exta.getAgentName()
																		+ " Currently unable to process IPlan due limits of used jadex-version");

																// check
																// direction
																// (POSITIVE ->
																// dispatch goal
																// / NEGATIVE ->
																// remove goal
																// // if
																// (decentralCoordInfo.getCoordinationType().equals(CoordinationType.NEGATIVE))
																// {
																// // IGoal[]
																// goals =
																// exta.getGoalbase().getGoals(metainfos[i][1]);
																// //
																// goals[goals.length
																// - 1].drop();
																// // TODO:
																// heuristic for
																// selection
																// //
																// System.out.println("BDICoordInfInterpreter# Removed GOAL from GOALBASE : "
																// +
																// nameOfElement
																// + " for " +
																// exta.getAgentName());
																// // } else {
																// // IGoal g =
																// exta.getGoalbase().createGoal(nameOfElement);
																// // // TODO:
																// Implement the
																// parameters
																// add
																// parameters:
																// // // for
																// (ParameterMapping
																// pm :
																// // //
																// ca.getParameter_mappings()){
																// // //
																// g.getParameter(pm.getLocalName()).setValue(item.getValueByName(pm.getRef()));
																// // // }
																// //
																// exta.getGoalbase().dispatchTopLevelGoal(g);
																// //
																// System.out.println("BDICoordInfInterpreter# Dispatched new GOAL: "
																// +
																// nameOfElement
																// + " for " +
																// exta.getAgentName());
																// // }
																// }
															} else if (elementType.equals(AgentElementType.INTERNAL_EVENT.toString()))
															{
																// System.out.println("BDICoordInfInterpreter# RECEIVED GOAL TO MANIPULATE:  "
																// +
																// nameOfElement
																// + " for " +
																// exta.getAgentName());

																// check
																// direction
																// (POSITIVE ->
																// event
																// addition /
																// NEGATIVE ->
																// element
																// removal):
																if (dci.getCoordinationType().equals(CoordinationType.NEGATIVE))
																{
																	System.out.println(exta.getAgentName() + ":");
																	System.out
																		.println("\t ERROR: can not remove internal events from execution context.");
																	// TODO make
																	// exception
																	// for this
																	// incident.
																} else
																{
																	IInternalEvent ie = exta.getEventbase().createInternalEvent(elementId);
																	// add
																	// parameters:
																	for (ParameterMapping pm : ae.getParameter_mappings())
																	{
																		ie.getParameter(pm.getLocalName()).setValue(
																			receivedParamDataMappings.get(pm.getRef()));
																	}
																	exta.getEventbase().dispatchInternalEvent(ie);
																	System.out
																		.println("#BDICoordInfInterpreter# Dispatched new InternalEvent: "
																			+ elementId + " for " + exta.getAgentName());
																}
															}
														}
													}
												}
											}
										}
									}
									// else if
									// (COORDINATE_INIT_PARTICIPANTS.equals(metainfos[i][0]))
									// {
									// ISpaceObject coordinationSpaceObj =
									// (ISpaceObject) percept;
									//
									// if
									// (coordinationSpaceObj.getProperty(CoordinationSpaceObject.AGENT_ELEMENT_TYPE).equals(AgentElementType.INTERNAL_EVENT))
									// {
									// //
									// System.out.println("BDICoordInfInterpreter-"
									// + thisAgentType +
									// "# RECEIVED INTERNAL_EVENT TO MANIPULATE with metainfo: "
									// + metainfos[i][1]);
									// IInternalEvent ie =
									// exta.getEventbase().createInternalEvent(metainfos[i][1]);
									// HashMap values = (HashMap)
									// ((ISpaceObject)
									// percept).getProperty("VALUES");
									// ie.getParameter("deco4mas_info").setValue(values);
									// exta.getEventbase().dispatchInternalEvent(ie);
									// //
									// System.out.println("BDICoordInfInterpreter# Dispatched Internal_Event "
									// + ie.toString() + " for " +
									// exta.getAgentName());
									// }
									// }
								}
							} catch (Exception e)
							{
								e.printStackTrace();
								// try catch for the case that the agent is not
								// yet inited and
								// the belief value is not accessible
								// Todo: fix agent init.
								// Exception might be thrown, when agent not yet
								// initialized
								// -> AgentRules.findValue() fails due to
								// missing initparents,
								// when belief is initialized on demand.
								// -> AMS should not provide external access to
								// agent when not yet inited.
							}
						}
					});
				}

				@Override
				public void exceptionOccurred(Object source, Exception exception)
				{
					// TODO Auto-generated method stub

				}
			});
		}
	}

	/**
	 * 
	 */
	protected String[][] getMetaInfos(String percepttype)
	{
		if (percepttypes == null)
		{
			this.percepttypes = new HashMap();
			Object[] percepttypes = getPerceptTypes();
			for (int i = 0; i < percepttypes.length; i++)
			{
				String[] per = (String[]) percepttypes[i];
				String[][] newmis = per.length == 3 ? new String[][] { { per[1], per[2] } } : new String[][] { { per[1], per[2], per[3] } };
				String[][] oldmis = (String[][]) this.percepttypes.get(per[0]);
				if (oldmis != null)
					newmis = (String[][]) SUtil.joinArrays(oldmis, newmis);
				this.percepttypes.put(per[0], newmis);
			}
		}
		return (String[][]) percepttypes.get(percepttype);
	}

	/**
	 * Evaluate a condition.
	 * 
	 * @param exp
	 *            The expression.
	 * @param fetcher
	 *            The value fetcher.
	 */
	protected boolean evaluate(IParsedExpression exp, IValueFetcher fetcher)
	{
		boolean ret = false;
		try
		{
			ret = ((Boolean) exp.getValue(fetcher)).booleanValue();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Get the percept types defined for this generator.
	 * 
	 * @return The percept types.
	 */
	protected Object[] getPerceptTypes()
	{
		return (Object[]) getProperty(PROPERTY_PERCEPTTYPES);
	}

	private String getAgentType(IBDIExternalAccess exta)
	{
		return exta.getModel().getName();
		// return
		// exta.getApplicationContext().getAgentType(exta.getAgentIdentifier());
	}

}
