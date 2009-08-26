package jadex.bdi.runtime;

import jadex.bdi.interpreter.AgentRules;
import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.GoalLifecycleRules;
import jadex.bdi.interpreter.InternalEventRules;
import jadex.bdi.interpreter.MessageEventRules;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.impl.BeliefbaseFlyweight;
import jadex.bdi.runtime.impl.CapabilityFlyweight;
import jadex.bdi.runtime.impl.EventbaseFlyweight;
import jadex.bdi.runtime.impl.ExpressionFlyweight;
import jadex.bdi.runtime.impl.ExpressionNoModel;
import jadex.bdi.runtime.impl.ExpressionbaseFlyweight;
import jadex.bdi.runtime.impl.ExternalAccessFlyweight;
import jadex.bdi.runtime.impl.GoalFlyweight;
import jadex.bdi.runtime.impl.GoalbaseFlyweight;
import jadex.bdi.runtime.impl.InternalEventFlyweight;
import jadex.bdi.runtime.impl.MessageEventFlyweight;
import jadex.bdi.runtime.impl.ParameterFlyweight;
import jadex.bdi.runtime.impl.ParameterSetFlyweight;
import jadex.bdi.runtime.impl.PlanFlyweight;
import jadex.bdi.runtime.impl.PlanbaseFlyweight;
import jadex.bdi.runtime.impl.PropertybaseFlyweight;
import jadex.bdi.runtime.impl.WaitqueueFlyweight;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.SReflect;
import jadex.commons.collection.SCollection;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.state.IOAVState;
import jadex.service.clock.IClockService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  The abstract plan is the abstract superclass
 *  for standard plans and mobile plans.
 */
public abstract class AbstractPlan implements java.io.Serializable, IPlan
{
	//-------- attributes --------

	/** The bdi interpreter. */
	protected BDIInterpreter interpreter;
	
	/** The runtime plan element. */
	private Object rplan;
	
	/** The runtime capability. */
	private Object rcapa ;
	
	/** The state. */
	private IOAVState state;
	
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public AbstractPlan()
	{
		String myadr	= ""+Thread.currentThread()+
			"_"+Thread.currentThread().hashCode();
//		System.out.println("init: "+planinit+" "+getClass().hashCode());
		
		this.interpreter = (BDIInterpreter)((Object[])planinit.get(myadr))[0];
		this.rplan	= ((Object[])planinit.get(myadr))[1];
		this.rcapa	= ((Object[])planinit.get(myadr))[2];
		
		this.state = interpreter.getState();
		
		if(interpreter==null || rplan==null || rcapa==null)
			throw new RuntimeException("Plan could not be inited: "+myadr+" - "+interpreter+" "+rplan);
	}

	/**
	 *  Remove the external usage preventing
	 *  the state object from being garbage
	 *  collected.
	 * /
	protected void finalize() throws Throwable
	{
		System.out.println("Finalized: "+this+" "+rplan);
	}*/
	
	//-------- IPlan interface --------
	
	/**
	 *  Get the lifecycle state of the plan (e.g. body or aborted).
	 *  @return The lifecycle state.
	 */
	public String	getLifecycleState()
	{
		IPlan plan = PlanFlyweight.getPlanFlyweight(getState(), getRCapability(), getRPlan());
		return plan.getLifecycleState();		
	}

	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public IWaitqueue getWaitqueue()
	{
		return WaitqueueFlyweight.getWaitqueueFlyweight(getState(), getRCapability(), getRPlan());
	}

	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public Object getBody()
	{
		return this;
	}
	
	/**
	 *  Get the reason (i.e. initial event).
	 *  @return The reason.
	 */
	public IElement getReason()
	{
		IPlan plan = PlanFlyweight.getPlanFlyweight(getState(), getRCapability(), getRPlan());
		return plan.getReason();		
	}
	
	/**
	 *  Add a goal listener.
	 *  @param listener The goal listener.
	 */
	public void addPlanListener(IPlanListener listener)
	{
		IPlan plan = PlanFlyweight.getPlanFlyweight(getState(), getRCapability(), getRPlan());
		plan.addPlanListener(listener);		
	}
	
	/**
	 *  Remove a goal listener.
	 *  @param listener The goal listener.
	 */
	public void removePlanListener(IPlanListener listener)
	{
		IPlan plan = PlanFlyweight.getPlanFlyweight(getState(), getRCapability(), getRPlan());
		plan.removePlanListener(listener);		
	}
	
	/**
	 *  Get the element type (i.e. the name declared in the ADF).
	 *  @return The element type.
	 */
	public String	getType()
	{
		IPlan plan = PlanFlyweight.getPlanFlyweight(getState(), getRCapability(), getRPlan());
		return plan.getType();		
	}

	//-------- methods --------

	/**
	 *  Let a plan fail.
	 */
	public void fail()
	{
		throw new PlanFailureException();
	}

	/**
	 *  Let a plan fail.
	 *  @param cause The cause.
	 */
	public void fail(Throwable cause)
	{
		throw new PlanFailureException(null, cause);
	}

	/**
	 *  Let a plan fail.
	 *  @param message The message.
	 *  @param cause The cause.
	 */
	public void fail(String message, Throwable cause)
	{
		throw new PlanFailureException(message, cause);
	}

	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public ICapability getScope()
	{
		return new CapabilityFlyweight(state, rcapa);
	}
	
	/**
	 *  Get the dispatched element, i.e. the element that caused
	 *  the current plan step being executed.
	 *  @return The dispatched element.
	 */
	public IElement getDispatchedElement()
	{
		Object elem = state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement);
		return getFlyweight(elem);
	}
	
	/**
	 *  Get flyweight for an element.
	 *  @param elem The element.
	 *  @return The flyweight.
	 */
	protected IElement getFlyweight(Object elem)
	{
		IElement ret = null;
		
		if(elem!=null)
		{
			// todo: wrong scope
			ret = WaitqueueFlyweight.getFlyweight(state, rcapa, elem);
		}
		
		return ret;
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return AgentRules.getLogger(state, rcapa);
	}

	
	/**
	 *  Start an atomic transaction.
	 *  All possible side-effects (i.e. triggered conditions)
	 *  of internal changes (e.g. belief changes)
	 *  will be delayed and evaluated after endAtomic() has been called.
	 *  @see #endAtomic()
	 */
	public void	startAtomic()
	{
		interpreter.startMonitorConsequences();
		interpreter.startAtomic();
	}

	/**
	 *  End an atomic transaction.
	 *  Side-effects (i.e. triggered conditions)
	 *  of all internal changes (e.g. belief changes)
	 *  performed after the last call to startAtomic()
	 *  will now be evaluated and performed.
	 *  @see #startAtomic()
	 */
	public void	endAtomic()
	{
		interpreter.endAtomic();
		interpreter.endMonitorConsequences();
	}

	/**
	 *  Dispatch a new subgoal.
	 *  @param subgoal The new subgoal.
	 *  @return The eventfilter for identifying the result event.
	 *  Note: plan step is interrupted after call.
	 * /
	public IFilter dispatchSubgoal(IGoal subgoal)
	{
		rplan.getScope().getAgent().startMonitorConsequences();

		try
		{
			IRGoal original = (IRGoal)((GoalWrapper)subgoal).unwrap(); // unwrap!!!
			return rplan.getScope().getGoalbase().dispatchSubgoal(rplan.getRootGoal(), original);
		}
		catch(GoalFailureException gfe)
		{
			gfe.setGoal(subgoal);
			throw gfe;
		}
		finally
		{
			// Interrupts the plan step, if necessary.
			rplan.getScope().getAgent().endMonitorConsequences();
		}
	}*/
	
	/**
	 *  Dispatch a new subgoal.
	 *  @param subgoal The new subgoal.
	 *  @return The eventfilter for identifying the result event.
	 *  Note: plan step is interrupted after call.
	 */
	public void dispatchSubgoal(IGoal subgoal)
	{
		Object rgoal = ((GoalFlyweight)subgoal).getHandle();
		Object scope = ((GoalFlyweight)subgoal).getScope();
		interpreter.startMonitorConsequences();
		GoalLifecycleRules.adoptGoal(state, scope, rgoal);
		state.addAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_subgoals, rgoal);
		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_parentplan, rplan);

		// Protect goal, if necessary.
		Object	planstate	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate);
		Object	reason	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_reason);
		boolean	protectgoal	= OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED.equals(planstate)
			|| OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED.equals(planstate)
			|| OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED.equals(planstate);
		if(!protectgoal && reason!=null && state.getType(reason).isSubtype(OAVBDIRuntimeModel.goal_type))
		{
			 protectgoal	= ((Boolean)state.getAttributeValue(reason, OAVBDIRuntimeModel.goal_has_protected)).booleanValue();
		}
		if(protectgoal)
		{
			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_protected, Boolean.TRUE);
		}
	
		interpreter.endMonitorConsequences();
	}

	/**
	 *  Get the name.
	 *  @return The name of the plan.
	 * /
	public String getName()
	{
		Object	mplan	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
		String	mname	= (String)state.getAttributeValue(mplan, OAVBDIMetaModel.modelelement_has_name);
		return mname+"_"+rplan;
	}*/

	/**
	 *  todo: remove
	 *  Get the plans root goal.
	 *  @return The goal.
	 * /
	public IProcessGoal getRootGoal()
	{
		if(rootgoal==null)
			rootgoal = new ProcessGoalWrapper(rplan.getRootGoal());
		return rootgoal;
	}*/
	
	/**
	 *  Add some code to the agent's agenda,
	 *  that will be executed on the agent's thread.
	 *  This method can safely be called from any thread
	 *  (e.g. AWT event handlers).
	 *  todo: remove
	 * /
	public void	invokeLater(Runnable code)
	{
		rplan.getScope().getAgent().invokeLater(code);
	}*/

	/**
	 *  Add some code to the agent's agenda,
	 *  and wait until it has been executed on the agent's thread.
	 *  This method can safely be called from any thread
	 *  (e.g. AWT event handlers).
	 *  todo: remove
	 * /
	public void	invokeAndWait(Runnable code)
	{
		rplan.getScope().getAgent().invokeAndWait(code);
	}*/


	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(SReflect.getInnerClassName(this.getClass()));
//		buf.append("(name=");
//		buf.append(getName());
//		buf.append(")");
		return buf.toString();
	}

	/**
	 *  Get the agent name.
	 *  @return The agent name.
	 */
	public String getAgentName()
	{
		return getAgentIdentifier().getLocalName();
	}
	
	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IAgentIdentifier	getAgentIdentifier()
	{
		return interpreter.getAgentAdapter().getAgentIdentifier();
	}

	/**
	 *  Check if the corresponding plan was aborted because the
	 *  proprietary goal succeeded during the plan was running.
	 *  @return True, if the goal was aborted on success of the proprietary goal.
	 * /
	public boolean isAbortedOnSuccess()
	{
		return getRootGoal().isAbortedOnSuccess();
	}*/

	/**
	 *  Get the uncatched exception that occurred in the body (if any).
	 *  Method should only be called when in failed() method.
	 *  @return The exception.
	 */
	public Exception getException()
	{
		return (Exception)getState().getAttributeValue(getRPlan(), OAVBDIRuntimeModel.plan_has_exception);
	} 

	/**
	 *  Kill this agent.
	 */
	public void killAgent()
	{
//		capability.killAgent();
		// Problem: duplicate functionality here and in capability flyweight :-(
//		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, 
//			OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATING);
		interpreter.startMonitorConsequences();
		getInterpreter().getAgentAdapter().killAgent();
		interpreter.endMonitorConsequences();
	}

	//-------- capability shortcut methods --------
	
	/**
	 *  Get the belief base.
	 *  @return The belief base.
	 */
	public IBeliefbase getBeliefbase()
	{
		return BeliefbaseFlyweight.getBeliefbaseFlyweight(state, rcapa);
	}

	/**
	 *  Get the goal base.
	 *  @return The goal base.
	 */
	public IGoalbase getGoalbase()
	{
		return GoalbaseFlyweight.getGoalbaseFlyweight(state, rcapa);
	}

	/**
	 *  Get the plan base.
	 *  @return The plan base.
	 */
	public IPlanbase getPlanbase()
	{
		return PlanbaseFlyweight.getPlanbaseFlyweight(state, rcapa);
	}

	/**
	 *  Get the event base.
	 *  @return The event base.
	 */
	public IEventbase getEventbase()
	{
		return EventbaseFlyweight.getEventbaseFlyweight(state, rcapa);
	}

	/**
	 * Get the expression base.
	 * @return The expression base.
	 */
	public IExpressionbase getExpressionbase()
	{
		return ExpressionbaseFlyweight.getExpressionbaseFlyweight(state, rcapa);
	}
	
	/**
	 *  Get the property base.
	 *  @return The property base.
	 */
	public IPropertybase getPropertybase()
	{
		return PropertybaseFlyweight.getPropertybaseFlyweight(state, rcapa);
	}

	/**
	 *  Get the clock.
	 *  @return The clock.
	 */
	public IClockService getClock()
	{
		return (IClockService)interpreter.getAgentAdapter().getPlatform().getService(IClockService.class);
	}

	/**
	 *  Get the current time.
	 *  The time unit depends on the currently running clock implementation.
	 *  For the default system clock, the time value adheres to the time
	 *  representation as used by {@link System#currentTimeMillis()}, i.e.,
	 *  the value of milliseconds passed since 0:00 'o clock, January 1st, 1970, UTC.
	 *  For custom simulation clocks, arbitrary representations can be used.
	 *  @return The current time.
	 */
	public long getTime()
	{
		return getClock().getTime();
	}

	//-------- goalbase shortcut methods --------
	
	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 *  Note: plan step is interrupted after call.
	 */
	public void dispatchTopLevelGoal(IGoal goal)
	{
		Object rgoal = ((GoalFlyweight)goal).getHandle();
		interpreter.startMonitorConsequences();
		GoalLifecycleRules.adoptGoal(state, rcapa, rgoal);
		interpreter.endMonitorConsequences();
	}

	/**
	 *  Create a goal from a template goal.
	 *  To be processed, the goal has to be dispatched as subgoal
	 *  or adopted as top-level goal.
	 *  @param type	The template goal name as specified in the ADF.
	 *  @return The created goal.
	 */
	public IGoal createGoal(String type)
	{
		return GoalbaseFlyweight.createGoal(type, rcapa, state);
	}

	//-------- eventbase shortcut methods --------
	
	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public void	sendMessage(IMessageEvent me)
	{	
		Object revent = ((MessageEventFlyweight)me).getHandle();
		Object rcapa = ((MessageEventFlyweight)me).getScope();
		interpreter.startMonitorConsequences();
		MessageEventRules.sendMessage(state, rcapa, revent);
		interpreter.endMonitorConsequences();
	}

	/**
	 *  Dispatch an internal event.
	 *  @param event The event.
	 *  Note: plan step is interrupted after call.
	 */
	public void dispatchInternalEvent(IInternalEvent event)
	{
		Object revent = ((InternalEventFlyweight)event).getHandle();
		Object rcapa = ((InternalEventFlyweight)event).getScope();
		interpreter.startMonitorConsequences();
		InternalEventRules.adoptInternalEvent(state, rcapa, revent);
		interpreter.endMonitorConsequences();
	}

	/**
	 *  Create a new message event.
	 *  @return The new message event.
	 */
	public IMessageEvent createMessageEvent(String type)
	{
		return EventbaseFlyweight.createMessageEvent(state, rcapa, type);
	}

	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 */
	public IInternalEvent createInternalEvent(String type)
	{
		return EventbaseFlyweight.createInternalEvent(state, rcapa, type);
	}

	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 *  @deprecated Convenience method for easy conversion to new explicit internal events.
	 *  Will be removed in later releases.
	 * /
	public IInternalEvent createInternalEvent(String type, Object content)
	{
		return capability.getEventbase().createInternalEvent(type, content);
	}*/

	//-------- gui methods --------

	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public IExternalAccess getExternalAccess()
	{
		return new ExternalAccessFlyweight(state, rcapa);
	}

	//-------- expressionbase shortcut methods --------
	// Hack!!! Not really shortcuts, because expressions/conditions are remembered for cleanup.
	
	/**
	 *  Get a query created from a predefined expression.
	 *  @param name	The name of an expression defined in the ADF.
	 *  @return The query object.
	 *  @deprecated	Use @link{#getExpression(String)} instead.
	 * /
	public IExpression	getQuery(String name)
	{
		return	getExpression(name);
	}*/

	/**
	 *  Get an instance of a predefined expression.
	 *  @param name	The name of an expression defined in the ADF.
	 *  @return The expression instance.
	 */
	public IExpression	getExpression(String name)
	{
		Object mcapa = state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
		Object mexp = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_expressions, name);
		if(mexp==null)
			throw new RuntimeException("Unknown expression: "+name);
		return ExpressionFlyweight.getExpressionFlyweight(state, rcapa, mexp);
	}

	/**
	 *  Get a condition predefined in the ADF.
	 *  Note that a new condition instance is returned each time this method is called.
	 *  @param name	The name of a condition defined in the ADF.
	 *  @return The condition object.
	 * /
	public ICondition	getCondition(String name)
	{
		return capability.getExpressionbase().getCondition(name);
	}*/

	/**
	 *  Create a precompiled query.
	 *  @param query	The query string.
	 *  @return The precompiled query.
	 *  @deprecated	Use @link{#createExpression(String)} instead.
	 * /
	public IExpression	createQuery(String query)
	{
		return createExpression(query);
	}*/

	/**
	 *  Create a precompiled expression.
	 *  @param expression	The expression string.
	 *  @return The precompiled expression.
	 */
	public IExpression	createExpression(String expression)
	{
		return createExpression(expression, null, null);
	}

	/**
	 *  Create a precompiled expression.
	 *  @param expression	The expression string.
	 *  @return The precompiled expression.
	 */
	public IExpression	createExpression(String expression, String[] paramnames, Class[] paramtypes)
	{
		// Hack!!! Should be configurable.
		IExpressionParser	exp_parser	= new JavaCCExpressionParser();
		Object mcapa = state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
		String[]	imports	= OAVBDIMetaModel.getImports(state, mcapa);
		
		Map	params	= null;
		if(paramnames!=null)
		{
			params	= new HashMap();
			for(int i=0; i<paramnames.length; i++)
			{
				params.put(paramnames[i], state.getTypeModel().getJavaType(paramtypes[i]));
			}
		}
		
		IParsedExpression pex = exp_parser.parseExpression(expression, imports, params, Thread.currentThread().getContextClassLoader());
		return new ExpressionNoModel(state, rcapa, pex);
	}

	/**
	 *  Create a condition, that is triggered whenever the expression
	 *  value changes to true.
	 *  @param expression	The condition expression.
	 *  @return The condition.
	 * /
	public ICondition	createCondition(String expression)
	{
		return createCondition(expression, ICondition.TRIGGER_CHANGES_TO_TRUE, null, null);
	}*/

	/**
	 *  Create a condition.
	 *  @param expression	The condition expression.
	 *  @param trigger	The condition trigger.
	 *  @return The condition.
	 * /
	public ICondition	createCondition(String expression, String trigger, String[] paramnames, Class[] paramtypes)
	{
		return capability.getExpressionbase().createCondition(expression, trigger, paramnames, paramtypes);
	}*/

	//-------- parameter handling --------

	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 */
	public IParameter[]	getParameters()
	{
		IParameter[] ret;
		
		Collection params = getState().getAttributeValues(getRPlan(), 
			OAVBDIRuntimeModel.parameterelement_has_parameters);
		if(params!=null)
		{
			ret = new IParameter[params.size()];
			int i=0;
			for(Iterator it=params.iterator(); it.hasNext(); i++)
			{
				Object param = it.next();
				String name = (String)getState().getAttributeValue(param, OAVBDIMetaModel.modelelement_has_name);
				ret[i] = ParameterFlyweight.getParameterFlyweight(getState(), getScope(), param, name, getRPlan());
			}
		}
		else
		{
			ret = new IParameter[0];
		}
		return ret;
	}

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 */
	public IParameterSet[]	getParameterSets()
	{
		IParameterSet[] ret;
		
		Collection paramsets = getState().getAttributeValues(getRPlan(), 
			OAVBDIRuntimeModel.parameterelement_has_parametersets);
		if(paramsets!=null)
		{
			ret = new IParameterSet[paramsets.size()];
			int i=0;
			for(Iterator it=paramsets.iterator(); it.hasNext(); i++)
			{
				Object paramset = it.next();
				String name = (String)getState().getAttributeValue(paramset, OAVBDIMetaModel.modelelement_has_name);
				ret[i] = ParameterSetFlyweight.getParameterSetFlyweight(getState(), getScope(), paramset, name, getRPlan());
			}
		}
		else
		{
			ret = new IParameterSet[0];
		}
		return ret;
	}

	/**
	 *  Get a parameter.
	 *  @param name The name.
	 *  @return The parameter.
	 */
	public IParameter getParameter(String name)
	{
		return ParameterFlyweight.getParameterFlyweight(state, rcapa, null, name, rplan);
	}

	/**
	 *  Get a parameter.
	 *  @param name The name.
	 *  @return The parameter set.
	 */
	public IParameterSet getParameterSet(String name)
	{
		return ParameterSetFlyweight.getParameterSetFlyweight(state, rcapa, null, name, rplan);
	}

	/**
	 *  Has the element a parameter element.
	 *  @param name The name.
	 *  @return True, if it has the parameter.
	 */
	public boolean hasParameter(String name)
	{
		boolean ret = state.containsKey(rplan, OAVBDIRuntimeModel.parameterelement_has_parameters, name);
		if(!ret)
		{
			Object mplan = state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
			ret = state.containsKey(mplan, OAVBDIMetaModel.parameterelement_has_parameters, name);
		}
		return ret;
	}

	/**
	 *  Has the element a parameter set element.
	 *  @param name The name.
	 *  @return True, if it has the parameter set.
	 */
	public boolean hasParameterSet(String name)
	{
		boolean ret = state.containsKey(rplan, OAVBDIRuntimeModel.parameterelement_has_parametersets, name);
		if(!ret)
		{
			Object mplan = state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
			ret = state.containsKey(mplan, OAVBDIMetaModel.parameterelement_has_parametersets, name);
		}
		return ret;
	}

	//-------- internal methods --------

	/**
	 *  This method is called after the plan has been terminated.
	 *  It can be overriden to perform any custom cleanup code
	 *  but this implementation should be called also, because
	 *  it performs cleanup concerning expressions and conditions.
	 * /
	// Replaced by passed(), failed(), aborted().
	protected void cleanup()
	{
		// Cleanup expressions / conditions.
		/*for(int i=0; i<expressions.size(); i++)
		{
			// Resolve references to cleanup original expression.
			IRElement	exp	= (IRElement)expressions.get(i);
			while(exp instanceof RElementReference)
				exp	= ((RElementReference)exp).getReferencedElement();

			exp.cleanup();
		}* /
	}*/

	/**
	 *  Get the state.
	 *  @return The state.
	 */
	public IOAVState getState()
	{
		return state;
	}
	
	/**
	 *  Get the state.
	 *  @return The state.
	 */
	// todo: make package access
	public BDIInterpreter getInterpreter()
	{
		return interpreter;
	}
	
	/**
	 *  Get the plan instance info.
	 *  @return The plan instance info.
	 */
	// todo: make package access
	public Object getRPlan()
	{
		return rplan;
	}

	/**
	 *  Get the capability.
	 *  @return The capability.
	 */
	protected Object getRCapability()
	{
		return rcapa;
	}

	/**
	 *  Get the capability.
	 *  @return The capability.
	 * /
	protected CapabilityWrapper getCapability()
	{
		return capability;
	}*/

	//-------- static part -------

	/** The hashtable containing plan init values (hack???). */
	// Needed for passing the rplan to the abstract plan instance.
	// Must be thread safe as more than one agent could use the table
	// at the same time.
	static Map planinit	= SCollection.createHashtable();
}
