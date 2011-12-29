package jadex.base.relay;

import jadex.commons.SUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  The benchmark servlet allows testing send and receive relay performance
 *  independently using dummy messages.
 */
public class BenchmarkServlet extends HttpServlet
{
	//-------- methods --------
	
	/**
	 *  Handle receivers.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
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

	/**
	 *  Handle senders.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			while(true)
			{
				InputStream	in	= request.getInputStream();
				byte[]	len	= SRelay.readData(in, 4);
				int	length	= SUtil.bytesToInt(len);
				SRelay.readData(in, length);
				
				// Send achnowledgement.
				response.getOutputStream().write(SRelay.MSGTYPE_PING);
				response.getOutputStream().flush();
			}
		}
		catch(Exception e)
		{			
//			response.sendError(404);
		}
	}
}
