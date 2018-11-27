package jadex.micro.testcases.servicecall;

import java.io.IOException;
import java.util.Map;

import org.bouncycastle.asn1.cmc.GetCert;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.IResultCommand;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.testcases.TestAgent;

/**
 *  Agent providing a direct service.
 */
@RequiredServices({
	@RequiredService(name="raw", type=IServiceCallService.class, proxytype=RequiredService.PROXYTYPE_RAW, scope=ServiceScope.GLOBAL),
	@RequiredService(name="direct", type=IServiceCallService.class, proxytype=RequiredService.PROXYTYPE_DIRECT, scope=ServiceScope.GLOBAL),
	@RequiredService(name="decoupled", type=IServiceCallService.class, proxytype=RequiredService.PROXYTYPE_DECOUPLED, scope=ServiceScope.GLOBAL),
})
@Agent
//@Arguments(replace=false, value=@Argument(name="max", clazz=int.class, defaultvalue="10"))
@Properties({@NameValue(name=Testcase.PROPERTY_TEST_TIMEOUT, value="jadex.base.Starter.getScaledDefaultTimeout(null, 3)")}) // cannot use $component.getId() because is extracted from test suite :-(
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
	protected int max	= 100;
	
	//-------- methods --------
	
	/**
	 *  Perform tests.
	 */
	protected IFuture<TestReport>	test(final IExternalAccess platform, final boolean local)
	{
		final Future<TestReport>	ret	= new Future<TestReport>();
		
//		System.out.println("Service call test on: "+agent.getComponentIdentifier());
		
		performTests(platform, RawServiceAgent.class.getName()+".class", local ? 5000 : 1, local ? 6 : 1, local ? 6 : 1).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
		{
			public void customResultAvailable(Void result)
			{
				performTests(platform, DirectServiceAgent.class.getName()+".class", local ? 20 : 1, local ? 10 : 1, local ? 5 : 1).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
				{
					public void customResultAvailable(Void result)
					{
						performTests(platform, DecoupledServiceAgent.class.getName()+".class", local ? 5 : 1, local ? 5 : 1, local ? 5 : 1).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
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
	protected IFuture<Void>	performTests(final IExternalAccess platform, final String agentname, final int rawfactor, final int directfactor, final int decoupledfactor)
	{
		final Future<Void> ret	= new Future<Void>();
		CreationInfo ci = platform.getId().getPlatformName().equals(agent.getId().getPlatformName())
			? new CreationInfo(agent.getId(), agent.getModel().getResourceIdentifier()) : new CreationInfo(agent.getModel().getResourceIdentifier());
		
		String an = agentname.toLowerCase();
		final String tag = an.indexOf("raw")!=-1? "raw": an.indexOf("direct")!=-1? "direct": an.indexOf("decoupled")!=-1? "decoupled": null;	
//		System.out.println("Tag is: "+tag+" "+agentname);	
		
		platform.createComponent(ci.setFilename(agentname))
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
		{
			public void customResultAvailable(final IExternalAccess exta)
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
						platform.getExternalAccess(exta.getId()).killComponent();
						ret.setException(exception);
					}
					
					public void resultAvailable(Void result)
					{
						System.out.println(platform.getExternalAccess(exta.getId()));
						platform.getExternalAccess(exta.getId()).killComponent().addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, Void>(ret)
						{
							public void customResultAvailable(Map<String, Object> result)
							{
								ret.setResult(null);
							}
						});
					}
				});
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Perform a number of calls on one required service.
	 */
	int cnt = 0;
	protected IFuture<Void>	performSingleTest(final String tag, final String servicename, final int factor)
	{
		final Future<Void> ret	= new Future<Void>();
		final Future<Void> ret1	= new Future<Void>();
		final Future<Void> ret2	= new Future<Void>();
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
//		if(cnt==9)
//			System.out.println("before "+cnt);
		IFuture<IServiceCallService> fut = agent.getFeature(IRequiredServicesFeature.class).getService(servicename);
//		System.out.println("after"+cnt++);
		
		fut.addResultListener(new ExceptionDelegationResultListener<IServiceCallService, Void>(ret)
		{
			public void customResultAvailable(final IServiceCallService service)
			{
				IResultListener<Void> lis = new CallListener(max, factor, servicename, tag, new IResultCommand<IFuture<Void>, Void>()
				{
					@Override
					public IFuture<Void> execute(Void args)
					{
						return service.call();
					}
					
					public String toString()
					{
						return "call";
					}
				}, ret1);
				
				service.call().addResultListener(lis);
				
				IResultListener<Void> lis2 = new CallListener(max, factor, servicename, tag, new IResultCommand<IFuture<Void>, Void>()
				{
					@Override
					public IFuture<Void> execute(Void args)
					{
						return service.rawcall();
					}
					
					public String toString()
					{
						return "rawcall";
					}
				}, ret2);
				
				service.rawcall().addResultListener(lis2);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				super.exceptionOccurred(exception);
			}
		});
		
		ret1.get();
		ret2.get();
		ret.setResultIfUndone(null);
		
		return ret;
	}
	
	/**
	 *  Call listener for recursively calling the service.
	 */
	public static class CallListener extends DelegationResultListener<Void>
	{
		IResultCommand<IFuture<Void>, Void> call;
		String servicename;
		String tag;
		int factor;
		int max;
		int	count = 0;
		int	num;
		long start;
		
		public CallListener(int max, int factor, String servicename, String tag, IResultCommand<IFuture<Void>, Void> call, Future<Void> ret)
		{
			super(ret);
			this.servicename = servicename;
			this.max = max;
			this.factor = factor;
			this.num = max*factor;
			this.tag = tag;
			this.call = call;
		}
			
		public void customResultAvailable(Void result)
		{
//			if(ag.getAgentAdapter().isExternalThread())
//				System.out.println("wrong thread");
			
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
				long end = System.nanoTime();
				
				System.out.println(servicename+" service "+call.toString()+" on "+tag+" service took "+((end-start)/10/((long)max*factor))/100.0+" microseconds per call ("+(max*factor)+" calls in "+(end-start)/1000000000.0+" seconds).");
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
				super.customResultAvailable(null);
//				ret.setResult(null);
			}
			else
			{
				count++;
				call.execute(null).addResultListener(this);
//				service.call().addResultListener(this);
			}
		}
		
		public void exceptionOccurred(Exception exception)
		{
			exception.printStackTrace();
			super.exceptionOccurred(exception);
		}
	}
	
	/**
	 *  Starter for testing.
	 */
	public static void main(String[] args) throws Exception
	{
		// Start platform with agent.
		IPlatformConfiguration	config1	= STest.getDefaultTestConfig(ServiceCallTestAgent.class);
//		config1.setLogging(true);
//		config1.setDefaultTimeout(-1);
		config1.addComponent(ServiceCallTestAgent.class);
		Starter.createPlatform(config1).get();
	}
}
