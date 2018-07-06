package jadex.micro.testcases.intermediate;

import java.util.Collection;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.util.STest;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.RemoteTestBaseAgent;

/**
 *  The invoker agent tests if intermediate results are directly delivered 
 *  back to the invoker in local and remote case.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
@Description("The invoker agent tests if intermediate results are directly " +
	"delivered back to the invoker in local and remote case.")
public class IntermediateTestAgent extends RemoteTestBaseAgent
{
	//-------- attributes --------
	
	@Agent
	protected IInternalAccess agent;

	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		final Testcase tc = new Testcase();
		if(SReflect.isAndroid()) 
		{
			tc.setTestCount(1);
		} 
		else 
		{
			tc.setTestCount(2);	
		}
		
		
		final Future<TestReport> ret = new Future<TestReport>();
		ret.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<TestReport>()
		{
			public void resultAvailable(TestReport result)
			{
//				System.out.println("tests finished");

				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
				agent.killComponent();		
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println(agent.getFeature(IExecutionFeature.class).isComponentThread()+" "+agent.getIdentifier());
				
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
				agent.killComponent();			
			}
		}));
			
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
		
		testLocal(1, 100, 3).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
				if(SReflect.isAndroid()) 
				{
					ret.setResult(null);
				}
				else
				{
					testRemote(2, 100, 3).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)
					{
						public void customResultAvailable(TestReport result)
						{
							tc.addReport(result);
							ret.setResult(null);
						}
					}));
				}
			}
		}));
	}
	
	/**
	 *  Test if local intermediate results are correctly delivered
	 *  (not as bunch when finished has been called). 
	 */
	protected IFuture<TestReport> testLocal(int testno, long delay, int max)
	{
		return performTest(agent.getIdentifier().getRoot(), testno, delay, max);
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
//				"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-awareness", "false",
//	//			"-logging_level", "java.util.logging.Level.INFO",
//				"-gui", "false", "-simulation", "false", "-printpass", "false",
//				"-superpeerclient", "false" // TODO: fails on shutdown due to auto restart
//			})
			
			IPlatformConfiguration	config	= STest.getDefaultTestConfig();
			config.getExtendedPlatformConfiguration().setSimulation(false);	// No simulaton, because we need to measure in real time
			Starter.createPlatform(config)
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
							performTest(platform.getIdentifier(), testno, delay, max)
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
	protected IFuture<TestReport> performTest(final IComponentIdentifier root, final int testno, final long delay, final int max)
	{
		final Future<TestReport> ret = new Future<TestReport>();

		final Future<TestReport> res = new Future<TestReport>();
		
		ret.addResultListener(new DelegationResultListener<TestReport>(res)
		{
			public void exceptionOccurred(Exception exception)
			{
				TestReport tr = new TestReport("#"+testno, "Tests if intermediate results work");
				tr.setFailed(exception);
				super.resultAvailable(tr);
			}
		});
		
		// Start service agent
		agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
//				System.out.println("root is: "+root)
				// Hack!!! use remote platform as search owner
				ServiceQuery<IClockService>	query	= new ServiceQuery<IClockService>(IClockService.class, null, root, null).setProvider(new BasicComponentIdentifier("clock", root));
//				agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( new BasicComponentIdentifier("clock", root)), IClockService.class)
				agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( query))
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IClockService, TestReport>(ret)
				{
					public void customResultAvailable(final IClockService clock)
					{
//						System.out.println("clock is: "+clock);
						IResourceIdentifier	rid	= new ResourceIdentifier(
							new LocalResourceIdentifier(root, agent.getModel().getResourceIdentifier().getLocalIdentifier().getUri()), null);
//						System.out.println("Using rid: "+rid);
						final boolean	local	= root.equals(agent.getIdentifier().getRoot());
						CreationInfo	ci	= new CreationInfo(local ? agent.getIdentifier() : root, rid);
						cms.createComponent(null, "jadex/micro/testcases/intermediate/IntermediateResultProviderAgent.class", ci, null)
							.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
						{	
							public void customResultAvailable(final IComponentIdentifier cid)
							{
//								System.out.println("cid is: "+cid);
								agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IIntermediateResultService.class).setProvider(cid))
									.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IIntermediateResultService, TestReport>(ret)
								{
									public void customResultAvailable(IIntermediateResultService service)
									{
										// Invoke service agent
//										System.out.println("Invoking");
										final Long[] start = new Long[1];
										IIntermediateFuture<String> fut = service.getResults(delay, max);
										fut.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<String>()
										{
											public void intermediateResultAvailable(String result)
											{
												if(start[0]==null)
												{
													start[0] = 	local ? clock.getTime() : System.currentTimeMillis();
												}
//													System.out.println("intermediateResultAvailable: "+result);
											}
											public void finished()
											{
												long needed = (local ? clock.getTime() : System.currentTimeMillis())-start[0].longValue();
//															System.out.println("finished: "+needed);
												TestReport tr = new TestReport("#"+testno, "Tests if intermediate results work");
												long expected = delay*(max-1);
												// deviation can happen because receival of results is measured
//												System.out.println("Results did arrive in (needed/expected): ("+needed+" / "+expected+")");
												
												if(needed*1.1>=expected) // 10% deviation allowed
												{
													tr.setSucceeded(true);
												}
												else
												{
													tr.setReason("Results did arrive too fast (in bunch at the end (needed/expected): ("+needed+" / "+expected);
												}
												cms.destroyComponent(cid);
												ret.setResult(tr);
											}
											public void resultAvailable(Collection<String> result)
											{
//												System.out.println("resultAvailable: "+result);
												TestReport tr = new TestReport("#"+testno, "Tests if intermediate results work");
												tr.setReason("resultAvailable was called");
												cms.destroyComponent(cid);
												ret.setResult(tr);
											}
											public void exceptionOccurred(Exception exception)
											{
//												System.out.println("exceptionOccurred: "+exception);
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
					}
				}));	
			}	
		});
		
		return res;
	}
}
