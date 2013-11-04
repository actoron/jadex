package jadex.micro.testcases.nflatency;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFRPropertyProvider;
import jadex.bridge.nonfunctional.annotation.NFRProperty;
import jadex.bridge.sensor.service.LatencyProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.MethodInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.ICommandFuture.Type;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.TupleResult;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.testcases.TestAgent;

import java.util.Collection;
import java.util.Map;

/**
 *  Tests if non-functional properties can be changed and passed back
 *  from the receiver to the sender side of a service invocation.
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="ts", type=ITestService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL)),
	@RequiredService(name="aser", type=ITestService.class, multiple=true,
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL, dynamic=true),
		nfprops=@NFRProperty(value=LatencyProperty.class, methodname="methodA", methodparametertypes=long.class))
})
public class InitiatorAgent extends TestAgent
{
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
		testLocal(1).addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
				testRemote(2).addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
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
	protected IFuture<TestReport> testLocal(final int testno)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		performTest(agent.getComponentIdentifier().getRoot(), testno, true)
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
	protected IFuture<TestReport> testRemote(final int testno)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		createPlatform(null).addResultListener(agent.createResultListener(
			new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{
			public void customResultAvailable(final IExternalAccess platform)
			{
				// Hack: announce platform immediately
				IFuture<IComponentManagementService> fut = agent.getRequiredService("cms");
				fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, TestReport>(ret)
				{
					public void customResultAvailable(IComponentManagementService cms)
					{
						CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[]{"component"}, new Object[]{platform.getComponentIdentifier().getRoot()}));
						cms.createComponent("jadex.platform.service.remote.ProxyAgent.class", ci).addResultListener(
							new Tuple2Listener<IComponentIdentifier, Map<String, Object>>()
//							new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
						{
							public void firstResultAvailable(IComponentIdentifier result)
							{
								performTest(platform.getComponentIdentifier(), testno, false)
									.addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
								{
									public void customResultAvailable(final TestReport result)
									{
										platform.killComponent();
	//										.addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, TestReport>(ret)
	//									{
	//										public void customResultAvailable(Map<String, Object> v)
	//										{
	//											ret.setResult(result);
	//										}
	//									});
										ret.setResult(result);
									}
								}));
							}
							public void secondResultAvailable(Map<String,Object> result) 
							{
							}
							public void exceptionOccurred(Exception exception)
							{
								ret.setException(exception);
							}
						});
					}
				});
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
				tr.setReason(exception.getMessage());
				super.resultAvailable(tr);
			}
		});
		
		final Future<Collection<Tuple2<String, Object>>> resfut = new Future<Collection<Tuple2<String, Object>>>();
		IResultListener<Collection<Tuple2<String, Object>>> reslis = new DelegationResultListener<Collection<Tuple2<String,Object>>>(resfut);
		
//		System.out.println("root: "+root+" "+SUtil.arrayToString(root.getAddresses()));
		createComponent(ProviderAgent.class.getName()+".class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
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
	protected IFuture<TestReport> callService(final IComponentIdentifier cid, int testno, final long to)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final TestReport tr = new TestReport("#"+testno, "Test if returning changed nf props works");
		
//		IFuture<ITestService> fut = agent.getServiceContainer().getService(ITestService.class, cid);
		
		// Add awarenessinfo for remote platform
//		IAwarenessManagementService awa = SServiceProvider.getService(agent.getServiceProvider(), IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
//		AwarenessInfo info = new AwarenessInfo(cid.getRoot(), AwarenessInfo.STATE_ONLINE, -1, 
//			null, null, null, SReflect.getInnerClassName(this.getClass()));
//		awa.addAwarenessInfo(info).get();
		
		IIntermediateFuture<ITestService> fut = agent.getRequiredServices("aser");
		fut.addResultListener(new IIntermediateResultListener<ITestService>()
		{
			boolean called;
			public void intermediateResultAvailable(ITestService result)
			{
				if(cid.equals(((IService)result).getServiceIdentifier().getProviderId()))
				{
					called = true;
					callService(result);
				}
			}
			public void finished()
			{
				if(!called)
				{
					tr.setFailed("Service not found");
					ret.setResult(tr);
				}
			}
			public void resultAvailable(Collection<ITestService> result)
			{
				for(ITestService ts: result)
				{
					intermediateResultAvailable(ts);
				}
				finished();
			}
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
			
			protected void callService(final ITestService ts)
			{
				ts.methodA(100).addResultListener(new IFutureCommandResultListener<Void>()
//				ts.methodA(100).addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						try
						{
							MethodInfo mi = new MethodInfo(ITestService.class.getMethod("methodA", new Class[]{long.class}));
							INFMixedPropertyProvider pp = ((INFRPropertyProvider)ts).getRequiredServicePropertyProvider().get();
							Long lat = (Long)pp.getMethodNFPropertyValue(mi, LatencyProperty.NAME).get();
							System.out.println("latency: "+lat);
							// Test is ok if latency could be fetched.
							// todo? Could also test if local latency is faster than remote
							tr.setSucceeded(true);
							ret.setResult(tr);
						}
						catch(Exception e)
						{
							e.printStackTrace();
							ret.setExceptionIfUndone(e);
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						tr.setFailed("Failed with exception: "+exception);
						ret.setResult(tr);
					}
					
					public void commandAvailable(Type command)
					{
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Hack class that avoids printouts of forward command
	 */
	abstract class Tuple2Listener<T, E> extends DefaultTuple2ResultListener<T, E> implements IFutureCommandResultListener<Collection<TupleResult>>
	{
		public void commandAvailable(Type command)
		{
			// nop, avoids printouts
		}
	}
}
