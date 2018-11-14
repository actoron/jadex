package jadex.micro.testcases.killcomponent;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.IExecutionFeature;
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
public class KillComponentTestAgent extends TestAgent
{
	/**
	 * Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		tc.setTestCount(2);
		final Future<Void> ret = new Future<Void>();

		agent.getLogger().severe("Testagent test local: " + agent.getDescription());
		testLocal(1).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				agent.getLogger().severe("Testagent test remote: " + agent.getDescription());
				tc.addReport(result);
				// ret.setResult(null);
				testRemote(2).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
				{
					public void customResultAvailable(TestReport result)
					{
						agent.getLogger().severe("Testagent tests finished: " + agent.getDescription());
						tc.addReport(result);
						ret.setResult(null);
					}
				}));
			}
		}));

		return ret;
	}

	@Override
	protected int getTestCount()
	{
		return 2;
	}

	/**
	 * Test local.
	 */
	protected IFuture<TestReport> testLocal(final int testno)
	{
		final Future<TestReport> ret = new Future<TestReport>();

		performTest(agent.getId().getRoot(), testno, true).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)
		{
			public void customResultAvailable(final TestReport result)
			{
				ret.setResult(result);
			}
		}));

		return ret;
	}

	/**
	 * Test remote.
	 */
	protected IFuture<TestReport> testRemote(final int testno)
	{
		final Future<TestReport> ret = new Future<TestReport>();

		setupRemotePlatform(false).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{
			public void customResultAvailable(final IExternalAccess platform)
			{
				performTest(platform.getId(), testno, false).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)));
			}
		}));

		return ret;
	}

	/**
	 * Perform the test. Consists of the following steps: Create provider agent
	 * kill it
	 */
	protected IFuture<TestReport> performTest(final IComponentIdentifier root, final int testno, final boolean hassectrans)
	{
		final Future<TestReport> ret = new Future<TestReport>();

		final Future<Map<String, Object>> resfut = new Future<Map<String, Object>>();
		IResultListener<Map<String, Object>> reslis = new DelegationResultListener<Map<String, Object>>(resfut);

		agent.getLogger().severe("Testagent create provider: " + agent.getDescription());
		createComponent(ProviderAgent.class.getName() + ".class", root, reslis).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid)
			{
				agent.getLogger().severe("Testagent create provider done: " + agent.getDescription());
				IExternalAccess exta = agent.getExternalAccess(cid).get();
				final TestReport tr = new TestReport("#" + testno, "Test if kill returns result");
				System.out.println("Killing my subcomponent...");
				exta.killComponent().addResultListener(new IResultListener<Map<String, Object>>()
				{
					@Override
					public void exceptionOccurred(Exception exception)
					{
						tr.setFailed(exception);
						ret.setResult(tr);
					}

					@Override
					public void resultAvailable(Map<String, Object> result)
					{
						System.out.println("got result in killeragent: " + result);
						tr.setSucceeded(("value").equals(result.get("exampleresult")));
						ret.setResult(tr);
					}
				});
				// cms.destroyComponent(cid).addResultListener(new
				// IResultListener<Map<String, Object>>() {
				// @Override
				// public void exceptionOccurred(Exception exception) {
				// tr.setFailed(exception);
				// ret.setResult(tr);
				// }
				//
				// @Override
				// public void resultAvailable(Map<String, Object> result) {
				// System.out.println("got result in killeragent: " +result);
				// tr.setSucceeded(true);
				// ret.setResult(tr);
				// }
				// });
			}

			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				super.exceptionOccurred(exception);
			}
		});

		return ret;
	}

	// private Future<IComponentManagementService> getCms() {
	// Future<IComponentManagementService> ret = new
	// Future<IComponentManagementService>();
	// agent.getFeature(IRequiredServicesFeature.class).searchService(new
	// ServiceQuery<>(IComponentManagementService.class,
	// RequiredServiceInfo.SCOPE_PLATFORM))
	// .addResultListener(new
	// DelegationResultListener<IComponentManagementService>(ret));
	// return ret;
	// }
}
