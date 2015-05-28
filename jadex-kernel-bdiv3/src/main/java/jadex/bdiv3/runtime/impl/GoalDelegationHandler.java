package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  Handler used for service-goal delegation.
 *  Creates specific goal for an incoming service request.
 *  Goal must have constructor that exactly fits to
 *  service invocation parameters
 */
@Service
public class GoalDelegationHandler  implements InvocationHandler
{
	//-------- attributes --------
	
	/** The agent. */
	protected IInternalAccess agent;
	
	/** The goal name. */
	protected Map<String, String> goalnames;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service wrapper invocation handler.
	 *  @param agent The internal access of the agent.
	 */
	public GoalDelegationHandler(IInternalAccess agent, Map<String, String> goalnames)
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
		String goalname = goalnames.get(method.getName());
		
		if(goalname==null)
			throw new RuntimeException("No method-goal mapping found: "+method.getName()+" "+goalnames);
		
		final IInternalBDIAgentFeature	bdif	= (IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class);
		MCapability mcapa = (MCapability)bdif.getCapability().getModelElement();
		final MGoal mgoal = mcapa.getGoal(goalname);
		
		Class<?> goalcl = mgoal.getTargetClass(agent.getClassLoader());
		
		Class<?>[] mptypes = method.getParameterTypes();
		
		Object goal;
		
		try
		{
			Constructor<?> c = goalcl.getConstructor(mptypes);
			goal = c.newInstance(args);
		}
		catch(Exception e)
		{
			Class<?>[] mptypes2 = new Class<?>[mptypes.length+1];
			System.arraycopy(mptypes, 0, mptypes2, 1, mptypes.length);
			Object pojo = agent.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
			mptypes2[0] = pojo.getClass();
			Constructor<?> c = goalcl.getConstructor(mptypes2);
			Object[] args2 = new Object[args.length+1];
			System.arraycopy(args, 0, args2, 1, args.length);
			args2[0] = pojo;
			goal = c.newInstance(args2);
		}
		final Object fgoal = goal;
		
		// Drop goal when future is terminated from service caller
		final Future<Object> ret = (Future<Object>)FutureFunctionality.getDelegationFuture(method.getReturnType(), new FutureFunctionality((Logger)null)
		{
			public void terminate(Exception reason, IResultListener<Void> terminate)
			{
//				System.out.println("terminated call: "+fgoal);
				((BDIAgentFeature)bdif).dropGoal(fgoal);
				super.terminate(reason, terminate);
			}
		});
		
		((BDIAgentFeature)bdif).dispatchTopLevelGoal(fgoal).addResultListener(new ExceptionDelegationResultListener<Object, Object>(ret)
		{
			public void customResultAvailable(Object result)
			{
//				Object res = RGoal.getGoalResult(fgoal, mgoal, agent.getClassLoader());
//				Object res = RGoal.getGoalResult(rgoal, agent.getClassLoader());
				// Do not set goal itself as result of service call but null then
				// Use setResultIfUndo as it could be a terminable future
				ret.setResultIfUndone(fgoal==result? null: result);
			}
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}
		});
	
		return ret;
	}
}
