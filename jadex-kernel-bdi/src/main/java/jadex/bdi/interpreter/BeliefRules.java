package jadex.bdi.interpreter;

import jadex.commons.SReflect;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.IPriorityEvaluator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.OrConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;


/**
 *  Static helper class for belief rules and actions.
 *  
 *  No rules here yet.
 */
public class BeliefRules
{
	//-------- constants --------
	
	/** The argument types for property change listener adding/removal (cached for speed). */
	protected static Class[]	PCL	= new Class[]{PropertyChangeListener.class};

	//-------- helper methods --------
	
	/**
	 *  Change values of a dynamic belief set.
	 *  @param state	The OAV state.
	 *  @param rbeliefset	The belief set handle.
	 *  @param neworigfacts	The new set of facts (some iterable object).
	 */
	protected static void updateBeliefSet(IOAVState state, Object rbeliefset, Object neworigfacts)
	{
		Collection newfacts = null;
		
		Collection oldorigfacts = state.getAttributeValues(rbeliefset, OAVBDIRuntimeModel.beliefset_has_facts);
		Collection oldfacts = null;
		
		// Clone newfacts for being able to use efficient contains.
		if(neworigfacts!=null)
		{
			newfacts = new HashSet();
			for(Iterator it=SReflect.getIterator(neworigfacts); it.hasNext(); )
				newfacts.add(it.next());
		}
		
		// Clone oldfacts because state return original collection.
		if(oldorigfacts!=null)
		{
			oldfacts = new HashSet();
			for(Iterator it=oldorigfacts.iterator(); it.hasNext(); )
				oldfacts.add(it.next());
		}
		
		if(newfacts!=null)
		{
			for(Iterator it=newfacts.iterator(); it.hasNext(); )
			{
				Object newfact = it.next();
				if(oldfacts==null || !oldfacts.contains(newfact))
					addBeliefSetValue(state, rbeliefset, newfact);
			}
		}
		if(oldfacts!=null)
		{
			for(Iterator it=oldfacts.iterator(); it.hasNext(); )
			{
				Object oldfact = it.next();
				if(newfacts==null || !newfacts.contains(oldfact))
					removeBeliefSetValue(state, rbeliefset, oldfact);
			}
		}
	}

	/**
	 *  Set the value of a belief.
	 *  belief_has_fact should only be modified through this method!
	 */
	public static void	setBeliefValue(IOAVState state, Object rbelief, Object fact)
	{
		// Convert wrapped basic values to desired class (e.g. Integer to Long).
		Object	mbel	= state.getAttributeValue(rbelief, OAVBDIRuntimeModel.element_has_model);
		Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
		fact = SReflect.convertWrappedValue(fact, clazz);
		
		Object oldfact = state.getAttributeValue(rbelief, OAVBDIRuntimeModel.belief_has_fact);
		
		if(oldfact!=fact)	// Update state, even when facts are equal (state will take care of not throwing event).
		{
			state.setAttributeValue(rbelief, OAVBDIRuntimeModel.belief_has_fact, fact);
		}
	}

	/**
	 *  Get the value of a belief.
	 *  Evaluates expression for dynamic (pull) belief.
	 */
	public static Object getBeliefValue(IOAVState state, Object rbelief, Object scope)
	{
		Object ret;
		
		Object	mbel	= state.getAttributeValue(rbelief, OAVBDIRuntimeModel.element_has_model);
		Object	evamode = state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_evaluationmode);
		final Long	update	= (Long)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_updaterate);
		if(!OAVBDIMetaModel.EVALUATIONMODE_PULL.equals(evamode) && update==null)
		{
			ret	= state.getAttributeValue(rbelief, OAVBDIRuntimeModel.belief_has_fact);
		}
		else
		{
			Object	exp = state.getAttributeValue(mbel, OAVBDIMetaModel.belief_has_fact);
			ret	= AgentRules.evaluateExpression(state, exp, new OAVBDIFetcher(state, scope));

			// Convert wrapped basic values to desired class (e.g. Integer to Long).
			Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
			ret	= SReflect.convertWrappedValue(ret, clazz);
		}
		
		return  ret;
	}
	
	/**
	 *  Add a value to a belief set.
	 *  beliefset_has_facts should only be modified through this method!
	 */
	public static void	addBeliefSetValue(IOAVState state, Object rbeliefset, Object fact)
	{
		// Convert wrapped basic values to desired class (e.g. Integer to Long).
		Object	mbel	= state.getAttributeValue(rbeliefset, OAVBDIRuntimeModel.element_has_model);
		Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
		fact	= SReflect.convertWrappedValue(fact, clazz);

		state.addAttributeValue(rbeliefset, OAVBDIRuntimeModel.beliefset_has_facts, fact);
		
//		System.out.println("Added bel val: "+rbeliefset+" "+fact);
	}

	/**
	 *  Remove a value from a belief set.
	 *  Takes care of registering / deregistering.
	 *  beliefset_has_facts should only be modified through this method!
	 */
	public static void	removeBeliefSetValue(IOAVState state, Object rbeliefset, Object fact)
	{
		// Convert wrapped basic values to desired class (e.g. Integer to Long).
		Object	mbel	= state.getAttributeValue(rbeliefset, OAVBDIRuntimeModel.element_has_model);
		Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
		fact	= SReflect.convertWrappedValue(fact, clazz);

		state.removeAttributeValue(rbeliefset, OAVBDIRuntimeModel.beliefset_has_facts, fact);
	}
	
	
	/**
	 *  Create a rule for a dynamic fact.
	 *  @param usercond	The ADF part of the target condition.
	 *  @param btname	The belief type name (e.g. "location").
	 */
	protected static Rule createDynamicBeliefUserRule(ICondition usercond, String btname)
	{
		Variable mbelief = new Variable("?mbelief", OAVBDIMetaModel.belief_type);
		Variable rbelief = new Variable("?rbelief", OAVBDIRuntimeModel.belief_type);
			
		ObjectCondition	mbelcon = new ObjectCondition(OAVBDIMetaModel.belief_type);
		mbelcon.addConstraint(new BoundConstraint(null, mbelief));
		mbelcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.modelelement_has_name, btname));
		
		ObjectCondition	belcon	= new ObjectCondition(OAVBDIRuntimeModel.belief_type);
		belcon.addConstraint(new BoundConstraint(null, rbelief));
		belcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mbelief));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefs, rbelief, IOperator.CONTAINS));

		Rule dynamic_belief = new Rule(btname+"_dynamicfact", new AndCondition(new ICondition[]{mbelcon, belcon, capcon, usercond}), 
			DYNAMIC_BELIEF_CHANGED);
		return dynamic_belief;
	}
	
	/**
	 *  Action that gets executed when new fact available.
	 */
	protected static IAction DYNAMIC_BELIEF_CHANGED = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rbelief	= assignments.getVariableValue("?rbelief");
			Object	fact	= assignments.getVariableValue("?ret");
			
			BeliefRules.setBeliefValue(state, rbelief, fact);
		}
	};
	
	/**
	 *  Create a rule for a dynamic facts expression.
	 *  @param usercond	The ADF part of the target condition.
	 *  @param btname	The belief type name (e.g. "location").
	 */
	protected static Rule createDynamicBeliefSetUserRule(ICondition usercond, String btname)
	{
		Variable mbeliefset = new Variable("?mbeliefset", OAVBDIMetaModel.beliefset_type);
//		Variable mcapa = new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable rbeliefset = new Variable("?rbeliefset", OAVBDIRuntimeModel.beliefset_type);
			
		ObjectCondition	mbelsetcon = new ObjectCondition(OAVBDIMetaModel.beliefset_type);
		mbelsetcon.addConstraint(new BoundConstraint(null, mbeliefset));
		mbelsetcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.modelelement_has_name, btname));
		
		ObjectCondition	belsetcon	= new ObjectCondition(OAVBDIRuntimeModel.beliefset_type);
		belsetcon.addConstraint(new BoundConstraint(null, rbeliefset));
		belsetcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mbeliefset));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefsets, rbeliefset, IOperator.CONTAINS));

		Rule dynamic_belief = new Rule(btname+"_dynamicfacts", new AndCondition(new ICondition[]{mbelsetcon, belsetcon, capcon, usercond}), 
			DYNAMIC_BELIEFSET_CHANGED);
		return dynamic_belief;
	}
	
	/**
	 *  Action that gets executed when new facts available.
	 */
	protected static IAction DYNAMIC_BELIEFSET_CHANGED = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object rbeliefset	= assignments.getVariableValue("?rbeliefset");
			Object neworigfacts	= assignments.getVariableValue("$?ret");
			
			updateBeliefSet(state, rbeliefset, neworigfacts);
		}
	};
	
	/**
	 *  Create a rule for an ADF condition.
	 *  @param usercond The user condition.
	 *  @param mcond The condition's model element. 
	 */
	protected static Rule createConditionUserRule(ICondition usercond, Object mcondition, String name)
	{
		Variable mcond = new Variable("?mcondition", OAVBDIMetaModel.condition_type);
		Variable wa = new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition mcondcon = new ObjectCondition(OAVBDIMetaModel.condition_type);
		mcondcon.addConstraint(new BoundConstraint(null, mcond));
		mcondcon.addConstraint(new LiteralConstraint(null, mcondition));
		
		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
		wacon.addConstraint(new BoundConstraint(null, wa));
		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_conditiontypes, mcond, IOperator.CONTAINS));
		
		ObjectCondition plancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));

		ObjectCondition capacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(null, rcapa));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));

		Rule cond = new Rule(name+"_condition", new AndCondition(new ICondition[]{mcondcon, wacon, plancon, capacon, usercond}),
			PLAN_WAIT_FOR_CONDITION);
		
		return cond;
	}
	
	/**
	 *  Action that gets executed when new facts available.
	 */
	protected static IAction PLAN_WAIT_FOR_CONDITION = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object rcapa = assignments.getVariableValue("?rcapa");
			Object rplan = assignments.getVariableValue("?rplan");

//			System.out.println("WFC: Setting plan to ready: "+rplan);
			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, 
				OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);

			PlanRules.cleanupPlanWait(state, rcapa, rplan, false);
			// todo: provide activation resp. variable bindings
			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement, null);
			BDIInterpreter.getInterpreter(state).getAgentAdapter().wakeup();
		}
	};
	
	/**
	 *  Create a rule for a dynamic parameter value.
	 *  @param mpename The processable model element name.
	 *  @param usercond	The ADF part of the target condition.
	 *  @param ptname The parameter type name (e.g. "location").
	 */
	protected static Rule createDynamicParameterUserRule(String mpename, ICondition usercond, String ptname)
	{
		Variable mparam = new Variable("?mparameter", OAVBDIMetaModel.parameter_type);
		Variable rparam = new Variable("?rparameter", OAVBDIRuntimeModel.parameter_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.parameterelement_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.parameterelement_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	rparamcon	= new ObjectCondition(OAVBDIRuntimeModel.parameter_type);
		rparamcon.addConstraint(new BoundConstraint(null, rparam));
		rparamcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.parameter_has_name, ptname));
//		belcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mparam));
		
		ObjectCondition	rparamelemcon = new ObjectCondition(OAVBDIRuntimeModel.parameterelement_type);
		rparamelemcon.addConstraint(new BoundConstraint(null, rpe));
		rparamelemcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		rparamelemcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameterelement_has_parameters, rparam, IOperator.CONTAINS));

		ObjectCondition	mparamcon = new ObjectCondition(OAVBDIMetaModel.parameter_type);
		mparamcon.addConstraint(new BoundConstraint(null, mparam));
		mparamcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.modelelement_has_name, ptname));
		
		ObjectCondition	mparamelemcon = new ObjectCondition(OAVBDIMetaModel.parameterelement_type);
		mparamelemcon.addConstraint(new BoundConstraint(null, mpe));
//		mparamelemcon.addConstraint(new LiteralConstraint(null, mparamelem));
		mparamelemcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.modelelement_has_name, mpename));
		mparamelemcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.parameterelement_has_parameters, mparam, IOperator.CONTAINS));

		ObjectCondition	rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		IConstraint con1 = new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rpe, IOperator.CONTAINS);
		IConstraint con2 = new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rpe, IOperator.CONTAINS);
		rcapacon.addConstraint(new OrConstraint(new IConstraint[]{con1, con2}));
		
		Rule dynamic_param = new Rule(mpename+"_"+ptname+"_dynamicvalue", new AndCondition(new ICondition[]{rparamcon, rparamelemcon,
			mparamcon, mparamelemcon, rcapacon, usercond}), DYNAMIC_VALUE_CHANGED, IPriorityEvaluator.PRIORITY_1);
		return dynamic_param;
	}
	
	/**
	 *  Action that gets executed when new value available.
	 */
	protected static IAction DYNAMIC_VALUE_CHANGED = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object rparam = assignments.getVariableValue("?rparameter");
			Object value = assignments.getVariableValue("?ret");
			
//			Object rpe = assignments.getVariableValue("?rpe");
//			System.out.println("RPE "+rpe+" "+state.getAttributeValue(rpe, OAVBDIRuntimeModel.element_has_model)+" "+rparam);

			BeliefRules.setParameterValue(state, rparam, value);
		}
	};
	
	/**
	 *  Set the value of a parameter.
	 *  parameter_has_value should only be modified through this method!
	 */
	public static void	setParameterValue(IOAVState state, Object rparam, Object value)
	{
		Class clazz = (Class)state.getAttributeValue(rparam, OAVBDIRuntimeModel.parameter_has_type);
		value = SReflect.convertWrappedValue(value, clazz);
		
		Object oldval = state.getAttributeValue(rparam, OAVBDIRuntimeModel.parameter_has_value);
		if(oldval!=value)
		{
			state.setAttributeValue(rparam, OAVBDIRuntimeModel.parameter_has_value, value);
		}
	}
	
	/**
	 *  Create a parameter.
	 */
	public static Object createParameter(IOAVState state, String name, Object value, Class clazz, Object rpe)
	{
		assert name!=null;
		assert clazz!=null: name;
		
		Object	rparam	= state.createObject(OAVBDIRuntimeModel.parameter_type);
		state.setAttributeValue(rparam, OAVBDIRuntimeModel.parameter_has_name, name);
		state.setAttributeValue(rparam, OAVBDIRuntimeModel.parameter_has_type, clazz);
		if(value==null)
			value = getInitialValue(clazz);
		if(value!=null)
			BeliefRules.setParameterValue(state, rparam, value);
		if(rpe!=null)
			state.addAttributeValue(rpe, OAVBDIRuntimeModel.parameterelement_has_parameters, rparam);
		
		return rparam;
	}
	
	/**
	 *  Create a parameterset.
	 */
	public static Object createParameterSet(IOAVState state, String name, Collection values, Class clazz, Object rpe)
	{
		assert name!=null;
		assert clazz!=null;
		
		Object	rparamset	= state.createObject(OAVBDIRuntimeModel.parameterset_type);
		state.setAttributeValue(rparamset, OAVBDIRuntimeModel.parameterset_has_name, name);
		state.setAttributeValue(rparamset, OAVBDIRuntimeModel.parameterset_has_type, clazz);
		if(values!=null)
		{
			for(Iterator it=values.iterator(); it.hasNext(); )
			{
				BeliefRules.addParameterSetValue(state, rparamset, it.next());
			}
		}
		if(rpe!=null)
			state.addAttributeValue(rpe, OAVBDIRuntimeModel.parameterelement_has_parametersets, rparamset);
		
		return rparamset;
	}
	
	/**
	 *  Create a rule for a dynamic values expression.
	 *  @param mpename The processable model element name.
	 *  @param usercond	The ADF part of the target condition.
	 *  @param ptname The parameter type name (e.g. "location").
	 */
	protected static Rule createDynamicParameterSetUserRule(String mpename, ICondition usercond, String ptname)
	{
		Variable mparam = new Variable("?mparameterset", OAVBDIMetaModel.parameterset_type);
		Variable rparam = new Variable("?rparameterset", OAVBDIRuntimeModel.parameterset_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.parameterelement_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.parameterelement_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	rparamcon	= new ObjectCondition(OAVBDIRuntimeModel.parameterset_type);
		rparamcon.addConstraint(new BoundConstraint(null, rparam));
		rparamcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.parameterset_has_name, ptname));
//		belcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mparam));
		
		ObjectCondition	rparamelemcon = new ObjectCondition(OAVBDIRuntimeModel.parameterelement_type);
		rparamelemcon.addConstraint(new BoundConstraint(null, rpe));
		rparamelemcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		rparamelemcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameterelement_has_parametersets, rparam, IOperator.CONTAINS));

		ObjectCondition	mparamcon = new ObjectCondition(OAVBDIMetaModel.parameterset_type);
		mparamcon.addConstraint(new BoundConstraint(null, mparam));
		mparamcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.modelelement_has_name, ptname));
		
		ObjectCondition	mparamelemcon = new ObjectCondition(OAVBDIMetaModel.parameterelement_type);
		mparamelemcon.addConstraint(new BoundConstraint(null, mpe));
//		mparamelemcon.addConstraint(new LiteralConstraint(null, mparamelem));
		mparamelemcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.modelelement_has_name, mpename));
		mparamelemcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.parameterelement_has_parametersets, mparam, IOperator.CONTAINS));

		ObjectCondition	rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		IConstraint con1 = new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rpe, IOperator.CONTAINS);
		IConstraint con2 = new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rpe, IOperator.CONTAINS);
		rcapacon.addConstraint(new OrConstraint(new IConstraint[]{con1, con2}));
		
		Rule dynamic_paramset = new Rule(mpename+"_"+ptname+"_dynamicvalues", new AndCondition(new ICondition[]{rparamcon, rparamelemcon, 
			mparamcon, mparamelemcon, rcapacon, usercond}), DYNAMIC_VALUES_CHANGED, IPriorityEvaluator.PRIORITY_1);
		return dynamic_paramset;
	}
	
	/**
	 *  Action that gets executed when new values are available.
	 */
	protected static IAction DYNAMIC_VALUES_CHANGED = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object rparamset = assignments.getVariableValue("?rparameterset");
			
			Object neworigvalues = assignments.getVariableValue("?ret");
			
			updateParameterSet(state, rparamset, neworigvalues);
		}
	};
	
	/**
	 *  Change values of a dynamic belief set.
	 *  @param state	The OAV state.
	 *  @param rbeliefset	The belief set handle.
	 *  @param neworigfacts	The new set of facts (some iterable object).
	 */
	protected static void updateParameterSet(IOAVState state, Object rbeliefset, Object neworigfacts)
	{
		Collection newfacts = null;
		
		Collection oldorigfacts = state.getAttributeValues(rbeliefset, OAVBDIRuntimeModel.parameterset_has_values);
		Collection oldfacts = null;
		
		// Clone newfacts for being able to use efficient contains.
		if(neworigfacts!=null)
		{
			newfacts = new HashSet();
			for(Iterator it=SReflect.getIterator(neworigfacts); it.hasNext(); )
				newfacts.add(it.next());
		}
		
		// Clone oldfacts because state return original collection.
		if(oldorigfacts!=null)
		{
			oldfacts = new HashSet();
			for(Iterator it=oldorigfacts.iterator(); it.hasNext(); )
				oldfacts.add(it.next());
		}
		
		if(newfacts!=null)
		{
			for(Iterator it=newfacts.iterator(); it.hasNext(); )
			{
				Object newfact = it.next();
				if(oldfacts==null || !oldfacts.contains(newfact))
					addParameterSetValue(state, rbeliefset, newfact);
			}
		}
		if(oldfacts!=null)
		{
			for(Iterator it=oldfacts.iterator(); it.hasNext(); )
			{
				Object oldfact = it.next();
				if(newfacts==null || !newfacts.contains(oldfact))
					removeParameterSetValue(state, rbeliefset, oldfact);
			}
		}
	}
	
	/**
	 *  Add a value to a parameter set.
	 *  parameterset_has_facts should only be modified through this method!
	 */
	public static void	addParameterSetValue(IOAVState state, Object rparameterset, Object value)
	{
		Class clazz = (Class)state.getAttributeValue(rparameterset, OAVBDIRuntimeModel.parameterset_has_type);
		value = SReflect.convertWrappedValue(value, clazz);

		state.addAttributeValue(rparameterset, OAVBDIRuntimeModel.parameterset_has_values, value);
	}

	/**
	 *  Remove a value from a parameter set.
	 *  parameterset_has_facts should only be modified through this method!
	 */
	public static void	removeParameterSetValue(IOAVState state, Object rparameterset, Object value)
	{
		Class clazz = (Class)state.getAttributeValue(rparameterset, OAVBDIRuntimeModel.parameterset_has_type);
		value = SReflect.convertWrappedValue(value, clazz);

		state.removeAttributeValue(rparameterset, OAVBDIRuntimeModel.parameterset_has_values, value);
	}
	
	/**
	 *  Get initial value for basic types.
	 */
	public static Object getInitialValue(Class clazz)
	{
		Object ret = null;
		
		if(clazz!=null && SReflect.isBasicType(clazz))
			// changed *.class to *.TYPE due to javaflow bug
		{
			if(clazz==Boolean.TYPE)
				ret	= Boolean.FALSE;
			else if(clazz==Byte.TYPE)
				ret	= new Byte((byte)0);
			else if(clazz==Character.TYPE)
				ret	= new Character((char)0);
			else if(clazz==Short.TYPE)
				ret	= new Short((short)0);
			else if(clazz==Double.TYPE)
				ret	= new Double(0);
			else if(clazz==Float.TYPE)
				ret	= new Float(0);
			else if(clazz==Long.TYPE)
				ret	= new Long(0);
			else if(clazz==Integer.TYPE)
				ret	= new Integer(0);
		}
		
		return ret;
	}
	
}
