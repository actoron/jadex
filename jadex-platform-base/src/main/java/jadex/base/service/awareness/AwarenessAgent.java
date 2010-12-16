package jadex.base.service.awareness;

import jadex.bridge.Argument;
import jadex.bridge.CreationInfo;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.clock.IClockService;
import jadex.commons.service.threadpool.IThreadPoolService;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  Agent that sends multicasts to locate other Jadex awareness agents.
 */
public class AwarenessAgent extends MicroAgent
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

	/** The proxy delay. */
	protected long proxydelay;

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
	
	/** The clock service. */
	protected IClockService clock;
	
	/** The root component id. */
	protected IComponentIdentifier root;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		try
		{
			this.address = InetAddress.getByName((String)getArgument("address"));
			if(address==null)
				throw new NullPointerException("Cannot get address: "+getArgument("address"));
			this.port = ((Number)getArgument("port")).intValue();
			this.delay = ((Number)getArgument("delay")).longValue();
			this.autocreate = ((Boolean)getArgument("autocreate")).booleanValue();
			this.autodelete = ((Boolean)getArgument("autodelete")).booleanValue();
			this.proxydelay = ((Number)getArgument("proxydelay")).longValue();
//			System.out.println("initial delay: "+delay);
			
			this.sendsocket = new MulticastSocket();
//			System.out.println(socket.getLoopbackMode());
			this.sendsocket.setLoopbackMode(true);
			
			this.discovered = new LinkedHashMap();
			
//			System.out.println(socket.getLoopbackMode());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
		SServiceProvider.getService(getServiceProvider(), IClockService.class).addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				clock = (IClockService)result;
				
				getRootIdentifier().addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						root = (IComponentIdentifier)result;
						discovered.put(root, new DiscoveryInfo(root, false, false, clock.getTime(), delay));
						
						startSendBehaviour();
						startRemoveBehaviour();
						startReceiving();
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						throw new RuntimeException(exception);
					}
				}));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				throw new RuntimeException(exception);
			}
		}));
	}
	
	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public void agentKilled()
	{
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
		
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				getRootIdentifier(getComponentIdentifier(), (IComponentManagementService)result, ret);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
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
			public void resultAvailable(Object source, Object result)
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
			
			public void exceptionOccurred(Object source, Exception exception)
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
	 *  Get the proxy delay.
	 *  @return the delay.
	 */
	public synchronized long getProxyDelay()
	{
		return proxydelay;
	}

	/**
	 *  Set the proxy delay.
	 *  @param delay The delay to set.
	 */
	public synchronized void setProxyDelay(long proxydelay)
	{
		this.proxydelay = proxydelay;
	}
	
	/**
	 *  Set the delay.
	 *  @param delay The delay to set.
	 */
	public synchronized void setExcluded(IComponentIdentifier cid, boolean excluded)
	{
		DiscoveryInfo dif = getDiscoveryInfo(cid);
		if(dif!=null)
			dif.setExcluded(excluded);
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
			public Object execute(IInternalAccess ia)
			{
				List todel = autodelete? new ArrayList(): null;
				synchronized(AwarenessAgent.this)
				{
					long time = clock.getTime();
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
								todel.add(dif.getComponentIdentifier());
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
						IComponentIdentifier cid = (IComponentIdentifier)todel.get(i);
						// Ignore deletion failures
						deleteProxy(cid).addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
							}
							public void exceptionOccurred(Object source, Exception exception)
							{
							}
						});
					}
				}
				
				waitFor(5000, this);
				return null;
			}
		});
	}
	
	/**
	 *  Check if local proxy is still available.
	 */
	public void checkProxy(final DiscoveryInfo dif)
	{
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class)
		.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				
				IComponentIdentifier lcid = cms.createComponentIdentifier(dif.getComponentIdentifier().getLocalName(), true);
				cms.getComponentDescription(lcid).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						dif.setProxy(false);
					}
				});
			}
			public void exceptionOccurred(Object source, Exception exception) 
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
			public Object execute(IInternalAccess ia)
			{
				if(sendid.equals(getSendId()))
				{
					send(new AwarenessInfo(root, clock.getTime(), delay));
					
					if(delay>0)
						waitFor(delay, this);
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
		args.put("delay", new Long(proxydelay));
		
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
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
						createResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							// todo: do not use source for cid?!
			//				System.out.println("Proxy killed: "+source);
							DiscoveryInfo dif = getDiscoveryInfo(cid);
							if(dif!=null)
								dif.setProxy(false);
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
//							getLogger().warning("Proxy was killed: "+exception);
						}
					})).addResultListener(createResultListener(new IResultListener()
					{
						
						public void resultAvailable(Object source, Object result)
						{
							DiscoveryInfo dif = getDiscoveryInfo(cid);
							dif.setProxy(true);
							ret.setResult(result);
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
	//						getLogger().warning("Exception during proxy creation: "+exception);
							ret.setException(exception);
						}
					}));
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Delete a proxy.
	 */
	public IFuture deleteProxy(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				
				IComponentIdentifier pcid = cms.createComponentIdentifier(cid.getLocalName(), true);
//				System.out.println("dela: "+pcid);
				
				cms.destroyComponent(pcid).addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						DiscoveryInfo dif = getDiscoveryInfo(cid);
						if(dif!=null)
							dif.setProxy(false);
						ret.setResult(result);
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
//						getLogger().warning("Exception during proxy creation: "+exception);
						ret.setException(exception);
					}
				}));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
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
		SServiceProvider.getService(getServiceProvider(), IThreadPoolService.class)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
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
								
								AwarenessInfo info = (AwarenessInfo)JavaReader.objectFromByteArray(target, getModel().getClassLoader());
//								System.out.println(getComponentIdentifier()+" received: "+info);
							
								final IComponentIdentifier sender = info.getSender();
								boolean createproxy;
								DiscoveryInfo dif;
								
								synchronized(AwarenessAgent.this)
								{
									dif = (DiscoveryInfo)discovered.get(sender);
									if(dif==null)
									{
										createproxy = isAutoCreateProxy();
										dif = new DiscoveryInfo(sender, createproxy, false, clock.getTime(), getDelay());
										discovered.put(sender, dif);
									}
									else
									{
										createproxy = isAutoCreateProxy() && !dif.isProxy() && !dif.isExcluded();
										dif.setTime(clock.getTime());
									}
								}
								
								if(createproxy)
								{
//									System.out.println("Creating new proxy for: "+sender+" "+getComponentIdentifier());
									createProxy(sender);
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
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				getLogger().warning("Awareness agent problem, could not get threadpool service: "+exception);
//				exception.printStackTrace();
			}
		}));
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		String[] configs = new String[]{"Frequent updates (5s)", "Normal updates (10s)", "Seldom updates (60s)"};
		return new MicroAgentMetaInfo("This agent looks for other awareness agents in the local net.", configs, 
			new IArgument[]
			{	
				new Argument("address", "This parameter is the ip multicast address used for finding other agents (range 224.0.0.0-239.255.255.255).", "String", "224.0.0.0"),	
				new Argument("port", "This parameter is the port used for finding other agents.", "int", new Integer(55667)),	
				new Argument("delay", "This parameter is the delay between sending awareness infos.", "long", 
					SUtil.createHashMap(configs, new Object[]{new Long(10000), new Long(20000), new Long(60000)})),	
				new Argument("autocreate", "This parameter describes if new proxies should be automatically created when discovering new components.", "boolean", Boolean.TRUE),	
				new Argument("autodelete", "This parameter describes if proxies should be automatically deleted when not discovered any longer.", "boolean", Boolean.TRUE),	
				new Argument("proxydelay", "This parameter is the delay used by proxies.", "long", new Long(15000)),	
			}, null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.base.service.awareness.AwarenessAgentPanel"}));
	}
}
