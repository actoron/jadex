package jadex.web.examples.helloworld;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.ThreadSuspendable;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *  Servlet implementation class JadexDispatcherServlet
 */
public class JadexDispatcherServlet extends HttpServlet
{
	//-------- attributes --------
	
	/** The platform. */
	protected IExternalAccess	platform;
	
	//-------- constructors --------
	
	/**
	 *  Init the servlet by starting the Jadex platform.
	 */
	public void init() throws ServletException
	{
		String[]	args	= new String[]
		{
			"-gui", "false",
			"-extensions", "null"
		};
		ThreadSuspendable	sus	= new ThreadSuspendable();
		this.platform	= Starter.createPlatform(args).get(sus, 30000);
	}
	
	//-------- methods --------
	
	/**
	 *  Called on each web request.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession	session	= request.getSession();
		session.setAttribute("platform", platform);
		RequestDispatcher	rd	= getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
		rd.forward(request, response);
	}
}
