package jadex.micro.testcases.distributedservicepool;

import java.util.Collection;
import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.util.STest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.TestAgent;


@Agent
@Imports(
{
	"jadex.platform.service.distributedservicepool.*",
	"jadex.bridge.service.*",
	"jadex.bridge.service.search.*"
})
@ComponentTypes(
{
	@ComponentType(name="Worker", clazz=WorkerAgent.class),
	@ComponentType(name="DistributedPool", filename = "jadex/platform/service/distributedservicepool/DistributedServicePoolAgent.class")
})
@Configurations(
{
	@Configuration(name="pool", components={
		//@Component(type="Worker"),
		@Component(type="DistributedPool", arguments = 
		{
			@NameValue(name="serviceinfo",
				value="new ServiceQuery(ITestService.class).setScope(ServiceScope.GLOBAL)"),
			@NameValue(name="checkdelay", value="4000l")
		})
	})
})
@Service
@Results(@Result(name="testresults", description= "The test results.", clazz=Testcase.class))
public class PoolTestAgent extends TestAgent
{
	protected int tstcnt = 1;
	
	/**
	 *  The test count.
	 */
	protected int getTestCount()
	{
		return tstcnt*2;
	}
	
	/**
     * Returns the platform config.
     * Can be overridden to apply special settings. 
     */
    public IPlatformConfiguration getConfig() 
    {
    	// It is important to turn off simulation also for local test!
    	// The distri pool will search globally using intermediate future which
    	// is not supported by the simulation blockers
        return STest.getDefaultTestConfig(getClass()).getExtendedPlatformConfiguration().setSimulation(false);
    }
	
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
//		agent.getLogger().severe("Testagent test local: "+agent.getDescription());
		testLocal(tstcnt).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IntermediateExceptionDelegationResultListener<TestReport, Void>(ret)
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
//				agent.getLogger().severe("Testagent test rmeote: "+agent.getDescription());
				testRemote(tstcnt).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IntermediateExceptionDelegationResultListener<TestReport, Void>(ret)
				{
					public void customResultAvailable(Collection<TestReport> result)
					{
						for(TestReport tr: result)
							tc.addReport(tr);
						ret.setResult(null);
					}
					
					public void finished()
					{
//						agent.getLogger().severe("Testagent tests finished: "+agent.getDescription());
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
		//System.out.println("test local");
		
		final IntermediateFuture<TestReport> ret = new IntermediateFuture<TestReport>();
		
		performTests(agent.getId().getRoot(), testno)
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IntermediateDelegationResultListener<TestReport>(ret)));
		
		return ret;
	}
	
	/**
	 *  Test remote.
	 */
	protected IIntermediateFuture<TestReport> testRemote(final int testno)
	{
		//System.out.println("test remote");
		
		final IntermediateFuture<TestReport> ret = new IntermediateFuture<TestReport>();
		
		setupRemotePlatform(false).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Collection<TestReport>>(ret)
		{
			public void customResultAvailable(final IExternalAccess exta)
			{
				performTests(exta.getId(), testno)
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
	protected IIntermediateFuture<TestReport> performTests(final IComponentIdentifier root, final int testno)
	{
		//System.out.println("performTests");
		
		final IntermediateFuture<TestReport> ret = new IntermediateFuture<TestReport>();

		final Future<Map<String, Object>> resfut = new Future<Map<String, Object>>();
		IResultListener<Map<String, Object>> reslis = new DelegationResultListener<Map<String,Object>>(resfut);
		
//		System.out.println("root: "+root+" "+SUtil.arrayToString(root.getAddresses()));
		createComponent(WorkerAgent.class.getName()+".class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Collection<TestReport>>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				doPerformTests(testno).addResultListener(new IntermediateDelegationResultListener<TestReport>(ret)
				{
					public void exceptionOccurred(Exception exception)
					{
						TestReport tr = new TestReport("#"+testno, "Test .");
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
	 *  Perform the tests. 
	 */
	protected IIntermediateFuture<TestReport> doPerformTests(int testno)
	{
		//System.out.println("doPerformTests");
		
		IntermediateFuture<TestReport> ret = new IntermediateFuture<TestReport>();
		
		final TestReport tr = new TestReport("#"+testno, "Test if pool revives broken service");
		
		Collection<ITestService> sers = agent.searchServices(new ServiceQuery<ITestService>(ITestService.class)).get();
		
		// todo: how to identify pool or worker (tagging workers or tagging pools)
		ITestService ser = null;
		for(ITestService s: sers)
		{
			if(((IService)s).getServiceId().toString().indexOf("Distributed")!=-1)
			{
				ser = s;
				break;
			}
		}
		
		try
		{
			ser.ok().get();
		}
		catch(Exception e)
		{
			System.out.println("got exception on ok: "+e);
		}
		
		for(int i=0; i<3; i++)
		{
			try
			{
				ser.ex().get();
			}
			catch(Exception e)
			{
				System.out.println("got exception on ex: "+e);
			}
		}
		
		for(int i=0; i<5; i++)
		{
			try
			{
				ser.ok().get();
				tr.setSucceeded(true);
				break;
			}
			catch(Exception e)
			{
				System.out.println("got exception on ok2: "+e);
			}
			agent.waitForDelay(5000).get();
		}
		
		if(!tr.isFinished())
			tr.setFailed("Not revived service");
		
		ret.addIntermediateResult(tr);
		ret.setFinished();
			
		System.out.println("XXXXXX test fini: "+tr);
		
		return ret;
	}
	
	public static void main(String[] args)
	{
		//new RequiredServiceInfo(ITestService.class).setDefaultBinding(new RequiredServiceBinding(ServiceScope.APPLICATION_GLOBAL));
	
		IExternalAccess platform = Starter.createPlatform(PlatformConfigurationHandler.getDefault()).get();
		CreationInfo ci = new CreationInfo().setFilenameClass(PoolTestAgent.class);
		platform.createComponent(ci).get();
	}
	
	/*@OnEnd
	public void end(Exception e)
	{
		e.printStackTrace();
		System.out.println("end");
	}*/
}
