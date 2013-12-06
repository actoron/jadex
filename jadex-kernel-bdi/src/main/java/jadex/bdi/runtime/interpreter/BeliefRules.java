package jadex.bdi.runtime.interpreter;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.impl.flyweights.BeliefbaseFlyweight;
import jadex.bridge.CheckedAction;
import jadex.bridge.service.types.clock.ITimedObject;
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
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;

import jadex.commons.beans.PropertyChangeListener;
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
	protected static void updateBeliefSet(IOAVState state, Object rbeliefset, Object neworigfacts, Object scope)
	{
		Collection newfacts = null;
		
		Collection oldorigfacts = state.getAttributeValues(rbeliefset, OAVBDIRuntimeModel.beliefset_has_facts);
		Collection oldfacts = null;
	
//		System.out.println("oldorigfacts: "+oldorigfacts);
//		System.out.println("neworigfacts: "+neworigfacts);
		
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
					addBeliefSetValue(state, rbeliefset, newfact, scope);
			}
		}
		if(oldfacts!=null)
		{
			for(Iterator it=oldfacts.iterator(); it.hasNext(); )
			{
				Object oldfact = it.next();
				if(newfacts==null || !newfacts.contains(oldfact))
					removeBeliefSetValue(state, rbeliefset, oldfact, scope);
			}
		}
		
//		Collection changed = state.getAttributeValues(rbeliefset, OAVBDIRuntimeModel.beliefset_has_facts);
//		System.out.println("changed to: "+changed);
	}

	/**
	 *  Set the value of a belief.
	 *  belief_has_fact should only be modified through this method!
	 */
	public static void	setBeliefValue(IOAVState state, Object rbelief, Object fact, Object scope)
	{
		// Convert wrapped basic values to desired class (e.g. Integer to Long).
		Object	mbel	= state.getAttributeValue(rbelief, OAVBDIRuntimeModel.element_has_model);
		Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
		fact = SReflect.convertWrappedValue(fact, clazz);
		
		Object oldfact = state.getAttributeValue(rbelief, OAVBDIRuntimeModel.belief_has_fact);
		
		if(oldfact!=fact)	// Update state, even when facts are equal (state will take care of not throwing event).
		{
			state.setAttributeValue(rbelief, OAVBDIRuntimeModel.belief_has_fact, fact);
			
			// Set belief value as result
			BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
			Object magent = state.getAttributeValue(ip.getAgent(), OAVBDIRuntimeModel.element_has_model);
			Collection mbels = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_beliefs);
			
			// case 1: belief is defined in agent -> check if belief is declared as result 
			if(mbels!=null && mbels.contains(mbel))
			{
				boolean result = ((Boolean)state.getAttributeValue(mbel, OAVBDIMetaModel.belief_has_result)).booleanValue();
				if(result)
				{
					String name = (String)state.getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name);
					BDIInterpreter.getInterpreter(state).setResultValue(name, fact);
				}
			}
			// case 2: check if agent belief references map to target belief
			else
			{
				String belname = (String)state.getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name);
				Collection mbelrefs = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_beliefrefs);
				if(mbelrefs!=null)
				{
					for(Iterator it=mbelrefs.iterator(); it.hasNext(); )
					{
						Object mbelref = it.next();
						boolean result = ((Boolean)state.getAttributeValue(mbelref, OAVBDIMetaModel.beliefreference_has_result)).booleanValue();
						if(result)
						{
							String ref = (String)state.getAttributeValue(mbelref, OAVBDIMetaModel.elementreference_has_concrete);
							Object[] tmp = AgentRules.resolveCapability(ref, OAVBDIRuntimeModel.belief_type, ip.getAgent(), state);
							if(belname.equals(tmp[0]) && scope.equals(tmp[1]))
							{
								String name = (String)state.getAttributeValue(mbelref, OAVBDIMetaModel.modelelement_has_name);
								BDIInterpreter.getInterpreter(state).setResultValue(name, fact);
							}
						}
					}
				}
			}
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
	public static void	addBeliefSetValue(IOAVState state, Object rbeliefset, Object fact, Object scope)
	{
		// Convert wrapped basic values to desired class (e.g. Integer to Long).
		Object	mbelset	= state.getAttributeValue(rbeliefset, OAVBDIRuntimeModel.element_has_model);
		Class	clazz	= (Class)state.getAttributeValue(mbelset, OAVBDIMetaModel.typedelement_has_class);
		fact	= SReflect.convertWrappedValue(fact, clazz);

		state.addAttributeValue(rbeliefset, OAVBDIRuntimeModel.beliefset_has_facts, fact);
		
		storeBeliefSetResults(state, rbeliefset, scope);
//		System.out.println("Added bel val: "+rbeliefset+" "+fact);
	}
	
	/**
	 *  Store belief set values as results of component.
	 */
	public static void storeBeliefSetResults(IOAVState state, Object rbeliefset, Object scope)
	{
		// Set beliefset value as result
		Object	mbelset	= state.getAttributeValue(rbeliefset, OAVBDIRuntimeModel.element_has_model);
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		Object magent = state.getAttributeValue(ip.getAgent(), OAVBDIRuntimeModel.element_has_model);
		Collection mbelsets = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_beliefsets);
		
		// case 1: belief is defined in agent -> check if belief is declared as result 
		if(mbelsets!=null && mbelsets.contains(mbelset))
		{
			boolean result = ((Boolean)state.getAttributeValue(mbelset, OAVBDIMetaModel.beliefset_has_result)).booleanValue();
			if(result)
			{
				String name = (String)state.getAttributeValue(mbelset, OAVBDIMetaModel.modelelement_has_name);
				Collection facts = state.getAttributeValues(rbeliefset, OAVBDIRuntimeModel.beliefset_has_facts);
				BDIInterpreter.getInterpreter(state).setResultValue(name, facts);
			}
		}
		// case 2: check if agent belief references map to target belief
		else
		{
			String belname = (String)state.getAttributeValue(mbelset, OAVBDIMetaModel.modelelement_has_name);
			Collection mbelsetrefs = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_beliefsetrefs);
			if(mbelsetrefs!=null)
			{
				for(Iterator it=mbelsetrefs.iterator(); it.hasNext(); )
				{
					Object mbelref = it.next();
					boolean result = ((Boolean)state.getAttributeValue(mbelref, OAVBDIMetaModel.beliefsetreference_has_result)).booleanValue();
					if(result)
					{
						String ref = (String)state.getAttributeValue(mbelref, OAVBDIMetaModel.elementreference_has_concrete);
						Object[] tmp = AgentRules.resolveCapability(ref, OAVBDIRuntimeModel.beliefset_type, ip.getAgent(), state);
						if(belname.equals(tmp[0]) && scope.equals(tmp[1]))
						{
							String name = (String)state.getAttributeValue(mbelref, OAVBDIMetaModel.modelelement_has_name);
							Collection facts = state.getAttributeValues(rbeliefset, OAVBDIRuntimeModel.beliefset_has_facts);
							BDIInterpreter.getInterpreter(state).setResultValue(name, facts);
						}
					}
				}
			}
		}
	}

	/**
	 *  Remove a value from a belief set.
	 *  Takes care of registering / deregistering.
	 *  beliefset_has_facts should only be modified through this method!
	 */
	public static void	removeBeliefSetValue(IOAVState state, Object rbeliefset, Object fact, Object scope)
	{
		// Convert wrapped basic values to desired class (e.g. Integer to Long).
		Object	mbel	= state.getAttributeValue(rbeliefset, OAVBDIRuntimeModel.element_has_model);
		Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
		fact	= SReflect.convertWrappedValue(fact, clazz);

		state.removeAttributeValue(rbeliefset, OAVBDIRuntimeModel.beliefset_has_facts, fact);
		
		storeBeliefSetResults(state, rbeliefset, scope);
	}
	
	
	/**
	 *  Create a rule for a dynamic fact.
	 *  @param usercond	The ADF part of the target condition.
	 *  @param model The belief model element.
	 */
	public static Object[]	createDynamicBeliefUserRule(Object model, final Variable var)
	{
		Variable rbelief = new Variable("?rbelief", OAVBDIRuntimeModel.belief_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
			
		ObjectCondition	belcon	= new ObjectCondition(OAVBDIRuntimeModel.belief_type);
		belcon.addConstraint(new BoundConstraint(null, rbelief));
		belcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, model));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefs, rbelief, IOperator.CONTAINS));

		return new Object[]{new AndCondition(new ICondition[]{belcon, capcon}), new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rbelief	= assignments.getVariableValue("?rbelief");
				Object	rcapa	= assignments.getVariableValue("?rcapa");
				Object	fact	= assignments.getVariableValue(var.getName());
							
				BeliefRules.setBeliefValue(state, rbelief, fact, rcapa);

//				System.out.println("Belief "+state.getAttributeValue(state.getAttributeValue(rbelief, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name)+" "+state.getAttributeValue(rbelief, OAVBDIRuntimeModel.belief_has_fact));
			}
		}, null, var};
	}
	
	/**
	 *  Create a rule for a dynamic facts expression.
	 *  @param usercond	The ADF part of the target condition.
	 *  @param model The belief set model element.
	 */
	public static Object[]	createDynamicBeliefSetUserRule(Object model, final Variable var)
	{
		Variable rbeliefset = new Variable("?rbeliefset", OAVBDIRuntimeModel.beliefset_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
			
		ObjectCondition	belsetcon	= new ObjectCondition(OAVBDIRuntimeModel.beliefset_type);
		belsetcon.addConstraint(new BoundConstraint(null, rbeliefset));
		belsetcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, model));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefsets, rbeliefset, IOperator.CONTAINS));

		return new Object[]{
			new AndCondition(new ICondition[]{belsetcon, capcon}), new IAction()
			{
				public void execute(IOAVState state, IVariableAssignments assignments)
				{
					Object rbeliefset	= assignments.getVariableValue("?rbeliefset");
					Object rcapa = assignments.getVariableValue("?rcapa");
					Object neworigfacts	= assignments.getVariableValue(var.getName());
					
					updateBeliefSet(state, rbeliefset, neworigfacts, rcapa);
					
//					System.out.println("Beliefset "+state.getAttributeValue(state.getAttributeValue(rbeliefset, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name)+" "+state.getAttributeValues(rbeliefset, OAVBDIRuntimeModel.beliefset_has_facts));
				}
			}, null, var};
	}
	
	/**
	 *  Create a rule for an ADF condition.
	 *  @param usercond The user condition.
	 *  @param mcond The condition's model element. 
	 */
	public static Object[]	createConditionUserRule(Object mcondition)
	{
		Variable mcond = new Variable("?mcondition", OAVBDIMetaModel.condition_type);
//		Variable wa = new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition mcondcon = new ObjectCondition(OAVBDIMetaModel.condition_type);
		mcondcon.addConstraint(new BoundConstraint(null, mcond));
		mcondcon.addConstraint(new LiteralConstraint(null, mcondition));
		
//		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_conditiontypes, mcond, IOperator.CONTAINS));
		
		ObjectCondition plancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
//		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		plancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes}, mcond, IOperator.CONTAINS));

		ObjectCondition capacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(null, rcapa));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));

		return new Object[]{
			new AndCondition(new ICondition[]{mcondcon, plancon, capacon}),
			PLAN_WAIT_FOR_CONDITION};
	}
	
	/**
	 *  Action that gets executed when new facts available.
	 */
	protected static final IAction PLAN_WAIT_FOR_CONDITION = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object mcond = assignments.getVariableValue("?mcondition");
			Object rcapa = assignments.getVariableValue("?rcapa");
			Object rplan = assignments.getVariableValue("?rplan");

//			System.out.println("WFC: Setting plan to ready: "+rplan);
			
			// todo: Should be a rule triggered event with variable values
			
			EventProcessingRules.schedulePlanInstanceCandidate(state, mcond, rplan, rcapa);
//			
//			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, 
//				OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
//
//			PlanRules.cleanupPlanWait(state, rcapa, rplan, false);
//			// todo: provide activation resp. variable bindings
//			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement, null);
			
			BDIInterpreter.getInterpreter(state).getAgentAdapter().wakeup();
		}
	};
	
	/**
	 *  Create a rule for a dynamic parameter value.
	 *  @param mpe The paremeter model element.
	 *  @param usercond	The ADF part of the target condition.
	 *  @param ptname The parameter type name (e.g. "location").
	 */
	public static Object[]	createDynamicParameterUserRule(Object mpe, String ptname, final Variable var)
	{
		Variable rparam = new Variable("?rparameter", OAVBDIRuntimeModel.parameter_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.parameterelement_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	rparamcon	= new ObjectCondition(OAVBDIRuntimeModel.parameter_type);
		rparamcon.addConstraint(new BoundConstraint(null, rparam));
		rparamcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.parameter_has_name, ptname));
		
		ObjectCondition	rparamelemcon = new ObjectCondition(OAVBDIRuntimeModel.parameterelement_type);
		rparamelemcon.addConstraint(new BoundConstraint(null, rpe));
		rparamelemcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		rparamelemcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameterelement_has_parameters, rparam, IOperator.CONTAINS));

		ObjectCondition	rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		IConstraint con1 = new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rpe, IOperator.CONTAINS);
		IConstraint con2 = new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rpe, IOperator.CONTAINS);
		rcapacon.addConstraint(new OrConstraint(new IConstraint[]{con1, con2}));
		
		return new Object[]{
			new AndCondition(new ICondition[]{rparamcon, rparamelemcon, rcapacon}),
			new IAction()
			{
				public void execute(IOAVState state, IVariableAssignments assignments)
				{
					Object rparam = assignments.getVariableValue("?rparameter");
					Object value = assignments.getVariableValue(var.getName());
					
//					System.out.println("Update: "+assignments);
					
					BeliefRules.setParameterValue(state, rparam, value);
				}
			},
			IPriorityEvaluator.PRIORITY_1,
			var};
	}
	
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
	public static Object createParameter(final IOAVState state, String name, Object value, Class clazz, Object rpe, final Object mparam, final Object rcapa)
	{
		assert name!=null;
		assert clazz!=null: name;
		
		final Object rparam = state.createObject(OAVBDIRuntimeModel.parameter_type);
		state.setAttributeValue(rparam, OAVBDIRuntimeModel.parameter_has_name, name);
		state.setAttributeValue(rparam, OAVBDIRuntimeModel.parameter_has_type, clazz);
		if(value==null)
			value = SReflect.getDefaultValue(clazz);
		if(value!=null)
			BeliefRules.setParameterValue(state, rparam, value);
		if(rpe!=null)
			state.addAttributeValue(rpe, OAVBDIRuntimeModel.parameterelement_has_parameters, rparam);
		
		if(mparam!=null)
		{
			final Long update = (Long)state.getAttributeValue(mparam, OAVBDIMetaModel.typedelement_has_updaterate);
		
			if(update!=null)
			{
				final ITimedObject[]	to	= new ITimedObject[1];
				final OAVBDIFetcher fet = new OAVBDIFetcher(state, rcapa);
				
				to[0] = new InterpreterTimedObject(BDIInterpreter.getInterpreter(state), new CheckedAction()
				{
					public boolean isValid()
					{
						return state.containsObject(rparam);
					}
					
					public void run()
					{
						Object	exp = state.getAttributeValue(mparam, OAVBDIMetaModel.parameter_has_value);
						try
						{
							Object value = AgentRules.evaluateExpression(state, exp, fet);
							BeliefRules.setParameterValue(state, rparam, value);
						}
						catch(Exception e)
						{
							String name = BDIInterpreter.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getName();
							BDIInterpreter.getInterpreter(state).getLogger(rcapa).severe("Could not evaluate parameter expression: "+name
								+" "+state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_parsed));
						}
		//					// changed *.class to *.TYPE due to javaflow bug
						state.setAttributeValue(rparam, OAVBDIRuntimeModel.typedelement_has_timer, 
								BDIInterpreter.getInterpreter(state).getClockService().createTimer(update.longValue(), to[0]));
					}
					
					// Not possible because rparam is not in state any more
//					public void cleanup()
//					{
//						state.setAttributeValue(rparam, OAVBDIRuntimeModel.typedelement_has_timer, null);
//					}
				});
				
		//			// changed *.class to *.TYPE due to javaflow bug
				state.setAttributeValue(rparam, OAVBDIRuntimeModel.typedelement_has_timer, 
						BDIInterpreter.getInterpreter(state).getClockService().createTimer(update.longValue(), to[0]));
			}
		}
		
		return rparam;
	}
	
	/**
	 *  Create a parameterset.
	 */
	public static Object createParameterSet(final IOAVState state, String name, Collection values, 
		Class clazz, Object rpe, final Object mparamset, final Object rcapa)
	{
		assert name!=null;
		assert clazz!=null;
		
		final Object rparamset = state.createObject(OAVBDIRuntimeModel.parameterset_type);
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
		
		if(mparamset!=null)
		{
			final Long update = (Long)state.getAttributeValue(mparamset, OAVBDIMetaModel.typedelement_has_updaterate);
		
			if(update!=null)
			{
				final ITimedObject[]	to	= new ITimedObject[1];
				final OAVBDIFetcher fet = new OAVBDIFetcher(state, rcapa);
				
				to[0] = new InterpreterTimedObject(BDIInterpreter.getInterpreter(state), new CheckedAction()
				{
					public boolean isValid()
					{
						return state.containsObject(rparamset);
					}
					
					public void run()
					{
						Object	exp = state.getAttributeValue(mparamset, OAVBDIMetaModel.parameterset_has_valuesexpression);
						try
						{
							Object values	= AgentRules.evaluateExpression(state, exp, fet);
							BeliefRules.updateBeliefSet(state, rparamset, values, rcapa);
						}
						catch(Exception e)
						{
							String name = BDIInterpreter.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getName();
							BDIInterpreter.getInterpreter(state).getLogger(rcapa).severe("Could not evaluate parameterset expression: "+name+" "+state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_parsed));
						}
						// changed *.class to *.TYPE due to javaflow bug
						state.setAttributeValue(rparamset, OAVBDIRuntimeModel.typedelement_has_timer, 
							BDIInterpreter.getInterpreter(state).getClockService().createTimer(update.longValue(), to[0]));
					}
				});
				
				// changed *.class to *.TYPE due to javaflow bug
				state.setAttributeValue(rparamset, OAVBDIRuntimeModel.typedelement_has_timer, 
					BDIInterpreter.getInterpreter(state).getClockService().createTimer(update.longValue(), to[0]));
			}
		}
		
		return rparamset;
	}
	
	/**
	 *  Create a rule for a dynamic values expression.
	 *  @param mpe The parameter model element.
	 *  @param usercond	The ADF part of the dynamic condition.
	 *  @param ptname The parameter type name (e.g. "location").
	 */
	public static Object[]	createDynamicParameterSetUserRule(Object mpe, String ptname, final Variable var)
	{
		Variable rparam = new Variable("?rparameterset", OAVBDIRuntimeModel.parameterset_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.parameterelement_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	rparamcon	= new ObjectCondition(OAVBDIRuntimeModel.parameterset_type);
		rparamcon.addConstraint(new BoundConstraint(null, rparam));
		rparamcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.parameterset_has_name, ptname));
		
		ObjectCondition	rparamelemcon = new ObjectCondition(OAVBDIRuntimeModel.parameterelement_type);
		rparamelemcon.addConstraint(new BoundConstraint(null, rpe));
		rparamelemcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		rparamelemcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.parameterelement_has_parametersets, rparam, IOperator.CONTAINS));

		ObjectCondition	rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		IConstraint con1 = new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rpe, IOperator.CONTAINS);
		IConstraint con2 = new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rpe, IOperator.CONTAINS);
		rcapacon.addConstraint(new OrConstraint(new IConstraint[]{con1, con2}));
		
		return new Object[]{
			new AndCondition(new ICondition[]{rparamcon, rparamelemcon, rcapacon}),
			new IAction()
			{
				public void execute(IOAVState state, IVariableAssignments assignments)
				{
					Object rparamset = assignments.getVariableValue("?rparameterset");
					Object neworigvalues = assignments.getVariableValue(var.getName());
					
					updateParameterSet(state, rparamset, neworigvalues);
				}
			},
			IPriorityEvaluator.PRIORITY_1,
			var};
	}
	
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
}
