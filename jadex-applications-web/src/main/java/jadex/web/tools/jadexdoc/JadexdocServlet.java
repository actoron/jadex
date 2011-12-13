package jadex.web.tools.jadexdoc;

import jadex.base.SComponentFactory;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.future.ThreadSuspendable;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  Front controller servlet for jadexdoc application.
 */
public class JadexdocServlet extends HttpServlet
{
	//-------- attributes --------
	
	/** The platform. */
	protected IExternalAccess	platform;
	
	//-------- constructors --------
	
	/**
	 *  Init the servlet by starting the Jadex platform
	 *  and fecthing the puzzle service.
	 */
	public void init() throws ServletException
	{
		String[]	args	= new String[]
		{
//			"-logging_level", "java.util.logging.Level.INFO",
			"-awareness", "false",
			"-gui", "false",
			"-extensions", "null"
		};
		int	timeout	= 30000;
		ThreadSuspendable	sus	= new ThreadSuspendable();
		platform	= Starter.createPlatform(args).get(sus, timeout);
	}
	
	/**
	 *  Shut down the platform on exit.
	 */
	public void destroy()
	{
		int	timeout	= 30000;
		ThreadSuspendable	sus	= new ThreadSuspendable();
		platform.killComponent().get(sus, timeout);
	}
	
	//-------- methods --------
	
	/**
	 *  Called on each web request.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String	view;
		
//		if(fut.isDone())
//		{
			try
			{
				int	timeout	= 30000;
				ThreadSuspendable	sus	= new ThreadSuspendable();
				IModelInfo	model	= SComponentFactory.loadModel(platform, request.getPathInfo(), null).get(sus, timeout);
				if(model!=null)
				{
					request.setAttribute("model", model);
					view	= "/WEB-INF/jsp/jadexdoc/model.jsp";
				}
				else
				{
					request.setAttribute("model", request.getPathInfo());
					view	= "/WEB-INF/jsp/jadexdoc/notfound.jsp";
				}
			}
			catch(Exception e)
			{
				request.setAttribute("exception", e);
				view	= "/WEB-INF/jsp/jadexdoc/exception.jsp";				
			}
//		}
//		else
//		{
//			view	= "/WEB-INF/jsp/jadexdoc/wait.jsp";
//		}
		
		RequestDispatcher	rd	= getServletContext().getRequestDispatcher(view);
		rd.forward(request, response);
	}
}
