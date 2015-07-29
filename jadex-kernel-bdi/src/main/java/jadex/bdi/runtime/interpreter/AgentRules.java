package jadex.bdi.runtime.interpreter;

import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IPlanExecutor;
import jadex.bdi.runtime.impl.flyweights.CapabilityFlyweight;
import jadex.bdi.runtime.impl.flyweights.ParameterFlyweight;
import jadex.bridge.CheckedAction;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureHelper;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Static helper class for agent rules and actions.
 *  
 *  The agent rules serve the following purposes:
 *  - start agent (when agent start = creating -> initialize capability)
 *  - terminating agent (when agent finished executing end gaols/plans -> set state terminated)
 *  - terminate agent (when agent state = terminated -> cleanup / remove interpreter)
 */
public class AgentRules
{
	/** Constant for the termination timeout. */
	public static final String TERMINATION_TIMEOUT = "termination_timeout";
	
	//-------- rule methods --------

//	/**
//	 *  Create the init1 agent rule.
//	 */
//	protected static Rule createInit0AgentRule()
//	{
//		ObjectCondition	ragentcon	= new ObjectCondition(OAVBDIRuntimeModel.agent_type);
//		ragentcon.addConstraint(new BoundConstraint(null, new Variable("?ragent", OAVBDIRuntimeModel.agent_type)));
//		ragentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_state, OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING0));
//		IAction	action	= new IAction()
//		{
//			public void execute(IOAVState state, IVariableAssignments assignments)
//			{
//				final Object ragent	= assignments.getVariableValue("?ragent");
//				final BDIInterpreter ip = BDIAgentFeature.getInterpreter(state);
//				
//				// Init the external access
//				ip.ea = new ExternalAccessFlyweight(state, ragent);
//				
//				// Get the services.
//				final boolean services[]	= new boolean[3];
//				SServiceProvider.getService(ip.getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ip.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener()
//	//			SServiceProvider.getService(getServiceProvider(), IClockService.class).addResultListener(new DefaultResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						ip.clockservice	= (IClockService)result;
//						boolean	startagent;
//						synchronized(services)
//						{
//							services[0]	= true;
//							startagent	= services[0] && services[1] && services[2];// && services[3];
//						}
//						if(startagent)
//						{
//							ip.state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state,OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING1);							
//						}
//					}
//				}));
//				SServiceProvider.getService(ip.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ip.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener()
//	//			SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class).addResultListener(new DefaultResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						ip.cms	= (IComponentManagementService)result;
//						boolean	startagent;
//						synchronized(services)
//						{
//							services[1]	= true;
//							startagent	= services[0] && services[1] && services[2];// && services[3];
//						}
//						if(startagent)
//							ip.state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state,OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING1);
//					}
//				}));
//				SServiceProvider.getService(ip.getServiceProvider(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ip.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener()
//	//			SServiceProvider.getService(getServiceProvider(), IMessageService.class).addResultListener(new DefaultResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						ip.msgservice	= (IMessageService)result;
//						boolean	startagent;
//						synchronized(services)
//						{
//							services[2]	= true;
//							startagent	= services[0] && services[1] && services[2];// && services[3];
//						}
//						if(startagent)
//							ip.state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state,OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING1);
//					}
//				}));
//	
//				// Previously done in createStartAgentRule
//				Map parents = new HashMap(); 
//				state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_initparents, parents);
//				AgentRules.createCapabilityInstance(state, ragent, parents);
////				List	futures	= AgentRules.createCapabilityInstance(state, ragent, parents);
//				
////				// Start service container.
////				futures.add(ip.getServiceContainer().start());
//				
////				IResultListener	crs	= new CounterResultListener(futures.size(), new IResultListener()
////				{
////					public void resultAvailable(Object result)
////					{
////						boolean	startagent;
////						synchronized(services)
////						{
////							services[3]	= true;
////							startagent	= services[0] && services[1] && services[2] && services[3];
////						}
////						if(startagent)
////						{
////							ip.getAgentAdapter().invokeLater(new Runnable()
////							{
////								public void run()
////								{
////									ip.state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state,OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING1);
////								}
////							});
////						}
////					}
////					
////					public void exceptionOccurred(Exception exception)
////					{
////					}
////				});
////				if(!futures.isEmpty())
////				{
////					for(int i=0; i<futures.size(); i++)
////					{
////						((Future)futures.get(i)).addResultListener(crs);
////					}
////				}
//	//			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_initparents, parents);
//	
//				
//				// This is the clean way to init the logger, but since 
//				// Java 6 the LogManager is a memory leak
//				// Also in Java 7 the memory leak exists :-(
//				// So only access logger if really necessary
//	//			Logger logger = adapter.getLogger();
//	//			initLogger(ragent, logger);			
//			}
//		};
//		Rule rule = new Rule("agent_init0", ragentcon, action);
//		return rule;
//	}
	
//	/**
//	 *  Create the start agent rule.
//	 */
//	protected static Rule createInit1AgentRule()
//	{
//		ObjectCondition	ragentcon	= new ObjectCondition(OAVBDIRuntimeModel.agent_type);
//		ragentcon.addConstraint(new BoundConstraint(null, new Variable("?ragent", OAVBDIRuntimeModel.agent_type)));
//		ragentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_state, OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING1));
//		IAction	action	= new IAction()
//		{
//			public void execute(final IOAVState state, IVariableAssignments assignments)
//			{
//				final Object	ragent	= assignments.getVariableValue("?ragent");
//				final BDIInterpreter	bdii	= BDIAgentFeature.getInterpreter(state);
//				
//				initializeCapabilityInstance(state, ragent).addResultListener(
//					bdii.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(bdii.inited)
//					{
//						public void customResultAvailable(Object result)
//						{
//							state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_initparents, null);
//							
//							state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE);
//							// Remove arguments from state.
//							if(state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_arguments)!=null) 
//								state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_arguments, null);
//							
//							// When init has finished -> notify cms.
//							bdii.inited.setResult(new Object[]{bdii, bdii.getAgentAdapter()});
//						}
//					})
//				);
//			}
//		};
//		Rule rule = new Rule("agent_init1", ragentcon, action);
//		return rule;
//	}

	/**
	 *  Terminating start agent rule. Exits running and e
	 *  Should ensures that the agent is idle and then transfer its
	 *  state to terminated.
	 * /
	protected static Rule createTerminatingStartAgentRule()
	{
		// Terminating starts when state is set to terminating.
		
		Variable ragent = new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		
		ObjectCondition ragentcon = new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		ragentcon.addConstraint(new BoundConstraint(null, ragent));
		ragentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_state, 
			OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATING));
		
		IAction	action	= new IAction()
		{
			public void execute(final IOAVState state, IVariableAssignments assignments)
			{
//				System.out.println("Terminating started");
				final Object ragent = assignments.getVariableValue("?ragent");
				
				startTerminating(state, ragent);
			}
		};
		
		Rule agent_terminating	= new Rule("agent_terminating_start", ragentcon, action);
		return agent_terminating;
	}*/
	
	/**
	 *  Terminating end agent rule. Sets state to terminated.
	 *  Should ensures that the agent is idle and then transfer its
	 *  state to terminated.
	 */
	public static Rule createTerminatingEndAgentRule()
	{
		// Terminating is finished when state==terminating and no more
		// goals and plans to do.
		
		Variable ragent = new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		
		ObjectCondition ragentcon = new ObjectCondition(ragent.getType());
		ragentcon.addConstraint(new BoundConstraint(null, ragent));
		ragentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_state, 
			OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATING));
		
		ObjectCondition rgoalcon = new ObjectCondition(rgoal.getType());
		rgoalcon.addConstraint(new BoundConstraint(null, rgoal));

		ObjectCondition rcapacon1 = new ObjectCondition(rcapa.getType());
		rcapacon1.addConstraint(new BoundConstraint(null, rcapa));
		rcapacon1.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));

		ObjectCondition rplancon = new ObjectCondition(rplan.getType());
		rplancon.addConstraint(new BoundConstraint(null, rplan));

		ObjectCondition rcapacon2 = new ObjectCondition(rcapa.getType());
		rcapacon2.addConstraint(new BoundConstraint(null, rcapa));
		rcapacon2.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
//				System.out.println("Terminating ended");
				
				Object	ragent	= assignments.getVariableValue("?ragent");
				ITimer termination_timer = (ITimer)state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_timer);
				if(termination_timer!=null)
				{
					termination_timer.cancel();
					state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_timer, null);
				}
				
				state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, 
					OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATED);
			}
		};
		
		Rule agent_terminating	= new Rule("agent_terminating_end",
			new AndCondition(new ICondition[]{ragentcon,
				new NotCondition(new AndCondition(new ICondition[]{rgoalcon, rcapacon1})),
				new NotCondition(new AndCondition(new ICondition[]{rplancon, rcapacon2}))}), action);
		return agent_terminating;
	}

	/**
	 *  Terminate agent rule.
	 *  Terminates the agent and removes it from the platform.
	 */
	public static Rule createTerminateAgentRule()
	{
		ObjectCondition ragentcon	= new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		ragentcon.addConstraint(new BoundConstraint(null, new Variable("?ragent", OAVBDIRuntimeModel.agent_type)));
		ragentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_state, 
			OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATED));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object ragent = assignments.getVariableValue("?ragent");
				cleanupAgent(state, ragent);
			}
		};
		
		Rule agent_terminating	= new Rule("agent_terminated", ragentcon, action);
		return agent_terminating;
	}
	
	/**
	 *  Cleanup timers of a capability and its subcapabilities.
	 *  @param rcapa The capability.
	 */
	public static void cleanupCapability(IOAVState state, Object rcapa)
	{
		// Cleanup subcapabilities
		Collection subcaps = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities);
		if(subcaps!=null)
		{
			for(Iterator it=subcaps.iterator(); it.hasNext(); )
			{
				Object subcapref = it.next();
				Object subcap = state.getAttributeValue(subcapref, OAVBDIRuntimeModel.capabilityreference_has_capability);
				cleanupCapability(state, subcap);
			}
		}
		
		// Cleanup plans.
		Collection plans = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_plans);
//		System.out.println("here: "+rcapa+" "+plans);
		if(plans!=null)
		{
			for(Iterator it=plans.iterator(); it.hasNext(); )
			{
				Object rplan = it.next();
				IPlanExecutor executor = BDIAgentFeature.getInterpreter(state).getPlanExecutor(rplan);
				if(executor!=null)
					executor.cleanup(BDIAgentFeature.getInternalAccess(state), rplan);
				PlanRules.cleanupPlanWait(state, rcapa, rplan, true);
			}
		}
		
		// Cleanup precandidates (required to break cycles in runtime state).
		Collection precands = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_precandidates);
		if(precands!=null)
		{
			Object[]	aprecands	= precands.toArray();
			for(int i=0; i<aprecands.length; i++)
			{
				Object	key	= state.getAttributeValue(aprecands[i], OAVBDIRuntimeModel.capability_has_precandidates.getIndexAttribute());
				state.removeAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_precandidates, key);
			}
		}
		
		// External accesses must be reactivated, otherwise external threads would sleep forever.
		Collection extas = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_externalaccesses);
		if(extas!=null)
		{
			for(Iterator it=extas.iterator(); it.hasNext(); )
			{
				Object exta = it.next();
				ITimer timer = (ITimer)state.getAttributeValue(exta, OAVBDIRuntimeModel.externalaccess_has_timer);
				if(timer!=null)
					timer.cancel();
				Runnable wact = (Runnable)state.getAttributeValue(exta, OAVBDIRuntimeModel.externalaccess_has_wakeupaction);
				if(wact!=null)
					wact.run();
			}
		}
		
		// Cleanup timers of updaterate beliefs.
		Collection bels = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_beliefs);
		if(bels!=null)
		{
			for(Iterator it=bels.iterator(); it.hasNext(); )
			{
				Object bel = it.next();
				ITimer timer = (ITimer)state.getAttributeValue(bel, OAVBDIRuntimeModel.typedelement_has_timer);
				if(timer!=null)
					timer.cancel();
			}
		}
		
		// Cleanup timers of updaterate beliefsets.
		Collection belsets = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_beliefsets);
		if(belsets!=null)
		{
			for(Iterator it=belsets.iterator(); it.hasNext(); )
			{
				Object belset = it.next();
				ITimer timer = (ITimer)state.getAttributeValue(belset, OAVBDIRuntimeModel.typedelement_has_timer);
				if(timer!=null)
					timer.cancel();
			}
		}
		
		// Agent timer.
		if(state.getType(rcapa).equals(OAVBDIRuntimeModel.agent_type))
		{
			ITimer timer = (ITimer)state.getAttributeValue(rcapa, OAVBDIRuntimeModel.agent_has_timer);
			if(timer!=null)
				timer.cancel();
		}
	}

	/**
	 *  Execute an external action.
	 */
	public static Rule createExecuteActionRule()
	{
		Variable	com	= new Variable("?step", OAVJavaType.java_object_type);
		Variable	ragent	= new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		
		ObjectCondition actioncon = new ObjectCondition(com.getType()); 
		actioncon.addConstraint(new BoundConstraint(null, com));
		
		ObjectCondition ragentcon = new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		ragentcon.addConstraint(new BoundConstraint(null, ragent));
		ragentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_state, 
			OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATED, IOperator.NOTEQUAL));
		ragentcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.agent_has_actions, com, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object ragent = assignments.getVariableValue("?ragent");
				Object[] step = (Object[])assignments.getVariableValue("?step");
//				System.out.println("Executing external action: "+step[0]);
				state.removeAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_actions, step);
				
				Future res = (Future)((Object[])step)[1];
				try
				{
					if(step[0] instanceof CheckedAction)
					{
						CheckedAction ca = (CheckedAction)step[0];
						if(ca.isValid())
							ca.run();
						ca.cleanup();
						res.setResult(null);
					}
					else if(step[0] instanceof Runnable)
					{
						((Runnable)step[0]).run();
						res.setResult(null);
					}
					else if(step[0] instanceof IComponentStep)
					{
						IComponentStep st = (IComponentStep)((Object[])step)[0];
						IFuture r = st.execute(new CapabilityFlyweight(state, step[2]));
						FutureFunctionality.connectDelegationFuture(res, (IFuture)r);
					}
				}
				catch(RuntimeException e)
				{
					res.setException(e);
					throw e;
				}
			}
		};
		
		Rule agent_execute_action	= new Rule("agent_execute_action", new AndCondition(new ICondition[]{actioncon, ragentcon}), action);
		return agent_execute_action;
	}

	/**
	 *  Cleanup rule for removing change events.
	 */
	public static Rule createRemoveChangeEventRule()
	{
		Variable ragent = new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		Variable changeevent = new Variable("?changeevent", OAVBDIRuntimeModel.changeevent_type);

		ObjectCondition changecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, changeevent));
		
		ObjectCondition ragentcon = new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		ragentcon.addConstraint(new BoundConstraint(null, ragent));
		ragentcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.agent_has_changeevents, changeevent, IOperator.CONTAINS));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object changeevent = assignments.getVariableValue("?changeevent");
				Object ragent = assignments.getVariableValue("?ragent");
				state.removeAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_changeevents, changeevent);
//				System.err.println("removing: "+changeevent+", "+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier());
			}
		};
		
		Rule rule = new Rule("changeevent_remove", new AndCondition(new ICondition[]{changecon, ragentcon}), action);
		return rule;
	}

	
	//-------- helper methods --------

	/**
	 *  Initialize the runtime state of an agent.
	 *  @param state The state.
	 *  @param rcapa The reference to the capability instance.
	 *  @param inivals Initial values for beliefs (e.g. arguments or config elements from outer capability);
	 * /
	protected static void initializeCapabilityInstance(final IOAVState state, final Object rcapa, Map arguments)
	{
		// Get configuration.
		String	config	= (String)state.getAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_configuration);
		Object	mcapa	= state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
		Object	mconfig;
		if(config==null)
		{
			mconfig	= state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_defaultconfiguration);
			if(mconfig==null)
			{
				Collection	mconfigs	= state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_configurations);
				if(mconfigs!=null)
					mconfig	= mconfigs.iterator().next();
				else
					mconfig	= null;
			}
		}
		else
		{
			if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_configurations, config))
				throw new RuntimeException("No such configuration: "+config);
			mconfig	= state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_configurations, config);
		}
		
		// Initialize properties.
		Collection	mprops = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_properties);
		if(mprops!=null)
		{
			for(Iterator it=mprops.iterator(); it.hasNext(); )
			{
				Object mexp = it.next();
				String name = (String)state.getAttributeValue(mexp, OAVBDIMetaModel.modelelement_has_name);
				Object val = evaluateExpression(state, mexp, new OAVBDIFetcher(state, rcapa));
				Object param = state.createObject(OAVBDIRuntimeModel.parameter_type);	
				state.setAttributeValue(param, OAVBDIRuntimeModel.parameter_has_name, name);
				state.setAttributeValue(param, OAVBDIRuntimeModel.parameter_has_value, val);
				state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_properties, param);
//				System.out.println("Property: "+name+" "+val);
			}
		}
		// Hack? Add kernelprops to agent properties.
		if(state.getType(mcapa).isSubtype(OAVBDIMetaModel.agent_type))
		{
			Map kernelprops = BDIAgentFeature.getInterpreter(state).getKernelProperties();
			for(Iterator it=kernelprops.keySet().iterator(); it.hasNext(); )
			{
				String name = (String)it.next();
				Object val = kernelprops.get(name);
				Object param = state.createObject(OAVBDIRuntimeModel.parameter_type);	
				state.setAttributeValue(param, OAVBDIRuntimeModel.parameter_has_name, name);
				state.setAttributeValue(param, OAVBDIRuntimeModel.parameter_has_value, val);
				state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_properties, param);
			}
		}
		
		// Hack!!! cache expression parameters?
		final OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rcapa);

		Map inivals = collectInitialValues(state, mcapa, mconfig, arguments, fetcher);
		if(inivals!=null && arguments==null)
			arguments	= (Map)inivals.get("");
		
		// Initialize subcapabilities.
		Collection	mcaps	= state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs);
		if(mcaps!=null)
		{
			for(Iterator it=mcaps.iterator(); it.hasNext(); )
			{
				Object	mcaparef	= it.next();
				String	name	= (String)state.getAttributeValue(mcaparef, OAVBDIMetaModel.modelelement_has_name);
				Object	msubcapa	= state.getAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_capability);
				
				Object	inicap	= null;
				if(mconfig!=null)
				{
					Collection	inicaps	= state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialcapabilities);
					if(inicaps!=null)
					{
						for(Iterator it2=inicaps.iterator(); inicap==null && it2.hasNext(); )
						{
							Object	tmp	= it2.next();
							if(state.getAttributeValue(tmp, OAVBDIMetaModel.initialcapability_has_ref)==mcaparef)
							{
								inicap	= tmp;
							}
						}
					}
				}

				Object	rsubcapa	= state.createObject(OAVBDIRuntimeModel.capability_type);
				state.setAttributeValue(rsubcapa, OAVBDIRuntimeModel.element_has_model, msubcapa);
				if(inicap!=null)
				{
					String	conf	= (String)state.getAttributeValue(inicap, OAVBDIMetaModel.initialcapability_has_configuration);
					state.setAttributeValue(rsubcapa, OAVBDIRuntimeModel.capability_has_configuration, conf);
				}
				Object	rcaparef	= state.createObject(OAVBDIRuntimeModel.capabilityreference_type);
				state.setAttributeValue(rcaparef, OAVBDIRuntimeModel.capabilityreference_has_name, name);
				state.setAttributeValue(rcaparef, OAVBDIRuntimeModel.capabilityreference_has_capability, rsubcapa);
				state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities, rcaparef);
				initializeCapabilityInstance(state, rsubcapa, inivals!=null ? (Map)inivals.get(name) : null);
			}
		}
		
		// Initialize beliefs.
		Collection	mbels = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_beliefs);
		if(mbels!=null)
		{
			for(Iterator it=mbels.iterator(); it.hasNext(); )
			{
				final Object mbel	= it.next();
				
				final Object rbel = state.createObject(OAVBDIRuntimeModel.belief_type);
				state.setAttributeValue(rbel, OAVBDIRuntimeModel.element_has_model, mbel);
				state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_beliefs, rbel);
				Object	evamode = state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_evaluationmode);
				final Long	update	= (Long)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_updaterate);

				boolean	found	= false;
				Object	value	= null;

				// Search for argument (includes config value).
				if(arguments!=null)
				{
					String	name =(String)state.getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name);
					if(arguments.containsKey(name))
					{
						value	= arguments.get(name);
						found	= true;
					}
				}

				// Set a value if the belief is static (or first value for update rate).
				if(OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode) || update!=null)
				{
					// Search for default value.
					if(!found)
					{
						Object	exp = state.getAttributeValue(mbel, OAVBDIMetaModel.belief_has_fact);
						if(exp!=null)
						{
							value	= evaluateExpression(state, exp, fetcher);
							found	= true;
						}
					}

					// Set value to default for basic type.
					if(!found)
					{
						Class clazz = (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
						value = BeliefRules.getInitialValue(clazz);
						if(value!=null)
							found = true;
					}
					
					// Set and register value.
					if(found)
					{
						BeliefRules.setBeliefValue(state, rbel, value);
					}
				}

				if(update!=null)
				{
					final ITimedObject[]	to	= new ITimedObject[1];
					to[0] = new InterpreterTimedObject(state, new InterpreterTimedObjectAction()
					{
						public void run()
						{
							Object	exp = state.getAttributeValue(mbel, OAVBDIMetaModel.belief_has_fact);
							Object value = evaluateExpression(state, exp, fetcher);
							BeliefRules.setBeliefValue(state, rbel, value);
//							// changed *.class to *.TYPE due to javaflow bug
							state.setAttributeValue(rbel, OAVBDIRuntimeModel.typedelement_has_timer, 
								((IClockService)BDIAgentFeature.getInterpreter(state).getAgentAdapter().getPlatform()
								.getService(IClockService.TYPE)).createTimer(update.longValue(), to[0]));
						}
					});
					
//					// changed *.class to *.TYPE due to javaflow bug
					state.setAttributeValue(rbel, OAVBDIRuntimeModel.typedelement_has_timer, 
						((IClockService)BDIAgentFeature.getInterpreter(state).getAgentAdapter().getPlatform()
						.getService(IClockService.TYPE)).createTimer(update.longValue(), to[0]));
				}
			}
		}
			
		Collection	mbelsets	= state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_beliefsets);
		if(mbelsets!=null)
		{
			for(Iterator it=mbelsets.iterator(); it.hasNext(); )
			{
				final Object mbelset	= it.next();
				if(state.getType(mbelset).isSubtype(OAVBDIMetaModel.beliefset_type))
				{
					final Object rbelset = state.createObject(OAVBDIRuntimeModel.beliefset_type);
					state.setAttributeValue(rbelset, OAVBDIRuntimeModel.element_has_model, mbelset);
					state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_beliefsets, rbelset);
					Object	evamode = state.getAttributeValue(mbelset, OAVBDIMetaModel.typedelement_has_evaluationmode);
					final Long	update	= (Long)state.getAttributeValue(mbelset, OAVBDIMetaModel.typedelement_has_updaterate);
					
					boolean	found	= false;
					Object	values	= null;
	
					// Search for argument (includes config values).
					if(arguments!=null)
					{
						String	name =(String)state.getAttributeValue(mbelset, OAVBDIMetaModel.modelelement_has_name);
						if(arguments.containsKey(name))
						{
							values	= arguments.get(name);
							found	= true;
						}
					}
	
					// Search for default values.
					if(!found)
					{
						Collection	facts	= state.getAttributeValues(mbelset, OAVBDIMetaModel.beliefset_has_facts);
						if(facts!=null)
						{
							List	lvalues	= new ArrayList();
							for(Iterator it2=facts.iterator(); it2.hasNext(); )
							{
								Object	fact	= it2.next();
								lvalues.add(evaluateExpression(state, fact, fetcher));
							}
							values	= lvalues;
							found	= true;
						}
					}
	
					// Set a value if the beliefSET is static (or first value for update rate).
					if(OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode) || update!=null)
					{
						// Search for default values expression.
						if(!found)
						{
							Object	exp	= state.getAttributeValue(mbelset, OAVBDIMetaModel.beliefset_has_factsexpression);
							if(exp!=null)
							{
								List	lvalues	= new ArrayList();
								Object	deffacts	= evaluateExpression(state, exp, fetcher);
								for(Iterator it2=SReflect.getIterator(deffacts); it2.hasNext(); )
								{
									lvalues.add(it2.next());
								}
								values	= lvalues;
								found	= true;
							}
						}
		
						// Set and register value.
						if(found)
						{
							for(Iterator it2=SReflect.getIterator(values); it2.hasNext(); )
							{
								Object	value	= it2.next();
								BeliefRules.addBeliefSetValue(state, rbelset, value);
							}
						}
					}
					else if(found)
					{
						throw new RuntimeException("Initial or argument values not supported for dynamic beliefset: "
							+ state.getAttributeValue(mbelset, OAVBDIMetaModel.modelelement_has_name));
					}

					if(update!=null)
					{
						final ITimedObject[]	to	= new ITimedObject[1];
						
						to[0]	= new InterpreterTimedObject(state, new InterpreterTimedObjectAction()
						{
							public void run()
							{
								Object	exp = state.getAttributeValue(mbelset, OAVBDIMetaModel.beliefset_has_factsexpression);
								Object values	= evaluateExpression(state, exp, fetcher);
								BeliefRules.updateBeliefSet(state, rbelset, values);
//								// changed *.class to *.TYPE due to javaflow bug
								state.setAttributeValue(rbelset, OAVBDIRuntimeModel.typedelement_has_timer, ((IClockService)BDIAgentFeature.getInterpreter(state)
									.getAgentAdapter().getPlatform().getService(IClockService.TYPE)).createTimer(update.longValue(), to[0]));
							}
						});
//						// changed *.class to *.TYPE due to javaflow bug
						state.setAttributeValue(rbelset, OAVBDIRuntimeModel.typedelement_has_timer, ((IClockService)BDIAgentFeature.getInterpreter(state)
							.getAgentAdapter().getPlatform().getService(IClockService.TYPE)).createTimer(update.longValue(), to[0]));
					}
				}
			}
		}
		
		// Initialize mplan triggers (cached for speed and simplicity of rules).
		Collection	mplans	= state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_plans);
		if(mplans!=null)
		{
			for(Iterator it=mplans.iterator(); it.hasNext(); )
			{
				Object	mplan	= it.next();
				Object	trigger	= state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_trigger);
				if(trigger!=null)
				{
					Collection	triggerrefs	= state.getAttributeValues(trigger, OAVBDIMetaModel.plantrigger_has_goals);
					if(triggerrefs!=null)
					{
						for(Iterator it2=triggerrefs.iterator(); it2.hasNext(); )
						{
							Object	triggerref	= it2.next();
							String	ref	= (String)state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_ref);
							Object[] scope = resolveCapability(ref, OAVBDIMetaModel.goal_type, rcapa, state);
							Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
							Object	precand	= state.createObject(OAVBDIRuntimeModel.precandidate_type);
							state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_mplan, mplan);
							state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_capability, rcapa);
							state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_triggerreference, triggerref);
	
							Object	mpe	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_goals, scope[0]);
							if(mpe==null)
								throw new RuntimeException("Cannot resolve plan trigger: "+ref);
							Object	precandlist	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_precandidates, mpe);
							if(precandlist==null)
							{
								precandlist	= state.createObject(OAVBDIRuntimeModel.precandidatelist_type);
								state.setAttributeValue(precandlist, OAVBDIRuntimeModel.precandidatelist_has_processableelement, mpe);
								state.addAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_precandidates, precandlist);
							}
							state.addAttributeValue(precandlist, OAVBDIRuntimeModel.precandidatelist_has_precandidates, precand);
						}
					}
	
					triggerrefs	= state.getAttributeValues(trigger, OAVBDIMetaModel.trigger_has_internalevents);
					if(triggerrefs!=null)
					{
						for(Iterator it2=triggerrefs.iterator(); it2.hasNext(); )
						{
							Object	triggerref	= it2.next();
							String	ref	= (String)state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_ref);
							Object[]	scope	= resolveCapability(ref, OAVBDIMetaModel.internalevent_type, rcapa, state);
							Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
							Object	precand	= state.createObject(OAVBDIRuntimeModel.precandidate_type);
							state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_mplan, mplan);
							state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_capability, rcapa);
							state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_triggerreference, triggerref);
	
							Object	mpe	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_internalevents, scope[0]);
							if(mpe==null)
								throw new RuntimeException("Cannot resolve plan trigger: "+ref);
							Object	precandlist	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_precandidates, mpe);
							if(precandlist==null)
							{
								precandlist	= state.createObject(OAVBDIRuntimeModel.precandidatelist_type);
								state.setAttributeValue(precandlist, OAVBDIRuntimeModel.precandidatelist_has_processableelement, mpe);
								state.addAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_precandidates, precandlist);
							}
							state.addAttributeValue(precandlist, OAVBDIRuntimeModel.precandidatelist_has_precandidates, precand);
						}
					}
	
					triggerrefs	= state.getAttributeValues(trigger, OAVBDIMetaModel.trigger_has_messageevents);
					if(triggerrefs!=null)
					{
						for(Iterator it2=triggerrefs.iterator(); it2.hasNext(); )
						{
							Object	triggerref	= it2.next();
							String	ref	= (String)state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_ref);
							Object[]	scope	= resolveCapability(ref, OAVBDIMetaModel.messageevent_type, rcapa, state);
							Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
							Object	precand	= state.createObject(OAVBDIRuntimeModel.precandidate_type);
							state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_mplan, mplan);
							state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_capability, rcapa);
							state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_triggerreference, triggerref);
	
							Object	mpe	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_messageevents, scope[0]);
							if(mpe==null)
								throw new RuntimeException("Cannot resolve plan trigger: "+ref);
							Object	precandlist	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_precandidates, mpe);
							if(precandlist==null)
							{
								precandlist	= state.createObject(OAVBDIRuntimeModel.precandidatelist_type);
								state.setAttributeValue(precandlist, OAVBDIRuntimeModel.precandidatelist_has_processableelement, mpe);
								state.addAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_precandidates, precandlist);
							}
							state.addAttributeValue(precandlist, OAVBDIRuntimeModel.precandidatelist_has_precandidates, precand);
						}
					}
				}
			}
		}

		if(mconfig!=null)
		{	
			// Create initial goals.
			Collection	cgoals	= state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialgoals);
			if(cgoals!=null)
			{
				for(Iterator it=cgoals.iterator(); it.hasNext(); )
				{
					createConfigGoal(state, rcapa, it.next(), fetcher);
				}
			}
			
			// Create initial plans.
			Collection	cplans	= state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialplans);
			if(cplans!=null)
			{
				for(Iterator it=cplans.iterator(); it.hasNext(); )
				{
					createConfigPlan(state, rcapa, mcapa, it.next(), fetcher);
				}
			}
	
			// Create initial message events.
			Collection	cmevents = state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialmessagevents);
			if(cmevents!=null)
			{
				for(Iterator it=cmevents.iterator(); it.hasNext(); )
				{
					createConfigMessageEvent(state, rcapa, it.next(), fetcher);
				}
			}
			
			// Create initial internal events.
			Collection	cievents = state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialinternalevents);
			if(cievents!=null)
			{
				for(Iterator it=cievents.iterator(); it.hasNext(); )
				{
					createConfigInternalEvent(state, rcapa, it.next(), fetcher);
				}
			}
		}
	}*/

	/**
	 *  Initialize the runtime state of an agent.
	 *  @param state The state.
	 *  @param rcapa The reference to the capability instance.
	 *  @param inivals Initial values for beliefs (e.g. arguments or config elements from outer capability);
	 */
	public static void	createCapabilityInstance(final IOAVState state, final Object rcapa, Map parents)//, Map arguments)
	{
//		List	futures	= new ArrayList();
		
		// Get configuration.
		Object	mcapa	= state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
		Object	mconfig = getConfiguration(state, rcapa);
		
//		// Initialize properties.
//		Collection	mprops = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_properties);
//		if(mprops!=null)
//		{
//			for(Iterator it=mprops.iterator(); it.hasNext(); )
//			{
//				Object mexp = it.next();
//				final String name = (String)state.getAttributeValue(mexp, OAVBDIMetaModel.modelelement_has_name);
//				Object	val	= evaluateExpression(state, mexp, new OAVBDIFetcher(state, rcapa));
//				Class	clazz	= (Class)state.getAttributeValue(mexp, OAVBDIMetaModel.expression_has_class);
//				if(clazz!=null && SReflect.isSupertype(IFuture.class, clazz))
//				{
////					System.out.println("Future property: "+name+" "+val);
//					if(val instanceof IFuture)
//					{
//						// Use second future to start agent only when value has already been set.
//						final Future	ret	= new Future();
//						((IFuture)val).addResultListener(BDIAgentFeature.getInterpreter(state).getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
//						{
//							public void resultAvailable(Object result)
//							{
////								System.out.println("Setting future property: "+name+" "+result);
//								Object param = state.createObject(OAVBDIRuntimeModel.parameter_type);	
//								state.setAttributeValue(param, OAVBDIRuntimeModel.parameter_has_name, name);
//								state.setAttributeValue(param, OAVBDIRuntimeModel.parameter_has_value, result);
//								state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_properties, param);
//								ret.setResult(result);
//							}
//
//							public void exceptionOccurred(Exception exception)
//							{
//								throw new RuntimeException(exception);
//							}
//						}));
//						futures.add(ret);
//					}
//					else if(val!=null)
//					{
//						throw new RuntimeException("Future property must evaluate to object of type jadex.commons.Future: "+name+", "+val);
//					}
//				}
//				else
//				{
//					Object param = state.createObject(OAVBDIRuntimeModel.parameter_type);	
//					state.setAttributeValue(param, OAVBDIRuntimeModel.parameter_has_name, name);
//					state.setAttributeValue(param, OAVBDIRuntimeModel.parameter_has_value, val);
//					state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_properties, param);
////					System.out.println("Property: "+name+" "+val);
//				}
//			}
//		}
//		// Hack? Add kernelprops to agent properties.
//		if(state.getType(mcapa).isSubtype(OAVBDIMetaModel.agent_type))
//		{
//			Map kernelprops = BDIAgentFeature.getInterpreter(state).getKernelProperties();
//			for(Iterator it=kernelprops.keySet().iterator(); it.hasNext(); )
//			{
//				String name = (String)it.next();
//				Object val = kernelprops.get(name);
//				Object param = state.createObject(OAVBDIRuntimeModel.parameter_type);	
//				state.setAttributeValue(param, OAVBDIRuntimeModel.parameter_has_name, name);
//				state.setAttributeValue(param, OAVBDIRuntimeModel.parameter_has_value, val);
//				state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_properties, param);
//			}
//		}
		
		// Initialize services.
		
//		// todo: connect services of capabilities, name them accordingly
//		Collection	mservices = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_providedservices);
//		if(mservices!=null)
//		{
//			for(Iterator it=mservices.iterator(); it.hasNext(); )
//			{
//				Object mpro = it.next();
//				
//				try
//				{
//					Class type = (Class)state.getAttributeValue(mpro, OAVBDIMetaModel.providedservice_has_class);
//					String proxytype = (String)state.getAttributeValue(mpro, OAVBDIMetaModel.providedservice_has_proxytype);
//					Object	mexp	= state.getAttributeValue(mpro, OAVBDIMetaModel.providedservice_has_implementation);
//					IParsedExpression pex = (IParsedExpression)state.getAttributeValue(mexp, OAVBDIMetaModel.expression_has_parsed);
//					Class impl = (Class)state.getAttributeValue(mexp, OAVBDIMetaModel.expression_has_class);
//					Object ser = null;
//					if(pex!=null)
//					{
//						ser = evaluateExpression(state, mexp, new OAVBDIFetcher(state, rcapa));
//					}
//					// object.class is set als deafult of expression attribute class in OAVBDIMetamodel 
//					else if(impl!=null && !Object.class.equals(impl))
//					{
//						ser = impl.newInstance();
//					}
//					
//					if(ser!=null)
//					{
//						BDIAgentFeature.getInterpreter(state).addService(type, ser, proxytype);
//					}
//					else
//					{
//						BDIAgentFeature.getInterpreter(state).getAgentAdapter().getLogger().warning("Service creation error: "+state.getAttributeValue(mexp, OAVBDIMetaModel.expression_has_text));
//					}
//				}
//				catch(Exception e)
//				{
////					e.printStackTrace();
//					BDIAgentFeature.getInterpreter(state).getAgentAdapter().getLogger().warning("Service creation error: "+state.getAttributeValue(mpro, OAVBDIMetaModel.providedservice_has_classname));
//				}
				
//				try
//				{
////					String name = (String)state.getAttributeValue(mexp, OAVBDIMetaModel.modelelement_has_name);
//					IInternalService val = (IInternalService)evaluateExpression(state, mexp, new OAVBDIFetcher(state, rcapa));
////					Class type = (Class)state.getAttributeValue(mexp, OAVBDIMetaModel.expression_has_class);
//					// cast hack?!
//					Boolean direct = (Boolean)state.getAttributeValue(mexp, OAVBDIMetaModel.providedservice_has_direct);
//					if(!direct.booleanValue())
//					{
//						val = DecouplingServiceInvocationInterceptor.createServiceProxy(BDIAgentFeature.getInterpreter(state).getExternalAccess(), BDIAgentFeature.getInterpreter(state).getAgentAdapter(), val);
////						System.out.println("Created decoupled service: "+val);
//					}
//					((IServiceContainer)BDIAgentFeature.getInterpreter(state).getServiceProvider()).addService(val);
////					System.out.println("Service: "+name+" "+val+" "+type);
//				}
//				catch(Exception e)
//				{
////					e.printStackTrace();
//					BDIAgentFeature.getInterpreter(state).getAgentAdapter().getLogger().warning("Service creation error: "+state.getAttributeValue(mexp, OAVBDIMetaModel.expression_has_text));
//				}
//			}
//		}
		
		// Create subcapabilities.
		Collection	mcaps	= state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs);
		if(mcaps!=null)
		{
			for(Iterator it=mcaps.iterator(); it.hasNext(); )
			{
				Object	mcaparef	= it.next();
				String	name	= (String)state.getAttributeValue(mcaparef, OAVBDIMetaModel.modelelement_has_name);
				Object	msubcapa	= state.getAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_capability);
				
				Object inicap = getInitialCapability(state, mcapa, mconfig,	mcaparef);

				Object	rsubcapa	= state.createObject(OAVBDIRuntimeModel.capability_type);
				state.setAttributeValue(rsubcapa, OAVBDIRuntimeModel.element_has_model, msubcapa);
				if(inicap!=null)
				{
					String	conf = (String)state.getAttributeValue(inicap, OAVBDIMetaModel.initialcapability_has_configuration);
					state.setAttributeValue(rsubcapa, OAVBDIRuntimeModel.capability_has_configuration, conf);
				}
				Object rcaparef = state.createObject(OAVBDIRuntimeModel.capabilityreference_type);
				state.setAttributeValue(rcaparef, OAVBDIRuntimeModel.capabilityreference_has_name, name);
				state.setAttributeValue(rcaparef, OAVBDIRuntimeModel.capabilityreference_has_capability, rsubcapa);
				state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities, rcaparef);
				parents.put(rsubcapa, rcapa);
				createCapabilityInstance(state, rsubcapa, parents);//, null);//inivals!=null ? (Map)inivals.get(name) : null);
//				List	fs	= createCapabilityInstance(state, rsubcapa, parents);//, null);//inivals!=null ? (Map)inivals.get(name) : null);
//				futures.addAll(fs);
			}
		}
		
//		return futures;
	}

	/**
	 *  Get the initial capability (if any) from the configuration.
	 */
	public static Object getInitialCapability(IOAVState state, Object mcapa, Object mconfig, Object mcaparef)
	{
		Object	inicap	= null;
		if(mconfig!=null)
		{
			Collection	inicaps	= state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialcapabilities);
			if(inicaps!=null)
			{
				for(Iterator it2=inicaps.iterator(); inicap==null && it2.hasNext(); )
				{
					Object	tmp	= it2.next();
					String	refname	= (String)state.getAttributeValue(tmp, OAVBDIMetaModel.initialcapability_has_ref);
					Object	iniref	= state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs, refname);
					if(iniref==mcaparef)
					{
						inicap	= tmp;
					}
				}
			}
		}
		return inicap;
	}
	
	/**
	 *  Initialize the runtime state of an agent.
	 *  @param state The state.
	 *  @param rcapa The reference to the capability instance.
	 *  @param inivals Initial values for beliefs (e.g. arguments or config elements from outer capability);
	 */
	public static IFuture<Void>	initializeCapabilityInstance(final IOAVState state, final Object rcapa)
	{
		final Future	ret	= new Future();
		
		// Get configuration.
		final Object	mcapa	= state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
//		System.out.println("iCI "+state.getAttributeValue(mcapa, OAVBDIMetaModel.modelelement_has_name)+", "+rcapa);
		final Object	mconfig = getConfiguration(state, rcapa);
		
//		// Hack!!! cache expression parameters?
		
		final OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rcapa, BDIAgentFeature.getInternalAccess(state).getFetcher());
		
//		InitFetcher fet = new InitFetcher(state, rcapa, parents, arguments);

		// Register belief(set) types for mplan triggers (must be done before beliefs are initialized).
		final Collection	mplans	= state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_plans);
		if(mplans!=null)
		{
			for(Iterator it=mplans.iterator(); it.hasNext(); )
			{
				Object	mplan	= it.next();
				Object	trigger	= state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_trigger);
				if(trigger!=null)
				{
					Collection	bels	= state.getAttributeValues(trigger, OAVBDIMetaModel.trigger_has_factchangeds);
					if(bels!=null)
					{
						for(Iterator it2=bels.iterator(); it2.hasNext(); )
						{
							String	ref	= (String)it2.next();
							Object[]	scope	= resolveCapability(ref, null, rcapa, state);
							Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
							Object	mbel	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefs, scope[0]);
							if(mbel==null)
								mbel	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
							if(mbel==null)
								throw new RuntimeException("Cannot resolve plan trigger: "+ref);
							BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(mbel);
						}
					}
					
					bels	= state.getAttributeValues(trigger, OAVBDIMetaModel.trigger_has_factaddeds);
					if(bels!=null)
					{
						for(Iterator it2=bels.iterator(); it2.hasNext(); )
						{
							String	ref	= (String)it2.next();
							Object[]	scope	= resolveCapability(ref, null, rcapa, state);
							Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
							Object	mbel	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
							if(mbel==null)
								throw new RuntimeException("Cannot resolve plan trigger: "+ref);
							BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(mbel);
						}
					}

					bels	= state.getAttributeValues(trigger, OAVBDIMetaModel.trigger_has_factremoveds);
					if(bels!=null)
					{
						for(Iterator it2=bels.iterator(); it2.hasNext(); )
						{
							String	ref	= (String)it2.next();
							Object[]	scope	= resolveCapability(ref, null, rcapa, state);
							Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
							Object	mbel	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
							if(mbel==null)
								throw new RuntimeException("Cannot resolve plan trigger: "+ref);
							BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(mbel);
						}
					}

					Collection	triggerrefs	= state.getAttributeValues(trigger, OAVBDIMetaModel.trigger_has_goalfinisheds);
					if(triggerrefs!=null)
					{
						for(Iterator it2=triggerrefs.iterator(); it2.hasNext(); )
						{
							Object	triggerref	= it2.next();
							String	ref	= (String)state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_ref);
							Object[]	scope	= resolveCapability(ref, OAVBDIMetaModel.internalevent_type, rcapa, state);
							Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
							Object	mgoal	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_goals, scope[0]);
							if(mgoal==null)
								throw new RuntimeException("Cannot resolve plan trigger: "+ref);
							BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(mgoal);
						}
					}
				}
			}
		}

		// Initialize beliefs.
		IFuture	belsdone;
		Collection	mbels = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_beliefs);
		if(mbels!=null)
		{
			Future	fut	= new Future();
			belsdone	= fut;
			final Iterator	it	= mbels.iterator();
			IResultListener	rl	= new DelegationResultListener(fut)
			{
				public void customResultAvailable(Object result)
				{
					if(it.hasNext())
					{
						Object mbel = it.next();
						// Create runtime belief if not already there.
						if(state.getAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_beliefs, mbel)==null)
						{
							initBelief(state, rcapa, mbel, fetcher).addResultListener(
								BDIAgentFeature.getInternalAccess(state).getComponentFeature(IExecutionFeature.class).createResultListener(this));
						}
						else
						{
							// continue with next belief
							customResultAvailable(null);
						}
					}
					else
					{
						// finished: now set result
						super.customResultAvailable(null);
					}
				}
			};
			rl.resultAvailable(null);
		}
		else
		{
			belsdone	= IFuture.DONE;
		}

		belsdone.addResultListener(BDIAgentFeature.getInternalAccess(state).getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				// Init assigntos for belief references
				Collection	mbelrefs = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_beliefrefs);
				if(mbelrefs!=null)
				{
					for(Iterator it=mbelrefs.iterator(); it.hasNext(); )
					{
						Object mbelref = it.next();
						registerAssignTos(state, rcapa, mbelref, OAVBDIMetaModel.beliefreference_type, OAVBDIMetaModel.capability_has_beliefrefs);
					}
				}
					
				// Initialize beliefsets.
				Collection	mbelsets = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_beliefsets);
				if(mbelsets!=null)
				{
					for(Iterator it=mbelsets.iterator(); it.hasNext(); )
					{
						final Object mbelset = it.next();
						// Create runtime beliefset if not already there.
						if(state.getAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_beliefsets, mbelset)==null)
						{
							initBeliefSet(state, rcapa, mbelset, fetcher);
						}
					}
				}
				
				// Init assigntos for beliefset references
				Collection	mbelsetrefs = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_beliefsetrefs);
				if(mbelsetrefs!=null)
				{
					for(Iterator it=mbelsetrefs.iterator(); it.hasNext(); )
					{
						Object mbelsetref = it.next();
						registerAssignTos(state, rcapa, mbelsetref, OAVBDIMetaModel.beliefsetreference_type, OAVBDIMetaModel.capability_has_beliefsetrefs);
					}
				}

				// Init assigntos for goals
				Collection	mgoals = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_goals);
				if(mgoals!=null)
				{
					for(Iterator it=mgoals.iterator(); it.hasNext(); )
					{
						Object mgoal = it.next();
						registerAssignTos(state, rcapa, mgoal, OAVBDIMetaModel.goalreference_type, OAVBDIMetaModel.capability_has_goalrefs);
					}
				}

				// Init assigntos for goalreferences
				Collection	mgoalrefs = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_goalrefs);
				if(mgoalrefs!=null)
				{
					for(Iterator it=mgoalrefs.iterator(); it.hasNext(); )
					{
						Object mgoalref = it.next();
						registerAssignTos(state, rcapa, mgoalref, OAVBDIMetaModel.goalreference_type, OAVBDIMetaModel.capability_has_goalrefs);
					}
				}

				// Init assigntos for internalevents
				Collection	minternalevents = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_internalevents);
				if(minternalevents!=null)
				{
					for(Iterator it=minternalevents.iterator(); it.hasNext(); )
					{
						Object minternalevent = it.next();
						registerAssignTos(state, rcapa, minternalevent, OAVBDIMetaModel.internaleventreference_type, OAVBDIMetaModel.capability_has_internaleventrefs);
					}
				}

				// Init assigntos for internaleventreferences
				Collection	minternaleventrefs = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_internaleventrefs);
				if(minternaleventrefs!=null)
				{
					for(Iterator it=minternaleventrefs.iterator(); it.hasNext(); )
					{
						Object minternaleventref = it.next();
						registerAssignTos(state, rcapa, minternaleventref, OAVBDIMetaModel.internaleventreference_type, OAVBDIMetaModel.capability_has_internaleventrefs);
					}
				}

				// Init assigntos for messageevents
				Collection	mmessageevents = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_messageevents);
				if(mmessageevents!=null)
				{
					for(Iterator it=mmessageevents.iterator(); it.hasNext(); )
					{
						Object mmessageevent = it.next();
						registerAssignTos(state, rcapa, mmessageevent, OAVBDIMetaModel.messageeventreference_type, OAVBDIMetaModel.capability_has_messageeventrefs);
					}
				}

				// Init assigntos for messageeventreferences
				Collection	mmessageeventrefs = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_messageeventrefs);
				if(mmessageeventrefs!=null)
				{
					for(Iterator it=mmessageeventrefs.iterator(); it.hasNext(); )
					{
						Object mmessageeventref = it.next();
						registerAssignTos(state, rcapa, mmessageeventref, OAVBDIMetaModel.messageeventreference_type, OAVBDIMetaModel.capability_has_messageeventrefs);
					}
				}

				// Initialize subcapabilities.
				IFuture	subcapsdone;
				Collection caparefs = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities);
				if(caparefs!=null)
				{
					Future	fut	= new Future();
					subcapsdone	= fut;
					final Iterator	it	= caparefs.iterator();
					IResultListener	rl	= new DelegationResultListener(fut)
					{
						public void customResultAvailable(Object result)
						{
							if(it.hasNext())
							{
								Object caparef = it.next();
								Object rsubcapa = state.getAttributeValue(caparef, OAVBDIRuntimeModel.capabilityreference_has_capability);
								initializeCapabilityInstance(state, rsubcapa).addResultListener(
									BDIAgentFeature.getInternalAccess(state).getComponentFeature(IExecutionFeature.class).createResultListener(this));
							}
							else
							{
								// finished: now set result
								super.customResultAvailable(null);
							}
						}
					};
					rl.resultAvailable(null);
				}
				else
				{
					subcapsdone	= IFuture.DONE;
				}
				
				subcapsdone.addResultListener(BDIAgentFeature.getInternalAccess(state).getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						// Initialize mplan triggers (cached for speed and simplicity of rules).
						if(mplans!=null)
						{
							for(Iterator it=mplans.iterator(); it.hasNext(); )
							{
								Object	mplan	= it.next();
								Object	trigger	= state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_trigger);
								if(trigger!=null)
								{
									Collection	triggerrefs	= state.getAttributeValues(trigger, OAVBDIMetaModel.plantrigger_has_goals);
									if(triggerrefs!=null)
									{
										for(Iterator it2=triggerrefs.iterator(); it2.hasNext(); )
										{
											Object	triggerref	= it2.next();
											String	ref	= (String)state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_ref);
											Object[] scope = resolveCapability(ref, OAVBDIMetaModel.goal_type, rcapa, state);
											Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
											Object	precand	= state.createObject(OAVBDIRuntimeModel.precandidate_type);
											state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_mplan, mplan);
											state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_capability, rcapa);
											state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_triggerreference, triggerref);
					
											Object	mpe	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_goals, scope[0]);
											if(mpe==null)
												throw new RuntimeException("Cannot resolve plan trigger: "+ref);
											Object	precandlist	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_precandidates, mpe);
											if(precandlist==null)
											{
												precandlist	= state.createObject(OAVBDIRuntimeModel.precandidatelist_type);
												state.setAttributeValue(precandlist, OAVBDIRuntimeModel.precandidatelist_has_processableelement, mpe);
												state.addAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_precandidates, precandlist);
											}
											state.addAttributeValue(precandlist, OAVBDIRuntimeModel.precandidatelist_has_precandidates, precand);
										}
									}
					
									triggerrefs	= state.getAttributeValues(trigger, OAVBDIMetaModel.trigger_has_internalevents);
									if(triggerrefs!=null)
									{
										for(Iterator it2=triggerrefs.iterator(); it2.hasNext(); )
										{
											Object	triggerref	= it2.next();
											String	ref	= (String)state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_ref);
											Object[]	scope	= resolveCapability(ref, OAVBDIMetaModel.internalevent_type, rcapa, state);
											Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
											Object	precand	= state.createObject(OAVBDIRuntimeModel.precandidate_type);
											state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_mplan, mplan);
											state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_capability, rcapa);
											state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_triggerreference, triggerref);
					
											Object	mpe	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_internalevents, scope[0]);
											if(mpe==null)
												throw new RuntimeException("Cannot resolve plan trigger: "+ref);
											Object	precandlist	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_precandidates, mpe);
											if(precandlist==null)
											{
												precandlist	= state.createObject(OAVBDIRuntimeModel.precandidatelist_type);
												state.setAttributeValue(precandlist, OAVBDIRuntimeModel.precandidatelist_has_processableelement, mpe);
												state.addAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_precandidates, precandlist);
											}
											state.addAttributeValue(precandlist, OAVBDIRuntimeModel.precandidatelist_has_precandidates, precand);
										}
									}
					
									triggerrefs	= state.getAttributeValues(trigger, OAVBDIMetaModel.trigger_has_messageevents);
									if(triggerrefs!=null)
									{
										for(Iterator it2=triggerrefs.iterator(); it2.hasNext(); )
										{
											Object	triggerref	= it2.next();
											String	ref	= (String)state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_ref);
											Object[]	scope	= resolveCapability(ref, OAVBDIMetaModel.messageevent_type, rcapa, state);
											Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
											Object	precand	= state.createObject(OAVBDIRuntimeModel.precandidate_type);
											state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_mplan, mplan);
											state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_capability, rcapa);
											state.setAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_triggerreference, triggerref);
					
											Object	mpe	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_messageevents, scope[0]);
											if(mpe==null)
												throw new RuntimeException("Cannot resolve plan trigger: "+ref);
											Object	precandlist	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_precandidates, mpe);
											if(precandlist==null)
											{
												precandlist	= state.createObject(OAVBDIRuntimeModel.precandidatelist_type);
												state.setAttributeValue(precandlist, OAVBDIRuntimeModel.precandidatelist_has_processableelement, mpe);
												state.addAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_precandidates, precandlist);
											}
											state.addAttributeValue(precandlist, OAVBDIRuntimeModel.precandidatelist_has_precandidates, precand);
										}
									}
								}
							}
						}

						if(mconfig!=null)
						{	
							// Create initial goals.
							Collection	cgoals	= state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialgoals);
							if(cgoals!=null)
							{
								for(Iterator it=cgoals.iterator(); it.hasNext(); )
								{
									createConfigGoal(state, rcapa, it.next(), fetcher);
								}
							}
							
							// Create initial plans.
							Collection	cplans	= state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialplans);
							if(cplans!=null)
							{
								for(Iterator it=cplans.iterator(); it.hasNext(); )
								{
									createConfigPlan(state, rcapa, mcapa, it.next(), fetcher);
								}
							}
					
							// Create initial message events.
							Collection	cmevents = state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialmessageevents);
							if(cmevents!=null)
							{
								for(Iterator it=cmevents.iterator(); it.hasNext(); )
								{
									createConfigMessageEvent(state, rcapa, it.next(), fetcher);
								}
							}
							
							// Create initial internal events.
							Collection	cievents = state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialinternalevents);
							if(cievents!=null)
							{
								for(Iterator it=cievents.iterator(); it.hasNext(); )
								{
									createConfigInternalEvent(state, rcapa, it.next(), fetcher);
								}
							}
						}
						
						ret.setResult(null);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Get the configuration.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @return The configuration.
	 */
	protected static Object getConfiguration(IOAVState state, Object rcapa)
	{
		// Get configuration.
		String	config	= (String)state.getAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_configuration);
		if("".equals(config))
			config	= null;	// Hack!!! Required for message based agent created in JADE.
		Object	mcap	= state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
		if(config==null)
			config	= (String)state.getAttributeValue(mcap, OAVBDIMetaModel.capability_has_defaultconfiguration);
		Object	mconfig;
		if(config==null)
		{
			Collection	mconfigs	= state.getAttributeValues(mcap, OAVBDIMetaModel.capability_has_configurations);
			if(mconfigs!=null)
				mconfig	= mconfigs.iterator().next();
			else
				mconfig	= null;
		}
		else
		{
			if(!state.containsKey(mcap, OAVBDIMetaModel.capability_has_configurations, config))
				throw new RuntimeException("No such configuration: "+config);
			mconfig	= state.getAttributeValue(mcap, OAVBDIMetaModel.capability_has_configurations, config);
		}
		
		return mconfig;
	}
	
	/**
	 *  Initialize a belief.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param mbel The belief model.
	 *  @param fetcher The fetcher.
	 */
	public static IFuture	initBelief(final IOAVState state, final Object rcapa, final Object mbel, IValueFetcher fetcher)
	{
		final String name = (String)state.getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name);
//		System.out.println("iB "+name+", "+rcapa);
		final Future	ret	= new Future();
		
		Object agent = BDIAgentFeature.getInterpreter(state).getAgent();
		Map parents = (Map)state.getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_initparents);
		Map arguments = (Map)state.getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_arguments);
		if(fetcher==null)
			fetcher = new OAVBDIFetcher(state, rcapa);
		
		final Object rbel = state.createObject(OAVBDIRuntimeModel.belief_type);
		state.setAttributeValue(rbel, OAVBDIRuntimeModel.element_has_model, mbel);
		state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_beliefs, rbel);
		Object evamode = state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_evaluationmode);
		final Long update = (Long)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_updaterate);
	
		// Set a value if the belief is static (or first value for update rate).
		if(OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode) || update!=null)
		{
			findValue(state, rcapa, rbel, parents, fetcher, arguments).addResultListener(BDIAgentFeature.getInternalAccess(state)
				.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
//					System.out.println("fact is: "+name+" "+result);
					BeliefRules.setBeliefValue(state, rbel, result, rcapa);
					
					// Set also argument value if belief is declared as argument.
					if(state.getType(rcapa).equals(OAVBDIRuntimeModel.agent_type))
					{
						boolean isarg = false;
						if(state.getType(mbel).equals(OAVBDIMetaModel.belief_type))
						{
							isarg = ((Boolean)state.getAttributeValue(mbel, OAVBDIMetaModel.belief_has_argument)).booleanValue();
						}
						else if(state.getType(mbel).equals(OAVBDIMetaModel.beliefreference_type))
						{
							isarg = ((Boolean)state.getAttributeValue(mbel, OAVBDIMetaModel.beliefreference_has_argument)).booleanValue();
						}
						if(!isarg)
						{
							String exp = ((String)state.getAttributeValue(mbel, OAVBDIMetaModel.referenceableelement_has_exported));
							isarg = exp.equals(OAVBDIMetaModel.EXPORTED_TRUE);
						}
						if(isarg)
						{
							String name = (String)state.getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name);
							BDIAgentFeature.getInternalAccess(state).getComponentFeature(IArgumentsResultsFeature.class).getArguments().put(name, result);
						}
					}
					
					if(update!=null)
					{
						final ITimedObject[]	to	= new ITimedObject[1];
						final OAVBDIFetcher fet = new OAVBDIFetcher(state, rcapa);
						to[0] = new InterpreterTimedObject(BDIAgentFeature.getInternalAccess(state), new CheckedAction()
						{
							public void run()
							{
								final Object	exp = state.getAttributeValue(mbel, OAVBDIMetaModel.belief_has_fact);
								try
								{
//									Object agent = BDIAgentFeature.getInterpreter(state).getAgent();
//									String name = (String)state.getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_name);
									Object value = evaluateExpression(state, exp, fet);
									if(value instanceof IFuture && IFuture.class.equals(state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_class)))
									{
										((IFuture)value).addResultListener(BDIAgentFeature.getInternalAccess(state)
											.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
										{
											public void resultAvailable(Object result)
											{
												BeliefRules.setBeliefValue(state, rbel, result, rcapa);
												state.setAttributeValue(rbel, OAVBDIRuntimeModel.typedelement_has_timer, 
													 SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).createTimer(update.longValue(), to[0]));
											}
											
											public void exceptionOccurred(Exception exception)
											{
												String name = BDIAgentFeature.getInternalAccess(state).getComponentIdentifier().getName();
												BDIAgentFeature.getInterpreter(state).getLogger(rcapa).severe("Could not evaluate belief expression: "+name+" "+state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_parsed));
												state.setAttributeValue(rbel, OAVBDIRuntimeModel.typedelement_has_timer, 
													SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).createTimer(update.longValue(), to[0]));
											}
										}));
									}
									else
									{
										BeliefRules.setBeliefValue(state, rbel, value, rcapa);
										state.setAttributeValue(rbel, OAVBDIRuntimeModel.typedelement_has_timer, 
											 SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).createTimer(update.longValue(), to[0]));
									}

//									System.out.println("Updating belief: "+state.getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name)+" = "+value+", "+BDIAgentFeature.getInterpreter(state).getClockService().getTime());
								}
								catch(Exception e)
								{
									String name = BDIAgentFeature.getInternalAccess(state).getComponentIdentifier().getName();
									BDIAgentFeature.getInterpreter(state).getLogger(rcapa).severe("Could not evaluate belief expression: "+name+" "+state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_parsed));
									state.setAttributeValue(rbel, OAVBDIRuntimeModel.typedelement_has_timer, 
										 SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).createTimer(update.longValue(), to[0]));
								}
							}
							
							public String toString()
							{
								return "CheckedAction: initBelief(), "+state.getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name)+", "+state.getAttributeValue(rbel, OAVBDIRuntimeModel.belief_has_fact);
							}
						});
						
				//			// changed *.class to *.TYPE due to javaflow bug
						state.setAttributeValue(rbel, OAVBDIRuntimeModel.typedelement_has_timer, 
							 SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).createTimer(update.longValue(), to[0]));
					}
					
					// Todo: why assigntos after setting value?
					registerAssignTos(state, rcapa, mbel, OAVBDIMetaModel.beliefreference_type, OAVBDIMetaModel.capability_has_beliefrefs);

					ret.setResult(null);
				}
			}));
			
			// Set default belief values immediately, otherwise evaluation of dependent expressions fails.
			FutureHelper.notifyStackedListeners();
		}
		else
		{
			registerAssignTos(state, rcapa, mbel, OAVBDIMetaModel.beliefreference_type, OAVBDIMetaModel.capability_has_beliefrefs);
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 * 	Register assigntos.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param melem	The element
	 *  @param reftype	The reference type (mbeliefreference etc.).
	 */
	protected static void registerAssignTos(IOAVState state, Object rcapa, Object melem, OAVObjectType reftype, OAVAttributeType refattrtype)
	{
		Collection	assigntos	= state.getAttributeValues(melem, OAVBDIMetaModel.referenceableelement_has_assignto);
		if(assigntos!=null)
		{
			Object	sourcecapa	= rcapa;
			Object	sourceelem	= melem;
			if(state.getType(melem).isSubtype(OAVBDIMetaModel.elementreference_type))
			{
				// Todo: resolve to original element directly.
				// Problem: required information for resolveCapability may not be there, yet. 
//				String	concrete	= (String)state.getAttributeValue(melem, OAVBDIMetaModel.elementreference_has_concrete);
//				Object[]	ret	= resolveCapability(concrete, reftype, rcapa, state);
			}
			
			for(Iterator it=assigntos.iterator(); it.hasNext(); )
			{
				String	assignto	= (String)it.next();
				Object[]	ret	= resolveCapability(assignto, reftype, rcapa, state, false);
				Object	mtargetcapa	= state.getAttributeValue(ret[1], OAVBDIRuntimeModel.element_has_model);
				Object	mtarget	= state.getAttributeValue(mtargetcapa, refattrtype, ret[0]);
				if(mtarget==null)
					throw new RuntimeException("AssignTo element not found: "+assignto);
				Object	abstractsource	= state.createObject(OAVBDIRuntimeModel.abstractsource_type);
				state.setAttributeValue(abstractsource, OAVBDIRuntimeModel.abstractsource_has_abstract, mtarget);
				state.setAttributeValue(abstractsource, OAVBDIRuntimeModel.abstractsource_has_rcapa, sourcecapa);
				state.setAttributeValue(abstractsource, OAVBDIRuntimeModel.abstractsource_has_source, sourceelem);
				state.addAttributeValue(ret[1], OAVBDIRuntimeModel.capability_has_abstractsources, abstractsource);
			}
		}
	}
	
	/**
	 *  Find the first belief value.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param rbel The belief.
	 *  @param parents The parents.
	 *  @param fetcher The fetcher.
	 *  @param arguments The arguments.
	 */
	protected static IFuture	findValue(IOAVState state, Object rcapa, Object rbel, Map parents, IValueFetcher fetcher, Map arguments)
	{	
		Future	ret	= new Future();
		Object mbel = state.getAttributeValue(rbel, OAVBDIRuntimeModel.element_has_model);
		String belname = (String)state.getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name);

		// Find parents of capability.
		List ps = new ArrayList();
		Object tmp = rcapa;
		while(tmp!=null)
		{
			ps.add(0, tmp);
			tmp = parents.get(tmp);
		}
			
		// Try to find argument value for the belief.
		// Try to find from arguments if agent.
		if(arguments!=null)
		{
			Object ragent = ps.get(0);
			if(rcapa==ragent && arguments.containsKey(belname))
			{
				ret.setResult(arguments.get(belname));
			}
			else
			{
				Object magent = state.getAttributeValue(ragent, OAVBDIRuntimeModel.element_has_model);				
				Collection belrefs = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_beliefrefs);
				if(belrefs!=null)
				{
					for(Iterator it=belrefs.iterator(); !ret.isDone() && it.hasNext(); )
					{
						Object belref = it.next();
						String name =(String)state.getAttributeValue(belref, OAVBDIMetaModel.modelelement_has_name);
						Object[] res = resolveCapability(name, OAVBDIMetaModel.belief_type, ragent, state);
						if(res[0].equals(belname) && res[1].equals(rcapa))
						{
							if(arguments.containsKey(name))
								ret.setResult(arguments.get(name));
						}
					}
				}
			}
		}
			
		if(!ret.isDone())
		{
			// Try to get value from outer capability (owner).
			// If undefined try go get initial value.
			Object	fact	= null;
			for(int i=0; i<ps.size() && fact==null; i++)
			{
				Object mconfig = getConfiguration(state, ps.get(i));
				if(mconfig!=null)
				{
					// Initial beliefs in configuration
					Collection	minibels = state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialbeliefs);
					if(minibels!=null)
					{
						for(Iterator it=minibels.iterator(); it.hasNext(); )
						{
							Object	minibel	= it.next();
							String name =(String)state.getAttributeValue(minibel, OAVBDIMetaModel.configbelief_has_ref);
							Object[] res = resolveCapability(name, OAVBDIMetaModel.belief_type, ps.get(i), state);
							if(res[0].equals(belname) && res[1].equals(rcapa))
							{
								fact	= state.getAttributeValue(minibel, OAVBDIMetaModel.belief_has_fact);
							}
						}
					}
				}
			}
			
			if(fact!=null)
			{
				Object	val	= evaluateExpression(state, fact, fetcher);
				if(val instanceof IFuture && IFuture.class.equals(state.getAttributeValue(fact, OAVBDIMetaModel.expression_has_class)))
				{
					((IFuture)val).addResultListener(BDIAgentFeature.getInternalAccess(state)
						.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
				}
				else
				{
					ret.setResult(val);
				}
			}
			else
			{
				// Try to fetch default value which is only contained in original.
				if(state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_beliefs).contains(rbel))
				{		
					Object	exp = state.getAttributeValue(mbel, OAVBDIMetaModel.belief_has_fact);
					if(exp!=null)
					{
						Object	val	= evaluateExpression(state, exp, fetcher);
						if(val instanceof IFuture && IFuture.class.equals(state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_class)))
						{
							((IFuture)val).addResultListener(BDIAgentFeature.getInternalAccess(state)
								.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
						}
						else
						{
							ret.setResult(val);
						}

					}
					else
					{
						Class clazz = (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
						ret.setResult(SReflect.getDefaultValue(clazz));
					}
				}				
			}
		}
		
		return ret;
	}
	
	/**
	 *  Initialize a beliefset.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param mbelset The beliefset model.
	 *  @param fetcher The fetcher.
	 */
	public static IFuture	initBeliefSet(final IOAVState state, final Object rcapa, final Object mbelset, IValueFetcher fetcher)
	{
//		System.out.println("iBs "+state.getAttributeValue(mbelset, OAVBDIMetaModel.modelelement_has_name)+", "+rcapa);
		Future	ret	= new Future();
		
		Object agent = BDIAgentFeature.getInterpreter(state).getAgent();
		Map parents = (Map)state.getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_initparents);
		Map arguments = (Map)state.getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_arguments);
		if(fetcher==null)
			fetcher = new OAVBDIFetcher(state, rcapa);
		
		final Object rbelset = state.createObject(OAVBDIRuntimeModel.beliefset_type);
		state.setAttributeValue(rbelset, OAVBDIRuntimeModel.element_has_model, mbelset);
		state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_beliefsets, rbelset);
		Object	evamode = state.getAttributeValue(mbelset, OAVBDIMetaModel.typedelement_has_evaluationmode);
		final Long update = (Long)state.getAttributeValue(mbelset, OAVBDIMetaModel.typedelement_has_updaterate);
	
		// Set a value if the belief is static (or first value for update rate).
		if(OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode) || update!=null)
		{
			findValues(state, rcapa, rbelset, parents, fetcher, arguments)
				.addResultListener(BDIAgentFeature.getInternalAccess(state).getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					for(Iterator it=SReflect.getIterator(result); it.hasNext(); )
					{
						BeliefRules.addBeliefSetValue(state, rbelset, it.next(), rcapa);
					}
					
					// Set also argument values if beliefset is declared as argument.
					if(state.getType(rcapa).equals(OAVBDIRuntimeModel.agent_type))
					{
						boolean isarg = false;
						if(state.getType(mbelset).equals(OAVBDIMetaModel.beliefset_type))
						{
							isarg = ((Boolean)state.getAttributeValue(mbelset, OAVBDIMetaModel.beliefset_has_argument)).booleanValue();
						}
						else if(state.getType(mbelset).equals(OAVBDIMetaModel.beliefsetreference_type))
						{
							isarg = ((Boolean)state.getAttributeValue(mbelset, OAVBDIMetaModel.beliefsetreference_has_argument)).booleanValue();
						}	
						if(!isarg)
						{
							String exp = ((String)state.getAttributeValue(mbelset, OAVBDIMetaModel.referenceableelement_has_exported));
							isarg = exp.equals(OAVBDIMetaModel.EXPORTED_TRUE);
						}
						if(isarg)
						{
							String name = (String)state.getAttributeValue(mbelset, OAVBDIMetaModel.modelelement_has_name);
							BDIAgentFeature.getInternalAccess(state).getComponentFeature(IArgumentsResultsFeature.class).getArguments().put(name, result);
						}
					}

					if(update!=null)
					{
						final ITimedObject[]	to	= new ITimedObject[1];
						
						final OAVBDIFetcher fet = new OAVBDIFetcher(state, rcapa);
						to[0]	= new InterpreterTimedObject(BDIAgentFeature.getInternalAccess(state), new CheckedAction()
						{
							public void run()
							{
								final Object	exp = state.getAttributeValue(mbelset, OAVBDIMetaModel.beliefset_has_factsexpression);
								try
								{
									Object values	= evaluateExpression(state, exp, fet);
									if(values instanceof IFuture && IFuture.class.equals(state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_class)))
									{
										((IFuture)values).addResultListener(BDIAgentFeature.getInternalAccess(state)
											.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
										{
											public void resultAvailable(Object result)
											{
												BeliefRules.updateBeliefSet(state, rbelset, result, rcapa);
												state.setAttributeValue(rbelset, OAVBDIRuntimeModel.typedelement_has_timer,  SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).createTimer(update.longValue(), to[0]));
											}
											public void exceptionOccurred(Exception exception)
											{
												String name = BDIAgentFeature.getInternalAccess(state).getComponentIdentifier().getName();
												BDIAgentFeature.getInterpreter(state).getLogger(rcapa).severe("Could not evaluate belief expression: "+name+" "+state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_parsed));
												state.setAttributeValue(rbelset, OAVBDIRuntimeModel.typedelement_has_timer,  SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).createTimer(update.longValue(), to[0]));
											}
										}));
									}
									else
									{
										BeliefRules.updateBeliefSet(state, rbelset, values, rcapa);
										state.setAttributeValue(rbelset, OAVBDIRuntimeModel.typedelement_has_timer, SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).createTimer(update.longValue(), to[0]));
									}
								}
								catch(Exception e)
								{
									String name = BDIAgentFeature.getInternalAccess(state).getComponentIdentifier().getName();
									BDIAgentFeature.getInterpreter(state).getLogger(rcapa).severe("Could not evaluate belief expression: "+name+" "+state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_parsed));
									state.setAttributeValue(rbelset, OAVBDIRuntimeModel.typedelement_has_timer,  SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).createTimer(update.longValue(), to[0]));
								}
							}
						});
						state.setAttributeValue(rbelset, OAVBDIRuntimeModel.typedelement_has_timer,  SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).createTimer(update.longValue(), to[0]));
					}

					registerAssignTos(state, rcapa, mbelset, OAVBDIMetaModel.beliefsetreference_type, OAVBDIMetaModel.capability_has_beliefsetrefs);
					
					super.customResultAvailable(null);
				}
			}));
			
			// Set default belief values immediately, otherwise evaluation of dependent expressions fails.
			FutureHelper.notifyStackedListeners();
		}
		else
		{
			registerAssignTos(state, rcapa, mbelset, OAVBDIMetaModel.beliefsetreference_type, OAVBDIMetaModel.capability_has_beliefsetrefs);
			
			ret.setResult(null);			
		}
		
		return ret;
	}
	
	/**
	 *  Find the first beliefset values.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param rbel The belief.
	 *  @param parents The parents.
	 *  @param fetcher The fetcher.
	 *  @param arguments The arguments.
	 */
	protected static IFuture	findValues(IOAVState state, Object rcapa, Object rbelset, Map parents, IValueFetcher fetcher, Map arguments)
	{	
		Future	ret	= new Future();
		Object mbelset = state.getAttributeValue(rbelset, OAVBDIRuntimeModel.element_has_model);
		String belsetname = (String)state.getAttributeValue(mbelset, OAVBDIMetaModel.modelelement_has_name);
		
		List ps = new ArrayList();
		Object tmp = rcapa;
		while(tmp!=null)
		{
			ps.add(0, tmp);
			tmp = parents.get(tmp);
		}
		
		// Try to find argument value for the belief.
		// Try to find from arguments if agent.
		if(arguments!=null)
		{
			Object ragent = ps.get(0);
			if(rcapa==ragent && arguments.containsKey(belsetname))
			{
				ret.setResult(arguments.get(belsetname));
			}
			else
			{
				Object magent = state.getAttributeValue(ragent, OAVBDIRuntimeModel.element_has_model);
				Collection belsetrefs = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_beliefsetrefs);
				if(belsetrefs!=null)
				{
					for(Iterator it=belsetrefs.iterator(); !ret.isDone() && it.hasNext(); )
					{
						Object belsetref = it.next();
						String name =(String)state.getAttributeValue(belsetref, OAVBDIMetaModel.modelelement_has_name);
						Object[] res = resolveCapability(name, OAVBDIMetaModel.beliefset_type, ragent, state);
						if(res[0].equals(belsetname) && res[1].equals(rcapa))
						{
							if(arguments.containsKey(name))
								ret.setResult(arguments.get(name));
						}
					}
				}
			}
		}
		
		if(!ret.isDone())
		{
			Object	fact	= null;
			Collection	facts	= null;
			// Try to get value from outer capability (owner).
			// If undefined try go get initial value. 
			for(int i=0; i<ps.size() && fact==null && facts==null; i++)
			{
				Object mconfig = getConfiguration(state, ps.get(i));
				if(mconfig!=null)
				{
					// Initial beliefs in configuration
					Collection	minibelsets = state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialbeliefsets);
					if(minibelsets!=null)
					{
						for(Iterator it=minibelsets.iterator(); it.hasNext() && fact==null && facts==null; )
						{
							Object	minibelset	= it.next();
							String name =(String)state.getAttributeValue(minibelset, OAVBDIMetaModel.configbeliefset_has_ref);
							Object[] res = resolveCapability(name, OAVBDIMetaModel.beliefset_type, ps.get(i), state);
							if(res[0].equals(belsetname) && res[1].equals(rcapa))
							{
								fact = state.getAttributeValue(minibelset, OAVBDIMetaModel.beliefset_has_factsexpression);
								facts = state.getAttributeValues(minibelset, OAVBDIMetaModel.beliefset_has_facts);
							}
						}
					}
				}
			}
			
//			System.out.println("fact:"+fact+" facts: "+facts);
			if(fact!=null)
			{
				Object	val	= evaluateExpression(state, fact, fetcher);
				if(val instanceof IFuture && IFuture.class.equals(state.getAttributeValue(fact, OAVBDIMetaModel.expression_has_class)))
				{
					((IFuture)val).addResultListener(BDIAgentFeature.getInternalAccess(state)
						.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
				}
				else
				{
					ret.setResult(val);
				}
			}
			else if(facts!=null)
			{
				List values	= new ArrayList();
				for(Iterator it2=facts.iterator(); it2.hasNext(); )
				{
					fact	= it2.next();
					values.add(evaluateExpression(state, fact, fetcher));
				}
				ret.setResult(values);
			}
			else
			{
				// Try to fetch default value which is only contained in original.
				if(state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_beliefsets).contains(rbelset))
				{		
					facts	= state.getAttributeValues(mbelset, OAVBDIMetaModel.beliefset_has_facts);
					if(facts!=null)
					{
						List values	= new ArrayList();
						for(Iterator it2=facts.iterator(); it2.hasNext(); )
						{
							fact	= it2.next();
							values.add(evaluateExpression(state, fact, fetcher));
						}
						ret.setResult(values);
					}
					else
					{
						Object	factsexp	= state.getAttributeValue(mbelset, OAVBDIMetaModel.beliefset_has_factsexpression);
						if(factsexp!=null)
						{
							Object	val	= evaluateExpression(state, factsexp, fetcher);
							if(val instanceof IFuture && IFuture.class.equals(state.getAttributeValue(factsexp, OAVBDIMetaModel.expression_has_class)))
							{
								((IFuture)val).addResultListener(BDIAgentFeature.getInternalAccess(state)
									.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
							}
							else
							{
								ret.setResult(val);
							}
						}
						else
						{
							ret.setResult(null);
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a config plan.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param cplan The config plan.
	 *  @param fetcher The fetcher.
	 */
	protected static void createConfigPlan(final IOAVState state,
			final Object rcapa, Object mcap, Object cplan, final OAVBDIFetcher fetcher)
	{
		Object mplanname = state.getAttributeValue(cplan, OAVBDIMetaModel.configelement_has_ref);
		Object mplan = state.getAttributeValue(mcap, OAVBDIMetaModel.capability_has_plans, mplanname);
		
		// Create plans according to binding possibilities.
		List bindings = AgentRules.calculateBindingElements(state, mplan, cplan, fetcher);
		if(bindings!=null)
		{
			for(int i=0; i<bindings.size(); i++)
			{
				Object rplan = PlanRules.instantiatePlan(state, rcapa, mplan, cplan, null, null, (Map)bindings.get(i), null);
				PlanRules.adoptPlan(state, rcapa, rplan);	
			}
		}
		else
		{
			Object rplan = PlanRules.instantiatePlan(state, rcapa, mplan, cplan, null, null, null, null);
			PlanRules.adoptPlan(state, rcapa, rplan);
		}
	}

	/**
	 *  Create a config message event.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param cevent The config event.
	 *  @param fetcher The fetcher.
	 */
	protected static void createConfigMessageEvent(final IOAVState state,
			final Object rcapa, Object cevent, final OAVBDIFetcher fetcher)
	{
		Object meventname = state.getAttributeValue(cevent, OAVBDIMetaModel.configelement_has_ref);
		Object[] scope = resolveCapability((String)meventname, OAVBDIMetaModel.messageevent_type, rcapa, state);
		Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mscope, OAVBDIMetaModel.capability_has_messageevents, scope[0]))
			throw new RuntimeException("Unknown message event: "+meventname);
		Object mevent = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_messageevents, scope[0]);
		
		// Create events according to binding possibilities.
		List bindings = AgentRules.calculateBindingElements(state, mevent, cevent, fetcher);
		if(bindings!=null)
		{
			for(int i=0; i<bindings.size(); i++)
			{
				Object revent = MessageEventRules.instantiateMessageEvent(state, scope[1], mevent, cevent, (Map)bindings.get(i), null, fetcher);
				state.addAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_outbox, revent);
			}
		}
		else
		{
			Object revent = MessageEventRules.instantiateMessageEvent(state, scope[1], mevent, cevent, null, null, fetcher);
			state.addAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_outbox, revent);
		}
	}

	/**
	 *  Create a config internal event.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param cevent The config event.
	 *  @param fetcher The fetcher.
	 */
	protected static void createConfigInternalEvent(final IOAVState state,
			final Object rcapa, Object cevent, final OAVBDIFetcher fetcher)
	{
		Object meventname = state.getAttributeValue(cevent, OAVBDIMetaModel.configelement_has_ref);
		Object[] scope = resolveCapability((String)meventname, OAVBDIMetaModel.internalevent_type, rcapa, state);
		Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mscope, OAVBDIMetaModel.capability_has_internalevents, scope[0]))
			throw new RuntimeException("Unknown internal event: "+meventname);
		Object mevent = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_internalevents, scope[0]);
		
		// Create events according to binding possibilities.
		List bindings = AgentRules.calculateBindingElements(state, mevent, cevent, fetcher);
		if(bindings!=null)
		{
			for(int i=0; i<bindings.size(); i++)
			{
				Object revent = InternalEventRules.instantiateInternalEvent(state, scope[1], mevent, cevent, (Map)bindings.get(i), null, fetcher);
				InternalEventRules.adoptInternalEvent(state, scope[1], revent);
			}
		}
		else
		{
			Object revent = InternalEventRules.instantiateInternalEvent(state, scope[1], mevent, cevent, null, null, fetcher);
			InternalEventRules.adoptInternalEvent(state, scope[1], revent);
		}
	}

	/**
	 *  Create a config goal.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param cgoal The config goal.
	 *  @param fetcher The fetcher.
	 */
	protected static void createConfigGoal(final IOAVState state,
			final Object rcapa, Object cgoal, final OAVBDIFetcher fetcher)
	{
		Object mgoalname = state.getAttributeValue(cgoal, OAVBDIMetaModel.configelement_has_ref);

		Object[] scope = resolveCapability((String)mgoalname, OAVBDIMetaModel.goal_type, rcapa, state);
		Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mscope, OAVBDIMetaModel.capability_has_goals, scope[0]))
			throw new RuntimeException("Unknown goal: "+mgoalname);
		Object mgoal = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_goals, scope[0]);
		
//		System.out.println("Creating config goal: "+mgoalname+" "+cgoal);
		
		// Create events according to binding possibilities.
		List bindings = AgentRules.calculateBindingElements(state, mgoal, cgoal, fetcher);
		if(bindings!=null)
		{
			for(int i=0; i<bindings.size(); i++)
			{
				Object rgoal = GoalLifecycleRules.instantiateGoal(state, scope[1], mgoal, cgoal, (Map)bindings.get(i), null, fetcher);
				GoalLifecycleRules.adoptGoal(state, scope[1], rgoal);
			}
		}
		else
		{
			Object rgoal = GoalLifecycleRules.instantiateGoal(state, scope[1], mgoal, cgoal, null, null, fetcher);
			GoalLifecycleRules.adoptGoal(state, scope[1], rgoal);
		}
	}

	/**
	 *  Build initial values for capability and direct sub capabilities (from references).
	 *  @return A map of maps with initial values for each subcapability.
	 *    Local values are added to arguments.
	 * /
	protected static Map collectInitialValues(IOAVState state, Object mcap, Object mconfig, Map arguments, IValueFetcher fetcher)
	{
		Map	inivals	= null;

		if(mconfig!=null)
		{
			// Initial beliefs in configuration
			Collection	minibels = state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialbeliefs);
			if(minibels!=null)
			{
				for(Iterator it=minibels.iterator(); it.hasNext(); )
				{
					Object	minibel	= it.next();
					Map	myinivals;
					String	name =(String)state.getAttributeValue(minibel, OAVBDIMetaModel.configbelief_has_ref);
					
					// Resolve subcapability reference.
					int	idx;
					if((idx=name.indexOf('.'))!=-1)
					{
						String	capname	= name.substring(0, idx);
						name	= name.substring(idx+1);
						if(name.indexOf('.')!=-1)
							throw new RuntimeException("Character '.' not allowed in element names.");
						
						if(inivals==null)
							inivals	= new HashMap();
							
						myinivals	= (Map)inivals.get(capname);
						if(myinivals==null)
						{
							myinivals	= new HashMap();
							inivals.put(capname, myinivals);
						}
					}
					else if(state.containsKey(mcap, OAVBDIMetaModel.capability_has_beliefrefs, name))
					{
						Object mbelref = state.getAttributeValue(mcap, OAVBDIMetaModel.capability_has_beliefrefs, name);
						String refname = (String)state.getAttributeValue(mbelref, OAVBDIMetaModel.elementreference_has_concrete);
					
						idx = refname.indexOf('.');
						String	capname	= refname.substring(0, idx);
						name = refname.substring(idx+1);
					
						if(name.indexOf('.')!=-1)
							throw new RuntimeException("Character '.' not allowed in element names.");
						
						if(inivals==null)
							inivals	= new HashMap();
							
						myinivals	= (Map)inivals.get(capname);
						if(myinivals==null)
						{
							myinivals	= new HashMap();
							inivals.put(capname, myinivals);
						}
						
//						System.out.println("Found: "+name+" "+capname);
					}
					else
					{
						if(inivals==null)
							inivals	= new HashMap();
						myinivals	= (Map)inivals.get("");
						if(myinivals==null)
						{
							myinivals	= arguments!=null ? arguments : new HashMap();
							inivals.put("", myinivals);
						}
					}
					
					// Extract and remember value for belief
					if(!myinivals.containsKey(name))
					{
						Object	fact	= state.getAttributeValue(minibel, OAVBDIMetaModel.belief_has_fact);
						Object	value	= evaluateExpression(state, fact, fetcher);
						myinivals.put(name, value);
					}
				}
			}
	
			// Initial beliefsets in configuration
			Collection	minibelsets	= state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialbeliefsets);
			if(minibelsets!=null)
			{
				for(Iterator it=minibelsets.iterator(); it.hasNext(); )
				{
					Object minibelset	= it.next();
					Map	myinivals;
					String	name =(String)state.getAttributeValue(minibelset, OAVBDIMetaModel.configbeliefset_has_ref);
	
					// Resolve subcapability reference.
					int	idx;
					if((idx=name.indexOf('.'))!=-1)
					{
						String	capname	= name.substring(0, idx);
						name	= name.substring(idx+1);
						if(name.indexOf('.')!=-1)
							throw new RuntimeException("Character '.' not allowed in element names.");
						
						if(inivals==null)
							inivals	= new HashMap();
							
						myinivals	= (Map)inivals.get(capname);
						if(myinivals==null)
						{
							myinivals	= new HashMap();
							inivals.put(capname, myinivals);
						}
					}
					else
					{
						if(inivals==null)
							inivals	= new HashMap();
						myinivals	= (Map)inivals.get("");
						if(myinivals==null)
						{
							myinivals	= arguments!=null ? arguments : new HashMap();
							inivals.put("", myinivals);
						}
					}
	
					// Extract and remember values for belief set
					if(!myinivals.containsKey(name))
					{
						Object	value;
						Object	fact	= state.getAttributeValue(minibelset, OAVBDIMetaModel.beliefset_has_factsexpression);
						if(fact!=null)
						{
							value	= evaluateExpression(state, fact, fetcher);
						}
						else
						{
							List	values	= new ArrayList();
							Collection	facts	= state.getAttributeValues(minibelset, OAVBDIMetaModel.beliefset_has_facts);
							if(facts==null)
								throw new RuntimeException("One of fact/facts must be present: "+name);
							for(Iterator it2=facts.iterator(); it2.hasNext(); )
							{
								fact	= it2.next();
								values.add(evaluateExpression(state, fact, fetcher));
							}
							value	= values;
						}
						myinivals.put(name, value);
					}
				}
			}
		}
		
		// Arguments mapped to subcapabilities through exported belief references.
		if(arguments!=null)
		{
			Collection	mbelrefs	= state.getAttributeValues(mcap, OAVBDIMetaModel.capability_has_beliefrefs);
			if(mbelrefs!=null)
			{
				for(Iterator it=mbelrefs.iterator(); it.hasNext(); )
				{
					Object mbelref	= it.next();
					String	name =(String)state.getAttributeValue(mbelref, OAVBDIMetaModel.modelelement_has_name);
					if(arguments.containsKey(name))
					{
						Object	value	= arguments.get(name);
						name	= (String)state.getAttributeValue(mbelref, OAVBDIMetaModel.elementreference_has_concrete);
						int	idx;
						if((idx=name.indexOf('.'))!=-1)
						{
							String	capname	= name.substring(0, idx);
							name	= name.substring(idx+1);
							if(name.indexOf('.')!=-1)
								throw new RuntimeException("Character '.' not allowed in element names.");
							
							if(inivals==null)
								inivals	= new HashMap();
							
							Map	myinivals	= (Map)inivals.get(capname);
							if(myinivals==null)
							{
								myinivals	= new HashMap();
								inivals.put(capname, myinivals);
							}
							
							// Arguments supersede initial values -> put without contains check.
							myinivals.put(name, value);
						}
					}
				}
				
				Collection	mbelsetrefs	= state.getAttributeValues(mcap, OAVBDIMetaModel.capability_has_beliefsetrefs);
				if(mbelsetrefs!=null)
				{
					for(Iterator it=mbelsetrefs.iterator(); it.hasNext(); )
					{
						Object mbelsetref	= it.next();
						String	name =(String)state.getAttributeValue(mbelsetref, OAVBDIMetaModel.modelelement_has_name);
						if(arguments.containsKey(name))
						{
							Object	value	= arguments.get(name);
							name	= (String)state.getAttributeValue(mbelsetref, OAVBDIMetaModel.elementreference_has_concrete);
							int	idx;
							if((idx=name.indexOf('.'))!=-1)
							{
								String	capname	= name.substring(0, idx);
								name	= name.substring(idx+1);
								if(name.indexOf('.')!=-1)
									throw new RuntimeException("Character '.' not allowed in element names.");
								
								if(inivals==null)
									inivals	= new HashMap();
								
								Map	myinivals	= (Map)inivals.get(capname);
								if(myinivals==null)
								{
									myinivals	= new HashMap();
									inivals.put(capname, myinivals);
								}
								
								// Arguments supersede initial values -> put without contains check.
								myinivals.put(name, value);
							}
						}
					}
				}
			}
			
		}

		return inivals;
	}*/
	
	/**
	 *  Evaluate an mexpression.
	 *  @param state	The state.
	 *  @param mexp	The expression object in the state.
	 *  @param exparams	Optional expression parameters.
	 */
	public static Object evaluateExpression(IOAVState state, Object mexp, IValueFetcher fetcher)
	{
		if(fetcher==null)
			throw new RuntimeException("Fetcher must not null.");
			
		Object ret	= null;

		IParsedExpression	pex = (IParsedExpression)state.getAttributeValue(mexp, OAVBDIMetaModel.expression_has_parsed);
//		try
//		{
			ret	= pex.getValue(fetcher);
//		}
//		catch(Exception e)
//		{
//			// Hack!!! Exception should be propagated.
//			System.err.println(pex.getExpressionText());
//			e.printStackTrace();
//		}
		return ret;
	}
	
	/**
	 *  Init parameters of parameter element.
	 *  @param state The state.
	 *  @param rparamelem The runtime parameter element.
	 *  @param configelem The configuration element.
	 *  @param fetcher The value fetcher.
	 *  @param configfetcher The value fetcher for the config element scope (might differ from original scope).
	 *  @param preparams The already initialized parameters.
	 */
	public static void initParameters(IOAVState state, Object rparamelem, Object configelem, IValueFetcher fetcher, IValueFetcher configfetcher, Set doneparams, Map bindings, Object rcapa)
	{
		// Set parameter(set) values.
		if(doneparams==null)
			doneparams = new HashSet();
		
		Object mparamelem = state.getAttributeValue(rparamelem, OAVBDIRuntimeModel.element_has_model);
		
		// Initialize config parameter/set values.
		if(configelem!=null)
		{
			Collection coll = state.getAttributeValues(configelem, OAVBDIMetaModel.configparameterelement_has_parameters);
			if(coll!=null)
			{
				for(Iterator it=coll.iterator(); it.hasNext(); )
				{
					Object cparam = it.next();
					Object pname = state.getAttributeValue(cparam, OAVBDIMetaModel.configparameter_has_ref);
					Object mparam = state.getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parameters, pname);
					if(!doneparams.contains(pname))
					{
//						Class clazz = (Class)state.getAttributeValue(mparam, OAVBDIMetaModel.typedelement_has_class);
						Class clazz = ParameterFlyweight.resolveClazz(state, mparamelem, (String)pname);
						
						Object value = null;
						if(bindings!=null && bindings.containsKey(pname))
						{
							value = bindings.get(pname);
//							System.out.println("Setting binding value: "+pname+" "+bindings.get(varname));
						}
						else
						{
							Object	pvalex	= state.getAttributeValue(cparam, OAVBDIMetaModel.parameter_has_value);
							// Todo: use parameter assignments instead of map for generating flyweights on the fly.
							value	= AgentRules.evaluateExpression(state, pvalex, configfetcher);
						}
						
						BeliefRules.createParameter(state, (String)pname, value, clazz, rparamelem, mparam, rcapa);
						doneparams.add(pname);
					}
				}
			}
			
			coll = state.getAttributeValues(configelem, OAVBDIMetaModel.configparameterelement_has_parametersets);
			if(coll!=null)
			{
				for(Iterator it=coll.iterator(); it.hasNext(); )
				{
					Object cparamset	= it.next();
					String pname	= (String)state.getAttributeValue(cparamset, OAVBDIMetaModel.configparameterset_has_ref);
					Object mparamset = state.getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parametersets, pname);
//					Class clazz = (Class)state.getAttributeValue(mparamset, OAVBDIMetaModel.typedelement_has_class);
					Class clazz = ParameterFlyweight.resolveClazz(state, mparamelem, (String)pname);

					Object rparamset = BeliefRules.createParameterSet(state, pname, null, clazz, rparamelem, mparamset, rcapa);
					doneparams.add(pname);
	
					Collection	pvalexs	= state.getAttributeValues(cparamset, OAVBDIMetaModel.parameterset_has_values);
					if(pvalexs!=null)
					{
						for(Iterator it2=pvalexs.iterator(); it2.hasNext(); )
						{
							// Todo: use parameter assignments instead of map for generating flyweights on the fly.
							Object	pvalue	= AgentRules.evaluateExpression(state, it2.next(), configfetcher);
							BeliefRules.addParameterSetValue(state, rparamset, pvalue);
						}
					}
					else
					{
						Object	pvalsex	= state.getAttributeValue(cparamset, OAVBDIMetaModel.parameterset_has_valuesexpression);
						// Todo: use parameter assignments instead of map for generating flyweights on the fly.
						Object	pvalues	= AgentRules.evaluateExpression(state, pvalsex, configfetcher);
						for(Iterator it2=SReflect.getIterator(pvalues); it2.hasNext(); )
						{
							BeliefRules.addParameterSetValue(state, rparamset, it2.next());
						}
					}
				}
			}
		}
		
		// Initialize default values of parameters and parameter sets.
		Collection coll = state.getAttributeValues(mparamelem, OAVBDIMetaModel.parameterelement_has_parameters);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				Object mparam = it.next();
				String pname = (String)state.getAttributeValue(mparam, OAVBDIMetaModel.modelelement_has_name);
				if(!doneparams.contains(pname))
				{
					Class clazz = (Class)state.getAttributeValue(mparam, OAVBDIMetaModel.typedelement_has_class);
					if(bindings!=null && bindings.containsKey(pname))
					{
//						System.out.println("Setting binding value: "+pname+" "+bindings.get(varname));
						BeliefRules.createParameter(state, pname, bindings.get(pname), clazz, rparamelem, mparam, rcapa);
						doneparams.add(pname);
					}
					else
					{
						Object	evamode = state.getAttributeValue(mparam, OAVBDIMetaModel.typedelement_has_evaluationmode);
						Object mvalex = state.getAttributeValue(mparam, OAVBDIMetaModel.parameter_has_value);
						if(mvalex!=null && (evamode==null || OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode)))
						{
							Object value = AgentRules.evaluateExpression(state, mvalex, fetcher);
							BeliefRules.createParameter(state, pname, value, clazz, rparamelem, mparam, rcapa);
							doneparams.add(pname);
						}
						// Hack! Add rparams for (query and meta-level) goals because query-rule needs params.
						else if(state.getType(rparamelem).isSubtype(OAVBDIRuntimeModel.goal_type))
						{
							BeliefRules.createParameter(state, pname, null, clazz, rparamelem, mparam, rcapa);
							doneparams.add(pname);
						}
					}
				}
			}
		}
		
		coll = state.getAttributeValues(mparamelem, OAVBDIMetaModel.parameterelement_has_parametersets);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				Object mparamset = it.next();
				String pname = (String)state.getAttributeValue(mparamset, OAVBDIMetaModel.modelelement_has_name);
				if(!doneparams.contains(pname))
				{
					Collection	pvalexs	= state.getAttributeValues(mparamset, OAVBDIMetaModel.parameterset_has_values);
					if(pvalexs!=null)
					{
						Class clazz = (Class)state.getAttributeValue(mparamset, OAVBDIMetaModel.typedelement_has_class);
						Object rparamset = BeliefRules.createParameterSet(state, pname, null, clazz, rparamelem, mparamset, rcapa);
						doneparams.add(pname);
						
						for(Iterator it2=pvalexs.iterator(); it2.hasNext(); )
						{
							// Todo: use parameter assignments instead of map for generating flyweights on the fly.
							Object	pvalue	= AgentRules.evaluateExpression(state, it2.next(), fetcher);
							BeliefRules.addParameterSetValue(state, rparamset, pvalue);
						}
					}
					else
					{
						Object	pvalsex	= state.getAttributeValue(mparamset, OAVBDIMetaModel.parameterset_has_valuesexpression);
						if(pvalsex!=null)
						{
							Class clazz = (Class)state.getAttributeValue(mparamset, OAVBDIMetaModel.typedelement_has_class);
							Object rparamset = BeliefRules.createParameterSet(state, pname, null, clazz, rparamelem, mparamset, rcapa);
							doneparams.add(pname);
							
							// Todo: use parameter assignments instead of map for generating flyweights on the fly.
							Object	pvalues	= AgentRules.evaluateExpression(state, pvalsex, fetcher);
							for(Iterator it2=SReflect.getIterator(pvalues); it2.hasNext(); )
							{
								BeliefRules.addParameterSetValue(state, rparamset, it2.next());
							}
						}
						// Hack! Add rparams for (query and meta-level) goals because query-rule needs params.
						else if(state.getType(rparamelem).isSubtype(OAVBDIRuntimeModel.goal_type))
						{
							Class clazz = (Class)state.getAttributeValue(mparamset, OAVBDIMetaModel.typedelement_has_class);
							BeliefRules.createParameterSet(state, pname, null, clazz, rparamelem, mparamset, rcapa);
							doneparams.add(pname);
						}
					}
				}
			}
		}
	}
	
	/**
	 *  Exit the running state of an agent.
	 *  @param state The state.
	 *  @param rcapa The handle to the capability instance.
	 */
	protected static void exitRunningState(final IOAVState state, final Object rcapa)
	{
		// exit running state of subcapabilities.
		Collection subcapas = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities);
		if(subcapas!=null)
		{
			for(Iterator it=subcapas.iterator(); it.hasNext(); )
			{
				Object	caparef	= it.next();
				Object	rsubcapa	= state.getAttributeValue(caparef, OAVBDIRuntimeModel.capabilityreference_has_capability);
				exitRunningState(state, rsubcapa);
			}
		}

		// Drop existing goals, if not protected.
		Collection rgoals = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_goals);
		if(rgoals!=null)
		{
			for(Iterator it=rgoals.iterator(); it.hasNext(); )
			{
				Object rgoal = it.next();
				String gs = (String)state.getAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate);
				if(!OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPING.equals(gs) && !OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED.equals(gs)
					&& !((Boolean)state.getAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_protected)).booleanValue())
				{
					GoalLifecycleRules.dropGoal(state, rgoal);
				}
			}
		}
		
		// Abort running plans.
		Collection rplans = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_plans);
		if(rplans!=null)
		{
			for(Iterator it=rplans.iterator(); it.hasNext(); )
			{
				Object rplan = it.next();
				String ps = (String)state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate);
				if(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_NEW.equals(ps) || OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY.equals(ps))
				{
					// Don't abort plan, when reason is a protected goal.
					Object	reason	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_reason);
					if(reason==null || !state.getType(reason).isSubtype(OAVBDIRuntimeModel.goal_type)
						|| !((Boolean)state.getAttributeValue(reason, OAVBDIRuntimeModel.goal_has_protected)).booleanValue())
					{
						PlanRules.abortPlan(state, rcapa, rplan);
					}
				}
			}
		}
	}
	
	/**
	 *  Activate the end state of an agent.
	 *  @param state The state.
	 *  @param rcapa The reference to the capability instance.
	 */
	protected static void activateEndState(final IOAVState state, final Object rcapa)
	{
		Object	mconfig	= getConfiguration(state, rcapa);
		
		// Initialize subcapabilities.
		Collection subcapas = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities);
		if(subcapas!=null)
		{
			for(Iterator it=subcapas.iterator(); it.hasNext(); )
			{
				Object	caparef	= it.next();
				Object	rsubcapa	= state.getAttributeValue(caparef, OAVBDIRuntimeModel.capabilityreference_has_capability);
				activateEndState(state, rsubcapa);
			}
		}

		if(mconfig!=null)
		{	
			// Hack!!! cache expression parameters?
			OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rcapa);
			
			// Create end goals.
			Collection	cgoals	= state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_endgoals);
			if(cgoals!=null)
			{
				for(Iterator it=cgoals.iterator(); it.hasNext(); )
				{
					createConfigGoal(state, rcapa, it.next(), fetcher);
				}
			}
			
			// Create end plans.
			Collection	cplans	= state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_endplans);
			if(cplans!=null)
			{
				Object	mcap	= state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
				for(Iterator it=cplans.iterator(); it.hasNext(); )
				{
					createConfigPlan(state, rcapa, mcap, it.next(), fetcher);
				}
			}
	
			// Create end message events.
			Collection	cmevents = state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_endmessageevents);
			if(cmevents!=null)
			{
				for(Iterator it=cmevents.iterator(); it.hasNext(); )
				{
					createConfigMessageEvent(state, rcapa, it.next(), fetcher);
				}
			}
			
			// Create end internal events.
			Collection	cievents = state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_endinternalevents);
			if(cievents!=null)
			{
				for(Iterator it=cievents.iterator(); it.hasNext(); )
				{
					createConfigInternalEvent(state, rcapa, it.next(), fetcher);
				}
			}
		}
	}
	
	//-------- helpers --------
	
	/**
	 *  Save a newly created element in the state. It will be stored
	 *  in the uservariables of the plan.
	 * /
	public static void savePlanElement(IOAVState state, Object rplan, Object relem)
	{
		state.addAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_uservariables, relem);
	}*/
	
	/**
	 *  Save a newly created element in the state. It will be stored in the 
	 *  externalaccesselements of the agent. 
	 * /
	public static void saveExternalAccessElement(IOAVState state, Object ragent, Object relem)
	{
		state.addAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_externalaccesselements, relem);
	}*/
	
	/**
	 *  Save agent unknown element.
	 * /
	public static void saveElement(final IOAVState state, final Object relem, IElement flyweight)
	{
		BDIInterpreter interpreter = BDIAgentFeature.getInterpreter(state);
		final Object ragent = BDIAgentFeature.getInterpreter(state).getAgent();
		state.addAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_temporaryobjects, relem);
		interpreter.extaccesses.addEntry(flyweight, new Runnable()
		{
			public void run()
			{
				state.removeAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_temporaryobjects, relem);
			}
		});
	}*/
	
	/**
	 *  Remove agent unknown element.
	 * /
	public static void removeElement(IOAVState state, IElement flyweight)
	{
		BDIInterpreter interpreter = BDIAgentFeature.getInterpreter(state);
		Runnable action = interpreter.extaccesses.removeEntry(flyweight);
		if(action!=null)
			action.run();
		
//		Object ragent = BDIAgentFeature.getInterpreter(state).getAgent();
//		state.removeAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_temporaryobjects, relem);
		
		// Hack!!! Only needed for external access!
		BDIAgentFeature.getInterpreter(state).getAgentAdapter().wakeup();
	}*/
	
	/**
	 *  Fetch an element and its scope by its complex name (e.g. procap.rp_initiate).
	 *  @param name The name.
	 *  @return An array [restname, scope].
	 * /
	public static Object[] resolveCapability(String name, Object scope, IOAVState state)
	{
		int	idx;
		if((idx=name.indexOf('.'))!=-1)
		{
			String	capname	= name.substring(0, idx);
			name = name.substring(idx+1);
			if(name.indexOf('.')!=-1)
				throw new RuntimeException("Character '.' not allowed in element names.");

			Object caparef	= state.getAttributeValue(scope, OAVBDIRuntimeModel.capability_has_subcapabilities, capname);
			if(caparef==null)
				throw new RuntimeException("No such capability: "+capname);
			scope = state.getAttributeValue(caparef, OAVBDIRuntimeModel.capabilityreference_has_capability);
		}
		
		return new Object[]{name, scope};
	}*/

	/**
	 *  Fetch an element and its scope by its complex name (e.g. procap.rp_initiate).
	 *  @param name The name.
	 *  @param type The object type (belief, beliefset, goal, internalevent, messageevent).
	 *  @param rcapa The runtime scope.
	 *  @param forward	resolve only forward (outer to inner).
	 *  @return An array [restname, scope].
	 */
	public static Object[] resolveCapability(String name, OAVObjectType type, Object rcapa, IOAVState state)
	{
		return resolveCapability(name, type, rcapa, state, true);
	}

	/**
	 *  Fetch an element and its scope by its complex name (e.g. procap.rp_initiate).
	 *  @param name The name.
	 *  @param type The object type (belief, beliefset, goal, internalevent, messageevent).
	 *  @param rcapa The runtime scope.
	 *  @param recurse	recurse (true) or resolve only one level (false).
	 *  @return An array [restname, scope].
	 */
	public static Object[] resolveCapability(String name, OAVObjectType type, Object rcapa, IOAVState state, boolean recurse)
	{
		// If name if in dot-notation resolve directly to target.
		int	idx;
		if((idx=name.indexOf('.'))!=-1)
		{
			String	capname	= name.substring(0, idx);
			name = name.substring(idx+1);
			if(name.indexOf('.')!=-1)
				throw new RuntimeException("Character '.' not allowed in element names.");

			Object caparef	= state.getAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities, capname);
			if(caparef==null)
				throw new RuntimeException("No such capability: "+capname);
			rcapa = state.getAttributeValue(caparef, OAVBDIRuntimeModel.capabilityreference_has_capability);
		}
		
		Object[] ret = new Object[]{name, rcapa};
		
		// Check if name is a reference. Then resolve reference further.
		if(recurse)
		{
			Object refelem = null;
			Object mcapa = state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.belief_type) || type.isSubtype(OAVBDIMetaModel.beliefreference_type)))
			{
				if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefrefs, name))
					refelem = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefrefs, name);
			}
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.beliefset_type) || type.isSubtype(OAVBDIMetaModel.beliefsetreference_type)))
			{
				if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsetrefs, name))
					refelem = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsetrefs, name);
			}
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.goal_type) || type.isSubtype(OAVBDIMetaModel.goalreference_type)))
			{
				if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_goalrefs, name))
					refelem = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_goalrefs, name);
			}
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.internalevent_type) || type.isSubtype(OAVBDIMetaModel.internaleventreference_type)))
			{
				if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_internaleventrefs, name))
					refelem = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_internaleventrefs, name);
			}
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.messageevent_type) || type.isSubtype(OAVBDIMetaModel.messageeventreference_type)))
			{	
				if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_messageeventrefs, name))
					refelem = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_messageeventrefs, name);
			}
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.expression_type) || type.isSubtype(OAVBDIMetaModel.expressionreference_type)))
			{	
				if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_expressionrefs, name))
					refelem = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_expressionrefs, name);
			}
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.condition_type)))// || type.isSubtype(OAVBDIMetaModel.conditionreference_type))
			{
				// todo: support me?!
			}
	
			if(refelem!=null)
			{
				name = (String)state.getAttributeValue(refelem,  OAVBDIMetaModel.elementreference_has_concrete);
				if(name!=null)
				{
					ret = resolveCapability(name, type, rcapa, state, true);
				}
				else
				{
					// Abstract element, should be assigned from outer scope.
					Object	abstractsource	= state.getAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_abstractsources, refelem);
					if(abstractsource==null)
					{
						throw new RuntimeException("Abstract element is not assigned: "+refelem);
					}
					Object	sourceelem	= state.getAttributeValue(abstractsource, OAVBDIRuntimeModel.abstractsource_has_source);
					Object	sourcecapa	= state.getAttributeValue(abstractsource, OAVBDIRuntimeModel.abstractsource_has_rcapa);
					name	= (String)state.getAttributeValue(sourceelem, OAVBDIMetaModel.modelelement_has_name);
					ret	= resolveCapability(name, type, sourcecapa, state, true);
				}
					
			}
		}
		
		return ret;
	}
	
	
	/**
	 *  Fetch an element and its scope by its complex name (e.g. procap.rp_initiate).
	 *  @param name The name.
	 *  @param type The object type (belief, beliefset, goal, internalevent, messageevent).
	 *  @param rcapa The runtime scope.
	 *  @param forward	resolve only forward (outer to inner).
	 *  @return An array [restname, scope].
	 */
	public static Object[] resolveMCapability(String name, OAVObjectType type, Object mcapa, IOAVState state)
	{
		return resolveMCapability(name, type, mcapa, state, true, "");
	}

	/**
	 *  Fetch an element and its scope by its complex name (e.g. procap.rp_initiate).
	 *  @param name The name.
	 *  @param type The object type (belief, beliefset, goal, internalevent, messageevent).
	 *  @param rcapa The runtime scope.
	 *  @param recurse	recurse (true) or resolve only one level (false).
	 *  @return An array [restname, scope, path].
	 */
	public static Object[] resolveMCapability(String name, OAVObjectType type, Object mcapa, IOAVState state, boolean recurse, String path)
	{
		Object[] ret;  // Object[]{name, mcapa, capname};

		int	idx;
		if((idx=name.indexOf('.'))!=-1)
		{
			String	capname	= name.substring(0, idx);
			name = name.substring(idx+1);
			if(name.indexOf('.')!=-1)
				throw new RuntimeException("Character '.' not allowed in element names.");

			Object mcaparef	= state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs, capname);
			if(mcaparef==null)
				throw new RuntimeException("No such capability: "+capname);
			mcapa = state.getAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_capability);
			
			path	= path.equals("") ? capname : path+"."+capname;
		}
		
		// Check if name is a reference. Then resolve reference further.
		if(recurse)
		{
			Object refelem = null;
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.belief_type) || type.isSubtype(OAVBDIMetaModel.beliefreference_type)))
			{
				if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefrefs, name))
					refelem = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefrefs, name);
			}
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.beliefset_type) || type.isSubtype(OAVBDIMetaModel.beliefsetreference_type)))
			{
				if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsetrefs, name))
					refelem = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsetrefs, name);
			}
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.goal_type) || type.isSubtype(OAVBDIMetaModel.goalreference_type)))
			{
				if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_goalrefs, name))
					refelem = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_goalrefs, name);
			}
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.internalevent_type) || type.isSubtype(OAVBDIMetaModel.internaleventreference_type)))
			{
				if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_internaleventrefs, name))
					refelem = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_internaleventrefs, name);
			}
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.messageevent_type) || type.isSubtype(OAVBDIMetaModel.messageeventreference_type)))
			{	
				if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_messageeventrefs, name))
					refelem = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_messageeventrefs, name);
			}
			if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.condition_type)))// || type.isSubtype(OAVBDIMetaModel.conditionreference_type))
			{
				// todo: support me?!
			}
	
			if(refelem!=null)
			{
				name = (String)state.getAttributeValue(refelem, OAVBDIMetaModel.elementreference_has_concrete);
				if(name!=null)
				{
					ret = resolveMCapability(name, type, mcapa, state, true, path);
				}
				else
				{
					throw new UnsupportedOperationException("todo: abstract at model?!");
					
					// Abstract element, should be assigned from outer scope.
//					Object	abstractsource	= state.getAttributeValue(mcapa, OAVBDIRuntimeModel.capability_has_abstractsources, refelem);
//					if(abstractsource==null)
//					{
//						throw new RuntimeException("Abstract element is not assigned: "+refelem);
//					}
//					
//					Object	sourceelem	= state.getAttributeValue(abstractsource, OAVBDIRuntimeModel.abstractsource_has_source);
//					Object	sourcecapa	= state.getAttributeValue(abstractsource, OAVBDIRuntimeModel.abstractsource_has_rcapa);
//					name	= (String)state.getAttributeValue(sourceelem, OAVBDIMetaModel.modelelement_has_name);
//					ret	= resolveCapability(name, type, sourcecapa, state, true);
				}
			}
			else
			{
				// Not a reference.
				ret	= new Object[]{name, mcapa, path};
			}
		}
		else
		{
			ret	= new Object[]{name, mcapa, path};
		}
		
		return ret;	
	}
	
	/**
	 * 
	 * /
	public static void resolveReference(String name, Object scope, IOAVState state, OAVObjectType type)
	{
		Object refelem;
		if(type.isSubtype(OAVBDIMetaModel.belief_type))
			refelem = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_beliefrefs, name);
		else if(type.isSubtype(OAVBDIMetaModel.beliefset_type))
			refelem = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_beliefsetrefs, name);
		else if(type.isSubtype(OAVBDIMetaModel.goal_type))
			refelem = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_goals, name);
		else if(type.isSubtype(OAVBDIMetaModel.internalevent_type))
			refelem = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_internaleventrefs, name);
		else if(type.isSubtype(OAVBDIMetaModel.messageevent_type))
			refelem = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_messageeventrefs, name);
		else
			throw new RuntimeException("Unknown element type: "+type);

		String concrete = (String)state.getAttributeValue(refelem,  OAVBDIMetaModel.elementreference_has_concrete);
		
		Object[] ret = resolveCapability(concrete, scope, state);
	}*/
	
	
	/**
	 *  Get all contained capabilities.
	 *  @param state The state.
	 *  @param rcapa The start capability.
	 *  @return All contained subcapabilities.
	 */
	public static List getAllSubcapabilities(IOAVState state, Object rcapa)
	{
		// Fetch all capabilities.
		List	capas = SCollection.createArrayList();
		capas.add(rcapa);
		
		for(int i=0; i<capas.size(); i++)
		{
			Collection	subcaps	= state.getAttributeValues(capas.get(i), OAVBDIRuntimeModel.capability_has_subcapabilities);
			if(subcaps!=null)
			{
				for(Iterator it=subcaps.iterator(); it.hasNext(); )
				{
					capas.add(state.getAttributeValue(it.next(), OAVBDIRuntimeModel.capabilityreference_has_capability));
				}
			}
		}
		
		return capas;
	}
	
	/**
	 *  Calculate the possible binding value combinations.
	 *  @param state The state.
	 *  @param mel The parameter element.
	 *  @param cel The config parameter element.
	 *  @param fetcher The value fetcher.
	 *  @return The list of binding maps.
	 */
	protected static List calculateBindingElements(IOAVState state, Object mel, Object cel, IValueFetcher fetcher)
	{
		List ret = null;
		Map	bindingparams	= null;
		Collection	params	= state.getAttributeValues(mel, OAVBDIMetaModel.parameterelement_has_parameters);
		if(params!=null)
		{
			Set initializedparams = new HashSet();
			if(cel!=null)
			{
				Collection cparams = state.getAttributeValues(cel, OAVBDIMetaModel.configparameterelement_has_parameters);
				if(cparams!=null)
				{
					for(Iterator it=cparams.iterator(); it.hasNext(); )
					{
						Object cparam = it.next();
						String pname = (String)state.getAttributeValue(cparam, OAVBDIMetaModel.configparameter_has_ref);
						Object param = state.getAttributeValue(mel, OAVBDIMetaModel.parameterelement_has_parameters, pname);
						initializedparams.add(param);
					}
				}
			}
			
			for(Iterator it=params.iterator(); it.hasNext(); )
			{
				Object	param	= it.next();
				if(!initializedparams.contains(param))
				{
					Object	bo	= state.getAttributeValue(param, OAVBDIMetaModel.parameter_has_bindingoptions);
					if(bo!=null)
					{
						if(bindingparams==null)
							bindingparams = new HashMap();
						bindingparams.put(state.getAttributeValue(param, OAVBDIMetaModel.modelelement_has_name),
							AgentRules.evaluateExpression(state, bo, fetcher));
					}
				}
			}
		}
		
		// Calculate bindings and generate candidates. 
		if(bindingparams!=null)
		{			
			String[]	names	= (String[])bindingparams.keySet().toArray(new String[bindingparams.keySet().size()]);
			Object[]	values	= new Object[names.length];
			for(int i=0; i<names.length; i++)
			{
				values[i]	= bindingparams.get(names[i]);
			}
			bindingparams	= null;
			ret = SUtil.calculateCartesianProduct(names, values);
		}
		return ret;
	}

	/**
	 *  Initiate the termination of an agent.
	 *  Creates the end state and a timeout action.
	 */
	public static void startTerminating(final IOAVState state, final Object ragent)
	{
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, 
			OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATING);

		exitRunningState(state, ragent);
		activateEndState(state, ragent);
		
		// Hack! Make timeout explicit/configurable.
		final IInternalBDIAgentFeature interpreter = BDIAgentFeature.getInterpreter(state);
		final IInternalAccess ia = BDIAgentFeature.getInternalAccess(state);
		long tt = 10000;
//		System.out.println("Adding termination timeout: "+interpreter.getAgentAdapter().getComponentIdentifier().getLocalName()+", "+tt);
		
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_timer,  SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).createTimer(tt, 
			new InterpreterTimedObject(BDIAgentFeature.getInternalAccess(state), new CheckedAction()
			{
				public boolean isValid()
				{
					return state.containsObject(ragent);
				}
				
				public void run()
				{
					// todo: test if already canceled?!
//					if(state.containsObject(ragent))
//					{
						// todo: cleanup? or in terminated action?
//						System.out.println("Forcing termination (timeout): "+interpreter.getAgentAdapter().getComponentIdentifier().getLocalName());
						BDIAgentFeature.getInterpreter(state).getLogger(ragent).info("Forcing termination (timeout): "+ia.getComponentIdentifier().getLocalName());
						state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, 
							OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATED);
						((IInternalExecutionFeature)ia.getComponentFeature(IExecutionFeature.class)).wakeup();
//					}
				}
			})));
	}
	
	/**
	 *  Perform any cleanup required for the agent.
	 *  Called after all endgoals and endplans have finished.
	 */
	public static void cleanupAgent(IOAVState state, Object ragent)
	{
		Object magent = state.getAttributeValue(ragent, OAVBDIRuntimeModel.element_has_model);
		IInternalBDIAgentFeature interpreter	= BDIAgentFeature.getInterpreter(state);
		
//				String name = BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName();
//				if(name.indexOf("jcc")!=-1)
//				System.out.println("Terminated agent: "+name);

		// Todo: no more rules should trigger -> No dropping of agent object!? 
//				state.dropObject(ragent);

//				System.out.println("terminated: "+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName());
		
		// Trigger pull beliefs if declared as result
		// todo: currently updates all beliefs/sets, should only update pull beliefs
		Map<String, Object> resultvals = collectResults(state, ragent);
		IArgument[] results = interpreter.getModel(ragent).getResults();
		for(int i=0; i<results.length; i++)
		{
//			interpreter.setResultValue(results[i].getName(), resultvals.get(results[i].getName()));
			BDIAgentFeature.getInternalAccess(state).getComponentFeature(IArgumentsResultsFeature.class).getResults().put(results[i].getName(), resultvals.get(results[i].getName()));
//			interpreter.setResultValue(results[i].getName(), resultvals.get(results[i].getName()));
		}
		
		// Cleanup timers.
		cleanupCapability(state, ragent);

		// Cleanup interpreter resources
		interpreter.cleanup();
		
		// Cancel scheduled actions.
		Collection<?>	actions	= state.getAttributeValues(ragent, OAVBDIRuntimeModel.agent_has_actions);
		if(actions!=null)
		{
			for(Object action: actions)
			{
				((Future)((Object[])action)[1]).setException(new ComponentTerminatedException(BDIAgentFeature.getInternalAccess(state).getComponentIdentifier()));			
			}
		}
		
		// Get kill future.
		Future<Void>	killfuture	= (Future<Void>)state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_killfuture);

		// Clean up state listeners.
		state.dispose();
		
		// Inform kill listeners.		
		if(killfuture!=null)
		{
			killfuture.setResult(null);
		}
	}
	
	/**
	 *  Collect the results.
	 */
	protected static Map<String, Object> collectResults(IOAVState state, Object ragent)
	{
		// Collect results for agent.
		Object magent = state.getAttributeValue(ragent, OAVBDIRuntimeModel.element_has_model);
		IInternalBDIAgentFeature	interpreter	= BDIAgentFeature.getInterpreter(state);
		IArgument[] results = BDIAgentFeature.getInternalAccess(state).getModel().getResults();
		Map res = new HashMap();
		
		for(int i=0; i<results.length; i++)
		{
			boolean found = false; 
			String resname = results[i].getName();
			
			// belief
			{
				Object mbel = state.getAttributeValue(magent, OAVBDIMetaModel.capability_has_beliefs, resname);
				if(mbel!=null)
				{
					Object rbel = state.getAttributeValue(ragent, OAVBDIRuntimeModel.capability_has_beliefs, mbel);
					Object val = BeliefRules.getBeliefValue(state, rbel, ragent);
					res.put(resname, val);
					found = true;
				}
			}
			
			// belief reference
			if(!found)
			{
				Object mbelref = state.getAttributeValue(magent, OAVBDIMetaModel.capability_has_beliefrefs, resname);
				if(mbelref!=null)
				{
					Object[] scope = AgentRules.resolveCapability(resname, OAVBDIMetaModel.belief_type, ragent, state);
				
					Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object mbel = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefs, scope[0]);
					if(mbel!=null)
					{
						// Init on demand.
						if(!state.containsKey(scope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel))
						{
							IFuture	fut	= AgentRules.initBelief(state, scope[1], mbel, null);
							if(!fut.isDone())
								throw new RuntimeException("Future belief not available: "+scope[0]+" in "+scope[1]);
						}
						Object rbel = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel);	
						Object val = BeliefRules.getBeliefValue(state, rbel, scope[1]);
						res.put(resname, val);
						found = true;
					}
				}
			}
			
			if(!found)
			{
				Object mbelset = state.getAttributeValue(magent, OAVBDIMetaModel.capability_has_beliefsets, resname);
				Object rbelset = state.getAttributeValue(ragent, OAVBDIRuntimeModel.capability_has_beliefsets, mbelset);
				if(rbelset!=null)
				{
					Collection coll = state.getAttributeValues(rbelset, OAVBDIRuntimeModel.beliefset_has_facts);
					Class clazz	= (Class)state.getAttributeValue(mbelset, OAVBDIMetaModel.typedelement_has_class);
					Object[] vals = (Object[])Array.newInstance(SReflect.getWrappedType(clazz), coll!=null ? coll.size() : 0);
					if(coll!=null)
					{
						vals = coll.toArray(vals);
					}
					res.put(resname, vals);
					found = true;
				}
			}
				
			if(!found)
			{
				Object mbelsetref = state.getAttributeValue(magent, OAVBDIMetaModel.capability_has_beliefsetrefs, resname);
				if(mbelsetref!=null)
				{
					Object[] scope = AgentRules.resolveCapability(resname, OAVBDIMetaModel.beliefset_type, ragent, state);
				
					Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object mbelset = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
					if(mbelset!=null)
					{
						// Init on demand.
						if(!state.containsKey(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbelset))
						{
							IFuture	fut	= AgentRules.initBeliefSet(state, scope[1], mbelset, null);
							if(!fut.isDone())
								throw new RuntimeException("Future beliefset not available: "+scope[0]+" in "+scope[1]);
						}
						Object rbelset = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbelset);	
						if(rbelset!=null)
						{
							Collection coll = state.getAttributeValues(rbelset, OAVBDIRuntimeModel.beliefset_has_facts);
							Class clazz	= (Class)state.getAttributeValue(mbelset, OAVBDIMetaModel.typedelement_has_class);
							Object[] vals = (Object[])Array.newInstance(SReflect.getWrappedType(clazz), coll!=null ? coll.size() : 0);
							if(coll!=null)
							{
								vals = coll.toArray(vals);
							}
							
							res.put(resname, vals);
							found = true;
						}
					}
				}
			}
			
			if(!found)
				throw new RuntimeException("Could not resolve result belief/set: "+resname);
		}
		
		return res;
//		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_results, res);
	}
}