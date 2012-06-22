package jadex.micro.benchmarks.servicecall;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Map;

/**
 *  Agent providing a direct service.
 */
@RequiredServices({
	@RequiredService(name="raw", type=IServiceCallService.class, binding=@Binding(proxytype=Binding.PROXYTYPE_RAW, dynamic=true)),
	@RequiredService(name="direct", type=IServiceCallService.class, binding=@Binding(proxytype=Binding.PROXYTYPE_DIRECT, dynamic=true)),
	@RequiredService(name="decoupled", type=IServiceCallService.class, binding=@Binding(proxytype=Binding.PROXYTYPE_DECOUPLED, dynamic=true)),
	@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})
@Agent
@Arguments(@Argument(name="max", clazz=int.class, defaultvalue="10000"))
public class ServiceCallAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The invocation count. */
	@AgentArgument
	protected int	max;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void>	body()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		performTests(RawServiceAgent.class.getName()+".class", 20).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				performTests(DirectServiceAgent.class.getName()+".class", 2).addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						performTests(DecoupledServiceAgent.class.getName()+".class", 1).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 *  Perform all tests with the given agent.
	 */
	protected IFuture<Void>	performTests(final String agentname, final int factor)
	{
		final Future<Void> ret	= new Future<Void>();
		IFuture<IComponentManagementService>	fut	= agent.getServiceContainer().getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				cms.createComponent(null, agentname, new CreationInfo(agent.getComponentIdentifier()), null)
					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
				{
					public void customResultAvailable(final IComponentIdentifier cid)
					{
						final Future<Void>	ret2	= new Future<Void>();
						performSingleTest("raw", 5*factor).addResultListener(new DelegationResultListener<Void>(ret2)
						{
							public void customResultAvailable(Void result)
							{
								performSingleTest("direct", 2*factor).addResultListener(new DelegationResultListener<Void>(ret2)
								{
									public void customResultAvailable(Void result)
									{
										performSingleTest("decoupled", 1*factor).addResultListener(new DelegationResultListener<Void>(ret2));
									}
								});
							}
						});
						
						ret2.addResultListener(new IResultListener<Void>()
						{
							public void exceptionOccurred(Exception exception)
							{
								cms.destroyComponent(cid);
								ret.setException(exception);
							}
							public void resultAvailable(Void result)
							{
								cms.destroyComponent(cid).addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, Void>(ret)
								{
									public void customResultAvailable(Map<String, Object> result)
									{
										ret.setResult(null);
									}
								});
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Perform a number of calls on one required service.
	 */
	protected IFuture<Void>	performSingleTest(final String servicename, final int factor)
	{
		final Future<Void> ret	= new Future<Void>();
		IFuture<IServiceCallService>	fut	= agent.getServiceContainer().getRequiredService(servicename);
		fut.addResultListener(new ExceptionDelegationResultListener<IServiceCallService, Void>(ret)
		{
			public void customResultAvailable(final IServiceCallService service)
			{
				IResultListener<Void>	lis	= new DelegationResultListener<Void>(ret)
				{
					int	count	= max*factor;
					long	start	= System.currentTimeMillis();
					
					public void customResultAvailable(Void result)
					{
						count--;
						if(count==0)
						{
							long	end	= System.currentTimeMillis();
							System.out.println(servicename+" service call on "+service+" took "+((end-start)*10000/(max*factor))/10.0+" microseconds per call ("+(max*factor)+" calls in "+(end-start)+" millis).");
							ret.setResult(null);
						}
						else
						{
							service.call().addResultListener(this);
						}
					}
				};
				service.call().addResultListener(lis);
			}
		});
		
		return ret;
	}
}
