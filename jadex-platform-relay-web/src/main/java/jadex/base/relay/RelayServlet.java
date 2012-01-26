package jadex.base.relay;

import jadex.base.fipa.SFipa;
import jadex.base.service.message.transport.MessageEnvelope;
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
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
	protected Map<Object, IBlockingQueue<Message>>	map;
	
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
		map	= Collections.synchronizedMap(new HashMap<Object, IBlockingQueue<Message>>());
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
		String	idstring	= request.getParameter("id");
		
		// Render status page.
		if(idstring==null)
		{
			request.setAttribute("platforms", platforms);
			String	view	= "/WEB-INF/jsp/status.jsp";
			RequestDispatcher	rd	= getServletContext().getRequestDispatcher(view);
			rd.forward(request, response);
		}
		
		// Handle platform connection.
		else
		{
			System.out.println("Entering GET request. opencnt="+(++opencnt1)+", mapsize="+map.size()+", infosize="+platforms.size());
			
			Object	id	= JavaReader.objectFromXML(idstring, getClass().getClassLoader());
			
			if(!"benchmark".equals(id))
			{
				PlatformInfo	info	= platforms.get(id);
				if(info==null)
				{
					info	= new PlatformInfo(id, request.getRemoteHost());
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
							Message	msg	= queue.dequeue(30000);	// Todo: make ping delay configurable on per client basis
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
					platforms.remove(id);
//					sendAwarenessInfos(id, false);
			//		System.out.println("Removed from map ("+items.size()+" remaining items). New size: "+map.size());
					for(int i=0; i<items.size(); i++)
					{
						items.get(i).getFuture().setException(new RuntimeException("Target disconnected."));
					}
				}
			}
			else
			{
				int	size	= Integer.parseInt(request.getParameter("size"));
				Random	rnd	= new Random();
					
				// Ping to let client know that it is connected.
				response.getOutputStream().write(SRelay.MSGTYPE_PING);  
				response.flushBuffer();
					
				try
				{
					while(true)
					{
						// Send message header.
						response.getOutputStream().write(SRelay.MSGTYPE_DEFAULT);
						
						// Send message to output stream.
						byte[]	msg	= new byte[size];
						rnd.nextBytes(msg);
						response.getOutputStream().write(SUtil.intToBytes(size));
						response.getOutputStream().write(msg);
						response.getOutputStream().flush();
					}
				}
				catch(Exception e)
				{
					// exception on output stream, when client disconnects
				}				
			}
			
			System.out.println("Leaving GET request. opencnt="+(--opencnt1)+", mapsize="+map.size()+", infosize="+platforms.size());
		}
	}

	/**
	 *  Called when a message should be sent.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		System.out.println("Entering POST request. opencnt="+(++opencnt2)+", mapsize="+map.size());
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
			Object	targetid	= readObject(in);
			
			// Read total message length.
			byte[]	len	= readData(in, 4);
			int	length	= SUtil.bytesToInt(len);

			// Read prolog (1 byte codec length + 1 byte xml codec id)
			readData(in, 2);
			
			// Read message.
			byte[] buffer = readData(in, length-2);
			MessageEnvelope	msg	= (MessageEnvelope)JavaReader.objectFromByteArray(buffer, SRelay.class.getClassLoader());
			AwarenessInfo	info	= (AwarenessInfo)msg.getMessage().get(SFipa.CONTENT);
			System.out.println("awareness request: "+targetid+", "+info);
		}
		else
		{
			ServletInputStream	in	= request.getInputStream();
			Object	targetid	= readObject(in);
			boolean	sent	= false;
			if(!"benchmark".equals(targetid))
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
			else
			{
				try
				{
					byte[]	buf	= new byte[8192];  
					while(in.read(buf)!=-1)
					{
					}
					sent	= true;
				}
				catch(Exception e)
				{			
				}
			}
			
			if(!sent)
			{
				// Todo: other error msg?
				response.sendError(404);
			}
		}
		System.out.println("Leaving POST request. opencnt="+(--opencnt2)+", mapsize="+map.size());
	}
	
	//-------- helper methods --------
	
	/**
	 *  Send awareness messages about a new (add=true) or disconnected (add=false) platform.
	 */
	protected void	sendAwarenessInfos(Object id, boolean add)
	{
		byte[]	data	= JavaWriter.objectToByteArray(id, SRelay.class.getClassLoader());
		byte[]	info	= new byte[data.length+4];
		System.arraycopy(SUtil.intToBytes(data.length), 0, info, 0, 4);
		System.arraycopy(data, 0, info, 4, data.length);
		
		Map.Entry<Object, IBlockingQueue<Message>>[]	entries	= map.entrySet().toArray(new Map.Entry[0]);
		for(int i=0; i<entries.length; i++)
		{
			if(!id.equals(entries[i].getKey()))	// Don't send awareness to self
			{
				try
				{
					entries[i].getValue().enqueue(new Message(add ? SRelay.MSGTYPE_AWAADD : SRelay.MSGTYPE_AWAREMOVE ,new ByteArrayInputStream(info)));
				}
				catch(Exception e)
				{
					// Queue closed, because platform just disconnected.
				}
			}
		}
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
		return JavaReader.objectFromByteArray(buffer, SRelay.class.getClassLoader());
	}
	
	/**
	 *  Write an object to the given stream.
	 *  @param obj	The object.
	 *  @param out	The output stream.
	 *  @throws	IOException when the stream is closed.
	 */
	public static void	writeObject(Object obj, OutputStream out) throws IOException
	{
		byte[]	data	= JavaWriter.objectToByteArray(obj, SRelay.class.getClassLoader());
		out.write(SUtil.intToBytes(data.length));
		out.write(data);
		out.flush();		
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
