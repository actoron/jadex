package jadex.base.relay;

import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.GZIPCodec;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.commons.SUtil;
import jadex.commons.collection.ArrayBlockingQueue;
import jadex.commons.collection.IBlockingQueue;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.ThreadSuspendable;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
	//-------- attributes --------
	
	/** The relay map (id -> queue for pending requests). */
	protected Map<String, IBlockingQueue<Message>>	map;
	
	/** Info about connected platforms.*/
	protected Map<Object, PlatformInfo>	platforms;
	
	/** Counter for open GET connections (for testing). */
	protected int	opencnt1	= 0;
	/** Counter for open POST connections (for testing). */
	protected int	opencnt2	= 0;
//	/** Counter for queued messages (for testing). */	
//	protected int queued;
//	/** Counter for sent messages (for testing). */	
//	protected int sent;
	
	//-------- constructors --------

	/**
	 *  Initialize the servlet.
	 */
	public void init() throws ServletException
	{
		map	= Collections.synchronizedMap(new HashMap<String, IBlockingQueue<Message>>());
		platforms	= Collections.synchronizedMap(new LinkedHashMap<Object, PlatformInfo>());
	}
	
	/**
	 *  Cleanup on sevlet shutdown.
	 */
	public void destroy()
	{
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
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a platform registers itself at the relay. 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if("/ping".equals(request.getServletPath()))
		{
		}
		else
		{
	
			String	id	= request.getParameter("id");
			
			// Render status page.
			if(id==null)
			{
				request.setAttribute("platforms", platforms);
				String	view	= "/WEB-INF/jsp/status.jsp";
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
					info	= new PlatformInfo(id, request.getRemoteHost(), request.getScheme());
					platforms.put(id, info);
				}
				else
				{
					info.reconnect(request.getRemoteHost());
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
					AwarenessInfo	awainfo	= platform!=null ? platform.getAwarenessInfo() : null;
					if(awainfo!=null)
					{
						awainfo.setState(AwarenessInfo.STATE_OFFLINE);
						sendAwarenessInfos(awainfo);
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
		System.out.println("Entering POST request. opencnt="+opencnt2+", mapsize="+map.size());
//		String	s;
//		s	= request.getContextPath();
//		s	= request.getPathInfo();
//		s	= request.getQueryString();
//		s	= request.getServletPath();
//		s	= request.getRequestURL().toString();
		if("/awareness".equals(request.getServletPath()))
		{
			ServletInputStream	in	= request.getInputStream();
			// Read target id.
			readString(in);
			
			// Read total message length.
			byte[]	len	= readData(in, 4);
			int	length	= SUtil.bytesToInt(len);

			// Read prolog (1 byte codec length + 1 byte xml codec id + 1 byte gzip codec id)
			readData(in, 4);
			
			// Read message and extract awareness info content.
			byte[] buffer = readData(in, length-4);
			buffer	= GZIPCodec.decodeBytes(buffer, getClass().getClassLoader());
			MessageEnvelope	msg	= (MessageEnvelope)JavaReader.objectFromByteArray(buffer, getClass().getClassLoader());
			AwarenessInfo	info	= (AwarenessInfo)JavaReader.objectFromByteArray((byte[])msg.getMessage().get(SFipa.CONTENT), getClass().getClassLoader());
			sendAwarenessInfos(info);
		}
		else
		{
			ServletInputStream	in	= request.getInputStream();
			String	targetid	= readString(in);
			boolean	sent	= false;
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
		System.out.println("Leaving POST request. opencnt="+opencnt2+", mapsize="+map.size());
	}
	
	//-------- helper methods --------
	
	/**
	 *  Send awareness messages for a new or changed awareness info.
	 */
	protected void	sendAwarenessInfos(AwarenessInfo awainfo)
	{
		// Update platform awareness info.
		String	id	= awainfo.getSender().getPlatformName();
		PlatformInfo	platform	= platforms.get(id);
		boolean	initial	= platform!=null && platform.getAwarenessInfo()==null && AwarenessInfo.STATE_ONLINE.equals(awainfo.getState());
		if(platform!=null)
		{
			platform.setAwarenessInfo(awainfo);
		}
		
		if(platform!=null ||  AwarenessInfo.STATE_OFFLINE.equals(awainfo.getState()))
		{
			byte[]	data	= GZIPCodec.encodeBytes(JavaWriter.objectToByteArray(awainfo, getClass().getClassLoader()), getClass().getClassLoader());
			byte[]	info	= new byte[data.length+4];
			System.arraycopy(SUtil.intToBytes(data.length), 0, info, 0, 4);
			System.arraycopy(data, 0, info, 4, data.length);
			
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
						entries[i].getValue().enqueue(new Message(SRelay.MSGTYPE_AWAINFO, new ByteArrayInputStream(info)));
					}
					catch(Exception e)
					{
						// Queue closed, because platform just disconnected.
					}
					
					// Send other awareness infos to newly connected platform.
					if(initial)
					{
						byte[]	data2	= GZIPCodec.encodeBytes(JavaWriter.objectToByteArray(awainfo2, getClass().getClassLoader()), getClass().getClassLoader());
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
	 * 	Read an object from the given stream.
	 *  @param in	The input stream.
	 *  @return The object.
	 *  @throws	IOException when the stream is closed.
	 */
	public static Object	readObject(InputStream in) throws IOException
	{
		byte[]	len	= readData(in, 4);
		int	length	= SUtil.bytesToInt(len);
		byte[] buffer = readData(in, length);
		return JavaReader.objectFromByteArray(buffer, RelayServlet.class.getClassLoader());
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
}
