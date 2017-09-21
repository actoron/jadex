package jadex.micro.testcases.timeout;

import java.util.Map;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.testcases.TestAgent;

/**
 * 
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="ts", type=ITestService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL))
})
@Properties({@NameValue(name=Testcase.PROPERTY_TEST_TIMEOUT, value="jadex.base.Starter.getScaledLocalDefaultTimeout(null, 3)")}) // cannot use $component.getComponentIdentifier() because is extracted from test suite :-(
public class InitiatorAgent extends TestAgent
{
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
		agent.getLogger().severe("Testagent test local: "+agent.getComponentDescription());
		testLocal(1).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				agent.getLogger().severe("Testagent test remote: "+agent.getComponentDescription());
				tc.addReport(result);
				testRemote(2).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
				{
					public void customResultAvailable(TestReport result)
					{
						agent.getLogger().severe("Testagent tests finished: "+agent.getComponentDescription());
						tc.addReport(result);
						ret.setResult(null);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Test local.
	 */
	protected IFuture<TestReport> testLocal(final int testno)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		performTest(agent.getComponentIdentifier().getRoot(), testno, true)
			.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)
		{
			public void customResultAvailable(final TestReport result)
			{
				ret.setResult(result);
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Test remote.
	 */
	protected IFuture<TestReport> testRemote(final int testno)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		System.out.println("SETUP PLATFORM");
		setupRemotePlatform(false).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(
			new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{
			public void customResultAvailable(final IExternalAccess platform)
			{
				System.out.println("PLATFORM DONE< PERFORM TEST");
				performTest(platform.getComponentIdentifier(), testno, false)
					.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Perform the test. Consists of the following steps:
	 *  Create provider agent
	 *  Call methods on it
	 */
	protected IFuture<TestReport> performTest(final IComponentIdentifier root, final int testno, final boolean hassectrans)
	{
		final Future<TestReport> ret = new Future<TestReport>();

		final Future<TestReport> res = new Future<TestReport>();
		
		ret.addResultListener(new DelegationResultListener<TestReport>(res)
		{
			public void exceptionOccurred(Exception exception)
			{
				TestReport tr = new TestReport("#"+testno, "Tests if timeout works.");
				tr.setFailed(exception);
				super.resultAvailable(tr);
			}
		});
		
		final Future<Map<String, Object>> resfut = new Future<Map<String, Object>>();
		IResultListener<Map<String, Object>> reslis = new DelegationResultListener<Map<String,Object>>(resfut);
		
//		System.out.println("root: "+root+" "+SUtil.arrayToString(root.getAddresses()));
		System.out.println("root: "+root+" "+root.getPlatformName());
		createComponent(ProviderAgent.class.getName()+".class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				System.out.println("Comp created.");
				callService(cid, testno, 5000).addResultListener(new DelegationResultListener<TestReport>(ret));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				super.exceptionOccurred(exception);
			}
		});
		
		return res;
	}
	
	/**
	 *  Call the service methods.
	 */
	protected IFuture<TestReport> callService(IComponentIdentifier cid, int testno, final long to)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final TestReport tr = new TestReport("#"+testno, "Test if timeout works "+(to==-1? "without ": "with "+to)+" timeout.");
		
		IFuture<ITestService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(ITestService.class, cid);
		
//		fut.addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				System.out.println("res: "+result+" "+SUtil.arrayToString(result.getClass().getInterfaces()));
//				try
//				{
//					ITestService ts = (ITestService)result;
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				exception.printStackTrace();
//			}
//		});
		
		fut.addResultListener(new ExceptionDelegationResultListener<ITestService, TestReport>(ret)
		{
			public void customResultAvailable(final ITestService ts)
			{
				// create a service call meta object and set the timeout
				final long start = System.currentTimeMillis();
				if(to!=-1)
				{
//					ServiceCall.setInvocationProperties(to, true);
					ServiceCall call = ServiceCall.getOrCreateNextInvocation();
					call.setTimeout(to);
					call.setRealtime(Boolean.TRUE);
					call.setProperty("extra", "somval");
				}				
				
				System.out.println("calling method: "+ServiceCall.getOrCreateNextInvocation());
				
				ts.method("test1").addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						tr.setFailed("No timeout occurred");
						ret.setResult(tr);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ServiceCall	next	= CallAccess.getNextInvocation();
						if(next!=null)
						{
							tr.setFailed("User invocation data still available: "+next);
						}
						else if(exception instanceof TimeoutException)
						{
							long diff = System.currentTimeMillis() - (start+to);
							if(to==Timeout.NONE || diff>=0 && diff<Starter.getScaledLocalDefaultTimeout(agent.getComponentIdentifier(), 1.0/15)) // 2 secs max overdue delay? ignore diff when deftimeout==-1
							{
								tr.setSucceeded(true);
							}
							else
							{
								tr.setFailed("Timeout difference too high: "+diff);
							}
						}
						else
						{
							tr.setFailed("No timeout occurred");
						}
						ret.setResult(tr);
					}
				});
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
		PlatformConfiguration	config1	= PlatformConfiguration.getMinimal();
//		config1.setLogging(true);
//		config1.setDefaultTimeout(-1);
		config1.setSecurity(true);
//		config1.setAwaMechanisms(AWAMECHANISM.local);
//		config1.setAwareness(true);
		config1.addComponent("jadex.platform.service.transport.tcp.TcpTransportAgent.class");
		config1.addComponent(InitiatorAgent.class);
		Starter.createPlatform(config1).get();
	}
}
