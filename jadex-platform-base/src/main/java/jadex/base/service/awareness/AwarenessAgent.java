package jadex.base.service.awareness;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ISettingsService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.threadpool.IThreadPoolService;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.xml.annotation.XMLClassname;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;
import jadex.xml.reader.Reader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
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

import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;


/**
 *  Agent that sends multicasts to locate other Jadex awareness agents.
 */
@Description("This agent looks for other awareness agents in the local net.")
@Arguments(
{
	@Argument(name="address", typename="String", defaultvalue="\"224.0.0.0\"", description="The ip multicast address used for finding other agents (range 224.0.0.0-239.255.255.255)."),
	@Argument(name="port", typename="int", defaultvalue="55667", description="The port used for finding other agents."),
	@Argument(name="delay", typename="long", defaultvalue="10000", description="The delay between sending awareness infos (in milliseconds)."),
	@Argument(name="autocreate", typename="boolean", defaultvalue="true", description="Set if new proxies should be automatically created when discovering new components."),
	@Argument(name="autodelete", typename="boolean", defaultvalue="true", description="Set if proxies should be automatically deleted when not discovered any longer."),
	@Argument(name="proxydelay", typename="long", defaultvalue="15000", description="The delay used by proxies."),
	@Argument(name="includes", typename="String", defaultvalue="\"\"", description="A list of platforms/IPs/hostnames to include (comma separated). Matches start of platform/IP/hostname."),
	@Argument(name="excludes", typename="String", defaultvalue="\"\"", description="A list of platforms/IPs/hostnames to exclude (comma separated). Matches start of platform/IP/hostname.")
})
@Configurations(
{
	@Configuration(name="Frequent updates (10s)", arguments=@NameValue(name="delay", value="10000")),
	@Configuration(name="Medium updates (20s)", arguments=@NameValue(name="delay", value="20000")),
	@Configuration(name="Seldom updates (60s)", arguments=@NameValue(name="delay", value="60000"))
})
@Properties(@NameValue(name="componentviewer.viewerclass", value="jadex.base.service.awareness.AwarenessAgentPanel"))
@RequiredServices(
{
	@RequiredService(name="cms", type=IComponentManagementService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM),
	@RequiredService(name="threadpool", type=IThreadPoolService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM),
//	@RequiredService(name="clock", type=IClockService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM),
	@RequiredService(name="settings", type=ISettingsService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM)
})
public class AwarenessAgent extends MicroAgent	implements IPropertiesProvider
{
	//-------- attributes --------
	
	/** The multicast internet address. */
	protected InetAddress address;
	
	/** The receiver port. */
	protected int port;
	
	
	/** Flag indicating if proxies should be automatically created. */
	protected boolean autocreate;
	
	/** Flag indicating if proxies should be automatically deleted. */
	protected boolean autodelete;

	/** The discovered components. */
	protected Map discovered;
	
	
	/** The socket to send. */
	protected MulticastSocket sendsocket;
	
	/** The send delay. */
	protected long delay;
		
	/** The current send id. */
	protected String sendid;
	
	
	/** The socket to send. */
	protected MulticastSocket receivesocket;
	
	/** Flag indicating agent killed. */
	protected boolean killed;
	
//	/** The clock service. */
//	protected IClockService clock;
	
	/** The timer. */
	protected Timer	timer;
	
	/** The root component id. */
	protected IComponentIdentifier root;
	
	/** The includes list. */
	protected List	includes;
	
	/** The excludes list. */
	protected List	excludes;
	
	/** The java reader for parsing received awareness infos. */
	protected JavaReader	reader;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture	agentCreated()
	{
		initArguments();
		
		try
		{
			this.sendsocket = new MulticastSocket();
			this.sendsocket.setLoopbackMode(true);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		this.discovered = new LinkedHashMap();
		this.reader	= new JavaReader(new XMLReporter()
		{
			public void report(String message, String type, Object related, Location location) throws XMLStreamException
			{
				// Ignore XML exceptions.
//				getLogger().warning(message);
			}
		});
		
		final Future	ret	= new Future();
		getRequiredService("settings").addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				ISettingsService	settings	= (ISettingsService)result;
				settings.registerPropertiesProvider(getAgentName(), AwarenessAgent.this)
					.addResultListener(new DelegationResultListener(ret));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Keep print stack trace until getRequiredService() of ComponentServiceContainer is fixed.
				exception.printStackTrace();
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
		try
		{
			this.address = InetAddress.getByName((String)getArgument("address"));			
		}
		catch(UnknownHostException e)
		{
			throw new RuntimeException(e);
		}
		if(address==null)
			throw new NullPointerException("Cannot get address: "+getArgument("address"));
		this.port = ((Number)getArgument("port")).intValue();
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
		getRootIdentifier().addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				root = (IComponentIdentifier)result;
				discovered.put(root, new DiscoveryInfo(root, null, getClockTime(), delay, false));
				
				startSendBehaviour();
				startRemoveBehaviour();
				startReceiving();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				throw new RuntimeException(exception);
			}
		}));
	}
	
	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture	agentKilled()
	{
		if(sendsocket!=null)
		{
			send(new AwarenessInfo(root, AwarenessInfo.STATE_OFFLINE, delay));
		}
		
//		System.out.println("killed set to true: "+getComponentIdentifier());
		synchronized(AwarenessAgent.this)
		{
			killed = true;
	
			if(sendsocket!=null)
			{
				sendsocket.close();
			}
			if(receivesocket!=null)
			{
				try
				{
					receivesocket.leaveGroup(address);
				}
				catch(Exception e)
				{
				}
				finally
				{
					receivesocket.close();
				}
			}
		}
		
		final Future	ret	= new Future();
		getRequiredService("settings").addResultListener(createResultListener(new IResultListener()
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
	 *  Start sending of message
	 */
	public void send(final AwarenessInfo info)
	{
		try
		{
			byte[] data = JavaWriter.objectToByteArray(info, getModel().getClassLoader());
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			sendsocket.send(packet);
//			System.out.println(getComponentIdentifier()+" sent '"+info+"' to "+receiver+":"+port);
		}
		catch(Exception e)
		{
			getLogger().warning("Could not send awareness message: "+e);
//			e.printStackTrace();
		}	
	}
	
	/**
	 *  Get the root component identifier.
	 */
	public IFuture getRootIdentifier()
	{
		final Future ret = new Future();
		
		getRequiredService("cms").addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				getRootIdentifier(getComponentIdentifier(), (IComponentManagementService)result, ret);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		}));
		
		return ret;
		
//		IExternalAccess root = getParent();
//		while(root.getParent()!=null)
//			root = root.getParent();
//		IComponentIdentifier ret = root.getComponentIdentifier();
////		System.out.println("root: "+root.getComponentIdentifier().hashCode()+SUtil.arrayToString(root.getComponentIdentifier().getAddresses()));
//		return ret;
	}
	
	/**
	 *  Internal method to get the root identifier.
	 */
	public void getRootIdentifier(final IComponentIdentifier cid, final IComponentManagementService cms, final Future future)
	{
		cms.getParent(cid).addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if(result==null)
				{
					future.setResult(cid);
				}
				else
				{
					getRootIdentifier((IComponentIdentifier)result, cms, future);
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				future.setException(exception);
			}
		}));
	}
	
	/**
	 *  Get the address.
	 *  @return the address.
	 */
	public synchronized Object[] getAddressInfo()
	{
		return new Object[]{address, new Integer(port)};
	}

	/**
	 *  Set the address.
	 *  @param address The address to set.
	 */
	public synchronized void setAddressInfo(InetAddress address, int port)
	{
//		System.out.println("setAddress: "+address+" "+port);
		this.address = address;
		this.port = port;
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
			startSendBehaviour();
		}
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
	public synchronized void setIncludes(String[] includes)
	{
		this.includes	= new ArrayList(Arrays.asList(includes));
	}

	/**
	 *  Set the excludes.
	 *  @param excludes The excludes to set.
	 */
	public synchronized void setExcludes(String[] excludes)
	{
		this.excludes	= new ArrayList(Arrays.asList(excludes));
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
	 *  Get the sendid.
	 *  @return the sendid.
	 */
	public String getSendId()
	{
		return sendid;
	}

	/**
	 *  Set the sendid.
	 *  @param sendid The sendid to set.
	 */
	public void setSendId(String sendid)
	{
		this.sendid = sendid;
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
				synchronized(AwarenessAgent.this)
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
		getRequiredService("cms").addResultListener(createResultListener(new IResultListener()
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
	 *  Start sending awareness infos.
	 *  (Ends automatically when a new send behaviour is started).
	 */
	protected void startSendBehaviour()
	{
		final String sendid = SUtil.createUniqueId(getAgentName());
		this.sendid = sendid;	
		
		scheduleStep(new IComponentStep()
		{
			@XMLClassname("send")
			public Object execute(IInternalAccess ia)
			{
				if(sendid.equals(getSendId()))
				{
					send(new AwarenessInfo(root, AwarenessInfo.STATE_ONLINE, delay, includes, excludes));
					
					if(delay>0)
						doWaitFor(delay, this);
				}
				return null;
			}
		});
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
		
		getRequiredService("cms").addResultListener(createResultListener(new DelegationResultListener(ret)
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
		
		getRequiredService("cms").addResultListener(createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				
				IComponentIdentifier cid = dif.getProxy();
				if(cid!=null)
				{
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
	 *  Start receiving awareness infos.
	 */
	public void startReceiving()
	{
		// Start the receiver thread.
		getRequiredService("threadpool").addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IThreadPoolService tp = (IThreadPoolService)result;
				
				tp.execute(new Runnable()
				{
					public void run()
					{
						// todo: max ip datagram length (is there a better way to determine length?)
						byte buf[] = new byte[65535];
						
						InetAddress myaddress = null;
						
						while(!killed)
						{
							try
							{
								// reopen when
								Object[] ai = getAddressInfo();
								InetAddress curaddress = (InetAddress)ai[0];
								int curport = ((Integer)ai[1]).intValue();
								
								synchronized(AwarenessAgent.this)
								{
									if(!killed)
									{
										if(receivesocket!=null && (receivesocket.getPort()!=curport || !SUtil.equals(curaddress, myaddress)))
										{
											receivesocket.leaveGroup(myaddress);
											receivesocket.close();
											receivesocket = null;
										}
										if(receivesocket==null)
										{
											try
											{
												receivesocket = new MulticastSocket(curport);
												receivesocket.joinGroup(curaddress);
												myaddress = curaddress;
											}
											catch(Exception e)
											{
												receivesocket	= null;
												getLogger().warning("Awareness error when joining mutlicast group: "+e);
												break;
											}
										}
									}
								}
								
								DatagramPacket pack = new DatagramPacket(buf, buf.length);
								receivesocket.receive(pack);
								
								byte[] target = new byte[pack.getLength()];
								System.arraycopy(buf, 0, target, 0, pack.getLength());
								
								AwarenessInfo info = (AwarenessInfo)Reader.objectFromByteArray(reader, target, getModel().getClassLoader());
								if(info.getSender()!=null)
								{
									// Fix broken awareness infos for backwards compatibility.
									if(info.getDelay()==0)
										info.setDelay(delay);
									if(info.getState()==null)
										info.setState(AwarenessInfo.STATE_ONLINE);
//									System.out.println(getComponentIdentifier()+" received: "+info);
							
									IComponentIdentifier sender = info.getSender();
									boolean	online	= AwarenessInfo.STATE_ONLINE.equals(info.getState());
									boolean deleteproxy	= false;
									boolean createproxy	= false;
									DiscoveryInfo dif;
									
									synchronized(AwarenessAgent.this)
									{
										dif = (DiscoveryInfo)discovered.get(sender);
										if(online)
										{
											boolean	remoteexcluded	= !isIncluded(root, info.getIncludes(), info.getExcludes());
											if(dif==null)
											{
												dif = new DiscoveryInfo(sender, null, getClockTime(), getDelay(), remoteexcluded);
												discovered.put(sender, dif);
											}
											
											createproxy = isIncluded(sender, getIncludes(), getExcludes())
												&& !remoteexcluded && isAutoCreateProxy() && dif.getProxy()==null;
											dif.setTime(getClockTime());
										}
										else
										{
											deleteproxy	= dif!=null && dif.getProxy()!=null;
										}
									}
										
									if(createproxy)
									{
//										System.out.println("Creating new proxy for: "+sender+" "+getComponentIdentifier());
										createProxy(sender);
									}
									else if(deleteproxy)
									{
										deleteProxy(dif);
										
									}
								}
							}
							catch(Exception e)
							{
//								getLogger().warning("Receiving awareness info error: "+e);
							}
						}
						
						synchronized(AwarenessAgent.this)
						{
							if(receivesocket!=null)
							{
								try
								{
									receivesocket.leaveGroup(address);
									receivesocket.close();
								}
								catch(Exception e)
								{
//									getLogger().warning("Receiving socket closing error: "+e);
								}
							}
						}
//						System.out.println("comp and receiver terminated: "+getComponentIdentifier());
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				getLogger().warning("Awareness agent problem, could not get threadpool service: "+exception);
//				exception.printStackTrace();
			}
		}));
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
					setAddressInfo(InetAddress.getByName(props.getStringProperty("address")), props.getIntProperty("port"));
					setDelay(props.getLongProperty("delay"));
					setAutoCreateProxy(props.getBooleanProperty("autocreate"));
					setAutoDeleteProxy(props.getBooleanProperty("autodelete"));
					
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
				props.addProperty(new Property("address", address.getHostAddress()));
				props.addProperty(new Property("port", ""+port));
				props.addProperty(new Property("delay", ""+delay));
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
