package jadex.platform.service.awareness.management;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.awareness.IDiscoveryService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;


/**
 *  Agent that sends multicasts to locate other Jadex awareness agents.
 */
@Description("This agent looks for other awareness agents in the local net.")
@Arguments(
{
//	@Argument(name="address", clazz=String.class, defaultvalue="\"224.0.0.0\"", description="The ip multicast address used for finding other agents (range 224.0.0.0-239.255.255.255)."),
//	@Argument(name="port", clazz=int.class, defaultvalue="55667", description="The port used for finding other agents."),
	@Argument(name="mechanisms", clazz=String.class, description="The discovery mechanisms."),
	@Argument(name="delay", clazz=long.class, defaultvalue="10000", description="The delay between sending awareness infos (in milliseconds)."),
	@Argument(name="fast", clazz=boolean.class, defaultvalue="true", description="Flag for enabling fast startup awareness (pingpong send behavior)."),
	@Argument(name="autocreate", clazz=boolean.class, defaultvalue="true", description="Set if new proxies should be automatically created when discovering new components."),
	@Argument(name="autodelete", clazz=boolean.class, defaultvalue="true", description="Set if proxies should be automatically deleted when not discovered any longer."),
	@Argument(name="proxydelay", clazz=long.class, defaultvalue="15000", description="The delay used by proxies."),
	@Argument(name="includes", clazz=String.class, defaultvalue="\"\"", description="A list of platforms/IPs/hostnames to include (comma separated). Matches start of platform/IP/hostname."),
	@Argument(name="excludes", clazz=String.class, defaultvalue="\"\"", description="A list of platforms/IPs/hostnames to exclude (comma separated). Matches start of platform/IP/hostname.")
})
@ComponentTypes({
	/* if[android]
	@ComponentType(name = "Bluetooth", filename = "jadex/platform/service/awareness/discovery/bluetoothp2p/BluetoothP2PDiscoveryAgent.class"),
	end[android]*/
	@ComponentType(name="Broadcast", filename="jadex/platform/service/awareness/discovery/ipbroadcast/BroadcastDiscoveryAgent.class"),
	@ComponentType(name="Multicast", filename="jadex/platform/service/awareness/discovery/ipmulticast/MulticastDiscoveryAgent.class"),
	@ComponentType(name="Scanner", filename="jadex/platform/service/awareness/discovery/ipscanner/ScannerDiscoveryAgent.class"),
	@ComponentType(name="Registry", filename="jadex/platform/service/awareness/discovery/registry/RegistryDiscoveryAgent.class"),
	@ComponentType(name="Message", filename="jadex/platform/service/awareness/discovery/message/MessageDiscoveryAgent.class"),
	@ComponentType(name="Relay", filename="jadex/platform/service/awareness/discovery/relay/RelayDiscoveryAgent.class")
})
/**@Configurations(
{
	@Configuration(name="Frequent updates (10s)", arguments=@NameValue(name="delay", value="10000")), 
	@Configuration(name="Medium updates (20s)", arguments=@NameValue(name="delay", value="20000")),
	@Configuration(name="Seldom updates (60s)", arguments=@NameValue(name="delay", value="60000"))
})*/
@Properties(@NameValue(name="componentviewer.viewerclass", value="new String[]{\"jadex.tools.awareness.AwarenessAgentPanel\", \"jadex.android.controlcenter.settings.AwarenessSettings\"}"))
@ProvidedServices(
	@ProvidedService(type=IAwarenessManagementService.class, implementation=@Implementation(expression="$component"))
)
@RequiredServices(
{
	@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="settings", type=ISettingsService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="discoveries", type=IDiscoveryService.class, multiple=true, binding=@Binding(scope=RequiredServiceInfo.SCOPE_COMPONENT))
})
@Service(IAwarenessManagementService.class)
//@GuiClassNames({ 
//	@GuiClassName("jadex.android.controlcenter.settings.AwarenessSettingsScreen"),
//	@GuiClassName("jadex.tools.awareness.AwarenessAgentPanel") 
//})
public class AwarenessManagementAgent extends MicroAgent implements IPropertiesProvider, IAwarenessManagementService
{
	//-------- attributes --------
	
	/** The send delay. */
	protected long delay;
	
	/** Flag for enabling fast startup awareness (pingpong send behavior). */
	protected boolean fast;
	
	/** Flag indicating if proxies should be automatically created. */
	protected boolean autocreate;
	
	/** Flag indicating if proxies should be automatically deleted. */
	protected boolean autodelete;

	/** The discovered components. */
	protected Map<IComponentIdentifier, DiscoveryInfo> discovered;
	
	/** The discovery listeners. */
	protected Set<SubscriptionIntermediateDelegationFuture<DiscoveryInfo>>	listeners;
	
	/** The timer. */
	protected Timer	timer;
	
	/** The root component id. */
	protected IComponentIdentifier root;
	
	/** The includes list. */
	protected List<String>	includes;
	
	/** The excludes list. */
	protected List<String>	excludes;
	
	/** The platforms creation future. */
	protected Future<IComponentIdentifier> pcreatefut;
	
	/** The cms, cached for speed. */
	protected IComponentManagementService	cms;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture<Void>	agentCreated()
	{
//		String[] test = new String[]{"test", "test2"};
		System.out.println("curcall awa: "+CallAccess.getCurrentInvocation().getCause());

		initArguments();
		
		this.discovered = new LinkedHashMap<IComponentIdentifier, DiscoveryInfo>();
		
		final Future<Void>	ret	= new Future<Void>();
		
		IFuture<ISettingsService>	setfut	= getServiceContainer().getRequiredService("settings");
		setfut.addResultListener(new IResultListener<ISettingsService>()
		{
			public void resultAvailable(ISettingsService settings)
			{
				settings.registerPropertiesProvider(getAgentName(), AwarenessManagementAgent.this)
					.addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						proceed();
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// No settings service: ignore.
				proceed();
			}
			
			protected void	proceed()
			{
				final String mechas = (String)getArgument("mechanisms");
				if(mechas!=null)
				{
					IFuture<IComponentManagementService>	cmsfut	= getServiceContainer().getRequiredService("cms");
					cmsfut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
					{
						public void customResultAvailable(IComponentManagementService cms)
						{
							AwarenessManagementAgent.this.cms	= cms;
							StringTokenizer	stok	= new StringTokenizer(mechas, ", \r\n\t");
							CounterResultListener<IComponentIdentifier> lis = new CounterResultListener<IComponentIdentifier>(stok.countTokens(), 
								false, new DelegationResultListener<Void>(ret)
							{
								public void customResultAvailable(Void result)
								{
									ret.setResult(null);
								}
							});
							
							CreationInfo info = new CreationInfo(getComponentIdentifier());
							info.setConfiguration(getConfiguration());
							Map<String, Object> args = new HashMap<String, Object>();
							args.put("delay", new Long(getDelay()));
							args.put("fast", isFastAwareness() ? Boolean.TRUE : Boolean.FALSE);
							args.put("includes", getIncludes());
							args.put("excludes", getExcludes());
							info.setArguments(args);
							
							System.out.println("curcall awa: "+CallAccess.getCurrentInvocation().getCause());
							
							while(stok.hasMoreTokens())
							{
	//							System.out.println("mecha: "+mechas[i]);
								cms.createComponent(null, stok.nextToken(), info, null).addResultListener(lis);
							}
						}
					});
				}
				else
				{
					ret.setResult(null);
				}
			}
		});

		
		return ret;
	}
	
	/**
	 *  Read arguments and set initial values. 
	 */
	protected void initArguments()
	{
		this.delay = ((Number)getArgument("delay")).longValue();
		this.autocreate = ((Boolean)getArgument("autocreate")).booleanValue();
		this.autodelete = ((Boolean)getArgument("autodelete")).booleanValue();
		
		this.includes	= new ArrayList<String>();
		StringTokenizer	stok	= new StringTokenizer((String)getArgument("includes"), ",");
		while(stok.hasMoreTokens())
		{
			includes.add(stok.nextToken().trim());
		}
		
		this.excludes	= new ArrayList<String>();
		stok	= new StringTokenizer((String)getArgument("excludes"), ",");
		while(stok.hasMoreTokens())
		{
			excludes.add(stok.nextToken().trim());
		}
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public IFuture<Void> executeBody()
	{
		root = getComponentIdentifier().getRoot();
		startRemoveBehaviour();
		return new Future<Void>(); // never kill by return
	}
	
	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture<Void>	agentKilled()
	{
		final Future<Void>	ret	= new Future<Void>();
		IFuture<ISettingsService>	setfut	= getServiceContainer().getRequiredService("settings");
		setfut.addResultListener(new IResultListener<ISettingsService>()
		{
			public void resultAvailable(ISettingsService settings)
			{
				settings.deregisterPropertiesProvider(getAgentName())
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// No settings service: ignore.
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Announce the discovered components.
	 *  @param infos The infos.
	 */
	public IFuture<Boolean> addAwarenessInfo(AwarenessInfo info)
	{
		// Return if inital discovery.
		boolean ret = false;
		
		// Fix broken awareness infos for backwards compatibility.
		if(info.getDelay()==0)
			info.setDelay(delay);
		if(info.getState()==null)
			info.setState(AwarenessInfo.STATE_ONLINE);
//		System.out.println("received: "+getComponentIdentifier()+" "+info.getSender());
		
		IComponentIdentifier sender = info.getSender();
		boolean	online	= AwarenessInfo.STATE_ONLINE.equals(info.getState());
//			boolean	initial	= false;	// Initial discovery of component.
		DiscoveryInfo dif;
		boolean	changedaddrs	= false;	// Should an existing proxy be updated with new addresses?
		
		dif = (DiscoveryInfo)discovered.get(sender);
		final String awamech = info.getProperties().get(AwarenessInfo.PROPERTY_AWAMECHANISM);
		
		// Hack!!! Used from relay if connection disappeared
		if(awamech!=null && AwarenessInfo.STATE_ALLOFFLINE.equals(info.getState()))
		{
			for(DiscoveryInfo di: discovered.values().toArray(new DiscoveryInfo[0]))
			{
				di.removeTimeDelay(awamech);
			}
			return new Future<Boolean>(false);
		}
		
		if(online)
		{
			boolean	remoteexcluded	= !isIncluded(root, info.getIncludes(), info.getExcludes());
			if(dif==null)
			{
				dif = new DiscoveryInfo(sender, null, remoteexcluded, info.getProperties());
				dif.setTimeDelay(awamech, getClockTime(), info.getDelay());
				discovered.put(sender, dif);
				informListeners(dif);
				ret	= true;
			}
			else
			{
				changedaddrs = !SUtil.arrayEquals(dif.getComponentIdentifier().getAddresses(), sender.getAddresses());
				dif.setComponentIdentifier(sender);
				
				if(awamech!=null)
				{
					dif.setTimeDelay(awamech, getClockTime(), info.getDelay());
					informListeners(dif);
				}
				else
				{
					// Hack!!! Only update times when longer valid.
					if(dif.getDelay(null)!=-1)
					{
						if(info.getDelay()==-1 || getClockTime()+info.getDelay()>dif.getTime()+dif.getDelay())
						{
							dif.setTimeDelay(null, getClockTime(), info.getDelay());
							informListeners(dif);
						}
					}
					else
					{
						// Allow users to keep track of recency of platform info.
						dif.setTimeDelay(null, getClockTime(), info.getDelay());
//						dif.setTime(getClockTime());
					}
				}
				
				dif.setRemoteExcluded(remoteexcluded);
			}
			
			if(isIncluded(sender, getIncludes(), getExcludes())
				&& !remoteexcluded && isAutoCreateProxy() && dif.getProxy()==null)
			{
				createProxy(dif);
				
//					if(initial && fast && started && !killed)
//					{
////						System.out.println(System.currentTimeMillis()+" fast discovery: "+getComponentIdentifier()+", "+sender);
//						received_self	= false;
//						waitFor((long)(Math.random()*500), new IComponentStep()
//						{
//							int	cnt;
//							public Object execute(IInternalAccess ia)
//							{
//								if(!received_self)
//								{
//									cnt++;
////									System.out.println("CSMACD try #"+(++cnt));
//									send(new AwarenessInfo(root, AwarenessInfo.STATE_ONLINE, delay, includes, excludes));
//									waitFor((long)(Math.random()*500*cnt), this);
//								}
//								return null;
//							}
//						});
//					}
			}
			
			// Update proxy to reflect new addresses.
			else if(changedaddrs && dif.getProxy()!=null)
			{
				final IComponentIdentifier	remote	= dif.getComponentIdentifier();
				dif.getProxy().addResultListener(new IResultListener<IComponentIdentifier>()
				{
					public void resultAvailable(IComponentIdentifier cid)
					{
						SServiceProvider.getService(getServiceProvider(), cid, IProxyAgentService.class)
							.addResultListener(new IResultListener<IProxyAgentService>()
						{
							public void resultAvailable(IProxyAgentService pas)
							{
								pas.setRemoteComponentIdentifier(remote);
								//	.addResultListener() -> ignore
							}
							
							public void exceptionOccurred(Exception exception)
							{
								// No proxy: ingore.
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// No proxy: ingore.
					}
				});
			}
		}
		else
		{
			discovered.remove(sender);
			if(dif!=null)
			{
				dif.removeTimeDelay(awamech);
//				dif.setTime(-1);
				informListeners(dif);
				deleteProxy(dif);
			}
		}
		
		return new Future<Boolean>(ret? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Get the discovery info for a platform, if any.
	 *  @param cid	The platform id.
	 *  @return The discovery info.
	 */
	public IFuture<DiscoveryInfo> getPlatformInfo(IComponentIdentifier cid)
	{
		return new Future<DiscoveryInfo>(discovered.get(cid));
	}
	
	/**
	 *  Get the currently known platforms.
	 *  @return The discovery infos of known platforms.
	 */
	public IFuture<Collection<DiscoveryInfo>> getKnownPlatforms()
	{
		return new Future<Collection<DiscoveryInfo>>(discovered.values());
	}
	
	/**
	 *  Retrieve information about platforms as they appear or vanish.
	 *  @param include_initial	If true, information about initially known platforms will be immediately posted to the caller.
	 *  	Otherwise only changes that happen after the subscription will be posted. 
	 *  @return An intermediate future that is notified about any changes.
	 */
	public ISubscriptionIntermediateFuture<DiscoveryInfo> subscribeToPlatformList(boolean include_initial)
	{
		if(listeners==null)
		{
			listeners	= new LinkedHashSet<SubscriptionIntermediateDelegationFuture<DiscoveryInfo>>();
		}
		SubscriptionIntermediateDelegationFuture<DiscoveryInfo>	ret	= new SubscriptionIntermediateDelegationFuture<DiscoveryInfo>();
		listeners.add(ret);
		
		if(include_initial)
		{
			for(DiscoveryInfo dif: discovered.values())
			{
				ret.addIntermediateResult(dif);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Inform listeners about a changed discovery info.
	 */
	protected void	informListeners(DiscoveryInfo dif)
	{
		if(listeners!=null)
		{
			for(Iterator<SubscriptionIntermediateDelegationFuture<DiscoveryInfo>> it=listeners.iterator(); it.hasNext() ; )
			{
				SubscriptionIntermediateDelegationFuture<DiscoveryInfo>	fut	= it.next();
				
				try
				{
					if(!fut.addIntermediateResultIfUndone(dif))
					{
						// Future terminated, i.e., subscription cancelled.
						it.remove();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 *  Get the delay.
	 *  @return the delay.
	 */
	public synchronized long getDelay()
	{
		return delay;
	}

	/**
	 *  Set the delay.
	 *  @param delay The delay to set.
	 */
	public synchronized void setDelay(final long delay)
	{
//		System.out.println("setDelay: "+delay+" "+getComponentIdentifier());
//		if(this.delay>=0 && delay>0)
//			scheduleStep(send);
		if(this.delay!=delay)
		{
			this.delay = delay;
//			startSendBehaviour();
		}
		
		IIntermediateFuture<IDiscoveryService>	disfut	= getRequiredServices("discoveries");
		disfut.addResultListener(new IntermediateDefaultResultListener<IDiscoveryService>()
		{
			public void intermediateResultAvailable(IDiscoveryService ds)
			{
				ds.setDelay(delay);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// No discoveries: ignore
			}
		});
	}
	

	/**
	 *  Set the fast startup awareness flag
	 *  @param fast	The fast awareness flag.
	 */
	public void setFastAwareness(final boolean fast)
	{
		this.fast = fast;

		IIntermediateFuture<IDiscoveryService>	disfut	= getRequiredServices("discoveries");
		disfut.addResultListener(new IntermediateDefaultResultListener<IDiscoveryService>()
		{
			public void intermediateResultAvailable(IDiscoveryService ds)
			{
				ds.setFast(fast);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// No discoveries: ignore
			}
		});
}
	
	/**
	 *  Get the fast startup awareness flag.
	 *  @return The fast flag.
	 */
	public boolean isFastAwareness()
	{
		return this.fast;
	}

	
	/**
	 *  Get the autocreate.
	 *  @return the autocreate.
	 */
	public synchronized boolean isAutoCreateProxy()
	{
		return autocreate;
	}

	/**
	 *  Set the autocreate.
	 *  @param autocreate The autocreate to set.
	 */
	public synchronized void setAutoCreateProxy(boolean autocreate)
	{
		this.autocreate = autocreate;
	}
	
	/**
	 *  Get the autodelete.
	 *  @return the autodelete.
	 */
	public synchronized boolean isAutoDeleteProxy()
	{
		return autodelete;
	}

	/**
	 *  Set the autodelete.
	 *  @param autodelete The autodelete to set.
	 */
	public synchronized void setAutoDeleteProxy(boolean autodelete)
	{
		this.autodelete = autodelete;
	}

	/**
	 *  Get the includes.
	 *  @return the includes.
	 */
	public synchronized String[] getIncludes()
	{
		return (String[])includes.toArray(new String[includes.size()]);
	}

	/**
	 *  Set the includes.
	 *  @param includes The includes to set.
	 */
	public void setIncludes(final String[] includes)
	{
		synchronized(this)
		{
			this.includes	= new ArrayList<String>(Arrays.asList(includes));
		}
		
		IIntermediateFuture<IDiscoveryService>	disfut	= getRequiredServices("discoveries");
		disfut.addResultListener(new IntermediateDefaultResultListener<IDiscoveryService>()
		{
			public void intermediateResultAvailable(IDiscoveryService ds)
			{
				ds.setIncludes(includes);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// No discoveries: ignore
			}
		});
	}

	/**
	 *  Set the excludes.
	 *  @param excludes The excludes to set.
	 */
	public void setExcludes(final String[] excludes)
	{
		synchronized(this)
		{
			this.excludes	= new ArrayList<String>(Arrays.asList(excludes));
		}
		
		IIntermediateFuture<IDiscoveryService>	disfut	= getRequiredServices("discoveries");
		disfut.addResultListener(new IntermediateDefaultResultListener<IDiscoveryService>()
		{
			public void intermediateResultAvailable(IDiscoveryService ds)
			{
				ds.setExcludes(excludes);
			}

			public void exceptionOccurred(Exception exception)
			{
				// No discoveries: ignore
			}
		});
	}
	
	/**
	 *  Get the excludes.
	 *  @return the excludes.
	 */
	public synchronized String[] getExcludes()
	{
		return (String[])excludes.toArray(new String[excludes.size()]);
	}
	
	/**
	 *  Get the discovered.
	 *  @return the discovered.
	 */
	public synchronized DiscoveryInfo[] getDiscoveryInfos()
	{
		return (DiscoveryInfo[])discovered.values().toArray(new DiscoveryInfo[discovered.size()]);
	}

	/**
	 *  Start removing discovered proxies.
	 */
	protected void startRemoveBehaviour()
	{
		scheduleStep(new IComponentStep<Void>()
		{
			@Classname("rem")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				List<DiscoveryInfo> todel = autodelete? new ArrayList<DiscoveryInfo>(): null;
				synchronized(AwarenessManagementAgent.this)
				{
					for(Iterator<DiscoveryInfo> it=discovered.values().iterator(); it.hasNext(); )
					{
						DiscoveryInfo dif = it.next();
						if(!dif.isAlive())
						{
//							System.out.println("Removing: "+dif);
							it.remove();
							informListeners(dif);
							if(autodelete)
							{
								todel.add(dif);
							}
						}
						
						// Check if the proxies still exist
						checkProxy(dif);
					}
				}
				
				if(todel!=null)
				{
					for(int i=0; i<todel.size(); i++)
					{
						DiscoveryInfo dif = todel.get(i);
						// Ignore deletion failures
						deleteProxy(dif);
					}
				}
				
				doWaitFor(5000, this);
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Check if local proxy is still available.
	 *  @param dif	The discovery info.
	 */
	public void checkProxy(final DiscoveryInfo dif)
	{
		// Only need to check, when proxy already created
		if(dif.getProxy()!=null && dif.getProxy().isDone() && dif.getProxy().getException()==null)
		{
			IComponentIdentifier	proxy	= dif.getProxy().get(null);
			cms.getComponentDescription(proxy)
				.addResultListener(new IResultListener<IComponentDescription>()
			{
				public void resultAvailable(IComponentDescription result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					dif.setProxy(null);
					informListeners(dif);
				}
			});
		}
	}
	
	/**
	 *  Get a discovery info.
	 *  @param cid	The component id;
	 *  @return the discovery info.
	 */
	public synchronized DiscoveryInfo getDiscoveryInfo(IComponentIdentifier cid)
	{
		return (DiscoveryInfo)discovered.get(cid);
	}
	
	/**
	 *  Get the proxy holder component.
	 *  (Creates it if it does not exist).
	 */
	protected IFuture<IComponentIdentifier> getProxyHolder()
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		final ComponentIdentifier cid = new ComponentIdentifier("platforms", getComponentIdentifier().getRoot());
		
		cms.getExternalAccess(cid).addResultListener(new IResultListener<IExternalAccess>()
		{
			public void resultAvailable(IExternalAccess exta)
			{
				ret.setResult(exta.getComponentIdentifier());
			}
			
			public void exceptionOccurred(Exception exception)
			{
				createProxyHolder().addResultListener((new DelegationResultListener<IComponentIdentifier>(ret)));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Create the platform proxy holder component.
	 *  (Can be called multiple times).
	 */
	protected IFuture<IComponentIdentifier> createProxyHolder()
	{
		if(pcreatefut!=null)
		{
			return pcreatefut;
		}
		else
		{
			pcreatefut = new Future<IComponentIdentifier>();
			CreationInfo	ci	= new CreationInfo(getComponentIdentifier().getRoot());
			ci.setDaemon(Boolean.TRUE);
			cms.createComponent("platforms", "jadex/platform/service/awareness/RemotePlatformAgent.class", ci, null)
				.addResultListener(new DelegationResultListener<IComponentIdentifier>(pcreatefut));
		}
		
		return pcreatefut;
	}
	
//	int	cnt;
	
	/**
	 *  Create a proxy using given settings.
	 *  @param dif	The discovery info
	 *  @return The id of the created proxy.
	 */
	public IFuture<IComponentIdentifier> createProxy(final DiscoveryInfo dif)
	{
		final Map<String, Object>	args = new HashMap<String, Object>();
		args.put("component", dif.getComponentIdentifier());
		
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		dif.setProxy(ret);
		ret.addResultListener(new IResultListener<IComponentIdentifier>()
		{
			public void resultAvailable(IComponentIdentifier result)
			{
				informListeners(dif);
			}
			public void exceptionOccurred(Exception exception)
			{
				dif.setProxy(null);
				informListeners(dif);
			}
		});
		
		if(dif.getComponentIdentifier().equals(root))
		{
			ret.setException(new RuntimeException("Proxy for local components not allowed"));
		}
		else
		{
			getProxyHolder().addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
			{
				public void customResultAvailable(IComponentIdentifier parent)
				{
					CreationInfo ci = new CreationInfo(args);
					ci.setDaemon(true);
					ci.setParent(parent);
					
//					System.out.println("create proxy: "+(++cnt));
					
					cms.createComponent(dif.getComponentIdentifier().getLocalName(), "jadex/platform/service/remote/ProxyAgent.class", ci, 
						createResultListener(new DefaultResultListener<Collection<Tuple2<String, Object>>>(getLogger())
					{
						public void resultAvailable(Collection<Tuple2<String, Object>> result)
						{
//									System.out.println("Proxy killed: "+source);
							dif.setProxy(null);
							informListeners(dif);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							if(!(exception instanceof ComponentTerminatedException))
								super.exceptionOccurred(exception);
						}
					})).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Delete a proxy.
	 *  @param dif	The discovery info.
	 *  @return Future to indicate success.
	 */
	public IFuture<Void> deleteProxy(final DiscoveryInfo dif)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(dif.getProxy()!=null)
		{
			dif.getProxy().addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
			{
				public void customResultAvailable(final IComponentIdentifier proxy)
				{
//					System.out.println("awareness destroy: "+proxy);
					cms.destroyComponent(proxy).addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, Void>(ret)
					{
						public void customResultAvailable(Map<String, Object> result)
						{
							dif.setProxy(null);
							informListeners(dif);
							ret.setResult(null);
						}
					});
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Test if a platform is included and/or not excluded.
	 *  @param cid	The platform id.
	 *  @param includes	The list of includes.
	 *  @param excludes	The list of excludes.
	 *  @return true when a proxy should be created.
	 */
	public static boolean	isIncluded(IComponentIdentifier cid, String[] includes, String[] excludes)
	{
		boolean	included	= includes.length==0;
		String[]	cidnames	= null;
		
		// Check if contained in includes.
		for(int i=0; !included && i<includes.length; i++)
		{
			if(includes[i]!=null)
			{
				if(cidnames==null)
					cidnames	= extractNames(cid);
				for(int j=0; !included && j<cidnames.length; j++)
				{
					included	= cidnames[j].startsWith(includes[i]);
				}
			}
		}
		
		// Check if not contained in excludes.
		for(int i=0; included && i<excludes.length; i++)
		{
			if(excludes[i]!=null)
			{
				if(cidnames==null)
					cidnames	= extractNames(cid);
				for(int j=0; included && j<cidnames.length; j++)
				{
					included	= !cidnames[j].startsWith(excludes[i]);
				}
			}
		}

		return included;
	}
	
	/**
	 *  Extract names for matching to includes/excludes list.
	 */
	protected static String[]	extractNames(IComponentIdentifier cid)
	{
		Set<String>	ret	= new LinkedHashSet<String>();
		ret.add(cid.getName());
		String[]	addrs	= cid.getAddresses();
		for(int i=0; i<addrs.length; i++)
		{
			int	prot	= addrs[i].indexOf("://");
			if(prot!=-1)
			{
				int	slash	= addrs[i].indexOf('/', prot+3);
				if(slash!=-1)
				{
					int	port	= addrs[i].lastIndexOf(':', slash);
					if(port!=-1 && port>prot+3)
					{
						ret.add(addrs[i].substring(prot+3, port));
					}
					else
					{
						ret.add(addrs[i].substring(prot+3, slash));
					}
				}
				else
				{
					int	port	= addrs[i].lastIndexOf(':');
					if(port!=-1 && port>prot+3)
					{
						ret.add(addrs[i].substring(prot+3, port));
					}
					else
					{
						ret.add(addrs[i].substring(prot+3));
					}
				}
			}
			else
			{
				System.out.println("Warning: Unknown address scheme "+addrs[i]);
			}
		}
//		System.out.println("cidnames: "+ret);
		return (String[])ret.toArray(new String[ret.size()]);
	}
	
	/**
	 *  Get the current time.
	 */
	protected long getClockTime()
	{
//		return clock.getTime();
		return System.currentTimeMillis();
	}
	
	/**
	 *  Wait for impl.
	 */
	protected void	doWaitFor(long delay, final IComponentStep<?> step)
	{
//		waitFor(delay, step);
		
		if(timer==null)
			timer	= new Timer(true);
		
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				try
				{
					scheduleStep(step);
				}
				catch(ComponentTerminatedException e)
				{
					// ignore
				}
			}
		}, delay);
	}
	
	//-------- IPropertiesProvider interface --------
	
	/**
	 *  Update from given properties.
	 */
	public IFuture<Void> setProperties(final jadex.commons.Properties props)
	{
		return scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				try
				{
//					setAddressInfo(InetAddress.getByName(props.getStringProperty("address")), props.getIntProperty("port"));
					long delay = props.getLongProperty("delay");
					if(delay>0)
						setDelay(delay);
					setFastAwareness(props.getProperty("fast")!=null ? props.getBooleanProperty("fast") : true);
					setAutoCreateProxy(props.getProperty("autocreate")!=null ? props.getBooleanProperty("autocreate") : true);
					setAutoDeleteProxy(props.getProperty("autodelete")!=null ? props.getBooleanProperty("autodelete") : true);
					
					Property[]	pincs	= props.getProperties("include");
					String[]	incs	= new String[pincs.length];
					for(int i=0; i<pincs.length; i++)
						incs[i]	= pincs[i].getValue();
					setIncludes(incs);
	
					Property[]	pexcs	= props.getProperties("exclude");
					String[]	excs	= new String[pexcs.length];
					for(int i=0; i<pexcs.length; i++)
						excs[i]	= pexcs[i].getValue();
					setExcludes(excs);
					
					return IFuture.DONE;
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		});
	}
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture<jadex.commons.Properties> getProperties()
	{
		return scheduleStep(new IComponentStep<jadex.commons.Properties>()
		{
			public IFuture<jadex.commons.Properties> execute(IInternalAccess ia)
			{
				jadex.commons.Properties	props	= new jadex.commons.Properties();
//				props.addProperty(new Property("address", address.getHostAddress()));
//				props.addProperty(new Property("port", ""+port));
//				props.addProperty(new Property("delay", ""+delay));
				props.addProperty(new Property("fast", ""+fast));
				props.addProperty(new Property("autocreate", ""+autocreate));
				props.addProperty(new Property("autodelete", ""+autodelete));
				for(int i=0; i<includes.size(); i++)
					props.addProperty(new Property("include", includes.get(i).toString()));
				for(int i=0; i<excludes.size(); i++)
					props.addProperty(new Property("exclude", excludes.get(i).toString()));
				return new Future<jadex.commons.Properties>(props);
			}
		});		
	}
}

