package jadex.micro.testcases.maxresults;

import java.util.Collection;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.TestAgent;

@Agent
@Service
@RequiredServices(@RequiredService(name="testser", type=ITestService.class, scope=ServiceScope.GLOBAL))
//@ComponentTypes(@ComponentType(name="provider", filename="jadex.micro.testcases.maxresults.ProviderAgent.class"))
//@Configurations(@Configuration(name="default", components=@Component(type="provider")))
@Results(@Result(name="testresults", description= "The test results.", clazz=Testcase.class))
public class UserAgent extends TestAgent
{
	protected int tstcnt = 2;
	
	/**
	 *  The test count.
	 */
	protected int getTestCount()
	{
		return tstcnt*2;
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
		createComponent(ProviderAgent.class.getName()+".class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Collection<TestReport>>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				doPerformTests(testno).addResultListener(new IntermediateDelegationResultListener<TestReport>(ret)
				{
					public void exceptionOccurred(Exception exception)
					{
						TestReport tr = new TestReport("#"+testno, "Tests if max works.");
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
		
		ITestService ser = (ITestService)agent.getFeature(IRequiredServicesFeature.class).getService("testser").get();
		
		Future<Void> barrier = new Future<>();
		int[] cnt = new int[1];
		
		IIntermediateFuture<String> fut1 = ser.getInfos();
		fut1.addResultListener(new IIntermediateResultListener<String>() 
		{
			final TestReport tr = new TestReport("#"+testno, "Test if intermediate future max works");
			int max = -1;
			int maxcnt;
			
			public void exceptionOccurred(Exception exception) 
			{
				System.out.println("ex: "+exception);
			}
			
			public void resultAvailable(Collection<String> result) 
			{
				System.out.println("result: "+result);
			}
			
			public void maxResultCountAvailable(int max) 
			{
				System.out.println("max rec: "+max);
				this.max = max;
				this.maxcnt++;
			}
			
			public void intermediateResultAvailable(String result) 
			{
				//System.out.println("ires: "+result);
			}
			
			public void finished() 
			{
				System.out.println("fini 1");
				
				if(this.max!=-1 && this.maxcnt==1)
				{
					tr.setSucceeded(true);
				}
				else if(this.max==-1)
				{
					tr.setFailed("No max value received.");
				}
				else
				{
					tr.setFailed("Received max value n times: "+this.maxcnt);
				}
				ret.addIntermediateResult(tr);
			
				if(++cnt[0]==2)
					barrier.setResult(null);
			}
		});
		
		IIntermediateFuture<String> fut2 = ser.subscribeToInfos();
		fut2.addResultListener(new IIntermediateResultListener<String>() 
		{
			final TestReport tr = new TestReport("#"+testno+1, "Test if subscription future max works");
			int max = -1;
			int maxcnt;
			
			public void exceptionOccurred(Exception exception) 
			{
				System.out.println("ex: "+exception);
			}
			
			public void resultAvailable(Collection<String> result) 
			{
				System.out.println("result: "+result);
			}
			
			public void maxResultCountAvailable(int max) 
			{
				System.out.println("max rec: "+max);
				this.max = max;
				this.maxcnt++;
			}
			
			public void intermediateResultAvailable(String result) 
			{
				//System.out.println("ires: "+result);
			}
			
			public void finished() 
			{
				System.out.println("fini 2");
				
				if(this.max!=-1 && this.maxcnt==1)
				{
					tr.setSucceeded(true);
				}
				else if(this.max==-1)
				{
					tr.setFailed("No max value received.");
				}
				else
				{
					tr.setFailed("Received max value n times: "+this.maxcnt);
				}
				ret.addIntermediateResult(tr);
				
				if(++cnt[0]==2)
					barrier.setResult(null);
			}
		});
		
		barrier.get();
		ret.setFinished();
		
		return ret;
	}
			
}
