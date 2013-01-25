package jadex.bdiv3.runtime;

//package jadex.bdi.runtime.impl;
//
//
//import jadex.bdi.model.IMParameter;
//import jadex.bdi.model.IMParameterSet;
//import jadex.bdi.model.OAVBDIMetaModel;
//import jadex.bdi.runtime.AgentEvent;
//import jadex.bdi.runtime.IBDIInternalAccess;
//import jadex.bdi.runtime.IGoal;
//import jadex.bdi.runtime.IGoalListener;
//import jadex.bdi.runtime.IParameter;
//import jadex.bdi.runtime.IParameterSet;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  
 */
@Service
public class GoalDelegationHandler  //implements InvocationHandler
{
	//-------- attributes --------
	
	/** The agent. */
	protected BDIAgent agent;
	
	/** The goal name. */
	protected Map<String, String> goalnames;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service wrapper invocation handler.
	 *  @param agent The internal access of the agent.
	 */
	public GoalDelegationHandler(BDIAgent agent, Map<String, String> goalnames)
	{
		if(agent==null)
			throw new IllegalArgumentException("Agent must not null.");
		if(goalnames==null)
			throw new IllegalArgumentException("Goal names must not null.");
		
		this.agent = agent;
		this.goalnames = goalnames;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a wrapper method is invoked.
	 *  Uses the cms to create a new invocation agent and lets this
	 *  agent call the web service. The result is transferred back
	 *  into the result future of the caller.
	 */
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
		final Future<Object> ret = new Future<Object>();
		
		String goalname = goalnames.get(method.getName());
		
		if(goalname==null)
			throw new RuntimeException("No method-goal mapping found: "+method.getName()+" "+goalnames);
		
		MCapability mcapa = (MCapability)agent.getCapability().getModelElement();
		MGoal mgoal = mcapa.getGoal(goalname);
		
		Class<?> goalcl = mgoal.getTargetClass(agent.getClassLoader());
		
		Class<?>[] mptypes = method.getParameterTypes();
		
		Constructor<?> c = goalcl.getConstructor(mptypes);
		if(c==null)
			throw new RuntimeException("Goal must have constructor with same signature as method: "+method);

		Object goal = c.newInstance(args);
		
		agent.dispatchTopLevelGoal(goal).addResultListener(new ExceptionDelegationResultListener<Object, Object>(ret)
		{
			public void customResultAvailable(Object result)
			{
//				ret.setResult(RGoal.getGoalResult(goal));
			}
		});
	
		return ret;
	}
}
