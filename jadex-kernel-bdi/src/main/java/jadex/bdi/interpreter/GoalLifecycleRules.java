package jadex.bdi.interpreter;

import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.AndConstraint;
import jadex.rules.rulesystem.rules.BoundConstraint;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *  Static helper class for goal lifecycle rules and actions.
 *  These rules are responsible for managing the lifecycle of goals.
 *  Concretely there are conditions that do the following:
 *  - Create a goal (user creation condition)
 *  - Suspend a goal (!context condition)
 *  - Optionize a goal (context condition)
 *  - Drop a goal (drop condition)
 */
public class GoalLifecycleRules
{
	//-------- helper methods --------

	/**
	 *  Create a goal of a given type.
	 *  Uses method instantiateGoal().
	 */
	public static Object createGoal(IOAVState state, Object rcapa, String type)
	{
		Object mcap = state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mcap, OAVBDIMetaModel.capability_has_goals, type))
			throw new RuntimeException("Unknown goal: "+type);
		Object mgoal = state.getAttributeValue(mcap, OAVBDIMetaModel.capability_has_goals, type);
		Object rgoal = GoalLifecycleRules.instantiateGoal(state, rcapa, mgoal, null, null, null, null);
		
		return rgoal;
	}
	
	/**
	 *  Instantiate a goal but does not add it to the state.
	 *  Creates also the goals parameters.
	 *  @param state	The state
	 *  @param rcapa	The capability.
	 *  @param mgoal	The goal model.
	 *  @param cgoal	The goal configuration (if any).
	 *  @return The goal instance.
	 */
	public static Object instantiateGoal(IOAVState state, Object rcapa, Object mgoal, Object cgoal, Map bindings, OAVBDIFetcher fetcher, OAVBDIFetcher configfetcher)
	{		
		Object	rgoal	= state.createObject(OAVBDIRuntimeModel.goal_type);
		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model, mgoal);
		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_NEW);
		
		// todo: adapter?
		if(fetcher==null)
			fetcher = new OAVBDIFetcher(state, rcapa);
		fetcher.setRGoal(rgoal);
		AgentRules.initParameters(state, rgoal, cgoal, fetcher, configfetcher, null, bindings);
		
//		System.out.println("Created goal: "+rcapa+" "+rgoal+" "+state.getAttributeValue(state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
		
		return rgoal;
	}
		
	/**
	 *  Adopt a goal.
	 *  Adds the goal to the state (goalbase).
	 *  @param state	The state
	 *  @param rcapa	The capability.
	 *  @param rgoal	The goal.
	 */
	public static void	adoptGoal(IOAVState state, Object rcapa, Object rgoal)
	{
		Collection coll = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_goals);
		if(coll!=null && coll.contains(rgoal))
			throw new RuntimeException("Cannot adopt already adopted goal: "+rgoal);
		
		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ADOPTED); // for listeners
		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION);
		state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_goals, rgoal);
		
		// Hack!!! Only needed for external access!
		BDIInterpreter.getInterpreter(state).getAgentAdapter().wakeup();
	}
	
	/**
	 *  Drop a goal.
	 *  @param state The state.
	 *  @param rgoal The goal.
	 */
	public static void dropGoal(IOAVState state, Object rgoal)
	{
		// Rule DeliberationRules.exitActiveState() does this too but
		// problem is that in the meantime also other eventprocessing rules
		// could be active
//		System.out.println("Dropped goal: "+rgoal);
		
		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, null);

		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPING);	
	}
	
	//-------- actions --------
	
	static int cnt=0;
	/**
	 *  Instantiate and adopt an MGoal in a given RCapability.
	 */
	protected static IAction	GOAL_CREATION_ACTION	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object rcapa = assignments.getVariableValue("?rcapa");
			Object mgoal = assignments.getVariableValue("?mgoal");
			
			// Create fetcher with binding values.
			OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rcapa);
			String[] varnames = assignments.getVariableNames();
			for(int i=0; i<varnames.length; i++)
			{
				fetcher.setValue(varnames[i], assignments.getVariableValue(varnames[i]));
			}
			
			// Create goals according to binding possibilities.
			List bindings = AgentRules.calculateBindingElements(state, mgoal, null, fetcher);
			if(bindings!=null)
			{
				for(int i=0; i<bindings.size(); i++)
				{
					Object rgoal = instantiateGoal(state, rcapa, mgoal, null, (Map)bindings.get(i), fetcher, null);
//					System.out.println("New goal: "+rgoal+" "+state.getAttributeValue(mgoal, OAVBDIMetaModel.modelelement_has_name));
					adoptGoal(state, rcapa, rgoal);	
				}
			}
			else
			{
				Object rgoal = instantiateGoal(state, rcapa, mgoal, null, null, fetcher, null);
//				System.out.println("New goal: "+rgoal+" "+state.getAttributeValue(mgoal, OAVBDIMetaModel.modelelement_has_name));
				adoptGoal(state, rcapa, rgoal);	
			}
		}
	};
	
	/**
	 *  Set an RGoal to the option state.
	 */
	protected static IAction GOAL_OPTION_ACTION	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rgoal	= assignments.getVariableValue("?rgoal");
			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION);
			
			// Rule DeliberationRules.exitActiveState() does this too but
			// problem is that in the meantime also other eventprocessing rules
			// could be active
			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, null);
						
//			System.out.println("Optionized goal: "+rgoal+" "+state.getAttributeValue(state.getAttributeValue(rgoal, 
//				OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
		}
	};
	
	/**
	 *  Suspend an RGoal.
	 */
	protected static IAction GOAL_SUSPEND_ACTION	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rgoal	= assignments.getVariableValue("?rgoal");
			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_SUSPENDED);

			// Rule DeliberationRules.exitActiveState() does this too but
			// problem is that in the meantime also other eventprocessing rules
			// could be active
			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, null);
						
//			System.out.println("Suspended goal: "+rgoal+" "+state.getAttributeValue(state.getAttributeValue(rgoal, 
//				OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
		}
	};
	
	/**
	 *  Start dropping of an RGoal.
	 *  Will lead to abortion of plans and ultimately the goal will be dropped.
	 */
	protected static IAction GOAL_DROPPING_ACTION	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rgoal	= assignments.getVariableValue("?rgoal");
			dropGoal(state, rgoal);
			if(((String)state.getAttributeValue(state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name)).startsWith("purchase"))
				System.out.println("Dropping goal: "+rgoal+" "+state.getAttributeValue(state.getAttributeValue(rgoal, 
						OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
		}
	};
	
	/**
	 *  Drop an RGoal and remove it from the goalbase. 
	 */
	protected static IAction GOAL_DROP_ACTION	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rgoal	= assignments.getVariableValue("?rgoal");
			Object	rcapa	= assignments.getVariableValue("?rcapa");
			Object	rplan	= state.getAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_parentplan);
			
//			System.out.println("Dropped goal: "+rgoal+" "+state.getAttributeValue(state.getAttributeValue(rgoal, 
//				OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED);
			if(rplan!=null)
			{
				state.removeAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_subgoals, rgoal);
			}
			state.removeAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_goals, rgoal);
		}
	};

	//-------- lifecycle rules --------
	
	/**
	 *  Create a goal, when the ADF creation condition triggers.
	 *  @param usercond	The ADF part of the creation condition.
	 *  @param gtname	The goal type name (e.g. "achievecleanup").
	 */
	protected static Rule createGoalCreationUserRule(ICondition usercond, String gtname)
	{
		Variable ragent = new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable mcapa = new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		
		ObjectCondition	ragentcon	= new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		ragentcon.addConstraint(new BoundConstraint(null, ragent));
		ragentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_state, OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));
		
		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.goal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.modelelement_has_name, gtname));
		
		ObjectCondition	mcapcon	= new ObjectCondition(OAVBDIMetaModel.capability_type);
		mcapcon.addConstraint(new BoundConstraint(null, mcapa));
		mcapcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_goals, mgoal, IOperator.CONTAINS));
		
		Rule rule = new Rule(gtname+"_create", new AndCondition(new ICondition[]{ragentcon, capcon, mgoalcon, mcapcon, usercond}), GOAL_CREATION_ACTION);
		return rule;
	}
	
	/**
	 *  Set a suspended goal to option state, when the ADF suspension condition triggers.
	 *  @param usercond	The ADF condition part.
	 *  @param gtname	The goal type name (e.g. "achievecleanup").
	 */
	protected static Rule createGoalOptionUserRule(ICondition usercond, String gtname)
	{
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		
		ObjectCondition	mgoalcon	= new ObjectCondition(OAVBDIMetaModel.goal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.modelelement_has_name, gtname));
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, new Variable("?mgoal", OAVBDIMetaModel.goal_type)));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_SUSPENDED));
		
		ICondition goalcond = new AndCondition(new ICondition[]{mgoalcon, goalcon, usercond});
		
		Rule goal_optionize	= new Rule(gtname+"_option", goalcond, GOAL_OPTION_ACTION);
		return goal_optionize;
	}

	/**
	 *  Set a goal to suspended state, when the ADF context condition triggers. 
	 *  @param usercond	The ADF part of the context condition (will be negated automatically).
	 *  @param gtname	The goal type name (e.g. "achievecleanup").
	 */
	protected static Rule createGoalSuspendUserRule(ICondition usercond, String gtname)
	{
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		
		ObjectCondition	mgoalcon	= new ObjectCondition(OAVBDIMetaModel.goal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.modelelement_has_name, gtname));
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, new Variable("?mgoal", OAVBDIMetaModel.goal_type)));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_SUSPENDED, IOperator.NOTEQUAL));
		
		ICondition goalcond = new AndCondition(new ICondition[]{mgoalcon, goalcon, new NotCondition(usercond)});
		
		Rule goal_dropping = new Rule(gtname+"_suspend", goalcond, GOAL_SUSPEND_ACTION);
		return goal_dropping;
	}

	/**
	 *  Start dropping a goal that is succeeded or failed.
	 */
	protected static Rule createGoalDroppingRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);

		ObjectCondition mgoalcon = new ObjectCondition(OAVBDIMetaModel.goal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new LiteralConstraint(OAVAttributeType.OBJECTTYPE, OAVBDIMetaModel.maintaingoal_type, IOperator.NOTEQUAL));
		
		// Todo: Not MGoal.recur
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new AndConstraint(
			new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPING, IOperator.NOTEQUAL),
			new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED, IOperator.NOTEQUAL)));
		goalcon.addConstraint(new OrConstraint(
			new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_SUCCEEDED),
			new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_FAILED)));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		
		// Hack!!! Higher priority required to avoid deliberation activating goal that is already finished in option state (e.g. due to immediately fulfilled target condition)
		Rule goal_dropping	= new Rule("goal_dropping", new AndCondition(new ICondition[]{mgoalcon, goalcon, capcon}), GOAL_DROPPING_ACTION, IPriorityEvaluator.PRIORITY_1);
		return goal_dropping;
	}

	/**
	 *  Start dropping a goal, when the ADF context condition triggers.
	 *  @param usercond	The ADF part of the context condition.
	 *  @param gtname	The goal type name (e.g. "achievecleanup").
	 */
	protected static Rule createGoalDroppingUserRule(ICondition usercond, String gtname)
	{
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		
		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.goal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.modelelement_has_name, gtname));
		
		ObjectCondition	rgoalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		rgoalcon.addConstraint(new BoundConstraint(null, rgoal));
		rgoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		rgoalcon.addConstraint(new AndConstraint(
			new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPING, IOperator.NOTEQUAL),
			new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED, IOperator.NOTEQUAL)));
		ICondition goalcond = new AndCondition(new ICondition[]{mgoalcon, rgoalcon, usercond});
		
		Rule goal_dropping = new Rule(gtname+"_drop", goalcond, GOAL_DROPPING_ACTION);
		return goal_dropping;
	}
	
	/**
	 *  Create the goal drop rule.
	 *  Drop a dropping goal when all plans are removed.
	 */
	protected static Rule createGoalDropRule()
	{
		Variable	rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable	rplans	= new Variable("$?rplans", OAVBDIRuntimeModel.plan_type, true);
		Variable	rcapa	= new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(rgoal.getType());
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPING));

		ObjectCondition	capcon	= new ObjectCondition(rcapa.getType());
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplans));

		ObjectCondition	plancon	= new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplans, IOperator.CONTAINS));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED, IOperator.NOTEQUAL));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_reason, rgoal));
		
		NotCondition notplancon	= new NotCondition(plancon);
		
		Rule goal_drop = new Rule("goal_drop", new AndCondition(new ICondition[]{goalcon, capcon, notplancon}), GOAL_DROP_ACTION);
		return goal_drop;
	}
	
	/**
	 *  Exit the active state.
	 *  @param state The state.
	 *  @param rgoal The rgoal.
	 * /
	protected static void exitActiveState(IOAVState state, Object rgoal)
	{
		// Reset eventprocessing to null to allow other elements being processed.
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		if(rgoal.equals(state.getAttributeValue(ip.getAgent(), OAVBDIRuntimeModel.agent_has_eventprocessing)))
			state.setAttributeValue(ip.getAgent(), OAVBDIRuntimeModel.agent_has_eventprocessing, null);
		
		// Reset APL.
		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_apl, null);
		// todo: introduce some state for finished?!
		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, null);//OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
	}*/
}
