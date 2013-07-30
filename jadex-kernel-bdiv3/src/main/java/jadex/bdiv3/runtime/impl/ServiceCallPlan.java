package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 *  Default plan for realizing a service call.
 *  Finds and calls a service.
 */
@Plan
public class ServiceCallPlan
{
	@PlanReason
	protected Object reason;
	
//	@PlanCapability
	protected IInternalAccess agent;
	
	/** The service name. */
	protected String service;
	
	/** The method. */
	protected String method;
	
	/** The parameter service mapper. */
	protected IServiceParameterMapper<Object> mapper;
	
	/**
	 * 
	 */
	public ServiceCallPlan(IInternalAccess agent, String service, String method, IServiceParameterMapper<Object> mapper)
	{
		this.agent = agent;
		this.service = service;
		this.method = method;
		this.mapper = mapper;
	}
	
	@PlanBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();

		IIntermediateFuture<Object> services = agent.getServiceContainer().getRequiredServices(service);
		
		services.addResultListener(new IIntermediateResultListener<Object>()
		{
			int opencalls = 0;
			boolean fini = false;
			public void intermediateResultAvailable(Object proxy)
			{
				try
				{
					opencalls++;
					
					Method tmp;
					if(method==null)
					{
						RequiredServiceInfo rsi = agent.getServiceContainer().getRequiredServiceInfo(service);
						Class<?> cl = rsi.getType().getType(agent.getClassLoader());
						tmp = cl.getDeclaredMethods()[0];
					}
					else
					{
						tmp = SReflect.getMethods(proxy.getClass(), method)[0];
					}
					final Method m = tmp;
					Object[] myargs = mapper.createServiceParameters(reason, m);
//					System.out.println("invoking service, args: "+SUtil.arrayToString(myargs));
					
					Object	res	= m.invoke(proxy, myargs);
					
					if(res instanceof IFuture<?>)
					{
						((IFuture<Object>)res).addResultListener(new IResultListener<Object>()
						{
							public void resultAvailable(Object result)
							{
								mapper.handleServiceResult(reason, m, result);
								opencalls--;
								if(opencalls==0)
									ret.setResult(null);
							}

							public void exceptionOccurred(Exception exception)
							{
								mapper.handleServiceResult(reason, m, exception);
								opencalls--;
								if(opencalls==0)
									ret.setResult(null);
							}
						});
//						System.out.println("invoked, result: "+resu);
						// todo: set return value on parameter
					}
					else
					{
					}
				}
				catch(Exception e)
				{
					opencalls--;
					if(opencalls==0 && fini)
						ret.setResult(null);
					e.printStackTrace();
				}
			}
			
			public void finished()
			{
				fini = true;
				if(opencalls==0)
					ret.setResult(null);
			}
			
			public void resultAvailable(Collection<Object> result)
			{
				for(Object res: result)
				{
					intermediateResultAvailable(res);
				}
				finished();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
}
