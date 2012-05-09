package jadex.bdi.runtime.impl;


import jadex.bdi.model.IMParameter;
import jadex.bdi.model.IMParameterElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.IParameter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *  
 */
@Service
public class GoalDelegationHandler  implements InvocationHandler
{
	//-------- attributes --------
	
	/** The agent. */
	protected IBDIInternalAccess agent;
	
	/** The goal name. */
	protected String goalname;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service wrapper invocation handler.
	 *  @param agent The internal access of the agent.
	 */
	public GoalDelegationHandler(IBDIInternalAccess agent, String goalname)
	{
		if(agent==null)
			throw new IllegalArgumentException("Agent must not null.");
		if(goalname==null)
			throw new IllegalArgumentException("Goal name must not null.");
		this.agent = agent;
		this.goalname = goalname;
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
		
		final IGoal goal = agent.getGoalbase().createGoal(goalname);
//		final Object handle = ((GoalFlyweight)paint).getHandle();
		goal.addGoalListener(new IGoalListener()
		{
			public void goalFinished(AgentEvent ae)
			{
				if(goal.isSucceeded())
				{
//					System.out.println("painter success: "+handle);
//					Map<String, Object> results = new HashMap<String, Object>();
//					IParameter[] params = goal.getParameters();
//					for(int i=0; i<params.length; i++)
//					{
//						String dir = ((IMParameter)params[i].getModelElement()).getDirection();
//						if(OAVBDIMetaModel.PARAMETER_DIRECTION_INOUT.equals(dir) || OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(dir))
//							results.put(params[i].getName(), params[i].getValue());
//					}
					ret.setResult(null);
				}
				else
				{
//						System.out.println("painter failure: "+handle);
					ret.setException(goal.getException()!=null? goal.getException(): new RuntimeException());
				}
			}
			
			public void goalAdded(AgentEvent ae)
			{
			}
		});
		agent.getGoalbase().dispatchTopLevelGoal(goal);
	
		return ret;
	}
}