package jadex.micro.testcases.nfcallreturn;

import java.util.Map;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.testcases.TestAgent;

/**
 *  Tests if non-functional properties can be changed and passed back
 *  from the receiver to the sender side of a service invocation.
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="ts", type=ITestService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL))
})
public class InitiatorAgent extends TestAgent
{
	/**
	 *  The test count.
	 */
	protected int	getTestCount()
	{
		return 4;
	}
	
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
		testLocal(1).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport[], Void>(ret)
		{
			public void customResultAvailable(TestReport[] result)
			{
				for(TestReport tr: result)
					tc.addReport(tr);
				testRemote(3).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport[], Void>(ret)
				{
					public void customResultAvailable(TestReport[] result)
					{
						for(TestReport tr: result)
							tc.addReport(tr);
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
	protected IFuture<TestReport[]> testLocal(final int testno)
	{
		final Future<TestReport[]> ret = new Future<TestReport[]>();
		
		performTests(agent.getComponentIdentifier().getRoot(), testno, true)
			.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport[]>(ret)));
		
		return ret;
	}
	
	/**
	 *  Test remote.
	 */
	protected IFuture<TestReport[]> testRemote(final int testno)
	{
		final Future<TestReport[]> ret = new Future<TestReport[]>();
		
		createPlatform(null).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, TestReport[]>(ret)
		{
			public void customResultAvailable(final IExternalAccess exta)
			{
				Starter.createProxy(agent.getExternalAccess(), exta).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport[]>(ret)
				{
					public void customResultAvailable(IComponentIdentifier result)
					{
						// inverse proxy from remote to local.
						Starter.createProxy(exta, agent.getExternalAccess())
							.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport[]>(ret)
						{
							public void customResultAvailable(IComponentIdentifier result)
							{
								performTests(exta.getComponentIdentifier(), testno, false)
									.addResultListener(agent.getComponentFeature(IExecutionFeature.class)
										.createResultListener(new DelegationResultListener<TestReport[]>(ret)));
							}
						});
					}
				});
			}
		});

		return ret;
	}
	
	/**
	 *  Perform the test. Consists of the following steps:
	 *  Create provider agent
	 *  Call methods on it
	 */
	protected IFuture<TestReport[]> performTests(final IComponentIdentifier root, final int testno, final boolean hassectrans)
	{
		final Future<TestReport[]> ret = new Future<TestReport[]>();

		final Future<TestReport[]> res = new Future<TestReport[]>();
		
		ret.addResultListener(new DelegationResultListener<TestReport[]>(res)
		{
			public void exceptionOccurred(Exception exception)
			{
				TestReport tr = new TestReport("#"+testno, "Tests if nfcallreturn works.");
				tr.setFailed(exception);
				super.resultAvailable(new TestReport[]{tr});
			}
		});
		
		final Future<Map<String, Object>> resfut = new Future<Map<String, Object>>();
		IResultListener<Map<String, Object>> reslis = new DelegationResultListener<Map<String,Object>>(resfut);
		
//		System.out.println("root: "+root+" "+SUtil.arrayToString(root.getAddresses()));
		createComponent(ProviderAgent.class.getName()+".class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport[]>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				callReqService(cid, testno, 5000).addResultListener(new ExceptionDelegationResultListener<TestReport, TestReport[]>(ret)
				{
					public void customResultAvailable(final TestReport result1)
					{
						callProService(cid, testno+1, 5000).addResultListener(new ExceptionDelegationResultListener<TestReport, TestReport[]>(ret)
						{
							public void customResultAvailable(TestReport result2)
							{
								ret.setResult(new TestReport[]{result1, result2});
							}
						});
					}
				});
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
	protected IFuture<TestReport> callReqService(IComponentIdentifier cid, int testno, final long to)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final TestReport tr = new TestReport("#"+testno, "Test if returning changed nf props works with required proxy");
		
		IFuture<ITestService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(ITestService.class, cid);
		
		fut.addResultListener(new ExceptionDelegationResultListener<ITestService, TestReport>(ret)
		{
			public void customResultAvailable(final ITestService ts)
			{
				callService(ts, tr).addResultListener(new DelegationResultListener<TestReport>(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Call the service methods.
	 */
	protected IFuture<TestReport> callProService(IComponentIdentifier cid, int testno, final long to)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final TestReport tr = new TestReport("#"+testno, "Test if returning changed nf props works with provided proxy");
		
		IFuture<ITestService> fut = SServiceProvider.getService(agent, cid, ITestService.class, false);
		
		fut.addResultListener(new ExceptionDelegationResultListener<ITestService, TestReport>(ret)
		{
			public void customResultAvailable(final ITestService ts)
			{
				callService(ts, tr).addResultListener(new DelegationResultListener<TestReport>(ret));
			}
		});
		return ret;
	}
	
	protected IFuture<TestReport> callService(ITestService ts, final TestReport tr)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		ServiceCall call = ServiceCall.getOrCreateNextInvocation();
		call.setProperty("extra", "somval");
		
		System.out.println("calling method: "+ServiceCall.getNextInvocation());
		
		ts.method("test1").addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				ServiceCall sc = ServiceCall.getLastInvocation();
				System.out.println("last invoc: "+sc);
				if("new".equals(sc.getProperty("new")))
				{
					tr.setSucceeded(true);
				}
				else
				{
					tr.setFailed("Wrong service call properties: "+sc);
				}
				ret.setResult(tr);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				tr.setFailed("Failed with exception: "+exception);
				ret.setResult(tr);
			}
		});
		
		return ret;
	}
}
