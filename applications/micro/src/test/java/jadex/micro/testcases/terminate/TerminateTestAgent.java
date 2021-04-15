package jadex.micro.testcases.terminate;

import java.util.Collection;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.TestAgent;

/**
 *  The invoker agent tests if futures can be terminated
 *  in local and remote cases.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
@Description("The invoker agent tests if futures can be terminated " +
	"in local and remote cases.")
public class TerminateTestAgent extends TestAgent
{
	//-------- attributes --------
	
	@Agent
	protected IInternalAccess agent;

	//-------- methods --------
	
	@Override
	protected IFuture<Void> performTests(Testcase tc)
	{
		tc.setTestCount(4);	
		
		// Enough time for all tests +1
		long	timeout	= Starter.getScaledDefaultTimeout(agent.getId().getRoot(), 1.0/(tc.getTestCount()+1));
		
		final Future<Void>	ret	= new Future<Void>();
		
		testLocal(1, timeout).addResultListener(new IntermediateExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			@Override
			public void intermediateResultAvailable(TestReport result)
			{
				tc.addReport(result);
			}
			
			public void finished()
			{
				testRemote(3, timeout).addResultListener(new ExceptionDelegationResultListener<Collection<TestReport>, Void>(ret)
				{
					public void customResultAvailable(Collection<TestReport> result)
					{
						for(TestReport rep: result)
						{
							tc.addReport(rep);
						}
						
						ret.setResult(null);
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 *  Test if local intermediate results are correctly delivered
	 *  (not as bunch when finished has been called). 
	 */
	protected IFuture<Collection<TestReport>> testLocal(int testno, long delay)
	{
		return performTest(agent.getId().getRoot(), testno, delay);
	}
	
	/**
	 *  Test if remote intermediate results are correctly delivered
	 *  (not as bunch when finished has been called). 
	 */
	protected IFuture<Collection<TestReport>> testRemote(final int testno, final long delay)
	{
		final Future<Collection<TestReport>> ret = new Future<Collection<TestReport>>();
		
		// Start platform
		setupRemotePlatform(false)
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Collection<TestReport>>(ret)
		{
			public void customResultAvailable(final IExternalAccess platform)
			{
				performTest(platform.getId(), testno, delay)
					.addResultListener(new DelegationResultListener<Collection<TestReport>>(ret)
				{
					public void customResultAvailable(final Collection<TestReport> result)
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
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Perform the test. Consists of the following steps:
	 *  - start an agent that offers the service
	 *  - invoke the service
	 *  - wait with intermediate listener for results 
	 */
	protected IIntermediateFuture<TestReport> performTest(final IComponentIdentifier root, final int testno, final long delay)
	{
		final IntermediateFuture<TestReport> ret = new IntermediateFuture<TestReport>();

		// Start service agent
//		IResourceIdentifier	rid	= new ResourceIdentifier(
//			new LocalResourceIdentifier(root, agent.getModel().getResourceIdentifier().getLocalIdentifier().getUri()), null);
		
		createComponent("jadex/micro/testcases/terminate/TerminableProviderAgent.class", root, null)
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Collection<TestReport>>(ret)
		{	
			public void customResultAvailable(final IComponentIdentifier id)
			{
				ret.addResultListener(new IResultListener<Collection<TestReport>>()
				{
					public void resultAvailable(Collection<TestReport> result)
					{
						agent.getExternalAccess(id).killComponent();
					}
					public void exceptionOccurred(Exception exception)
					{
						agent.getExternalAccess(id).killComponent();
					}
				});
				
//						System.out.println("cid is: "+cid);
				agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(ITerminableService.class).setProvider(id))
					.addResultListener(new ExceptionDelegationResultListener<ITerminableService, Collection<TestReport>>(ret)
				{
					public void customResultAvailable(final ITerminableService service)
					{
						testTerminate(testno, service, delay).addResultListener(
							new ExceptionDelegationResultListener<TestReport, Collection<TestReport>>(ret)
						{
							public void customResultAvailable(TestReport result)
							{
								ret.addIntermediateResult(result);
								testTerminateAction(testno+1, service, delay).addResultListener(
									new ExceptionDelegationResultListener<TestReport, Collection<TestReport>>(ret)
								{
									public void customResultAvailable(TestReport result)
									{
										ret.addIntermediateResult(result);
										ret.setFinished();
									}
								});
							}
						});
					}
				});
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Test terminating a future.
	 */
	protected IFuture<TestReport>	testTerminate(int testno, ITerminableService service, long delay)
	{
		System.out.println(agent.getId()+": testTerminate1");
		
		final Future<Void> tmp = new Future<Void>();
		ITerminableFuture<String> fut = service.getResult(delay);
		fut.addResultListener(new IResultListener<String>()
		{
			public void resultAvailable(String result)
			{
				System.out.println(agent.getId()+": testTerminate2");
				tmp.setException(new RuntimeException("Termination did not occur: "+result));
			}
			public void exceptionOccurred(Exception exception)
			{
				System.out.println(agent.getId()+": testTerminate3");
				if(exception instanceof FutureTerminatedException)
				{
					tmp.setResult(null);
				}
				else
				{
					tmp.setException(new RuntimeException("Wrong exception occurred: "+exception));
				}
			}
		});
		fut.terminate();
		System.out.println(agent.getId()+": testTerminate1b");

		final Future<TestReport>	ret	= new Future<TestReport>();
		final TestReport tr = new TestReport("#"+testno, "Tests if terminating future works");
		tmp.addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
		{
			public void customResultAvailable(Void result)
			{
				System.out.println(agent.getId()+": testTerminate4");
				tr.setSucceeded(true);
				ret.setResult(tr);
			}
			public void exceptionOccurred(Exception exception)
			{
				System.out.println(agent.getId()+": testTerminate5");
				tr.setFailed(exception.getMessage());
				ret.setResult(tr);
			}
		});
		return ret;
	}

	
	/**
	 *  Test if terminate action is called.
	 */
	protected IFuture<TestReport>	testTerminateAction(int testno, ITerminableService service, long delay)
	{
		System.out.println(agent.getId()+": testTerminateAction1");
		
		final Future<Void> tmp = new Future<Void>();
		
		final ITerminableFuture<String> fut = service.getResult(delay);
		service.isTerminateCalled().addResultListener(new IntermediateExceptionDelegationResultListener<Void, Void>(tmp)
		{
			public void intermediateResultAvailable(Void result)
			{
				fut.terminate();
			}
			public void customResultAvailable(Collection<Void> result)
			{
				System.out.println(agent.getId()+": testTerminateAction2");
				tmp.setResult(null);
			}
			public void finished()
			{
				System.out.println(agent.getId()+": testTerminateAction3");
				tmp.setResult(null);
			}
		});

		final Future<TestReport>	ret	= new Future<TestReport>();
		final TestReport tr = new TestReport("#"+testno, "Tests if terminating action of future is called");
		tmp.addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
		{
			public void customResultAvailable(Void result)
			{
				System.out.println(agent.getId()+": testTerminateAction4");
				tr.setSucceeded(true);
				ret.setResult(tr);
			}
			public void exceptionOccurred(Exception exception)
			{
				System.out.println(agent.getId()+": testTerminateAction5");
				tr.setFailed(exception.getMessage());
				ret.setResult(tr);
			}
		});
		return ret;
	}
	
	@Override
	public IPlatformConfiguration getConfig()
	{
		return super.getConfig().
			setValue("debugservices", "ITerminableService");
	}
}
