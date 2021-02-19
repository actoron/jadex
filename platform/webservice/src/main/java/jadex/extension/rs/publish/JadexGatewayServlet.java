package jadex.extension.rs.publish;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.ICommand;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;


/**
 *  Servlet implementation class WebStarter
 *  Parameter 'components' for startup components.
 */
public class JadexGatewayServlet extends HttpServlet
{
	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constant used to store the jadex platform in the application (servlet) context. */
	public static final String JADEX_PLATFORM = "jadex_platform";
	
	/** Constant used to store the jadex platform reference count in the application (servlet) context. */
	public static final String JADEX_PLATFORM_REFCOUNT = "jadex_platform_refcount";
	
	/** The Jadex platform. */
	protected IExternalAccess platform;

	/** The request handler. */
	protected IRequestHandlerService handler;
	
	/**
	 *  The servlet init.
	 */
	public void init(ServletConfig config) throws ServletException 
	{
	    super.init(config);
	    
	    this.platform = startPlatform();
	    ServletCallAccess.purgeServiceCalls();
	    
//		cms.createComponent(ExternalRSPublishAgent.class.getName()+".class", null).getFirstResult();
		this.handler = platform.searchService(new ServiceQuery<>(IRequestHandlerService.class, ServiceScope.PLATFORM)).get();

		// create components
		Enumeration<String> pnames = config.getInitParameterNames();
		Map<String, Map<String, Object>> comps = new HashMap<String, Map<String, Object>>();
		List<ICommand<IExternalAccess>> initcmds = new ArrayList<ICommand<IExternalAccess>>();
		
//		for(String cname=pnames.nextElement(); pnames.hasMoreElements(); cname=pnames.nextElement())
		while(pnames.hasMoreElements())
		{
			String cname = pnames.nextElement();
			
			if(cname.startsWith("component"))
			{
				int cnt = SUtil.countOccurrences(cname, '_');
				if(cnt==1)
				{
					Map<String, Object> comp = getComoponentMap(cname, comps);
					comp.put("__model", config.getInitParameter(cname));
				}
				else if(cnt==2)
				{
					int idx = cname.lastIndexOf("_");
					String pname = cname.substring(idx+1);
					String rcname = cname.substring(0, idx);
					
					Map<String, Object> comp = getComoponentMap(rcname, comps);
					
					String arg = config.getInitParameter(pname);
					if(arg!=null)
					{
						try
						{
							Object o = SJavaParser.evaluateExpression(arg, null);
							comp.put(pname, o);
						}
						catch(Exception e)
						{
							ServletCallAccess.purgeServiceCalls();
							throw new RuntimeException("Arguments evaluation error: "+e);
						}
					}
				}
			}
			
			if(cname.startsWith("initcommand"))
			{
				try
				{
					Class<?> cmdcl = SReflect.classForName(config.getInitParameter(cname), null);
					@SuppressWarnings("unchecked")
					ICommand<IExternalAccess> cmd = (ICommand<IExternalAccess>)cmdcl.newInstance();
					initcmds.add(cmd);
				}
				catch(Exception e)
				{
					ServletCallAccess.purgeServiceCalls();
					throw new RuntimeException("Initcommand error: "+config.getInitParameter(cname));
				}
			}
		}
		
		System.out.println("Found components: "+comps);
		
		for(Map.Entry<String, Map<String, Object>> entry: comps.entrySet())
		{
			String model = (String)entry.getValue().remove("__model");
			CreationInfo cinfo = new CreationInfo(entry.getValue()).setFilename(model);
			platform.createComponent(cinfo).get();
		}
		
		System.out.println("Found init commands: "+initcmds);
		
		for(ICommand<IExternalAccess> cmd: initcmds)
		{
			cmd.execute(platform);
		}
		
		ServletCallAccess.purgeServiceCalls();
	}
	
	/**
	 *  Start the platform.
	 *  @return The platform.
	 */
	public IExternalAccess startPlatform()
	{
		ServletContext ctx = getServletContext();
		IExternalAccess ret = null;
		
		synchronized(ctx)
		{
			ret = (IExternalAccess) ctx.getAttribute(JADEX_PLATFORM);
			
			if(ret==null)
			{
				IPlatformConfiguration pc = PlatformConfigurationHandler.getDefault();
			    pc.setGui(false);
			    pc.getExtendedPlatformConfiguration().setRsPublish(true);
			
			    ret = Starter.createPlatform(pc).get();
			    
			    ctx.setAttribute(JADEX_PLATFORM, ret);
			}
			
			Integer refcount = (Integer) ctx.getAttribute(JADEX_PLATFORM_REFCOUNT);
			if (refcount == null)
				refcount = new Integer(1);
			else
				refcount = new Integer(refcount.intValue() + 1);
			ctx.setAttribute(JADEX_PLATFORM_REFCOUNT, refcount);
		}
		
		return ret;
	}
	
	/**
	 *  Get the component map.
	 *  @param cname The component name.
	 *  @param comps The map of components.
	 *  @return The component map.
	 */
	protected Map<String, Object> getComoponentMap(String cname, Map<String, Map<String, Object>> comps)
	{
		Map<String, Object> comp = comps.get(cname);
		if(comp==null)
		{
			comp = new HashMap<String, Object>();
			comps.put(cname, comp);
		}
		return comp;
	}
	
	/**
	 *  Handle get requests.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
//		response.getWriter().append("Served at: ").append(request.getContextPath());
	
		
//		handler.handleRequest(request, response, null).addResultListener(new IResultListener<Void>()
//		{
//			public void exceptionOccurred(Exception e)
//			{
//				throw SUtil.throwUnchecked(e);
//			}
//			
//			public void resultAvailable(Void result)
//			{
//			}
//		});
		
		// Async must be set here because otherwise the result page is immediately delivered
		// without waiting for the async processing
		
		try
		{
//			System.out.println("received request: "+request.getRequestURL().toString());
			
			// Some idiot servers (read: the Eclipse one - surprise)
			// destroy the context path in async mode
			// which is apparently allowed by spec, so we save it here.
			// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=433321
			final String ctxtpath = request.getContextPath();
			request = new HttpServletRequestWrapper(request)
			{
				public String getContextPath()
				{
					return ctxtpath;
				}
			};
			
			final AsyncContext ctx = request.startAsync();
			final boolean[] complete = new boolean[1];
			AsyncListener alis = new AsyncListener()
			{
				public void onTimeout(AsyncEvent arg0) throws IOException
				{
				}
				
				public void onStartAsync(AsyncEvent arg0) throws IOException
				{
				}
				
				public void onError(AsyncEvent arg0) throws IOException
				{
				}
				
				public void onComplete(AsyncEvent arg0) throws IOException
				{
					complete[0] = true;
				}
			};
			ctx.addListener(alis);
			
			// Must be async because Jadex runs on other thread
			// tomcat async bug? http://jira.icesoft.org/browse/PUSH-116
			request.setAttribute(IAsyncContextInfo.ASYNC_CONTEXT_INFO, new IAsyncContextInfo()
			{
				public boolean isComplete()
				{
					return complete[0];
				}
			});
			
			handler.handleRequest(request, response, null).addResultListener(new IResultListener<Void>()
			{
				public void exceptionOccurred(Exception e)
				{
					if(!complete[0])
						ctx.complete();
					throw SUtil.throwUnchecked(e);
				}
				
				public void resultAvailable(Void result)
				{
					// done internally
	//				ctx.complete();
				}
			});
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
		ServletCallAccess.purgeServiceCalls();
//		System.out.println("resp is orig: "+response.hashCode());
//		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 *  Handle post requests.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}
	
	/**
	 *  Cleanup
	 */
	public void destroy()
	{
		System.out.println("Executing Servlet destroy.");
		super.destroy();
		
		ServletContext ctx = getServletContext();
		synchronized(ctx)
		{
			Integer refcount = (Integer) ctx.getAttribute(JADEX_PLATFORM_REFCOUNT);
			
			if (refcount == null || refcount.intValue() - 1 == 0)
			{
				if (platform != null)
				{
					try
					{
						platform.killComponent().get();
					}
					catch (Exception e)
					{
					}
				}
				ctx.removeAttribute(JADEX_PLATFORM);
				ctx.removeAttribute(JADEX_PLATFORM_REFCOUNT);
			}
			else
			{
				refcount = new Integer(refcount.intValue() - 1);
				ctx.setAttribute(JADEX_PLATFORM_REFCOUNT, refcount);
			}
		}
		ServletCallAccess.purgeServiceCalls();
	}
	
	/**
	 *  Purge service call ThreadLocal objects,
	 *  to avoid container leaks.
	 */
//	protected void purgeServiceCalls()
//	{
//		
//		try
//		{
//			Field cf = ServiceCall.class.getDeclaredField("CURRENT");
//			Field nf = ServiceCall.class.getDeclaredField("NEXT");
//			Field lf = ServiceCall.class.getDeclaredField("LAST");
//			cf.setAccessible(true);
//			nf.setAccessible(true);
//			lf.setAccessible(true);
//			
//			((ThreadLocal<?>) cf.get(null)).remove();
//			((ThreadLocal<?>) nf.get(null)).remove();
//			((ThreadLocal<?>) lf.get(null)).remove();
//		}
//		catch (Exception e)
//		{
//		}
//	}
	
	protected static class ServletCallAccess extends CallAccess
	{
		public static void purgeServiceCalls()
		{
			ServiceCall.LAST.remove();
			ServiceCall.CURRENT.remove();
			ServiceCall.NEXT.remove();
		}
	}
}

