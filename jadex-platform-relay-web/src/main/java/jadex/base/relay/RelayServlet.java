package jadex.base.relay;

import jadex.commons.Tuple2;
import jadex.commons.collection.ArrayBlockingQueue;
import jadex.commons.collection.IBlockingQueue;
import jadex.commons.collection.IBlockingQueue.TimeoutException;
import jadex.commons.future.Future;
import jadex.commons.future.ThreadSuspendable;
import jadex.xml.bean.JavaReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	protected Map<Object, IBlockingQueue<Tuple2<InputStream, Future<Void>>>>	map;
	
	/** Counter for open GET connections (for testing). */
	protected int	opencnt1	= 0;
	/** Counter for open POST connections (for testing). */
	protected int	opencnt2	= 0;
	
	//-------- constructors --------

	/**
	 *  Initialize the servlet.
	 */
	public void init() throws ServletException
	{
		map	= Collections.synchronizedMap(new HashMap<Object, IBlockingQueue<Tuple2<InputStream, Future<Void>>>>());
	}
	
	/**
	 *  Cleanup on sevlet shutdown.
	 */
	public void destroy()
	{
		if(map!=null && !map.isEmpty())
		{
			for(Iterator<IBlockingQueue<Tuple2<InputStream, Future<Void>>>> it=map.values().iterator(); it.hasNext(); )
			{
				IBlockingQueue<Tuple2<InputStream, Future<Void>>>	queue	= it.next();
				it.remove();
				List<Tuple2<InputStream, Future<Void>>>	items	= queue.setClosed(true);
				for(int i=0; i<items.size(); i++)
				{
					items.get(i).getSecondEntity().setException(new RuntimeException("Target disconnected."));
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
		System.out.println("Entering GET request. opencnt="+(++opencnt1)+", mapsize="+map.size());
		
		String	idstring	= request.getParameter("id");
		Object	id	= JavaReader.objectFromXML(idstring, getClass().getClassLoader());
		IBlockingQueue<Tuple2<InputStream, Future<Void>>>	queue	= map.get(id);
		List<Tuple2<InputStream, Future<Void>>>	items	= null;
		if(queue!=null)
		{
			// Close old queue to free old servlet request
			items	= queue.setClosed(true);
			queue	= 	new ArrayBlockingQueue<Tuple2<InputStream, Future<Void>>>();
			// Add outstanding requests to new queue.
			for(int i=0; i<items.size(); i++)
			{
				queue.enqueue(items.get(i));
			}
		}
		else
		{
			queue	= 	new ArrayBlockingQueue<Tuple2<InputStream, Future<Void>>>();
		}
		map.put(id, queue);
//		System.out.println("Added to map. New size: "+map.size());
		try
		{
			while(true)
			{
				try
				{
					// Get next request from queue.
					Tuple2<InputStream, Future<Void>>	msg	= queue.dequeue(30000);	// Todo: make ping delay configurable on per client basis
					try
					{
						// Send message header.
						response.getOutputStream().write(SRelay.MSGTYPE_DEFAULT);
						
						// Copy message to output stream.
						byte[]	buf	= new byte[8192];  
						int	len;  
						while((len=msg.getFirstEntity().read(buf)) != -1)
						{
							response.getOutputStream().write(buf, 0, len);  
						}
						response.getOutputStream().flush();
						msg.getSecondEntity().setResult(null);
					}
					catch(Exception e)
					{
						msg.getSecondEntity().setException(e);
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
	//		System.out.println("Removed from map ("+items.size()+" remaining items). New size: "+map.size());
			for(int i=0; i<items.size(); i++)
			{
				items.get(i).getSecondEntity().setException(new RuntimeException("Target disconnected."));
			}
		}
		System.out.println("Leaving GET request. opencnt="+(--opencnt1)+", mapsize="+map.size());
	}

	/**
	 *  Called when a message should be sent.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		System.out.println("Entering POST request. opencnt="+(++opencnt2)+", mapsize="+map.size());
		ServletInputStream	in	= request.getInputStream();
		Object	targetid	= SRelay.readObject(in);
		IBlockingQueue<Tuple2<InputStream, Future<Void>>>	queue	= map.get(targetid);
		boolean	sent	= false;
		if(queue!=null)
		{
			Future<Void>	fut	= new Future<Void>();
			try
			{
				queue.enqueue(new Tuple2<InputStream, Future<Void>>(in, fut));
				fut.get(new ThreadSuspendable(), 30000);	// todo: how to set a useful timeout value!?
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
		System.out.println("Leaving POST request. opencnt="+(--opencnt2)+", mapsize="+map.size());
	}
}
