package jadex.micro.testcases.recfutures;

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
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.CreationInfo;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  !!! unfinished !!!
 * 
 *  This test case is a first check if future in future results
 *  could possible made to work.
 *  
 *  The test succeeds, yet no conceptual support for future in futures 
 *  has been derived so far.
 *  
 *  Problems are:
 *  - in local case the inner future result values do not pass
 *    the interceptor chain
 *  - in remote case it seems that currently the results are passed as bunch
 *    and not individually.
 */
@Agent
@Service
@Results(@Result(name="testresults", clazz=Testcase.class))
@ComponentTypes(@ComponentType(name="aagent", clazz=AAgent.class))
@RequiredServices(@RequiredService(name="aser", type=IAService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_NONE, create=true, creationinfo=@CreationInfo(type="aagent"))))
public class UserAgent 
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		final Testcase tc = new Testcase();
		if(SReflect.isAndroid()) 
		{
			System.out.println("Running on android, setting test nr to 1");
			tc.setTestCount(1);
		} 
		else 
		{
			tc.setTestCount(2);	
		}
		
		final Future<TestReport> ret = new Future<TestReport>();
		ret.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<TestReport>()
		{
			public void resultAvailable(TestReport result)
			{
//				System.out.println("tests finished");

				agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
				agent.killComponent();			
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println(agent.getComponentFeature(IExecutionFeature.class).isComponentThread()+" "+agent.getComponentIdentifier());
				
				agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
				agent.killComponent();	
			}
		}));
			
//			testLocal().addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)
//			{
//				public void customResultAvailable(Void result)
//				{
//					System.out.println("tests finished");
//				}
//			}));
		
//			testRemote().addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)
//			{
//				public void customResultAvailable(Void result)
//				{
//					System.out.println("tests finished");
//				}
//			}));
		
		testLocal(1, 100, 3).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
				if(SReflect.isAndroid()) 
				{
					System.out.println("Running on android, so skipping remote tests.");
					ret.setResult(null);
				}
				else
				{
					testRemote(2, 100, 3).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)
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
		
//		IAService aser = (IAService)agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("aser").get();
//		
//		IFuture<IFuture<String>> futa = aser.methodA();
//		futa.addResultListener(new DefaultResultListener<IFuture<String>>()
//		{
//			public void resultAvailable(IFuture<String> fut2)
//			{
//				System.out.println("received first: "+fut2);
//				
//				fut2.addResultListener(new DefaultResultListener<String>()
//				{
//					public void resultAvailable(String result)
//					{
//						System.out.println("received second: "+result);
//					}
//				});
//			}
//		});
//		
//		IFuture<IIntermediateFuture<String>> futb = aser.methodB();
//		futb.addResultListener(new DefaultResultListener<IIntermediateFuture<String>>()
//		{
//			public void resultAvailable(IIntermediateFuture<String> fut2)
//			{
//				System.out.println("received first: "+fut2);
//				
//				fut2.addResultListener(new IntermediateDefaultResultListener<String>()
//				{
//					public void intermediateResultAvailable(String result) 
//					{
//						System.out.println("received: "+result);
//					}
//					
//					public void finished() 
//					{
//						System.out.println("fini");
//					}	
//				});
//			}
//		});
	}
	
	/**
	 *  Test if local intermediate results are correctly delivered
	 *  (not as bunch when finished has been called). 
	 */
	protected IFuture<TestReport> testLocal(int testno, long delay, int max)
	{
		return performTest(agent.getComponentIdentifier().getRoot(), testno, delay, max);
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
			String url	= SUtil.getOutputDirsExpression("jadex-applications-micro");	// Todo: support RID for all loaded models.
	//		String url	= process.getModel().getResourceIdentifier().getLocalIdentifier().getUrl().toString();
			Starter.createPlatform(new String[]{"-libpath", url, "-platformname", agent.getComponentIdentifier().getPlatformPrefix()+"_*",
				"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-awareness", "false",
	//			"-logging_level", "java.util.logging.Level.INFO",
				"-gui", "false", "-simulation", "false", "-printpass", "false"
			}).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(
				new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
			{
				public void customResultAvailable(final IExternalAccess platform)
				{
//					ComponentIdentifier.getTransportIdentifier(platform).addResultListener(new ExceptionDelegationResultListener<ITransportComponentIdentifier, TestReport>(ret)
//	                {
//	                    public void customResultAvailable(ITransportComponentIdentifier result)
//	                    { 
							performTest(platform.getComponentIdentifier(), testno, delay, max)
								.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)
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
//	                    }
//	                });
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
				TestReport tr = new TestReport("#"+testno, "Tests if rec results work");
				tr.setFailed(exception);
				super.resultAvailable(tr);
			}
		});
		
		// Start service agent
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				IResourceIdentifier	rid	= new ResourceIdentifier(
					new LocalResourceIdentifier(root, agent.getModel().getResourceIdentifier().getLocalIdentifier().getUri()), null);
//						System.out.println("Using rid: "+rid);
				final boolean	local	= root.equals(agent.getComponentIdentifier().getRoot());
				jadex.bridge.service.types.cms.CreationInfo	ci	= new jadex.bridge.service.types.cms.CreationInfo(local ? agent.getComponentIdentifier() : root, rid);
				cms.createComponent(null, AAgent.class.getName()+".class", ci, null)
					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
				{	
					public void customResultAvailable(final IComponentIdentifier cid)
					{
						SServiceProvider.getService(agent, cid, IAService.class)
							.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IAService, TestReport>(ret)
						{
							public void customResultAvailable(IAService service)
							{
								System.out.println("found service: "+((IService)service).getServiceIdentifier());
								
//								IFuture<IFuture<String>> futa = service.methodA();
//								futa.addResultListener(new DefaultResultListener<IFuture<String>>()
//								{
//									public void resultAvailable(IFuture<String> fut2)
//									{
//										System.out.println("received first: "+fut2);
//										
//										fut2.addResultListener(new DefaultResultListener<String>()
//										{
//											public void resultAvailable(String result)
//											{
//												System.out.println("received second: "+result);
//												TestReport tr = new TestReport("#"+testno, "Tests if rec results work");
//												tr.setSucceeded(true);
//												ret.setResult(tr);
//											}
//										});
//									}
//								});
								
								IFuture<IIntermediateFuture<String>> futb = service.methodB();
								futb.addResultListener(new DefaultResultListener<IIntermediateFuture<String>>()
								{
									public void resultAvailable(IIntermediateFuture<String> fut2)
									{
										System.out.println("received first: "+fut2);
										
										fut2.addResultListener(new IntermediateDefaultResultListener<String>()
										{
											public void intermediateResultAvailable(String result) 
											{
												System.out.println("received: "+result);
											}
											
											public void finished() 
											{
												System.out.println("fini");
												TestReport tr = new TestReport("#"+testno, "Tests if rec results work");
												tr.setSucceeded(true);
												ret.setResult(tr);
											}	
										});
									}
								});
							}
						}));
					}
				});
			}
		});
		
		return res;
	}
}
