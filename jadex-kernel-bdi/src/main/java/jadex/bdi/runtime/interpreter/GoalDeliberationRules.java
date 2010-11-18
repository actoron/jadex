package jadex.bdi.runtime.interpreter;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.AndConstraint;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.CollectCondition;
import jadex.rules.rulesystem.rules.Constant;
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
import jadex.rules.state.OAVAttributeType;
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
 */
public class GoalDeliberationRules
{
	//-------- deliberation rules --------
	
	/**
	 *  Add an inhibition entry (the inhibitor) to a goal when
	 *  a) there is an inhibiting rgoal on type level
	 *  b) there has no inhibition condition been specified.
	 */
	protected static Rule createAddTypeInhibitionLinkRule()
	{
		Variable ringoal = new Variable("?ringoal", OAVBDIRuntimeModel.goal_type);
		Variable rincapa = new Variable("?rincapa", OAVBDIRuntimeModel.capability_type);
		Variable inhibits = new Variable("?inhibits", OAVBDIMetaModel.inhibits_type);
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable ref = new Variable("?ref", OAVJavaType.java_string_type);
		Variable inmode = new Variable("?inmode", OAVJavaType.java_string_type);
		Variable rinhibitors = new Variable("?rinhibitors", OAVBDIRuntimeModel.goal_type, true, false);

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, ringoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_inhibitors, rinhibitors));
		
		ObjectCondition rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rincapa));
		rcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, ringoal, IOperator.CONTAINS));
		
		ObjectCondition	inhicon	= new ObjectCondition(OAVBDIMetaModel.inhibits_type);
		inhicon.addConstraint(new BoundConstraint(null, inhibits));
		inhicon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.expression_has_text, null));	// cannot use parsed because might be null for JCL conditions which are parsed after loading.
		inhicon.addConstraint(new BoundConstraint(OAVBDIMetaModel.inhibits_has_ref, ref));
		inhicon.addConstraint(new BoundConstraint(OAVBDIMetaModel.inhibits_has_inhibit, inmode));
	
		ObjectCondition	mingoalcon	= new ObjectCondition(OAVBDIMetaModel.goal_type);
		mingoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mingoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_inhibits, inhibits, IOperator.CONTAINS));
		
		ObjectCondition	ingoalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		ingoalcon.addConstraint(new BoundConstraint(null, rgoal));
		ingoalcon.addConstraint(new BoundConstraint(null, rinhibitors, IOperator.EXCLUDES));
		ingoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		ingoalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		ingoalcon.addConstraint(new OrConstraint(new LiteralConstraint(inmode, OAVBDIMetaModel.INHIBITS_WHEN_ACTIVE), 
			new AndConstraint(new LiteralConstraint(inmode, OAVBDIMetaModel.INHIBITS_WHEN_IN_PROCESS), 
				new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS))));
		ingoalcon.addConstraint(new BoundConstraint(null, ringoal, IOperator.NOTEQUAL));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa, ref, ringoal, rincapa})));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object ringoal = assignments.getVariableValue("?ringoal");
				Object rgoal = assignments.getVariableValue("?rgoal");
//				System.out.println("Adding inhibitor to goal: "+ringoal+" "+rgoal);
				state.addAttributeValue(ringoal, OAVBDIRuntimeModel.goal_has_inhibitors, rgoal);
			}
		};
		return new Rule("goal_deliberate_addtypeinhibition",
			new AndCondition(new ICondition[]{goalcon, rcapacon, inhicon, mingoalcon, ingoalcon, capcon}), 
			action, IPriorityEvaluator.PRIORITY_1);
	}
	
	/**
	 *  Remove an inhibition entry (the inhibitor) to a goal when
	 *  there was (=inactive or not(inprocess)) an inhibiting rgoal on type level
	 */
	protected static Rule createRemoveTypeInhibitionLinkRule()
	{
		Variable ringoal = new Variable("?ringoal", OAVBDIRuntimeModel.goal_type);
		Variable rincapa = new Variable("?rincapa", OAVBDIRuntimeModel.capability_type);
		Variable inhibits = new Variable("?inhibits", OAVBDIMetaModel.inhibits_type);
		Variable ref = new Variable("?ref", OAVJavaType.java_string_type);
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable inmode = new Variable("?inmode", OAVJavaType.java_string_type);
		Variable rinhibitors = new Variable("?rinhibitors", OAVBDIRuntimeModel.goal_type, true, false);

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, ringoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_inhibitors, rinhibitors));
		
		ObjectCondition rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rincapa));
		rcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, ringoal, IOperator.CONTAINS));
		
		ObjectCondition	inhicon	= new ObjectCondition(OAVBDIMetaModel.inhibits_type);
		inhicon.addConstraint(new BoundConstraint(null, inhibits));
//		inhicon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.expression_has_content, null));
		inhicon.addConstraint(new BoundConstraint(OAVBDIMetaModel.inhibits_has_ref, ref));
		inhicon.addConstraint(new BoundConstraint(OAVBDIMetaModel.inhibits_has_inhibit, inmode));
	
		ObjectCondition	mingoalcon	= new ObjectCondition(OAVBDIMetaModel.goal_type);
		mingoalcon.addConstraint(new BoundConstraint(null, mgoal));
		mingoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.goal_has_inhibits, inhibits, IOperator.CONTAINS));
		
		ObjectCondition	ingoalcon = new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		ingoalcon.addConstraint(new BoundConstraint(null, rgoal));
		ingoalcon.addConstraint(new BoundConstraint(null, rinhibitors, IOperator.CONTAINS));
		ingoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
//		ingoalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		ingoalcon.addConstraint(new OrConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE, IOperator.NOTEQUAL), 
			new AndConstraint(new LiteralConstraint(inmode, OAVBDIMetaModel.INHIBITS_WHEN_IN_PROCESS), 
				new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS, IOperator.NOTEQUAL))));
		ingoalcon.addConstraint(new BoundConstraint(null, ringoal, IOperator.NOTEQUAL));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa, ref, ringoal, rincapa})));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object ringoal = assignments.getVariableValue("?ringoal");
				Object rgoal = assignments.getVariableValue("?rgoal");
//				System.out.println("Removing inhibitor from goal: "+ringoal+" "+rgoal);
				state.removeAttributeValue(ringoal, OAVBDIRuntimeModel.goal_has_inhibitors, rgoal);
			}
		};
		return new Rule("goal_deliberate_removetypeinhibition",
			new AndCondition(new ICondition[]{goalcon, rcapacon, inhicon, mingoalcon, ingoalcon, capcon}), 
			action, IPriorityEvaluator.PRIORITY_1);
	}
	
	/**
	 *  Add an inhibition entry (the inhibitor) to a goal when
	 *  a) there is an inhibiting rgoal on type level
	 *  b) the inhibition condition triggers.
	 */
	public static Object[]	createAddInhibitionLinkUserRule(Object model, String inmode, String ref)
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable refgoal = new Variable("?refgoal", OAVBDIRuntimeModel.goal_type);
		Variable refcapa = new Variable("?rrefcapa", OAVBDIRuntimeModel.capability_type);

		// The inhibiting goal (?rgoal)
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, model));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		if(OAVBDIMetaModel.INHIBITS_WHEN_IN_PROCESS.equals(inmode))
			goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS));

		// The inhibited goal (?refgoal)
		ObjectCondition	refgoalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		refgoalcon.addConstraint(new BoundConstraint(null, refgoal));
		refgoalcon.addConstraint(new BoundConstraint(null, rgoal, IOperator.NOTEQUAL));
		refgoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_inhibitors, rgoal, IOperator.EXCLUDES));
		
		ObjectCondition refcapcon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		refcapcon.addConstraint(new BoundConstraint(null, refcapa));
		refcapcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, refgoal, IOperator.CONTAINS));		
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa, new Constant(ref), refgoal, refcapa})));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object refgoal = assignments.getVariableValue("?refgoal");
				Object rgoal = assignments.getVariableValue("?rgoal");
//				System.out.println("Adding instance inhibitor to goal: "+refgoal+" "+rgoal);
				state.addAttributeValue(refgoal, OAVBDIRuntimeModel.goal_has_inhibitors, rgoal);
			}
		};
		return new Object[]{
			new AndCondition(new ICondition[]{goalcon, refgoalcon, refcapcon, capcon}),
			action, IPriorityEvaluator.PRIORITY_1};
	}
	
	/**
	 *  Remove an inhibition entry (the inhibitor) from a goal when
	 *  b) the negated inhibition condition triggers.
	 */
	public static Object[]	createRemoveInhibitionLinkUserRule(Object model, String inmode, String ref)
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable refgoal = new Variable("?refgoal", OAVBDIRuntimeModel.goal_type);
		Variable refcapa = new Variable("?rrefcapa", OAVBDIRuntimeModel.capability_type);

		// The inhibiting goal (?rgoal)
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, model));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		if(OAVBDIMetaModel.INHIBITS_WHEN_IN_PROCESS.equals(inmode))
			goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_processingstate, OAVBDIRuntimeModel.GOALPROCESSINGSTATE_INPROCESS));

		// The inhibited goal (?refgoal)
		ObjectCondition	refgoalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		refgoalcon.addConstraint(new BoundConstraint(null, refgoal));
		refgoalcon.addConstraint(new BoundConstraint(null, rgoal, IOperator.NOTEQUAL));
		refgoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.goal_has_inhibitors, rgoal, IOperator.CONTAINS));
		
		ObjectCondition refcapcon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		refcapcon.addConstraint(new BoundConstraint(null, refcapa));
		refcapcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, refgoal, IOperator.CONTAINS));		
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		capcon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa, new Constant(ref), refgoal, refcapa})));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object refgoal = assignments.getVariableValue("?refgoal");
				Object rgoal = assignments.getVariableValue("?rgoal");
//				System.out.println("Removing instance inhibitor from goal: "+refgoal+" "+rgoal);
				state.removeAttributeValue(refgoal, OAVBDIRuntimeModel.goal_has_inhibitors, rgoal);
			}
		};
		return new Object[]{
			new AndCondition(new ICondition[]{goalcon, refgoalcon, refcapcon, capcon}), 
			action,
			IPriorityEvaluator.PRIORITY_1,
			null,
			Boolean.TRUE};
	}
	
	/**
	 *  Rule for activating a non-inhibited goal.
	 * /
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
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
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
		// Not-Node required, because rule would otherwise not trigger if no goals are active.
		return new Rule("deliberate_goal_activation",
			new AndCondition(new ICondition[]{mgoalcon, goalcon, rcapacon, new NotCondition(cardcon)}), action);
	}*/
	
	/**
	 *  Rule for activating a non-inhibited goal.
	 */
	protected static Rule createActivateGoalRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable samegoals = new Variable("$?same_goals", OAVBDIRuntimeModel.goal_type, true, false);
		Variable cardinality = new Variable("?cardinality", OAVJavaType.java_integer_type);

		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		goalcon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.element_has_model, OAVBDIMetaModel.goal_has_cardinality}, cardinality));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_inhibitors, null));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION));
		
		ObjectCondition rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		rcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rgoal, IOperator.CONTAINS));
		
		// The cardinality allows activation.
		// Collect number of same goals (same model) and ensure that not (number of same goals)>=cardinality 
		ObjectCondition	samegoalcon = new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		samegoalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		samegoalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE));
		
		CollectCondition cardcon = new CollectCondition(samegoalcon, null);
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
		// Not-Node required, because rule would otherwise not trigger if no goals are active.
		return new Rule("goal_deliberate_activation",
			new AndCondition(new ICondition[]{goalcon, rcapacon, new NotCondition(cardcon)}), action);
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
		return new Rule("goal_deliberate_deactivation",
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
		
		return new Rule("goal_exit_active_state", new AndCondition(new ICondition[]{goalcon, capcon}), action, IPriorityEvaluator.PRIORITY_1);
	}
}
