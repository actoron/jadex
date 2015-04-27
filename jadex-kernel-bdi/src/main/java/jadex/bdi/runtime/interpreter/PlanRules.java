package jadex.bdi.runtime.interpreter;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.BDIFailureException;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IPlanExecutor;
import jadex.bdi.runtime.TimeoutException;
import jadex.bdi.runtime.impl.flyweights.ChangeEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.GoalFlyweight;
import jadex.bdi.runtime.impl.flyweights.InternalEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.MessageEventFlyweight;
import jadex.bridge.CheckedAction;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimer;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.AndConstraint;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.Constant;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.IPriorityEvaluator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.LiteralReturnValueConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.OrConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Static helper class for plan rules and actions.
 *  
 *  Plan rules are responsible for:
 *  - create a plan body (when lifecycle state = new 
 *  	and processing state = ready -> create plan body)
 *  - execute body code (when lifecycle state = body 
 *  	and processing state = ready -> execute body code)
 *  - execute passed code (when lifecycle state = passed 
 *  	and processing state = ready -> execute passed code)
 *  - execute failed code (when lifecycle state = failed 
 *  	and processing state = ready -> execute failed code)
 *  - execute aborted code (when lifecycle state = aborted 
 *  	and processing state = ready -> execute aborted code)
 *  
 *  - continue plan processing when subgoal finished -> set processing state = ready 
 *  
 *  - removing a plan (when processing state = fishied -> copyback parameters)
 *  
 *  - abort a plan (when lifecycle state = aborted -> abort subgoals)
 */
public class PlanRules
{
	//-------- constants --------
	
	/** Tick timer constant. */
	public static final long TICK_TIMER = -2;
	
	//-------- helper methods --------
	
	/**
	 *  Adopt a plan.
	 *  Adds the plan to the state (planbase).
	 *  @param state	The state
	 *  @param rcapa	The capability.
	 *  @param rgoal	The goal.
	 */
	public static void adoptPlan(IOAVState state, Object rcapa, Object rplan)
	{
//		System.out.println("adoptPlan: Setting plan to ready: "
//				+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//				+", "+rplan);
		state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_plans, rplan);
		state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
		
//		Object	reason	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_reason);
//		if(reason!=null && state.getType(reason).isSubtype(OAVBDIRuntimeModel.goal_type))
//		{
//			state.addAttributeValue(reason, OAVBDIRuntimeModel.goal_has_plans, rplan);
//		}
	}
	
	/**
	 *  Instantiate a plan.
	 *  @param state	The state
	 *  @param rcap	The capability.
	 *  @param mplan	The plan model.
	 *  @param cplan	The plan configuration (if any).
	 *  @param preparams From outside supplied parameter values.
	 *  @return The plan instance.
	 */
	public static Object instantiatePlan(IOAVState state, Object rcap, Object mplan, Object cplan, Object reason, Collection preparams, Map bindings, OAVBDIFetcher fetcher)
	{
		Object	rplan	= state.createObject(OAVBDIRuntimeModel.plan_type);
		state.setAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model, mplan);
		state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_NEW);
		state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
		state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_reason, reason);
		state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement, reason);
//		state.addAttributeValue(rcap, OAVBDIRuntimeModel.capability_has_plans, rplan);
//		System.out.println("instantiatePlan: Setting plan to ready: "
//				+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//				+", "+rplan);
//		
		if(fetcher==null)
			fetcher = new OAVBDIFetcher(state, rcap);
		if(reason!=null)
		{
			if(state.getType(reason).isSubtype(OAVBDIRuntimeModel.goal_type))
			{
				fetcher.setRGoal(reason);
			}
			else if(state.getType(reason).isSubtype(OAVBDIRuntimeModel.changeevent_type))
			{
				String	type	= (String)state.getAttributeValue(reason, OAVBDIRuntimeModel.changeevent_has_type);
				Object	value	= state.getAttributeValue(reason, OAVBDIRuntimeModel.changeevent_has_value);
				fetcher.setValue(OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED.equals(type) ? "$addedfact" :
					OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED.equals(type) ? "$removedfact" : "$changedfact", value);
			}
		}
		fetcher.setRPlan(rplan);		
		
		// The preparams are already created and filled with values from EventProcessingRules.createMPlanCandidate().
		Set	doneparams	= new HashSet();	// Remember, which parameters are already set.
		if(preparams!=null)
		{
			for(Iterator it=preparams.iterator(); it.hasNext(); )
			{
				Object preparam = it.next();
				state.addAttributeValue(rplan, OAVBDIRuntimeModel.parameterelement_has_parameters, preparam);
				doneparams.add(state.getAttributeValue(preparam, OAVBDIRuntimeModel.parameter_has_name));
			}
		}
		
		// Init goal mapping parameters
		// todo: this code is not finished
		// - must allow multiple goal mapping specifications -> metamodel
		// - add support for other mappings, internal event, message event
		
		
		boolean iem = reason!=null && state.getType(reason).isSubtype(OAVBDIRuntimeModel.internalevent_type);
		boolean mem = reason!=null && state.getType(reason).isSubtype(OAVBDIRuntimeModel.messageevent_type);
		boolean gom = reason!=null && state.getType(reason).isSubtype(OAVBDIRuntimeModel.goal_type);
		
		Collection coll = state.getAttributeValues(mplan, OAVBDIMetaModel.parameterelement_has_parameters);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				Object mparam = it.next();
				
				if(gom)
				{
					String	paramref	= (String)state.getAttributeValue(mparam, OAVBDIMetaModel.planparameter_has_goalmapping);
					if(paramref!=null)
					{
						int pidx = paramref.lastIndexOf('.');
						String paramname = paramref.substring(pidx+1);
						generateParameterMapping(state, rplan, mparam, paramname, reason, rcap);
						continue;
					}
				}
				else if(mem)
				{
					String paramref	= (String)state.getAttributeValue(mparam, OAVBDIMetaModel.planparameter_has_messageeventmapping);
					if(paramref!=null)
					{
						int pidx = paramref.lastIndexOf('.');
						String paramname = paramref.substring(pidx+1);
						generateParameterMapping(state, rplan, mparam, paramname, reason, rcap);
						continue;
					}
				}
				else if(iem)
				{
					String paramref	= (String)state.getAttributeValue(mparam, OAVBDIMetaModel.planparameter_has_internaleventmapping);
					if(paramref!=null)
					{
						int pidx = paramref.lastIndexOf('.');
						String paramname = paramref.substring(pidx+1);
						generateParameterMapping(state, rplan, mparam, paramname, reason, rcap);
						continue;
					}
				}
			}
		}
		
		coll = state.getAttributeValues(mplan, OAVBDIMetaModel.parameterelement_has_parametersets);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				Object mparamset = it.next();
				String	paramref	= (String)state.getAttributeValue(mparamset, OAVBDIMetaModel.planparameterset_has_goalmapping);
				if(paramref!=null)
				{
					int pidx = paramref.lastIndexOf('.');
					String paramname = paramref.substring(pidx+1);
					generateParameterSetMapping(state, rplan, mparamset, paramname, reason, rcap);
					continue;
				}
				
				paramref	= (String)state.getAttributeValue(mparamset, OAVBDIMetaModel.planparameterset_has_messageeventmapping);
				if(paramref!=null)
				{
					int pidx = paramref.lastIndexOf('.');
					String paramname = paramref.substring(pidx+1);
					generateParameterSetMapping(state, rplan, mparamset, paramname, reason, rcap);
					continue;
				}
				
				paramref	= (String)state.getAttributeValue(mparamset, OAVBDIMetaModel.planparameterset_has_internaleventmapping);
				if(paramref!=null)
				{
					int pidx = paramref.lastIndexOf('.');
					String paramname = paramref.substring(pidx+1);
					generateParameterSetMapping(state, rplan, mparamset, paramname, reason, rcap);
					continue;
				}
			}
		}
		
		// Bindings for plans are done with EventprocessingRules.createMPlanCandidate or via CREATION_ACTION
		AgentRules.initParameters(state, rplan, cplan, fetcher, fetcher, doneparams, bindings, rcap);
		
		// Initialize waitqueue (if defined in model).
		Object	wqtrigger	= state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_waitqueue);
		if(wqtrigger!=null)
		{
			Object	wqwa	= state.createObject(OAVBDIRuntimeModel.waitabstraction_type);
			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa, wqwa);
			
			coll	= state.getAttributeValues(wqtrigger, OAVBDIMetaModel.trigger_has_factaddeds);
			if(coll!=null)
			{
				for(Iterator it=coll.iterator(); it.hasNext(); )
				{
					String	ref	= (String)it.next();
					Object[]	scope	= AgentRules.resolveCapability(ref, OAVBDIMetaModel.beliefset_type, rcap, state);
					Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object	mbelset	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
					Object	rbelset	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbelset);
					state.addAttributeValue(wqwa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds, rbelset);
					BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(rbelset);
				}
			}

			coll	= state.getAttributeValues(wqtrigger, OAVBDIMetaModel.trigger_has_factremoveds);
			if(coll!=null)
			{
				for(Iterator it=coll.iterator(); it.hasNext(); )
				{
					String	ref	= (String)it.next();
					Object[]	scope	= AgentRules.resolveCapability(ref, OAVBDIMetaModel.beliefset_type, rcap, state);
					Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object	mbelset	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
					Object	rbelset	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbelset);
					state.addAttributeValue(wqwa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rbelset);
					BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(rbelset);
				}
			}
			
			coll	= state.getAttributeValues(wqtrigger, OAVBDIMetaModel.trigger_has_factchangeds);
			if(coll!=null)
			{
				for(Iterator it=coll.iterator(); it.hasNext(); )
				{
					String	ref	= (String)it.next();
					// Hack!!! belief or beliefset???
					Object[]	scope	= AgentRules.resolveCapability(ref, null, rcap, state);
					Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object	mbelset	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
					if(mbelset!=null)
					{
						Object	rbelset	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbelset);
						state.addAttributeValue(wqwa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rbelset);
						BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(rbelset);
					}
					else
					{
						Object	mbel	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefs, scope[0]);
						Object	rbel	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel);
						state.addAttributeValue(wqwa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rbel);
						BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(rbel);
					}
				}
			}

			coll	= state.getAttributeValues(wqtrigger, OAVBDIMetaModel.trigger_has_goalfinisheds);
			if(coll!=null)
			{
				for(Iterator it=coll.iterator(); it.hasNext(); )
				{
					Object	triggerref	= it.next();
					Object	match	= state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_match);
					if(match!=null)
						throw new RuntimeException("Match expression not (yet) supported for waitqueues.");
					String	ref	= (String)state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_ref);
					Object[]	scope	= AgentRules.resolveCapability(ref, OAVBDIMetaModel.goal_type, rcap, state);
					Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object	mgoal	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_goals, scope[0]);
					state.addAttributeValue(wqwa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds, mgoal);
					BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(mgoal);
				}
			}
			
			coll	= state.getAttributeValues(wqtrigger, OAVBDIMetaModel.trigger_has_internalevents);
			if(coll!=null)
			{
				for(Iterator it=coll.iterator(); it.hasNext(); )
				{
					Object	triggerref	= it.next();
					Object	match	= state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_match);
					if(match!=null)
						throw new RuntimeException("Match expression not (yet) supported for waitqueues.");
					String	ref	= (String)state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_ref);
					Object[]	scope	= AgentRules.resolveCapability(ref, OAVBDIMetaModel.internalevent_type, rcap, state);
					Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object	mevent	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_internalevents, scope[0]);
					state.addAttributeValue(wqwa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes, mevent);
				}
			}
			
			coll	= state.getAttributeValues(wqtrigger, OAVBDIMetaModel.trigger_has_messageevents);
			if(coll!=null)
			{
				for(Iterator it=coll.iterator(); it.hasNext(); )
				{
					Object	triggerref	= it.next();
					Object	match	= state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_match);
					if(match!=null)
						throw new RuntimeException("Match expression not (yet) supported for waitqueues.");
					String	ref	= (String)state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_ref);
					Object[]	scope	= AgentRules.resolveCapability(ref, OAVBDIMetaModel.messageevent_type, rcap, state);
					Object	mscope	= state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object	mevent	= state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_messageevents, scope[0]);
					state.addAttributeValue(wqwa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes, mevent);
				}
			}
			
		}
		
//		System.out.println("instantiating plan: "+mplan);
		
		return rplan;
	}
	
	/**
	 *  Abort a plan.
	 */
	public static void abortPlan(IOAVState state, Object rcapa, Object rplan)
	{
//		System.out.println("abortPlan: Setting plan to ready: "
//				+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//				+", "+rplan);
		String ps = (String)state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate);
		state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED);

		if(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY.equals(ps))
		{
//			System.out.println("abort body: "+rplan);
			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_GOALCLEANUP);
		
			endPlanPart(state, rcapa, rplan, false);
		}
		else // if(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_NEW.equals(ps))
		{
//			System.out.println("abort new: "+rplan);
			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED);
		}
	}

	/**
	 *  End a part (i.e. body/passed/failed/aborted) of a plan.
	 *  Cleanup wait abstraction and timers and drop all subgoals.
	 *  Use cleanupwq flag to also remove elements from the waitqueue,
	 *  when this is the final part of the plan (passed/failed/aborted). 
	 */
	public static void endPlanPart(IOAVState state, Object rcapa, Object rplan, boolean cleanupwq)
	{
		// Cleanup wait abstraction and wait queue
		cleanupPlanWait(state, rcapa, rplan, cleanupwq);
		
		// Drop subgoals
		Collection	subgoals	= state.getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_subgoals);
//		System.out.println("Aborting: "+rplan+" "+state.getAttributeValue(state.getAttributeValue(
//			rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
		if(subgoals!=null)
		{
			for(Iterator it=subgoals.iterator(); it.hasNext(); )
			{
				Object	subgoal	= it.next();
				Object gs = state.getAttributeValue(subgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate);
				
				if(!OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPING.equals(gs) 
					&& !OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED.equals(gs))
				{
					GoalLifecycleRules.dropGoal(state, subgoal);
				}
			}
		}
	}
	
	/**
	 *  Create new parameter and copy value.
	 */
	protected static void generateParameterMapping(IOAVState state, Object rplan, Object mparam, String oname, Object reason, Object rcapa)
	{
		// Create a new rparameter
		String pname = (String)state.getAttributeValue(mparam, OAVBDIMetaModel.modelelement_has_name);
		Class clazz = (Class)state.getAttributeValue(mparam, OAVBDIMetaModel.typedelement_has_class);
		
		// Determine the value from rgoal.parameter
		Object roparam = state.getAttributeValue(reason, OAVBDIRuntimeModel.parameterelement_has_parameters, oname);
		Object roval = null;
		if(roparam!=null)
			roval = state.getAttributeValue(roparam, OAVBDIRuntimeModel.parameter_has_value);
		
		BeliefRules.createParameter(state, pname, roval, clazz, rplan, mparam, rcapa);
	}
	
	/**
	 *  Create new parameter set and copy values.
	 */
	protected static void generateParameterSetMapping(IOAVState state, Object rplan, Object mparamset, String oname, Object reason, Object rcapa)
	{
		// Create a new rparameterset
		String psname = (String)state.getAttributeValue(mparamset, OAVBDIMetaModel.modelelement_has_name);
		Class clazz = (Class)state.getAttributeValue(mparamset, OAVBDIMetaModel.typedelement_has_class);
		
		// Determine the value from rgoal.parameterset
		Object roparamset = state.getAttributeValue(reason, OAVBDIRuntimeModel.parameterelement_has_parametersets, oname);
		Collection rovals = null;
		if(roparamset!=null)
			rovals = state.getAttributeValues(roparamset, OAVBDIRuntimeModel.parameterset_has_values);
		
		BeliefRules.createParameterSet(state, psname, rovals, clazz, rplan, mparamset, rcapa);
	}
	
	//-------- rule methods --------	
	
	/**
	 *  Create the plan execution rule.
	 * /
	protected static Rule createPlanBodyRule()
	{
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, new Variable("?rplan", OAVBDIRuntimeModel.plan_type)));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, 
			OAVBDIRuntimeModel.PLANLIFECYCLESTATE_NEW));
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, new Variable("?rcapa", OAVBDIRuntimeModel.capability_type)));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, 
			new Variable("?rplan", OAVBDIRuntimeModel.plan_type), IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
//				System.out.println("new plan body creation rule triggered");
				
				Object	rplan	= assignments.getVariableValue("?rplan");
				Object	rcapa	= assignments.getVariableValue("?rcapa");
				
				try
				{
					// todo: get plan executor for capability
					BDIInterpreter	interpreter	= BDIAgentFeature.getInterpreter(state);
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_RUNNING);
					interpreter.getPlanExecutor(rplan).createPlanBody(interpreter, rcapa, rplan); // Hack
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
//					System.out.println("New body: "+rplan+" "+body);
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY);
				}
				catch(Exception e)
				{
					if(!(e instanceof BDIFailureException))
						e.printStackTrace();
				}
			}
		};
		
		Rule	executeplan	= new Rule("plan_createbody", new AndCondition(new ICondition[]{plancon, capcon}), action);
		return executeplan;
	}*/
	
	/**
	 *  Create the plan execution rule.
	 */
	public static Rule createPlanBodyExecutionRule()
	{
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable step = new Variable("?step", OAVJavaType.java_integer_type);
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY));
		plancon.addConstraint(new OrConstraint(new IConstraint[]{
			new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_NEW),
			new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY)
		}));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_step, step));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{					
//				System.out.println("plan body execution rule triggered");
				
				Object	rplan	= assignments.getVariableValue("?rplan");
				Object	rcapa	= assignments.getVariableValue("?rcapa");
				
				IInternalBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
				boolean interrupted = false;

				int	step	= ((Integer)assignments.getVariableValue("?step")).intValue();
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_step, Integer.valueOf(step+1));
				
				try
				{
					ip.setCurrentPlan(rplan);
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_RUNNING);

					// On first plan step create body.
					if(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_NEW.equals(state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate)))
					{
						state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY);
					}
//					System.out.println("Body: "+rplan+" "+state.getAttributeValue(state.getAttributeValue(
//						rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
					
					// todo: get plan executor for capability
					// todo: can cause nullpointer when killAgent is called
//		     		System.out.println("executePlanStep: "+ip.getComponentIdentifier()+", "+rplan+", "+
//		     			state.getAttributeValue(state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
					interrupted = ip.getPlanExecutor(rplan).executePlanStep(BDIAgentFeature.getInternalAccess(state), rcapa, rplan); // Hack
//		     		System.out.println("executePlanStep finished: "+ip.getComponentIdentifier()+", "+rplan);
				}
				catch(Exception e)
				{
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_exception, e);
					
					// todo: currently only remembers last plan exception in goal
					Object reason = state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_reason);
					if(reason!=null && state.getType(reason).isSubtype(OAVBDIRuntimeModel.goal_type))
					{
						state.setAttributeValue(reason, OAVBDIRuntimeModel.goal_has_exception, e);
					}
					StringWriter sw	= new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					//System.out.println(cap.getAgent().getName()+": Exception while executing: "+this);
					//e.printStackTrace();
					
					// Log user-level exception (i.e. not BDI exceptions).
					if(!(e instanceof BDIFailureException))
					{
//						Level level = (Level)cap.getPropertybase().getProperty(PROPERTY_LOGGING_LEVEL_EXCEPTIONS);
//						AgentRules.BDIAgentFeature.getInterpreter(state).getLogger(rcapa).log(level, ip.getAgentAdapter().getComponentIdentifier()+
//							": Exception while executing: "+rplan+"\n"+sw);
						BDIAgentFeature.getInterpreter(state).getLogger(rcapa).warning(BDIAgentFeature.getInternalAccess(state).getComponentIdentifier()+
							": Exception while executing: "+rplan+" "+state.getAttributeValue(state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name)+"\n"+sw);
					}
					else
					{
						BDIAgentFeature.getInterpreter(state).getLogger(rcapa).info(BDIAgentFeature.getInternalAccess(state).getComponentIdentifier()+
							": Exception while executing: "+rplan+"\n"+sw);
					}
				}
				ip.setCurrentPlan(null);
				
				if(interrupted)
				{
//					System.out.println("createPlanBodyExecutionRule: Setting plan to ready: "
//							+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//							+", "+rplan);
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, 
						OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
				}
			}
		};
		
		Rule	executeplan	= new Rule("plan_executebody", new AndCondition(new ICondition[]{plancon, capcon}), action);
		return executeplan;
	}
	
	/**
	 *  Create the plan execution rule.
	 */
	public static Rule createPlanPassedExecutionRule()
	{
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable step = new Variable("?step", OAVJavaType.java_integer_type);
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, 
			OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_step, step));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, 
			rplan, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rplan	= assignments.getVariableValue("?rplan");
				Object	rcapa	= assignments.getVariableValue("?rcapa");
//				System.out.println("Passed: "+rplan+" "+state.getAttributeValue(state.getAttributeValue(
//					rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_RUNNING);

				int	step	= ((Integer)assignments.getVariableValue("?step")).intValue();
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_step, Integer.valueOf(step+1));
				
				boolean interrupted = false;
				IInternalBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
				ip.setCurrentPlan(rplan);
				try
				{
					// todo: get plan executor for capability
					// todo: can cause nullpointer when killAgent is called
					interrupted = ip.getPlanExecutor(rplan).executePassedStep(BDIAgentFeature.getInternalAccess(state), rplan); // Hack
				}
				catch(Exception e)
				{
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_exception, e);
					StringWriter	sw	= new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					//System.out.println(cap.getAgent().getName()+": Exception while executing: "+this);
					//e.printStackTrace();
					
					// Log user-level exception (i.e. not BDI exceptions).
					if(!(e instanceof BDIFailureException))
					{
//						Level level = (Level)cap.getPropertybase().getProperty(PROPERTY_LOGGING_LEVEL_EXCEPTIONS);
//						AgentRules.BDIAgentFeature.getInterpreter(state).getLogger(rcapa).log(level, ip.getAgentAdapter().getComponentIdentifier()+
//							": Exception while executing: "+rplan+"\n"+sw);
						BDIAgentFeature.getInterpreter(state).getLogger(rcapa).severe(BDIAgentFeature.getInternalAccess(state).getComponentIdentifier()+
							": Exception while executing: "+rplan+"\n"+sw);
					}
					else
					{
						BDIAgentFeature.getInterpreter(state).getLogger(rcapa).info(BDIAgentFeature.getInternalAccess(state).getComponentIdentifier()+
							": Exception while executing: "+rplan+"\n"+sw);
					}
				}
				ip.setCurrentPlan(null);
				
				if(interrupted)
				{
//					System.out.println("createPlanPassedExecutionRule: Setting plan to ready: "
//							+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//							+", "+rplan);
					assert !OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED
						.equals(state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate));
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, 
						OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
				}
			}
		};
		
		Rule	executeplan	= new Rule("plan_executepassed", new AndCondition(new ICondition[]{plancon, capcon}), action);
		return executeplan;
	}
	
	/**
	 *  Create the plan execution rule.
	 */
	public static Rule createPlanFailedExecutionRule()
	{
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable step = new Variable("?step", OAVJavaType.java_integer_type);
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, 
			OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_step, step));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, 
			rplan, IOperator.CONTAINS));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rplan	= assignments.getVariableValue("?rplan");
				Object	rcapa	= assignments.getVariableValue("?rcapa");
//				System.out.println("Passed: "+rplan+" "+state.getAttributeValue(state.getAttributeValue(
//					rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_RUNNING);
				
				int	step	= ((Integer)assignments.getVariableValue("?step")).intValue();
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_step, Integer.valueOf(step+1));
				
				boolean interrupted = false;
				IInternalBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
				ip.setCurrentPlan(rplan);
				try
				{
					// todo: get plan executor for capability
					// todo: can cause nullpointer when killAgent is called
					interrupted = ip.getPlanExecutor(rplan).executeFailedStep(BDIAgentFeature.getInternalAccess(state), rplan); // Hack
				}
				catch(Exception e)
				{
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_exception, e);
					StringWriter	sw	= new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					//System.out.println(cap.getAgent().getName()+": Exception while executing: "+this);
					//e.printStackTrace();
					
					// Log user-level exception (i.e. not BDI exceptions).
					if(!(e instanceof BDIFailureException))
					{
//						Level level = (Level)cap.getPropertybase().getProperty(PROPERTY_LOGGING_LEVEL_EXCEPTIONS);
//						AgentRules.BDIAgentFeature.getInterpreter(state).getLogger(rcapa).log(level, ip.getAgentAdapter().getComponentIdentifier()+
//							": Exception while executing: "+rplan+"\n"+sw);
						BDIAgentFeature.getInterpreter(state).getLogger(rcapa).severe(BDIAgentFeature.getInternalAccess(state).getComponentIdentifier()+
							": Exception while executing: "+rplan+"\n"+sw);
					}
					else
					{
						BDIAgentFeature.getInterpreter(state).getLogger(rcapa).info(BDIAgentFeature.getInternalAccess(state).getComponentIdentifier()+
								": Exception while executing: "+rplan+"\n"+sw);
					}
				}
				ip.setCurrentPlan(null);
				
				if(interrupted)
				{
//					System.out.println("createPlanFailedExecutionRule: Setting plan to ready: "
//							+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//							+", "+rplan);
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, 
						OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
				}
			}
		};
		
		Rule	executeplan	= new Rule("plan_executefailed", new AndCondition(new ICondition[]{plancon, capcon}), action);
		return executeplan;
	}

	/**
	 *  Create the plan execution rule.
	 */
	public static Rule createPlanAbortedExecutionRule()
	{
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable step = new Variable("?step", OAVJavaType.java_integer_type);

		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, 
			OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_step, step));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, 
			rplan, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rplan	= assignments.getVariableValue("?rplan");
				Object	rcapa	= assignments.getVariableValue("?rcapa");

//				System.out.println("Aborted: "+rplan+" "+state.getAttributeValue(state.getAttributeValue(
//					rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_RUNNING);
				
				int	step	= ((Integer)assignments.getVariableValue("?step")).intValue();
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_step, Integer.valueOf(step+1));
				
				boolean interrupted = false;
				IInternalBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
				ip.setCurrentPlan(rplan);
				try
				{
					// todo: get plan executor for capability
					// todo: can cause nullpointer when killAgent is called
					interrupted = ip.getPlanExecutor(rplan).executeAbortedStep(BDIAgentFeature.getInternalAccess(state), rplan); // Hack
				}
				catch(Exception e)
				{
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_exception, e);
					StringWriter	sw	= new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					//System.out.println(cap.getAgent().getName()+": Exception while executing: "+this);
					//e.printStackTrace();
					
					// Log user-level exception (i.e. not BDI exceptions).
					if(!(e instanceof BDIFailureException))
					{
//						Level level = (Level)cap.getPropertybase().getProperty(PROPERTY_LOGGING_LEVEL_EXCEPTIONS);
//						AgentRules.BDIAgentFeature.getInterpreter(state).getLogger(rcapa).log(level, ip.getAgentAdapter().getComponentIdentifier()+
//							": Exception while executing: "+rplan+"\n"+sw);
						BDIAgentFeature.getInterpreter(state).getLogger(rcapa).severe(BDIAgentFeature.getInternalAccess(state).getComponentIdentifier()+
							": Exception while executing: "+rplan+"\n"+sw);
					}
					else
					{
						BDIAgentFeature.getInterpreter(state).getLogger(rcapa).info(BDIAgentFeature.getInternalAccess(state).getComponentIdentifier()+
							": Exception while executing: "+rplan+"\n"+sw);
					}
				}
				ip.setCurrentPlan(null);
				
				if(interrupted)
				{
//					System.out.println("createPlanAbortedExecutionRule: Setting plan to ready: "
//							+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//							+", "+rplan);
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, 
						OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
				}
			}
		};
		
		Rule	executeplan	= new Rule("plan_executeaborted", new AndCondition(new ICondition[]{plancon, capcon}), action);
		return executeplan;
	}
	
	/**
	 *  Reactivate a plan when goal cleanup is finished.
	 */
	public static Rule createPlanInstanceCleanupFinishedRule()
	{
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_subgoals, null));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate,
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_GOALCLEANUP));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rplan	= assignments.getVariableValue("?rplan");
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
			}
		};
		
		Rule planinstance_goalcleanupfinished	= new Rule("planinstance_goalcleanupfinished", plancon, action);
		return planinstance_goalcleanupfinished;
	}
	
	
	/**
	 *  Reactivate a plan when the goal it waits for is finished.
	 */
	public static Rule createPlanInstanceGoalFinishedRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rgoals = new Variable("$?rgoal", OAVBDIRuntimeModel.goal_type, true, false);
		Variable mgoals = new Variable("$?mgoal", OAVBDIMetaModel.goal_type, true, false);
//		Variable wa = new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
//		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		plancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_goals}, rgoals));
		plancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds}, mgoals));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate,
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING));

//		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_goals, rgoals));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds, mgoals));
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED));
		IConstraint	co1	= new BoundConstraint(null, rgoals, IOperator.CONTAINS);
		IConstraint	co2	= new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoals, IOperator.CONTAINS);
		goalcon.addConstraint(new OrConstraint(new IConstraint[]{co1, co2}));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rplan	= assignments.getVariableValue("?rplan");
				Object	rcapa	= assignments.getVariableValue("?rcapa");
				Object	rgoal	= assignments.getVariableValue("?rgoal");
//				System.out.println("createPlanInstanceGoalFinishedRule: Setting plan to ready: "
//						+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//						+", "+rplan);
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement, rgoal);
				cleanupPlanWait(state, rcapa, rplan, false);
				
				// If dispatched from waitqueue, remove from waitqueue
				Collection	wqelements	= state.getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements);
				if(wqelements!=null && wqelements.contains(rgoal))
				{
					state.removeAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, rgoal);
					// plan should be already contained in rgoal.goal_has_finisheddispatchedplans
				}
				
				// If not previously dispatched to waitqueue, remember plan in goal, to avoid multiple dispatching of finished event.
				else
				{
					state.addAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_finisheddispatchedplans, rplan);					
				}

//				System.out.println("Subgoal finished: "+rplan+" "+state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model)+" "+
//					state.getAttributeValue(state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
			}
		};
		
		Rule	subgoal_finished	= new Rule("planinstance_goalfinished", new AndCondition(new ICondition[]{plancon, goalcon, capcon}), action);
		return subgoal_finished;
	}
	
	
	/**
	 *  Add a goal to the waitqueue of a plan when the goal it waits for is finished.
	 */
	public static Rule createPlanWaitqueueGoalFinishedRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
//		Variable rgoals = new Variable("$?rgoal", OAVBDIRuntimeModel.goal_type, true);
//		Variable mgoals = new Variable("$?mgoal", OAVBDIMetaModel.goal_type, true);
//		Variable wa = new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
//		Variable wa2 = new Variable("?wa2", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rplans = new Variable("$?rplans", OAVBDIRuntimeModel.plan_type, true, false);
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
//		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa2));
		IConstraint co1 = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, null);
		IConstraint co2a = new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_goals}, rgoal, IOperator.EXCLUDES);
		IConstraint co2b = new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds}, mgoal, IOperator.EXCLUDES);
		plancon.addConstraint(new OrConstraint(co1, new AndConstraint(co2a, co2b)));
		plancon.addConstraint(new BoundConstraint(null, rplans, IOperator.EXCLUDES));
//		rplancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitqueuewa, wa));
		IConstraint co3 = new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitqueuewa, OAVBDIRuntimeModel.waitabstraction_has_goals}, rgoal, IOperator.CONTAINS);
		IConstraint co4 = new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitqueuewa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds}, mgoal, IOperator.CONTAINS);
		plancon.addConstraint(new OrConstraint(co3, co4));
		
//		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_goals, rgoals));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds, mgoals));
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
//		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finisheddispatchedplans, rplan, IOperator.EXCLUDES));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finisheddispatchedplans, rplans));
//		IConstraint	co1	= new BoundConstraint(null, rgoals, IOperator.CONTAINS);
//		IConstraint	co2	= new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoals, IOperator.CONTAINS);
//		goalcon.addConstraint(new OrConstraint(new IConstraint[]{co1, co2}));
	
//		ObjectCondition	wacon2 = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon2.addConstraint(new BoundConstraint(null, wa2));
//		IConstraint co1a = new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_goals, rgoal, IOperator.CONTAINS);
//		IConstraint co2a = new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds, mgoal, IOperator.CONTAINS);
//		wacon2.addConstraint(new OrConstraint(new IConstraint[]{co1a, co2a}));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rplan	= assignments.getVariableValue("?rplan");
				Object	rgoal	= assignments.getVariableValue("?rgoal");
				state.addAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, rgoal);
				state.addAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_finisheddispatchedplans, rplan);

//				System.out.println("planwaitqueue_goalfinished: "+rgoal+", "+rplan);
			}
		};
		
		Rule	planwaitqueue_goalfinished	= new Rule("planwaitqueue_goalfinished",
			//new AndCondition(new ICondition[]{plancon, goalcon, new NotCondition(wacon2)}),
			new AndCondition(new ICondition[]{goalcon, plancon}),
			action, IPriorityEvaluator.PRIORITY_1);	// Hack!!! works, because goal will still be referenced in change event
		return planwaitqueue_goalfinished;
	}
	
	/**
	 *  Create the plan instance maintain goal finished rule.
	 */
	public static Rule createPlanInstanceMaintainGoalFinishedRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.maintaingoal_type);
//		Variable wa = new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS, IOperator.NOTEQUAL));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.maintaingoal_type, IOperator.INSTANCEOF));
		
//		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		IConstraint co1 = new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_goals, rgoal, IOperator.CONTAINS);
//		IConstraint co2 = new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds, mgoal, IOperator.CONTAINS);
//		wacon.addConstraint(new OrConstraint(new IConstraint[]{co1, co2}));
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
//		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate,
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING));
		IConstraint co1 = new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_goals}, rgoal, IOperator.CONTAINS);
		IConstraint co2 = new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds}, mgoal, IOperator.CONTAINS);
		plancon.addConstraint(new OrConstraint(new IConstraint[]{co1, co2}));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rplan	= assignments.getVariableValue("?rplan");
				Object	rcapa	= assignments.getVariableValue("?rcapa");
				Object	rgoal	= assignments.getVariableValue("?rgoal");
//				System.out.println("createPlanInstanceMaintainGoalFinishedRule: Setting plan to ready: "
//						+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//						+", "+rplan);
//				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
//				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement, rgoal);
//				cleanupPlanWait(state, rcapa, rplan, false);
//				
				EventProcessingRules.schedulePlanInstanceCandidate(state, rgoal, rplan, rcapa);
				
//				System.out.println("Maintaingoal finished: "+rplan+" "+state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model)+" "+
//					state.getAttributeValue(state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
			}
		};
		
		Rule maintain_subgoal_finished = new Rule("planinstance_maintaingoalfinished", new AndCondition(new ICondition[]{goalcon, plancon, capcon}), action);
		return maintain_subgoal_finished;
	}
	
	/**
	 *  Create the plan removal rule.
	 *  Removes a plan from its capability, when execution has finished.
	 */
	public static Rule createPlanRemovalRule()
	{
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_subgoals, null));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
				OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
		
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(null, rcapa));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, 
			rplan, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rplan	= assignments.getVariableValue("?rplan");
				Object	rcapa	= assignments.getVariableValue("?rcapa");
				Object	reason	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_reason);

//				System.out.println("Removing plan: "+rplan+" "+state.getAttributeValue(state.getAttributeValue(
//					rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));

				
				if(reason!=null && state.getType(reason).isSubtype(OAVBDIRuntimeModel.goal_type))
				{
					// APL handling only required if goal is not finished (e.g. due to target condition)
					if(OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS.equals(state.getAttributeValue(reason, OAVBDIRuntimeModel.goal_has_processingstate)))
					{
						Object mgoal = state.getAttributeValue(reason, OAVBDIRuntimeModel.element_has_model);
						String exclude = (String)state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_exclude);
						
						Object mcand = state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_plancandidate);
						if(mcand!=null)
						{
							// Add mplancandidate to tried candidates if reason is goal.
							if(OAVBDIMetaModel.EXCLUDE_WHEN_TRIED.equals(exclude))
							{
								state.addAttributeValue(reason, OAVBDIRuntimeModel.goal_has_triedmplans, mcand);
							}
							else 
							{
								String planstate = (String)state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate);
								if(OAVBDIMetaModel.EXCLUDE_WHEN_FAILED.equals(exclude) 
									&& OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED.equals(planstate))
								{
									state.addAttributeValue(reason, OAVBDIRuntimeModel.goal_has_triedmplans, mcand);
								}
								else if(OAVBDIMetaModel.EXCLUDE_WHEN_SUCCEEDED.equals(exclude) 
									&& OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED.equals(planstate))
								{
									state.addAttributeValue(reason, OAVBDIRuntimeModel.goal_has_triedmplans, mcand);
								}
							}
						}
						
						// Remove candidate from APL if exclude mode demands this.
						Object apl = state.getAttributeValue(reason, OAVBDIRuntimeModel.processableelement_has_apl);
						if(apl!=null)
						{
							String planstate = (String)state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate);
							if(OAVBDIMetaModel.EXCLUDE_WHEN_TRIED.equals(exclude) 
								|| (OAVBDIMetaModel.EXCLUDE_WHEN_FAILED.equals(exclude) 
								&& OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED.equals(planstate))
								|| (OAVBDIMetaModel.EXCLUDE_WHEN_SUCCEEDED.equals(exclude) 
								&& OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED.equals(planstate)))
							{
								if(mcand!=null)
								{
									// Hack!!! When apl rebuilding is used, apl can be already a new object.
									if(state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_plancandidates).contains(mcand))
										state.removeAttributeValue(apl, OAVBDIRuntimeModel.apl_has_plancandidates, mcand);
									state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_plancandidate, null);
//									System.out.println("PlanRules.createPlanRemovalRule() remove: "+apl+", "+mcand);
								}
								else
								{
									Object rcand = state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_planinstancecandidate);
									if(rcand!=null)
									{
										// Hack!!! When apl rebuilding is used, apl can be already a new object.
										if(state.getAttributeValues(apl, OAVBDIRuntimeModel.plan_has_planinstancecandidate).contains(rcand))
											state.removeAttributeValue(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates, rcand);
										state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_planinstancecandidate, null);
									}
									else
									{
										Object wcand = state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuecandidate);
										assert wcand!=null;
										// Hack!!! When apl rebuilding is used, apl can be already a new object.
										if(state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates).contains(wcand))
											state.removeAttributeValue(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates, wcand);
										state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuecandidate, null);
									}
								}
							}
							
							// Clear apl if empty
							Collection pcs = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_plancandidates);
							Collection pics = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates);
							Collection wqcs = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates);
							if(pcs==null && pics==null && wqcs==null)
							{
//								System.out.println("Set null apl: "+rpe+" "+apl);
								state.setAttributeValue(reason, OAVBDIRuntimeModel.processableelement_has_apl, null);
							}
						}
					}

					// Add finished plan to goal.
					if(OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS.equals(state.getAttributeValue(reason, OAVBDIRuntimeModel.goal_has_processingstate)))
						state.addAttributeValue(reason, OAVBDIRuntimeModel.goal_has_finishedplans, rplan);
					
					// Copy back parameters to goal (if any).
					// todo: this code is not finished
					// - must allow multiple goal mapping specifications -> metamodel
					Object	mplan	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
					Collection coll = state.getAttributeValues(mplan, OAVBDIMetaModel.parameterelement_has_parameters);
					Object mreason = state.getAttributeValue(reason, OAVBDIRuntimeModel.element_has_model);
					if(coll!=null)
					{
						for(Iterator it=coll.iterator(); it.hasNext(); )
						{
							Object mparam = it.next();
							String paramref = (String)state.getAttributeValue(mparam, OAVBDIMetaModel.planparameter_has_goalmapping);
							String dir = (String)state.getAttributeValue(mparam, OAVBDIMetaModel.parameter_has_direction);
							if(paramref!=null && (OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(dir)||OAVBDIMetaModel.PARAMETER_DIRECTION_INOUT.equals(dir)))
							{
								int pidx = paramref.lastIndexOf('.');
								String paramname = paramref.substring(pidx+1);
								
								// Determine the value from rplan parameter
								String pname = (String)state.getAttributeValue(mparam, OAVBDIMetaModel.modelelement_has_name);
								Object rparam = state.getAttributeValue(rplan, 
									OAVBDIRuntimeModel.parameterelement_has_parameters, pname);
								if(rparam!=null)
								{
									// Get/create goal parameter
									Object roparam = state.getAttributeValue(reason, 
										OAVBDIRuntimeModel.parameterelement_has_parameters, paramname);
									if(roparam==null)
									{
										Object mgoalparam = state.getAttributeValue(mreason, OAVBDIMetaModel.parameterelement_has_parameters, paramname);
										Class clazz = (Class)state.getAttributeValue(mgoalparam, OAVBDIMetaModel.typedelement_has_class);
										roparam = BeliefRules.createParameter(state, paramname, null, clazz, reason, mgoalparam, rcapa);
									}								

									Object roval = state.getAttributeValue(rparam, OAVBDIRuntimeModel.parameter_has_value);
									BeliefRules.setParameterValue(state, roparam, roval);
								}
							}
						}
					}
					
					coll = state.getAttributeValues(mplan, OAVBDIMetaModel.parameterelement_has_parametersets);
					if(coll!=null)
					{
						for(Iterator it=coll.iterator(); it.hasNext(); )
						{
							// Todo: multiple mappings
							Object mparamset = it.next();
							String paramref = (String)state.getAttributeValue(mparamset, OAVBDIMetaModel.planparameterset_has_goalmapping);
							String dir = (String)state.getAttributeValue(mparamset, OAVBDIMetaModel.parameterset_has_direction);
							if(paramref!=null && (OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(dir)||OAVBDIMetaModel.PARAMETER_DIRECTION_INOUT.equals(dir)))
							{
								int pidx = paramref.lastIndexOf('.');
								String paramname = paramref.substring(pidx+1);
								
								// Determine the value from rplan parameter
								String pname = (String)state.getAttributeValue(mparamset, OAVBDIMetaModel.modelelement_has_name);
								Object rparamset = state.getAttributeValue(rplan, 
									OAVBDIRuntimeModel.parameterelement_has_parametersets, pname);
								if(rparamset!=null)
								{
									// Get/create goal parameter
									Object roparamset = state.getAttributeValue(reason, 
										OAVBDIRuntimeModel.parameterelement_has_parametersets, paramname);
									if(roparamset==null)
									{
										Object mgoalparamset = state.getAttributeValue(mreason, OAVBDIMetaModel.parameterelement_has_parametersets, paramname);
										if(mgoalparamset==null)
											System.out.println("abc");
										Class clazz = (Class)state.getAttributeValue(mgoalparamset, OAVBDIMetaModel.typedelement_has_class);
										roparamset = BeliefRules.createParameterSet(state, paramname, null, clazz, reason, mgoalparamset, rcapa);
								
									}
									else
									{
										Collection oldvals = state.getAttributeValues(roparamset, OAVBDIRuntimeModel.parameterset_has_values);
										if(oldvals!=null)
										{
											Object[]	avals	= oldvals.toArray();
											for(int i=0; i<avals.length; i++)
												BeliefRules.removeParameterSetValue(state, roparamset, avals[i]);
										}
									}

									Collection newvals = state.getAttributeValues(rparamset, OAVBDIRuntimeModel.parameterset_has_values);
									if(newvals!=null)
									{
										for(Iterator it2=newvals.iterator(); it2.hasNext(); )
										{
											BeliefRules.addParameterSetValue(state, roparamset, it2.next());
										}
									}
								}
							}
						}
					}

				}

				// Required to remove registered reply message events from capability.
				cleanupPlanWait(state, rcapa, rplan, true);

				// Todo: Hack!!! Fix garbage collection in state.
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_reason, null);
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement, null);
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_plancandidate, null);
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_planinstancecandidate, null);
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuecandidate, null);
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa, null);
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body, null);
				
				state.removeAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_plans, rplan);
//				System.out.println("Plan removed: "+rplan+", "+wqes);
			}
		};
		
		Rule	plan_removal	= new Rule("plan_removal", new AndCondition(new ICondition[]{plancon, capacon}), action);
		return plan_removal;
	}

	/**
	 *  Rule to abort a plan when the corresponding goal was deactivated.
	 */
	public static Rule createPlanInstanceAbortRule()
	{
		Variable	rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable	rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable	rcapa	= new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(rgoal.getType());
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS, IOperator.NOTEQUAL));

		ObjectCondition	plancon	= new ObjectCondition(rplan.getType());
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new OrConstraint(
			new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_NEW),
			new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY)));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_reason, rgoal));

		ObjectCondition	capcon	= new ObjectCondition(rcapa.getType());
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));
		
		Rule	abort_plan	= new Rule("planinstance_abort", new AndCondition(new ICondition[]{goalcon, plancon, capcon}), PLAN_ABORT);
		return abort_plan;
	}
	
	/**
	 *  Create the plan creation rule.
	 *  @param usercond	The ADF part of the target condition.
	 *  @param mplan	The plan model element.
	 */
	public static Object[]	createPlanCreationUserRule(Object mplan)
	{
		Variable ragent = new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		Variable mplanvar = new Variable("?mplan", OAVBDIMetaModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
			
		ObjectCondition	ragentcon	= new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		ragentcon.addConstraint(new BoundConstraint(null, ragent));
		ragentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_state, OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE));
		
		ObjectCondition	rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		rcapacon.addConstraint(new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.capability_has_plans}, mplan, IOperator.CONTAINS));
		// Hack??? How to pass mplan to action!?
		rcapacon.addConstraint(new BoundConstraint(new Constant(mplan), mplanvar));
		
		return new Object[]{
			new AndCondition(new ICondition[]{ragentcon, rcapacon}),
			PLAN_CREATION};
	}
	
	/**
	 *  Trigger plan creation on fact changed event.
	 */
	public static Rule createPlanInstanceFactChangedTriggerRule()
	{
		Variable	rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
//		Variable	wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable	rtels	= new Variable("$?rtels", OAVBDIRuntimeModel.element_type, true, false);
		Variable	change	= new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
		Variable	rcapa	= new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	rplancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		rplancon.addConstraint(new BoundConstraint(null, rplan));
//		rplancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		rplancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_factchangeds}, rtels));

		ObjectCondition	capcon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));
			
//		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factchangeds, rtels));

		ObjectCondition	changecon	= new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, change));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtels, IOperator.CONTAINS));
		
		Rule plan_factwait = new Rule("planinstancetrigger_factchanged",
			new AndCondition(new ICondition[]{rplancon, changecon, capcon}),
			PLAN_CHANGEWAIT, IPriorityEvaluator.PRIORITY_1);
		return plan_factwait;
	}		
	
	/**
	 *  Trigger plan creation on fact added event.
	 */
	public static Rule createPlanInstanceFactAddedTriggerRule()
	{
		Variable	rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
//		Variable	wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable	rtels	= new Variable("$?rtels", OAVBDIRuntimeModel.element_type, true, false);
		Variable	change	= new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
		Variable	rcapa	= new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
			
		ObjectCondition	rplancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		rplancon.addConstraint(new BoundConstraint(null, rplan));
//		rplancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		rplancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_factaddeds}, rtels));

		ObjectCondition	capcon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));

//		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factaddeds, rtels));

		ObjectCondition	changecon	= new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, change));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtels, IOperator.CONTAINS));
		
		Rule plan_factwait = new Rule("planinstancetrigger_factadded",
			new AndCondition(new ICondition[]{rplancon, changecon, capcon}),
			PLAN_CHANGEWAIT, IPriorityEvaluator.PRIORITY_1);
		return plan_factwait;
	}

	/**
	 *  Trigger plan creation on fact removed event.
	 */
	public static Rule createPlanInstanceFactRemovedTriggerRule()
	{
		Variable	rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
//		Variable	wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable	rtels	= new Variable("$?rtels", OAVBDIRuntimeModel.element_type, true, false);
		Variable	change	= new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
		Variable	rcapa	= new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	rplancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		rplancon.addConstraint(new BoundConstraint(null, rplan));
//		rplancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		rplancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_factremoveds}, rtels));

		ObjectCondition	capcon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));
			
//		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rtels));

		ObjectCondition	changecon	= new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, change));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtels, IOperator.CONTAINS));
		
		Rule plan_factwait = new Rule("planinstancetrigger_factremoved",
			new AndCondition(new ICondition[]{rplancon, changecon, capcon}),
			PLAN_CHANGEWAIT, IPriorityEvaluator.PRIORITY_1);
		return plan_factwait;
	}	
	
	/**
	 *  Add event to waitqueue of running plan on fact added event.
	 */
	public static Rule createPlanWaitqueueFactAddedTriggerRule()
	{
		Variable	rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
//		Variable	wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
//		Variable	wqwa	= new Variable("?wqwa", OAVBDIRuntimeModel.waitabstraction_type);
//		Variable	rtels	= new Variable("$?rtels", OAVBDIRuntimeModel.element_type, true);
		Variable	rtel	= new Variable("?rtel", OAVBDIRuntimeModel.element_type);
		Variable	change	= new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
			
		ObjectCondition	rplancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		rplancon.addConstraint(new BoundConstraint(null, rplan));
//		rplancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitqueuewa, wqwa));
		rplancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitqueuewa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds}, rtel, IOperator.CONTAINS));
//		rplancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		IConstraint co1 = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, null);
		IConstraint co2 = new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_factaddeds}, rtel, IOperator.EXCLUDES);
		rplancon.addConstraint(new OrConstraint(new IConstraint[]{co1, co2}));

//		ObjectCondition	wqwacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wqwacon.addConstraint(new BoundConstraint(null, wqwa));
//		wqwacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factaddeds, rtels));

		ObjectCondition	changecon	= new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, change));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtel));
//		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtels, IOperator.CONTAINS));

//		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factaddeds, rtel, IOperator.CONTAINS));
		
		Rule factadded_planwaitqueuetrigger = new Rule("planwaitqueuetrigger_factadded",
//			new AndCondition(new ICondition[]{rplancon, changecon, new NotCondition(wacon)}),
			new AndCondition(new ICondition[]{changecon, rplancon}),
			PLAN_CHANGEWAITQUEUE, IPriorityEvaluator.PRIORITY_1);
		return factadded_planwaitqueuetrigger;
	}	
	
	
	/**
	 *  Add event to waitqueue of running plan on fact added event.
	 */
	public static Rule createPlanWaitqueueFactRemovedTriggerRule()
	{
		Variable	rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
//		Variable	wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
//		Variable	wqwa	= new Variable("?wqwa", OAVBDIRuntimeModel.waitabstraction_type);
//		Variable	rtels	= new Variable("$?rtels", OAVBDIRuntimeModel.element_type, true);
		Variable	rtel	= new Variable("?rtel", OAVBDIRuntimeModel.element_type);
		Variable	change	= new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
			
		ObjectCondition	rplancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		rplancon.addConstraint(new BoundConstraint(null, rplan));
//		rplancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitqueuewa, wqwa));
		rplancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitqueuewa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds}, rtel, IOperator.CONTAINS));
//		rplancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		IConstraint co1 = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, null);
		IConstraint co2 = new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_factremoveds}, rtel, IOperator.EXCLUDES);
		rplancon.addConstraint(new OrConstraint(new IConstraint[]{co1, co2}));

//		ObjectCondition	wqwacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wqwacon.addConstraint(new BoundConstraint(null, wqwa));
//		wqwacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rtels));

		ObjectCondition	changecon	= new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, change));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtel));
//		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtels, IOperator.CONTAINS));

//		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rtel, IOperator.CONTAINS));
		
		Rule factremoved_planwaitqueuetrigger = new Rule("planwaitqueuetrigger_factremoved",
//			new AndCondition(new ICondition[]{rplancon, changecon, new NotCondition(wacon)}),
			new AndCondition(new ICondition[]{changecon, rplancon}),
			PLAN_CHANGEWAITQUEUE, IPriorityEvaluator.PRIORITY_1);
		return factremoved_planwaitqueuetrigger;
	}	
	
	
	/**
	 *  Add event to waitqueue of running plan on fact added event.
	 */
	public static Rule createPlanWaitqueueFactChangedTriggerRule()
	{
		Variable	rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
//		Variable	wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
//		Variable	wqwa	= new Variable("?wqwa", OAVBDIRuntimeModel.waitabstraction_type);
//		Variable	rtels	= new Variable("$?rtels", OAVBDIRuntimeModel.element_type, true);
		Variable	rtel	= new Variable("?rtel", OAVBDIRuntimeModel.element_type);
		Variable	change	= new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
			
		ObjectCondition	rplancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		rplancon.addConstraint(new BoundConstraint(null, rplan));
//		rplancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitqueuewa, wqwa));
		rplancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitqueuewa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds}, rtel, IOperator.CONTAINS));
//		rplancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		IConstraint co1 = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, null);
		IConstraint co2 = new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_factchangeds}, rtel, IOperator.EXCLUDES);
		rplancon.addConstraint(new OrConstraint(new IConstraint[]{co1, co2}));

//		ObjectCondition	wqwacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wqwacon.addConstraint(new BoundConstraint(null, wqwa));
//		wqwacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factchangeds, rtels));

		ObjectCondition	changecon	= new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, change));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtel));
//		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtels, IOperator.CONTAINS));

//		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factchangeds, rtel, IOperator.CONTAINS));
		
		Rule factchanged_planwaitqueuetrigger = new Rule("planwaitqueuetrigger_factchanged",
//			new AndCondition(new ICondition[]{rplancon, changecon, new NotCondition(wacon)}),
			new AndCondition(new ICondition[]{changecon, rplancon}),
			PLAN_CHANGEWAITQUEUE, IPriorityEvaluator.PRIORITY_1);
		return factchanged_planwaitqueuetrigger;
	}	

	/**
	 *  Trigger plan continuation on external condition.
	 */
	public static Rule createPlanInstanceExternalConditionTriggerRule()
	{
		Variable cond	= new Variable("?cond", OAVBDIRuntimeModel.java_externalcondition_type);
		Variable rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa	= new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
//		Variable wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
			
		ObjectCondition	condcon = new ObjectCondition(cond.getType());
		condcon.addConstraint(new BoundConstraint(null, cond));
		condcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.java_externalcondition_type.getAttributeType("true"), Boolean.TRUE));

		ObjectCondition	rplancon = new ObjectCondition(rplan.getType());
		rplancon.addConstraint(new BoundConstraint(null, rplan));
//		rplancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		rplancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_externalconditions}, cond, IOperator.CONTAINS));

		ObjectCondition	capcon = new ObjectCondition(rcapa.getType());
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));

//		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_externalconditions, cond, IOperator.CONTAINS));
		
		Rule plan_wait = new Rule("planinstancetrigger_externalcondition",
			new AndCondition(new ICondition[]{condcon, rplancon, capcon}),
			PLAN_EXTERNALCONDITIONWAIT);
		return plan_wait;
	}	
	
	/**
	 *  Trigger plan creation on fact changed event.
	 */
	public static Rule createPlanFactChangedTriggerRule()
	{
		Variable	mplan	= new Variable("?mplan", OAVBDIMetaModel.plan_type);
		Variable	trigger	= new Variable("?trigger", OAVBDIMetaModel.plantrigger_type);
		Variable	ref	= new Variable("?ref", OAVJavaType.java_string_type);
		Variable	mcapa	= new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable	rcapa	= new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable	rtel	= new Variable("?rtel", OAVBDIRuntimeModel.element_type);
		Variable	rtargetcapa	= new Variable("?rtargetcapa", OAVBDIRuntimeModel.capability_type);
		Variable	change	= new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
			
		ObjectCondition	mtricon = new ObjectCondition(OAVBDIMetaModel.plantrigger_type);
		mtricon.addConstraint(new BoundConstraint(null, trigger));
		mtricon.addConstraint(new BoundConstraint(OAVBDIMetaModel.trigger_has_factchangeds, Arrays.asList(new Variable[]
		{
			new Variable("$?x", OAVJavaType.java_string_type, true, false),
			ref,
			new Variable("$?y", OAVJavaType.java_string_type, true, false),
		}), IOperator.EQUAL));

		ObjectCondition	mplancon = new ObjectCondition(OAVBDIMetaModel.plan_type);
		mplancon.addConstraint(new BoundConstraint(null, mplan));
		mplancon.addConstraint(new BoundConstraint(OAVBDIMetaModel.plan_has_trigger, trigger));

		ObjectCondition	mcapacon = new ObjectCondition(OAVBDIMetaModel.capability_type);
		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_plans, mplan, IOperator.CONTAINS));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));

		ObjectCondition	rtelcon	= new ObjectCondition(OAVBDIRuntimeModel.typedelement_type);
		rtelcon.addConstraint(new BoundConstraint(null, rtel));

		ObjectCondition	targetcapcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		targetcapcon.addConstraint(new BoundConstraint(null, rtargetcapa));
		targetcapcon.addConstraint(new OrConstraint(new IConstraint[]
		{
			new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefs, rtel, IOperator.CONTAINS),
			new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefsets, rtel, IOperator.CONTAINS)
		}));

		ObjectCondition	changecon	= new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, change));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtel));
		changecon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa, ref, rtel, rtargetcapa})));
		
		Rule plan_creation = new Rule("plantrigger_factchanged",
			new AndCondition(new ICondition[]{mtricon, mplancon, mcapacon, capcon, rtelcon, targetcapcon, changecon}),
			PLAN_CHANGECREATION, IPriorityEvaluator.PRIORITY_1);
		return plan_creation;
	}
	
	
	/**
	 *  Trigger plan creation on fact added event.
	 */
	public static Rule createPlanFactAddedTriggerRule()
	{
		Variable	mplan	= new Variable("?mplan", OAVBDIMetaModel.plan_type);
		Variable	trigger	= new Variable("?trigger", OAVBDIMetaModel.plantrigger_type);
		Variable	ref	= new Variable("?ref", OAVJavaType.java_string_type);
		Variable	mcapa	= new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable	rcapa	= new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable	rtel	= new Variable("?rtel", OAVBDIRuntimeModel.element_type);
		Variable	rtargetcapa	= new Variable("?rtargetcapa", OAVBDIRuntimeModel.capability_type);
		Variable	change	= new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
			
		ObjectCondition	mtricon = new ObjectCondition(OAVBDIMetaModel.plantrigger_type);
		mtricon.addConstraint(new BoundConstraint(null, trigger));
		mtricon.addConstraint(new BoundConstraint(OAVBDIMetaModel.trigger_has_factaddeds, Arrays.asList(new Variable[]
		{
			new Variable("$?x", OAVJavaType.java_string_type, true, false),
			ref,
			new Variable("$?y", OAVJavaType.java_string_type, true, false),
		}), IOperator.EQUAL));

		ObjectCondition	mplancon = new ObjectCondition(OAVBDIMetaModel.plan_type);
		mplancon.addConstraint(new BoundConstraint(null, mplan));
		mplancon.addConstraint(new BoundConstraint(OAVBDIMetaModel.plan_has_trigger, trigger));

		ObjectCondition	mcapacon = new ObjectCondition(OAVBDIMetaModel.capability_type);
		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_plans, mplan, IOperator.CONTAINS));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));

		ObjectCondition	rtelcon	= new ObjectCondition(OAVBDIRuntimeModel.beliefset_type);
		rtelcon.addConstraint(new BoundConstraint(null, rtel));

		ObjectCondition	targetcapcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		targetcapcon.addConstraint(new BoundConstraint(null, rtargetcapa));
		targetcapcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefsets, rtel, IOperator.CONTAINS));

		ObjectCondition	changecon	= new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, change));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtel));
		changecon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa, ref, rtel, rtargetcapa})));
		
		Rule plan_creation = new Rule("plantrigger_factadded",
			new AndCondition(new ICondition[]{mtricon, mplancon, mcapacon, capcon, rtelcon, targetcapcon, changecon}),
			PLAN_CHANGECREATION, IPriorityEvaluator.PRIORITY_1);
		return plan_creation;
	}
	
	/**
	 *  Trigger plan creation on fact removed event.
	 */
	public static Rule createPlanFactRemovedTriggerRule()
	{
		Variable	mplan	= new Variable("?mplan", OAVBDIMetaModel.plan_type);
		Variable	trigger	= new Variable("?trigger", OAVBDIMetaModel.plantrigger_type);
		Variable	ref	= new Variable("?ref", OAVJavaType.java_string_type);
		Variable	mcapa	= new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable	rcapa	= new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable	rtel	= new Variable("?rtel", OAVBDIRuntimeModel.element_type);
		Variable	rtargetcapa	= new Variable("?rtargetcapa", OAVBDIRuntimeModel.capability_type);
		Variable	change	= new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
			
		ObjectCondition	mtricon = new ObjectCondition(OAVBDIMetaModel.plantrigger_type);
		mtricon.addConstraint(new BoundConstraint(null, trigger));
		mtricon.addConstraint(new BoundConstraint(OAVBDIMetaModel.trigger_has_factremoveds, Arrays.asList(new Variable[]
		{
				new Variable("$?x", OAVJavaType.java_string_type, true, false),
				ref,
				new Variable("$?y", OAVJavaType.java_string_type, true, false),
		}), IOperator.EQUAL));

		ObjectCondition	mplancon = new ObjectCondition(OAVBDIMetaModel.plan_type);
		mplancon.addConstraint(new BoundConstraint(null, mplan));
		mplancon.addConstraint(new BoundConstraint(OAVBDIMetaModel.plan_has_trigger, trigger));

		ObjectCondition	mcapacon = new ObjectCondition(OAVBDIMetaModel.capability_type);
		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_plans, mplan, IOperator.CONTAINS));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));

		ObjectCondition	rtelcon	= new ObjectCondition(OAVBDIRuntimeModel.beliefset_type);
		rtelcon.addConstraint(new BoundConstraint(null, rtel));

		ObjectCondition	targetcapcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		targetcapcon.addConstraint(new BoundConstraint(null, rtargetcapa));
		targetcapcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefsets, rtel, IOperator.CONTAINS));

		ObjectCondition	changecon	= new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, change));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtel));
		changecon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa, ref, rtel, rtargetcapa})));
		
		Rule plan_creation = new Rule("plantrigger_factremoved",
			new AndCondition(new ICondition[]{mtricon, mplancon, mcapacon, capcon, rtelcon, targetcapcon, changecon}),
			PLAN_CHANGECREATION, IPriorityEvaluator.PRIORITY_1);
		return plan_creation;
	}
	
	/**
	 *  Trigger plan creation on goal finished event.
	 */
	public static Rule createPlanGoalFinishedTriggerRule()
	{
		Variable mplan = new Variable("?mplan", OAVBDIMetaModel.plan_type);
		Variable trigger = new Variable("?trigger", OAVBDIMetaModel.plantrigger_type);
		Variable refelem = new Variable("?refelem", OAVBDIMetaModel.triggerreference_type);
		Variable ref = new Variable("?ref", OAVJavaType.java_string_type);
		Variable mcapa = new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable rtel = new Variable("?rtel", OAVBDIRuntimeModel.element_type);
		Variable rtargetcapa	= new Variable("?rtargetcapa", OAVBDIRuntimeModel.capability_type);
		Variable change = new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
			
		ObjectCondition	mtricon = new ObjectCondition(OAVBDIMetaModel.plantrigger_type);
		mtricon.addConstraint(new BoundConstraint(null, trigger));
		mtricon.addConstraint(new BoundConstraint(OAVBDIMetaModel.trigger_has_goalfinisheds, Arrays.asList(new Variable[]
		{
			new Variable("$?x", OAVJavaType.java_string_type, true, false),
			refelem,
			new Variable("$?y", OAVJavaType.java_string_type, true, false),
		}), IOperator.EQUAL));

		ObjectCondition trcon = new ObjectCondition(OAVBDIMetaModel.triggerreference_type);
		trcon.addConstraint(new BoundConstraint(null, refelem));
		trcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.triggerreference_has_ref, ref));
		
		ObjectCondition	mplancon = new ObjectCondition(OAVBDIMetaModel.plan_type);
		mplancon.addConstraint(new BoundConstraint(null, mplan));
		mplancon.addConstraint(new BoundConstraint(OAVBDIMetaModel.plan_has_trigger, trigger));

		ObjectCondition	mcapacon = new ObjectCondition(OAVBDIMetaModel.capability_type);
		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_plans, mplan, IOperator.CONTAINS));
		
		ObjectCondition	capcon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));

		ObjectCondition	changecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, change));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rtel));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_scope, rtargetcapa));
		changecon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa, ref, rtel, rtargetcapa})));
		
		Rule plan_goalfini = new Rule("plantrigger_goalfinished",
			new AndCondition(new ICondition[]{mtricon, trcon, mplancon, mcapacon, capcon, changecon}),
			PLAN_CHANGECREATION, IPriorityEvaluator.PRIORITY_1);
		return plan_goalfini;
	}
	
	/**
	 *  Create the plan context invalid rule.
	 *  @param usercond	The ADF part of the target condition.
	 *  @param mplan	The plan model element.
	 */
	public static Object[]	createPlanContextInvalidUserRule(Object mplan)
	{
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, mplan));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));
		
		return new Object[]{new AndCondition(new ICondition[]{plancon, capcon}), PLAN_ABORT, null, null, Boolean.TRUE};
	}	
	
	/**
	 *  Create a new plan.
	 */
	protected static final IAction PLAN_CREATION = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
//			System.out.println("Plan creation triggered.");
			Object rcapa = assignments.getVariableValue("?rcapa");
			Object mplan = assignments.getVariableValue("?mplan");
			
			// Create fetcher with binding values.
			OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rcapa);
			String[] varnames = assignments.getVariableNames();
			for(int i=0; i<varnames.length; i++)
			{
				fetcher.setValue(varnames[i], assignments.getVariableValue(varnames[i]));
			}
			
			// Create plans according to binding possibilities.
			List bindings = AgentRules.calculateBindingElements(state, mplan, null, fetcher);
			if(bindings!=null)
			{
				for(int i=0; i<bindings.size(); i++)
				{
					Object rplan = PlanRules.instantiatePlan(state, rcapa, mplan, null, null, null, (Map)bindings.get(i), fetcher);
					PlanRules.adoptPlan(state, rcapa, rplan);	
				}
			}
			else
			{
				Object rplan = PlanRules.instantiatePlan(state, rcapa, mplan, null, null, null, null, fetcher);
				PlanRules.adoptPlan(state, rcapa, rplan);	
			}
		}
	};
	
	/**
	 *  Create a plan in reaction to a change event.
	 */
	protected static final IAction PLAN_CHANGECREATION = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
//			System.out.println("Plan creation triggered.");
			Object rcapa = assignments.getVariableValue("?rcapa");
			Object mplan = assignments.getVariableValue("?mplan");
			Object change = assignments.getVariableValue("?change");
			
//			String cetype = (String)state.getAttributeValue(change, OAVBDIRuntimeModel.changeevent_has_type);
//			if(OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(cetype))
//			{
//				change = state.getAttributeValue(change, OAVBDIRuntimeModel.changeevent_has_element);
//			}
			
			// Create fetcher with binding values.
			OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rcapa);
			String[] varnames = assignments.getVariableNames();
			for(int i=0; i<varnames.length; i++)
			{
				fetcher.setValue(varnames[i], assignments.getVariableValue(varnames[i]));
			}
			
			// Create plans according to binding possibilities.
			List bindings = AgentRules.calculateBindingElements(state, mplan, null, fetcher);
			if(bindings!=null)
			{
				for(int i=0; i<bindings.size(); i++)
				{
					Object rplan = PlanRules.instantiatePlan(state, rcapa, mplan, null, change, null, (Map)bindings.get(i), fetcher);
					PlanRules.adoptPlan(state, rcapa, rplan);	
				}
			}
			else
			{
				Object rplan = PlanRules.instantiatePlan(state, rcapa, mplan, null, change, null, null, fetcher);
				PlanRules.adoptPlan(state, rcapa, rplan);	
			}
		}
	};
	
	/**
	 *  Reschedule a plan after change event.
	 */
	protected static final IAction PLAN_CHANGEWAIT = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rplan	= assignments.getVariableValue("?rplan");
			Object	rcapa	= assignments.getVariableValue("?rcapa");
			Object	change	= assignments.getVariableValue("?change");
			
//			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement, change);
//			cleanupPlanWait(state, rcapa, rplan, false);
//			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, 
//				OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
			
			EventProcessingRules.schedulePlanInstanceCandidate(state, change, rplan, rcapa);
			
//			System.out.println("PLAN_CHANGEWAIT: Setting plan to ready: "
//					+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//					+", "+rplan);
		}
	};
	
	/**
	 *  Add a collected event to the waitqueue
	 */
	protected static final IAction PLAN_CHANGEWAITQUEUE = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rplan	= assignments.getVariableValue("?rplan");
			Object	change	= assignments.getVariableValue("?change");
			
			EventProcessingRules.scheduleWaitqueueCandidate(state, change, rplan);
		}
	};
	
	/**
	 *  Set an plan context to invalid.
	 */
	protected static final IAction PLAN_ABORT	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rplan	= assignments.getVariableValue("?rplan");
			Object	rcapa	= assignments.getVariableValue("?rcapa");
//			System.out.println("Aborting: "+rplan);
			abortPlan(state, rcapa, rplan);
		}
	};

	
	/**
	 *  Reschedule a plan after external condition becomes true
	 */
	protected static final IAction PLAN_EXTERNALCONDITIONWAIT = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rplan	= assignments.getVariableValue("?rplan");
			Object	rcapa	= assignments.getVariableValue("?rcapa");
			
			cleanupPlanWait(state, rcapa, rplan, false);
			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, 
				OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);

//			System.out.println("PLAN_EXTERNALCONDITIONWAIT: Setting plan to ready: "
//					+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//					+", "+rplan);
			
//			System.out.println("Plan reactivated from external condition: "+rplan);
		}
	};
	
	//-------- helper methods for waiting --------

	/**
	 *  Wait for a wait abstraction.
	 *  @return The dispatched element.
	 */
	public static Object waitForWaitAbstraction(Object wa, long timeout, IOAVState state, Object rcapa, Object rplan)
	{
		Object[] ret = initializeWait(wa, timeout, state, rcapa, rplan);
		
		if(ret[0]==null)
		{
			doWait(state, rplan);
			ret[0] = afterWait(wa, (boolean[])ret[1], state, rcapa, rplan);
		}
		return ret[0];
	}
	
	/**
	 *  Initialize the wait by
	 *  a) check if one of goals is already finished.
	 *  b) set the waitabstraction for the plan.
	 *  c) set the timer.
	 */
	public static Object[] initializeWait(final Object wa, long timeout, final IOAVState state, final Object rcapa, final Object rplan)
	{	
		Object ret = null;

		assert state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitabstraction)==null;
		
		// Clear dispatched element.
		state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement, null);

		
		if(wa!=null)
		{
			Collection rgoals = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goals);
			if(rgoals!=null)
			{
				for(Iterator it=rgoals.iterator(); it.hasNext(); )
				{
					Object rgoal = it.next();
					if(OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED.equals(state.getAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate)))
					{
						// Remove waitabstraction from state, as it isn't used.
						state.dropObject(wa);

						if(OAVBDIRuntimeModel.GOALPROCESSINGSTATE_FAILED.equals(
								state.getAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_processingstate)))
						{
							throw new GoalFailureException("Goal failed: "+rgoal);
						}
						// Todo: Hack!!! wrong scope of goal
						ret = GoalFlyweight.getGoalFlyweight(state, rcapa, rgoal);
						state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement, rgoal);
					}
				}
			}
		}
				
		final boolean[] to = new boolean[1];

		if(ret==null)
		{
			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitabstraction, wa);

			if(timeout>-1)
			{
				// todo: what happens when timer is immediately due?! can this lead to problems?
				// timer runs on other thread.
				TimeoutAction toa = new TimeoutAction(state, rplan, rcapa, to);
				
//				System.out.println("Create timer: "+timeout+", "+BDIAgentFeature.getInterpreter(state).getName()+", "+System.currentTimeMillis());
				IClockService cs = SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				ITimer timer = cs.createTimer(timeout, new InterpreterTimedObject(BDIAgentFeature.getInternalAccess(state), toa));
				toa.setTimer(timer); // This works because isValid() will always be executed on agent thread (InterpreterTimedObject).
				
//				System.out.println("Timer created: "+BDIAgentFeature.getInterpreter(state).getName()+", "+System.currentTimeMillis());
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_timer, timer);
			}
			else if(timeout==TICK_TIMER)
			{
				TimeoutAction toa = new TimeoutAction(state, rplan, rcapa, to);
				
//				System.out.println("Create tick timer: "+BDIAgentFeature.getInterpreter(state).getName());
				IClockService cs = SServiceProvider.getLocalService(BDIAgentFeature.getInternalAccess(state), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				ITimer timer = cs.createTickTimer(new InterpreterTimedObject(BDIAgentFeature.getInternalAccess(state), toa));
				toa.setTimer(timer); // This works because isValid() will always be executed on agent thread (InterpreterTimedObject).

//				System.out.println("Tick timer created: "+BDIAgentFeature.getInterpreter(state).getName());
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_timer, timer);
			}
		}
		
		return new Object[]{ret, to};
	}
		
	/**
	 *  Do the waiting, i.e. set the external caller thread to sleep mode.
	 */
	public static void doWait(IOAVState state, Object rplan)
	{
		IPlanExecutor exe = BDIAgentFeature.getInterpreter(state).getPlanExecutor(rplan);
		exe.eventWaitFor(BDIAgentFeature.getInternalAccess(state), rplan);
	}
	
	/**
	 *  Perform the cleanup operations after a wait.
	 *  Mainly removes the wait abstraction and generates the result.
	 */
	public static Object afterWait(Object wa, boolean[] to, IOAVState state, Object rcapa, Object rplan)
	{
		Object ret = null;
		
		if(to[0])
		{
			if(wa!=null)
				throw new TimeoutException();
		}

		Object de = state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement);
		if(de!=null)
		{
			OAVObjectType type = state.getType(de);
			if(OAVBDIRuntimeModel.goal_type.equals(type))
			{
				// When goal is not succeeded (or idle for maintaingoals) throw exception.
				if(!OAVBDIRuntimeModel.GOALPROCESSINGSTATE_SUCCEEDED.equals(
					state.getAttributeValue(de, OAVBDIRuntimeModel.goal_has_processingstate)))
				{
					Object	mgoal	= state.getAttributeValue(de, OAVBDIRuntimeModel.element_has_model);
					if(!state.getType(mgoal).isSubtype(OAVBDIMetaModel.maintaingoal_type)
						|| !OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE.equals(
							state.getAttributeValue(de, OAVBDIRuntimeModel.goal_has_processingstate)))
					{
						throw new GoalFailureException("Goal failed: "+de);
					}
				}
				// Todo: Hack!!! wrong scope
				ret = GoalFlyweight.getGoalFlyweight(state, rcapa, de);
			}
			else if(OAVBDIRuntimeModel.internalevent_type.equals(type))
			{
				// Todo: Hack!!! wrong scope
				ret = InternalEventFlyweight.getInternalEventFlyweight(state, rcapa, de);
			}
			else if(OAVBDIRuntimeModel.messageevent_type.equals(type))
			{
				// Todo: Hack!!! wrong scope
				ret = MessageEventFlyweight.getMessageEventFlyweight(state, rcapa, de);
			}
			else if(OAVBDIRuntimeModel.changeevent_type.equals(type))
			{
				// Todo: Hack!!! wrong scope
				ret = new ChangeEventFlyweight(state, rcapa, de);
			}
			else if(OAVBDIMetaModel.condition_type.equals(type))
			{
				// Todo: change event for triggered condition. 
				ret = state.getAttributeValue(de, OAVBDIMetaModel.modelelement_has_name);
			}
			else
			{
				throw new RuntimeException("Unsupported return element: "+de+" "+type);
			}
		}
		
		return ret;
	}

	
	/**
	 *  Cleanup plan wait abstraction, waitqueue and wait timer.
	 *  @param rplan	The plan to clean up.
	 *  @param cleanwq	Flag indicating if waitqueue should be cleaned.
	 */
	public static void	cleanupPlanWait(IOAVState state, Object rcapa, Object rplan, boolean cleanwq)
	{
		Object	wa	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitabstraction);
		if(wa!=null)
		{
			cleanupWaitAbstraction(state, rcapa, wa);
			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitabstraction, null);
		}
		ITimer	timer	= (ITimer)state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_timer);
		if(timer!=null)
		{
			try
			{
				timer.cancel();
			}
			catch(RuntimeException e)
			{
				// ThreadPool could have been already shut down	
			}
			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_timer, null);
		}

		if(cleanwq)
		{
			Object	wqwa	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa);
			if(wqwa!=null)
			{
				cleanupWaitAbstraction(state, rcapa, wqwa);
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa, null);
			}
			Collection	coll	= state.getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements);
			if(coll!=null)
			{
				Object[]	wqes	= coll.toArray();
				for(int i=0; i<wqes.length; i++)
				{
					state.removeAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, wqes[i]);
				}
			}
		}
	}

	/**
	 *  Cleanup a wait abstraction, i.e. remove sent message events from capability
	 *  and observed elements from event reificator.
	 */
	protected static void cleanupWaitAbstraction(IOAVState state, Object rcapa, Object wa)
	{
		Collection	coll	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				MessageEventRules.deregisterMessageEvent(state, it.next(), rcapa);
			}
		}
		coll	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(it.next());
			}
		}
		coll	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(it.next());
			}
		}
		coll	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(it.next());
			}
		}
		coll	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(it.next());
			}
		}
		coll	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goals);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(it.next());
			}
		}
	}
}

/**
 *  Action to be executed on timeout wait.
 */
class TimeoutAction extends CheckedAction
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	/** The plan. */
	protected Object rplan;
	
	/** The capability. */
	protected Object rcapa;
	
	/** The timer. */
	protected ITimer timer;
	
	/** The result timeout occurred. */
	protected boolean[] to;
	
	//-------- constructors --------
	
	/**
	 *  Create a new timeout action.
	 *  @param state The state.
	 *  @param rplan The rplan
	 *  @param to The timeout result array.
	 */
	public TimeoutAction(IOAVState state, Object rplan, Object rcapa, boolean[] to)
	{
		this.state = state;
		this.rplan = rplan;
		this.rcapa = rcapa;
		this.to = to;
	}
	
	//-------- methods --------
	
	/**
	 *  Set the timer.
	 *  @param timer The timer to set.
	 */
	public void setTimer(ITimer timer)
	{
		this.timer = timer;
	}

	/**
	 *  Test if the action is valid.
	 */
	public boolean isValid()
	{
		return state.containsObject(rplan)
			&& timer.equals(state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_timer))
			&& super.isValid();
	}
	
	/**
	 *  Execute the action.
	 */
	public void run()
	{
//		System.out.println("Timer occurred: ");
		to[0] = true;
		EventProcessingRules.schedulePlanInstanceCandidate(state, null, rplan, rcapa);
		((IInternalExecutionFeature)BDIAgentFeature.getInternalAccess(state).getComponentFeature(IExecutionFeature.class)).wakeup();
	}
}
