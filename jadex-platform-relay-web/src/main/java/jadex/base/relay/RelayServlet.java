package jadex.base.relay;

import jadex.base.service.message.MapSendTask;
import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.message.ICodec;
import jadex.commons.SUtil;
import jadex.commons.collection.ArrayBlockingQueue;
import jadex.commons.collection.IBlockingQueue;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.transformation.binaryserializer.BinarySerializer;
import jadex.xml.bean.JavaReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  The relay servlet allows distributing Jadex messages through firewall/NAT settings
 *  by acting as a central public relay.
 */
public class RelayServlet extends HttpServlet
{
	//-------- constants --------

	/** The directory for settings and statistics. */ 
	public final static File	SYSTEMDIR;
	
	static
	{
		File dir;
		String	home	= System.getenv("RELAY_HOME");	// System.getProperty() does not return environment variables, but just server VM properties.
		if(home!=null)
		{
			dir	= new File(home);
		}
		else
		{
			dir	= new File(System.getProperty("user.home"), ".relaystats");
		}
		
		if(!dir.exists())
		{
			dir.mkdirs();
		}
		else if(!dir.isDirectory())
		{
			throw new RuntimeException("Settings path '"+dir+"' is not a directory.");
		}
		SYSTEMDIR	= dir;
		
		System.out.println("Relay settings directory: "+SYSTEMDIR.getAbsolutePath());
	}
	
	//-------- attributes --------
	
	/** The relay map (id -> queue for pending requests). */
	protected Map<String, IBlockingQueue<Message>>	map;
	
	/** Info about connected platforms.*/
	protected Map<Object, PlatformInfo>	platforms;
	
	/** The available codecs for awareness infos (cached for speed). */
	protected Map<Byte, ICodec>	codecs;
	
	/** Counter for open GET connections (for testing). */
	protected int	opencnt1	= 0;
	/** Counter for open POST connections (for testing). */
	protected int	opencnt2	= 0;
//	/** Counter for queued messages (for testing). */	
//	protected int queued;
//	/** Counter for sent messages (for testing). */	
//	protected int sent;
	
	/** The peer list. */
	protected PeerList	peers;
	
	//-------- constructors --------

	/**
	 *  Initialize the servlet.
	 */
	public void init() throws ServletException
	{
		this.map	= Collections.synchronizedMap(new HashMap<String, IBlockingQueue<Message>>());
		this.platforms	= Collections.synchronizedMap(new LinkedHashMap<Object, PlatformInfo>());
		CodecFactory	cfac	= new CodecFactory();
		this.codecs	= cfac.getAllCodecs();
		this.peers	= new PeerList();
	}
	
	/**
	 *  Cleanup on sevlet shutdown.
	 */
	public void destroy()
	{
		this.peers.dispose();
		
		if(map!=null && !map.isEmpty())
		{
			for(Iterator<IBlockingQueue<Message>> it=map.values().iterator(); it.hasNext(); )
			{
				IBlockingQueue<Message>	queue	= it.next();
				it.remove();
				List<Message>	items	= queue.setClosed(true);
				for(int i=0; i<items.size(); i++)
				{
					items.get(i).getFuture().setException(new RuntimeException("Target disconnected."));
				}
			}
		}
		if(platforms!=null && !platforms.isEmpty())
		{
			for(Iterator<PlatformInfo> it=platforms.values().iterator(); it.hasNext(); )
			{
				it.next().disconnect();	// Writes end time in DB.
			}
		}
		StatsDB.getDB().shutdown();
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a platform registers itself at the relay. 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if("/ping".equals(request.getServletPath()))
		{
			// somebody is checking, if the server is available, just return an empty http ok.
		}
		else if(request.getServletPath().startsWith("/resources"))
		{
			// serve images etc. (hack? url mapping doesn't support excludes and we want the relay servlet to react to the wepapp root url.
			serveResource(request, response);
		}
		else
		{
	
			String	id	= request.getParameter("id");
			// Render status page.
			if(id==null)
			{
				String	view;
				// todo: add request property
				if("/history".equals(request.getServletPath()))
				{
					request.setAttribute("platforms", StatsDB.getDB().getPlatformInfos(20));
					view	= "/WEB-INF/jsp/history.jsp";
				}
				else if("/history_all".equals(request.getServletPath()))
				{
					request.setAttribute("platforms", StatsDB.getDB().getPlatformInfos(-1));
					view	= "/WEB-INF/jsp/history.jsp";
				}
				else if("/export".equals(request.getServletPath()))
				{
					request.setAttribute("platforms", StatsDB.getDB().getAllPlatformInfos());
					view	= "/WEB-INF/jsp/csv.jsp";
				}
				else if("/servers".equals(request.getServletPath()))
				{
					String	peerurl	= request.getParameter("peerurl");
					if(peerurl!=null)
					{
						peers.addPeer(peerurl, false);
					}
					request.setAttribute("peers", peers.getURLs(request));					
					view	= "/WEB-INF/jsp/servers.jsp";
				}
				else
				{
					// Fetch array to avoid concurrency problems
					request.setAttribute("platforms", platforms.values().toArray(new PlatformInfo[0]));
					request.setAttribute("refresh", "30");
					view	= "/WEB-INF/jsp/status.jsp";
				}
				RequestDispatcher	rd	= getServletContext().getRequestDispatcher(view);
				rd.forward(request, response);
			}
			
			// Handle platform connection.
			else
			{
				synchronized(this)
				{
					opencnt1++;
				}
				System.out.println("Entering GET request. opencnt="+opencnt1+", mapsize="+map.size()+", infosize="+platforms.size());
				
				
				PlatformInfo	info	= platforms.get(id);
				if(info==null)
				{
					info	= new PlatformInfo(id, request.getRemoteAddr(), request.getRemoteHost(), request.getScheme());
					platforms.put(id, info);
				}
				else
				{
					info.reconnect(request.getRemoteAddr(), request.getRemoteHost());
				}
				
				IBlockingQueue<Message>	queue	= map.get(id);
				List<Message>	items	= null;
				if(queue!=null)
				{
					// Close old queue to free old servlet request
					items	= queue.setClosed(true);
					queue	= 	new ArrayBlockingQueue<Message>();
					// Add outstanding requests to new queue.
					for(int i=0; i<items.size(); i++)
					{
						queue.enqueue(items.get(i));
					}
				}
				else
				{
					queue	= 	new ArrayBlockingQueue<Message>();
				}
				map.put(id, queue);
				
				// Set cache header to avoid interference of proxies (e.g. vodafone umts)
				response.setHeader("Cache-Control", "no-cache, no-transform");
				response.setHeader("Pragma", "no-cache");
				
				// Ping to let client know that it is connected.
				response.getOutputStream().write(SRelay.MSGTYPE_PING);
				response.flushBuffer();
				
		//		System.out.println("Added to map. New size: "+map.size());
				try
				{
					while(true)
					{
						try
						{
							// Get next request from queue.
							Message	msg	= queue.dequeue(SRelay.PING_DELAY);	// Todo: make ping delay configurable on per client basis
							try
							{
								// Send message header.
								response.getOutputStream().write(msg.getMessageType());
								
								// Copy message to output stream.
								long	start	= System.nanoTime();
								byte[]	buf	= new byte[8192];  
								int	len;
								int	cnt	= 0;
								while((len=msg.getContent().read(buf)) != -1)
								{
									response.getOutputStream().write(buf, 0, len);
									cnt	+= len;
								}
								response.getOutputStream().flush();
								info.addMessage(cnt, System.nanoTime()-start);
								msg.getFuture().setResult(null);
//								synchronized(this)
//								{
//									sent++;
//								}
//								System.out.println("sent: "+sent);
							}
							catch(Exception e)
							{
								msg.getFuture().setException(e);
								throw e;	// rethrow exception to end servlet execution for client.
							}
						}
						catch(TimeoutException te)
						{
							// Send ping and continue loop.
							response.getOutputStream().write(SRelay.MSGTYPE_PING);  
							response.getOutputStream().flush();
						}
					}
				}
				catch(Exception e)
				{
					// exception on queue, when same platform reconnects or servlet is destroyed
					// exception on output stream, when client disconnects
				}
				
				if(!queue.isClosed())
				{
					items	= queue.setClosed(true);
					map.remove(id);
					PlatformInfo	platform	= platforms.remove(id);
					if(platform!=null)
						platform.disconnect();
					AwarenessInfo	awainfo	= platform!=null ? platform.getAwarenessInfo() : null;
					if(awainfo!=null)
					{
						awainfo.setState(AwarenessInfo.STATE_OFFLINE);
						sendAwarenessInfos(awainfo, platform.getPreferredCodecs());
					}
			//		System.out.println("Removed from map ("+items.size()+" remaining items). New size: "+map.size());
					for(int i=0; i<items.size(); i++)
					{
						items.get(i).getFuture().setException(new RuntimeException("Target disconnected."));
					}
				}
				
				synchronized(this)
				{
					opencnt1--;
				}
				System.out.println("Leaving GET request. opencnt="+opencnt1+", mapsize="+map.size()+", infosize="+platforms.size());
			}
		}
	}

	/**
	 *  Called when a message should be sent.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		synchronized(this)
		{
			opencnt2++;
		}
//		System.out.println("Entering POST request. opencnt="+opencnt2+", mapsize="+map.size());
//		String	s;
//		s	= request.getContextPath();
//		s	= request.getPathInfo();
//		s	= request.getQueryString();
//		s	= request.getServletPath();
//		s	= request.getRequestURL().toString();
		if("/awareness".equals(request.getServletPath()) || "/awareness/".equals(request.getServletPath()))	// new code always adds slash.
		{
			ServletInputStream	in	= request.getInputStream();
			// Read target id.
			readString(in);
			
			// Read total message length.
			byte[]	len	= readData(in, 4);
			int	length	= SUtil.bytesToInt(len);
			
			// Read message and extract awareness info content.
			byte[] buffer = readData(in, length-1);
			MessageEnvelope	msg	= (MessageEnvelope)MapSendTask.decodeMessage(buffer, codecs, getClass().getClassLoader());
			ICodec[]	pcodecs	= MapSendTask.getCodecs(buffer, codecs);
			AwarenessInfo	info;
			if(SFipa.JADEX_RAW.equals(msg.getMessage().get(SFipa.LANGUAGE)))
			{
				info = (AwarenessInfo)msg.getMessage().get(SFipa.CONTENT);
			}
			else if(SFipa.JADEX_XML.equals(msg.getMessage().get(SFipa.LANGUAGE)))
			{
				info = (AwarenessInfo)JavaReader.objectFromByteArray((byte[])msg.getMessage().get(SFipa.CONTENT), getClass().getClassLoader());
			}
			else //if(SFipa.JADEX_BINARY.equals(msg.getMessage().get(SFipa.LANGUAGE)))
			{
				info = (AwarenessInfo)BinarySerializer.objectFromByteArray((byte[])msg.getMessage().get(SFipa.CONTENT), null, null, getClass().getClassLoader(), null);
			}
			sendAwarenessInfos(info, pcodecs);
		}
		else
		{
			ServletInputStream	in	= request.getInputStream();
			String	targetid	= readString(in);
			boolean	sent	= false;
			
			// Only send message when request is not https or target is also connected via https.
			PlatformInfo	targetpi	= platforms.get(targetid);
			if(targetpi!=null && (!request.getScheme().equals("https") || targetpi.getScheme().equals("https")))
			{
				IBlockingQueue<Message>	queue	= map.get(targetid);
				if(queue!=null)
				{
					try
					{
						Message	msg	= new Message(SRelay.MSGTYPE_DEFAULT, in);
						queue.enqueue(msg);
	//					synchronized(this)
	//					{
	//						queued++;
	//					}
	//					System.out.println("queued: "+queued);
						msg.getFuture().get(new ThreadSuspendable(), 30000);	// todo: how to set a useful timeout value!?
						sent	= true;
					}
					catch(Exception e)
					{
						// timeout or platform just disconnected
					}
				}
			}
			
			if(!sent)
			{
				// Set content length to avoid error page being sent.
				response.setStatus(404);
				response.setContentLength(0);
			}
		}
		synchronized(this)
		{
			opencnt2--;
		}
//		System.out.println("Leaving POST request. opencnt="+opencnt2+", mapsize="+map.size());
	}
	
	//-------- helper methods --------
	
	/**
	 *  Send awareness messages for a new or changed awareness info.
	 */
	protected void	sendAwarenessInfos(AwarenessInfo awainfo, ICodec[] pcodecs)
	{
		// Update platform awareness info.
		String	id	= awainfo.getSender().getPlatformName();
		PlatformInfo	platform	= platforms.get(id);
		boolean	initial	= platform!=null && platform.getAwarenessInfo()==null && AwarenessInfo.STATE_ONLINE.equals(awainfo.getState());
		if(platform!=null)
		{
			platform.setAwarenessInfo(awainfo);
			platform.setPreferredCodecs(pcodecs);
		}
		
		if(platform!=null || AwarenessInfo.STATE_OFFLINE.equals(awainfo.getState()))
		{
			byte[]	propinfo	= null;
			byte[]	nopropinfo	= null;
			
			Map.Entry<String, IBlockingQueue<Message>>[]	entries	= map.entrySet().toArray(new Map.Entry[0]);
			for(int i=0; i<entries.length; i++)
			{
				// Send awareness to other platforms with awareness on.
				PlatformInfo	p2	= platforms.get(entries[i].getKey());
				AwarenessInfo	awainfo2	= p2!=null ? p2.getAwarenessInfo() : null;
				if(awainfo2!=null && !id.equals(entries[i].getKey()))
				{
					try
					{
						// Send awareness infos with or without properties, for backwards compatibility with Jadex 2.1
						if(awainfo2.getProperties()==null && nopropinfo==null)
						{
							AwarenessInfo	awanoprop	= awainfo;
							if(awainfo.getProperties()!=null)
							{
								awanoprop	= new AwarenessInfo(awainfo.getSender(), awainfo.getState(), awainfo.getDelay(), awainfo.getIncludes(), awainfo.getExcludes(), awainfo.getMasterId());
								awanoprop.setProperties(null);
							}
							
							byte[]	data	= MapSendTask.encodeMessage(awanoprop, pcodecs, getClass().getClassLoader());
							nopropinfo	= new byte[data.length+4];
							System.arraycopy(SUtil.intToBytes(data.length), 0, nopropinfo, 0, 4);
							System.arraycopy(data, 0, nopropinfo, 4, data.length);
							
							if(awainfo.getProperties()==null)
							{
								propinfo	= nopropinfo;
							}

						}
						else if(awainfo2.getProperties()!=null && propinfo==null)
						{
							byte[]	data	= MapSendTask.encodeMessage(awainfo, pcodecs, getClass().getClassLoader());
							propinfo	= new byte[data.length+4];
							System.arraycopy(SUtil.intToBytes(data.length), 0, propinfo, 0, 4);
							System.arraycopy(data, 0, propinfo, 4, data.length);
							
							if(awainfo.getProperties()==null)
							{
								nopropinfo	= propinfo;
							}

						}
						
						entries[i].getValue().enqueue(new Message(SRelay.MSGTYPE_AWAINFO, new ByteArrayInputStream(awainfo2.getProperties()==null ? nopropinfo : propinfo)));
					}
					catch(Exception e)
					{
						// Queue closed, because platform just disconnected.
					}
					
					// Send other awareness infos to newly connected platform.
					if(initial)
					{
						byte[]	data2	= MapSendTask.encodeMessage(awainfo2, p2.getPreferredCodecs(), getClass().getClassLoader());
						byte[]	info2	= new byte[data2.length+4];
						System.arraycopy(SUtil.intToBytes(data2.length), 0, info2, 0, 4);
						System.arraycopy(data2, 0, info2, 4, data2.length);
						
						try
						{
							map.get(id).enqueue(new Message(SRelay.MSGTYPE_AWAINFO, new ByteArrayInputStream(info2)));
						}
						catch(Exception e)
						{
							// Queue closed, because platform just disconnected.
						}
					}
				}
			}
		}
	}

	/**
	 * 	Read a string from the given stream.
	 *  @param in	The input stream.
	 *  @return The string.
	 *  @throws	IOException when the stream is closed.
	 */
	public static String	readString(InputStream in) throws IOException
	{
		byte[]	len	= readData(in, 4);
		int	length	= SUtil.bytesToInt(len);
		byte[] buffer = readData(in, length);
		return new String(buffer, "UTF-8");
	}
	
	/**
	 *  Read data into a byte array.
	 */
	protected static byte[] readData(InputStream is, int length) throws IOException
	{
		int num	= 0;
		byte[]	buffer	= new byte[length];
		while(num<length)
		{
			int read	= is.read(buffer, num, length-num);
			if(read==-1)
			{
				throw new IOException("Stream closed.");
			}
			num	= num + read;
		}
		return buffer;
	}
	
	/**
	 *  Serve a static resource from the file system.
	 */
	protected void serveResource(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
        File	file	= new File(getServletContext().getRealPath(request.getServletPath()));
        if(!file.canRead())
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getRequestURI());
        }
        else
        {
        	response.setContentLength((int)file.length());
            response.setDateHeader("Last-Modified", file.lastModified());
        	String	mimetype	= URLConnection.guessContentTypeFromName(file.getName());
        	if(mimetype!=null)
        	{
        		response.setContentType(mimetype);
        	}
        	
			// Copy file content to output stream.
        	FileInputStream	in	= new FileInputStream(file);
			byte[]	buf	= new byte[8192];  
			int	len;
			while((len=in.read(buf)) != -1)
			{
				response.getOutputStream().write(buf, 0, len);
			}
			in.close();
        }
	}
}
