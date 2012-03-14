package jadex.micro.testcases.terminate;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.remote.RemoteException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.Collection;

/**
 *  The intermediate invoker agent tests if intermediate futures can be terminated
 *  in local and remote cases.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
@Description("The intermediate invoker agent tests if intermediate futures can be terminated " +
	"in local and remote cases.")
public class IntermediateInvokerAgent
{
	//-------- attributes --------
	
	@Agent
	protected MicroAgent agent;

	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		final Testcase tc = new Testcase();
		tc.setTestCount(2);
		
		final Future<TestReport> ret = new Future<TestReport>();
		ret.addResultListener(agent.createResultListener(new IResultListener<TestReport>()
		{
			public void resultAvailable(TestReport result)
			{
				System.out.println("tests finished: "+tc.isSucceeded());

				agent.setResultValue("testresults", tc);
				agent.killAgent();				
			}
			public void exceptionOccurred(Exception exception)
			{
				agent.setResultValue("testresults", tc);
				agent.killAgent();	
			}
		}));
			
//		testLocal(1, 1000, 3).addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
//		{
//			public void customResultAvailable(TestReport result)
//			{
//				tc.addReport(result);
//				ret.setResult(null);
//			}
//		}));
		
//		testRemote(1, 1000, 3).addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
//		{
//			public void customResultAvailable(TestReport result)
//			{
//				tc.addReport(result);
//				ret.setResult(null);
//			}
//		}));
		
		testLocal(1, 100, 3).addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
//				ret.setResult(null);
				testRemote(2, 100, 3).addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
				{
					public void customResultAvailable(TestReport result)
					{
						tc.addReport(result);
						ret.setResult(null);
					}
				}));
			}
		}));
	}
	
	/**
	 *  Test if local intermediate results are correctly delivered
	 *  (not as bunch when finished has been called). 
	 */
	protected IFuture<TestReport> testLocal(int testno, long delay, int max)
	{
		return performTest(agent.getServiceProvider(), agent.getComponentIdentifier().getRoot(), testno, delay, max);
	}
	
	/**
	 *  Test if remote intermediate results are correctly delivered
	 *  (not as bunch when finished has been called). 
	 */
	protected IFuture<TestReport> testRemote(final int testno, final long delay, final int max)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		// Start platform
		String url	= "new String[]{\"../jadex-applications-micro/target/classes\"}";	// Todo: support RID for all loaded models.
//		String url	= process.getModel().getResourceIdentifier().getLocalIdentifier().getUrl().toString();
		Starter.createPlatform(new String[]{"-platformname", "testi_1", "-libpath", url,
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-awareness", "false",
//			"-logging_level", "java.util.logging.Level.INFO",
			"-gui", "false", "-usepass", "false", "-simulation", "false"
		}).addResultListener(agent.createResultListener(
			new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{
			public void customResultAvailable(final IExternalAccess platform)
			{
				performTest(platform.getServiceProvider(), platform.getComponentIdentifier(), testno, delay, max)
					.addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
				{
					public void customResultAvailable(final TestReport result)
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
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Perform the test. Consists of the following steps:
	 *  - start an agent that offers the service
	 *  - invoke the service
	 *  - wait with intermediate listener for results 
	 */
	protected IFuture<TestReport> performTest(final IServiceProvider provider, final IComponentIdentifier root, final int testno, final long delay, final int max)
	{
		final Future<TestReport> ret = new Future<TestReport>();

		final Future<TestReport> res = new Future<TestReport>();
		
		final TestReport tr = new TestReport("#"+testno, "Tests if terminating future works");
		
		ret.addResultListener(new DelegationResultListener<TestReport>(res)
		{
			public void exceptionOccurred(Exception exception)
			{
				tr.setReason(exception.getMessage());
				super.resultAvailable(tr);
			}
		});
		
		// Start service agent
		IFuture<IComponentManagementService> fut = SServiceProvider.getServiceUpwards(
			provider, IComponentManagementService.class);
		fut.addResultListener(agent.createResultListener(
			new ExceptionDelegationResultListener<IComponentManagementService, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				IResourceIdentifier	rid	= new ResourceIdentifier(
					new LocalResourceIdentifier(root, agent.getModel().getResourceIdentifier().getLocalIdentifier().getUrl()), null);
				
				cms.createComponent(null, "jadex/micro/testcases/terminate/TerminableProviderAgent.class", new CreationInfo(rid), null)
					.addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
				{	
					public void customResultAvailable(final IComponentIdentifier cid)
					{
//						System.out.println("cid is: "+cid);
						SServiceProvider.getService(agent.getServiceProvider(), cid, ITerminableService.class)
							.addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<ITerminableService, TestReport>(ret)
						{
							public void customResultAvailable(ITerminableService service)
							{
								// Invoke service agent
//								System.out.println("Invoking");
								final ITerminableIntermediateFuture<String> fut = service.getResults(delay, max);
								fut.addResultListener(agent.createResultListener(new IIntermediateResultListener<String>()
								{
									public void resultAvailable(Collection<String> result)
									{
//										System.out.println("resultAvailable: "+result);
										cms.destroyComponent(cid);
										tr.setReason("Termination did not occur: "+result);
										ret.setResult(tr);
									}
									public void intermediateResultAvailable(String result)
									{
										System.out.println("intermediate result: "+result);
									}
									public void finished()
									{
										cms.destroyComponent(cid);
										tr.setReason("Termination did not occur.");
										ret.setResult(tr);
									}
									public void exceptionOccurred(Exception exception)
									{
//										System.out.println("exceptionOccurred: "+exception);
										cms.destroyComponent(cid);
										if(exception instanceof FutureTerminatedException || 
											(exception instanceof RemoteException && ((RemoteException)exception).getType().equals(FutureTerminatedException.class)))
										{
											tr.setSucceeded(true);
										}
										else
										{
											tr.setFailed("Wrong exception occurred: "+exception);
										}
										ret.setResult(tr);
									}
								}));
								
								agent.waitFor(delay*(max-1)+delay/2, new IComponentStep<Void>()
								{
									public IFuture<Void> execute(IInternalAccess ia)
									{
										fut.terminate();
										return IFuture.DONE;
									}
								});
								
//								System.out.println("Added listener");
							}		
						}));
					}
				}));
			}	
		}));
		
		return res;
	}
}
