package jadex.web.tools.jadexdoc;

import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jadex.base.JarAsDirectory;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.ThreadSuspendable;

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
		// Force AWT thread on system class loader instead of web app clas loader
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
		Toolkit.getDefaultToolkit();
		Thread.currentThread().setContextClassLoader(cl);

		String[]	args	= new String[]
		{
//			"-logging_level", "java.util.logging.Level.INFO",
			"-awareness", "false",
			"-gui", "false",
			"-extensions", "null",
			"-welcome", "false",
			"-relaytransport", "false",
			"-tcptransport", "false",
			"-rspublish", "false",
			"-wspublish", "false"
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
		platform.get(timeout).killComponent().get(timeout);
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
			byte[]	icon	= SComponentFactory.getFileTypeIcon(platform.get(timeout), type).get(timeout);
		    response.setContentType(URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(icon)));
		    response.getOutputStream().write(icon);
		}
		else
		{
			String	view;
			String	title;
			boolean	jaxcent	= false;
			String	file	= request.getParameter("model");
			
			try
			{
				IFuture<IModelInfo>	model	= file!=null ? loadModel(file) : null;
				request.setAttribute("model", model);
				request.setAttribute("models", models);
				
//				// Async page.
//				if("/view".equals(request.getPathInfo()))
//				{
//					jaxcent	= true;
//					title	= "Loading.";
//					view	= "/WEB-INF/jsp/jadexdoc/loading.jsp";
//					request.getSession().setAttribute("model", model);
//					request.getSession().setAttribute("models", models);
//					request.getSession().setAttribute("url", getContentUrl(request));
//					request.getSession().setAttribute("murl", getModelsUrl(request));
//					request.getSession().setAttribute("file", file);
//				}
//				
//				// Model contents page (blocking, but model already loaded).
//				else if("/contents".equals(request.getPathInfo()))
//				{
//					jaxcent	= true;
//					long	timeout	= 30000;
//					ThreadSuspendable	sus	= new ThreadSuspendable();
//					title	= model.get(sus, timeout).getFullName();
//					view	= "/WEB-INF/jsp/jadexdoc/modelcontents.jsp";
//				}
//				
//				// Model overview page (nonblocking, based on available results).
//				else if("/models".equals(request.getPathInfo()))
//				{
//					title	= "Overview";
//					view	= "/WEB-INF/jsp/jadexdoc/modellist.jsp";
//					request.setAttribute("models", models);
//				}
//				
//				// Blocking page.
//				else
				{
					long	timeout	= 30000;
					if(model!=null && model.get(timeout)!=null)
					{
						IModelInfo	mi	= model.get(timeout);
						title	= mi.getName() + " (" + mi.getType() + ")";
						view	= "/WEB-INF/jsp/jadexdoc/model.jsp";
					}
					else
					{
						request.setAttribute("model", null);
						title	= "File not found.";
						view	= "/WEB-INF/jsp/jadexdoc/notfound.jsp";
					}
				}
			}
			catch(Exception e)
			{
				request.setAttribute("model", null);
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
		if(file==null)
			throw new NullPointerException();
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
				ea.searchService( new ServiceQuery<>( ILibraryService.class, ServiceScope.PLATFORM))
					.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Collection<IModelInfo>>(ret)
				{
					public void customResultAvailable(ILibraryService ls)
					{
						ls.getAllURLs().addResultListener(new ExceptionDelegationResultListener<List<URL>, Collection<IModelInfo>>(ret)
						{
							public void customResultAvailable(List<URL> urls)
							{
								Set<File>	files	= scanForModels(urls);
								final CounterResultListener<Void>	crl	= new CounterResultListener<Void>(files.size(), new DefaultResultListener<Void>()
								{
									public void resultAvailable(Void result)
									{
										ret.setFinished();
									}
								});
								for(Iterator<File> it=files.iterator(); it.hasNext(); )
								{
									SComponentFactory.loadModel(ea, it.next().getAbsolutePath(), null)
										.addResultListener(new IResultListener<IModelInfo>()
									{
										public void resultAvailable(IModelInfo result)
										{
											if(result!=null)
											{
												ret.addIntermediateResult(result);
											}
											crl.resultAvailable(null);
										}
										
										public void exceptionOccurred(Exception exception)
										{
											crl.resultAvailable(null);
//											System.out.println("Exception: "+exception);
										}
									});
								}
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
	protected Set<File>	scanForModels(List<URL> urls)
	{
		Set<String> done_urls	=  new LinkedHashSet<String>();
		Set<File> dirs	=  new LinkedHashSet<File>();
		Set<File> files	=  new LinkedHashSet<File>();
		while(!dirs.isEmpty() || !urls.isEmpty())
		{
			if(!dirs.isEmpty())
			{
				Iterator<File>	it	= dirs.iterator();
				File	file	= it.next();
				it.remove();
				if(file.isDirectory())
				{
//					System.out.println("Directory: "+file);
					dirs.addAll(Arrays.asList(file.listFiles()));
				}
				else
				{
//					System.out.println("File: "+file);
					files.add(file);
				}
			}
			else if(!urls.isEmpty())
			{
				URL	url	= urls.remove(0);
//				System.out.println("URL: "+url);
				try
				{
					String	path	= url.toURI().getPath();
					String	abs	= new File(path).getCanonicalPath();
					if(!done_urls.contains(abs))
					{
						if(abs.endsWith(".jar"))
						{
							JarAsDirectory	jar	= new JarAsDirectory(abs);
							jar.refresh();
							dirs.add(jar);
						}
						else
						{
							dirs.add(new File(abs));
						}
						done_urls.add(abs);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return files;
	}
	
	/**
	 *  Get model content request.
	 */
	public static String getContentUrl(HttpServletRequest req)
	{
	    return req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()
	    	+req.getContextPath()+req.getServletPath()+"/contents?"+req.getQueryString();
	}
	
	/**
	 *  Get model list request.
	 */
	public static String getModelsUrl(HttpServletRequest req)
	{
	    return req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()
	    	+req.getContextPath()+req.getServletPath()+"/models";
	}
}
