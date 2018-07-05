package jadex.web.examples.hellobdiv3;

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
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;

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
//		ThreadSuspendable	sus	= new ThreadSuspendable();
		this.platform	= Starter.createPlatform(args).get(30000);
		IComponentManagementService cms = platform.searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get(30000);
		cms.createComponent("jadex.web.examples.hellobdiv3.SayHelloBDI.class", null).getFirstResult();
	}
	
	/**
	 *  Shut down the platform on exit.
	 */
	public void destroy()
	{
		int	timeout	= 30000;
		ThreadSuspendable	sus	= new ThreadSuspendable();
		platform.killComponent().get(timeout);
	}	
	
	//-------- methods --------
	
	/**
	 *  Called on each web request.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
//		ThreadSuspendable	sus	= new ThreadSuspendable();
		ISayHelloService shs = platform.searchService( new ServiceQuery<>( ISayHelloService.class)).get(30000);
		String text = shs.sayHello().get(30000);
		request.setAttribute("text", text);
		
		HttpSession	session	= request.getSession();
		session.setAttribute("platform", platform);
		RequestDispatcher	rd	= getServletContext().getRequestDispatcher("/WEB-INF/jsp/hellobdiv3/index.jsp");
		rd.forward(request, response);
	}
}
