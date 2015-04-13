package jadex.micro.testcases;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.component.IArgumentsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Agent
@RequiredServices(
{
	@RequiredService(name="msgservice", type=IMessageService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="cms", type=IComponentManagementService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="clock", type=IClockService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
//@ComponentTypes(
//	@ComponentType(name="receiver", filename="jadex/micro/testcases/stream/ReceiverAgent.class")
//)
@Results(@Result(name="testresults", clazz=Testcase.class))
public abstract class TestAgent
{
	@Agent
	protected IInternalAccess agent;
	
	protected Set<IExternalAccess>	platforms	= new LinkedHashSet<IExternalAccess>();
	
	
	/**
	 *  Cleanup created platforms.
	 */
	@AgentKilled
	public IFuture<Void>	cleanup()
	{
		final Future<Void>	ret	= new Future<Void>();
		IResultListener<Map<String, Object>>	crl	= new CounterResultListener<Map<String, Object>>(platforms.size(), new DelegationResultListener<Void>(ret));
		
		for(IExternalAccess platform: platforms)
		{
//			platform.killComponent().addResultListener(crl);
//			System.out.println("kill: "+platform.getComponentIdentifier());
			platform.killComponent().addResultListener(crl);
		}
		platforms	= null;
		
		return ret;
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		final Testcase tc = new Testcase();
		tc.setTestCount(getTestCount());
		
		performTests(tc).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
//				System.out.println("tests finished: "+agent.getComponentIdentifier());

				//agent.getComponentFeature(IArgumentsFeature.class).getResults().put("testresults", tc);
				agent.getComponentFeature(IArgumentsFeature.class).getResults().put("testresults", tc);
				ret.setResult(null);
//				agent.killComponent()				
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("tests failed: "+agent.getComponentIdentifier());
				
				exception.printStackTrace();
				
				agent.getComponentFeature(IArgumentsFeature.class).getResults().put("testresults", tc);
				ret.setResult(null);
//				agent.killComponent()	
			}
		}));
		
		return ret;
	}
	
	/**
	 *  The agent body.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		IFuture<IComponentManagementService>	fut	= agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				test(cms, true).addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
				{
					public void customResultAvailable(TestReport result)
					{
						tc.addReport(result);
						createPlatform(null).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
						{
							public void customResultAvailable(final IExternalAccess exta)
							{
								createProxy(agent.getExternalAccess(), exta).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
								{
									public void customResultAvailable(IComponentIdentifier result)
									{
										createProxy(exta, agent.getExternalAccess()).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
										{
											public void customResultAvailable(IComponentIdentifier result)
											{
												SServiceProvider.getService(exta, IComponentManagementService.class)
													.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
												{
													public void customResultAvailable(IComponentManagementService cms2)
													{
														test(cms2, false).addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
														{
															public void customResultAvailable(TestReport result)
															{
																tc.addReport(result);
																ret.setResult(null);
															}
														});
													}
												}));
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  The test count.
	 */
	protected int	getTestCount()
	{
		return 2;
	}

	/**
	 *  Create a proxy for the remote platform.
	 */
	protected IFuture<IComponentIdentifier>	createProxy(IExternalAccess local, final IExternalAccess remote)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		SServiceProvider.getService(local, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(final IComponentManagementService flocal)
			{
				SServiceProvider.getService(remote, ITransportAddressService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<ITransportAddressService, IComponentIdentifier>(ret)
				{
					public void customResultAvailable(ITransportAddressService tas)
					{
						tas.getTransportComponentIdentifier(remote.getComponentIdentifier().getRoot()).addResultListener(new ExceptionDelegationResultListener<ITransportComponentIdentifier, IComponentIdentifier>(ret)
						{
							public void customResultAvailable(ITransportComponentIdentifier extacid)
							{
								Map<String, Object>	args = new HashMap<String, Object>();
								args.put("component", extacid);
								CreationInfo ci = new CreationInfo(args);
								flocal.createComponent(null, "jadex/platform/service/remote/ProxyAgent.class", ci, null).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<IExternalAccess> createPlatform(String[] args)
	{
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		
		// Start platform
//		String url	= "new String[]{\"../jadex-applications-micro/target/classes\"}";	// Todo: support RID for all loaded models.
//		String url	= process.getModel().getResourceIdentifier().getLocalIdentifier().getUrl().toString();
//		Starter.createPlatform(new String[]{"-platformname", "testi_1", "-libpath", url,
		String[] defargs = new String[]{
//			"-libpath", url,
			"-platformname", agent.getComponentIdentifier().getPlatformPrefix()+"_*",
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-awareness", "false",
//			"-logging", "true",
//			"-relaytransport", "false",
			"-niotcptransport", "false",	// Use tcp instead of nio to test both transports (original testcase platform uses nio)
			"-tcptransport", "true",	// Todo: make autoterminate work also with niotcp
//				"-gui", "false", "-usepass", "false", "-simulation", "false"
//			"-binarymessages", "false",
			"-gui", "false",
			"-cli", "false",
			"-simulation", "false", "-printpass", "false"};
		
		if(args!=null && args.length>0)
		{
			Map<String, String> argsmap = new HashMap<String, String>();
			for(int i=0; i<defargs.length; i++)
			{
				argsmap.put(defargs[i], defargs[++i]);
			}
			for(int i=0; i<args.length; i++)
			{
				argsmap.put(args[i], args[++i]);
			}
			defargs = new String[argsmap.size()*2];
			int i=0;
			for(String key: argsmap.keySet())
			{
				defargs[i*2]= key; 
				defargs[i*2+1] = argsmap.get(key);
				i++;
			}
		}

//		System.out.println("platform args: "+SUtil.arrayToString(defargs));
		
		Starter.createPlatform(defargs).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(
			new DelegationResultListener<IExternalAccess>(ret)
		{
			public void customResultAvailable(IExternalAccess result)
			{
				platforms.add(result);
				super.customResultAvailable(result);
			}
		}));
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<IComponentIdentifier> createComponent(final String filename,
		final IComponentIdentifier root, final IResultListener<Collection<Tuple2<String,Object>>> reslis)
	{
		return createComponent(filename, null, null, root, reslis);
	}
	
	/**
	 * 
	 */
	protected IFuture<IComponentIdentifier> createComponent(final String filename, final Map<String, Object> args, 
		final String config, final IComponentIdentifier root, final IResultListener<Collection<Tuple2<String,Object>>> reslis)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
//				IResourceIdentifier	rid	= new ResourceIdentifier(
//					new LocalResourceIdentifier(root, agent.getModel().getResourceIdentifier().getLocalIdentifier().getUri()), null);
				boolean	local = root.equals(agent.getComponentIdentifier().getRoot());
				CreationInfo ci	= new CreationInfo(local? agent.getComponentIdentifier(): root, agent.getModel().getResourceIdentifier());
				ci.setArguments(args);
				ci.setConfiguration(config);
				cms.createComponent(null, filename, ci, reslis)
					.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
				{
					public void customResultAvailable(IComponentIdentifier result)
					{
						super.customResultAvailable(result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
						super.exceptionOccurred(exception);
					}
				}
				);
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Map<String, Object>> destroyComponent(final IComponentIdentifier cid)
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Map<String, Object>>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				cms.destroyComponent(cid).addResultListener(new DelegationResultListener<Map<String, Object>>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Setup a local test.
	 */
	protected IFuture<IComponentIdentifier>	setupLocalTest(String filename, IResultListener<Collection<Tuple2<String,Object>>> reslis)
	{
		return createComponent(filename, agent.getComponentIdentifier().getRoot(), reslis);
	}
	
	/**
	 *  Setup a remote test.
	 */
	protected IFuture<IComponentIdentifier>	setupRemoteTest(final String filename, final String config,
		final IResultListener<Collection<Tuple2<String,Object>>> reslis, final boolean remove)
	{
		final Future<IComponentIdentifier>	ret	= new Future<IComponentIdentifier>();
		
		createPlatform(null).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(final IExternalAccess exta)
			{
				if(remove)
					platforms.remove(exta);
				
//				createProxy(agent.getComponentIdentifier().getRoot(), exta.getComponentIdentifier()).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
				createProxy(agent.getExternalAccess(), exta).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
				{
					public void customResultAvailable(IComponentIdentifier result)
					{
						// inverse proxy from remote to local.
						createProxy(exta, agent.getExternalAccess())
							.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
						{
							public void customResultAvailable(IComponentIdentifier result)
							{
								createComponent(filename, null, config, exta.getComponentIdentifier(), reslis)
									.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	public <T> IFuture<T>	waitForRealtimeDelay(final long delay, final IComponentStep<T> step)
	{
		final Future<T>	ret	= new Future<T>();
		IFuture<IClockService>	clockfut	= agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("clock");
		clockfut.addResultListener(new ExceptionDelegationResultListener<IClockService, T>(ret)
		{
			public void customResultAvailable(IClockService clock)
			{
				clock.createRealtimeTimer(delay, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						agent.getComponentFeature(IExecutionFeature.class).scheduleStep(step).addResultListener(new DelegationResultListener<T>(ret));
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Perform  the test.
	 *  @param cms	The cms of the platform to test (local or remote).
	 * 	@param local	True when tests runs on local platform. 
	 *  @return	The test result.
	 */
	protected IFuture<TestReport>	test(IComponentManagementService cms, boolean local)
	{
		throw new UnsupportedOperationException("Implement test() or performTests()");
	}
}
