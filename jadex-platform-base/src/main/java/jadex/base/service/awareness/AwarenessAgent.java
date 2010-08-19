package jadex.base.service.awareness;

import jadex.bridge.Argument;
import jadex.bridge.CreationInfo;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.SServiceProvider;
import jadex.service.clock.IClockService;
import jadex.service.library.ILibraryService;
import jadex.service.threadpool.IThreadPoolService;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
	
	
	/** Receiving thread. */
	protected Thread receiver;
	
	/** Flag indicating if proxies should be automatically created. */
	protected boolean autocreate;
	
	/** The created proxies via component identifiers. */
	protected Set proxies;
	
	/** The discovered components. */
	protected Set discovered;
	
	
	/** The socket to send. */
	protected MulticastSocket sendsocket;
	
	/** The send delay. */
	protected long delay;
	
	/** The current send id. */
	protected String sendid;
	
	
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
			this.port = ((Number)getArgument("port")).intValue();
			this.delay = ((Number)getArgument("delay")).longValue();
			this.autocreate = ((Boolean)getArgument("autocreate")).booleanValue();
//			System.out.println("initial delay: "+delay);
			
			this.sendsocket = new MulticastSocket();
//			System.out.println(socket.getLoopbackMode());
			this.sendsocket.setLoopbackMode(true);
			
			this.proxies = new HashSet();
			this.discovered = new LinkedHashSet();
			
//			System.out.println(socket.getLoopbackMode());
			
			
			startReceiving();
		}
		catch(Exception e)
		{
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
						proxies.add(root);
						
						startSendBehaviour();
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
		killed = true;

		if(sendsocket!=null)
			sendsocket.close();
		synchronized(AwarenessAgent.this)
		{
			if(receiver!=null)
				receiver.interrupt();
		}
	}
	
	/**
	 *  Start sending of message
	 */
	public void send(final AwarenessInfo info)
	{
		SServiceProvider.getService(getServiceProvider(), ILibraryService.class)
			.addResultListener(createResultListener(new DefaultResultListener() 
		{	
			public void resultAvailable(Object source, Object result) 
			{
				try
				{
					ILibraryService ls = (ILibraryService)result;
					byte[] data = JavaWriter.objectToByteArray(info, ls.getClassLoader());
					DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
					sendsocket.send(packet);
					System.out.println(getComponentIdentifier()+" sent '"+info+"' to "+receiver+":"+port);
				}
				catch(Exception e)
				{
					getLogger().warning("Could not send awareness message: "+e);
//						e.printStackTrace();
				}	
			}
		}));
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
	 *  Get the discovered proxy data.
	 *  @return The proxy data.
	 */
	public synchronized Object[] getDiscoveredProxies()
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
	protected void startremoveBehaviour()
	{
		scheduleStep(new ICommand()
		{
			public void execute(Object args)
			{
				send(new AwarenessInfo(root, clock.getTime(), delay));
				
				waitFor(delay, this);
			}
		});
	}
	
	/**
	 *  Start sending awareness infos.
	 *  (Ends automatically when a new send behaviour is started).
	 */
	protected void startSendBehaviour()
	{
		final String sendid = SUtil.createUniqueId(getAgentName());
		this.sendid = sendid;	
		
		scheduleStep(new ICommand()
		{
			public void execute(Object args)
			{
				if(sendid.equals(getSendId()))
				{
					send(new AwarenessInfo(root, clock.getTime(), delay));
					
					if(delay>0)
						waitFor(delay, this);
				}
			}
		});
	}
	
	/**
	 *  Start receiving awareness infos.
	 */
	public void startReceiving()
	{
		// Start the receiver thread.
		SServiceProvider.getService(getServiceProvider(), ILibraryService.class)
		.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class)
					.addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final IComponentManagementService cms = (IComponentManagementService)result;
						
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
										synchronized(AwarenessAgent.this)
										{
											receiver = Thread.currentThread();
										}
										
										// todo: max ip datagram length (is there a better way to determine length?)
										byte buf[] = new byte[65535];
										
										MulticastSocket s = null;
										InetAddress myaddress = null;
										
										while(!killed)
										{
											try
											{
												// reopen when
												Object[] ai = getAddressInfo();
												InetAddress curaddress = (InetAddress)ai[0];
												int curport = ((Integer)ai[1]).intValue();
												
												if(s!=null && (s.getPort()!=curport || !SUtil.equals(curaddress, myaddress)))
												{
													s.leaveGroup(myaddress);
													s.close();
													s = null;
												}
												if(s==null)
												{
													s = new MulticastSocket(curport);
													s.joinGroup(curaddress);
													myaddress = curaddress;
												}
												
												DatagramPacket pack = new DatagramPacket(buf, buf.length);
												s.receive(pack);
												
												byte[] target = new byte[pack.getLength()];
												System.arraycopy(buf, 0, target, 0, pack.getLength());
												
												AwarenessInfo info = (AwarenessInfo)JavaReader.objectFromByteArray(target, ls.getClassLoader());
		//										System.out.println(getComponentIdentifier()+" received: "+info);
											
												final IComponentIdentifier sender = info.getSender();
												discovered.add(sender);
												
												if(isAutoCreateProxy() && !proxies.contains(sender))
												{
//													System.out.println("Creating new proxy for: "+sender+" "+getComponentIdentifier());
													
													Map args = new HashMap();
													args.put("platform", sender);
													CreationInfo ci = new CreationInfo(args);
													cms.createComponent(sender.getLocalName(), "jadex/base/service/remote/ProxyAgent.class", ci, 
														createResultListener(new IResultListener()
													{
														public void resultAvailable(Object source, Object result)
														{
															// todo: do not use source for cid?!
		//															System.out.println("Proxy killed: "+source);
															proxies.remove(sender);
														}
														
														public void exceptionOccurred(Object source, Exception exception)
														{
															getLogger().warning("Could not create proxy: "+exception);
														}
													})).addResultListener(createResultListener(new IResultListener()
													{
														
														public void resultAvailable(Object source, Object result)
														{
															proxies.add(sender);
														}
														
														public void exceptionOccurred(Object source, Exception exception)
														{
															getLogger().warning("Exception during proxy execution: "+exception);
														}
													}));
												}
		//										else
		//										{
		//											System.out.println("No proxy for: "+sender);
		//										}
											}
											catch(Exception e)
											{
												getLogger().warning("Receiving awareness info error: "+e);
											}
										}
										
										if(s!=null)
										{
											try
											{
												s.leaveGroup(address);
												s.close();
											}
											catch(Exception e)
											{
												getLogger().warning("Receiving socket closing error: "+e);
											}
										}
										
										synchronized(AwarenessAgent.this)
										{
											receiver = null;
										}
										System.out.println("comp and receiver terminated: "+getComponentIdentifier());
									}
								});
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								getLogger().warning("Awareness agent problem, could not get threadpool service: "+exception);
		//						exception.printStackTrace();
							}
						}));
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						getLogger().warning("Awareness agent problem, could not get component management service: "+exception);
		//				exception.printStackTrace();
					}
				}));
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				getLogger().warning("Awareness agent problem, could not get library service: "+exception);
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
					SUtil.createHashMap(configs, new Object[]{new Long(5000), new Long(10000), new Long(60000)})),	
				new Argument("autocreate", "This parameter describes if new proxies should be automatically created when discovering new components.", "boolean", Boolean.TRUE),	
			}, null, null, SUtil.createHashMap(new String[]{"serviceviewer.viewerclass"}, new Object[]{"jadex.tools.serviceviewer.awareness.AwarenessAgentPanel"}));
	}
}
