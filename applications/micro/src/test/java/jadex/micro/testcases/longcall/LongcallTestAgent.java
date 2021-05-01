package jadex.micro.testcases.longcall;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateFutureCommandResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.testcases.TestAgent;

/**
 *  Tests if a long lasting call works even with default or small timeout.
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="ts", type=ITestService.class, scope=ServiceScope.GLOBAL)
})
@Properties({@NameValue(name=Testcase.PROPERTY_TEST_TIMEOUT, value="jadex.base.Starter.getScaledDefaultTimeout(null, 10)")}) // cannot use $component.getId() because is extracted from test suite :-(
public class LongcallTestAgent extends TestAgent
{
	/**
	 *  The test count.
	 */
	protected int getTestCount()
	{
		return 12;
	}
	
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
//		agent.getLogger().severe("Testagent test local: "+agent.getDescription());
		testLocal(1).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IntermediateExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(Collection<TestReport> result)
			{
				for(TestReport tr: result)
					tc.addReport(tr);
				proceed();
			}
			
			public void finished()
			{
				proceed();
			}
			
			public void intermediateResultAvailable(TestReport result)
			{
				tc.addReport(result);
			}
			
			public void proceed()
			{
//					agent.getLogger().severe("Testagent test rmeote: "+agent.getDescription());
				testRemote(3).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IntermediateExceptionDelegationResultListener<TestReport, Void>(ret)
				{
					public void customResultAvailable(Collection<TestReport> result)
					{
						for(TestReport tr: result)
							tc.addReport(tr);
						ret.setResult(null);
					}
					
					public void finished()
					{
//							agent.getLogger().severe("Testagent tests finished: "+agent.getDescription());
						ret.setResult(null);
					}
					
					public void intermediateResultAvailable(TestReport result)
					{
						tc.addReport(result);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Test local.
	 */
	protected IIntermediateFuture<TestReport> testLocal(final int testno)
	{
		final IntermediateFuture<TestReport> ret = new IntermediateFuture<TestReport>();
		
		performTests(agent.getId().getRoot(), testno, true)
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IntermediateDelegationResultListener<TestReport>(ret)));
		
		return ret;
	}
	
	/**
	 *  Test remote.
	 */
	protected IIntermediateFuture<TestReport> testRemote(final int testno)
	{
		final IntermediateFuture<TestReport> ret = new IntermediateFuture<TestReport>();
		
		setupRemotePlatform(false).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Collection<TestReport>>(ret)
		{
			public void customResultAvailable(final IExternalAccess exta)
			{
				performTests(exta.getId(), testno, false)
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IntermediateDelegationResultListener<TestReport>(ret)));
			}
		});

		return ret;
	}
	
	/**
	 *  Perform the test. Consists of the following steps:
	 *  Create provider agent
	 *  Call methods on it
	 */
	protected IIntermediateFuture<TestReport> performTests(final IComponentIdentifier root, final int testno, final boolean hassectrans)
	{
		final IntermediateFuture<TestReport> ret = new IntermediateFuture<TestReport>();

//		final IntermediateFuture<TestReport> res = new IntermediateFuture<TestReport>();
//		
//		ret.addResultListener(new IntermediateDelegationResultListener<TestReport>(res)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				TestReport tr = new TestReport("#"+testno, "Tests if a long running call works.");
//				tr.setFailed(exception);
//				List<TestReport> li = new ArrayList<TestReport>();
//				super.resultAvailable(li);
//			}
//		});
		
		final Future<Map<String, Object>> resfut = new Future<Map<String, Object>>();
		IResultListener<Map<String, Object>> reslis = new DelegationResultListener<Map<String,Object>>(resfut);
		
//		System.out.println("root: "+root+" "+SUtil.arrayToString(root.getAddresses()));
		createComponent(ProviderAgent.class.getName()+".class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Collection<TestReport>>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				callServices(cid, testno, -1).addResultListener(new IntermediateDelegationResultListener<TestReport>(ret)
				{
					public void exceptionOccurred(Exception exception)
					{
						TestReport tr = new TestReport("#"+testno, "Tests if a long running call works.");
						tr.setFailed(exception);
						super.intermediateResultAvailable(tr);
						super.finished();
					}
				});
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
	 *  Call the service methods.
	 */
	protected IIntermediateFuture<TestReport> callServices(final IComponentIdentifier cid, int testno, final long to)
	{
		final IntermediateFuture<TestReport> ret = new IntermediateFuture<TestReport>();
		
		IFuture<ITestService> fut = agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(ITestService.class).setProvider(cid));
		
		fut.addResultListener(new ExceptionDelegationResultListener<ITestService, Collection<TestReport>>(ret)
		{
			public void customResultAvailable(final ITestService ts)
			{
				// create a service call meta object and set the timeout
				if(to!=-1)
				{
//					ServiceCall.setInvocationProperties(to, true);
					ServiceCall call = ServiceCall.getOrCreateNextInvocation();
					call.setTimeout(to);
//					call.setRealtime(Boolean.FALSE);
				}				
				
//				System.out.println("calling method: "+ServiceCall.getOrCreateNextInvocation());
				
				callMethod(ts, 1, ret).addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						ret.setFinished();
					}
					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> callMethod(final ITestService ts, final int cnt, final IntermediateFuture<TestReport> ret) 
	{
		final Future<Void> res = new Future<Void>();
		
		try
		{
			// Garbage collect before executing method to avoid interference causing timeouts on slow computers
			System.gc();
			
			final TestReport tr = new TestReport("#"+cnt, "Test if long call works with normal timeout.");

			Method m = ITestService.class.getMethod("method"+cnt, new Class[0]);
			System.out.println("calling method "+cnt+": "+System.currentTimeMillis());
			
			// set timeout to low value to avoid long waiting in test
			ServiceCall.getOrCreateNextInvocation().setTimeout(Starter.getScaledDefaultTimeout(agent.getId(), 0.05));
			
			final long start	= agent.getLocalService(IClockService.class).getTime();
			Object	fut	= m.invoke(ts, new Object[0]);
			
			if(fut instanceof ISubscriptionIntermediateFuture)
			{
				((ISubscriptionIntermediateFuture<Object>)fut).addResultListener(new IIntermediateFutureCommandResultListener<Object>()
				{
					public void intermediateResultAvailable(Object result)
					{
					}
					
					public void finished()
					{
						long time	= agent.getLocalService(IClockService.class).getTime() - start;
						System.out.println("rec result "+cnt+": "+time);
						tr.setSucceeded(true);
						ret.addIntermediateResult(tr);
						proceed();
					}
					
					public void resultAvailable(Collection<Object> result)
					{
						finished();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						long time	= agent.getLocalService(IClockService.class).getTime() - start;
						System.out.println("rec exception "+cnt+": "+time);
						exception.printStackTrace();
						tr.setFailed("Exception: "+exception);
						ret.addIntermediateResult(tr);
						proceed();
					}
					
					public void commandAvailable(Object command)
					{
						// ignore timer updates
					}
					
					public void maxResultCountAvailable(int max) 
					{
					}
					
					public void proceed()
					{
						if(cnt<6)
						{
							callMethod(ts, cnt+1, ret).addResultListener(new DelegationResultListener<Void>(res));
						}
						else
						{
							res.setResult(null);
						}
					}
				});				
			}
			else
			{
				((IFuture<Object>)fut).addResultListener(new IFutureCommandResultListener<Object>()
				{
					public void resultAvailable(Object result)
					{
						System.out.println("rec result "+cnt+": "+(System.currentTimeMillis()-start)+", "+System.currentTimeMillis());
						tr.setSucceeded(true);
						ret.addIntermediateResult(tr);
						proceed();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("rec exception "+cnt+": "+(System.currentTimeMillis()-start)+", "+System.currentTimeMillis());
						exception.printStackTrace();
						tr.setFailed("Exception: "+exception);
						ret.addIntermediateResult(tr);
						proceed();
					}
					
					public void commandAvailable(Object command)
					{
						// ignore timer updates
					}
					
					public void proceed()
					{
						if(cnt<6)
						{
							callMethod(ts, cnt+1, ret).addResultListener(new DelegationResultListener<Void>(res));
						}
						else
						{
							res.setResult(null);
						}
					}
				});
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return res;
	}

	/**
	 *  Starter for testing.
	 */
	public static void main(String[] args) throws Exception
	{
		// Start platform with agent.
		IPlatformConfiguration	config1	= PlatformConfigurationHandler.getMinimal();
//		config1.setLogging(true);
//		config1.setDefaultTimeout(-1);
		config1.getExtendedPlatformConfiguration().setSecurity(true);
//		config1.setAwaMechanisms(AWAMECHANISM.local);
//		config1.setAwareness(true);
		config1.getExtendedPlatformConfiguration().setTcpTransport(true);
		config1.addComponent(LongcallTestAgent.class);
		Starter.createPlatform(config1).get();
	}
}
