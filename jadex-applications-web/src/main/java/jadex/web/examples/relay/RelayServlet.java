package jadex.web.examples.relay;

import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.commons.Tuple2;
import jadex.commons.collection.ArrayBlockingQueue;
import jadex.commons.collection.IBlockingQueue;
import jadex.commons.future.Future;
import jadex.commons.future.ThreadSuspendable;
import jadex.xml.bean.JavaReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
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
	
	//-------- constructors --------

	/**
	 *  Initialize the servlet.
	 */
	public void init() throws ServletException
	{
		map	= Collections.synchronizedMap(new HashMap<Object, IBlockingQueue<Tuple2<InputStream, Future<Void>>>>());
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a platform registers itself at the relay. 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String	idstring	= request.getParameter("id");
		Object	id	= JavaReader.objectFromXML(idstring, getClass().getClassLoader());
		IBlockingQueue<Tuple2<InputStream, Future<Void>>>	queue	= new ArrayBlockingQueue<Tuple2<InputStream, Future<Void>>>();
		map.put(id, queue);
		try
		{
			while(true)
			{
				// Get next request from queue.
				Tuple2<InputStream, Future<Void>>	msg	= queue.dequeue();
				try
				{
					// Copy data to output stream.
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
		}
		catch(Exception e)
		{
			// exception on queue, when servlet is destroyed
			// exception on output stream, when client disconnects
		}
		queue.setClosed(true);
		map.remove(id);
	}

	/**
	 *  Called when a message should be sent.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
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
			response.sendError(404);
		}
	}
}
