package org.activecomponents.udp.holepunching.server.web;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activecomponents.udp.holepunching.server.IRegisteredHost;
import org.activecomponents.udp.holepunching.server.ServerConnection;
import org.activecomponents.udp.holepunching.server.commands.IServerCommand;
import org.activecomponents.udp.holepunching.server.webcommands.WebConnectedHost;

/**
 * Servlet implementation class HpServlet
 */
public class HpServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	

    /**
     * Default constructor. 
     */
    public HpServlet()
    {
    	System.out.println(Arrays.toString(ServerConnection.COMMAND_CLASSNAMES.toArray()));
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
//		response.getWriter().append("Attributes\n");
//		Enumeration<?> attrs = request.getAttributeNames();
//		while (attrs.hasMoreElements())
//		{
//			response.getWriter().append(attrs.nextElement().toString());
//			response.getWriter().append("\n");
//		}
//		response.getWriter().append("Parameters\n");
//		Enumeration<?> params = request.getParameterNames();
//		while (params.hasMoreElements())
//		{
//			String paramname = (String) params.nextElement();
//			response.getWriter().append(paramname);
//			response.getWriter().append("=");
//			response.getWriter().append(request.getParameter(paramname));
//			response.getWriter().append("\n");
//		}
		
		String cmd = request.getParameter("cmd");
//		System.out.println("CMD: " + cmd);
		
		if (cmd != null)
		{
			IServerCommand[] commands = ServerConnection.getCommands(ServerConnection.COMMAND_CLASSNAMES_WEB, getClass().getClassLoader());
			for (int i = 0; i < commands.length; ++i)
			{
				if (commands[i] != null && commands[i].isApplicable(cmd))
				{
					try
					{
						List<String> params = new ArrayList<String>();
						String param = null;
						int count = 0;
						do
						{
							param = request.getParameter("p" + count);
							if (param != null)
							{
								params.add(param);
							}
							++count;
						}
						while (param != null);
						//(Map<String, IRegisteredHost>) getServletContext().getAttribute("registeredhosts")
						//(DatagramSocket) getServletContext().getAttribute("dgsocket")
						@SuppressWarnings("unchecked")
						WebConnectedHost chost = new WebConnectedHost(request.getRemoteAddr(),
							(Map<String, IRegisteredHost>) getServletContext().getAttribute("registeredhosts"),
							(DatagramSocket) getServletContext().getAttribute("dgsocket"));
						
						try
						{
							response.getWriter().append(commands[i].execute(cmd, params.toArray(new String[params.size()]), chost));
							response.getWriter().flush();
							break;
						}
						catch(Exception e1)
						{
							e1.printStackTrace();
							response.getWriter().append("Request failed.");
							response.getWriter().flush();
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						response.getWriter().append("Request failed.");
						response.getWriter().flush();
					}
				}
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}
	
//	protected class ConnectedHost implements IConnectedHost
//	{
//		protected String remoteaddr;
//		
//		public ConnectedHost(String remoteaddr)
//		{
//			this.remoteaddr = remoteaddr;
//		}
//		
//		/**
//		 *  Retrieves the registered hosts.
//		 *  
//		 *  @return The registered hosts.
//		 */
//		@SuppressWarnings("unchecked")
//		public Map<String, IRegisteredHost> getRegisteredHosts()
//		{
//			Map<String, IRegisteredHost> ret = (Map<String, IRegisteredHost>) getServletContext().getAttribute("registeredhosts");
//			return ret;
//		}
//		
//		/**
//		 *  Gets the address of the connected host.
//		 *  @return The address.
//		 */
//		public InetAddress getRemoteAddress()
//		{
//			try
//			{
//				return InetAddress.getByName(remoteaddr);
//			}
//			catch (UnknownHostException e)
//			{
//				e.printStackTrace();
//			}
//			return null;
//		}
//		
//		/**
//		 *  Retrieves the UDP socket for testing communication.
//		 *  
//		 *  @return The UDP socket.
//		 */
//		public DatagramSocket getUdpSocket()
//		{
//			DatagramSocket ret = (DatagramSocket) getServletContext().getAttribute("dgsocket");
//			return ret;
//		}
//	}
}
