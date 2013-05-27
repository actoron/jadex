package jadex.bdi.runtime.interpreter;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bridge.CheckedAction;
import jadex.bridge.service.types.clock.ITimer;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.AndConstraint;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.IPriorityEvaluator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.OrConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;

import java.util.Collection;

/**
 *  Static helper class for goal rules and actions.
 *  These goals handle change of the goal processing state.
 *  
 *  todo: unify createXYZProcessingRule() methods
 */
public class GoalProcessingRules
{
	//-------- helper methods --------
	
	/**
	 *  Change the goal processing state.
	 */
	public static void	changeProcessingState(IOAVState state, Object rgoal, Object newstate)
	{
		// If was inprocess -> now stop processing.
//		Object	curstate	= state.getAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_processingstate);
//		System.out.println("changeprocstate: "+rgoal+" "+newstate+" "+curstate);

		if(!OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS.equals(newstate))
		{
			// todo: introduce some state for finished?!
			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, null);
			
			// Remove finished plans that would otherwise interfere with next goal processing (if any).
			Collection<?>	fplans	= state.getAttributeValues(rgoal, OAVBDIRuntimeModel.goal_has_finishedplans);
			if(fplans!=null && !fplans.isEmpty())
			{
				Object[]	afplans	= fplans.toArray();
				for(int i=0; i<afplans.length; i++)
					state.removeAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_finishedplans, afplans[i]);
			}
			
			// Reset event processing.
//			BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
//			if(rgoal.equals(state.getAttributeValue(ip.getAgent(), OAVBDIRuntimeModel.agent_has_eventprocessing)))
//				state.setAttributeValue(ip.getAgent(), OAVBDIRuntimeModel.agent_has_eventprocessing, null);
			
			// Reset APL.
			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_apl, null);
			
			// Clean tried plans if necessary.
			Collection<?> coll = state.getAttributeValues(rgoal, OAVBDIRuntimeModel.goal_has_triedmplans);
			if(coll!=null)
			{
				Object[]	acoll	= coll.toArray();
				for(int i=0; i<acoll.length; i++)
				{
					state.removeAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_triedmplans, acoll[i]);
				}
			}
			
			// Remove timers.
			ITimer retrytimer = (ITimer)state.getAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_retrytimer);
			if(retrytimer!=null)
			{
				retrytimer.cancel();
				((InterpreterTimedObject)retrytimer.getTimedObject()).getAction().setValid(false);
			}
			
			if(!OAVBDIRuntimeModel.GOALPROCESSINGSTATE_PAUSED.equals(newstate))
			{
				ITimer recurtimer = (ITimer)state.getAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_recurtimer);
				if(recurtimer!=null)
				{
					recurtimer.cancel();
					((InterpreterTimedObject)recurtimer.getTimedObject()).getAction().setValid(false);
				}
			}
		}
		
		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_processingstate, newstate);
		
		// If now is inprocess -> start processing
		if(OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS.equals(newstate))
			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
	
//		System.out.println("exit: "+rgoal+" "+state.getAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state));
	}
	
	//-------- performgoal rules --------
	
	/**
	 *  Create performgoal processing rule.
	 */
	protected static Rule createPerformgoalProcessingRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.performgoal_type, IOperator.INSTANCEOF));
		
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));

		Rule performgoal_processing	= new Rule("performgoal_processing", new AndCondition(new ICondition[]{goalcon, capacon}), GOAL_PROCESSING);
		return performgoal_processing;
	}

	/**
	 *  Create the performgoal finished rule.
	 *  A perform goal is finished when not rebuild and last plan finished.
	 */
	protected static Rule createPerformgoalFinishedRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, rplan, IOperator.CONTAINS));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.performgoal_type, IOperator.INSTANCEOF));
		goalcon.addConstraint(new LiteralConstraint(new OAVAttributeType[]{
			OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_rebuild}, Boolean.FALSE));

		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
		plancon.addConstraint(new BoundConstraint(null, rplan));
		
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		Rule performgoal_finished = new Rule("performgoal_finished", new AndCondition(new ICondition[]{plancon, goalcon, capacon}), 
			GOAL_SUCCEEDED_PLAN_REMOVE, IPriorityEvaluator.PRIORITY_1);
		// Must have higher priority than recur!
		
		return performgoal_finished;
	}

	/**
	 *  Create the performgoal retry rule.
	 *  Retry a goal when retry=true, posttoall=false, (rebuild=true || apl!=null), plan finished
	 * /
	protected static Rule createPerformgoalRetryRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.performgoal_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable recalc	= new Variable("?recalc", OAVJavaType.java_boolean_type);

		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.performgoal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_rebuild, recalc));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.goal_has_retry, Boolean.TRUE));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.processableelement_has_posttoall, Boolean.FALSE));

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, IGoal.LIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_plans, rplan, IOperator.CONTAINS));
		IConstraint conaplnotnull = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, IOperator.NOTEQUAL);
		IConstraint conrecalc = new LiteralConstraint(recalc, Boolean.TRUE);
		OrConstraint orcon = new OrConstraint(new IConstraint[]{conaplnotnull, conrecalc});
		goalcon.addConstraint(orcon);
	
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
		
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));
				
		Rule performgoal_retry	= new Rule("performgoal_retry", new AndCondition(new ICondition[]{mgoalcon, goalcon, plancon, capacon}), GOAL_RETRY);
		return performgoal_retry;
	}*/

	//-------- achievegoal rules --------
	
	/**
	 *  Create achievegoal processing rule.
	 */
	protected static Rule createAchievegoalProcessingRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.achievegoal_type, IOperator.INSTANCEOF));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));

		Rule achievegoal_processing	= new Rule("achievegoal_processing", new AndCondition(new ICondition[]{goalcon, capcon}), GOAL_PROCESSING);
		return achievegoal_processing;
	}

	/**
	 *  Create the achievegoal succeeded rule (for goals without target condition).
	 *  The goal succeeds, when one plan succeeds.
	 */
	protected static Rule createAchievegoalSucceededRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, rplan, IOperator.CONTAINS));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.achievegoal_type, IOperator.INSTANCEOF));
		goalcon.addConstraint(new LiteralConstraint(new OAVAttributeType[]{
			OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.achievegoal_has_targetcondition}, null));

		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		
		Rule achievegoal_succeeded = new Rule("achievegoal_succeeded", new AndCondition(new ICondition[]{plancon, goalcon, capcon}), 
			GOAL_SUCCEEDED_PLAN_REMOVE, IPriorityEvaluator.PRIORITY_1);
		return achievegoal_succeeded;
	}

	/**
	 *  Create the achievegoal succeeded rule (for goals with target condition).
	 *  @param usercond	The ADF part of the target condition.
	 *  @param model The goal model element.
	 */
	public static Object[]	createAchievegoalSucceededUserRule(Object model)
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, model));
		
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(null, rcapa));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		
		return new Object[]{
			new AndCondition(new ICondition[]{goalcon, capacon}),
			GOAL_SUCCEEDED,
			IPriorityEvaluator.PRIORITY_1};
	}
	
	/**
	 *  Create the achievegoal failed rule.
	 *  The goal fails, when rebuild is false and
	 *  a) has no targetcondition and last plan failed
	 *  b) last plan finished (regardless of success state).
	 *  In case of rebuild the failure occurs if buildAPL does
	 *  not produce any further candidates.
	 *  Goal failed when (rebuild=false || apl!=null), plan finished,
	 *  (planstate=failed || targetcondition!=null)
	 * /
	protected static Rule createAchievegoalFailedRule()
	{
		Variable mgoal	= new Variable("?mgoal", OAVBDIMetaModel.achievegoal_type);
		Variable rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable fplans	= new Variable("$?fplans", OAVBDIRuntimeModel.plan_type, true);
		Variable target = new Variable("?target", OAVBDIMetaModel.condition_type);
		
		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.achievegoal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
//		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.achievegoal_has_targetcondition, null));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.achievegoal_has_targetcondition, target));
		IConstraint reb = new LiteralConstraint(OAVBDIMetaModel.goal_has_rebuild, Boolean.FALSE);
		IConstraint retry = new LiteralConstraint(OAVBDIMetaModel.goal_has_retry, Boolean.FALSE);
		mgoalcon.addConstraint(new OrConstraint(new IConstraint[]{reb, retry}));
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, IOperator.EQUAL));
		
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, fplans));
				
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new BoundConstraint(null, fplans, IOperator.CONTAINS));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
//		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED));
		IConstraint goalhastarget = new LiteralConstraint(target, null, IOperator.NOTEQUAL);
		IConstraint planfail = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED);
		plancon.addConstraint(new OrConstraint(new IConstraint[]{goalhastarget, planfail}));

		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));
		
		Rule	achievegoal_failed	= new Rule("achievegoal_failed", new AndCondition(new ICondition[]{mgoalcon, goalcon, plancon, capcon}), GOAL_FAILED_PLAN_REMOVE);
		return achievegoal_failed;
	}*/
	
	/**
	 *  Create the achievegoal failed rule.
	 *  
	 *  recur=true -> never failed
	 *  hastarget -> no more plans regardless of state
	 *  !hastarget -> no more plans and last one failed
	 *  no more plan = (!retry || (rebuild=false && apl==null)) (in case rebuild=true buildAPL will be called)
	 * /
	protected static Rule createAchievegoalFailedRule()
	{
		Variable mgoal	= new Variable("?mgoal", OAVBDIMetaModel.achievegoal_type);
		Variable rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable fplans	= new Variable("$?fplans", OAVBDIRuntimeModel.plan_type, true);
		Variable target = new Variable("?target", OAVBDIMetaModel.condition_type);
		Variable retry = new Variable("?retry", OAVJavaType.java_boolean_type);
		Variable rebuild = new Variable("?rebuild", OAVJavaType.java_boolean_type);
		
		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.achievegoal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.goal_has_recur, Boolean.FALSE));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.achievegoal_has_targetcondition, target));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_rebuild, rebuild));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_retry, retry));
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, fplans));
		IConstraint rebapl = new AndConstraint(new IConstraint[]{new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, 
			IOperator.EQUAL), new LiteralConstraint(rebuild, Boolean.FALSE)});
		IConstraint nomoreplan = new OrConstraint(new IConstraint[]{new LiteralConstraint(retry, Boolean.FALSE), rebapl});
		goalcon.addConstraint(nomoreplan);
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new BoundConstraint(null, fplans, IOperator.CONTAINS));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
//		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED));
		IConstraint goalhastarget = new LiteralConstraint(target, null, IOperator.NOTEQUAL);
		IConstraint planfail = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED);
		IConstraint planabort = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED);
		plancon.addConstraint(new OrConstraint(new IConstraint[]{goalhastarget, planfail, planabort}));

		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));
		
		Rule achievegoal_failed	= new Rule("achievegoal_failed", new AndCondition(new ICondition[]{mgoalcon, goalcon, plancon, capcon}), GOAL_FAILED_PLAN_REMOVE);
		return achievegoal_failed;
	}*/
	
	/**
	 *  Create the achievegoal failed rule.
	 *  
	 *  recur=true -> never failed
	 *  hastarget -> no more plans regardless of state
	 *  !hastarget -> no more plans and last one failed
	 *  no more plan = (!retry || (rebuild=false && apl==null)) (in case rebuild=true buildAPL will be called)
	 */
	protected static Rule createAchievegoalFailedRule()
	{
		Variable rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable fplans	= new Variable("$?fplans", OAVBDIRuntimeModel.plan_type, true, false);
		Variable target = new Variable("?target", OAVBDIMetaModel.condition_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, fplans));
		IConstraint rebapl = new AndConstraint(new IConstraint[]{new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, 
			IOperator.EQUAL), new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_rebuild}, Boolean.FALSE)});
		IConstraint nomoreplan = new OrConstraint(new IConstraint[]{new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_retry}, Boolean.FALSE), rebapl});
		goalcon.addConstraint(nomoreplan);
		goalcon.addConstraint(new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_recur}, Boolean.FALSE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.achievegoal_type, IOperator.INSTANCEOF));
		goalcon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.achievegoal_has_targetcondition}, target));
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new BoundConstraint(null, fplans, IOperator.CONTAINS));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
		IConstraint goalhastarget = new LiteralConstraint(target, null, IOperator.NOTEQUAL);
		IConstraint planfail = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED);
		IConstraint planabort = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED);
		plancon.addConstraint(new OrConstraint(new IConstraint[]{goalhastarget, planfail, planabort}));

		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));
		
		Rule achievegoal_failed	= new Rule("achievegoal_failed", new AndCondition(new ICondition[]{goalcon, plancon, capcon}), GOAL_FAILED_PLAN_REMOVE);
		return achievegoal_failed;
	}
	
	/**
	 *  Create the achievegoal retry rule.
	 *  Retry a goal when retry=true, posttoall=false, (rebuild=true || apl!=null), plan finished,
	 *  (planstate=failed || targetcondition!=null)
	 * /
	protected static Rule createAchievegoalRetryRule()
	{
		Variable mgoal	= new Variable("?mgoal", OAVBDIMetaModel.achievegoal_type);
		Variable rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable fplans	= new Variable("$?fplans", OAVBDIRuntimeModel.plan_type, true);
		Variable recalc	= new Variable("?recalc", OAVJavaType.java_boolean_type);
		Variable target = new Variable("?target", OAVBDIMetaModel.condition_type);
		
		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.achievegoal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.achievegoal_has_targetcondition, target));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_rebuild, recalc));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.goal_has_retry, Boolean.TRUE));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.processableelement_has_posttoall, Boolean.FALSE));

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		IConstraint conaplnotnull = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, IOperator.NOTEQUAL);
		IConstraint conrecalc = new LiteralConstraint(recalc, Boolean.TRUE);
		OrConstraint orcon = new OrConstraint(new IConstraint[]{conaplnotnull, conrecalc});
		goalcon.addConstraint(orcon);
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, rplan, IOperator.CONTAINS));

		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new BoundConstraint(null, fplans, IOperator.CONTAINS));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
		IConstraint goalhastarget = new LiteralConstraint(target, null, IOperator.NOTEQUAL);
		IConstraint planfail = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED);
		IConstraint planabort = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED);
		IConstraint orcon2 = new OrConstraint(new IConstraint[]{goalhastarget, planfail, planabort});
		plancon.addConstraint(orcon2);
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));
	
		Rule achievegoal_retry	= new Rule("achievegoal_retry", new AndCondition(new ICondition[]{mgoalcon, plancon, goalcon, capcon}), GOAL_RETRY);
		return achievegoal_retry;
	}
	
	/**
	 *  Create the achievegoal retry rule.
	 *  Retry a goal when retry=true, posttoall=false, (rebuild=true || apl!=null), plan finished,
	 *  (planstate=failed || targetcondition!=null)
	 */
	protected static Rule createAchievegoalRetryRule()
	{
		Variable rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable fplans	= new Variable("$?fplans", OAVBDIRuntimeModel.plan_type, true, false);
		Variable target = new Variable("?target", OAVBDIMetaModel.condition_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		IConstraint conaplnotnull = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, IOperator.NOTEQUAL);
		IConstraint conrecalc = new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_rebuild}, Boolean.TRUE);
		OrConstraint orcon = new OrConstraint(new IConstraint[]{conaplnotnull, conrecalc});
		goalcon.addConstraint(orcon);
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, fplans));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.achievegoal_type, IOperator.INSTANCEOF));
		goalcon.addConstraint(new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_retry}, Boolean.TRUE));
		goalcon.addConstraint(new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.processableelement_has_posttoall}, Boolean.FALSE));
		goalcon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.achievegoal_has_targetcondition}, target));

		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new BoundConstraint(null, fplans, IOperator.CONTAINS));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
		IConstraint goalhastarget = new LiteralConstraint(target, null, IOperator.NOTEQUAL);
		IConstraint planfail = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED);
		IConstraint planabort = new LiteralConstraint(OAVBDIRuntimeModel.plan_has_lifecyclestate, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED);
		IConstraint orcon2 = new OrConstraint(new IConstraint[]{goalhastarget, planfail, planabort});
		plancon.addConstraint(orcon2);
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));
	
		Rule achievegoal_retry	= new Rule("achievegoal_retry", new AndCondition(new ICondition[]{goalcon, plancon, capcon}), GOAL_RETRY);
		return achievegoal_retry;
	}
	
	//-------- querygoal rules --------
	
	/**
	 *  Create querygoal processing rule.
	 */
	protected static Rule createQuerygoalProcessingRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.querygoal_type, IOperator.INSTANCEOF));

		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));

		Rule querygoal_processing = new Rule("querygoal_processing", new AndCondition(new ICondition[]{goalcon, capcon}), GOAL_PROCESSING);
		return querygoal_processing;
	}

	/**
	 *  Create the querygoal succeeded rule.
	 */
	protected static Rule createQuerygoalSucceededRule()
	{
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.querygoal_type);
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable mparams = new Variable("$?mparams", OAVBDIMetaModel.parameter_type, true, false);
		Variable mparamsets = new Variable("$?mparamsets", OAVBDIMetaModel.parameterset_type, true, false);
		Variable rparams = new Variable("$?rparams", OAVBDIRuntimeModel.parameter_type, true, false);
		Variable rparamsets = new Variable("$?rparamsets", OAVBDIRuntimeModel.parameterset_type, true, false);
		Variable paramname = new Variable("?paramname", OAVJavaType.java_string_type);
		Variable paramsetname = new Variable("?paramsetname", OAVJavaType.java_string_type);
		
		// A query goal is succeeded when all (in)out parameters have been supplied with a value
		// and all (in)out parametersets have at least one value

		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.querygoal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.parameterelement_has_parameters, mparams));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.parameterelement_has_parametersets, mparamsets));
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameterelement_has_parameters, rparams));		
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameterelement_has_parametersets, rparamsets));		

		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));

		// No empty parameter
		ObjectCondition paramcon = new ObjectCondition(OAVBDIRuntimeModel.parameter_type);
		paramcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameter_has_name, paramname));
		paramcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.parameter_has_value, null));
		paramcon.addConstraint(new BoundConstraint(null, rparams, IOperator.CONTAINS));
		
		ObjectCondition mparamcon = new ObjectCondition(OAVBDIMetaModel.parameter_type);
		mparamcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.parameter_has_optional, Boolean.TRUE, IOperator.NOTEQUAL));
		mparamcon.addConstraint(new OrConstraint(new IConstraint[]{
			new LiteralConstraint(OAVBDIMetaModel.parameter_has_direction, OAVBDIMetaModel.PARAMETER_DIRECTION_INOUT),
			new LiteralConstraint(OAVBDIMetaModel.parameter_has_direction, OAVBDIMetaModel.PARAMETER_DIRECTION_OUT)
			}));
		mparamcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.modelelement_has_name, paramname));
		mparamcon.addConstraint(new BoundConstraint(null, mparams, IOperator.CONTAINS));
		
		AndCondition emptyparam = new AndCondition(new ICondition[]{paramcon, mparamcon});
		NotCondition noemptyparam = new NotCondition(emptyparam);
		
		// No empty parameter set
		ObjectCondition paramsetcon = new ObjectCondition(OAVBDIRuntimeModel.parameterset_type);
		paramsetcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameterset_has_name, paramsetname));
		// todo: length==0?
		paramsetcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.parameterset_has_values, null));
		paramsetcon.addConstraint(new BoundConstraint(null, rparamsets, IOperator.CONTAINS));
		
		ObjectCondition mparamsetcon = new ObjectCondition(OAVBDIMetaModel.parameterset_type);
		mparamsetcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.parameterset_has_optional, Boolean.TRUE, IOperator.NOTEQUAL));
		mparamsetcon.addConstraint(new OrConstraint(new IConstraint[]{
			new LiteralConstraint(OAVBDIMetaModel.parameterset_has_direction, OAVBDIMetaModel.PARAMETER_DIRECTION_INOUT),
			new LiteralConstraint(OAVBDIMetaModel.parameterset_has_direction, OAVBDIMetaModel.PARAMETER_DIRECTION_OUT)}));
		mparamsetcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.modelelement_has_name, paramsetname));
		mparamsetcon.addConstraint(new BoundConstraint(null, mparamsets, IOperator.CONTAINS));
		
		AndCondition emptyparamset = new AndCondition(new ICondition[]{paramsetcon, mparamsetcon});
		NotCondition noemptyparamset = new NotCondition(emptyparamset);
		
		Rule querygoal_succeeded = new Rule("querygoal_succeeded", new AndCondition(
			new ICondition[]{mgoalcon, goalcon, capcon, noemptyparam, noemptyparamset}), GOAL_SUCCEEDED, IPriorityEvaluator.PRIORITY_1);
		return querygoal_succeeded;
	}

	/**
	 *  Create the querygoal succeeded rule.
	 * /
	protected static Rule createQuerygoalSucceededRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable mparams = new Variable("$?mparams", OAVBDIMetaModel.parameter_type, true);
		Variable mparamsets = new Variable("$?mparamsets", OAVBDIMetaModel.parameterset_type, true);
		Variable rparams = new Variable("$?rparams", OAVBDIRuntimeModel.parameter_type, true);
		Variable rparamsets = new Variable("$?rparamsets", OAVBDIRuntimeModel.parameterset_type, true);
		Variable paramname = new Variable("?paramname", OAVJavaType.java_string_type);
		Variable paramsetname = new Variable("?paramsetname", OAVJavaType.java_string_type);
		
		// A query goal is succeeded when all (in)out parameters have been supplied with a value
		// and all (in)out parametersets have at least one value

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameterelement_has_parameters, rparams));		
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameterelement_has_parametersets, rparamsets));		
		goalcon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.parameterelement_has_parameters}, mparams));
		goalcon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.parameterelement_has_parametersets}, mparamsets));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.querygoal_type, IOperator.INSTANCEOF));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));

		// No empty parameter
		ObjectCondition paramcon = new ObjectCondition(OAVBDIRuntimeModel.parameter_type);
		paramcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameter_has_name, paramname));
		paramcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.parameter_has_value, null));
		paramcon.addConstraint(new BoundConstraint(null, rparams, IOperator.CONTAINS));
		
		ObjectCondition mparamcon = new ObjectCondition(OAVBDIMetaModel.parameter_type);
		mparamcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.parameter_has_optional, Boolean.TRUE, IOperator.NOTEQUAL));
		mparamcon.addConstraint(new OrConstraint(new IConstraint[]{
			new LiteralConstraint(OAVBDIMetaModel.parameter_has_direction, OAVBDIMetaModel.PARAMETER_DIRECTION_INOUT),
			new LiteralConstraint(OAVBDIMetaModel.parameter_has_direction, OAVBDIMetaModel.PARAMETER_DIRECTION_OUT)
			}));
		mparamcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.modelelement_has_name, paramname));
		mparamcon.addConstraint(new BoundConstraint(null, mparams, IOperator.CONTAINS));
		
		AndCondition emptyparam = new AndCondition(new ICondition[]{paramcon, mparamcon});
		NotCondition noemptyparam = new NotCondition(emptyparam);
		
		// No empty parameter set
		ObjectCondition paramsetcon = new ObjectCondition(OAVBDIRuntimeModel.parameterset_type);
		paramsetcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameterset_has_name, paramsetname));
		// todo: length==0?
		paramsetcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.parameterset_has_values, null));
		paramsetcon.addConstraint(new BoundConstraint(null, rparamsets, IOperator.CONTAINS));
		
		ObjectCondition mparamsetcon = new ObjectCondition(OAVBDIMetaModel.parameterset_type);
		mparamsetcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.parameterset_has_optional, Boolean.TRUE, IOperator.NOTEQUAL));
		mparamsetcon.addConstraint(new OrConstraint(new IConstraint[]{
			new LiteralConstraint(OAVBDIMetaModel.parameterset_has_direction, OAVBDIMetaModel.PARAMETER_DIRECTION_INOUT),
			new LiteralConstraint(OAVBDIMetaModel.parameterset_has_direction, OAVBDIMetaModel.PARAMETER_DIRECTION_OUT)}));
		mparamsetcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.modelelement_has_name, paramsetname));
		mparamsetcon.addConstraint(new BoundConstraint(null, mparamsets, IOperator.CONTAINS));
		
		AndCondition emptyparamset = new AndCondition(new ICondition[]{paramsetcon, mparamsetcon});
		NotCondition noemptyparamset = new NotCondition(emptyparamset);
		
		Rule querygoal_succeeded = new Rule("querygoal_succeeded", new AndCondition(
			new ICondition[]{goalcon, capcon, noemptyparam, noemptyparamset}), GOAL_SUCCEEDED, IPriorityEvaluator.PRIORITY_1);
		return querygoal_succeeded;
	}*/

	
	/**
	 *  Create the querygoal failed rule (when the last plan has failed).
	 *  
	 *  recur=true -> never failed
	 *  no more plan = (!retry || (rebuild=false && apl==null)) (in case rebuild=true buildAPL will be called)	
	 * /
	protected static Rule createQuerygoalFailedRule()
	{
		Variable mgoal	= new Variable("?mgoal", OAVBDIMetaModel.querygoal_type);
		Variable rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable fplans	= new Variable("$?fplans", OAVBDIRuntimeModel.plan_type, true);
		Variable retry = new Variable("?retry", OAVJavaType.java_boolean_type);
		Variable rebuild = new Variable("?rebuild", OAVJavaType.java_boolean_type);
		
		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.querygoal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.goal_has_recur, Boolean.FALSE));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_rebuild, retry));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_retry, rebuild));
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, fplans));
		IConstraint rebapl = new AndConstraint(new IConstraint[]{new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, IOperator.EQUAL), new LiteralConstraint(rebuild, Boolean.FALSE)});
		IConstraint nomoreplan = new OrConstraint(new IConstraint[]{new LiteralConstraint(retry, Boolean.FALSE), rebapl});
		goalcon.addConstraint(nomoreplan);
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new BoundConstraint(null, fplans, IOperator.CONTAINS));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));

		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));
		
		Rule querygoal_failed	= new Rule("querygoal_failed", new AndCondition(new ICondition[]{mgoalcon, goalcon, plancon, capcon}), GOAL_FAILED_PLAN_REMOVE);
		return querygoal_failed;
	}*/
	
	/**
	 *  Create the querygoal failed rule (when the last plan has failed).
	 *  
	 *  recur=true -> never failed
	 *  no more plan = (!retry || (rebuild=false && apl==null)) (in case rebuild=true buildAPL will be called)	
	 */
	protected static Rule createQuerygoalFailedRule()
	{
		Variable rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable fplans	= new Variable("$?fplans", OAVBDIRuntimeModel.plan_type, true, false);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, fplans));
		IConstraint rebapl = new AndConstraint(new IConstraint[]{new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, IOperator.EQUAL), 
			new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_rebuild}, Boolean.FALSE)});
		IConstraint nomoreplan = new OrConstraint(new IConstraint[]{new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_retry}, Boolean.FALSE), rebapl});
		goalcon.addConstraint(nomoreplan);
		goalcon.addConstraint(new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_recur}, Boolean.FALSE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.querygoal_type, IOperator.INSTANCEOF));
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new BoundConstraint(null, fplans, IOperator.CONTAINS));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));

		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));
		
		Rule querygoal_failed = new Rule("querygoal_failed", new AndCondition(new ICondition[]{goalcon, plancon, capcon}), GOAL_FAILED_PLAN_REMOVE);
		return querygoal_failed;
	}
	
	/**
	 *  Create the querygoal retry rule.
	 *  Retry a goal when retry=true, posttoall=false, (rebuild=true || apl!=null), plan finished
	 * /
	protected static Rule createQuerygoalRetryRule()
	{
		Variable mgoal	= new Variable("?mgoal", OAVBDIMetaModel.querygoal_type);
		Variable rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable recalc	= new Variable("?recalc", OAVJavaType.java_boolean_type);
		
		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.querygoal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_rebuild, recalc));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.goal_has_retry, Boolean.TRUE));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.processableelement_has_posttoall, Boolean.FALSE));

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, IGoal.LIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_plans, rplan, IOperator.CONTAINS));
		IConstraint conaplnotnull = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, IOperator.NOTEQUAL);
		IConstraint conrecalc = new LiteralConstraint(recalc, Boolean.TRUE);
		OrConstraint orcon = new OrConstraint(new IConstraint[]{conaplnotnull, conrecalc});
		goalcon.addConstraint(orcon);
	
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null,rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));
	
		Rule querygoal_retry	= new Rule("querygoal_retry", new AndCondition(new ICondition[]{plancon, mgoalcon, goalcon, capcon}), GOAL_RETRY);
		return querygoal_retry;
	}*/
	
	//-------- maintaingoal rules --------
	
	/**
	 *  Create maintaingoal processing rule.
	 *  Start processing when maintain condition gets violated.
	 *  @param usercond	The ADF part of the maintain condition (will be negated to trigger maintenance).
	 *  @param model The goal model element.
	 */
	public static Object[]	createMaintaingoalProcessingUserRule(Object model)
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE));
//		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.maintaingoal_type, IOperator.INSTANCEOF));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, model));
		
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(null, rcapa));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		
		return new Object[]{new AndCondition(new ICondition[]{goalcon, capacon}),
			GOAL_PROCESSING,
			IPriorityEvaluator.PRIORITY_1,
			null,
			Boolean.TRUE};
	}

	/**
	 *  Create the maintaingoal succeeded rule.
	 *  @param usercond	The ADF part of the target condition.
	 *  @param model The goal model element.
	 */
	public static Object[]	createMaintaingoalSucceededUserRule(Object model)
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
//		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.maintaingoal_type, IOperator.INSTANCEOF));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, model));
		
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(null, rcapa));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		
		return new Object[]{
			new AndCondition(new ICondition[]{goalcon, capacon}),
			GOAL_IDLE,
			IPriorityEvaluator.PRIORITY_1};
	}
	
	/**
	 *  Create the maintaingoal failed rule (when the last plan has failed).
	 * /
	protected static Rule createMaintaingoalFailedRule()
	{
		Variable	mgoal	= new Variable("?mgoal", OAVBDIMetaModel.maintaingoal_type);
		Variable	rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable	rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);

		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));

		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.maintaingoal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		IConstraint reb = new LiteralConstraint(OAVBDIMetaModel.goal_has_rebuild, Boolean.FALSE);
		IConstraint retry = new LiteralConstraint(OAVBDIMetaModel.goal_has_retry, Boolean.FALSE);
		mgoalcon.addConstraint(new OrConstraint(new IConstraint[]{reb, retry}));

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, IOperator.EQUAL));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, rplan, IOperator.CONTAINS));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));

		Rule maintaingoal_failed = new Rule("maintaingoal_failed", new AndCondition(new ICondition[]{plancon, mgoalcon, goalcon, capcon}), 
			GOAL_FAILED_PLAN_REMOVE);
		return maintaingoal_failed;
	}*/
	
	/**
	 *  Create the maintaingoal failed rule (when the last plan has failed).
	 */
	public static Rule createMaintaingoalFailedRule()
	{
		Variable rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);

		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, rplan, IOperator.CONTAINS));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.maintaingoal_type, IOperator.INSTANCEOF));
		
		// (!recur && (!retry || (rebuild=false && apl==null)))
		IConstraint notrecur = new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_recur}, Boolean.FALSE);
		IConstraint notreb = new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_rebuild}, Boolean.FALSE);
		IConstraint aplnull = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, IOperator.EQUAL);
		IConstraint notretry = new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_retry}, Boolean.FALSE);
		goalcon.addConstraint(new AndConstraint(new IConstraint[]{notrecur, new OrConstraint(new IConstraint[]{notretry, new AndConstraint(new IConstraint[]{notreb, aplnull})})}));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));

		Rule maintaingoal_failed = new Rule("maintaingoal_failed", new AndCondition(new ICondition[]{plancon, goalcon, capcon}), 
			GOAL_FAILED_PLAN_REMOVE);
		return maintaingoal_failed;
	}
	
	/**
	 *  Create the maintaingoal retry rule.
	 * /
	protected static Rule createMaintaingoalRetryRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.maintaingoal_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable recalc	= new Variable("?recalc", OAVJavaType.java_boolean_type);

		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.maintaingoal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_rebuild, recalc));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.goal_has_retry, Boolean.TRUE));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.processableelement_has_posttoall, Boolean.FALSE));

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, IGoal.LIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_plans, rplan, IOperator.CONTAINS));
		IConstraint conaplnotnull = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, IOperator.NOTEQUAL);
		IConstraint conrecalc = new LiteralConstraint(recalc, Boolean.TRUE);
		OrConstraint orcon = new OrConstraint(new IConstraint[]{conaplnotnull, conrecalc});
		goalcon.addConstraint(orcon);
	
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
		
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));
				
		Rule maintaingoal_retry	= new Rule("maintaingoal_retry", new AndCondition(new ICondition[]{mgoalcon, goalcon, plancon, capacon}), GOAL_RETRY);
		return maintaingoal_retry;
	}*/
	
	/**
	 *  Create the goal retry rule.
	 *  !achievegoal, retry=true, posttoall=false, (rebuild || apl!=null), plan=finished, procstate=candselected
	 * /
	protected static Rule createGoalRetryRule()
	{
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rebuild = new Variable("?recalc", OAVJavaType.java_boolean_type);

		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.goal_type);
		mgoalcon.addConstraint(new LiteralConstraint(OAVAttributeType.OBJECTTYPE, OAVBDIMetaModel.achievegoal_type, IOperator.NOTEQUAL));
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_rebuild, rebuild));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.goal_has_retry, Boolean.TRUE));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.processableelement_has_posttoall, Boolean.FALSE));

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		
		IConstraint conaplnotnull = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, IOperator.NOTEQUAL);
		OrConstraint orcon = new OrConstraint(new IConstraint[]{conaplnotnull, new LiteralConstraint(rebuild, Boolean.TRUE)});
		goalcon.addConstraint(orcon);
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, rplan, IOperator.CONTAINS));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_CANDIDATESSELECTED));
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
		
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));
				
		Rule goal_retry	= new Rule("goal_retry", new AndCondition(new ICondition[]{plancon, mgoalcon, goalcon, capacon}), GOAL_RETRY);
		return goal_retry;
	}*/
	
	/**
	 *  Create the goal retry rule.
	 *  !achievegoal, retry=true, posttoall=false, (rebuild || apl!=null), plan=finished, procstate=candselected
	 */
	public static Rule createGoalRetryRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVAttributeType.OBJECTTYPE}, OAVBDIMetaModel.achievegoal_type, IOperator.NOTEQUAL));
		goalcon.addConstraint(new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_retry}, Boolean.TRUE));
		goalcon.addConstraint(new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.processableelement_has_posttoall}, Boolean.FALSE));
		IConstraint conaplnotnull = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null, IOperator.NOTEQUAL);
		OrConstraint orcon = new OrConstraint(new IConstraint[]{conaplnotnull, new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_rebuild}, Boolean.TRUE)});
		goalcon.addConstraint(orcon);
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_finishedplans, rplan, IOperator.CONTAINS));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_CANDIDATESSELECTED));
		
		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED));
		
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.EXCLUDES));
				
		Rule goal_retry	= new Rule("goal_retry", new AndCondition(new ICondition[]{plancon, goalcon, capacon}), GOAL_RETRY);
		return goal_retry;
	}

	//-------- common actions --------

	/**
	 *  Start the processing of an RGoal by setting the state to in-process.
	 */
	protected static IAction	GOAL_PROCESSING	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rgoal	= assignments.getVariableValue("?rgoal");
//			System.out.println("Processing: "+rgoal+" "+state.getAttributeValue(state.getAttributeValue(rgoal, 
//				OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
			changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS);
		}
	};

	/**
	 *  Set an RGoal to idle.
	 */
	protected static IAction	GOAL_IDLE	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rgoal	= assignments.getVariableValue("?rgoal");
//			
//			System.out.println("Idle: "+rgoal+" "+state.getAttributeValue(state.getAttributeValue(rgoal, 
//				OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
			changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE);
		}
	};
	
	/**
	 *  Retry an RGoal by setting the processingstate to apl-available.
	 *  Requires availability of (additional) plans.
	 */
	protected static IAction GOAL_RETRY	= new IAction()
	{
		public void execute(final IOAVState state, IVariableAssignments assignments)
		{	
			final Object	rgoal	= assignments.getVariableValue("?rgoal");
			final Object	rplan	= assignments.getVariableValue("?rplan");
			
			state.removeAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_finishedplans, rplan);

			Object mgoal = state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model);
			long retrydelay = ((Long)state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_retrydelay)).longValue();
			if(retrydelay>0)
			{
//				// changed *.class to *.TYPE due to javaflow bug
				state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_retrytimer,
					BDIInterpreter.getInterpreter(state).getClockService().createTimer(retrydelay, 
						new InterpreterTimedObject(BDIInterpreter.getInterpreter(state), new CheckedAction()
				{
					public void run()
					{
						doAchieveRetry(state, rgoal);
					}
					
					public void cleanup()
					{
						state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_retrytimer, null);
					}
				})));
			}
			else
			{
				doAchieveRetry(state, rgoal);
			}
		}
	};
	
	/**
	 *  Do the retry action for achieve goals.
	 */
	protected static void doAchieveRetry(IOAVState state, Object rgoal)
	{
		Object mgoal = state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model);
		boolean recalc = ((Boolean)state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_rebuild)).booleanValue();
		
		// Todo: post-to-all!?
		if(recalc)
		{
			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
		}
		else
		{
//			System.out.println("APL available (retry): "+rgoal);
			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_APLAVAILABLE);
		}
	}

	/**
	 *  Set an RGoal to failed.
	 */
	protected static IAction	GOAL_FAILED	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rgoal	= assignments.getVariableValue("?rgoal");
//			System.out.println("Failed: "+rgoal+" "+state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model));
			changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_FAILED);
		}
	};

	/**
	 *  Set an RGoal to failed and remove a finished plan from the goal.
	 */
	protected static IAction	GOAL_FAILED_PLAN_REMOVE	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rgoal	= assignments.getVariableValue("?rgoal");
			Object	rplan	= assignments.getVariableValue("?rplan");
			
//			if(((String)state.getAttributeValue(state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name)).startsWith("purchase"))
//				System.out.println("FailedRemove: "+rgoal+" "+state.getAttributeValue(state.getAttributeValue(rgoal, 
//					OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
			state.removeAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_finishedplans, rplan);

			changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_FAILED);
		}
	};

	/**
	 *  Set an RGoal to succeeded.
	 */
	protected static IAction	GOAL_SUCCEEDED	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rgoal	= assignments.getVariableValue("?rgoal");
//			System.out.println("Succeeded: "+rgoal+" "+state.getAttributeValue(state.getAttributeValue(rgoal, 
//				OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
			changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_SUCCEEDED);
		}
	};
	
	/**
	 *  Set an RGoal to succeeded and remove a finished plan from the goal.
	 */
	protected static IAction	GOAL_SUCCEEDED_PLAN_REMOVE	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rgoal	= assignments.getVariableValue("?rgoal");
			Object	rplan	= assignments.getVariableValue("?rplan");
//			System.out.println("SucceededRemove: "+rgoal+" "+state.getAttributeValue(state.getAttributeValue(rgoal, 
//				OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
			state.removeAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_finishedplans, rplan);
			changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_SUCCEEDED);
		}
	};

	//-------- common rules --------

	/**
	 *  Create the goal failed rule (when no plans available).
	 *  (state = nocandidates, recur = false)
	 */
	protected static Rule createGoalFailedRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_NOCANDIDATES));
		goalcon.addConstraint(new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_recur}, Boolean.FALSE));
		
		Rule goal_failed = new Rule("goal_failed", goalcon, GOAL_FAILED);
		return goal_failed;
	}
	
	/**
	 *  Create a recur rule for a goal when means-end reasoning finished.
	 *  (recur = true, state = inprocess, notexist a plan with reason = goal,
	 *  (procstate = nocandidates | (procstate = candidateseleected & apl=null & !rebuild))) 
	 *  @param state The state.
	 *  @param rgoal The goal.
	 * /
	protected static Rule createGoalRecurRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		Variable rebuild = new Variable("?rebuild", OAVJavaType.java_boolean_type);
		
		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.goal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.goal_has_recur, Boolean.TRUE));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_rebuild, rebuild));
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS));
		IConstraint nocandcon = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_NOCANDIDATES);
		IConstraint candselcon = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_CANDIDATESSELECTED);
		IConstraint noaplcon = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null);
		IConstraint notrebuild = new LiteralConstraint(rebuild, Boolean.FALSE);
		goalcon.addConstraint(new OrConstraint(new IConstraint[]{nocandcon, new AndConstraint(new IConstraint[]{candselcon, noaplcon, notrebuild})}));
		
		ObjectCondition plancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_reason, rgoal));
		
		IAction action = new IAction()
		{
			public void execute(final IOAVState state, IVariableAssignments assignments)
			{
				final Object rgoal = assignments.getVariableValue("?rgoal");
				
//				System.out.println("Recur initiated: "+rgoal);
				changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_PAUSED);
				
				// Initiate recur when delay or no condition
				
				// todo: recur condition
				
				Object mgoal = state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model);
				long recurdelay = ((Long)state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_recurdelay)).longValue();
				Object recurcond = state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_recurcondition);
				
				if(recurdelay>0)
				{
//					// changed *.class to *.TYPE due to javaflow bug
//					IClockService clock = (IClockService)BDIInterpreter.getInterpreter(state).getAgentAdapter().getPlatform().getService(IClockService.class);
					IClockService clock = (IClockService)BDIInterpreter.getInterpreter(state).getAgentAdapter().getPlatform().getService(IClockService.TYPE);
					state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_recurtimer, clock.createTimer(recurdelay, 
						new InterpreterTimedObject(state, new InterpreterTimedObjectAction()
					{
						public void run()
						{
							state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
							changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS);
						}
						
						public void cleanup()
						{
							state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_recurtimer, null);
						}
					})));
				}
				else if(recurcond==null)
				{
					state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
					changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS);
				}
			}
		};
		
		Rule goal_merfinished = new Rule("goal_recur", new AndCondition(new ICondition[]{mgoalcon, goalcon, new NotCondition(plancon)}), action);
		return goal_merfinished;
	}*/
	
	/**
	 *  Create a recur rule for a goal when means-end reasoning finished.
	 *  (recur = true, state = inprocess, notexist a plan with reason = goal,
	 *  (procstate = nocandidates | (procstate = candidateseleected & apl=null & !rebuild))) 
	 *  @param state The state.
	 *  @param rgoal The goal.
	 */
	protected static Rule createGoalRecurRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS));
		IConstraint nocandcon = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_NOCANDIDATES);
		IConstraint candselcon = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_CANDIDATESSELECTED);
		IConstraint noaplcon = new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_apl, null);
		IConstraint notrebuild = new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_rebuild}, Boolean.FALSE);
		goalcon.addConstraint(new OrConstraint(new IConstraint[]{nocandcon, new AndConstraint(new IConstraint[]{candselcon, noaplcon, notrebuild})}));
		goalcon.addConstraint(new LiteralConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_recur}, Boolean.TRUE));
		
		ObjectCondition plancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_reason, rgoal));
		
		IAction action = new IAction()
		{
			public void execute(final IOAVState state, IVariableAssignments assignments)
			{
				final Object rgoal = assignments.getVariableValue("?rgoal");
				
//				System.out.println("Recur initiated: "+rgoal);
				changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_PAUSED);
				
				// Initiate recur when delay or no condition (otherwise recur user rule is used)
				Object mgoal = state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model);
				long recurdelay = ((Long)state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_recurdelay)).longValue();
				Object recurcond = state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_recurcondition);
				
				if(recurdelay>0)
				{
					state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_recurtimer,
						BDIInterpreter.getInterpreter(state).getClockService().createTimer(recurdelay, 
							new InterpreterTimedObject(BDIInterpreter.getInterpreter(state), new CheckedAction()
					{
						public void run()
						{
							state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
							changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS);
						}
						
						public void cleanup()
						{
							state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_recurtimer, null);
						}
					})));
				}
				else if(recurcond==null)
				{
					state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
					changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS);
				}
			}
		};
		
		Rule goal_merfinished = new Rule("goal_recur", new AndCondition(new ICondition[]{goalcon, new NotCondition(plancon)}), action);
		return goal_merfinished;
	}
	
	/**
	 *  Start recurring a goal, when the ADF recur condition triggers.
	 *  @param usercond	The ADF part of the context condition.
	 *  @param model	The goal model element.
	 */
	public static Object[]	createGoalRecurUserRule(Object model)
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	rgoalcon = new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		rgoalcon.addConstraint(new BoundConstraint(null, rgoal));
		rgoalcon.addConstraint(new AndConstraint(
			new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPING, IOperator.NOTEQUAL),
			new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED, IOperator.NOTEQUAL)));
		rgoalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_PAUSED));
		rgoalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, model));

		ObjectCondition	rcapcon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapcon.addConstraint(new BoundConstraint(null, rcapa));
		rcapcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));

		return new Object[]{new AndCondition(new ICondition[]{rgoalcon, rcapcon}), new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object rgoal = assignments.getVariableValue("?rgoal");
				state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
				changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS);
			}
		}};
	}
	
}
