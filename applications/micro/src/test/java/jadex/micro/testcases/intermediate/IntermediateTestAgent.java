package jadex.micro.testcases.intermediate;

import java.util.Collection;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.simulation.SSimulation;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateEmptyResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.TestAgent;

/**
 *  The invoker agent tests if intermediate results are directly delivered 
 *  back to the invoker in local and remote case.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
@Description("The invoker agent tests if intermediate results are directly " +
	"delivered back to the invoker in local and remote case.")
public class IntermediateTestAgent extends TestAgent
{
	//-------- attributes --------
	
	@Agent
	protected IInternalAccess agent;

	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	//@AgentBody
	@OnStart
	public IFuture<Void> body()
	{
		final Testcase tc = new Testcase();
		tc.setTestCount(2);	
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
		
		
		final Future<Void> ret = new Future<>();
			
//		testLocal().addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				System.out.println("tests finished");
//			}
//		}));
		
//		testRemote().addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				System.out.println("tests finished");
//			}
//		}));
		
		long	delay	= Starter.getScaledDefaultTimeout(agent.getId().getRoot(), 0.01);
		
		testLocal(1, delay, 3).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
				testRemote(2, delay, 3).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
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
	 *  Test if local intermediate results are correctly delivered
	 *  (not as bunch when finished has been called). 
	 */
	protected IFuture<TestReport> testLocal(int testno, long delay, int max)
	{
		return performTest(agent.getId().getRoot(), null, testno, delay, max);
	}
	
	/**
	 *  Test if remote intermediate results are correctly delivered
	 *  (not as bunch when finished has been called). 
	 */
	protected IFuture<TestReport> testRemote(final int testno, final long delay, final int max)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		// Start platform
		try
		{
//			String url	= SUtil.getOutputDirsExpression("jadex-applications-micro", true);	// Todo: support RID for all loaded models.
//	//		String url	= process.getModel().getResourceIdentifier().getLocalIdentifier().getUrl().toString();
//			Starter.createPlatform(new String[]{"-libpath", url, "-platformname", agent.getComponentIdentifier().getPlatformPrefix()+"_*",
//				"-saveonexit", "false", "-welcome", "false", "-awareness", "false",
//	//			"-logging_level", "java.util.logging.Level.INFO",
//				"-gui", "false", "-simulation", "false", "-printsecret", "false",
//				"-superpeerclient", "false" // TODO: fails on shutdown due to auto restart
//			})
			
			setupRemotePlatform(true)
				.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(
				new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
			{
				public void customResultAvailable(final IExternalAccess platform)
				{
					createProxies(platform)
						.addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
					{
						public void customResultAvailable(Void result)
						{
							performTest(platform.getId(), platform, testno, delay, max)
							.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)
						{
							public void customResultAvailable(final TestReport result)
							{
								platform.killComponent();
								ret.setResult(result);
							}
						}));
						}
					});
				}
			}));
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Perform the test. Consists of the following steps:
	 *  - start an agent that offers the service
	 *  - invoke the service
	 *  - wait with intermediate listener for results 
	 */
	protected IFuture<TestReport> performTest(final IComponentIdentifier root, IExternalAccess platform, final int testno, final long delay, final int max)
	{
		IResourceIdentifier	rid	= new ResourceIdentifier(
				new LocalResourceIdentifier(root, agent.getModel().getResourceIdentifier().getLocalIdentifier().getUri()), null);
//					System.out.println("Using rid: "+rid);
		final boolean	local	= root.equals(agent.getId().getRoot());
		TestReport tr = new TestReport("#"+testno, "Tests if "+(local?"local":"remote")+" intermediate results work");
		final Future<TestReport> ret = new Future<TestReport>();

		final Future<TestReport> res = new Future<TestReport>();
		
		ret.addResultListener(new DelegationResultListener<TestReport>(res)
		{
			public void exceptionOccurred(Exception exception)
			{
				tr.setFailed(exception);
				super.resultAvailable(tr);
			}
		});
		
		// Start service agent
		CreationInfo	ci	= new CreationInfo(rid);
		(local ? agent.getExternalAccess() : platform).createComponent(ci.setFilename("jadex/micro/testcases/intermediate/IntermediateResultProviderAgent.class"))
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{	
			public void customResultAvailable(final IExternalAccess exta)
			{
//						System.out.println("cid is: "+cid);
				agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IIntermediateResultService.class).setProvider(exta.getId()))
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IIntermediateResultService, TestReport>(ret)
				{
					public void customResultAvailable(IIntermediateResultService service)
					{
						// Invoke service agent
						final Long[] start = new Long[1];
						IClockService	clock	= agent.getFeature(IRequiredServicesFeature.class).getLocalService(IClockService.class);
						IIntermediateFuture<String> fut = service.getResults(delay, max);
						fut.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IntermediateEmptyResultListener<String>()
						{
							public void intermediateResultAvailable(String result)
							{
								if(start[0]==null)
								{
									start[0] = 	local || SSimulation.isBisimulating(agent) ? clock.getTime() : (System.nanoTime()/1000000);
								}
//													System.out.println("intermediateResultAvailable: "+result);
							}
							public void finished()
							{
								long needed = (local || SSimulation.isBisimulating(agent) ? clock.getTime() : (System.nanoTime()/1000000))-start[0].longValue();
//															System.out.println("finished: "+needed);
								long expected = delay*(max-1);
								// deviation can happen because receival of results is measured
								System.out.println("Results did arrive in (needed/expected): ("+needed+" / "+expected+")");
								
								if(needed*1.1>=expected) // 10% deviation allowed
								{
									tr.setSucceeded(true);
								}
								else
								{
									tr.setReason("Results did arrive too fast (in bunch at the end (needed/expected): ("+needed+" / "+expected+")");
								}
								agent.getExternalAccess(exta.getId()).killComponent();
								ret.setResult(tr);
							}
							public void resultAvailable(Collection<String> result)
							{
//										System.out.println("resultAvailable: "+result);
								TestReport tr = new TestReport("#"+testno, "Tests if intermediate results work");
								tr.setReason("resultAvailable was called");
								agent.getExternalAccess(exta.getId()).killComponent();
								ret.setResult(tr);
							}
							public void exceptionOccurred(Exception exception)
							{
//										System.out.println("exceptionOccurred: "+exception);
								TestReport tr = new TestReport("#"+testno, "Tests if intermediate results work");
								tr.setFailed(exception);
								ret.setResult(tr);
							}
						}));
//								System.out.println("Added listener");
					}		
				}));
			}
		});
		
		return res;
	}
}
