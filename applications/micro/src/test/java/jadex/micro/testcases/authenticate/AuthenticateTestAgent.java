package jadex.micro.testcases.authenticate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import jadex.base.IPlatformConfiguration;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.SUtil;
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
 *  Agent to test authentication checks for service invocation. 
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="ts", type=ITestService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL))
})
@Properties({@NameValue(name=Testcase.PROPERTY_TEST_TIMEOUT, value="jadex.base.Starter.getScaledRemoteDefaultTimeout(null, 4)")}) // cannot use $component.getId() because is extracted from test suite :-(
public class AuthenticateTestAgent extends TestAgent
{
	//-------- test settings --------
	
	// Define expected test results for scenarios (un=unrestricted, cus=custom, def=default).
	protected static boolean[][]	tests	= new boolean[][]
	{
		// Annot.:		un		def		cus		cus2	def		cus		un		def
		new boolean[] {true,	false,	false,	false,	false,	false,	true,	false},
		new boolean[] {true,	true,	false,	false,	true,	false,	true,	true},
		new boolean[] {true,	true,	true,	true,	true,	true,	true,	true}
	};

	@Override
	protected int getTestCount()
	{
		return tests.length;
	}
	
	//-------- test execution --------
	
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
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
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
		{
			@Override
			public void customResultAvailable(final IExternalAccess platform) throws Exception
			{
				invokeServices().addResultListener(new ExceptionDelegationResultListener<boolean[], Void>(ret)
				{
					@Override
					public void customResultAvailable(boolean[] result) throws Exception
					{
						tc.addReport(new TestReport("#"+test, "Test security checks of service invocations ("+test+")", 
							Arrays.equals(tests[test], result), "Expected "+Arrays.toString(tests[test])+" but was "+Arrays.toString(result)));
						
						platform.killComponent().addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Void>(ret)
						{
							@Override
							public void customResultAvailable(Map<String, Object> result) throws Exception
							{
								if(test<tests.length-1)
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
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Start a platform based on given settings.
	 *  @param def	Allow default communication.
	 *  @param cus	Allow successful custom role authentication.
	 */
	protected	IFuture<IExternalAccess> setupTestPlatform(boolean def, final boolean cus)
	{
		IPlatformConfiguration	conf	= STest.getDefaultTestConfig();
		// use different platform name / key etc.
		conf.setPlatformName("other_*");
		conf.setValue("settings.readonly", Boolean.TRUE);	// Do not save settings (hack!!! security isn't read from config, when settings file exists)
		
		// Not default visibility means test unrestricted access -> don't use test network.
		if(!def)
		{
			conf.setNetworkNames((String[]) null);
			conf.setNetworkSecrets((String[])null);
		}
		
		// Add agents.
		conf.addComponent(BasicProviderAgent.class);
		conf.addComponent(OverridingProviderAgent.class);
		
		final Future<IExternalAccess>	ret	= new Future<IExternalAccess>();
		createPlatform(conf, null)
			.addResultListener(new DelegationResultListener<IExternalAccess>(ret)
		{
			@Override
			public void customResultAvailable(final IExternalAccess exta)
			{
				createProxies(exta).addResultListener(new ExceptionDelegationResultListener<Void, IExternalAccess>(ret)
				{
					@Override
					public void customResultAvailable(Void result) throws Exception
					{
						// Access with custom roles should work -> add roles to new platform.
						if(cus)
						{
							exta.searchService( new ServiceQuery<>( ISecurityService.class))
								.addResultListener(new ExceptionDelegationResultListener<ISecurityService, IExternalAccess>(ret)
							{
								@Override
								public void customResultAvailable(ISecurityService result) throws Exception
								{
									result.addRole(STest.testnetwork_name, "custom")
										.addResultListener(new ExceptionDelegationResultListener<Void, IExternalAccess>(ret)
									{
										@Override
										public void customResultAvailable(Void result) throws Exception
										{
											ret.setResult(exta);
										}
									});
								}
							});
						}
						else
						{
							ret.setResult(exta);
						}
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Search and invoke services and return the success flags.
	 */
	protected IFuture<boolean[]>	invokeServices()
	{
		final Future<boolean[]>	ret	= new Future<boolean[]>();
//		System.out.println("invokeServices "+IComponentIdentifier.LOCAL.get());
		
		agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(ITestService.class, Binding.SCOPE_GLOBAL))
			.addResultListener(new ExceptionDelegationResultListener<Collection<ITestService>, boolean[]>(ret)
		{
			@Override
			public void customResultAvailable(Collection<ITestService> result) throws Exception
			{
				if(result.size()!=2)
				{
					ret.setException(new RuntimeException("Found wrong services: "+result));
				}
				else
				{
					// Sort results by toString -->  Basic... goes first, then Overriding...
					Collection<ITestService>	sorted	= new TreeSet<ITestService>(new Comparator<ITestService>()
					{
						@Override
						public int compare(ITestService o1, ITestService o2)
						{
							return o1.toString().compareTo(o2.toString());
						}
					});
					sorted.addAll(result);
					System.out.println("Sorted services: "+sorted);
					
					final Iterator<ITestService>	it	= sorted.iterator();
					invokeService(it.next())
						.addResultListener(new DelegationResultListener<boolean[]>(ret)
					{
						@Override
						public void customResultAvailable(final boolean[] result1)
						{
							invokeService(it.next())
								.addResultListener(new DelegationResultListener<boolean[]>(ret)
							{
								@Override
								public void customResultAvailable(boolean[] result2)
								{
									ret.setResult((boolean[])SUtil.joinArrays(result1, result2));
								}
							});
						}
					});
				}
			}
		});
		return ret;
	}
	
	/**
	 *  Invoke one service.
	 */
	protected IFuture<boolean[]>	invokeService(final ITestService ts)
	{
		final Future<boolean[]>	fret	= new Future<boolean[]>();
		final boolean[]	ret	= new boolean[4];
		ts.unrestrictedMethod().addResultListener(new IResultListener<Void>()
		{			
			@Override
			public void resultAvailable(Void result)
			{
				proceed(true);
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				proceed(false);
			}
			
			private void proceed(boolean val)
			{
				ret[0]	= val;
				ts.defaultMethod().addResultListener(new IResultListener<Void>()
				{			
					@Override
					public void resultAvailable(Void result)
					{
						proceed(true);
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						proceed(false);
					}
					
					private void proceed(boolean val)
					{
						ret[1]	= val;
						ts.customMethod().addResultListener(new IResultListener<Void>()
						{			
							@Override
							public void resultAvailable(Void result)
							{
								proceed(true);
							}
							
							@Override
							public void exceptionOccurred(Exception exception)
							{
								proceed(false);
							}
							
							private void proceed(boolean val)
							{
								ret[2]	= val;
								ts.custom1Method().addResultListener(new IResultListener<Void>()
								{			
									@Override
									public void resultAvailable(Void result)
									{
										proceed(true);
									}
									
									@Override
									public void exceptionOccurred(Exception exception)
									{
										proceed(false);
									}
									
									private void proceed(boolean val)
									{
										ret[3]	= val;
										fret.setResult(ret);
									}
								});
							}
						});

					}
				});

			}
		});
		return fret;
	}
	
//	/**
//	 *  Test local.
//	 */
//	protected IFuture<TestReport> testLocal(final int testno)
//	{
//		final Future<TestReport> ret = new Future<TestReport>();
//		
//		final ISecurityService	sec	= agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( ISecurityService.class));
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
