package jadex.bdi.runtime.impl;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IElement;
import jadex.bdi.runtime.IExternalCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IParameter;
import jadex.bdi.runtime.IParameterSet;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.impl.flyweights.BeliefFlyweight;
import jadex.bdi.runtime.impl.flyweights.BeliefSetFlyweight;
import jadex.bdi.runtime.impl.flyweights.ChangeEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.impl.flyweights.ExpressionFlyweight;
import jadex.bdi.runtime.impl.flyweights.ExpressionNoModel;
import jadex.bdi.runtime.impl.flyweights.GoalFlyweight;
import jadex.bdi.runtime.impl.flyweights.InternalEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.MessageEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.ParameterFlyweight;
import jadex.bdi.runtime.impl.flyweights.ParameterSetFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanFlyweight;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.BeliefRules;
import jadex.bdi.runtime.interpreter.GoalLifecycleRules;
import jadex.bdi.runtime.interpreter.InternalEventRules;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IInternalAccess;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Shared functionality of ea and normal flyweights.
 *  Helps to avoid code copying.
 */
public class SFlyweightFunctionality 
{
	//-------- beliefbase --------
	
	/**
	 * 
	 */
	public static ElementFlyweight getBelief(IOAVState state, Object handle, Object scope, String name)
	{
		ElementFlyweight ret = null;
		
		Object[] rscope = AgentRules.resolveCapability(name, OAVBDIMetaModel.belief_type, scope, state);
	
		Object mscope = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.element_has_model);
		Object mbel = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefs, rscope[0]);
		if(mbel!=null)
		{
			// Init on demand.
			if(!state.containsKey(rscope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel))
			{
				IFuture	fut	= AgentRules.initBelief(state, rscope[1], mbel, null);
				if(!fut.isDone())
					throw new RuntimeException("Future belief not available: "+rscope[0]+" in "+rscope[1]);
			}
			Object rbel = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel);	
			
			ret = BeliefFlyweight.getBeliefFlyweight(state, rscope[1], rbel);
		}
		else
		{
			throw new RuntimeException("No such belief: "+rscope[0]+" in "+rscope[1]);
		}
		
		return ret;
	}
	
	/**
	 */
	public static ElementFlyweight getBeliefSet(IOAVState state, Object handle, Object scope, String name)
	{
		ElementFlyweight ret = null;
	
		Object[] rscope = AgentRules.resolveCapability(name, OAVBDIMetaModel.beliefset_type, scope, state);
	
		Object mscope = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.element_has_model);
		Object mbelset = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, rscope[0]);
		if(mbelset!=null)
		{
			// Init on demand.
			if(!state.containsKey(rscope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbelset))
			{
				IFuture	fut	= AgentRules.initBeliefSet(state, rscope[1], mbelset, null);
				if(!fut.isDone())
				{
					throw new RuntimeException("Future beliefset not available: "+rscope[0]+" in "+rscope[1]);					
				}
			}
			
			Object rbelset = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbelset);	

			ret = BeliefSetFlyweight.getBeliefSetFlyweight(state, rscope[1], rbelset);
		}
		else
		{
			throw new RuntimeException("No such belief set: "+rscope[0]+" in "+rscope[1]);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static boolean containsBelief(IOAVState state, Object handle, Object scope, String name)
	{
		Object[] rscope = AgentRules.resolveCapability(name, OAVBDIMetaModel.belief_type, scope, state);
		Object mscope = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.element_has_model);
		Object mbel = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefs, rscope[0]);
		return mbel!=null;
	}
	
	/**
	 * 
	 */
	public static boolean containsBeliefSet(IOAVState state, Object handle, Object scope, String name)
	{
		Object[] rscope = AgentRules.resolveCapability(name, OAVBDIMetaModel.beliefset_type, scope, state);
		Object mscope = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.element_has_model);
		Object mbelset = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, rscope[0]);
		return mbelset!=null;
	}
	
	/**
	 * 
	 */
	public static String[] getBeliefNames(IOAVState state, Object handle, Object scope)
	{
		String[] ret;	
		Object mscope = state.getAttributeValue(scope, OAVBDIRuntimeModel.element_has_model);
		Collection bels = state.getAttributeValues(mscope, OAVBDIMetaModel.capability_has_beliefs);
		
		if(bels!=null)
		{
			ret = new String[bels.size()];
			int i=0;
			for(Iterator it=bels.iterator(); it.hasNext(); i++)
			{
				ret[i] = (String)state.getAttributeValue(it.next(), OAVBDIMetaModel.modelelement_has_name);
			}
		}
		else
		{
			ret = SUtil.EMPTY_STRING_ARRAY;
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public static String[] getBeliefSetNames(IOAVState state, Object handle, Object scope)
	{
		String[] ret;	
		Object mscope = state.getAttributeValue(scope, OAVBDIRuntimeModel.element_has_model);
		Collection belsets = state.getAttributeValues(mscope, OAVBDIMetaModel.capability_has_beliefsets);
		
		if(belsets!=null)
		{
			ret = new String[belsets.size()];
			int i=0;
			for(Iterator it=belsets.iterator(); it.hasNext(); i++)
			{
				ret[i] = (String)state.getAttributeValue(it.next(), OAVBDIMetaModel.modelelement_has_name);
			}
		}
		else
		{
			ret = SUtil.EMPTY_STRING_ARRAY;
		}
		return ret;
	}
	
	//-------- belief --------
	
	/**
	 * 
	 */
	public static void setFact(IOAVState state, Object handle, Object fact, Object scope)
	{
		Object	mbel	= state.getAttributeValue(handle, OAVBDIRuntimeModel.element_has_model);
		Object	evamode = state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_evaluationmode);
		if(!OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode))
		{
			throw new RuntimeException("Setting value not supported for dynamic belief: "
				+ state.getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name));
		}
		BeliefRules.setBeliefValue(state, handle, fact, scope);
	}
	
	/**
	 * 
	 * /
	public static Object getFact(IOAVState state, Object handle, Object scope)
	{
		return BeliefRules.getBeliefValue(state, handle, scope);
	}*/
	
	/**
	 * 
	 * @param state
	 * @param handle
	 * @param interpreter
	 */
	public static void modified(IOAVState state, Object handle, IInternalAccess interpreter)
	{
		Object	fact = state.getAttributeValue(handle, OAVBDIRuntimeModel.belief_has_fact);
		((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).getEventReificator().objectModified(handle, state.getType(handle), OAVBDIRuntimeModel.belief_has_fact, fact, fact);
	}
	
	/**
	 * 
	 */
	public static Class getClazz(IOAVState state, Object handle)
	{
		Object me = state.getAttributeValue(handle, OAVBDIRuntimeModel.element_has_model);
		return (Class)state.getAttributeValue(me, OAVBDIMetaModel.typedelement_has_class);
	}
	
	//-------- beliefset --------
	
	/**
	 * 
	 * @param state
	 * @param handle
	 * @param fact
	 */
	public static void removeFact(IOAVState state, Object handle, Object fact, Object scope)
	{
		Object me = state.getAttributeValue(handle, OAVBDIRuntimeModel.element_has_model);
		Object	evamode	= state.getAttributeValue(me, OAVBDIMetaModel.typedelement_has_evaluationmode);
		if(!OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode))
			throw new RuntimeException("Removing value on dynamic beliefset not allowed: "+me);
		
		BeliefRules.removeBeliefSetValue(state, handle, fact, scope);
	}
	
	/**
	 * 
	 * @param state
	 * @param handle
	 * @param facts
	 */
	public static void addFacts(IOAVState state, Object handle, Object[] facts, Object scope)
	{
		Object me = state.getAttributeValue(handle, OAVBDIRuntimeModel.element_has_model);
		Object	evamode	= state.getAttributeValue(me, OAVBDIMetaModel.typedelement_has_evaluationmode);
		if(!OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode))
			throw new RuntimeException("Adding values on dynamic beliefset not allowed: "+me);

		for(int i=0; facts!=null && i<facts.length; i++)
			BeliefRules.addBeliefSetValue(state, handle, facts[i], scope);
	}
	
	/**
	 * 
	 * @param state
	 * @param handle
	 */
	public static void removeFacts(IOAVState state, Object handle, Object scope)
	{
		Object me = state.getAttributeValue(handle, OAVBDIRuntimeModel.element_has_model);
		Object	evamode	= state.getAttributeValue(me, OAVBDIMetaModel.typedelement_has_evaluationmode);
		if(!OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode))
			throw new RuntimeException("Removing values on dynamic beliefset not allowed: "+me);
	
		Collection coll = state.getAttributeValues(handle, OAVBDIRuntimeModel.beliefset_has_facts);
		if(coll!=null)
		{
			Object[] facts = coll.toArray();
			
			for(int i=0; i<facts.length; i++)
				BeliefRules.removeBeliefSetValue(state, handle, facts[i], scope);
		}
	}
	
	/**
	 * 
	 */
	public static Object getFact(IOAVState state, Object handle, Object scope, Object oldval)
	{
		Object ret = null;
		
		// Convert wrapped basic values to desired class (e.g. Integer to Long).
		Object	mbel	= state.getAttributeValue(handle, OAVBDIRuntimeModel.element_has_model);
		Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
		Object	coldval	= SReflect.convertWrappedValue(oldval, clazz);
	
		Object newval = null;
		boolean found = false;
		Collection coll = state.getAttributeValues(handle, OAVBDIRuntimeModel.beliefset_has_facts);
		if(coll!=null)
		{
			for(Iterator it = coll.iterator(); it.hasNext() && !found; )
			{
				Object	val	= it.next();
				found = SUtil.equals(coldval, val);
				if(found)
					newval = val;
			}
			ret =  newval;
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static boolean containsFact(IOAVState state, Object handle, Object fact)
	{
		// Convert wrapped basic values to desired class (e.g. Integer to Long).
		Object	mbel	= state.getAttributeValue(handle, OAVBDIRuntimeModel.element_has_model);
		Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
		Object	newfact	= SReflect.convertWrappedValue(fact, clazz);
	
		Collection	coll	= state.getAttributeValues(handle, OAVBDIRuntimeModel.beliefset_has_facts);
		return coll!=null && coll.contains(newfact);
	}

	/**
	 * 
	 */
	public static Object[] getFacts(IOAVState state, Object handle)
	{
		Object[] ret;
		
		Object	mbelset	= state.getAttributeValue(handle, OAVBDIRuntimeModel.element_has_model);
		Collection facts	= state.getAttributeValues(handle, OAVBDIRuntimeModel.beliefset_has_facts);
		Class	clazz	= (Class)state.getAttributeValue(mbelset, OAVBDIMetaModel.typedelement_has_class);
		ret	= (Object[])Array.newInstance(SReflect.getWrappedType(clazz), facts!=null ? facts.size() : 0);
		if(facts!=null)
		{
			ret = facts.toArray(ret);
		}
		return ret;
	}
	
	/**
	 * 
	 * @param state
	 * @param handle
	 * @param newfact
	 */
	public static void updateFact(IOAVState state, Object handle, Object newfact, Object scope)
	{
		if(containsFact(state, handle, newfact))
			BeliefRules.removeBeliefSetValue(state, handle, newfact, scope);
		BeliefRules.addBeliefSetValue(state, handle, newfact, scope);
	}

	
	//-------- capability --------
	
	//-------- change event --------
	
	/**
	 * 
	 */
	public static ElementFlyweight getElement(IOAVState state, Object handle, Object scope)
	{
		ElementFlyweight ret; 
		Object elem = state.getAttributeValue(handle, OAVBDIRuntimeModel.changeevent_has_element);
		OAVObjectType type = state.getType(elem);
		
		if(type.isSubtype(OAVBDIRuntimeModel.goal_type))
		{
			ret = GoalFlyweight.getGoalFlyweight(state, scope, elem);
		}
		else if(type.isSubtype(OAVBDIRuntimeModel.messageevent_type))
		{
			ret = MessageEventFlyweight.getMessageEventFlyweight(state, scope, elem);
		}
		else if(type.isSubtype(OAVBDIRuntimeModel.internalevent_type))
		{
			ret = InternalEventFlyweight.getInternalEventFlyweight(state, scope, elem);
		}
		else if(type.isSubtype(OAVBDIRuntimeModel.belief_type))
		{
			ret = BeliefFlyweight.getBeliefFlyweight(state, scope, elem);
		}
		else if(type.isSubtype(OAVBDIRuntimeModel.beliefset_type))
		{
			ret = BeliefSetFlyweight.getBeliefSetFlyweight(state, scope, elem);
		}
		else
		{
			throw new RuntimeException("Unknown element type: "+elem);
		}

		return ret;
	}
	
	//-------- eventbase --------
	
	/**
	 * 
	 * /
	public static ElementFlyweight createReply(IOAVState state, Object handle, Object scope, ElementFlyweight event, String msgeventtype, boolean ea)
	{
		ElementFlyweight reply = ea? (ElementFlyweight)EEventbaseFlyweight.createMessageEvent(state, scope, msgeventtype)
			: (ElementFlyweight)EventbaseFlyweight.createMessageEvent(state, scope, msgeventtype);
		MessageEventRules.initializeReply(state, scope, ((ElementFlyweight)event).getHandle(), ((ElementFlyweight)reply).getHandle());
		return reply;
	}*/
	
	/**
	 *  Create an internal event of a given type but does not add to state.
	 *  @param state The state.
	 *  @param rcap The scope.
	 *  @param type The type.
	 */
	public static Object createInternalEvent(IOAVState state, Object rcap, String type)
	{
		Object ret;
		
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.internalevent_type, rcap, state);
		Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(state.containsKey(mscope, OAVBDIMetaModel.capability_has_internalevents, scope[0]))
		{
			Object	mevent = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_internalevents, scope[0]);
			Object	revent = InternalEventRules.instantiateInternalEvent(state, scope[1], mevent, null, null, null, null);
			ret	= InternalEventFlyweight.getInternalEventFlyweight(state, scope[1], revent);
		}
		else
		{
			throw new RuntimeException("No such message event: "+scope[0]+" in "+scope[1]);
		}
	
		return ret;
	}
	
	/**
	 *  Create an message event of a given type but does not add to state.
	 *  @param state The state.
	 *  @param rcap The scope.
	 *  @param type The type.
	 */
	public static Object createMessageEvent(IOAVState state, Object rcap, String type)
	{
		Object ret;
		
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.messageevent_type, rcap, state);
		Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(state.containsKey(mscope, OAVBDIMetaModel.capability_has_messageevents, scope[0]))
		{
			Object	mevent = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_messageevents, scope[0]);
			Object	revent = MessageEventRules.instantiateMessageEvent(state, scope[1], mevent, null, null, null, null);
			ret	= MessageEventFlyweight.getMessageEventFlyweight(state, scope[1], revent);
		}
		else
		{
			throw new RuntimeException("No such message event: "+scope[0]+" in "+scope[1]);
		}
	
		return ret;
	}
	
	//-------- expressionbase --------
	
	/**
	 *  Create an expression of a given type but does not add to state.
	 *  @param state The state.
	 *  @param rcapa The scope.
	 *  @param type The type.
	 *  @param rplan The plan (if created from plan).
	 */
	public static Object createExpression(IOAVState state, Object rcapa, String type)
	{
		Object mcapa = state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_expressions, type))
			throw new RuntimeException("Unknown expression: "+type);
		Object mexp = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_expressions, type);
		return ExpressionFlyweight.getExpressionFlyweight(state, rcapa, mexp);
	}
	
	/**
	 * 
	 */
	public static Object createExpression(IOAVState state, Object scope, 
		final String expression, final String[] paramnames, final Class[] paramtypes)
	{
		// Hack!!! Should be configurable.
		IExpressionParser	exp_parser	= new JavaCCExpressionParser();
//		Object mcapa = state.getAttributeValue(scope, OAVBDIRuntimeModel.element_has_model);
//		String[] imports	= OAVBDIMetaModel.getImports(state, mcapa);
		
		Map	params	= null;
		if(paramnames!=null)
		{
			params	= new HashMap();
			for(int i=0; i<paramnames.length; i++)
			{
				params.put(paramnames[i], state.getTypeModel().getJavaType(paramtypes[i]));
			}
		}
		
		IParsedExpression pex = exp_parser.parseExpression(expression, null, params, Thread.currentThread().getContextClassLoader());
		return new ExpressionNoModel(state, scope, pex);
	}
	
	//-------- expression --------

	/**
	 * 
	 *  @param names
	 *  @param values
	 *  @param state
	 *  @param scope
	 *  @param ea
	 *  @param expression
	 *  @param paramnames
	 *  @param paramtypes
	 */
	public static Object execute(IOAVState state, Object handle, Object scope, String[] names, Object[] values)
	{
		OAVBDIFetcher fetcher = new OAVBDIFetcher(state, scope);
		for(int i=0; i<names.length; i++)
			fetcher.setValue(names[i], values[i]);
		return AgentRules.evaluateExpression(state, handle, fetcher);
	}
	
	/**
	 * 
	 */
	public static Object execute(IOAVState state, IParsedExpression exp, Object scope, String[] names, Object[] values)
	{
		OAVBDIFetcher fetcher = new OAVBDIFetcher(state, scope);
		for(int i=0; i<names.length; i++)
			fetcher.setValue(names[i], values[i]);
		return exp.getValue(fetcher);
	}

	//-------- goalbase --------
	
	/**
	 * 
	 */
	public static Object[] getGoals(IOAVState state, Object scope, final String type)
	{
		Object[] rscope	= AgentRules.resolveCapability(type, OAVBDIMetaModel.goal_type, scope, state);
		
		Object mcap = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.element_has_model);
		Object mgoal = state.getAttributeValue(mcap, OAVBDIMetaModel.capability_has_goals, rscope[0]);
		if(mgoal==null)
			throw new RuntimeException("Undefined goal type '"+type+"'.");
		
		Object[] ret;
		Collection	goals = state.getAttributeValues(rscope[1], OAVBDIRuntimeModel.capability_has_goals);
		if(goals!=null)
		{
			List	matched	= new ArrayList();
			for(Iterator it=goals.iterator(); it.hasNext(); )
			{
				Object	rgoal	= it.next();
				if(mgoal.equals(state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model)))
				{
					matched.add(GoalFlyweight.getGoalFlyweight(state, rscope[1], rgoal));
				}
			}
			ret	= (IGoal[])matched.toArray(new IGoal[matched.size()]);
		}
		else
		{
			ret	= new IGoal[0];
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static Object[] getGoals(IOAVState state, Object scope)
	{
		Object[] ret;
		Collection	goals = state.getAttributeValues(scope, OAVBDIRuntimeModel.capability_has_goals);
		if(goals!=null)
		{
			List flyweights = new ArrayList();
			for(Iterator it=goals.iterator(); it.hasNext(); )
			{
				flyweights.add(GoalFlyweight.getGoalFlyweight(state, scope, it.next()));
			}
			ret	= (IGoal[])flyweights.toArray(new IGoal[flyweights.size()]);
		}
		else
		{
			ret	= new IGoal[0];
		}
		
		return ret;
	}
	
	/**
	 *  Create a goal.
	 *  @param ref	The goal name (may include capability with dot notation).
	 *  @param rcapa	The local capability.
	 *  @param state	The state.
	 */
	public static ElementFlyweight createGoal(IOAVState state, Object scope, String ref)
	{
		Object[] rscope = AgentRules.resolveCapability(ref, OAVBDIMetaModel.goal_type, scope, state);
		Object rgoal = GoalLifecycleRules.createGoal(state, rscope[1], (String)rscope[0]);
		return GoalFlyweight.getGoalFlyweight(state, rscope[1], rgoal);
	}
	
	//-------- goal --------
	
	/**
	 * 
	 */
	public static boolean isAdopted(IOAVState state, Object handle)
	{
		String st = (String)state.getAttributeValue(handle, OAVBDIRuntimeModel.goal_has_lifecyclestate);
		return OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ADOPTED.equals(st)
			|| OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE.equals(st) 
			|| OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION.equals(st) 
			|| OAVBDIRuntimeModel.GOALLIFECYCLESTATE_SUSPENDED.equals(st);
	}
	
	/**
	 * 
	 */
	public static boolean isSucceeded(IOAVState state, Object handle)
	{
		boolean	ret;
		Object	pstate	= state.getAttributeValue(handle, OAVBDIRuntimeModel.goal_has_processingstate);
		Object	mgoal	= state.getAttributeValue(handle, OAVBDIRuntimeModel.element_has_model);
		if(state.getType(mgoal).isSubtype(OAVBDIMetaModel.maintaingoal_type))
			ret	= OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE.equals(pstate);
		else
			ret	= OAVBDIRuntimeModel.GOALPROCESSINGSTATE_SUCCEEDED.equals(pstate);	
		return ret;
	}
	
	/**
	 * 
	 */
	public static boolean isFinished(IOAVState state, Object handle)
	{
		String st = (String)state.getAttributeValue(handle, OAVBDIRuntimeModel.goal_has_lifecyclestate);
		return OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED.equals(st);
	}

	//-------- internalevent --------
	
	//-------- messageevent --------
	
	//-------- parameterelement --------
	
	/**
	 * 
	 */
	public static Object[] getParameters(IOAVState state, Object scope, Object handle)
	{
		Object[] ret;
		
		Collection params = state.getAttributeValues(handle, 
			OAVBDIRuntimeModel.parameterelement_has_parameters);
		if(params!=null)
		{
			Object[] oarray = new IParameter[params.size()];
			int i=0;
			for(Iterator it=params.iterator(); it.hasNext(); i++)
			{
				Object param = it.next();
				String name = (String)state.getAttributeValue(param, OAVBDIRuntimeModel.parameter_has_name);
				oarray[i] = ParameterFlyweight.getParameterFlyweight(state, scope, param, name, handle);
			}
			ret = oarray;
		}
		else
		{
			ret = new IParameter[0];
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static Object[] getParameterSets(IOAVState state, Object scope, Object handle)
	{
		Object[] ret;

		Collection paramsets = state.getAttributeValues(handle, 
			OAVBDIRuntimeModel.parameterelement_has_parametersets);
		if(paramsets!=null)
		{
			Object[] oarray = new IParameterSet[paramsets.size()];
			int i=0;
			for(Iterator it=paramsets.iterator(); it.hasNext(); i++)
			{
				Object paramset = it.next();
				String name = (String)state.getAttributeValue(paramset, OAVBDIRuntimeModel.parameterset_has_name);
				oarray[i] = ParameterSetFlyweight.getParameterSetFlyweight(state, scope, paramset, name, handle);
			}
			return oarray;
		}
		else
		{
			ret = new IParameterSet[0];
		}
		
		return ret;
	}
	
	/**
	 *  Get the parameter.
	 *  @param name The name.
	 *  @return The param.
	 */
	public static Object getParameter(IOAVState state, Object scope, Object handle, final String name)
	{
		Object param = state.getAttributeValue(handle, 
			OAVBDIRuntimeModel.parameterelement_has_parameters, name);
		return ParameterFlyweight.getParameterFlyweight(state, scope, param, name, handle);
	}
	
	/**
	 *  Get the parameter set element.
 	 *  @param name The name.
	 *  @return The param set.
	 */
	public static Object getParameterSet(IOAVState state, Object scope, Object handle, final String name)
	{
		Object paramset = state.getAttributeValue(handle, 
			OAVBDIRuntimeModel.parameterelement_has_parametersets, name);
		return ParameterSetFlyweight.getParameterSetFlyweight(state, scope, paramset, name, handle);
	}
	
	/**
	 *  Get the type name (name of the modelelement).
	 */
	public static String getTypeName(IOAVState state, Object handle)
	{
		// Only called from synchronized code -> no agent invocation necessary 
		String ret = "unknown";
		try
		{
			if(handle!=null && state.getType(handle).isSubtype(OAVBDIRuntimeModel.element_type))
			{
				Object me = state.getAttributeValue(handle, OAVBDIRuntimeModel.element_has_model);
				if(me!=null)
					ret = (String)state.getAttributeValue(me, OAVBDIMetaModel.modelelement_has_name);
			}
		}
		catch(RuntimeException e)
		{
		}
		return ret;
	}
	
	//-------- parameter --------
	
	/**
	 * 
	 * /
	public static void setValue(IOAVState state, Object scope, Object handle, boolean ea, Object value)
	{
		if(!hasHandle())
		{
			setHandle(state.getAttributeValue(parameterelement, 
				OAVBDIRuntimeModel.parameterelement_has_parameters, name));
		}
		if(!hasHandle())
		{
			Object mparamelem = state.getAttributeValue(parameterelement, OAVBDIRuntimeModel.element_has_model);	
			Object mparam = state.getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parameters, name);
			Class clazz = resolveClazz(state, mparamelem, name);
			setHandle(BeliefRules.createParameter(state, name, null, clazz, parameterelement, mparam, getScope()));
		}
		
		String	direction 	= resolveDirection();
		if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(direction)
			|| OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(direction) && inprocess(state, parameterelement, getScope())
			|| OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(direction) && !inprocess(state, parameterelement, getScope()))
			throw new RuntimeException("Write access not allowed to parameter: "
				+direction+" "+getName());
		
		getInterpreter().startMonitorConsequences();
		BeliefRules.setParameterValue(state, handle, value);
		getInterpreter().endMonitorConsequences();

		
		
		
		if(!hasHandle())
		{
			setHandle(state.getAttributeValue(parameterelement, 
				OAVBDIRuntimeModel.parameterelement_has_parameters, name));
		}
		if(!hasHandle())
		{
			Object mparamelem = state.getAttributeValue(parameterelement, OAVBDIRuntimeModel.element_has_model);	
			Object mparam = state.getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parameters, name);
			Class clazz = resolveClazz(state, mparamelem, name);
			setHandle(BeliefRules.createParameter(state, name, null, clazz, parameterelement, mparam, getScope()));
		}
		
		String	direction 	= resolveDirection();
		if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(direction)
			|| OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(direction) && inprocess(state, parameterelement, getScope())
			|| OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(direction) && !inprocess(state, parameterelement, getScope()))
			throw new RuntimeException("Write access not allowed to parameter: "
				+direction+" "+getName());
		
		getInterpreter().startMonitorConsequences();
		BeliefRules.setParameterValue(state, handle, value);
		getInterpreter().endMonitorConsequences();
	}*/
	
	//-------- parameterset --------

	//-------- planbase --------
	
	/**
	 * 
	 */
	public static Object[] getPlans(IOAVState state, Object handle)
	{
		Object[] ret;
		Collection plans = state.getAttributeValues(handle, OAVBDIRuntimeModel.capability_has_plans);
		if(plans!=null)
		{
			ret = new IPlan[plans.size()];
			int i=0;
			for(Iterator it=plans.iterator(); it.hasNext(); i++)
			{
				ret[i] = PlanFlyweight.getPlanFlyweight(state, handle, it.next());
			}
		}
		else
		{
			ret	= new IPlan[0];
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static Object[] getPlans(IOAVState state, Object handle, String type)
	{
		List ret = null;
		Collection	plans = state.getAttributeValues(handle, OAVBDIRuntimeModel.capability_has_plans);
		if(plans!=null)
		{
			ret = new ArrayList();
			for(Iterator it=plans.iterator(); it.hasNext(); )
			{
				Object rplan = it.next();
				Object mplan = state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
				String tname = (String)state.getAttributeValue(mplan, OAVBDIMetaModel.modelelement_has_name);
				if(tname.equals(type))
				{
					ret.add(PlanFlyweight.getPlanFlyweight(state, handle, rplan));
				}
			}
		}
		
		return ret==null? new IPlan[0]: (IPlan[])ret.toArray(new IPlan[ret.size()]);
	}
	
	/**
	 * 
	 */
	public static Object checkElementType(IOAVState state, Object scope, String type, OAVAttributeType elemtype)
	{
		Object mcapa = state.getAttributeValue(scope, OAVBDIRuntimeModel.element_has_model);
		Object melem = state.getAttributeValue(mcapa, elemtype, type);
		if(melem==null)
			throw new RuntimeException("Element not found: "+type);
		return melem;
	}
	
	//-------- plan --------
	
	/**
	 * 
	 */
	public static IElement getReason(IOAVState state, Object scope, Object handle)
	{
		Object	elem = state.getAttributeValue(handle, OAVBDIRuntimeModel.plan_has_reason);
		IElement ret = null;
		if(elem!=null)
		{
			// todo: wrong scope
			ret = SFlyweightFunctionality.getFlyweight(state, scope, elem);
		}
		return ret;
	}
	
//	//-------- propertybase --------
//	
//	/**
//	 * 
//	 */
//	public static String[] getPropertyNames(IOAVState state, Object handle)
//	{
//		String[] ret = new String[0];
//		Collection coll = state.getAttributeValues(handle, OAVBDIRuntimeModel.capability_has_properties);
//		if(coll!=null)
//		{
//			ret = new String[coll.size()];
//			int i = 0;
//			for(Iterator it=coll.iterator(); it.hasNext(); )
//			{
//				Object prop = it.next();
//				ret[i++] = (String)state.getAttributeValue(prop, OAVBDIRuntimeModel.parameter_has_name);
//			}
//		}
//		return ret;
//	}
	
	//-------- waitabstraction --------
	
	/**
	 *  Add a goal type to an OAV waitabstraction.
	 */
	public static void addGoal(Object wa, String type, IOAVState state, Object rcapa)
	{
		Collection mgoals = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds);
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.goal_type, rcapa, state);
		
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_goals, scope[0]))
			throw new RuntimeException("Unknown goal: "+type);
		Object mgoal = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_goals, scope[0]);

		if(mgoals==null || !mgoals.contains(mgoal))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds, mgoal);
		
		BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(mgoal);
	}

	/**
	 *  Add a goal instance to an OAV waitabstraction.
	 */
	public static void addGoal(Object wa, ElementFlyweight goal, IOAVState state, Object rcapa)
	{
		Collection goals = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goals);
		Object rgoal = goal.getHandle();
		if(goals==null || !goals.contains(rgoal))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_goals, rgoal);

		BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(rgoal);
	}

	/**
	 *  Add a fact changed event to an OAV waitabstraction.
	 */
	public static void addFactChanged(Object wa, String type, IOAVState state, Object rcapa)
	{
		// HACK! null->{belief, beliefset}?
		Object[] scope = AgentRules.resolveCapability(type, null, rcapa, state);
		
		Object rbel;
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefs, scope[0]))
		{
			Object	mbel = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefs, scope[0]);
			rbel = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel);
		}
		else if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]))
		{
			Object	mbel = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
			rbel = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
		}
		else
			throw new RuntimeException("Unknown belief(set): "+type);

		Collection rbels = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds);
		if(rbels==null || !rbels.contains(rbel))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds, rbel);
		
		BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(rbel);
	}

	/**
	 *  Add a fact added event to an OAV waitabstraction.
	 */
	public static void addFactAdded(Object wa, String type, IOAVState state, Object rcapa)
	{
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.beliefset_type, rcapa, state);
		
		Object rbel;
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]))
		{
			Object	mbel = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
			rbel = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
		}
		else
			throw new RuntimeException("Unknown beliefset: "+type);

		Collection rbels = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds);
		if(rbels==null || !rbels.contains(rbel))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds, rbel);

		BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(rbel);
	}

	/**
	 *  Add a fact removed event to an OAV waitabstraction.
	 */
	public static void addFactRemoved(Object wa, String type, IOAVState state, Object rcapa)
	{
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.beliefset_type, rcapa, state);
		
		Object rbel;
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]))
		{
			Object	mbel = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
			rbel = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
		}
		else
			throw new RuntimeException("Unknown beliefset: "+type);

		Collection rbels = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds);
		if(rbels==null || !rbels.contains(rbel))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rbel);

		BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(rbel);
	}

	/**
	 *  Add an external condition to an OAV waitabstraction.
	 */
	public static void addExternalCondition(Object wa, IExternalCondition condition, IOAVState state, Object rcapa)
	{
		Collection conditions = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions);
		if(conditions==null || !conditions.contains(condition))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions, condition);
	}

	/**
	 *  Add an internal event type to an OAV waitabstraction.
	 */
	public static void addInternalEvent(Object wa, String type, IOAVState state, Object rcapa)
	{
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.internalevent_type, rcapa, state);
		
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_internalevents, scope[0]))
			throw new RuntimeException("Unknown internal event: "+type);
		Object mevent = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_internalevents, scope[0]);

		Collection ievents = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes);
		if(ievents==null || !ievents.contains(mevent))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes, mevent);
	}

	/**
	 *  Add a message event instance to an OAV waitabstraction.
	 */
	public static void addReply(Object wa, ElementFlyweight me, IOAVState state, Object rcapa)
	{
		// Register event also in conversation map for message routing.
		Object rmevent = me.getHandle();
		MessageEventRules.registerMessageEvent(state, rmevent, rcapa);
		
		Collection rmevents = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents);
		if(rmevents==null || !rmevents.contains(rmevent))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents, rmevent);
	}

	/**
	 *  Add a message event type to an OAV waitabstraction.
	 */
	public static void addMessageEvent(Object wa, String type, IOAVState state, Object rcapa)
	{
		Collection mevents = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes);
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.messageevent_type, rcapa, state);
		
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_messageevents, scope[0]))
			throw new RuntimeException("Unknown message event: "+type);
		Object mevent = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_messageevents, scope[0]);

		if(mevents==null || !mevents.contains(mevent))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes, mevent);
	}

	/**
	 *  Add a condition type to an OAV waitabstraction.
	 */
	public static void addCondition(Object wa, String condition, IOAVState state, Object rcapa)
	{
		Collection mconds = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes);
		Object[] scope = AgentRules.resolveCapability(condition, OAVBDIMetaModel.condition_type, rcapa, state);
		
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_conditions, scope[0]))
			throw new RuntimeException("Unknown condition: "+condition);
		Object mcond = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_conditions, scope[0]);

		if(mconds==null || !mconds.contains(mcond))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes, mcond);
	}
	
	/**
	 * 
	 */
	public static void removeMessageEvent(IOAVState state, Object scope, String type, Object wa)
	{
		if(wa!=null)
		{
			Collection mevents = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes);
			Object[] rscope = AgentRules.resolveCapability(type, OAVBDIMetaModel.messageevent_type, scope, state);
			
			Object mcapa = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.element_has_model);
			if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_messageevents, rscope[0]))
				throw new RuntimeException("Unknown message event: "+type);
			Object mevent = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_messageevents, rscope[0]);
	
			if(mevents!=null && mevents.contains(mevent))
				state.removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes, mevent);
		}
	}
	
	/**
	 * 
	 */
	public static void removeReply(IOAVState state, Object scope, ElementFlyweight me, Object wa)
	{
		// Register event also in conversation map for message routing.
		Object rmevent = me.getHandle();
		MessageEventRules.deregisterMessageEvent(state, rmevent, scope);
		
		if(wa!=null)
		{
			Collection rmevents = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents);
			if(rmevents!=null && rmevents.contains(rmevent))
				state.removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents, rmevent);
		}
	}
	
	/**
	 *  Remove an internal event.
	 *  @param type The type.
	 */
	public static void removeInternalEvent(IOAVState state, Object scope, String type, Object wa)
	{
		if(wa!=null)
		{
			Collection mevents = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes);
			Object[] rscope = AgentRules.resolveCapability(type, OAVBDIMetaModel.internalevent_type, scope, state);
			
			Object mcapa = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.element_has_model);
			if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_internalevents, rscope[0]))
				throw new RuntimeException("Unknown internal event: "+type);
			Object mevent = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_internalevents, rscope[0]);

			if(mevents!=null && mevents.contains(mevent))
				state.removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes, mevent);
		}
	}

	/**
	 *  Remove a goal.
	 *  @param type The type.
	 */
	public static void removeGoal(IOAVState state, Object scope, String type, Object wa)
	{
		if(wa!=null)
		{
			Collection mgoals = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds);
			Object[] rscope = AgentRules.resolveCapability(type, OAVBDIMetaModel.goal_type, scope, state);
			
			Object mcapa = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.element_has_model);
			if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_goals, rscope[0]))
				throw new RuntimeException("Unknown goal: "+type);
			Object mgoal = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_goals, rscope[0]);

			if(mgoals!=null && mgoals.contains(mgoal))
				state.removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds, mgoal);

			BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(mgoal);
		}
	}

	/**
	 *  Remove a goal.
	 *  @param goal The goal.
	 */
	public static void removeGoal(IOAVState state, ElementFlyweight goal, Object wa)
	{
		if(wa!=null)
		{
			Object rgoal = goal.getHandle();
			Collection goals = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goals);
			if(goals!=null && goals.contains(rgoal))
				state.removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_goals, rgoal);

			BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(rgoal);
		}
	}
	
	/**
	 *  Remove a fact changed.
	 *  @param belief The belief or beliefset.
	 */
	public static void removeFactChanged(IOAVState state, Object scope, String belief, Object wa)
	{		
		if(wa!=null)
		{
			Object[] rscope = AgentRules.resolveCapability(belief, OAVBDIMetaModel.beliefset_type, scope, state);
			
			Object rbel;
			Object mcapa = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.element_has_model);
			if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefs, rscope[0]))
			{
				Object	mbel = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefs, rscope[0]);
				rbel = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel);
			}
			else if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, rscope[0]))
			{
				Object	mbel = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, rscope[0]);
				rbel = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
			}
			else
				throw new RuntimeException("Unknown belief(set): "+belief);

			state.removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds, rbel);
			BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(rbel);
		}
	}
	
	/**
	 *  Remove a fact added.
	 *  @param beliefset The beliefset.
	 */
	public static void removeFactAdded(IOAVState state, Object scope, String beliefset, Object wa)
	{
		if(wa!=null)
		{
			Object[] rscope = AgentRules.resolveCapability(beliefset, OAVBDIMetaModel.beliefset_type, scope, state);
			
			Object rbel;
			Object mcapa = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.element_has_model);
			if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, rscope[0]))
			{
				Object	mbel = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, rscope[0]);
				rbel = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
			}
			else
				throw new RuntimeException("Unknown beliefset: "+beliefset);

			state.removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds, rbel);
			BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(rbel);
		}
	}


	/**
	 *  Remove a fact removed.
	 *  @param beliefset The beliefset.
	 */
	public static void removeFactRemoved(IOAVState state, Object scope, String beliefset, Object wa)
	{
		if(wa!=null)
		{
			Object[] rscope = AgentRules.resolveCapability(beliefset, OAVBDIMetaModel.beliefset_type, scope, state);
			
			Object rbel;
			Object mcapa = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.element_has_model);
			if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, rscope[0]))
			{
				Object	mbel = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, rscope[0]);
				rbel = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
			}
			else
				throw new RuntimeException("Unknown beliefset: "+beliefset);

			state.removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rbel);
			BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(rbel);
		}
	}
	
	/**
	 *  Remove a condition.
	 *  @param type The condition.
	 */
	public static void removeCondition(IOAVState state, Object scope, String type, Object wa)
	{
		if(wa!=null)
		{
			Collection mconds = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes);
			Object[] rscope = AgentRules.resolveCapability(type, OAVBDIMetaModel.condition_type, scope, state);
			
			Object mcapa = state.getAttributeValue(rscope[1], OAVBDIRuntimeModel.element_has_model);
			if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_conditions, rscope[0]))
				throw new RuntimeException("Unknown condition: "+type);
			Object mcond = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_conditions, rscope[0]);

			if(mconds!=null && mconds.contains(mcond))
				state.removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes, mconds);
		}
	}
	
	/**
	 *  Remove an external condition.
	 *  @param condition The condition.
	 */
	public static void removeExternalCondition(IOAVState state, IExternalCondition condition, Object wa)
	{
		if(wa!=null)
		{
			Collection conditions = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions);
			if(conditions!=null && conditions.contains(condition))
				state.removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions, condition);
		}
	}
	
	//-------- waitqueue --------
	
	/**
	 *  Get all elements.
	 *  @return The elements.
	 */
	public static Object[] getElements(IOAVState state, Object scope, Object rplan)
	{
		Object[] ret;
		Collection coll = state.getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements);
		if(coll!=null)
		{
			ret = new Object[coll.size()];
			int i=0;
			for(Iterator it=coll.iterator(); it.hasNext(); i++)
			{
				// todo: wrong scope!
				ret[i] = getFlyweight(state, scope, it.next());
			}
		}
		else
		{
			ret = new Object[0];
		}
		return ret;
	}

	/**
	 *  Get the next element.
	 *  @return The next element (or null if none).
	 */
	public static Object removeNextElement(IOAVState state, Object scope, Object rplan)
	{
		Object ret = null;
		Collection coll = state.getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements);
		if(coll!=null && coll.iterator().hasNext())
		{
			Object pe = coll.iterator().next();
			state.removeAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, pe);
			// todo: wrong scope!
			ret = getFlyweight(state, scope, pe);
		}
		return ret;
	}

	/**
	 *  Add a Goal. Overrides method for checking if rgoal is already finished.
	 *  @param goal The goal.
	 * /
	public static IWaitAbstraction addGoal(IOAVState state, Object scope, IGoal goal, Object rplan)
	{
		Object rgoal  = ((ElementFlyweight)goal).getHandle();
		// Directly add rgoal to waitqueue if already finished.
		if(goal.isFinished())
		{
			state.addAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, rgoal);
		}
		else
		{
			FlyweightFunctionality.addGoal(getOrCreateWaitAbstraction(), goal, state, scope);
		}
		return this;
	}*/
	
	/**
	 *  Get flyweight for element.
	 *  @param elem The element.
	 *  @return The flyweight.
	 */
	public static ElementFlyweight getFlyweight(IOAVState state, Object rcapa, Object elem)
	{
		ElementFlyweight ret = null;
		OAVObjectType type = state.getType(elem);
		
		if(type.equals(OAVBDIRuntimeModel.goal_type))
		{
			ret = GoalFlyweight.getGoalFlyweight(state, rcapa, elem);
		}
		else if(type.equals(OAVBDIRuntimeModel.internalevent_type))
		{
			ret = InternalEventFlyweight.getInternalEventFlyweight(state, rcapa, elem);
		}
		else if(type.equals(OAVBDIRuntimeModel.messageevent_type))
		{
			ret = MessageEventFlyweight.getMessageEventFlyweight(state, rcapa, elem);
		}
		else if(type.isSubtype(OAVBDIRuntimeModel.changeevent_type))
		{
			String cetype = (String)state.getAttributeValue(elem, OAVBDIRuntimeModel.changeevent_has_type);
			if(OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(cetype))
			{
				ret = GoalFlyweight.getGoalFlyweight(state, rcapa, state.getAttributeValue(elem, OAVBDIRuntimeModel.changeevent_has_element));
			}
			else
			{
				ret = new ChangeEventFlyweight(state, rcapa, elem);
			}
		}
		
		return ret;
	}
}
