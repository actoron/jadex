package jadex.base.service.message.transport.httprelaymtp.io;

import jadex.base.service.message.transport.codecs.GZIPCodec;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Binding;
import jadex.xml.bean.JavaReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  The receiver connects to the relay server
 *  and accepts messages.
 */
public class HttpReceiver
{
	//-------- attributes --------
	
	/** The transport. */
	protected HttpRelayTransport	transport;

	/** The thread pool. */
	protected IThreadPoolService	threadpool;
	
	/** The shutdown flag. */
	protected boolean	shutdown;
	
	/** The connections (if any). */
	protected List<HttpURLConnection>	cons;
	
	/** The external access. */
	protected IExternalAccess access;
	
	/** The default addresses. */
	protected String defaddresses;
	
	/** The current connected server address (if any). */
	protected String address;
	
	/** The logger (set on first access). */
	protected Logger	logger;
	
	//-------- constructors --------
	
	/**
	 *  Create a new receiver.
	 */
	public HttpReceiver(HttpRelayTransport transport, IExternalAccess access, String defaddresses, IThreadPoolService threadpool)
	{
		this.transport	= transport;
		this.access	= access;
		this.defaddresses	= defaddresses;
		this.threadpool	= threadpool;
		this.cons	= Collections.synchronizedList(new ArrayList<HttpURLConnection>());
	}
	
	//-------- methods --------
	
	/**
	 *  (Re-)Start the receiver.
	 */
	public void start()
	{
		if(!shutdown)
		{
			final long	lasttry	= System.currentTimeMillis();
			fetchServerAddresses().addResultListener(new IResultListener<String>()
			{
				public void resultAvailable(String curadrs)
				{
					if(!shutdown)
					{
						selectServer(curadrs).addResultListener(new IResultListener<String>()
						{
							public void resultAvailable(String adr)
							{
								if(!shutdown)
								{
									handleConnection(adr).addResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
											// Connection should always end with error. 
											assert true;
										}
										
										public void exceptionOccurred(Exception exception)
										{
											restart(exception);
										}
									});
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
								restart(exception);
							}
						});
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					restart(exception);
				}
				
				protected void restart(Exception e)
				{
					if(address!=null)
					{
						HttpReceiver.this.transport.connected(address, true);
						address	= null;
					}
					
					if(!shutdown)
					{
						log(Level.WARNING, "Relay transport exception causing reconnect: "+e);
						
						// When last connection attempt was less than 30 seconds ago, wait some time.
						if(System.currentTimeMillis()-lasttry<HttpRelayTransport.ALIVETIME)
						{
							long	sleep	= lasttry+HttpRelayTransport.ALIVETIME-System.currentTimeMillis();
							log(Level.INFO, "Relay transport waiting "+sleep+" milliseconds before reconnect.");
							Timer	timer	= new Timer(true);
							timer.schedule(new TimerTask()
							{
								public void run()
								{
									if(!shutdown)
									{
										start();
									}
								}
							}, sleep);
						}
						else
						{
							start();							
						}
					}
				}
			});
		}
	}
	
	/**
	 *  Stop the receiver.
	 */
	public void	stop()
	{
		shutdown	= true;
		
		HttpURLConnection[]	acon	= cons.toArray(new HttpURLConnection[0]);
		for(int i=0; i<acon.length; i++)
		{
			// Use sun.net.www.http.HttpClient.closeServer()
			// as con.disconnect() just blocks for sun default implementation :-(
			if(acon[i].getClass().getName().equals("sun.net.www.protocol.http.HttpURLConnection"))
			{
				try
				{
					Field	f	= acon[i].getClass().getDeclaredField("http");
					f.setAccessible(true);
					Object	client	= f.get(acon[i]);
					client.getClass().getMethod("closeServer", new Class[0]).invoke(client, new Object[0]);
					
				}
				catch(Exception e)
				{
					acon[i].disconnect();	// Hangs until next ping :-(
				}
			}
			// Special treatment for android impl not needed, because disconnect() works fine. 
//			else if()
//			{
//				// org.apache.harmony.luni.internal.net.www.protocol.http.HttpURLConnectionImpl	con;
//				// org.apache.harmony.luni.internal.net.www.protocol.http.HttpConnection	connection = con.connection;
//				// Socket socket	= connection.socket;
//				Field	f	= con.getClass().getDeclaredField("connection");
//				f.setAccessible(true);
//				Object	connection	= f.get(con);
//				f	= connection.getClass().getDeclaredField("socket");
//				f.setAccessible(true);
//				Socket	socket	= (Socket)f.get(connection);
//				socket.close();				
//			}
			else
			{
				acon[i].disconnect();
			}			
		}
		
		access	= null;
		address	= null;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Read a complete message from the stream.
	 */
	protected static byte[]	readMessage(InputStream in) throws IOException
	{
		byte[] rawmsg	= null;
		
		// Read message header (size)
		int msg_size;
		byte[] asize = new byte[4];
		for(int i=0; i<asize.length; i++)
		{
			int	b	= in.read();
			if(b==-1) 
				throw new IOException("Stream closed");
			asize[i] = (byte)b;
		}
		
		msg_size = SUtil.bytesToInt(asize);
//		System.out.println("reclen: "+msg_size);
		if(msg_size>0)
		{
			rawmsg = new byte[msg_size];
			int count = 0;
			while(count<msg_size) 
			{
				int bytes_read = in.read(rawmsg, count, msg_size-count);
				if(bytes_read==-1) 
					throw new IOException("Stream closed");
				count += bytes_read;
			}
		}
		
		return rawmsg;
	}
	
	/**
	 *  Post a received awareness info to awareness service (if any).
	 */
	protected void	postAwarenessInfo(final byte[] data, final int type)
	{
		SServiceProvider.getService(access.getServiceProvider(), IAwarenessManagementService.class, Binding.SCOPE_PLATFORM)
			.addResultListener(new IResultListener<IAwarenessManagementService>()
		{
			public void resultAvailable(IAwarenessManagementService awa)
			{
				try
				{
					AwarenessInfo	info	= (AwarenessInfo)JavaReader.objectFromByteArray(
						GZIPCodec.decodeBytes(data, getClass().getClassLoader()), getClass().getClassLoader());
					awa.addAwarenessInfo(info);
				}
				catch(Exception e)
				{
					System.out.println("Error receiving awareness info: "+e);										
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// No awa service -> ignore awa infos.
			}
		});		
	}

	/**
	 *  Get the current addresses (if any).
	 */
	public synchronized String[]	getAddresses()
	{
		return address!=null ? new String[]{address} : SUtil.EMPTY_STRING_ARRAY;
	}

	/**
	 *  Do some log output.
	 */
	protected void	log(final Level level, final String msg)
	{
		if(logger!=null)
		{
			logger.log(level, msg);
		}
		else
		{
			access.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					logger	= ia.getLogger();
					logger.log(level, msg);
					return IFuture.DONE;
				}
			});
		}
	}
	
	//-------- connection handling --------
	
	/**
	 *  Fetch an up-to-date server list.
	 *  @return A comma separated list of potential servers.
	 */
	protected IFuture<String>	fetchServerAddresses()
	{
		log(Level.INFO, "Relay transport fetching server addresses from: "+defaddresses);
		
		final Future<String>	ret	= new Future<String>();
		StringTokenizer	stok	= new StringTokenizer(defaddresses, ", ");
		final CounterResultListener<Void>	crl	= new CounterResultListener<Void>(stok.countTokens(), true,
			new ExceptionDelegationResultListener<Void, String>(ret)
		{
			public void customResultAvailable(Void result)
			{
				// If all threads done, but no result -> set exception.
				ret.setExceptionIfUndone(new RuntimeException("Cannot retrieve server list."));
			}
		});
		
		while(stok.hasMoreTokens())
		{
			final String	adr	= stok.nextToken().trim().substring(6);	// strip 'relay-' prefix.
			threadpool.execute(new Runnable()
			{
				public void run()
				{
					URLConnection	con	= null;
					try
					{
						URL	url	= new URL(adr+(adr.endsWith("/") ? "servers" : "/servers"));
						con	= url.openConnection();
						cons.add((HttpURLConnection)con);
						if(con.getContentType().startsWith("text/plain"))
						{
							String	curadrs	= new Scanner(con.getInputStream()).useDelimiter("\\A").next();
							log(Level.INFO, "Relay transport got server addresses from: "+adr+", "+curadrs);
							ret.setResultIfUndone(curadrs);
						}
						crl.resultAvailable(null);
					}
					catch(Exception e)
					{
						crl.exceptionOccurred(e);
					}
					if(con!=null)
					{
						cons.remove(con);
					}
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Select a server to connect to.
	 *  @return The address of an available server.
	 */
	protected IFuture<String>	selectServer(String curadrs)
	{
		log(Level.INFO, "Relay transport selecting server from: "+curadrs);
		final Future<String>	ret	= new Future<String>();
		StringTokenizer	stok	= new StringTokenizer(curadrs, ", ");
		List<String>	adrs	= new LinkedList<String>();
		Random	rnd	= new Random();
		while(stok.hasMoreTokens())
		{
			// Insert addresses randomly to distribute load across servers.
			adrs.add(rnd.nextInt(adrs.size()+1), stok.nextToken().trim());
		}
		
		final CounterResultListener<Void>	crl	= new CounterResultListener<Void>(adrs.size(), true,
			new ExceptionDelegationResultListener<Void, String>(ret)
		{
			public void customResultAvailable(Void result)
			{
				// If all threads done, but no result -> set exception.
				ret.setExceptionIfUndone(new RuntimeException("No server available."));
			}
		});
		
		for(int i=0; i<adrs.size(); i++)
		{
			final String	adr	= adrs.get(i);
			threadpool.execute(new Runnable()
			{
				public void run()
				{
					if(!ret.isDone())
					{
						URLConnection	con	= null;
						try
						{
							URL	url	= new URL(adr.substring(6)+(adr.endsWith("/") ? "ping" : "/ping")); // strip 'relay-' prefix.
							con	= url.openConnection();
							cons.add((HttpURLConnection)con);
							if(((HttpURLConnection)con).getResponseCode()==200)
							{
								log(Level.INFO, "Relay transport found server: "+adr);
								ret.setResultIfUndone(adr);
							}
							crl.resultAvailable(null);
						}
						catch(Exception e)
						{
							crl.exceptionOccurred(e);
						}
						if(con!=null)
						{
							cons.remove(con);
						}
					}
				}
			});
			
			if(!ret.isDone())
			{
				try
				{
					Thread.sleep(20);	// Short delay to give servers a chance to reply.
				}
				catch(InterruptedException e)
				{
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Main thread while connected.
	 *  @return When the connection is closed.
	 */
	protected IFuture<Void> handleConnection(final String adr)
	{
		final Future<Void>	ret	= new Future<Void>();
		SServiceProvider.getService(access.getServiceProvider(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
		{
			public void customResultAvailable(final IMessageService ms)
			{
				threadpool.execute(new Runnable()
				{
					public void run()
					{
						HttpURLConnection	con	= null;
						try
						{
							String	xmlid	= HttpReceiver.this.access.getComponentIdentifier().getRoot().getName();
							URL	url	= new URL(adr.substring(6)+"?id="+URLEncoder.encode(xmlid, "UTF-8")); // strip 'relay-' prefix.
							con	= (HttpURLConnection)url.openConnection();
							cons.add(con);
							con.setUseCaches(false);
							
	//						// Hack!!! Do not validate server (todo: enable/disable by platform argument).
	//						if(con instanceof HttpsURLConnection)
	//						{
	//							HttpsURLConnection httpscon = (HttpsURLConnection) con;  
	//					        httpscon.setHostnameVerifier(new HostnameVerifier()  
	//					        {        
	//					            public boolean verify(String hostname, SSLSession session)  
	//					            {  
	//					                return true;  
	//					            }  
	//					        });												
	//						}
							
							InputStream	in	= con.getInputStream();
							address	= adr;
							transport.connected(address, false);
							while(true)
							{
								// Read message type.
								int	b	= in.read();
								if(b==-1)
								{
									throw new IOException("Stream closed");
								}
								else if(b==SRelay.MSGTYPE_PING)
								{
	//								System.out.println("Received ping");
								}
								else if(b==SRelay.MSGTYPE_AWAINFO)
								{
									final byte[] rawmsg = readMessage(in);
									postAwarenessInfo(rawmsg, b);
								}
								else if(b==SRelay.MSGTYPE_DEFAULT)
								{
									final byte[] rawmsg = readMessage(in);
									if(rawmsg!=null)
									{
										try
										{
											ms.deliverMessage(rawmsg);
										}
										catch(Exception e)
										{
											log(Level.WARNING, "Relay transport exception when delivering message: "+e+", "+rawmsg);
										}
									}
								}
							}		
						}
						catch(Exception e)
						{
							ret.setException(e);
						}
						
						if(con!=null)
						{
							cons.remove(con);
						}
					}
				});
			}
		});
		return ret;
	}
}
