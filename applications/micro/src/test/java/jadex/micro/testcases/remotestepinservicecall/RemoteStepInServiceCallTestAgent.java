package jadex.micro.testcases.remotestepinservicecall;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
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
@ProvidedServices(@ProvidedService(type=ITestService.class, implementation=@Implementation(expression="$pojoagent")))
public class RemoteStepInServiceCallTestAgent extends TestAgent	 implements ITestService
{
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
		agent.getLogger().severe("Testagent test local: "+agent.getDescription());
		testLocal(1).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				agent.getLogger().severe("Testagent test remote: "+agent.getDescription());
				tc.addReport(result);
				testRemote(2).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
				{
					public void customResultAvailable(TestReport result)
					{
						agent.getLogger().severe("Testagent tests finished: "+agent.getDescription());
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
		
		performTest(agent.getIdentifier().getRoot(), testno)
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)
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
		
		setupRemotePlatform(false).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(
			new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{
			public void customResultAvailable(final IExternalAccess platform)
			{
				performTest(platform.getComponentIdentifier(), testno)
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Perform the test. Consists of the following steps:
	 *  Create provider agent
	 *  Call methods on it
	 */
	protected IFuture<TestReport> performTest(final IComponentIdentifier root, final int testno)
	{
		final Future<TestReport> ret = new Future<TestReport>();

		final Future<TestReport> res = new Future<TestReport>();
		
		ret.addResultListener(new DelegationResultListener<TestReport>(res)
		{
			public void exceptionOccurred(Exception exception)
			{
				TestReport tr = new TestReport("#"+testno, "Test if remote scheduling inside a service call works");
				tr.setFailed(exception);
				super.resultAvailable(tr);
			}
		});
		
		final Future<Map<String, Object>> resfut = new Future<Map<String, Object>>();
		IResultListener<Map<String, Object>> reslis = new DelegationResultListener<Map<String,Object>>(resfut);
		
//		System.out.println("root: "+root+" "+SUtil.arrayToString(root.getAddresses()));
		createComponent(ProviderAgent.class.getName()+".class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				callService(cid, testno).addResultListener(new DelegationResultListener<TestReport>(ret));
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
	protected IFuture<TestReport> callService(final IComponentIdentifier cid, final int testno)
	{
		System.out.println("calling0: "+cid+", #"+testno);
		final Future<TestReport> ret = new Future<TestReport>();
		
		final TestReport tr = new TestReport("#"+testno, "Test if remote scheduling inside a service call works " + (testno == 1? "(local case)." : "(remote case)."));
		
		IFuture<ITestService> fut = agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(ITestService.class, cid));
		

		fut.addResultListener(new ExceptionDelegationResultListener<ITestService, TestReport>(ret)
		{
			public void customResultAvailable(final ITestService ts)
			{
				IFuture<Void> fut;
				System.out.println("calling1: "+cid+", #"+testno);
				fut = ts.method(agent.getExternalAccess());
				System.out.println("calling2: "+cid+", #"+testno);
				fut.get();
				System.out.println("calling3: "+cid+", #"+testno);

				tr.setSucceeded(true);
				ret.setResult(tr);
			}
		});
		return ret;
	}
	
	/**
	 *  Nop for callback testing.
	 */
	@Override
	public IFuture<Void> method(IExternalAccess exta)
	{
		return IFuture.DONE;
	}
}
