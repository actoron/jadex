package jadex.micro.testcases.servicecall;

import java.io.IOException;
import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.util.STest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.testcases.TestAgent;

/**
 *  Agent providing a direct service.
 */
@RequiredServices({
	@RequiredService(name="raw", type=IServiceCallService.class, binding=@Binding(proxytype=Binding.PROXYTYPE_RAW, scope=Binding.SCOPE_GLOBAL)),
	@RequiredService(name="direct", type=IServiceCallService.class, binding=@Binding(proxytype=Binding.PROXYTYPE_DIRECT, scope=Binding.SCOPE_GLOBAL)),
	@RequiredService(name="decoupled", type=IServiceCallService.class, binding=@Binding(proxytype=Binding.PROXYTYPE_DECOUPLED, scope=Binding.SCOPE_GLOBAL)),
	@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})
@Agent
//@Arguments(replace=false, value=@Argument(name="max", clazz=int.class, defaultvalue="10"))
@Properties({@NameValue(name=Testcase.PROPERTY_TEST_TIMEOUT, value="jadex.base.Starter.getScaledLocalDefaultTimeout(null, 3)")}) // cannot use $component.getIdentifier() because is extracted from test suite :-(
public class ServiceCallTestAgent extends TestAgent
{
	//-------- constants --------
	
	/** Wait for key pressed between local and remote tests (for profiling). */
	public static boolean	WAIT	= false;
	
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	@Agent
	protected IInternalAccess ag;
	
	/** The invocation count. */
	@AgentArgument
	protected int max	= 500;
	
	//-------- methods --------
	
	/**
	 *  Perform tests.
	 */
	protected IFuture<TestReport>	test(final IComponentManagementService cms, final boolean local)
	{
		final Future<TestReport>	ret	= new Future<TestReport>();
		
//		System.out.println("Service call test on: "+agent.getComponentIdentifier());
		
		performTests(cms, RawServiceAgent.class.getName()+".class", local ? 20000 : 1, local ? 6 : 1, local ? 6 : 1).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
		{
			public void customResultAvailable(Void result)
			{
				performTests(cms, DirectServiceAgent.class.getName()+".class", local ? 10 : 1, local ? 6 : 1, local ? 4 : 1).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
				{
					public void customResultAvailable(Void result)
					{
						performTests(cms, DecoupledServiceAgent.class.getName()+".class", local ? 2 : 1, local ? 4 : 1, local ? 2 : 1).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
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
	protected IFuture<Void>	performTests(final IComponentManagementService cms, final String agentname, final int rawfactor, final int directfactor, final int decoupledfactor)
	{
		final Future<Void> ret	= new Future<Void>();
		CreationInfo ci = ((IService)cms).getServiceIdentifier().getProviderId().getPlatformName().equals(agent.getIdentifier().getPlatformName())
			? new CreationInfo(agent.getIdentifier(), agent.getModel().getResourceIdentifier()) : new CreationInfo(agent.getModel().getResourceIdentifier());
		
		String an = agentname.toLowerCase();
		final String tag = an.indexOf("raw")!=-1? "raw": an.indexOf("direct")!=-1? "direct": an.indexOf("decoupled")!=-1? "decoupled": null;	
//		System.out.println("Tag is: "+tag+" "+agentname);	
		
		cms.createComponent(null, agentname, ci, null)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid)
			{
				final Future<Void>	ret2 = new Future<Void>();
				performSingleTest(tag, "raw", rawfactor).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret2)
				{
					public void customResultAvailable(Void result)
					{
						performSingleTest(tag, "direct", directfactor).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret2)
						{
							public void customResultAvailable(Void result)
							{
								performSingleTest(tag, "decoupled", decoupledfactor).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret2)));
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
//		IFuture<IServiceCallService> fut = SServiceProvider.waitForService(agent, new IResultCommand<IFuture<IServiceCallService>, Void>()
//		{
//			public IFuture<IServiceCallService> execute(Void args)
//			{
//				return agent.getComponentFeature(IRequiredServicesFeature.class).getService(servicename);
//				return agent.getComponentFeature(IRequiredServicesFeature.class).getService(servicename, true, 
//					new TagFilter<IServiceCallService>(agent.getExternalAccess(), tag));
//			}
//		}, 7, 1500);
		IFuture<IServiceCallService> fut = agent.getFeature(IRequiredServicesFeature.class).getService(servicename);
		
		fut.addResultListener(new ExceptionDelegationResultListener<IServiceCallService, Void>(ret)
		{
			public void customResultAvailable(final IServiceCallService service)
			{
				IResultListener<Void>	lis	= new DelegationResultListener<Void>(ret)
				{
					int	count = 0;
					int	num	= max*factor;
					long start;
					
					public void customResultAvailable(Void result)
					{
//						if(ag.getAgentAdapter().isExternalThread())
//							System.out.println("wrong thread");
						
						if(count==0)
						{
							// To start profiling after setup.
							if(WAIT && "raw".equals(tag) && "raw".equals(servicename))
							{
								try
								{
									System.out.println("Press [RETURN] to start...");
									while(System.in.read()!='\n');
								}
								catch(IOException e)
								{
								}
							}
							start = System.nanoTime();
						}
						
						if(count==num)
						{
							long	end	= System.nanoTime();
							System.out.println(servicename+" service call on "+tag+" service took "+((end-start)/10/((long)max*factor))/100.0+" microseconds per call ("+(max*factor)+" calls in "+(end-start)+" nanos).");
							// To stop profiling after finished.
							if(WAIT && "decoupled".equals(tag) && "decoupled".equals(servicename))
							{
								try
								{
									System.out.println("Press [RETURN] to continue...");
									while(System.in.read()!='\n');
								}
								catch(IOException e)
								{
								}
							}
							ret.setResult(null);
						}
						else
						{
							count++;
							service.call().addResultListener(this);
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
						super.exceptionOccurred(exception);
					}
				};
				service.call().addResultListener(lis);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				super.exceptionOccurred(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Starter for testing.
	 */
	public static void main(String[] args) throws Exception
	{
		// Start platform with agent.
		IPlatformConfiguration	config1	= STest.getDefaultTestConfig();
//		config1.setLogging(true);
//		config1.setDefaultTimeout(-1);
		config1.addComponent(ServiceCallTestAgent.class);
		Starter.createPlatform(config1).get();
	}
}
