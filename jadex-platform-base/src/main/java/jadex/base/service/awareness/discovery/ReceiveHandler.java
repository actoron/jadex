package jadex.base.service.awareness.discovery;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.net.InetAddress;

/**
 *  Abstract receive handler base class.
 */
public abstract class ReceiveHandler
{
	//-------- attributes --------
	
	/** The agent. */
	protected DiscoveryAgent agent;

	/** The current send id. */
	protected String sendid;
	
	/** Flag indicating that the agent has received its own discovery info. */
	protected boolean received_self;
	
	//-------- constructors --------
	
	/**
	 *  Create a new lease time handling object.
	 */
	public ReceiveHandler(DiscoveryAgent agent)
	{
		this.agent = agent;
	}
	
	//-------- methods --------
	
	/**
	 *  Receive a packet.
	 */
	public abstract Object[] receive();
	
	/**
	 *  Start receiving awareness infos.
	 *  @return A future indicating when the receiver thread is ready.
	 */
	public IFuture<Void>	startReceiving()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Start the receiver thread.
		IFuture<IThreadPoolService>	tpfut	= agent.getMicroAgent().getServiceContainer().getRequiredService("threadpool");
		tpfut.addResultListener(new IResultListener<IThreadPoolService>()
		{
			public void resultAvailable(final IThreadPoolService tp)
			{
				tp.execute(new Runnable()
				{
					public void run()
					{						
						try
						{
							ret.setResult(null);
							
//							// Init receive socket
//							try
//							{
//								agent.initNetworkRessource();
//								ret.setResultIfUndone(null);
//							}
//							catch(Exception e)
//							{
//								ret.setExceptionIfUndone(e);
//							}
						
							while(!agent.isKilled())
							{
								try
								{
//									final DatagramPacket pack = new DatagramPacket(buf, buf.length);
									final Object[] packet = receive();
									if(packet!=null)
									{
										agent.getMicroAgent().scheduleStep(new IComponentStep<Void>()
										{
											public IFuture<Void> execute(IInternalAccess ia)
											{
												try
												{
													AwarenessInfo info = (AwarenessInfo)DiscoveryAgent.decodeObject((byte[])packet[2], agent.getAllCodecs(), agent.getMicroAgent().getClassLoader());
//													System.out.println("received info: "+info);
													handleReceivedPacket((InetAddress)packet[0], ((Integer)packet[1]).intValue(), (byte[])packet[2], info);
												}
												catch(Exception e)
												{
													ia.getLogger().warning("Could not decode discovery message: "+e);//+"\n"+new String(GZIPCodec.decodeBytes((byte[])packet[2], 
														//agent.getMicroAgent().getClassLoader())));
//													DiscoveryState.decodeObject((byte[])packet[2], agent.getMicroAgent().getClassLoader());
												}
												return IFuture.DONE;
											}
										});
									}
	//								System.out.println("received: "+getComponentIdentifier());
								}
								catch(Exception e)
								{
									// Can happen if is slave and master goes down.
									// In that case it tries to find new master.
									
									// Can also happen when getSocket() does not work
									// In this case stop calling receive for some time.
									if(e instanceof ConnectionException)
									{
										// todo: make customizable
										Thread.sleep(60000);
									}
									
	//								getLogger().warning("Receiving awareness info error: "+e);
//									ret.setExceptionIfUndone(e);
								}
							}
						}
						catch(Exception e) 
						{
							ret.setExceptionIfUndone(e);
						}
//						System.out.println("comp and receiver terminated: "+getComponentIdentifier());
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				if(!(exception instanceof ComponentTerminatedException))
					agent.getMicroAgent().getLogger().warning("Awareness agent problem, could not get threadpool service: "+exception);
//				exception.printStackTrace();
				ret.setExceptionIfUndone(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Handle a received packet.
	 */
	public void handleReceivedPacket(InetAddress address, int port, byte[] data, AwarenessInfo info)
	{
//		InetAddress address = packet.getAddress();
//		int port = packet.getPort();
//		InetSocketAddress sa = new InetSocketAddress(address, port);
//		System.out.println("received: "+obj+" "+address);
			
		if(info!=null && info.getSender()!=null)
		{
			if(!info.getSender().equals(agent.getRoot()))
			{
				announceAwareness(info);
			}
			else
			{
				received_self	= true;
//				return;
			}
//			System.out.println(System.currentTimeMillis()+" "+getComponentIdentifier()+" received: "+info.getSender());
		}	
//		System.out.println("received awa info: "+getComponentIdentifier().getLocalName()+" "+info.getSender());
	}
		
	/**
	 *  Announce newly arrived awareness info to management service.
	 */
	public void announceAwareness(final AwarenessInfo info)
	{
//		System.out.println("announcing: "+info);
		
		if(info.getSender()!=null)
		{
			if(info.getSender().equals(agent.getRoot()))
				received_self	= true;
			
//			System.out.println(System.currentTimeMillis()+" "+getComponentIdentifier()+" received: "+info.getSender());
			
			IFuture<IAwarenessManagementService>	msfut	= agent.getMicroAgent().getRequiredService("management");
			msfut.addResultListener(new DefaultResultListener<IAwarenessManagementService>(agent.getMicroAgent().getLogger())
			{
				public void resultAvailable(IAwarenessManagementService ms)
				{
					ms.addAwarenessInfo(info).addResultListener(new DefaultResultListener<Boolean>(agent.getMicroAgent().getLogger())
					{
						public void resultAvailable(Boolean result)
						{
							boolean initial = result.booleanValue();
							if(initial && agent.isFast() && agent.isStarted() && !agent.isKilled())
							{
		//						System.out.println(System.currentTimeMillis()+" fast discovery: "+getComponentIdentifier()+", "+sender);
								received_self = false;
								agent.doWaitFor((long)(Math.random()*500), new IComponentStep<Void>()
								{
									int	cnt;
									public IFuture<Void> execute(IInternalAccess ia)
									{
										final Future<Void> ret = new Future<Void>();
										final IComponentStep<Void> step = this;
										if(!received_self)
										{
											cnt++;
		//									System.out.println("CSMACD try #"+(++cnt));
											agent.createAwarenessInfo().addResultListener(agent.getMicroAgent()
												.createResultListener(new ExceptionDelegationResultListener<AwarenessInfo, Void>(ret)
											{
												public void customResultAvailable(AwarenessInfo info)
												{
													agent.sender.send(info);
													agent.doWaitFor((long)(Math.random()*500*cnt), step);
												}
											}));
										}
										return ret;
									}
								});
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							if(!(exception instanceof ComponentTerminatedException))
								super.exceptionOccurred(exception);
						}
					});
				}
				
				public void exceptionOccurred(Exception exception)
				{
					if(!(exception instanceof ComponentTerminatedException))
						super.exceptionOccurred(exception);
				}
			});
		}
	}

}
