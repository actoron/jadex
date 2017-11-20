package jadex.micro.testcases.killcomponent;

import java.util.Collection;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.testcases.TestAgent;

/**
 * 
 */
@Agent
public class KillerAgent extends TestAgent
{
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		tc.setTestCount(2);
		final Future<Void> ret = new Future<Void>();

		agent.getLogger().severe("Testagent test local: "+agent.getComponentDescription());
		testLocal(1).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				agent.getLogger().severe("Testagent test remote: "+agent.getComponentDescription());
				tc.addReport(result);
//				ret.setResult(null);
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

	@Override
	protected int getTestCount() {
		return 2;
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
		
		setupRemotePlatform(false).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(
			new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{
			public void customResultAvailable(final IExternalAccess platform)
			{
				performTest(platform.getComponentIdentifier(), testno, false)
					.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Perform the test. Consists of the following steps:
	 *  Create provider agent
	 *  kill it
	 */
	protected IFuture<TestReport> performTest(final IComponentIdentifier root, final int testno, final boolean hassectrans)
	{
		final Future<TestReport> ret = new Future<TestReport>();

		final Future<Map<String, Object>> resfut = new Future<Map<String, Object>>();
		IResultListener<Map<String, Object>> reslis = new DelegationResultListener<Map<String,Object>>(resfut);
		
		agent.getLogger().severe("Testagent create provider: "+agent.getComponentDescription());
		createComponent(ProviderAgent.class.getName()+".class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				agent.getLogger().severe("Testagent create provider done: "+agent.getComponentDescription());
				IComponentManagementService cms = getCms().get();
				IExternalAccess exta = cms.getExternalAccess(cid).get();
				final TestReport tr = new TestReport("#"+testno, "Test if kill returns result");
				System.out.println("Killing my subcomponent...");
				exta.killComponent().addResultListener(new IResultListener<Map<String, Object>>() {
					@Override
					public void exceptionOccurred(Exception exception) {
						tr.setFailed(exception);
						ret.setResult(tr);
					}

					@Override
					public void resultAvailable(Map<String, Object> result) {
//						System.out.println("got result in killeragent: " +result);
						tr.setSucceeded(result.get("exampleresult").equals("value"));
						ret.setResult(tr);
					}
				});
//				cms.destroyComponent(cid).addResultListener(new IResultListener<Map<String, Object>>() {
//					@Override
//					public void exceptionOccurred(Exception exception) {
//						tr.setFailed(exception);
//						ret.setResult(tr);
//					}
//
//					@Override
//					public void resultAvailable(Map<String, Object> result) {
//						System.out.println("got result in killeragent: " +result);
//						tr.setSucceeded(true);
//						ret.setResult(tr);
//					}
//				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				super.exceptionOccurred(exception);
			}
		});
		
		return ret;
	}
	
	private Future<IComponentManagementService> getCms() {
		Future<IComponentManagementService> ret = new Future<IComponentManagementService>();
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DelegationResultListener<IComponentManagementService>(ret));
		return ret;
	}
}
