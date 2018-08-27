package jadex.platform.service.globalservicepool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NFRProperty;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.sensor.service.LatencyProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.TupleResult;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.TestAgent;
import jadex.platform.service.servicepool.PoolServiceInfo;

/**
 * 
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="ts", type=ITestService.class, scope=RequiredServiceInfo.SCOPE_GLOBAL),
	@RequiredService(name="aser", type=ITestService.class, multiple=true, scope=RequiredServiceInfo.SCOPE_GLOBAL,
		nfprops=@NFRProperty(value=LatencyProperty.class, methodname="methodA", methodparametertypes=long.class))
})
// Test requires starting/stopping multiple platforms and many test calls  -> increase test timeout
@Properties(
	@NameValue(name="test.timeout", value="jadex.base.Starter.getScaledDefaultTimeout(null, 2)"))
public class InitiatorAgent extends TestAgent
{
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
		testLocal(1).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);

				testRemote(2).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
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
		
		final List<IExternalAccess> pls = new ArrayList<IExternalAccess>();
		pls.add(agent.getExternalAccess());
		setupRemotePlatforms(4, 0, pls).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret) 
		{
			public void customResultAvailable(Void result) 
			{
				performTest(agent.getId(), testno, true)
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Test remote.
	 */
	protected IFuture<TestReport> testRemote(final int testno)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final List<IExternalAccess> pls = new ArrayList<IExternalAccess>();
		pls.add(agent.getExternalAccess());
		setupRemotePlatforms(4, 0, pls).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret) 
		{
			public void customResultAvailable(Void result) 
			{
				performTest(pls.get(1).getId(), testno, false)
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)));
			}
		});
		
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
				TestReport tr = new TestReport("#"+testno, "Test #"+testno+" if nflatency works.");
				tr.setFailed(exception);
				super.resultAvailable(tr);
			}
		});
		
		final Future<Collection<Tuple2<String, Object>>> resfut = new Future<Collection<Tuple2<String, Object>>>();
		IResultListener<Collection<Tuple2<String, Object>>> reslis = new DelegationResultListener<Collection<Tuple2<String,Object>>>(resfut);

		Map<String, Object> args = new HashMap<String, Object>();
		args.put("serviceinfos", new PoolServiceInfo[]{new PoolServiceInfo(WorkerAgent.class.getName()+".class", ITestService.class)});
		createComponent(GlobalServicePoolAgent.class.getName()+".class", args, null, root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
//				System.err.println("-------------+++++++++++++--------------- created global service pool #"+testno);
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
	protected IFuture<TestReport> callService(final IComponentIdentifier cid, final int testno, final long to)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final TestReport tr = new TestReport("#"+testno, "Test if returning changed nf props works");
		
//		IFuture<ITestService> fut = agent.getServiceContainer().getService(ITestService.class, cid);
		
		// Add awarenessinfo for remote platform
//		IAwarenessManagementService awa = agent.getServiceProvider().searchService( new ServiceQuery<>( IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
//		AwarenessInfo info = new AwarenessInfo(cid.getRoot(), AwarenessInfo.STATE_ONLINE, -1, 
//			null, null, null, SReflect.getInnerClassName(this.getClass()));
//		awa.addAwarenessInfo(info).get();
		
		IIntermediateFuture<ITestService> fut = agent.getFeature(IRequiredServicesFeature.class).getServices("aser");
		fut.addResultListener(new IIntermediateResultListener<ITestService>()
		{
			boolean called;
			public void intermediateResultAvailable(ITestService result)
			{
				System.out.println("found: "+((IService)result).getId());
//				System.err.println("-------------+++++++++++++--------------- found #"+testno+", "+result);
				if(cid.equals(((IService)result).getId().getProviderId()))
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
				int cnt = 101;
				
				CounterResultListener<Void> lis = new CounterResultListener<Void>(cnt, new ExceptionDelegationResultListener<Void, TestReport>(ret)
				{
					public void customResultAvailable(Void result) 
					{
						if(tr.getReason()==null)
							tr.setSucceeded(true);
						ret.setResult(tr);
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
//						System.err.println("-------------+++++++++++++--------------- call returned with exception #"+testno+", "+exception);
						super.exceptionOccurred(exception);
					}
				});
				
				ts.methodA(0).addResultListener(lis);
				
				for(int i=0; i<10; i++)
				{
					for(int j=0; j<10; j++)
					{
						ts.methodA(i*10+j).addResultListener(lis);
					}
					
					agent.getFeature(IExecutionFeature.class).waitForDelay(10000).get();
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Hack class that avoids printouts of forward command
	 */
	abstract class Tuple2Listener<T, E> extends DefaultTuple2ResultListener<T, E> implements IFutureCommandResultListener<Collection<TupleResult>>
	{
		public void commandAvailable(Object command)
		{
			// nop, avoids printouts
		}
	}
}
