package jadex.base.service.message.transport.httprelaymtp.io;

import jadex.base.service.message.transport.codecs.GZIPCodec;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Binding;
import jadex.xml.bean.JavaReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 *  The receiver connects to the relay server
 *  and accepts messages.
 */
public class HttpReceiver
{
	//-------- attributes --------
	
	/** The receiver thread. */
	protected Thread	thread;

	/** The finished flag to stop thread execution. */
	protected boolean finished;
	
	/** The connection (if any). */
	protected HttpURLConnection	con;
	
	IExternalAccess access;
	String address;
	
	//-------- constructors --------
	
	/**
	 *  Create and start a new receiver.
	 */
	public HttpReceiver(final HttpRelayTransport transport, IExternalAccess access_, String address_)
	{
		this.access	= access_;
		this.address	= address_;
		SServiceProvider.getService(access.getServiceProvider(), IMessageService.class, Binding.SCOPE_PLATFORM)
			.addResultListener(new DefaultResultListener<IMessageService>()
		{
			public void resultAvailable(IMessageService ms)
			{
				// Todo: update cid at runtime?
				ms.updateComponentIdentifier(access.getComponentIdentifier().getRoot())
					.addResultListener(new DefaultResultListener<IComponentIdentifier>()
				{
					public void resultAvailable(final IComponentIdentifier cid)
					{
						thread	= new Thread(new Runnable()
						{
							public void run()
							{
								long	lasttry	= 0;
								while(!finished)
								{
									try
									{
										// When last connection attempt was less than 30 seconds ago, wait some time.
										if(lasttry!=0 && System.currentTimeMillis()-lasttry<HttpRelayTransport.ALIVETIME)
										{
											Thread.sleep(lasttry+HttpRelayTransport.ALIVETIME-System.currentTimeMillis());
										}
										
										if(!finished)
										{
											lasttry	= System.currentTimeMillis();
											String	xmlid	= cid.getName();
											URL	url	= new URL(address+"?id="+URLEncoder.encode(xmlid, "UTF-8"));
//											System.out.println("Connecting to: "+url);
											con	= (HttpURLConnection)url.openConnection();
											con.setUseCaches(false);
											
//											// Hack!!! Do not validate server (todo: enable/disable by platform argument).
//											if(con instanceof HttpsURLConnection)
//											{
//												HttpsURLConnection httpscon = (HttpsURLConnection) con;  
//										        httpscon.setHostnameVerifier(new HostnameVerifier()  
//										        {        
//										            public boolean verify(String hostname, SSLSession session)  
//										            {  
//										                return true;  
//										            }  
//										        });												
//											}
											
											InputStream	in	= con.getInputStream();
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
//													System.out.println("Received ping");
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
														access.scheduleStep(new IComponentStep<Void>()
														{
															public IFuture<Void> execute(final IInternalAccess ia)
															{
																ia.getServiceContainer().searchService(IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
																	.addResultListener(new IResultListener<IMessageService>()
																{
																	public void resultAvailable(IMessageService ms)
																	{
																		try
																		{
																			ms.deliverMessage(rawmsg);
																		}
																		catch(Exception e)
																		{
																			ia.getLogger().warning("Exception when delivering message: "+e+", "+rawmsg);													
																		}
																	}
																	
																	public void exceptionOccurred(Exception e)
																	{
																		ia.getLogger().warning("Exception when delivering message: "+e+", "+rawmsg);
																	}
																});
																return IFuture.DONE;
															}
														});
													}
												}
											}
										}
									}
									catch(final Exception e)
									{
										transport.connected(address, true);
										if(!finished)
										{
											access.scheduleStep(new IComponentStep<Void>()
											{
												public IFuture<Void> execute(IInternalAccess ia)
												{
													ia.getLogger().warning("Exception in HTTP releay receiver thread causing reconnect: "+e);
													return IFuture.DONE;
												}
											});
										}
									}
								}
							}
						});
						thread.start();
					}
				});
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Stop the receiver.
	 */
	public void	stop()
	{
		finished	= true;
		
		if(con!=null)
		{
			// Use sun.net.www.http.HttpClient.closeServer()
			// as con.disconnect() just blocks for sun default implementation :-(
			if(con.getClass().getName().equals("sun.net.www.protocol.http.HttpURLConnection"))
			{
				try
				{
					Field	f	= con.getClass().getDeclaredField("http");
					f.setAccessible(true);
					Object	client	= f.get(con);
					client.getClass().getMethod("closeServer", new Class[0]).invoke(client, new Object[0]);
					
				}
				catch(Exception e)
				{
					con.disconnect();	// Hangs until next ping :-(
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
				con.disconnect();
			}			
		}
		
		access	= null;
		address	= null;
		thread	= null;
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
}
