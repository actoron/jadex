package jadex.base.service.awareness;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.ICommand;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.MicroAgent;
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
	protected DatagramSocket socket;
	
	/** The delay. */
	protected long delay;
	
	/** The created proxies via component identifiers. */
	protected Set proxies;
	
	/** Flag indicating agent killed. */
	protected boolean killed;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		try
		{
			this.address = getArgument("address")==null? 
				InetAddress.getByName("224.0.0.0"): InetAddress.getByName((String)getArgument("address"));
			this.port = getArgument("port")==null? 
				55667: ((Integer)getArgument("port")).intValue();
			this.delay = getArgument("delay")==null? 
				10000: ((Integer)getArgument("delay")).intValue();
			
			this.socket =  new DatagramSocket();
			this.proxies = new HashSet();
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
		killed = true;
	}
	
	/**
	 *  Start sending of message
	 */
	public void send(final InetAddress receiver, final int port, final AwarenessInfo info)
	{
		SServiceProvider.getService(getServiceProvider(), ILibraryService.class)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				try
				{
					ILibraryService ls = (ILibraryService)result;
					byte[] data = JavaWriter.objectToByteArray(info, ls.getClassLoader());
					DatagramPacket packet = new DatagramPacket(data, data.length, receiver, port);
					socket.send(packet);
//					System.out.println(getComponentIdentifier()+" sent '"+info+"' to "+receiver+":"+port);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				exception.printStackTrace();
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
										// todo: max ip datagram length (is there a better way to determine length?)
										byte buf[] = new byte[65535];
										
										while(!killed)
										{
											try
											{
												MulticastSocket s = new MulticastSocket(port);
												s.joinGroup(address);
												
												DatagramPacket pack = new DatagramPacket(buf, buf.length);
												s.receive(pack);
												
												byte[] target = new byte[pack.getLength()];
												System.arraycopy(buf, 0, target, 0, pack.getLength());
												
												AwarenessInfo info = (AwarenessInfo)JavaReader.objectFromByteArray(target, ls.getClassLoader());
		//												System.out.println(getComponentIdentifier()+" received: "+info);
											
												final IComponentIdentifier sender = info.getSender();
												if(!proxies.contains(sender))
												{
		//													System.out.println("Creating new proxy for: "+sender);
													
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
														}
													})).addResultListener(createResultListener(new IResultListener()
													{
														
														public void resultAvailable(Object source, Object result)
														{
															proxies.add(sender);
														}
														
														public void exceptionOccurred(Object source, Exception exception)
														{
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
		//										e.printStackTrace();
												getLogger().warning("Awareness agent problem, could not handle awareness info: "+e);
											}
										}
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
	
	
}
