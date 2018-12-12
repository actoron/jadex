package jadex.web.examples.helloworld;

import java.awt.Toolkit;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;

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
		// Force AWT thread on system class loader instead of web app clas loader
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
		Toolkit.getDefaultToolkit();
		Thread.currentThread().setContextClassLoader(cl);
		
		String[]	args	= new String[]
		{
			"-awareness", "false",
			"-gui", "false",
			"-extensions", "null",
			"-welcome", "false"
		};
		this.platform	= Starter.createPlatform(args).get(30000);
	}
	
	/**
	 *  Shut down the platform on exit.
	 */
	public void destroy()
	{
		int	timeout	= 30000;
		platform.killComponent().get(timeout);
	}	
	
	//-------- methods --------
	
	/**
	 *  Called on each web request.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession	session	= request.getSession();
		session.setAttribute("platform", platform);
		RequestDispatcher	rd	= getServletContext().getRequestDispatcher("/WEB-INF/jsp/helloworld/index.jsp");
		rd.forward(request, response);
	}
}
