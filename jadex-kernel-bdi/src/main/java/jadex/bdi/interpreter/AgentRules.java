package jadex.bdi.interpreter;

import jadex.bdi.runtime.IPlanExecutor;
import jadex.bdi.runtime.impl.InterpreterTimedObject;
import jadex.bdi.runtime.impl.InterpreterTimedObjectAction;
import jadex.bridge.IClockService;
import jadex.bridge.ITimedObject;
import jadex.bridge.ITimer;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;
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
import jadex.rules.state.OAVObjectType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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

	/**
	 *  Create the start agent rule.
	 */
	protected static Rule createStartAgentRule()
	{
		ObjectCondition	ragentcon	= new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		ragentcon.addConstraint(new BoundConstraint(null, new Variable("?ragent", OAVBDIRuntimeModel.agent_type)));
		ragentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_state, OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_CREATING));
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	ragent	= assignments.getVariableValue("?ragent");
				// Get map of arguments for initial beliefs values.
				Map	arguments	= (Map)state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_arguments);
				Map	argcopy	= null;
				if(arguments!=null)
				{
					argcopy	= new HashMap();
					argcopy.putAll(arguments);
				}
//				initializeCapabilityInstance(state, ragent, argcopy);	// Only supply copy as map is modified.
				Map parents = new HashMap(); 
				createCapabilityInstance(state, ragent, parents);
				state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_initparents, parents);
				initializeCapabilityInstance(state, ragent);
				state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_initparents, null);
				
				state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE);
				// Remove arguments from state.
				if(arguments!=null) 
					state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_arguments, null);
			}
		};
		Rule rule = new Rule("agent_start", ragentcon, action);
		return rule;
	}

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
	protected static Rule createTerminatingEndAgentRule()
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
	protected static Rule createTerminateAgentRule()
	{
		ObjectCondition ragentcon	= new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		ragentcon.addConstraint(new BoundConstraint(null, new Variable("?ragent", OAVBDIRuntimeModel.agent_type)));
		ragentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_state, 
			OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATED));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				BDIInterpreter	interpreter	= BDIInterpreter.getInterpreter(state);
//				System.out.println("Terminated agent: "+BDIInterpreter.getInterpreter(state).getAgentAdapter().getAgentIdentifier().getLocalName());

				// Todo: no more rules should trigger -> No dropping of agent object!? 
				Object ragent = assignments.getVariableValue("?ragent");
//				state.dropObject(ragent);

//				System.out.println("terminated: "+BDIInterpreter.getInterpreter(state).getAgentAdapter().getAgentIdentifier().getLocalName());
				
				// Cleanup timers.
				cleanupCapability(state, ragent);

				// Cleanup interpreter resources
				interpreter.cleanup();
				
				// Remove kill listeners.
				Collection	killlisteners	= state.getAttributeValues(ragent, OAVBDIRuntimeModel.agent_has_killlisteners);
				if(killlisteners!=null)
				{
					for(Iterator it=killlisteners.iterator(); it.hasNext(); )
					{
						((IResultListener)it.next()).resultAvailable(interpreter.getAgentAdapter().getAgentIdentifier());
					}
				}
				
				// Clean up state listeners.
				state.dispose();
			}
		};
		
		Rule agent_terminating	= new Rule("agent_terminated", ragentcon, action);
		return agent_terminating;
	}
	
	/**
	 *  Cleanup timers of a capability and its subcapabilities.
	 *  @param rcapa The capability.
	 */
	protected static void cleanupCapability(IOAVState state, Object rcapa)
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
		if(plans!=null)
		{
			for(Iterator it=plans.iterator(); it.hasNext(); )
			{
				Object rplan = it.next();
				IPlanExecutor	executor	= BDIInterpreter.getInterpreter(state).getPlanExecutor(rplan);
				if(executor!=null)
					executor.cleanup(rplan);
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
	 * /
	protected static Rule createExecuteActionRule()
	{
		Variable	runnable	= new Variable("?runnable", OAVBDIRuntimeModel.java_runnable_type);
		Variable	ragent	= new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		
		ObjectCondition actioncon = new ObjectCondition(runnable.getType()); 
		actioncon.addConstraint(new BoundConstraint(null, runnable));
		
		ObjectCondition ragentcon = new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		ragentcon.addConstraint(new BoundConstraint(null, ragent));
		ragentcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.agent_has_actions, runnable, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object ragent = assignments.getVariableValue("?ragent");
				Runnable runnable = (Runnable)assignments.getVariableValue("?runnable");
//				System.out.println("Executing external action: "+runnable);
				state.removeAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_actions, runnable);
				if(runnable instanceof InterpreterTimedObjectAction)
				{
					if(((InterpreterTimedObjectAction)runnable).isValid())
						runnable.run();
					((InterpreterTimedObjectAction)runnable).cleanup();
				}
				else //if(entries[i] instanceof Runnable)
				{
					runnable.run();
				}
			}
		};
		
		Rule agent_execute_action	= new Rule("agent_execute_action", new AndCondition(new ICondition[]{actioncon, ragentcon}), action);
		return agent_execute_action;
	}*/

	/**
	 *  Cleanup rule for removing change events.
	 */
	protected static Rule createRemoveChangeEventRule()
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
//				System.err.println("removing: "+changeevent+", "+BDIInterpreter.getInterpreter(state).getAgentAdapter().getAgentIdentifier());
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
			Map kernelprops = BDIInterpreter.getInterpreter(state).getKernelProperties();
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
								((IClockService)BDIInterpreter.getInterpreter(state).getAgentAdapter().getPlatform()
								.getService(IClockService.TYPE)).createTimer(update.longValue(), to[0]));
						}
					});
					
//					// changed *.class to *.TYPE due to javaflow bug
					state.setAttributeValue(rbel, OAVBDIRuntimeModel.typedelement_has_timer, 
						((IClockService)BDIInterpreter.getInterpreter(state).getAgentAdapter().getPlatform()
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
								state.setAttributeValue(rbelset, OAVBDIRuntimeModel.typedelement_has_timer, ((IClockService)BDIInterpreter.getInterpreter(state)
									.getAgentAdapter().getPlatform().getService(IClockService.TYPE)).createTimer(update.longValue(), to[0]));
							}
						});
//						// changed *.class to *.TYPE due to javaflow bug
						state.setAttributeValue(rbelset, OAVBDIRuntimeModel.typedelement_has_timer, ((IClockService)BDIInterpreter.getInterpreter(state)
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
	protected static void createCapabilityInstance(final IOAVState state, final Object rcapa, Map parents)//, Map arguments)
	{
		// Get configuration.
		Object	mcapa	= state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
		Object	mconfig = getConfiguration(state, rcapa);
		
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
			Map kernelprops = BDIInterpreter.getInterpreter(state).getKernelProperties();
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
		
		// Create subcapabilities.
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
							String	refname	= (String)state.getAttributeValue(tmp, OAVBDIMetaModel.initialcapability_has_ref);
							Object	iniref	= state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs, refname);
							if(iniref==mcaparef)
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
					String	conf = (String)state.getAttributeValue(inicap, OAVBDIMetaModel.initialcapability_has_configuration);
					state.setAttributeValue(rsubcapa, OAVBDIRuntimeModel.capability_has_configuration, conf);
				}
				Object rcaparef = state.createObject(OAVBDIRuntimeModel.capabilityreference_type);
				state.setAttributeValue(rcaparef, OAVBDIRuntimeModel.capabilityreference_has_name, name);
				state.setAttributeValue(rcaparef, OAVBDIRuntimeModel.capabilityreference_has_capability, rsubcapa);
				state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities, rcaparef);
				parents.put(rsubcapa, rcapa);
				createCapabilityInstance(state, rsubcapa, parents);//, null);//inivals!=null ? (Map)inivals.get(name) : null);
			}
		}	
	}
	
	/**
	 *  Initialize the runtime state of an agent.
	 *  @param state The state.
	 *  @param rcapa The reference to the capability instance.
	 *  @param inivals Initial values for beliefs (e.g. arguments or config elements from outer capability);
	 */
	protected static void initializeCapabilityInstance(final IOAVState state, final Object rcapa)
	{
		// Get configuration.
		Object	mcapa	= state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
		Object	mconfig = getConfiguration(state, rcapa);
		
//		// Hack!!! cache expression parameters?
		final OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rcapa);
//		InitFetcher fet = new InitFetcher(state, rcapa, parents, arguments);

		// Register belief(set) types for mplan triggers (most be done before beliefs are initialized).
		Collection	mplans	= state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_plans);
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
							BDIInterpreter.getInterpreter(state).getEventReificator().addObservedElement(mbel);
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
							BDIInterpreter.getInterpreter(state).getEventReificator().addObservedElement(mbel);
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
							BDIInterpreter.getInterpreter(state).getEventReificator().addObservedElement(mbel);
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
							BDIInterpreter.getInterpreter(state).getEventReificator().addObservedElement(mgoal);
						}
					}
				}
			}
		}

		// Initialize beliefs.
		Collection	mbels = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_beliefs);
		if(mbels!=null)
		{
			for(Iterator it=mbels.iterator(); it.hasNext(); )
			{
				final Object mbel = it.next();
				// Create runtime belief if not already there.
				if(state.getAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_beliefs, mbel)==null)
				{
					initBelief(state, rcapa, mbel, fetcher);
				}
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
		
		// Initialize beliefs from subcapas.
		Collection caparefs = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities);
		if(caparefs!=null)
		{
			for(Iterator it=caparefs.iterator(); it.hasNext(); )
			{
				Object caparef = it.next();
				Object rsubcapa = state.getAttributeValue(caparef, OAVBDIRuntimeModel.capabilityreference_has_capability);
				initializeCapabilityInstance(state, rsubcapa);
			}
		}
		
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
	
	protected static Object UNDEFINED = new Object();

	/**
	 *  Initialize a belief.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param mbel The belief model.
	 *  @param fetcher The fetcher.
	 */
	public static void initBelief(final IOAVState state, final Object rcapa, final Object mbel, IValueFetcher fetcher)
	{
		Object agent = BDIInterpreter.getInterpreter(state).getAgent();
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
			Object value = findValue(state, rcapa, rbel, parents, fetcher, arguments);
			
			// Set value.
			if(value!=UNDEFINED)
			{
				BeliefRules.setBeliefValue(state, rbel, value);
			}
		}
		
		if(update!=null)
		{
			final ITimedObject[]	to	= new ITimedObject[1];
			final OAVBDIFetcher fet = new OAVBDIFetcher(state, rcapa);
			to[0] = new InterpreterTimedObject(state, new InterpreterTimedObjectAction()
			{
				public void run()
				{
					Object	exp = state.getAttributeValue(mbel, OAVBDIMetaModel.belief_has_fact);
					try
					{
//						Object agent = BDIInterpreter.getInterpreter(state).getAgent();
//						String name = (String)state.getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_name);
						Object value = evaluateExpression(state, exp, fet);
						BeliefRules.setBeliefValue(state, rbel, value);
					}
					catch(Exception e)
					{
						String name = BDIInterpreter.getInterpreter(state).getAgentAdapter().getAgentIdentifier().getName();
						getLogger(state, rcapa).severe("Could not evaluate belief expression: "+name+" "+state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content));
					}
	//					// changed *.class to *.TYPE due to javaflow bug
					state.setAttributeValue(rbel, OAVBDIRuntimeModel.typedelement_has_timer, 
						((IClockService)BDIInterpreter.getInterpreter(state).getAgentAdapter().getPlatform()
						.getService(IClockService.TYPE)).createTimer(update.longValue(), to[0]));
				}
			});
			
	//			// changed *.class to *.TYPE due to javaflow bug
			state.setAttributeValue(rbel, OAVBDIRuntimeModel.typedelement_has_timer, 
				((IClockService)BDIInterpreter.getInterpreter(state).getAgentAdapter().getPlatform()
				.getService(IClockService.TYPE)).createTimer(update.longValue(), to[0]));
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
	protected static Object findValue(IOAVState state, Object rcapa, Object rbel, Map parents, IValueFetcher fetcher, Map arguments)
	{	
		Object ret = UNDEFINED;
		Object mbel = state.getAttributeValue(rbel, OAVBDIRuntimeModel.element_has_model);
		String belname = (String)state.getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name);
		
		if(ret==UNDEFINED)
		{
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
					ret = arguments.get(belname);
				}
				else
				{
					Object magent = state.getAttributeValue(ragent, OAVBDIRuntimeModel.element_has_model);				
					Collection belrefs = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_beliefrefs);
					if(belrefs!=null)
					{
						for(Iterator it=belrefs.iterator(); it.hasNext(); )
						{
							Object belref = it.next();
							String name =(String)state.getAttributeValue(belref, OAVBDIMetaModel.modelelement_has_name);
							Object[] res = resolveCapability(name, OAVBDIMetaModel.belief_type, ragent, state);
							if(res[0].equals(belname) && res[1].equals(rcapa))
							{
								if(arguments.containsKey(name))
									ret = arguments.get(name);
							}
						}
					}
				}
			}
			
			// Try to get value from outer capability (owner).
			// If undefined try go get initial value. 
			for(int i=0; i<ps.size() && ret==UNDEFINED; i++)
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
								Object	fact	= state.getAttributeValue(minibel, OAVBDIMetaModel.belief_has_fact);
								ret	= evaluateExpression(state, fact, fetcher);
							}
						}
					}
				}
			}
		}
		
		// Try to fetch default value which is only contained in original.
		if(state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_beliefs).contains(rbel))
		{		
			// If undefined try to get default value.
			if(ret==UNDEFINED)
			{
				Object	exp = state.getAttributeValue(mbel, OAVBDIMetaModel.belief_has_fact);
				if(exp!=null)
				{
					ret	= evaluateExpression(state, exp, fetcher);
				}
			}
			
			// If undefined try to get basic value.
			if(ret==UNDEFINED)
			{
				Class clazz = (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
				ret = BeliefRules.getInitialValue(clazz);
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
	public static void initBeliefSet(final IOAVState state, final Object rcapa, final Object mbelset, IValueFetcher fetcher)
	{
		Object agent = BDIInterpreter.getInterpreter(state).getAgent();
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
			Object values = findValues(state, rcapa, rbelset, parents, fetcher, arguments);
			
			// Set value.
			if(values!=UNDEFINED)
			{
				for(Iterator it=SReflect.getIterator(values); it.hasNext(); )
					BeliefRules.addBeliefSetValue(state, rbelset, it.next());
			}
		}
		
		if(update!=null)
		{
			final ITimedObject[]	to	= new ITimedObject[1];
			
			final OAVBDIFetcher fet = new OAVBDIFetcher(state, rcapa);
			to[0]	= new InterpreterTimedObject(state, new InterpreterTimedObjectAction()
			{
				public void run()
				{
					Object	exp = state.getAttributeValue(mbelset, OAVBDIMetaModel.beliefset_has_factsexpression);
					try
					{
						Object values	= evaluateExpression(state, exp, fet);
						BeliefRules.updateBeliefSet(state, rbelset, values);
					}
					catch(Exception e)
					{
						String name = BDIInterpreter.getInterpreter(state).getAgentAdapter().getAgentIdentifier().getName();
						getLogger(state, rcapa).severe("Could not evaluate belief expression: "+name+" "+state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content));
					}
					// changed *.class to *.TYPE due to javaflow bug
					state.setAttributeValue(rbelset, OAVBDIRuntimeModel.typedelement_has_timer, ((IClockService)BDIInterpreter.getInterpreter(state)
						.getAgentAdapter().getPlatform().getService(IClockService.TYPE)).createTimer(update.longValue(), to[0]));
				}
			});
//			// changed *.class to *.TYPE due to javaflow bug
			state.setAttributeValue(rbelset, OAVBDIRuntimeModel.typedelement_has_timer, ((IClockService)BDIInterpreter.getInterpreter(state)
				.getAgentAdapter().getPlatform().getService(IClockService.TYPE)).createTimer(update.longValue(), to[0]));
		}
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
	protected static Object findValues(IOAVState state, Object rcapa, Object rbelset, Map parents, IValueFetcher fetcher, Map arguments)
	{	
		Object ret = UNDEFINED;
		Object mbelset = state.getAttributeValue(rbelset, OAVBDIRuntimeModel.element_has_model);
		String belsetname = (String)state.getAttributeValue(mbelset, OAVBDIMetaModel.modelelement_has_name);
		
		if(ret==UNDEFINED)
		{
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
					ret = arguments.get(belsetname);
				}
				else
				{
					Object magent = state.getAttributeValue(ragent, OAVBDIRuntimeModel.element_has_model);
					Collection belsetrefs = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_beliefsetrefs);
					if(belsetrefs!=null)
					{
						for(Iterator it=belsetrefs.iterator(); it.hasNext(); )
						{
							Object belsetref = it.next();
							String name =(String)state.getAttributeValue(belsetref, OAVBDIMetaModel.modelelement_has_name);
							Object[] res = resolveCapability(name, OAVBDIMetaModel.beliefset_type, ragent, state);
							if(res[0].equals(belsetname) && res[1].equals(rcapa))
							{
								if(arguments.containsKey(name))
									ret = arguments.get(name);
							}
						}
					}
				}
			}
			
			// Try to get value from outer capability (owner).
			// If undefined try go get initial value. 
			for(int i=0; i<ps.size() && ret==UNDEFINED; i++)
			{
				Object mconfig = getConfiguration(state, ps.get(i));
				if(mconfig!=null)
				{
					// Initial beliefs in configuration
					Collection	minibelsets = state.getAttributeValues(mconfig, OAVBDIMetaModel.configuration_has_initialbeliefsets);
					if(minibelsets!=null)
					{
						for(Iterator it=minibelsets.iterator(); it.hasNext(); )
						{
							Object	minibelset	= it.next();
							String name =(String)state.getAttributeValue(minibelset, OAVBDIMetaModel.configbeliefset_has_ref);
							Object[] res = resolveCapability(name, OAVBDIMetaModel.beliefset_type, ps.get(i), state);
							if(res[0].equals(belsetname) && res[1].equals(rcapa))
							{
								Object	fact = state.getAttributeValue(minibelset, OAVBDIMetaModel.beliefset_has_factsexpression);
								Collection	facts = state.getAttributeValues(minibelset, OAVBDIMetaModel.beliefset_has_facts);
								if(fact!=null)
								{
									ret = evaluateExpression(state, fact, fetcher);
								}
								else if(facts!=null)
								{
									List values	= new ArrayList();
									for(Iterator it2=facts.iterator(); it2.hasNext(); )
									{
										fact	= it2.next();
										values.add(evaluateExpression(state, fact, fetcher));
									}
									ret	= values;
								}
							}
						}
					}
				}
			}
		}
		
		// Try to fetch default value which is only contained in original.
		if(state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_beliefsets).contains(rbelset))
		{		
			// If undefined try to get default value.
			if(ret==UNDEFINED)
			{
				Collection	facts	= state.getAttributeValues(mbelset, OAVBDIMetaModel.beliefset_has_facts);
				if(facts!=null)
				{
					List values	= new ArrayList();
					for(Iterator it2=facts.iterator(); it2.hasNext(); )
					{
						Object	fact	= it2.next();
						values.add(evaluateExpression(state, fact, fetcher));
					}
					ret	= values;
				}
				else
				{
					Object	factsexp	= state.getAttributeValue(mbelset, OAVBDIMetaModel.beliefset_has_factsexpression);
					if(factsexp!=null)
					{
						ret	= evaluateExpression(state, factsexp, fetcher);
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
	 */
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
							if(inivals==null)
								inivals	= new HashMap();
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
							if(inivals==null)
								inivals	= new HashMap();
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
	}
	
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

		IParsedExpression	pex = (IParsedExpression)state.getAttributeValue(mexp, OAVBDIMetaModel.expression_has_content);
		try
		{
			ret	= pex.getValue(fetcher);
		}
		catch(Exception e)
		{
			// Hack!!! Exception should be propagated.
			System.err.println(pex.getExpressionText());
			e.printStackTrace();
		}
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
						Class clazz = (Class)state.getAttributeValue(mparam, OAVBDIMetaModel.typedelement_has_class);
						
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
					Class clazz = (Class)state.getAttributeValue(mparamset, OAVBDIMetaModel.typedelement_has_class);

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
		BDIInterpreter interpreter = BDIInterpreter.getInterpreter(state);
		final Object ragent = BDIInterpreter.getInterpreter(state).getAgent();
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
		BDIInterpreter interpreter = BDIInterpreter.getInterpreter(state);
		Runnable action = interpreter.extaccesses.removeEntry(flyweight);
		if(action!=null)
			action.run();
		
//		Object ragent = BDIInterpreter.getInterpreter(state).getAgent();
//		state.removeAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_temporaryobjects, relem);
		
		// Hack!!! Only needed for external access!
		BDIInterpreter.getInterpreter(state).getAgentAdapter().wakeup();
	}*/
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public static Logger getLogger(IOAVState state, Object rcapa)
	{
		// get logger with unique capability name
		// todo: implement getDetailName()
		//String name = getDetailName();
		String name = rcapa.toString();
		Logger ret = LogManager.getLogManager().getLogger(name);
		
		// if logger does not already exists, create it
		if(ret==null)
		{
			// Hack!!! Might throw exception in applet / webstart.
			try
			{
				ret = Logger.getLogger(name);
				initLogger(state, rcapa, ret);
				//System.out.println(logger.getParent().getLevel());
			}
			catch(SecurityException e)
			{
				// Hack!!! For applets / webstart use anonymous logger.
				ret	= Logger.getAnonymousLogger();
				initLogger(state, rcapa, ret);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Init the logger with capability settings.
	 *  @param logger The logger.
	 */
	protected static void initLogger(IOAVState state, Object rcapa, Logger logger)
	{
		// get logging properties (from ADF)
		// the level of the logger
		// can be Integer or Level
		
		Object prop = AgentRules.getPropertyValue(state, rcapa, "logging.level");
		Level level = prop==null? Level.SEVERE: (Level)prop;
		logger.setLevel(level);

		// if logger should use Handlers of parent (global) logger
		// the global logger has a ConsoleHandler(Level:INFO) by default
		prop = AgentRules.getPropertyValue(state, rcapa, "logging.useParentHandlers");
		if(prop!=null)
		{
			logger.setUseParentHandlers(((Boolean)prop).booleanValue());
		}
			
		// add a ConsoleHandler to the logger to print out
        // logs to the console. Set Level to given property value
		prop = AgentRules.getPropertyValue(state, rcapa, "addConsoleHandler");
		if(prop!=null)
		{
            ConsoleHandler console = new ConsoleHandler();
            console.setLevel(Level.parse(prop.toString()));
            logger.addHandler(console);
        }
		
		// Code adapted from code by Ed Komp: http://sourceforge.net/forum/message.php?msg_id=6442905
		// if logger should add a filehandler to capture log data in a file. 
		// The user specifies the directory to contain the log file.
		// $scope.getAgentName() can be used to have agent-specific log files 
		//
		// The directory name can use special patterns defined in the
		// class, java.util.logging.FileHandler, 
		// such as "%h" for the user's home directory.
		// 
		String logfile =	(String)AgentRules.getPropertyValue(state, rcapa, "logging.file");
		if(logfile!=null)
		{
		    try
		    {
			    Handler fh	= new FileHandler(logfile);
		    	fh.setFormatter(new SimpleFormatter());
		    	logger.addHandler(fh);
		    }
		    catch (IOException e)
		    {
		    	System.err.println("I/O Error attempting to create logfile: "
		    		+ logfile + "\n" + e.getMessage());
		    }
		}
	}

	/**
	 *  Fetch an element and its scope by its complex name (e.g. amscap.ams_create_agent).
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
	 *  Fetch an element and its scope by its complex name (e.g. amscap.ams_create_agent).
	 *  @param name The name.
	 *  @param type The object type (belief, beliefset, goal, internalevent, messageevent).
	 *  @param rcapa The runtime scope.
	 *  @return An array [restname, scope].
	 */
	public static Object[] resolveCapability(String name, OAVObjectType type, Object rcapa, IOAVState state)
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
		if(refelem==null && (type==null || type.isSubtype(OAVBDIMetaModel.condition_type)))// || type.isSubtype(OAVBDIMetaModel.conditionreference_type))
		{
			// todo: support me?!
		}

		if(refelem!=null)
		{
			name = (String)state.getAttributeValue(refelem,  OAVBDIMetaModel.elementreference_has_concrete);
			ret = resolveCapability(name, type, rcapa, state);
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
							bindingparams	= new HashMap();
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
		final BDIInterpreter interpreter = BDIInterpreter.getInterpreter(state);
		Long prop = (Long)state.getAttributeValue(ragent, OAVBDIRuntimeModel.capability_has_properties, TERMINATION_TIMEOUT);
		long tt = prop!=null? prop.longValue(): 10000;
		
		// changed *.class to *.TYPE due to javaflow bug
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_timer, ((IClockService)interpreter.getAgentAdapter().getPlatform()
			.getService(IClockService.TYPE)).createTimer(tt, new InterpreterTimedObject(state, new InterpreterTimedObjectAction()
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
						getLogger(state, ragent).info("Forcing termination (timeout): "+interpreter.getAgentAdapter().getAgentIdentifier().getLocalName());
						state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, 
							OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATED);
						interpreter.getAgentAdapter().wakeup();
//					}
				}
			})));
	}
	
	/**
	 *  Get a property value.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param name The name.
	 *  @return The property value.
	 */
	public static Object getPropertyValue(IOAVState state, Object rcapa, String name)
	{
		Object rparam = state.getAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_properties, name);
		return rparam==null? null: state.getAttributeValue(rparam, OAVBDIRuntimeModel.parameter_has_value);
	}
}