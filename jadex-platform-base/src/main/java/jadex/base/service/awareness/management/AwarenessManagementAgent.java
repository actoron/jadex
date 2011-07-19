package jadex.base.service.awareness.management;

import jadex.base.service.awareness.AwarenessInfo;
import jadex.base.service.awareness.discovery.IDiscoveryService;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ISettingsService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Property;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.xml.annotation.XMLClassname;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
	@Argument(name="delay", clazz=long.class, defaultvalue="10000", description="The delay between sending awareness infos (in milliseconds)."),
//	@Argument(name="fast", clazz=boolean.class, defaultvalue="true", description="Flag for enabling fast startup awareness (pingpong send behavior)."),
	@Argument(name="autocreate", clazz=boolean.class, defaultvalue="true", description="Set if new proxies should be automatically created when discovering new components."),
	@Argument(name="autodelete", clazz=boolean.class, defaultvalue="true", description="Set if proxies should be automatically deleted when not discovered any longer."),
	@Argument(name="proxydelay", clazz=long.class, defaultvalue="15000", description="The delay used by proxies."),
	@Argument(name="includes", clazz=String.class, defaultvalue="\"\"", description="A list of platforms/IPs/hostnames to include (comma separated). Matches start of platform/IP/hostname."),
	@Argument(name="excludes", clazz=String.class, defaultvalue="\"\"", description="A list of platforms/IPs/hostnames to exclude (comma separated). Matches start of platform/IP/hostname.")
})
@ComponentTypes({
	@ComponentType(name="broadcastdis", filename="jadex/base/service/awareness/discovery/ipbroadcast/BroadcastDiscoveryAgent.class"),
	@ComponentType(name="multicastdis", filename="jadex/base/service/awareness/discovery/ipmulticast/MulticastDiscoveryAgent.class"),
	@ComponentType(name="scannerdis", filename="jadex/base/service/awareness/discovery/ipscanner/IPScannerDiscoveryAgent.class")
})
@Configurations(
{
	@Configuration(name="Frequent updates (10s)", arguments=@NameValue(name="delay", value="10000"), 
		components=
		{
			@Component(name="broadcastdis", type="broadcastdis")
//			@Component(name="multicastdis", type="multicastdis")
//			@Component(name="scannerdis", type="scannerdis")
		}),
	@Configuration(name="Medium updates (20s)", arguments=@NameValue(name="delay", value="20000"),
		components=
		{
			@Component(name="broadcastdis", type="broadcastdis")
//			@Component(name="multicastdis", type="multicastdis"),
//			@Component(name="scannerdis", type="scannerdis")
		}),
	@Configuration(name="Seldom updates (60s)", arguments=@NameValue(name="delay", value="60000"),
		components=
		{
			@Component(name="broadcastdis", type="broadcastdis")
//			@Component(name="multicastdis", type="multicastdis"),
//			@Component(name="scannerdis", type="scannerdis")
		})
})
@Properties(@NameValue(name="componentviewer.viewerclass", value="\"jadex.base.service.awareness.AwarenessAgentPanel\""))
@ProvidedServices(
	@ProvidedService(type=IManagementService.class, implementation=@Implementation(expression="$component"))
)
@RequiredServices(
{
	@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
//	@RequiredService(name="clock", type=IClockService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM),
	@RequiredService(name="settings", type=ISettingsService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="discovery", type=IDiscoveryService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
public class AwarenessManagementAgent extends MicroAgent implements IPropertiesProvider, IManagementService
{
	//-------- attributes --------
	
	/** The send delay. */
	protected long delay;
	
	/** Flag indicating if proxies should be automatically created. */
	protected boolean autocreate;
	
	/** Flag indicating if proxies should be automatically deleted. */
	protected boolean autodelete;

	/** The discovered components. */
	protected Map discovered;
	
	/** The timer. */
	protected Timer	timer;
	
	/** The root component id. */
	protected IComponentIdentifier root;
	
	/** The includes list. */
	protected List	includes;
	
	/** The excludes list. */
	protected List	excludes;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture	agentCreated()
	{
		initArguments();
		
		this.discovered = new LinkedHashMap();
		
		final Future	ret	= new Future();
		getServiceContainer().getRequiredService("settings").addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				ISettingsService	settings	= (ISettingsService)result;
				settings.registerPropertiesProvider(getAgentName(), AwarenessManagementAgent.this)
					.addResultListener(new DelegationResultListener(ret));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// No settings service: ignore.
				ret.setResult(null);
			}
		}));
		
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
		
		this.includes	= new ArrayList();
		StringTokenizer	stok	= new StringTokenizer((String)getArgument("includes"), ",");
		while(stok.hasMoreTokens())
		{
			includes.add(stok.nextToken().trim());
		}
		
		this.excludes	= new ArrayList();
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
	public void executeBody()
	{
		root = getComponentIdentifier().getRoot();
		discovered.put(root, new DiscoveryInfo(root, null, getClockTime(), delay, false));
		
		startRemoveBehaviour();
	}
	
	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture	agentKilled()
	{
		final Future	ret	= new Future();
		getServiceContainer().getRequiredService("settings").addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				ISettingsService	settings	= (ISettingsService)result;
				settings.deregisterPropertiesProvider(getAgentName())
					.addResultListener(new DelegationResultListener(ret));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// No settings service: ignore.
				ret.setResult(null);
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Announce the discovered components.
	 *  @param infos The infos.
	 */
	public void addAwarenessInfo(AwarenessInfo info)
	{
		// Fix broken awareness infos for backwards compatibility.
		if(info.getDelay()==0)
			info.setDelay(delay);
		if(info.getState()==null)
			info.setState(AwarenessInfo.STATE_ONLINE);
//		System.out.println(System.currentTimeMillis()+" "+getComponentIdentifier()+" received: "+info.getSender());

		IComponentIdentifier sender = info.getSender();
		boolean	online	= AwarenessInfo.STATE_ONLINE.equals(info.getState());
//			boolean	initial	= false;	// Initial discovery of component.
		DiscoveryInfo dif;
		
		dif = (DiscoveryInfo)discovered.get(sender);
		if(online)
		{
			boolean	remoteexcluded	= !isIncluded(root, info.getIncludes(), info.getExcludes());
			if(dif==null)
			{
				dif = new DiscoveryInfo(sender, null, getClockTime(), getDelay(), remoteexcluded);
				discovered.put(sender, dif);
//					initial	= true;
			}
			
			dif.setTime(getClockTime());
			
			if(isIncluded(sender, getIncludes(), getExcludes())
				&& !remoteexcluded && isAutoCreateProxy() && dif.getProxy()==null)
			{
				createProxy(sender);
				
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
		}
		else
		{
			if(dif!=null && dif.getProxy()!=null)
			{
				deleteProxy(dif);
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
	public synchronized void setDelay(long delay)
	{
//		System.out.println("setDelay: "+delay+" "+getComponentIdentifier());
//		if(this.delay>=0 && delay>0)
//			scheduleStep(send);
		if(this.delay!=delay)
		{
			this.delay = delay;
//			startSendBehaviour();
		}
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
			this.includes	= new ArrayList(Arrays.asList(includes));
		}
		
		getRequiredService("discovery").addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IDiscoveryService ds = (IDiscoveryService)result;
				ds.setIncludes(includes);
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
			this.excludes	= new ArrayList(Arrays.asList(excludes));
		}
		
		getRequiredService("discovery").addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IDiscoveryService ds = (IDiscoveryService)result;
				ds.setExcludes(excludes);
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
		scheduleStep(new IComponentStep()
		{
			@XMLClassname("rem")
			public Object execute(IInternalAccess ia)
			{
				List todel = autodelete? new ArrayList(): null;
				synchronized(AwarenessManagementAgent.this)
				{
					long time = getClockTime();
					for(Iterator it=discovered.values().iterator(); it.hasNext(); )
					{
						DiscoveryInfo dif = (DiscoveryInfo)it.next();
						// five seconds buffer
						if(time>dif.getTime()+dif.getDelay()*3.2) // Have some time buffer before delete
						{
//							System.out.println("Removing: "+dif);
							it.remove();
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
						DiscoveryInfo dif = (DiscoveryInfo)todel.get(i);
						// Ignore deletion failures
						deleteProxy(dif);
					}
				}
				
				doWaitFor(5000, this);
				return null;
			}
		});
	}
	
	/**
	 *  Check if local proxy is still available.
	 */
	public void checkProxy(final DiscoveryInfo dif)
	{
		getServiceContainer().getRequiredService("cms").addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if(dif.getProxy()!=null)
				{
					IComponentManagementService cms = (IComponentManagementService)result;
					cms.getComponentDescription(dif.getProxy()).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
						}
						
						public void exceptionOccurred(Exception exception)
						{
							dif.setProxy(null);
						}
					});
				}
			}
			public void exceptionOccurred(Exception exception) 
			{
				getLogger().warning("Could not get cms: "+exception);
			}
		}));
	}
	
	/**
	 *  Get a discovery info.
	 */
	public synchronized DiscoveryInfo getDiscoveryInfo(IComponentIdentifier cid)
	{
		return (DiscoveryInfo)discovered.get(cid);
	}
	
	/**
	 *  Create a proxy using given settings.
	 */
	public IFuture createProxy(final IComponentIdentifier cid)
	{
		final Map	args = new HashMap();
		args.put("component", cid);
		
		final Future ret = new Future();
		
		getServiceContainer().getRequiredService("cms").addResultListener(createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				
				if(cid.equals(root))
				{
					ret.setException(new RuntimeException("Proxy for local components not allowed"));
				}
				else
				{
					CreationInfo ci = new CreationInfo(args);
					cms.createComponent(cid.getLocalName(), "jadex/base/service/remote/ProxyAgent.class", ci, 
						createResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object result)
						{
//							System.out.println("Proxy killed: "+source);
							DiscoveryInfo dif = getDiscoveryInfo(cid);
							if(dif!=null)
								dif.setProxy(null);
						}
					})).addResultListener(createResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							DiscoveryInfo dif = getDiscoveryInfo(cid);
							if(dif!=null)
								dif.setProxy((IComponentIdentifier)result);
							ret.setResult(result);
						}
					}));
				}
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Delete a proxy.
	 */
	public IFuture deleteProxy(final DiscoveryInfo dif)
	{
		final Future ret = new Future();
		
		getServiceContainer().getRequiredService("cms").addResultListener(createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				
				IComponentIdentifier cid = dif.getProxy();
				if(cid!=null)
				{
//					System.out.println("awareness destroy: "+cid);
					cms.destroyComponent(cid).addResultListener(createResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							dif.setProxy(null);
							ret.setResult(result);
						}
					}));
				}
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Test if a platform is included and/or not excluded.
	 */
	protected synchronized boolean	isIncluded(IComponentIdentifier cid, String[] includes, String[] excludes)
	{
		boolean	included	= includes.length==0;
		String[]	cidnames	= null;
		
		// Check if contained in includes.
		for(int i=0; !included && i<includes.length; i++)
		{
			if(cidnames==null)
				cidnames	= extractNames(cid);
			for(int j=0; !included && j<cidnames.length; j++)
			{
				included	= cidnames[j].startsWith(includes[i]);
			}
		}
		
		// Check if not contained in excludes.
		for(int i=0; included && i<excludes.length; i++)
		{
			if(cidnames==null)
				cidnames	= extractNames(cid);
			for(int j=0; included && j<cidnames.length; j++)
			{
				included	= !cidnames[j].startsWith(excludes[i]);
			}
		}

		return included;
	}
	
	/**
	 *  Extract names for matching to includes/excludes list.
	 */
	protected String[]	extractNames(IComponentIdentifier cid)
	{
		List	ret	= new ArrayList();
		ret.add(cid.getName());
		String[]	addrs	= cid.getAddresses();
		for(int i=0; i<addrs.length; i++)
		{
			int	prot	= addrs[i].indexOf("://");
			int	port	= addrs[i].indexOf(':', prot+3);
			if(prot!=-1 && port!=-1)
			{
//				ret.add(addrs[i].substring(prot+3, port));
				ret.add(addrs[i].substring(0, port));
			}
			else
			{
				System.out.println("Warning: Unknown address scheme "+addrs[i]);
			}
		}
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
	protected void	doWaitFor(long delay, final IComponentStep step)
	{
//		waitFor(delay, step);
		
		if(timer==null)
			timer	= new Timer(true);
		
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				scheduleStep(step);
			}
		}, delay);
	}
	
	//-------- IPropertiesProvider interface --------
	
	/**
	 *  Update from given properties.
	 */
	public IFuture setProperties(final jadex.commons.Properties props)
	{
		return scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				try
				{
//					setAddressInfo(InetAddress.getByName(props.getStringProperty("address")), props.getIntProperty("port"));
					long delay = props.getLongProperty("delay");
					if(delay>0)
						setDelay(delay);
//					setFastAwareness(props.getProperty("fast")!=null ? props.getBooleanProperty("fast") : true);
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
					
					return null;
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
	public IFuture getProperties()
	{
		return scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				jadex.commons.Properties	props	= new jadex.commons.Properties();
//				props.addProperty(new Property("address", address.getHostAddress()));
//				props.addProperty(new Property("port", ""+port));
//				props.addProperty(new Property("delay", ""+delay));
//				props.addProperty(new Property("fast", ""+fast));
				props.addProperty(new Property("autocreate", ""+autocreate));
				props.addProperty(new Property("autodelete", ""+autodelete));
				for(int i=0; i<includes.size(); i++)
					props.addProperty(new Property("include", includes.get(i).toString()));
				for(int i=0; i<excludes.size(); i++)
					props.addProperty(new Property("exclude", excludes.get(i).toString()));
				return props;
			}
		});		
	}
}

