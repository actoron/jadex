package jadex.extension.rs.publish;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;


/**
 *  Servlet implementation class WebStarter
 *  Parameter 'components' for startup components.
 */
public class JadexGatewayServlet extends HttpServlet
{
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
	    
	    PlatformConfiguration pc = PlatformConfiguration.getDefault();
	    pc.getRootConfig().setGui(false);
//	    pc.getRootConfig().setLogging(true);
	    pc.getRootConfig().setRsPublish(true);	
	    this.platform = Starter.createPlatform(pc).get();
	    
		IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
//		cms.createComponent(ExternalRSPublishAgent.class.getName()+".class", null).getFirstResult();
		this.handler = SServiceProvider.getService(platform, IRequestHandlerService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();

		// create components
		Enumeration<String> pnames = config.getInitParameterNames();
		Map<String, Map<String, Object>> comps = new HashMap<String, Map<String, Object>>();
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
							throw new RuntimeException("Arguments evaluation error: "+e);
						}
					}
				}
			}
		}
		
		System.out.println("Found components: "+comps);
		
		for(Map.Entry<String, Map<String, Object>> entry: comps.entrySet())
		{
			String model = (String)entry.getValue().remove("__model");
			CreationInfo cinfo = new CreationInfo(entry.getValue());
			cms.createComponent(model, cinfo).getFirstResult();
		}
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
			System.out.println("received request: "+request.getRequestURL().toString());
			
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
			e.printStackTrace();
		}
		
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
}

