package jadex.bdiv3.runtime.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.commons.SReflect;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;

/**
 *  Handler used for service-goal delegation.
 *  Creates specific goal for an incoming service request.
 *  For Pojo Goal must have constructor that exactly fits to
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
	
	/** The type. */
	protected Class<?> type;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service wrapper invocation handler.
	 *  @param agent The internal access of the agent.
	 */
	public GoalDelegationHandler(IInternalAccess agent, Map<String, String> goalnames, Class<?> type)
	{
		if(agent==null)
			throw new IllegalArgumentException("Agent must not null.");
		if(goalnames==null)
			throw new IllegalArgumentException("Goal names must not null.");
		if(type==null)
			throw new IllegalArgumentException("Type must not null.");
		
		this.agent = agent;
		this.goalnames = goalnames;
		this.type = type;
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
//		System.out.println("gloaldelehandler: "+SUtil.arrayToString(args));
		String goalname = goalnames.get(method.getName());
		
		if(goalname==null)
			throw new RuntimeException("No method-goal mapping found: "+method.getName()+" "+goalnames);
		
		final IInternalBDIAgentFeature	bdif	= agent.getComponentFeature(IInternalBDIAgentFeature.class);
		MCapability mcapa = (MCapability)bdif.getCapability().getModelElement();
		final MGoal mgoal = mcapa.getGoal(goalname);
		
		Class<?> goalcl = mgoal.getTargetClass(agent.getClassLoader());

		Object goal;
		if(goalcl!=null)
		{
			Class<?>[] mptypes = method.getParameterTypes();
			
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
			
		}
		else
		{
			Map<String, Object> vals = new HashMap<String, Object>();
//			Annotation[][] annss = method.getParameterAnnotations();
//			if(annss!=null)
//			{
//				for(int i=0; i<annss.length; i++)
//				{
//					for(Annotation ann: annss[i])
//					{
//						if(ann instanceof ParameterInfo)
//						{
//							ParameterInfo pi = (ParameterInfo)ann;
//							String name = pi.value();
//							vals.put(name, args[i]);
//						}
//					}
//				}
//			}
			
			List<MParameter> mparams = mgoal.getParameters();
			if(mparams!=null)
			{
				String typename = SReflect.getInnerClassName(type);
				String methodname = method.getName();
				
				for(int i=0; i<mparams.size(); i++)
				{
					List<String> mappings = mparams.get(i).getServiceMappings();
					if(mappings!=null)
					{
						for(String mapping: mappings)
						{
							boolean ok = false;
							
							int count = mapping.length() - mapping.replace(".", "").length();
							if(count==1)
							{
								ok = mapping.indexOf(typename)!=-1;
								if(!ok)
								{
									ok = mapping.indexOf(methodname)!=-1;
								}
							}
							else if(count==2)
							{
								ok = mapping.indexOf(typename)!=-1 && mapping.indexOf(methodname)!=-1;
							}
							if(ok)
							{
								String target = mapping.substring(mapping.lastIndexOf(".")+1); 
								if("result".equals(target))
									continue;
								if(target.startsWith("arg"))
									target=target.substring(3);
								if(target.startsWith("argument"))
									target=target.substring(8);
								int num = Integer.valueOf(target);
								vals.put(mparams.get(i).getName(), args[num]);
								break;
							}
						}
					}
					else
					{
						vals.put(mparams.get(i).getName(), args[i]);
					}
				}
			}
			
			goal = new RGoal(agent, mgoal, null, null, vals, null);
		}
		
		final Object fgoal = goal;
		
		// Drop goal when future is terminated from service caller
		FutureFunctionality	func	= new FutureFunctionality((Logger)null)
		{
			@Override
			public void handleTerminated(Exception reason)
			{
//				System.out.println("terminated call: "+fgoal);
				IBDIAgentFeature bf = agent.getComponentFeature0(IBDIAgentFeature.class);
				if(bf!=null)
				{
					bf.dropGoal(fgoal);
				}
				else
				{
					((IGoal)fgoal).drop();
				}
//				((IBDIAgentFeature)bdif).dropGoal(fgoal);
			}
		};
		final Future<Object> ret = (Future<Object>)FutureFunctionality.getDelegationFuture(method.getReturnType(), func);
		
//		System.out.println("gloaldelehandler disp: "+((RGoal)fgoal).getId());
		
		IResultListener<Object> lis = new ExceptionDelegationResultListener<Object, Object>(ret)
		{
			public void customResultAvailable(Object result)
			{
//				Object res = RGoal.getGoalResult(fgoal, mgoal, agent.getClassLoader());
//				Object res = RGoal.getGoalResult(rgoal, agent.getClassLoader());
				
				// Do not set goal itself as result of service call but null then
				// Use setResultIfUndo as it could be a terminable future
				
//				System.out.println("gloaldelehandler end"+SUtil.arrayToString(args));
				ret.setResultIfUndone(fgoal==result? null: result);
			}
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("gloaldelehandler endex"+SUtil.arrayToString(args));
				ret.setExceptionIfUndone(exception);
			}
		};
		
		IBDIAgentFeature bf = agent.getComponentFeature0(IBDIAgentFeature.class);
		if(bf!=null)
		{
			bf.dispatchTopLevelGoal(goal).addResultListener(lis);
		}
		else
		{
			IBDIXAgentFeature bfx = agent.getComponentFeature0(IBDIXAgentFeature.class);
			bfx.getGoalbase().dispatchTopLevelGoal((IGoal)goal).addResultListener(lis);
		}
		
		return ret;
	}
	
	/**
	 *  Create a wrapper service implementation based on a published goal.
	 */
	public static Object createServiceImplementation(IInternalAccess agent, Class<?> type, String[] methodnames, String[] goalnames)
	{
//		if(methodnames==null || methodnames.length==0)
//			throw new IllegalArgumentException("At least one method-goal mapping must be given.");
		Map<String, String> gn = new HashMap<String, String>();
		for(int i=0; i<methodnames.length; i++)
		{
			gn.put(methodnames[i], goalnames[i]);
		}
		return Proxy.newProxyInstance(agent.getClassLoader(), new Class[]{type}, 
			new GoalDelegationHandler(agent, gn, type));
	}
}
