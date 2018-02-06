package jadex.micro.testcases.authenticate;

import java.util.Map;

import org.junit.Ignore;

import jadex.base.test.Testcase;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.testcases.TestAgent;

/**
 *  Agent to test authentication checks for service invocation. 
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="ts", type=ITestService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL))
})
@Ignore // work in progress...
public class AuthenticateTestAgent extends TestAgent
{
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		// Define expected test results for scenarios (un=unrestricted, cus=custom, def=default).
		boolean[][]	tests	= new boolean[][]
		{
			// Annot.:		un		def		cus		cus2	def		cus		un		def
			new boolean[] {true,	false,	false,	false,	false,	false,	true,	false},	
			new boolean[] {true,	true,	false,	false,	true,	false,	true,	true},
			new boolean[] {true,	true,	true,	true,	true,	true,	true,	true}	
		};
		
		return performTest(tc, tests, 0);
	}
	
	/**
	 *  Perform a test with given settings.
	 */
	protected IFuture<Void>	performTest(final Testcase tc, final boolean[][] tests, final int test)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Use expected results for def, cus to decide platform settings
		setupTestPlatform(tests[test][1], tests[test][2])
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
		{
			@Override
			public void customResultAvailable(IExternalAccess platform) throws Exception
			{
				platform.killComponent().addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Void>(ret)
				{
					@Override
					public void customResultAvailable(Map<String, Object> result) throws Exception
					{
						if(test<tests.length)
						{
							performTest(tc, tests, test+1)
								.addResultListener(new DelegationResultListener<Void>(ret));
						}
						else
						{
							ret.setResult(null);
						}
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Start a platform based on given settings.
	 *  @param def	Allow default communication.
	 *  @param cus	Allow successful custom role authentication.
	 */
	protected	IFuture<IExternalAccess> setupTestPlatform(boolean def, boolean cus)
	{
		return null;
	}
	
//	/**
//	 *  Test local.
//	 */
//	protected IFuture<TestReport> testLocal(final int testno)
//	{
//		final Future<TestReport> ret = new Future<TestReport>();
//		
//		final ISecurityService	sec	= SServiceProvider.getLocalService(agent, ISecurityService.class);
//		
//		sec.addRole(agent.getComponentIdentifier().getPlatformPrefix(), "testuser")
//			.addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				performTest(agent.getComponentIdentifier().getRoot(), testno)
//					.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)
//				{
//					public void customResultAvailable(final TestReport result)
//					{
//						sec.removeRole(agent.getComponentIdentifier().getPlatformPrefix(), "testuser").
//							addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
//						{
//							public void customResultAvailable(Void v) throws Exception
//							{
//								ret.setResult(result);
//							}
//						});
//					}
//				}));
//			}
//		});
//		
//		return ret;
//	}
//	
//	/**
//	 *  Test remote.
//	 */
//	protected IFuture<TestReport> testRemote(final int testno, boolean custom, boolean unrestricted)
//	{
//		final Future<TestReport> ret = new Future<TestReport>();
//		
//		//
//		
//		
//		new String[]{"-virtualnames",
//		"jadex.commons.SUtil.createHashMap(new String[]{\"testuser\"}, new Object[]{jadex.commons.SUtil.createHashSet(new String[]{\"testcases\"})})"}
//		
//		createPlatform()
//			.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(
//			new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
//		{
//			public void customResultAvailable(final IExternalAccess platform)
//			{
//				createProxies(platform).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(
//					new ExceptionDelegationResultListener<Void, TestReport>(ret)
//				{
//					public void customResultAvailable(Void result) throws Exception
//					{
//						performTest(platform.getComponentIdentifier(), testno)
//							.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)));
//					}
//				}));
//			}
//		}));
//		
//		return ret;
//	}
//	
//	/**
//	 *  Perform the test. Consists of the following steps:
//	 *  Create provider agent
//	 *  Call methods on it
//	 */
//	protected IFuture<TestReport> performTest(final IComponentIdentifier root, final int testno)
//	{
//		final Future<TestReport> ret = new Future<TestReport>();
//
//		final Future<TestReport> res = new Future<TestReport>();
//		
//		ret.addResultListener(new DelegationResultListener<TestReport>(res)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				TestReport tr = new TestReport("#"+testno, "Tests if authentication works.");
//				tr.setFailed(exception);
//				super.resultAvailable(tr);
//			}
//		});
//		
//		final Future<Map<String, Object>> resfut = new Future<Map<String, Object>>();
//		IResultListener<Map<String, Object>> reslis = new DelegationResultListener<Map<String,Object>>(resfut);
//		
////		System.out.println("root: "+root+" "+SUtil.arrayToString(root.getAddresses()));
//		createComponent("jadex/micro/testcases/authenticate/ProviderAgent.class", root, reslis)
//			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
//		{
//			public void customResultAvailable(final IComponentIdentifier cid) 
//			{
//				callService(cid, testno).addResultListener(new DelegationResultListener<TestReport>(ret));
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				exception.printStackTrace();
//				super.exceptionOccurred(exception);
//			}
//		});
//		
//		return res;
//	}
//	
//	/**
//	 *  Call the service methods.
//	 */
//	protected IFuture<TestReport> callService(final IComponentIdentifier cid, int testno)
//	{
//		final Future<TestReport> ret = new Future<TestReport>();
//		
//		final TestReport tr = new TestReport("#"+testno, "Test if authentication works.");
//		
//		IFuture<ITestService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(ITestService.class, cid);
//		fut.addResultListener(new ExceptionDelegationResultListener<ITestService, TestReport>(ret)
//		{
//			public void customResultAvailable(final ITestService ts)
//			{
//				ts.method("test1").addResultListener(new IResultListener<Void>()
//				{
//					public void resultAvailable(Void result)
//					{
//						tr.setSucceeded(true);
//						ret.setResult(tr);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						tr.setFailed(exception);
//						ret.setResult(tr);
//					}
//				});
//			}
//		});
//		return ret;
//	}
}
