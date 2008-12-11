package jadex.bdi.interpreter;

import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.AndConstraint;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.CollectCondition;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.IPriorityEvaluator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.LiteralReturnValueConstraint;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.OrConstraint;
import jadex.rules.rulesystem.rules.PredicateConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.Length;
import jadex.rules.rulesystem.rules.functions.OperatorFunction;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;

/**
 *  These are the rules for realizing the "easy deliberation strategy"
 *  for deciding among a possibly inconsistent goal set.
 *  The strategy changes the lifecycle state of goals between
 *  option <-> active.
 *  The strategy is based on two characteristics:
 *  - inhibition links
 *    - a) on type level (goal_type_a inhibits goal_type_b)
 *    - b) on instance level (goal_type_a inhibits goal_type_b if a condition holds)
 *  - cardinalities (restricts the number of active goals of a given type)
 *  
 *  // todo: expression-based inhibitions!
 */
public class GoalDeliberationRules
{
	//-------- deliberation rules --------

	/**
	 *  Create the deliberate goal activation rule.
	 * /
	protected static Rule createDeliberateGoalActivationRule()
	{
		Variable	rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable	rcapa	= new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable	mgoal	= new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		
		Variable	inhibits1	= new Variable("?inhibits1", OAVBDIMetaModel.inhibits_type);
		Variable	ref1	= new Variable("?ref1", OAVJavaType.java_string_type);
		Variable	mingoal1	= new Variable("?mingoal1", OAVBDIMetaModel.goal_type);
		Variable	ringoal1	= new Variable("?ringoal1", OAVBDIRuntimeModel.goal_type);
		Variable	rcapa1	= new Variable("?rcapa1", OAVBDIRuntimeModel.capability_type);
		
		Variable	inhibits2	= new Variable("?inhibits2", OAVBDIMetaModel.inhibits_type);
		Variable	ref2	= new Variable("?ref2", OAVJavaType.java_string_type);
		Variable	mingoal2	= new Variable("?mingoal2", OAVBDIMetaModel.goal_type);
		Variable	ringoal2	= new Variable("?ringoal2", OAVBDIRuntimeModel.goal_type);
		Variable	rcapa2	= new Variable("?rcapa2", OAVBDIRuntimeModel.capability_type);
		
		// There is some "optionized" ?rgoal
		ObjectCondition	mgoalcon	= new ObjectCondition(OAVBDIMetaModel.goal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.modelelement_has_name, new Variable("?mgoalname", OAVJavaType.java_string_type)));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_cardinality, new Variable("?cardinality", OAVJavaType.java_integer_type)));

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION));
		
		ObjectCondition rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		rcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		
		// There is no inhibiting goal for ?rgoal (option) a) case "when_active"
		ObjectCondition	inhicon1	= new ObjectCondition(OAVBDIMetaModel.inhibits_type);
		inhicon1.addConstraint(new BoundConstraint(null, inhibits1));
		inhicon1.addConstraint(new BoundConstraint(OAVBDIMetaModel.inhibits_has_ref, ref1));
		inhicon1.addConstraint(new LiteralConstraint(OAVBDIMetaModel.inhibits_has_inhibit, OAVBDIMetaModel.INHIBITS_WHEN_ACTIVE));
		
		ObjectCondition	mingoalcon1	= new ObjectCondition(OAVBDIMetaModel.goal_type);
		mingoalcon1.addConstraint(new BoundConstraint(null, mingoal1));
		mingoalcon1.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_inhibits, inhibits1, IOperator.CONTAINS));
		
		ObjectCondition	ingoalcon1	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		ingoalcon1.addConstraint(new BoundConstraint(null, ringoal1));
		ingoalcon1.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mingoal1));
		ingoalcon1.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		
		ObjectCondition	capcon1	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon1.addConstraint(new BoundConstraint(null, rcapa1));
		capcon1.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, ringoal1, IOperator.CONTAINS));
		capcon1.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa1, ref1, rgoal, rcapa})));

		// There is no inhibiting goal for ?rgoal (option) a) case "when_in_process"
		ObjectCondition	inhicon2	= new ObjectCondition(OAVBDIMetaModel.inhibits_type);
		inhicon2.addConstraint(new BoundConstraint(null, inhibits2));
		inhicon2.addConstraint(new BoundConstraint(OAVBDIMetaModel.inhibits_has_ref, ref2));
		inhicon2.addConstraint(new LiteralConstraint(OAVBDIMetaModel.inhibits_has_inhibit, OAVBDIMetaModel.INHIBITS_WHEN_IN_PROCESS));
		
		ObjectCondition	mingoalcon2	= new ObjectCondition(OAVBDIMetaModel.goal_type);
		mingoalcon2.addConstraint(new BoundConstraint(null, mingoal2));
		mingoalcon2.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_inhibits, inhibits2, IOperator.CONTAINS));
		
		ObjectCondition	ingoalcon2	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		ingoalcon2.addConstraint(new BoundConstraint(null, ringoal2));
		ingoalcon2.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mingoal2));
		ingoalcon2.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		ingoalcon2.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS));
		
		ObjectCondition	capcon2	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon2.addConstraint(new BoundConstraint(null, rcapa2));
		capcon2.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, ringoal2, IOperator.CONTAINS));
		capcon2.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa2, ref2, rgoal, rcapa})));
		
		// The cardinality allows activation.
		// Collect number of same goals (same application type) and ensure card>(number of same goals) 
		ObjectCondition	samegoalcon = new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		samegoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, new Variable("?mg", OAVBDIMetaModel.goal_type)));
		samegoalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		ObjectCondition	samemgoalcon	= new ObjectCondition(OAVBDIMetaModel.goal_type);
		samemgoalcon.addConstraint(new BoundConstraint(null, new Variable("?mg", OAVBDIMetaModel.goal_type)));
		samemgoalcon.addConstraint(new BoundConstraint(null, new Variable("?mgoalname", OAVJavaType.java_string_type)));
		CollectCondition cardcon = new CollectCondition(new ObjectCondition[]{samegoalcon, samemgoalcon}, null);
		cardcon.addConstraint(new BoundConstraint(null, new Variable("$?same_goals", OAVBDIRuntimeModel.goal_type, true)));
		FunctionCall fc_num = new FunctionCall(new Length(), new Object[]{new Variable("$?same_goals", OAVBDIRuntimeModel.goal_type, true)});
		FunctionCall fc_numcard = new FunctionCall(new OperatorFunction(IOperator.GREATEROREQUAL), new Object[]{fc_num, new Variable("?cardinality", OAVJavaType.java_integer_type)});
		cardcon.addConstraint(new PredicateConstraint(fc_numcard));
		
		ICondition	cond	= new AndCondition(new ICondition[]{mgoalcon, goalcon, rcapacon,
			new NotCondition(cardcon),
			new NotCondition(new AndCondition(new ICondition[]{inhicon1, mingoalcon1, ingoalcon1, capcon1})),
			new NotCondition(new AndCondition(new ICondition[]{inhicon2, mingoalcon2, ingoalcon2, capcon2}))});

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rgoal	= assignments.getVariableValue("?rgoal");
				state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE);
				GoalProcessingRules.changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE);
			}
		};
		return new Rule("deliberate_goal_activation", cond, action);
	}*/
	
	/**
	 *  Create the deliberate goal deactivation rule.
	 * /
	protected static Rule createDeliberateGoalDeactivationRule()
	{
		Variable rgoal	= new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa	= new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable inhibits	= new Variable("?inhibits", OAVBDIMetaModel.inhibits_type);
		Variable ref	= new Variable("?ref", OAVJavaType.java_string_type);
		Variable mingoal	= new Variable("?mingoal", OAVBDIMetaModel.goal_type);
		Variable ringoal	= new Variable("?ringoal", OAVBDIRuntimeModel.goal_type);
		Variable rincapa	= new Variable("?rincapa", OAVBDIRuntimeModel.capability_type);
		Variable inmode = new Variable("?inmode", OAVJavaType.java_string_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		
		ObjectCondition rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		rcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		
		ObjectCondition	inhicon	= new ObjectCondition(OAVBDIMetaModel.inhibits_type);
		inhicon.addConstraint(new BoundConstraint(null, inhibits));
		inhicon.addConstraint(new BoundConstraint(OAVBDIMetaModel.inhibits_has_ref, ref));
		inhicon.addConstraint(new BoundConstraint(OAVBDIMetaModel.inhibits_has_inhibit, inmode));
		
		ObjectCondition	mingoalcon	= new ObjectCondition(OAVBDIMetaModel.goal_type);
		mingoalcon.addConstraint(new BoundConstraint(null, mingoal));
		mingoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_inhibits, inhibits, IOperator.CONTAINS));
		
		ObjectCondition	ingoalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		ingoalcon.addConstraint(new BoundConstraint(null, ringoal));
		ingoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mingoal));
		ingoalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		ingoalcon.addConstraint(new OrConstraint(new LiteralConstraint(inmode, OAVBDIMetaModel.INHIBITS_WHEN_ACTIVE), 
			new AndConstraint(new LiteralConstraint(inmode, OAVBDIMetaModel.INHIBITS_WHEN_IN_PROCESS), 
				new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS))));
		ingoalcon.addConstraint(new BoundConstraint(null, rgoal, IOperator.NOTEQUAL));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rincapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, ringoal, IOperator.CONTAINS));
		capcon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rincapa, ref, rgoal, rcapa})));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rgoal	= assignments.getVariableValue("?rgoal");
				state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION);
			}
		};
		return new Rule("deliberate_goal_deactivation",
			new AndCondition(new ICondition[]{goalcon, rcapacon, inhicon, mingoalcon, ingoalcon, capcon}), action);
	}*/
	
	//-------- other version --------
	
	/**
	 *  Add an inhibition entry (the inhibitor) to a goal when
	 *  a) there is an inhibiting rgoal on type level
	 *  b) there has no inhibition condition been specified.
	 */
	protected static Rule createAddTypeInhibitionLinkRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable inhibits = new Variable("?inhibits", OAVBDIMetaModel.inhibits_type);
		Variable ref = new Variable("?ref", OAVJavaType.java_string_type);
		Variable mingoal = new Variable("?mingoal", OAVBDIMetaModel.goal_type);
		Variable ringoal = new Variable("?ringoal", OAVBDIRuntimeModel.goal_type);
		Variable rincapa = new Variable("?rincapa", OAVBDIRuntimeModel.capability_type);
		Variable inmode = new Variable("?inmode", OAVJavaType.java_string_type);
		Variable rinhibitors = new Variable("?rinhibitors", OAVBDIRuntimeModel.goal_type, true);

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_inhibitors, rinhibitors));
		
		ObjectCondition rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		rcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		
		ObjectCondition	inhicon	= new ObjectCondition(OAVBDIMetaModel.inhibits_type);
		inhicon.addConstraint(new BoundConstraint(null, inhibits));
		inhicon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.expression_has_content, null));
		inhicon.addConstraint(new BoundConstraint(OAVBDIMetaModel.inhibits_has_ref, ref));
		inhicon.addConstraint(new BoundConstraint(OAVBDIMetaModel.inhibits_has_inhibit, inmode));
	
		ObjectCondition	mingoalcon	= new ObjectCondition(OAVBDIMetaModel.goal_type);
		mingoalcon.addConstraint(new BoundConstraint(null, mingoal));
		mingoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_inhibits, inhibits, IOperator.CONTAINS));
		
		ObjectCondition	ingoalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		ingoalcon.addConstraint(new BoundConstraint(null, ringoal));
		ingoalcon.addConstraint(new BoundConstraint(null, rinhibitors, IOperator.EXCLUDES));
		ingoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mingoal));
		ingoalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		ingoalcon.addConstraint(new OrConstraint(new LiteralConstraint(inmode, OAVBDIMetaModel.INHIBITS_WHEN_ACTIVE), 
			new AndConstraint(new LiteralConstraint(inmode, OAVBDIMetaModel.INHIBITS_WHEN_IN_PROCESS), 
				new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS))));
		ingoalcon.addConstraint(new BoundConstraint(null, rgoal, IOperator.NOTEQUAL));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rincapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, ringoal, IOperator.CONTAINS));
		capcon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rincapa, ref, rgoal, rcapa})));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object rgoal = assignments.getVariableValue("?rgoal");
				Object ringoal = assignments.getVariableValue("?ringoal");
//				System.out.println("Adding inhibitor to goal: "+rgoal+" "+ringoal);
				state.addAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_inhibitors, ringoal);
			}
		};
		return new Rule("2deliberate_goal_addtypeinhibition",
			new AndCondition(new ICondition[]{goalcon, rcapacon, inhicon, mingoalcon, ingoalcon, capcon}), 
			action, IPriorityEvaluator.PRIORITY_1);
	}
	
	/**
	 *  Remove an inhibition entry (the inhibitor) to a goal when
	 *  there was (=inactive or not(inprocess)) an inhibiting rgoal on type level
	 */
	protected static Rule createRemoveTypeInhibitionLinkRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable inhibits = new Variable("?inhibits", OAVBDIMetaModel.inhibits_type);
		Variable ref = new Variable("?ref", OAVJavaType.java_string_type);
		Variable mingoal = new Variable("?mingoal", OAVBDIMetaModel.goal_type);
		Variable ringoal = new Variable("?ringoal", OAVBDIRuntimeModel.goal_type);
//		Variable rincapa = new Variable("?rincapa", OAVBDIRuntimeModel.capability_type);
		Variable inmode = new Variable("?inmode", OAVJavaType.java_string_type);
		Variable rinhibitors = new Variable("?rinhibitors", OAVBDIRuntimeModel.goal_type, true);

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_inhibitors, rinhibitors));
		
		ObjectCondition rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		rcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		
		ObjectCondition	inhicon	= new ObjectCondition(OAVBDIMetaModel.inhibits_type);
		inhicon.addConstraint(new BoundConstraint(null, inhibits));
//		inhicon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.expression_has_content, null));
		inhicon.addConstraint(new BoundConstraint(OAVBDIMetaModel.inhibits_has_ref, ref));
		inhicon.addConstraint(new BoundConstraint(OAVBDIMetaModel.inhibits_has_inhibit, inmode));
	
		ObjectCondition	mingoalcon	= new ObjectCondition(OAVBDIMetaModel.goal_type);
		mingoalcon.addConstraint(new BoundConstraint(null, mingoal));
		mingoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_inhibits, inhibits, IOperator.CONTAINS));
		
		ObjectCondition	ingoalcon = new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		ingoalcon.addConstraint(new BoundConstraint(null, ringoal));
		ingoalcon.addConstraint(new BoundConstraint(null, rinhibitors, IOperator.CONTAINS));
		ingoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mingoal));
//		ingoalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		ingoalcon.addConstraint(new OrConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE, IOperator.NOTEQUAL), 
			new AndConstraint(new LiteralConstraint(inmode, OAVBDIMetaModel.INHIBITS_WHEN_IN_PROCESS), 
				new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS, IOperator.NOTEQUAL))));
		ingoalcon.addConstraint(new BoundConstraint(null, rgoal, IOperator.NOTEQUAL));
		
//		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
//		capcon.addConstraint(new BoundConstraint(null, rincapa));
//		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, ringoal, IOperator.CONTAINS));
//		capcon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rincapa, ref, rgoal, rcapa})));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object rgoal = assignments.getVariableValue("?rgoal");
				Object ringoal = assignments.getVariableValue("?ringoal");
//				System.out.println("Removing inhibitor from goal: "+rgoal+" "+ringoal);
				state.removeAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_inhibitors, ringoal);
			}
		};
		return new Rule("2deliberate_goal_removetypeinhibition",
			new AndCondition(new ICondition[]{goalcon, rcapacon, inhicon, mingoalcon, ingoalcon}), 
			action, IPriorityEvaluator.PRIORITY_1);
	}
	
	/**
	 *  Rule for activating a non-inhibited goal.
	 */
	protected static Rule createActivateGoalRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable mg = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		Variable mgoalname = new Variable("?mgoalname", OAVJavaType.java_string_type);
		Variable samegoals = new Variable("$?same_goals", OAVBDIRuntimeModel.goal_type, true);
		Variable cardinality = new Variable("?cardinality", OAVJavaType.java_integer_type);

		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.goal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.modelelement_has_name, mgoalname));
		mgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_cardinality, cardinality));
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_inhibitors, null));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION));
		
		ObjectCondition rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		rcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		
		// The cardinality allows activation.
		// Collect number of same goals (same application type) and ensure card>(number of same goals) 
		ObjectCondition	samegoalcon = new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		samegoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mg));
		samegoalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		
		ObjectCondition	samemgoalcon = new ObjectCondition(OAVBDIMetaModel.goal_type);
		samemgoalcon.addConstraint(new BoundConstraint(null, mg));
		samemgoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.modelelement_has_name, mgoalname));
		
		CollectCondition cardcon = new CollectCondition(new ObjectCondition[]{samegoalcon, samemgoalcon}, null);
		cardcon.addConstraint(new BoundConstraint(null, samegoals));
		FunctionCall fc_num = new FunctionCall(new Length(), new Object[]{samegoals});
		FunctionCall fc_numcard = new FunctionCall(new OperatorFunction(IOperator.GREATEROREQUAL), new Object[]{fc_num, cardinality});
		cardcon.addConstraint(new PredicateConstraint(fc_numcard));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object rgoal = assignments.getVariableValue("?rgoal");
//				String name = (String)state.getAttributeValue(state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name);
//				if(name.indexOf("get_v")==-1)
//					System.out.println("Deliberate goal activation: "+rgoal+" "+name);
				state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE);
				GoalProcessingRules.changeProcessingState(state, rgoal, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE);
			}
		};
		return new Rule("2deliberate_goal_activation",
			new AndCondition(new ICondition[]{mgoalcon, goalcon, rcapacon, new NotCondition(cardcon)}), action);
	}
	
	/**
	 *  Rule for activating a non-inhibited goal.
	 */
	protected static Rule createDeactivateGoalRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_inhibitors, null, IOperator.NOTEQUAL));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		
		ObjectCondition rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		rcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object rgoal = assignments.getVariableValue("?rgoal");
//				String name = (String)state.getAttributeValue(state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name);
//				if(name.indexOf("get_v")==-1)
//					System.out.println("Deliberate goal deactivation: "+rgoal+" "+name);
				state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION);
			}
		};
		return new Rule("2deliberate_goal_deactivation",
			new AndCondition(new ICondition[]{goalcon, rcapacon}), action);
	}
	
	/**
	 *  Create the goal exit active state rule.
	 *  Stop goal processing, if necessary.
	 */
	protected static Rule createGoalExitActiveStateRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE, IOperator.NOTEQUAL));
		OrConstraint statecon = new OrConstraint(new IConstraint[]{
			new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS),
			new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_PAUSED)
		});
		goalcon.addConstraint(statecon);
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, new Variable("?rgoal", OAVBDIMetaModel.goal_type), IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rgoal	= assignments.getVariableValue("?rgoal");
//				System.out.println("Exit active state of: "+rgoal);
				GoalProcessingRules.changeProcessingState(state, rgoal, null);	// Todo: aborted?
			}
		};
		
//		System.out.println("exit active: "+rgoal);
		
		return new Rule("exit_active_state", new AndCondition(new ICondition[]{goalcon, capcon}), action, IPriorityEvaluator.PRIORITY_1);
	}
}
