package jadex.micro.testcases.pull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.util.STest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.IPullSubscriptionIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.RemoteTestBaseAgent;

/**
 *  The invoker agent tests if intermediate results are directly delivered 
 *  back to the invoker in local and remote case.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
@Description("The invoker agent tests if pull results are directly " +
	"delivered back to the invoker in local and remote case.")
@Properties({@NameValue(name=Testcase.PROPERTY_TEST_TIMEOUT, value="jadex.base.Starter.getScaledDefaultTimeout(null, 4)")}) // cannot use $component.getId() because is extracted from test suite :-(
public class PullResultTestAgent extends RemoteTestBaseAgent
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
//		if(SReflect.isAndroid()) 
		{
			tc.setTestCount(2);
		} 
//		else 
//		{
//			tc.setTestCount(4);
//		}
		
		final Future<Void> ret = new Future<Void>();
		ret.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
//				System.out.println("tests finished");

				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
				agent.killComponent();				
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println(agent.getFeature(IExecutionFeature.class).isComponentThread()+" "+agent.getId());
				
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
				agent.killComponent();	
			}
		}));
//					
//		testLocal(1, 100, 3).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport[], Void>(ret)
//		{
//			public void customResultAvailable(TestReport[] result)
//			{
//				for(TestReport tr: result)
//					tc.addReport(tr);
//				if(SReflect.isAndroid()) 
//				{
//					ret.setResult(null);
//				} 
//				else 
//				{
					testRemote(2, 100, 3).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport[], Void>(ret)
					{
						public void customResultAvailable(TestReport[] result)
						{
							for(TestReport tr: result)
								tc.addReport(tr);
							ret.setResult(null);
						}
					}));
//				}
//			}
//		}));
	}
	
	/**
	 *  Test if local intermediate results are correctly delivered
	 *  (not as bunch when finished has been called). 
	 */
	protected IFuture<TestReport[]> testLocal(final int testno, final long delay, final int max)
	{
		disableLocalSimulationMode().get();
		final Future<TestReport[]> ret = new Future<TestReport[]>();
		performTestA(agent.getId().getRoot(), testno, delay, max)
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, TestReport[]>(ret)
		{
			public void customResultAvailable(final TestReport result1)
			{
				performTestB(agent.getId().getRoot(), testno+1, delay, max)
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, TestReport[]>(ret)
				{
					public void customResultAvailable(final TestReport result2)
					{
						ret.setResult(new TestReport[]{result1, result2});
					}
				}));
			}
		}));
		return ret;
	}
	
	/**
	 *  Test if remote intermediate results are correctly delivered
	 *  (not as bunch when finished has been called). 
	 */
	protected IFuture<TestReport[]> testRemote(final int testno, final long delay, final int max)
	{
		final Future<TestReport[]> ret = new Future<TestReport[]>();
		
		// Start platform
		try
		{
			disableLocalSimulationMode().get();
			
			String url	= SUtil.getOutputDirsExpression("jadex-applications-micro", true);	// Todo: support RID for all loaded models.
//	//		String url	= process.getModel().getResourceIdentifier().getLocalIdentifier().getUrl().toString();
//			Starter.createPlatform(STest.getDefaultTestConfig(), new String[]{"-libpath", url, "-platformname", agent.getId().getPlatformPrefix()+"_*",
//				"-saveonexit", "false", "-welcome", "false", "-awareness", "false",
//	//			"-logging_level", "java.util.logging.Level.INFO",
//				"-gui", "false", "-simulation", "false", "-simul", "false", "-printpass", "false",
//				"-superpeerclient", "false" // TODO: fails on shutdown due to auto restart
//			}).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(
			IPlatformConfiguration conf = STest.getDefaultTestConfig(getClass());
			conf.getExtendedPlatformConfiguration().setSimul(false);
			conf.getExtendedPlatformConfiguration().setSimulation(false);
			Starter.createPlatform(conf).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(
				new ExceptionDelegationResultListener<IExternalAccess, TestReport[]>(ret)
			{
				public void customResultAvailable(final IExternalAccess platform)
				{
					createProxies(platform)
						.addResultListener(new ExceptionDelegationResultListener<Void, TestReport[]>(ret)
					{
						public void customResultAvailable(Void result)
						{
							performTestA(platform.getId(), testno, delay, max)
								.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, TestReport[]>(ret)
							{
								public void customResultAvailable(final TestReport result1)
								{
									performTestB(platform.getId(), testno+1, delay, max)
										.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, TestReport[]>(ret)
									{
										public void customResultAvailable(final TestReport result2)
										{
											platform.killComponent();
				//								.addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, TestReport>(ret)
				//							{
				//								public void customResultAvailable(Map<String, Object> v)
				//								{
				//									ret.setResult(result);
				//								}
				//							});
											ret.setResult(new TestReport[]{result1, result2});
										}
									}));
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
	protected IFuture<TestReport> performTestA(final IComponentIdentifier root, final int testno, final long delay, final int max)
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
		// Hack!!! TODO: use some internal/external access for fetching service???
		@SuppressWarnings("unchecked")
		IClockService clock	= (IClockService)ServiceRegistry.getRegistry(root)
			.getLocalService(ServiceRegistry.getRegistry(root).searchService(new ServiceQuery<>(IClockService.class).setNetworkNames(null)));
		IResourceIdentifier	rid	= new ResourceIdentifier(
			new LocalResourceIdentifier(root, agent.getModel().getResourceIdentifier().getLocalIdentifier().getUri()), null);
//		System.out.println("Using rid: "+rid);
		final boolean	local	= root.equals(agent.getId().getRoot());
		CreationInfo ci	= new CreationInfo(rid).setFilename(PullResultProviderAgent.class.getName()+".class");
		agent.getExternalAccess(local ? agent.getId() : root).createComponent(ci)
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{	
			public void customResultAvailable(final IExternalAccess exta)
			{
//				System.out.println("cid is: "+exta);
				agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IPullResultService.class).setProvider(exta.getId()))
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IPullResultService, TestReport>(ret)
				{
					public void customResultAvailable(IPullResultService service)

					{
						// Invoke service agent
						System.out.println("Invoking");
						IPullIntermediateFuture<String> fut = service.getResultsA(max);
						
						fut.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<String>()
						{
							protected List<String> res = new ArrayList<String>();
							
							public void intermediateResultAvailable(String result)
							{
								System.out.println("intermediateResultAvailable: "+result);
								res.add(result);
							}
							public void finished()
							{
								System.out.println("finished: ");
								TestReport tr = new TestReport("#"+testno, "Tests if pull results work");
								if(res.size()==max)
								{
									tr.setSucceeded(true);
								}
								else
								{
									tr.setReason("Not all results did arrive: ("+res.size()+" / "+max);
								}
//												cms.destroyComponent(cid);
								ret.setResult(tr);
							}
							public void resultAvailable(Collection<String> result)
							{
								System.out.println("resultAvailable: "+result);
								TestReport tr = new TestReport("#"+testno, "Tests if intermediate results work");
								tr.setReason("resultAvailable was called");
//												cms.destroyComponent(cid);
								ret.setResult(tr);
							}
							public void exceptionOccurred(Exception exception)
							{
								System.out.println("exceptionOccurred: "+exception);
								TestReport tr = new TestReport("#"+testno, "Tests if intermediate results work");
								tr.setFailed(exception);
								ret.setResult(tr);
							}
						}));
						System.out.println("Added listener");
						
						for(int i=0; i<max; i++)
						{
							System.out.println("pulling");
							fut.pullIntermediateResult();
						}
					}		
				}));
			}
		});
		
		return res;
	}
	
	/**
	 *  Perform the test. Consists of the following steps:
	 *  - start an agent that offers the service
	 *  - invoke the service
	 *  - wait with intermediate listener for results 
	 */
	protected IFuture<TestReport> performTestB(final IComponentIdentifier root, final int testno, final long delay, final int max)
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
		// Hack!!! TODO: use some internal/external access for fetching service???
		IResourceIdentifier	rid	= new ResourceIdentifier(
			new LocalResourceIdentifier(root, agent.getModel().getResourceIdentifier().getLocalIdentifier().getUri()), null);
		final boolean	local	= root.equals(agent.getId().getRoot());
		CreationInfo	ci	= new CreationInfo(rid).setFilename(PullResultProviderAgent.class.getName()+".class");
		System.out.println("create: "+rid);
		agent.getExternalAccess(local ? agent.getId() : root).createComponent(ci)
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{	
			public void customResultAvailable(final IExternalAccess exta)
			{
				System.out.println("cid is: "+exta);
				agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IPullResultService.class).setProvider(exta.getId()))
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IPullResultService, TestReport>(ret)
				{
					public void customResultAvailable(IPullResultService service)
					{
						// Invoke service agent
						System.out.println("Invoking B");
						IPullSubscriptionIntermediateFuture<String> fut2 = service.getResultsB(max);
						
						fut2.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<String>()
						{
							protected List<String> res = new ArrayList<String>();
							
							public void intermediateResultAvailable(String result)
							{
								System.out.println("intermediateResultAvailable: "+result);
								res.add(result);
							}
							public void finished()
							{
								System.out.println("finished: ");
								TestReport tr = new TestReport("#"+testno, "Tests if pull results work");
								tr.setReason("Exception did not occur");
								agent.getExternalAccess(exta.getId()).killComponent();
								ret.setResult(tr);
							}
							public void resultAvailable(Collection<String> result)
							{
								System.out.println("resultAvailable: "+result);
								TestReport tr = new TestReport("#"+testno, "Tests if pull results work");
								tr.setReason("Exception did not occur");
								agent.getExternalAccess(exta.getId()).killComponent();
								ret.setResult(tr);
							}
							public void exceptionOccurred(Exception exception)
							{
								System.out.println("exceptionOccurred: "+exception);
								
								TestReport tr = new TestReport("#"+testno, "Tests if pull results work");
								if(exception instanceof FutureTerminatedException)
								{
									tr.setSucceeded(true);
								}
								else
								{
									tr.setReason("Other exception: ("+exception);
								}
								agent.getExternalAccess(exta.getId()).killComponent();
								ret.setResult(tr);
							}
						}));
						System.out.println("Added listener");
						
						fut2.pullIntermediateResult();
						
						fut2.terminate();
						
						// Test expects exception, because of message overtakes
//										for(int i=0; i<max-1; i++)
							fut2.pullIntermediateResult();
					}		
				}));
			}
		});
		
		return res;
	}
}
