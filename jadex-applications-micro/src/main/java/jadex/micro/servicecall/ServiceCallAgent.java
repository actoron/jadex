package jadex.micro.servicecall;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.TagFilter;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.IResultCommand;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.testcases.TestAgent;

/**
 *  Agent providing a direct service.
 */
@RequiredServices({
	@RequiredService(name="raw", type=IServiceCallService.class, binding=@Binding(proxytype=Binding.PROXYTYPE_RAW, dynamic=true, scope=Binding.SCOPE_GLOBAL)),
	@RequiredService(name="direct", type=IServiceCallService.class, binding=@Binding(proxytype=Binding.PROXYTYPE_DIRECT, dynamic=true, scope=Binding.SCOPE_GLOBAL)),
	@RequiredService(name="decoupled", type=IServiceCallService.class, binding=@Binding(proxytype=Binding.PROXYTYPE_DECOUPLED, dynamic=true, scope=Binding.SCOPE_GLOBAL)),
	@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})
@Agent
@Arguments(replace=false, value=@Argument(name="max", clazz=int.class, defaultvalue="10"))
public class ServiceCallAgent	extends TestAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	@Agent
	protected IInternalAccess ag;
	
	/** The invocation count. */
	@AgentArgument
	protected int max;
	
	//-------- methods --------
	
	/**
	 *  Perform tests.
	 */
	protected IFuture<TestReport>	test(final IComponentManagementService cms, final boolean local)
	{
		final Future<TestReport>	ret	= new Future<TestReport>();
		
		System.out.println("Service call test on: "+agent.getComponentIdentifier());
		
		performTests(cms, RawServiceAgent.class.getName()+".class", local ? 2000 : 1).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
		{
			public void customResultAvailable(Void result)
			{
				performTests(cms, DirectServiceAgent.class.getName()+".class", local ? 200 : 1).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
				{
					public void customResultAvailable(Void result)
					{
						performTests(cms, DecoupledServiceAgent.class.getName()+".class", local ? 100 : 1).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
						{
							public void customResultAvailable(Void result)
							{
//								System.out.println("XXXXXXXXXXXXXXXXXXX: "+local);
								ret.setResult(new TestReport("#1", "test", true, null));
							}
						});
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 *  Perform all tests with the given agent.
	 */
	protected IFuture<Void>	performTests(final IComponentManagementService cms, final String agentname, final int factor)
	{
		final Future<Void> ret	= new Future<Void>();
		CreationInfo ci = ((IService)cms).getServiceIdentifier().getProviderId().getPlatformName().equals(agent.getComponentIdentifier().getPlatformName())
			? new CreationInfo(agent.getComponentIdentifier(), agent.getModel().getResourceIdentifier()) : new CreationInfo(agent.getModel().getResourceIdentifier());
		
		String an = agentname.toLowerCase();
		final String tag = an.indexOf("raw")!=-1? "raw": an.indexOf("direct")!=-1? "direct": an.indexOf("decoupled")!=-1? "decoupled": null;	
		System.out.println("Tag is: "+tag+" "+agentname);	
		
		cms.createComponent(null, agentname, ci, null)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid)
			{
				final Future<Void>	ret2 = new Future<Void>();
				performSingleTest(tag, "raw", 5*factor).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret2)
				{
					public void customResultAvailable(Void result)
					{
						performSingleTest(tag, "direct", 2*factor).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret2)
						{
							public void customResultAvailable(Void result)
							{
								performSingleTest(tag, "decoupled", 1*factor).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret2)));
							}
						}));
					}
				}));
				
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
		
		return ret;
	}
	
	/**
	 *  Perform a number of calls on one required service.
	 */
	protected IFuture<Void>	performSingleTest(final String tag, final String servicename, final int factor)
	{
		final Future<Void> ret	= new Future<Void>();
//		IFuture<IServiceCallService> fut = getServiceCallService(servicename, 0, 2, 3000);
		IFuture<IServiceCallService> fut = SServiceProvider.waitForService(agent, new IResultCommand<IFuture<IServiceCallService>, Void>()
		{
			public IFuture<IServiceCallService> execute(Void args)
			{
//				return agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService(servicename);
				return agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService(servicename, true, 
					new TagFilter<IServiceCallService>(agent.getExternalAccess(), tag));
			}
		}, 7, 1500);
		
		fut.addResultListener(new ExceptionDelegationResultListener<IServiceCallService, Void>(ret)
		{
			public void customResultAvailable(final IServiceCallService service)
			{
				IResultListener<Void>	lis	= new DelegationResultListener<Void>(ret)
				{
					int	count = max*factor;
					long start = System.currentTimeMillis();
					
					public void customResultAvailable(Void result)
					{
//						if(ag.getAgentAdapter().isExternalThread())
//							System.out.println("wrong thread");
						
						count--;
						if(count==0)
						{
							long	end	= System.currentTimeMillis();
							System.out.println(servicename+" service call on "+service+" took "+((end-start)*10000/((long)max*factor))/10.0+" microseconds per call ("+(max*factor)+" calls in "+(end-start)+" millis).");
							ret.setResult(null);
						}
						else
						{
							service.call().addResultListener(this);
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						super.exceptionOccurred(exception);
					}
				};
				service.call().addResultListener(lis);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				super.exceptionOccurred(exception);
			}
		});
		
		return ret;
	}
}
