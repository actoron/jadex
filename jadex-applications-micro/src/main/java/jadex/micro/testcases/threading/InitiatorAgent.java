package jadex.micro.testcases.threading;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.Tuple2;
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

import java.util.Collection;

/**
 *  Test if service invocations are decoupled back to the caller thread
 *  with the DecouplingReturnInterceptor.
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="ts", type=ITestService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL))
})
public class InitiatorAgent extends TestAgent
{
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
		testLocal(1, 10000).addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
				testRemote(2, 1000).addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
				{
					public void customResultAvailable(TestReport result)
					{
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
	protected IFuture<TestReport> testLocal(final int testno, final int max)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		System.out.println("Test local: "+agent.getModel().getFullName());
		
		performTest(agent.getComponentIdentifier().getRoot(), testno, max, true)
			.addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
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
	protected IFuture<TestReport> testRemote(final int testno, final int max)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		System.out.println("Test remote: "+agent.getModel().getFullName());
		
		createPlatform(new String[]{"-ssltcptransport", "false"}).addResultListener(agent.createResultListener(
			new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{
			public void customResultAvailable(final IExternalAccess platform)
			{
				System.out.println("Test remote1: "+agent.getModel().getFullName());
				
				performTest(platform.getComponentIdentifier(), testno, max, false)
					.addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
				{
					public void customResultAvailable(final TestReport result)
					{
						platform.killComponent();
//							.addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, TestReport>(ret)
//						{
//							public void customResultAvailable(Map<String, Object> v)
//							{
//								ret.setResult(result);
//							}
//						});
						ret.setResult(result);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Perform the test. Consists of the following steps:
	 *  Create provider agent
	 *  Call methods on it
	 */
	protected IFuture<TestReport> performTest(final IComponentIdentifier root, final int testno, final int max, final boolean hassectrans)
	{
		final Future<TestReport> ret = new Future<TestReport>();

		final Future<TestReport> res = new Future<TestReport>();
		
		ret.addResultListener(new DelegationResultListener<TestReport>(res)
		{
			public void exceptionOccurred(Exception exception)
			{
				TestReport tr = new TestReport("#"+testno, "Tests if secure transport works.");
				tr.setReason(exception.getMessage());
				super.resultAvailable(tr);
			}
		});
		
		final Future<Collection<Tuple2<String, Object>>> resfut = new Future<Collection<Tuple2<String, Object>>>();
		IResultListener<Collection<Tuple2<String, Object>>> reslis = new DelegationResultListener<Collection<Tuple2<String,Object>>>(resfut);
		
		createComponent("jadex/micro/testcases/threading/ProviderAgent.class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				callService(cid, hassectrans, testno, max).addResultListener(new DelegationResultListener<TestReport>(ret));
			}
		});
		
		return res;
	}
	
	/**
	 *  Call the service methods.
	 */
	protected IFuture<TestReport> callService(IComponentIdentifier cid, final boolean hassectrans, int testno, final int max)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		System.out.println("Call service: "+agent.getModel().getFullName());
		
		final TestReport tr = new TestReport("#"+testno, "Test if secure transmission works "+(hassectrans? "with ": "without ")+"secure transport.");
		
		IFuture<ITestService> fut = agent.getServiceContainer().getService(ITestService.class, cid);
		fut.addResultListener(new ExceptionDelegationResultListener<ITestService, TestReport>(ret)
		{
			public void customResultAvailable(final ITestService ts)
			{
				final long start = System.currentTimeMillis();
				invoke(ts, 0, max).addResultListener(new ExceptionDelegationResultListener<Integer, TestReport>(ret)
				{
					public void customResultAvailable(Integer result)
					{
						long dur = System.currentTimeMillis()-start;
						System.out.println("Needed per call [ms]: "+((double)dur)/max);
						System.out.println("Calls per second: "+((double)max)/dur*1000);
						if(result==0)
						{
							tr.setSucceeded(true);
						}
						else
						{
							tr.setFailed("Invocations failed: "+result);
						}
						ret.setResult(tr);
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Invoke the method.
	 */
	protected IFuture<Integer> invoke(final ITestService ts, final int i, final int max)
	{
		final Future<Integer> ret = new Future<Integer>();
		
		System.out.println("Invoke: "+System.currentTimeMillis()+", "+agent.getModel().getFullName());

		final IComponentIdentifier caller = IComponentIdentifier.LOCAL.get();
		
		final int[] errcnt = new int[1];
		ts.testThreading().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				if(!caller.equals(IComponentIdentifier.LOCAL.get()))
				{
					errcnt[0]++;
					System.out.println("err: "+caller+" "+IComponentIdentifier.LOCAL.get());
				}
				else
				{
					System.out.println("ok: "+System.currentTimeMillis()+", "+i);
				}
				
				if(i<max)
				{
					invoke(ts, i+1, max).addResultListener(new DelegationResultListener<Integer>(ret));
				}
				else
				{
					ret.setResult(new Integer(errcnt[0]));
				}
			}
		
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
				resultAvailable(null);
			}
		});
		
		return ret;
	}
}
