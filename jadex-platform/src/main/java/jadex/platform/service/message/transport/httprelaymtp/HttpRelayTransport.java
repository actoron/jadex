package jadex.platform.service.message.transport.httprelaymtp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.SecureTransmission;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.SynchronizedServiceRegistry;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.security.SSecurity;
import jadex.micro.annotation.Binding;
import jadex.platform.service.awareness.discovery.relay.IRelayAwarenessService;
import jadex.platform.service.message.ISendTask;
import jadex.platform.service.message.transport.ITransport;

/**
 *  A transport that allows communication and awareness
 *  using relay servers.
 */
public class HttpRelayTransport implements ITransport
{
	//-------- constants --------
	
	/** The alive time for assuming a connection is working. */
	protected static final long	ALIVETIME	= 30000;
	
	/** The maximum number of workers for an address. */
	protected static final int	MAX_WORKERS;
	
//	/** Lock for synchronizing access to the URL connection pool. */
//	protected static final Object	POOL_LOCK	= new Object();
	
	static
	{
		// Can only use as many workers as connections are allowed
		// (otherwise runs out of ports when sending many messages, e.g. from streams)
		int	maxworkers;
		try
		{
			String prop = System.getProperty("http.maxConnections");
			maxworkers	= prop!=null  ? Integer.parseInt(prop)
				: 5; // Default according to http://docs.oracle.com/javase/1.4.2/docs/guide/net/properties.html
		}
		catch(Exception e)
		{
			maxworkers	= 1; // Not efficient, but uses the least ports, if keep-alive not available.
		}
		MAX_WORKERS	= maxworkers;
//		System.out.println("Relay maxworkers: "+maxworkers);
		
		
		// HACK!!! Disable all certificate checking (only until we find a more fine-grained solution)
        try
        {
	        TrustManager[] trustAllCerts = new TrustManager[]
	        {
                new X509TrustManager()
                {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers()
                    {
                    	return null;
                    }
                    
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) 
                    { 
                    }
                    
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) 
                    { 
                    }
                }
	        };
	
	        // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init( null, trustAllCerts, SSecurity.getSecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
            {
                public boolean verify(String urlHostName, SSLSession session)
                {
                	return true;
                }
            });
        }
        catch(Exception e)
        {
            //We can not recover from this exception.
            e.printStackTrace();
        }
	}
	
	//-------- attributes --------
	
	/** The component. */
	protected IInternalAccess component;
	
	/** The thread pool. */
	protected IDaemonThreadPoolService	threadpool;
	
	/** The default relay server lookup addresses. */
	protected String	defaddresses;
	
	/** Flag if receiver connection should use https. */
	protected boolean	secure;
	
	/** Flag if only awareness messages should be sent through relay. */
	protected boolean	awaonly;
	
	/** The connection manager. */
	protected RelayConnectionManager	conman;
	
	/** The receiver process. */
	protected HttpReceiver	receiver;
	
	/** The known addresses (address -> last used date (0 for pinging, negative for dead connections)). */
	protected Map<String, Long>	addresses;
	
	/** The worker count (address -> count). */
	protected Map<String, Integer>	workers;
	
	/** The ready queue per address (tasks to reschedule after ping). */
	protected Map<String, Collection<ISendTask>>	readyqueue;
	
	/** The send queue per address (tasks to send on worker thread). */
	protected Map<String, List<Tuple2<ISendTask, Future<Void>>>>	sendqueue;
	
	//-------- constructors --------
	
	/**
	 *  Create a new relay transport.
	 */
	public HttpRelayTransport(IInternalAccess component, String defaddresses, boolean secure, boolean awaonly)
	{
		this.component	= component;
		this.secure	= secure;
		this.awaonly	= awaonly;
		this.addresses	= Collections.synchronizedMap(new HashMap<String, Long>());	// Todo: cleanup unused addresses!?
		this.workers	= new HashMap<String, Integer>();
		this.readyqueue	= new HashMap<String, Collection<ISendTask>>();
		this.sendqueue	= new HashMap<String, List<Tuple2<ISendTask, Future<Void>>>>();
		
		StringBuffer	defs	= new StringBuffer();
		StringTokenizer	stok	= new StringTokenizer(defaddresses, ",");
		while(stok.hasMoreTokens())
		{
			String	adr	= RelayConnectionManager.relayAddress(stok.nextToken().trim());
			if(defs.length()>0)
			{
				defs.append(", ");
			}
			defs.append(secure ? RelayConnectionManager.secureAddress(adr) : adr);
				
			boolean	found	= false;
			for(int i=0; !found && i<getServiceSchemas().length; i++)
			{
				found	= adr.startsWith(getServiceSchemas()[i]);
			}
			if(!found)
			{
				throw new RuntimeException("Address does not match supported service schemes: "+adr+", "+SUtil.arrayToString(getServiceSchemas()));
			}
		}
		
		this.defaddresses	= defs.toString();
	}
	
	//-------- accessors --------
	
	/**
	 *  Get the default addresses.
	 */
	public String	getDefaultAddresses()
	{
		return defaddresses;
	}
	
	/**
	 *  Get the thread pool.
	 */
	public IDaemonThreadPoolService	getThreadPool()
	{
		return threadpool;
	}
	
	/**
	 *  Get the connection manager.
	 */
	public RelayConnectionManager	getConnectionManager()
	{
		return conman;
	}
	
	/**
	 *  Flag if receiver connection should use https.
	 */
	public boolean	isSecure()
	{
		return secure;
	}
	
	//-------- methods --------
	
	/**
	 *  Start the transport.
	 */
	public IFuture<Void> start()
	{
		final Future<Void>	ret	= new Future<Void>();
		SServiceProvider.getService(component, IDaemonThreadPoolService.class, Binding.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IDaemonThreadPoolService, Void>(ret)
		{
			public void customResultAvailable(IDaemonThreadPoolService tps)
			{
				threadpool	= tps;
				conman	= new RelayConnectionManager();
				receiver	= new HttpReceiver(HttpRelayTransport.this, component.getExternalAccess());
				receiver.start();
				ret.setResult(null);
			}
		});
		return ret;
	}

	/**
	 *  Perform cleanup operations (if any).
	 */
	public IFuture<Void> shutdown()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Async shutdown as offline message or connection closing may hang :-(
		SServiceProvider.getLocalService(component, IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.execute(new Runnable()
		{
			public void run()
			{
				try
				{
					String adr	= receiver.address==null ? null
						: RelayConnectionManager.httpAddress(receiver.address)+"offline";
					
					receiver.stop();
					if(adr!=null)
					{
//						System.out.println("going offline: "+adr+", "+component.getComponentIdentifier().getRoot());
						conman.postMessage(adr, component.getComponentIdentifier().getRoot(), new byte[0][]);
//						System.out.println("offline: "+adr+", "+component.getComponentIdentifier().getRoot());
					}
					conman.dispose();
					ret.setResultIfUndone(null);
				}
				catch(Exception e)
				{
//					System.out.println("not offline: "+adr+", "+component.getComponentIdentifier().getRoot()+", "+e);
					ret.setExceptionIfUndone(e);
				}
			}
		});
		
		// Use short timeout as www2 relay sometimes hangs and thus otherwise would break multiplatforms test (hack???)
		SServiceProvider.getLocalService(component, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.createRealtimeTimer(500, new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				ret.setExceptionIfUndone(new TimeoutException());
			}
		});
		
		return ret;
	}
	
	/**
	 *  Called from receiver thread, when it connects to an address.
	 */
	protected void	connected(final String address, final boolean dead)
	{
//		if(dead)
//		{
//			Thread.dumpStack();
//		}
		
//		final String	httpadr	= RelayConnectionManager.httpAddress(address);
		
//		Long	oldtime	= addresses.get(httpadr);
		// Remove all old entries with start address (e.g. also awareness urls).
//		if(!dead)
//		{
//			String[]	aadrs	= addresses.keySet().toArray(new String[0]);
//			for(int i=0; i<aadrs.length; i++)
//			{
//				if(aadrs[i].startsWith(httpadr))
//				{
//					addresses.remove(aadrs[i]);
//				}
//			}
//		}
//		addresses.put(httpadr, new Long(dead ? -System.currentTimeMillis() : System.currentTimeMillis()));
//		
//		ISendTask[]	readytasks	= null;
//		synchronized(readyqueue)
//		{
//			Collection<ISendTask>	queue	= readyqueue.get(httpadr);
//			if(queue!=null)
//			{
//				readytasks	= queue.toArray(new ISendTask[queue.size()]);
//				readyqueue.remove(httpadr);
//			}
//		}
//		for(int i=0; readytasks!=null && i<readytasks.length; i++)
//		{
//			internalSendMessage(httpadr, readytasks[i]);
//		}
		
		// inform awa when olddead
//		boolean	olddead	= oldtime==null || oldtime.longValue()<=0;
//		System.out.println("Dead old: "+dead+", "+olddead);
//		if(dead != olddead)	// Todo: fix race condition!?
//		{
			// Inform awareness manager (if any).
			component.getExternalAccess().scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(final IInternalAccess ia)
				{
					SServiceProvider.getService(ia, IMessageService.class, Binding.SCOPE_PLATFORM)
						.addResultListener(new IResultListener<IMessageService>()
					{
						public void resultAvailable(IMessageService ms)
						{
							ms.refreshAddresses().addResultListener(new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
									IRelayAwarenessService ras	= SynchronizedServiceRegistry.getRegistry(ia)
										.searchService(new ClassInfo(IRelayAwarenessService.class), ia.getComponentIdentifier(), Binding.SCOPE_PLATFORM, true);
									if(ras!=null)
									{
										if(dead)
											ras.disconnected(address);
										else
											ras.connected(address);
									}
								}
								
								public void exceptionOccurred(Exception exception)
								{
									ia.getLogger().info("Relay transport problem refreshing addresses: "+exception);
								}
							});
						}
						
						public void exceptionOccurred(Exception exception)
						{
							// No message service -> ignore awa infos.
						}
					});
					return IFuture.DONE;
				}
			});
//		}
	}
	
	/**
	 *  Test if a transport is applicable for the target address.
	 *  @return True, if the transport is applicable for the address.
	 */
	public boolean	isApplicable(String address)
	{
		boolean	applicable	= false;
		for(int i=0; !applicable && i<getServiceSchemas().length; i++)
		{
			applicable	= address.startsWith(getServiceSchemas()[i])
				&& (!awaonly || address.endsWith("awareness"));
		}
		return applicable;
	}
	
	/**
	 *  Test if a transport satisfies the non-functional requirements.
	 *  @param nonfunc	The non-functional requirements (name, value).
	 *  @param address	The transport address.
	 *  @return True, if the transport satisfies the non-functional requirements.
	 */
	public boolean	isNonFunctionalSatisfied(Map<String, Object> nonfunc, String address)
	{
		Boolean sec = nonfunc!=null? (Boolean)nonfunc.get(SecureTransmission.SECURE_TRANSMISSION): null;
		return sec==null || !sec.booleanValue() || address.equals(RelayConnectionManager.secureAddress(address));
	}
	
	/**
	 *  Send a message to the given address.
	 *  This method is called multiple times for the same message, i.e. once for each applicable transport / address pair.
	 *  The transport should asynchronously try to connect to the target address
	 *  (or reuse an existing connection) and afterwards call-back the ready() method on the send task.
	 *  
	 *  The send manager calls the obtained send commands of the transports and makes sure that the message
	 *  gets sent only once (i.e. call send commands sequentially and stop, when a send command finished successfully).
	 *  
	 *  All transports may keep any established connections open for later messages.
	 *  
	 *  @param address The address to send to.
	 *  @param task A task representing the message to send.
	 */
	public void	sendMessage(String address, ISendTask task)
	{
		internalSendMessage(RelayConnectionManager.httpAddress(address), task);
		
//		((AbstractSendTask)task).getFuture().addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.err.print("Relay error:");
//				exception.printStackTrace();
//			}
//		});
	}
	
	/**
	 * 	Schedule message sending.
	 */
	public void	internalSendMessage(final String address, final ISendTask task)
	{
		final Long	time	= addresses.get(address);
		// Connection available or dead.
		if(time!=null && time.longValue()!=0 && Math.abs(time.longValue())+ALIVETIME>System.currentTimeMillis())
		{
//			System.out.println("sending: "+task.hashCode());
			IResultCommand<IFuture<Void>, Void>	send	= new IResultCommand<IFuture<Void>, Void>()
			{
				public IFuture<Void> execute(Void args)
				{
					IFuture<Void>	ret;
					if(time.longValue()>0)
					{
						// Connection alive.
						ret	= queueDoSendTask(address, task);
					}
					else
					{
						// Connection dead.
//						System.err.println("Relay. Dead connection to: "+address+", "+time);
						ret	= new Future<Void>(new RuntimeException("No connection to "+address));
					}
					
//					ret.addResultListener(new IResultListener<Void>()
//					{
//						public void resultAvailable(Void result)
//						{
//							System.out.println("sent: "+task.hashCode());
//						}
//						public void exceptionOccurred(Exception exception)
//						{
//							System.out.println("send failed: "+task.hashCode());
//						}
//					});
					return ret;
				}
			};
			task.ready(send);
		}
		
		// Ping required or already running.
		else
		{
//			System.out.println("queueing: "+task.hashCode());
			queueReadySendTask(address, task, time==null || time.longValue()!=0);
		}
	}
	
	/**
	 *  Returns the prefix of this transport
	 *  @return Transport prefix.
	 */
	public String[] getServiceSchemas()
	{
		return SRelay.ADDRESS_SCHEMES;
	}
	
	/**
	 *  Get the addresses of this transport.
	 *  @return An array of strings representing the addresses 
	 *  of this message transport mechanism.
	 */
	public String[] getAddresses()
	{
		return awaonly ? SUtil.EMPTY_STRING_ARRAY : receiver.getAddresses();
	}
	
	/**
	 *  Queue a ready send task for execution after a ping.
	 */
	protected void	queueReadySendTask(final String address, ISendTask task, boolean ping)
	{
		synchronized(readyqueue)
		{
			Collection<ISendTask>	queue	= readyqueue.get(address);
			if(queue==null)
			{
				queue	= new ArrayList<ISendTask>();
				readyqueue.put(address, queue);
			}
			queue.add(task);
		}
		
		if(ping)
		{
			addresses.put(address, Long.valueOf(0));
			
			threadpool.execute(new Runnable()
			{
				public void run()
				{
					// Start new server ping.
//					System.out.println("pinging: "+address);
					try
					{
						conman.ping(address);
						addresses.put(address, Long.valueOf(System.currentTimeMillis()));
					}
					catch(Exception e)
					{
//						e.printStackTrace();
						component.getLogger().info("HTTP relay: No connection to "+address+", "+e);
						addresses.put(address, Long.valueOf(-System.currentTimeMillis()));
					}
					
					ISendTask[]	readytasks	= null;
					synchronized(readyqueue)
					{
						Collection<ISendTask>	queue	= readyqueue.get(address);
						if(queue!=null)
						{
							readytasks	= queue.toArray(new ISendTask[queue.size()]);
							readyqueue.remove(address);
						}
					}
					for(int i=0; readytasks!=null && i<readytasks.length; i++)
					{
						internalSendMessage(address, readytasks[i]);
					}
				}
			});
		}
		else
		{
			// Check if ping succeeded in mean time.
			Long	time	= addresses.get(address);
			if(time!=null && time.longValue()!=0)
			{
				ISendTask[]	readytasks	= null;
				synchronized(readyqueue)
				{
					Collection<ISendTask>	queue	= readyqueue.get(address);
					if(queue!=null)
					{
						readytasks	= queue.toArray(new ISendTask[queue.size()]);
						readyqueue.remove(address);
					}
				}
				for(int i=0; readytasks!=null && i<readytasks.length; i++)
				{
					internalSendMessage(address, readytasks[i]);
				}				
			}
		}
	}

	/**
	 *  Queue a send task for execution on a worker thread.
	 */
	protected IFuture<Void>	queueDoSendTask(final String address, ISendTask task)
	{
		Future<Void>	ret	= new Future<Void>();
		boolean	startworker	= false;
		synchronized(workers)
		{
			List<Tuple2<ISendTask, Future<Void>>>	queue	= sendqueue.get(address);
			if(queue==null)
			{
				queue	= new LinkedList<Tuple2<ISendTask,Future<Void>>>();
				sendqueue.put(address, queue);
			}
			queue.add(new Tuple2<ISendTask, Future<Void>>(task, ret));
			
			Integer	cnt	= workers.get(address);
			if(cnt==null)
			{
				cnt	= Integer.valueOf(0);
				workers.put(address, cnt);
			}
			if(cnt.intValue()<MAX_WORKERS)
			{
				workers.put(address, Integer.valueOf(cnt.intValue()+1));
				startworker	= true;
//				System.out.println("starting worker: "+workers.get(address));
			}
		}
		
		if(startworker)
		{
			threadpool.execute(new Worker(address));
		}
		return ret;
	}
	
	/**
	 *  Worker for sending messages.
	 */
	public class Worker implements Runnable
	{
		private final String	address;

		public Worker(String address)
		{
			this.address = address;
		}

		public void run()
		{
//			System.out.println("starting worker");
			boolean	again	= true;
			
			while(again)
			{
				ISendTask	task	= null;
				Future<Void>	ret	= null;
				synchronized(workers)
				{
					List<Tuple2<ISendTask, Future<Void>>>	queue	= sendqueue.get(address);
					if(queue!=null)
					{
						Tuple2<ISendTask, Future<Void>>	tup	= queue.remove(0);
						task	= tup.getFirstEntity();
						ret	= tup.getSecondEntity();
						if(queue.isEmpty())
						{
							sendqueue.remove(address);
						}
						// help gc when connection is aborted.
						tup	= null;
						queue	= null;
					}
					else
					{
						again	= false;
						Integer	cnt	= workers.get(address);
						if(cnt.intValue()>1)
						{
							workers.put(address, Integer.valueOf(cnt.intValue()-1));
						}
						else
						{
							workers.remove(address);
						}
					}
				}
				
				if(task!=null)
				{
					try
					{
						// Message service only calls transport.sendMessage() with receivers on same destination
						// so just use first to fetch platform id.
						IComponentIdentifier	targetid	= task.getReceivers()[0].getRoot();
						byte[][]	data	= new byte[][]{task.getProlog(), task.getData()};
						task	= null; // help gc when connection is aborted.
						conman.postMessage(address, targetid, data);
						addresses.put(address, Long.valueOf(System.currentTimeMillis()));
						ret.setResult(null);
//						System.out.println("Sent with HTTP Relay: "+System.currentTimeMillis()+", "+address);
					}
					catch(Exception e)
					{
						ret.setException(e);
					}
					finally
					{
						// help gc when connection is aborted.
						ret	= null;
					}
				}
			}
//			System.out.println("stopping worker");
		}
	}

}
