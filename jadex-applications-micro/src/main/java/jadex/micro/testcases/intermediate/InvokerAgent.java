package jadex.micro.testcases.intermediate;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.Collection;

@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class InvokerAgent
{
	@Agent
	protected MicroAgent agent;
	
	@AgentBody
	public void body()
	{
		final Testcase tc = new Testcase();
		tc.setTestCount(2);
		
		final Future<TestReport> ret = new Future<TestReport>();
		ret.addResultListener(new IResultListener<TestReport>()
		{
			public void resultAvailable(TestReport result)
			{
				agent.setResultValue("testresults", tc);
				System.out.println("tests finished");
			}
			public void exceptionOccurred(Exception exception)
			{
				agent.setResultValue("testresults", tc);
			}
		});
			
//		testLocal().addResultListener(agent.createResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				System.out.println("tests finished");
//			}
//		}));
		
//		testRemote().addResultListener(agent.createResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				System.out.println("tests finished");
//			}
//		}));
		
		testLocal(1, 100, 3).addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
				testRemote(1, 100, 3).addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
				{
					public void customResultAvailable(TestReport result)
					{
						tc.addReport(result);
						
					}
				}));
			}
		}));
	}
	
	/**
	 * 
	 */
	protected IFuture<TestReport> testLocal(int testno, long delay, int max)
	{
		return performTest(agent.getServiceProvider(), testno, delay, max);
	}
	
	/**
	 * 
	 */
	protected IFuture<TestReport> testRemote(final int testno, final long delay, final int max)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		// Start platform
		String url	= "new String[]{\"../jadex-applications-micro/target/classes\"}";	// Todo: support RID for all loaded models.
//		String url	= process.getModel().getResourceIdentifier().getLocalIdentifier().getUrl().toString();
		Starter.createPlatform(new String[]{"-platformname", "testi_1", "-libpath", url,
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-awareness", "false",
			"-gui", "false", "-usepass", "false", "-simulation", "false"
//			"-logging_level", "java.util.logging.Level.INFO"
		}).addResultListener(agent.createResultListener(
			new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{
			public void customResultAvailable(IExternalAccess platform)
			{
				performTest(platform.getServiceProvider(), testno, delay, max).addResultListener(
					agent.createResultListener(new DelegationResultListener<TestReport>(ret)));
			}
		}));
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<TestReport> performTest(final IServiceProvider provider, final int testno, final long delay, final int max)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		// Start service agent
		IFuture<IComponentManagementService> fut = SServiceProvider.getServiceUpwards(
			provider, IComponentManagementService.class);
		fut.addResultListener(agent.createResultListener(
			new ExceptionDelegationResultListener<IComponentManagementService, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				IFuture<IClockService> fut = SServiceProvider.getServiceUpwards(
					provider, IClockService.class);
				fut.addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<IClockService, TestReport>(ret)
				{
					public void customResultAvailable(final IClockService clock)
					{
						cms.createComponent(null, "jadex/micro/testcases/intermediate/IntermediateResultProviderAgent.class", null, null)
							.addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
						{	
							public void customResultAvailable(IComponentIdentifier cid)
							{
								System.out.println("cid is: "+cid);
								SServiceProvider.getService(agent.getServiceProvider(), cid, IIntermediateResultService.class)
									.addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<IIntermediateResultService, TestReport>(ret)
								{
									public void customResultAvailable(IIntermediateResultService service)
									{
										// Invoke service agent
		//								System.out.println("Invoking");
										final Long[] start = new Long[1];
										IIntermediateFuture<String> fut = service.getResults(delay, max);
										fut.addResultListener(agent.createResultListener(new IIntermediateResultListener<String>()
										{
											public void intermediateResultAvailable(String result)
											{
												if(start[0]==null)
													start[0] = clock.getTime();
												System.out.println("intermediateResultAvailable: "+result);
											}
											public void finished()
											{
												long needed = clock.getTime()-start[0].longValue();
												System.out.println("finished: "+needed);
												TestReport tr = new TestReport("#"+testno, "Tests if intermediate results work");
												long expected = delay*(max-1);
												if(needed>=expected)
												{
													System.out.println("Results did arrive in (needed/expected): ("+needed+" / "+expected+")");
													tr.setSucceeded(true);
												}
												else
												{
													tr.setReason("Results did arrive too fast (in bunch at the end (needed/expected): ("+needed+" / "+expected);
												}
												ret.setResult(tr);
											}
											public void resultAvailable(Collection<String> result)
											{
												System.out.println("resultAvailable: "+result);
												TestReport tr = new TestReport("#"+testno, "Tests if intermediate results work");
												tr.setReason("resultAvailable was called");
												ret.setResult(tr);
											}
											public void exceptionOccurred(Exception exception)
											{
												System.out.println("exceptionOccurred: "+exception);
												TestReport tr = new TestReport("#"+testno, "Tests if intermediate results work");
												tr.setReason("Exception occurred: "+exception);
												ret.setResult(tr);
											}
										}));
		//								System.out.println("Added listener");
									}		
								}));
							}
						}));
					}
				}));
			}	
		}));
		
		return ret;
	}
}
