package jadex.base.service.awareness;

import jadex.bridge.Argument;
import jadex.bridge.CreationInfo;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.ICommand;
import jadex.commons.SUtil;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.SServiceProvider;
import jadex.service.library.ILibraryService;
import jadex.service.threadpool.IThreadPoolService;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.HashSet;
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
	
	/** The socket. */
	protected MulticastSocket socket;
	
	/** The delay. */
	protected long delay;
	
	/** The created proxies via component identifiers. */
	protected Set proxies;
	
	/** Flag indicating agent killed. */
	protected boolean killed;
	
	/** Receiving thread. */
	protected Thread receiver;
	
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
			
			this.socket =  new MulticastSocket();
//			System.out.println(socket.getLoopbackMode());
			this.socket.setLoopbackMode(true);
			this.proxies = new HashSet();
//			System.out.println(socket.getLoopbackMode());
			proxies.add(getRootIdentifier());
		
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
		final IComponentIdentifier root = getRootIdentifier();
		
		ICommand send = new ICommand()
		{
			public void execute(Object args)
			{
				send(address, port, new AwarenessInfo(root));
				waitFor(delay, this);
			}
		};
		send.execute(this);
	}
	
	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public void agentKilled()
	{
		System.out.println("killed set to true: "+getComponentIdentifier());
		killed = true;

		if(socket!=null)
			socket.close();
		synchronized(AwarenessAgent.this)
		{
			if(receiver!=null)
				receiver.interrupt();
		}
	}
	
	/**
	 *  Start sending of message
	 */
	public void send(final InetAddress receiver, final int port, final AwarenessInfo info)
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
					DatagramPacket packet = new DatagramPacket(data, data.length, receiver, port);
					socket.send(packet);
//						System.out.println(getComponentIdentifier()+" sent '"+info+"' to "+receiver+":"+port);
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
	 *  Start receiving on 
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
										try
										{
											s = new MulticastSocket(port);
											s.joinGroup(address);
										
											while(!killed)
											{
												try
												{
													DatagramPacket pack = new DatagramPacket(buf, buf.length);
													s.receive(pack);
													
													byte[] target = new byte[pack.getLength()];
													System.arraycopy(buf, 0, target, 0, pack.getLength());
													
													AwarenessInfo info = (AwarenessInfo)JavaReader.objectFromByteArray(target, ls.getClassLoader());
			//										System.out.println(getComponentIdentifier()+" received: "+info);
												
													final IComponentIdentifier sender = info.getSender();
													
													if(!proxies.contains(sender))
													{
//														System.out.println("Creating new proxy for: "+sender+" "+getComponentIdentifier());
														
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
										}
										catch(Exception e)
										{
											getLogger().warning("Receiving socket init error: "+e);
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
	
	/**
	 *  Get the root component identifier.
	 */
	public IComponentIdentifier getRootIdentifier()
	{
		IExternalAccess root = getParent();
		while(root.getParent()!=null)
			root = root.getParent();
		IComponentIdentifier ret = root.getComponentIdentifier();
//		System.out.println("root: "+root.getComponentIdentifier().hashCode()+SUtil.arrayToString(root.getComponentIdentifier().getAddresses()));
		return ret;
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
			}, null, null, null);
	}
}
