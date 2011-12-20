package jadex.web.tools.jadexdoc;

import jadex.base.SComponentFactory;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.ThreadSuspendable;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

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
	protected IFuture<IExternalAccess>	platform;
	
	/** The models. */
	protected IIntermediateFuture<IModelInfo>	models;
	
	//-------- constructors --------
	
	/**
	 *  Init the servlet by starting the Jadex platform.
	 */
	public void init() throws ServletException
	{
		String[]	args	= new String[]
		{
//			"-logging_level", "java.util.logging.Level.INFO",
			"-awareness", "false",
			"-gui", "false",
			"-extensions", "null",
			"-welcome", "false"
		};
		platform	= Starter.createPlatform(args);
//		models	= loadModels();
	}
	
	/**
	 *  Shut down the platform on exit.
	 */
	public void destroy()
	{
		int	timeout	= 30000;
		ThreadSuspendable	sus	= new ThreadSuspendable();
		platform.get(sus, timeout).killComponent().get(sus, timeout);
	}
	
	//-------- methods --------
	
	/**
	 *  Called on each web request.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String	view;
		String	title;
		boolean	jaxcent	= false;
		String	file	= request.getParameter("model");
		
		try
		{
			IFuture<IModelInfo>	model	= loadModel(file);
			request.setAttribute("model", model);
			
			// Async page.
			if("/view".equals(request.getPathInfo()))
			{
				jaxcent	= true;
				title	= "Loading.";
				view	= "/WEB-INF/jsp/jadexdoc/loading.jsp";
				request.getSession().setAttribute("model", model);
				request.getSession().setAttribute("url", getContentUrl(request));
				request.getSession().setAttribute("file", file);
			}
			
			// Model contents page (blocking, but model already loaded).
			else if("/contents".equals(request.getPathInfo()))
			{
				jaxcent	= true;
				long	timeout	= 30000;
				ThreadSuspendable	sus	= new ThreadSuspendable();
				title	= model.get(sus, timeout).getFullName();
				view	= "/WEB-INF/jsp/jadexdoc/modelcontents.jsp";
			}
			
			// Blocking page.
			else
			{
				long	timeout	= 30000;
				ThreadSuspendable	sus	= new ThreadSuspendable();
				if(model.get(sus, timeout)!=null)
				{
					title	= model.get(sus, timeout).getFullName();
					view	= "/WEB-INF/jsp/jadexdoc/model.jsp";
				}
				else
				{
					title	= "File not found.";
					view	= "/WEB-INF/jsp/jadexdoc/notfound.jsp";
				}
			}
		}
		catch(Exception e)
		{
			request.setAttribute("exception", e);
			title	= "File could not be loaded.";
			view	= "/WEB-INF/jsp/jadexdoc/exception.jsp";				
		}
		
		request.setAttribute("title", title);
		request.setAttribute("file", file);
		request.setAttribute("jaxcent", jaxcent ? Boolean.TRUE : Boolean.FALSE);
		RequestDispatcher	rd	= getServletContext().getRequestDispatcher(view);
		rd.forward(request, response);
	}
	
	//-------- helper methods --------
	
	/**
	 *  Asynchronously load a model.
	 */
	public IFuture<IModelInfo>	loadModel(final String file)
	{
		final Future<IModelInfo>	ret	= new Future<IModelInfo>();
		platform.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IModelInfo>(ret)
		{
			public void customResultAvailable(IExternalAccess ea)
			{
				SComponentFactory.loadModel(ea, file, null)
					.addResultListener(new DelegationResultListener<IModelInfo>(ret)
				{
					public void customResultAvailable(IModelInfo result)
					{
						super.customResultAvailable(result);
					}
				});
			}
		});
		return ret;
	}
	
//	/**
//	 *  Asynchronously load all models.
//	 */
//	public IIntermediateFuture<IModelInfo>	loadModels()
//	{
//		final IntermediateFuture<IModelInfo>	ret	= new IntermediateFuture<IModelInfo>();
//		platform.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Collection<IModelInfo>>(ret)
//		{
//			public void customResultAvailable(IExternalAccess ea)
//			{
//				SServiceProvider.getService(ea.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//					.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Collection<IModelInfo>>(ret)
//				{
//					public void customResultAvailable(ILibraryService ls)
//					{
//						ls.getAllURLs().addResultListener(new ExceptionDelegationResultListener<List<URL>, Collection<IModelInfo>>(ret)
//						{
//							public void customResultAvailable(List<URL> urls)
//							{
//								System.out.println(urls);
//								ret.setFinished();
//							}
//						});
//					}
//				});
//			}
//		});
//		return ret;
//		
//	}
	
	/**
	 *  Get model content request.
	 */
	public static String getContentUrl(HttpServletRequest req)
	{
	    return req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()
	    	+req.getContextPath()+req.getServletPath()+"/contents?"+req.getQueryString();
	}
}
