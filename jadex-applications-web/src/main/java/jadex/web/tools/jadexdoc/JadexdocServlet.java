package jadex.web.tools.jadexdoc;

import jadex.base.SComponentFactory;
import jadex.base.Starter;
import jadex.base.gui.filetree.JarAsDirectory;
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
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.ThreadSuspendable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.Icon;

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
			"-welcome", "false",
			"-relaytransport", "false",
			"-niotransport", "false"
		};
		platform	= Starter.createPlatform(args);
		models	= loadModels();
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
		// Icon for component type (blocking)
		if("/icon".equals(request.getPathInfo()))
		{
			long	timeout	= 30000;
			ThreadSuspendable	sus	= new ThreadSuspendable();
			String	type	= request.getParameter("type");
			Icon	icon	= SComponentFactory.getFileTypeIcon(platform.get(sus, timeout), type).get(sus, timeout);
		    BufferedImage	img	= new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		    icon.paintIcon(null, img.getGraphics(), 0, 0);
		    response.setContentType("image/png");
		    ImageIO.write(img, "png", response.getOutputStream());
		}
		else
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
					request.getSession().setAttribute("models", models);
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
	
	/**
	 *  Asynchronously load all models.
	 */
	protected IIntermediateFuture<IModelInfo>	loadModels()
	{
		final IntermediateFuture<IModelInfo>	ret	= new IntermediateFuture<IModelInfo>();
		platform.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Collection<IModelInfo>>(ret)
		{
			public void customResultAvailable(final IExternalAccess ea)
			{
				SServiceProvider.getService(ea.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Collection<IModelInfo>>(ret)
				{
					public void customResultAvailable(ILibraryService ls)
					{
						ls.getAllURLs().addResultListener(new ExceptionDelegationResultListener<List<URL>, Collection<IModelInfo>>(ret)
						{
							public void customResultAvailable(List<URL> urls)
							{
								scanForModels(ea, new LinkedList<File>(), urls, ret, new HashSet<String>());
							}
						});
					}
				});
			}
		});
		return ret;
		
	}
	
	/**
	 *  Scan classpath URLs for loadable models.
	 */
	protected void	scanForModels(final IExternalAccess ea, final List<File> files,
		final List<URL> urls, final IntermediateFuture<IModelInfo> fut, final Set<String> done_urls)
	{
		if(files.isEmpty() && urls.isEmpty())
		{
			fut.setFinished();
		}
		else if(files.isEmpty())
		{
			URL	url	= urls.remove(0);
//			System.out.println("URL: "+url);
			try
			{
				String	abs	= new File(url.getFile()).getCanonicalPath();
				if(!done_urls.contains(abs))
				{
					if(url.getFile().endsWith(".jar"))
					{
						JarAsDirectory	jar	= new JarAsDirectory(url.getFile());
						jar.refresh();
						files.add(jar);
					}
					else
					{
						files.add(new File(url.getFile()));
					}
					done_urls.add(abs);
				}
				scanForModels(ea, files, urls, fut, done_urls);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			File	file	= files.remove(0);
			if(file.isDirectory())
			{
				files.addAll(Arrays.asList(file.listFiles()));
				scanForModels(ea, files, urls, fut, done_urls);
			}
			else
			{
				SComponentFactory.loadModel(ea, file.getAbsolutePath(), null)
					.addResultListener(new IResultListener<IModelInfo>()
				{
					public void resultAvailable(IModelInfo result)
					{
						if(result!=null)
						{
							fut.addIntermediateResult(result);
						}
						scanForModels(ea, files, urls, fut, done_urls);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						scanForModels(ea, files, urls, fut, done_urls);
					}
				});
			}
		}
	}
	
	/**
	 *  Get model content request.
	 */
	public static String getContentUrl(HttpServletRequest req)
	{
	    return req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()
	    	+req.getContextPath()+req.getServletPath()+"/contents?"+req.getQueryString();
	}
}
