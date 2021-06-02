package jadex.platform;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent
@RequiredServices(
{
//	@RequiredService(name="msgservice", type=IMessageService.class, 
//		binding=@Binding(scope=ServiceScope.PLATFORM)),
//	@RequiredService(name="cms", type=IComponentManagementService.class),
	@RequiredService(name="clock", type=IClockService.class)
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
	//@AgentKilled
	@OnEnd
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
	//@AgentBody
	@OnStart
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		final Testcase tc = new Testcase();
		tc.setTestCount(getTestCount());
		
		performTests(tc).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
//				System.out.println("tests finished: "+agent.getComponentIdentifier());

				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
				ret.setResult(null);
//				agent.killAgent();				
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("tests failed: "+agent.getComponentIdentifier());
				
				exception.printStackTrace();
				
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
				ret.setResult(null);
//				agent.killAgent();	
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
		
		test(agent.getExternalAccess(), true).addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
				createPlatform(null).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
				{
					public void customResultAvailable(final IExternalAccess exta)
					{
						Starter.createProxy(agent.getExternalAccess(), exta).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
						{
							public void customResultAvailable(IExternalAccess result)
							{
								test(exta, false).addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
								{
									public void customResultAvailable(TestReport result)
									{
										tc.addReport(result);
										ret.setResult(null);
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
			"-platformname", agent.getId().getPlatformPrefix()+"_*",
			"-saveonexit", "false", "-welcome", "false", "-awareness", "false",
//			"-logging", "true",
//			"-relaytransport", "false",
//				"-gui", "false", "-usepass", "false", "-simulation", "false"
//			"-binarymessages", "false",
			"-gui", "false",
			"-cli", "false",
			"-simulation", "false", "-printsecret", "false"};
		
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
		
		Starter.createPlatform(defargs).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(
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
		final IComponentIdentifier root, final IResultListener<Map<String,Object>> reslis)
	{
		return createComponent(filename, null, null, root, reslis);
	}
	
	/**
	 * 
	 */
	protected IFuture<IComponentIdentifier> createComponent(final String filename, final Map<String, Object> args, 
		final String config, final IComponentIdentifier root, final IResultListener<Map<String,Object>> reslis)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		IResourceIdentifier	rid	= new ResourceIdentifier(
		new LocalResourceIdentifier(root, agent.getModel().getResourceIdentifier().getLocalIdentifier().getUri()), null);
//		boolean	local = root.equals(agent.getComponentIdentifier().getRoot());
//		CreationInfo ci	= new CreationInfo(local? agent.getComponentIdentifier(): root, rid);
		CreationInfo ci	= new CreationInfo(rid);
		ci.setArguments(args);
		ci.setConfiguration(config);
		ci.setFilename(filename);
		agent.getExternalAccess(root==null? agent.getId(): root).createComponent(ci)
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(IExternalAccess result)
			{
				result.waitForTermination().addResultListener(reslis);
				ret.setResult(result.getId());
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
	 * 
	 */
	protected IFuture<Map<String, Object>> destroyComponent(final IComponentIdentifier cid)
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		agent.getExternalAccess(cid).killComponent().addResultListener(new DelegationResultListener<Map<String, Object>>(ret));
		
		return ret;
	}
	
	/**
	 *  Setup a local test.
	 */
	protected IFuture<IComponentIdentifier>	setupLocalTest(String filename, IResultListener<Map<String,Object>> reslis)
	{
		return createComponent(filename, agent.getId().getRoot(), reslis);
	}
	
	/**
	 *  Setup a remote test.
	 */
	protected IFuture<IComponentIdentifier>	setupRemoteTest(final String filename, final String config,
		final IResultListener<Map<String,Object>> reslis)
	{
		final Future<IComponentIdentifier>	ret	= new Future<IComponentIdentifier>();
		
		createPlatform(null).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(final IExternalAccess exta)
			{
				
//				exta.getServiceProvider().searchService( new ServiceQuery<>( IComponentManagementService.class, ServiceScope.PLATFORM))
//					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
//				{
//					public void customResultAvailable(IComponentManagementService cms)
//					{
//						cms.addComponentListener(exta.getComponentIdentifier(), new ICMSComponentListener()
//						{
//							public IFuture<Void> componentRemoved(IComponentDescription desc, Map<String, Object> results)
//							{
//								platforms.remove(desc.getName());
//								return IFuture.DONE;
//							}
//							
//							public IFuture<Void> componentChanged(IComponentDescription desc)
//							{
//								return IFuture.DONE;
//							}
//							
//							public IFuture<Void> componentAdded(IComponentDescription desc)
//							{
//								return IFuture.DONE;
//							}
//						});
//					}
//				});
				
				Starter.createProxy(agent.getExternalAccess(), exta).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IComponentIdentifier>(ret)
				{
					public void customResultAvailable(IExternalAccess result)
					{
						// inverse proxy from remote to local.
						Starter.createProxy(exta, agent.getExternalAccess())
							.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IComponentIdentifier>(ret)
						{
							public void customResultAvailable(IExternalAccess result)
							{
								if(filename!=null)
								{
									createComponent(filename, null, config, exta.getId(), reslis)
										.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
								}
								else
								{
									ret.setResult(null);
								}
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Create remote platform and add proxies on both sides.
	 */
	protected IFuture<IExternalAccess>	setupRemotePlatform()
	{
		final Future<IExternalAccess> ret	= new Future<IExternalAccess>();
		
		createPlatform(null).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IExternalAccess>(ret)
		{
			public void customResultAvailable(final IExternalAccess exta)
			{
				Starter.createProxy(agent.getExternalAccess(), exta)
					.addResultListener(new DelegationResultListener<IExternalAccess>(ret)
				{
					public void customResultAvailable(IExternalAccess result)
					{
						// inverse proxy from remote to local.
						Starter.createProxy(exta, agent.getExternalAccess())
							.addResultListener(new DelegationResultListener<IExternalAccess>(ret)
						{
							public void customResultAvailable(IExternalAccess result)
							{
								ret.setResult(exta);
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Create remote platform and add proxies on all sides.
	 */
	protected IFuture<Void>	setupRemotePlatforms(final int n, final int cnt, final List<IExternalAccess> platforms)
	{
		final Future<Void> ret	= new Future<Void>();
		
		if(cnt<n)
		{
			createPlatform(null).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
			{
				public void customResultAvailable(final IExternalAccess exta)
				{
					System.out.println("creating platform: "+cnt);
					CounterResultListener<IExternalAccess> lis = new CounterResultListener<IExternalAccess>(platforms.size()*2, new DelegationResultListener<Void>(ret)
					{
						public void customResultAvailable(Void result) 
						{
							platforms.add(exta);
							setupRemotePlatforms(n, cnt+1, platforms).addResultListener(new DelegationResultListener<Void>(ret));
						}
					});
					
					for(IExternalAccess other: platforms)
					{
						// connect other platforms with new one
						Starter.createProxy(other, exta).addResultListener(lis);
						// connect this platform to all others
						Starter.createProxy(exta, other).addResultListener(lis);
					}
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
//	public <T> IFuture<T>	waitForRealtimeDelay(final long delay, final IComponentStep<T> step)
//	{
//		final Future<T>	ret	= new Future<T>();
//		IFuture<IClockService>	clockfut	= agent.getFeature(IRequiredServicesFeature.class).getService("clock");
//		clockfut.addResultListener(new ExceptionDelegationResultListener<IClockService, T>(ret)
//		{
//			public void customResultAvailable(IClockService clock)
//			{
//				clock.createRealtimeTimer(delay, new ITimedObject()
//				{
//					public void timeEventOccurred(long currenttime)
//					{
//						agent.getFeature(IExecutionFeature.class).scheduleStep(step).addResultListener(new DelegationResultListener<T>(ret));
//					}
//				});
//			}
//		});
//		return ret;
//	}
	
	/**
	 *  Perform  the test.
	 *  @param cms	The cms of the platform to test (local or remote).
	 * 	@param local	True when tests runs on local platform. 
	 *  @return	The test result.
	 */
	protected IFuture<TestReport>	test(IExternalAccess platform, boolean local)
	{
		throw new UnsupportedOperationException("Implement test() or performTests()");
	}
}
