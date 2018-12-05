package jadex.extension.rs.publish;

import java.io.Writer;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Rest publish service that works with an external web server.
 *  
 *  In case of an external web server the host:port/context part of requests
 *  are determined by the server.
 *  
 *  // todo: should store the published servers using hostname and port
 *  // currently can get confused if mixed published ids are used (with and without [])
 *  // leads to problems because different ports are used then for different handlers
 *  // and the default handler (at port 0) is not found for a request coming on another port such as 8080
 */
@Service
public class ExternalRestPublishService extends AbstractRestPublishService implements IRequestHandlerService
{
	// The default address is used to abstract from the concrete deployment address and context
	// If services are published using brackets [] in the publish id, the external rest
	// publish service will replace this first part using the default address
	
	/** The default host name. */
	public static final String DEFAULT_HOST = "DEFAULTHOST";
	
	/** The default port. */
	public static final int DEFAULT_PORT = 0;
	
	/** The default app name. */
	public static final String DEFAULT_APP = "DEFAULTAPP";
	
	/** The default hostportappcontext. */
	public static final String DEFAULT_COMPLETECONTEXT = "http://"+DEFAULT_HOST+":"+DEFAULT_PORT+"/"+DEFAULT_APP+"/";
	
	/** The servers per service id (for unpublishing). */
	protected Map<IServiceIdentifier, Tuple2<PathHandler, URI>> sidservers;
	
	/** The servers per port. */
	protected Map<Integer, PathHandler> portservers;
	
	/** Inited flag because impl is used for 2 services. */
	protected boolean inited;
	
	/**
     *  The service init.
     */
    @ServiceStart
    public IFuture<Void> init()
    {
    	if(!inited)
    	{
    		inited = true;
    		super.init();
    	
    		IProvidedServicesFeature psf = component.getFeature(IProvidedServicesFeature.class);
    		return psf.addService("requesthandlerser", IRequestHandlerService.class, this);
    	}
    	else
    	{
    		return IFuture.DONE;
    	}
    }
	
	/**
	 *  Handle the request.
	 *  @param request The request.
	 *  @param response The response.
	 *  @param args Container specific args.
	 */
	public IFuture<Void> handleRequest(HttpServletRequest request, HttpServletResponse response, Object args)
	{
//		System.out.println("service received: "+request.getRequestURL().toString()+" "+request.getParameterMap());
		
		final Future<Void> ret = new Future<Void>();
		
		String err = null;
		if(portservers!=null)
		{
			PathHandler ph = portservers.get(Integer.valueOf(request.getLocalPort()));

			// If tolerant mode (todo) use default server (one might not know the hostname port before deployment)
			if(ph==null)
				ph = portservers.get(0);
			
			if(ph!=null)
			{
				try
				{
					ph.handleRequest(request, response, args);
				}
				catch(Exception e)
				{
					err = getServicesInfo(request, ph);
				}
			}
			else
			{
				err = "No service registered to handle the request.";
			}
		}
		else
		{
			err = "No server at port: "+request.getLocalPort();
		}
		
		if(err!=null)
		{
//			System.out.println("resp is: "+response.hashCode());
			
			// Set response content type
	        response.setContentType("text/html");

	        // Actual logic goes here.
	        try
	        {
		        Writer out = response.getWriter();
		        out.write("<html><head></head><body>"+err+"</body></html>");
//		        out.flush();
		        
		        complete(request, response);
//		         hack? todo: where to handle this complete?
//		        if(request.isAsyncStarted())
//		        	request.getAsyncContext().complete();
		        
		        ret.setResult(null);
	        }
	        catch(Exception e)
	        {
	        	ret.setException(e);
	        }
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Test if publishing a specific type is supported (e.g. web service).
	 *  @param publishtype The type to test.
	 *  @return True, if can be published.
	 */
	public IFuture<Boolean> isSupported(String publishtype)
	{
	    return IPublishService.PUBLISH_RS.equals(publishtype) ? IFuture.TRUE : IFuture.FALSE;
	}
	
	/**
	 *  Publish a service.
	 *  @param cl The classloader.
	 *  @param service The original service.
	 *  @param pid The publish id (e.g. url or name).
	 */
	public IFuture<Void> publishService(final IServiceIdentifier serviceid, final PublishInfo info)
	{
	    try
	    {
//	    	final IService service = (IService) component.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( serviceid)).get();
	    	
	    	String infopid = info.getPublishId();
	    	if(infopid.endsWith("/"))
	    		infopid = infopid.substring(0, infopid.length()-1);
	    	
	    	// If tolerant url notation cut off first part till real publish part
	    	URI uri = new URI(infopid.replace("[", "").replace("]", ""));
	    	
	    	String pid = infopid;
	    	if(pid.startsWith("["))
	    	{
	    		pid = pid.substring(pid.indexOf("]")+1);
	    		uri = new URI(DEFAULT_COMPLETECONTEXT+pid);
//	    		uri = new URI("http://DEFAULTHOST:0/DEFAULTAPP/"+pid);
	    	}
	    	
	        component.getLogger().info("Adding http handler to server: "+uri.getPath());
	        
	        // is overridden by nano to return nano server :-( cast then does not work
//	        PathHandler ph = (PathHandler)getHttpServer(uri, info);
	        getHttpServer(uri, info);
	        PathHandler ph = portservers.get(uri.getPort());
	        
	        final MultiCollection<String, MappingInfo> mappings = evaluateMapping(serviceid, info);
	
	        IRequestHandler rh = new IRequestHandler()
			{
	        	protected IService service = null;
	        	
				public void handleRequest(HttpServletRequest request, HttpServletResponse response, Object args) throws Exception
				{
					if(service == null)
						service = (IService) component.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>((Class<IService>)null).setServiceIdentifier(serviceid)).get();
					ExternalRestPublishService.this.handleRequest(service, mappings, request, response, null);
				}
			};
			if(ph.containsSubhandlerForExactUri(null, uri.getPath()))
			{
//				System.out.println("The URL "+uri.getPath() + " is already published, unpublishing...");
				component.getLogger().info("The URL "+uri.getPath() + " is already published, unpublishing...");
				ph.removeSubhandler(null, uri.getPath());
			}
			ph.addSubhandler(null, uri.getPath(), rh);
	        
	        if(sidservers==null)
	            sidservers = new HashMap<IServiceIdentifier, Tuple2<PathHandler, URI>>();
	        sidservers.put(serviceid, new Tuple2<PathHandler, URI>(ph, uri));
	    }
	    catch(Exception e)
	    {
	        throw new RuntimeException(e);
	    }
	    
	    return IFuture.DONE;
	}
	
	/**
	 *  Get or start an api to the http server.
	 */
	public Object getHttpServer(URI uri, PublishInfo info)
	{
		IRequestHandler server = null;

        try
        {
//	        URI baseuri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
            server = portservers==null? null: portservers.get(uri.getPort());

            if(server==null)
            {
                System.out.println("Starting new server: "+uri.getPort());
                PathHandler ph = new PathHandler();

                if(portservers==null)
                    portservers = new HashMap<Integer, PathHandler>();
                portservers.put(uri.getPort(), ph);
                server = ph;
            }
        }
        catch(RuntimeException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }

        return server;
	}
	
	/**
	 *  Unpublish a service.
	 *  @param sid The service identifier.
	 */
	public IFuture<Void> unpublishService(IServiceIdentifier sid)
	{
		Tuple2<PathHandler, URI> tup = sidservers.get(sid);
		if(tup!=null)
		{
			tup.getFirstEntity().removeSubhandler(null, tup.getSecondEntity().getPath());
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Publish a static page (without ressources).
	 */
	public IFuture<Void> publishHMTLPage(String pid, String vhost, final String html)
	{
		try
	    {
			URI uri = null;
	    	if(pid.startsWith("["))
	    	{
	    		pid = pid.substring(pid.indexOf("]")+1);
	    		uri = new URI(DEFAULT_COMPLETECONTEXT+pid);
//		    		uri = new URI("http://DEFAULTHOST:0/DEFAULTAPP/"+pid);
	    	}
	    	else
	    	{
	    		uri = new URI(pid);
	    	}
	    	
	        component.getLogger().info("Adding http handler to server: "+uri.getPath());
	        
	        PathHandler ph = (PathHandler)getHttpServer(uri, null);
	        
	        IRequestHandler rh = new IRequestHandler()
			{
				public void handleRequest(HttpServletRequest request, HttpServletResponse response, Object args) throws Exception
				{
					response.getWriter().write(html);
				}
			};
			if(ph.containsSubhandlerForExactUri(null, uri.getPath()))
			{
				component.getLogger().info("The URL "+uri.getPath() + " is already published, unpublishing...");
				ph.removeSubhandler(null, uri.getPath());
			}
			ph.addSubhandler(null, uri.getPath(), rh);
	    }
	    catch(Exception e)
	    {
	        throw new RuntimeException(e);
	    }
	    
	    return IFuture.DONE;
	}
	
	/**
	 *  Publish file resources from the classpath.
	 */
	public IFuture<Void> publishResources(String uri, String rootpath)
	{
	    throw new UnsupportedOperationException();
	}
	
	/**
	 *  Publish file resources from the file system.
	 */
	public IFuture<Void> publishExternal(URI uri, String rootpath)
	{
	    throw new UnsupportedOperationException();
	}

	public IFuture<Void> publishRedirect(URI uri, String html)
	{
	    throw new UnsupportedOperationException();
	}
	
	public IFuture<Void> unpublish(String vhost, URI uri)
	{
	    throw new UnsupportedOperationException();
	}
	
	public IFuture<Void> mirrorHttpServer(URI sourceserveruri, URI targetserveruri, PublishInfo info)
	{
	    throw new UnsupportedOperationException();
	}
	
	public IFuture<Void> shutdownHttpServer(URI uri)
	{
	    throw new UnsupportedOperationException();
	}
	
	/**
	 *  Produce overview site of published services.
	 */
	public String getServicesInfo(HttpServletRequest request, PathHandler ph)
	{
		StringBuffer ret = new StringBuffer();
		
		try
		{
			String functionsjs = loadFunctionJS();
			String stylecss = loadStyleCSS();
			
			ret.append("<html>");
			ret.append("\n");
			ret.append("<head>");
			ret.append("\n");
			ret.append(stylecss);
			ret.append("\n");
			ret.append(functionsjs);
			ret.append("\n");
	//		ret.append("<script src=\"functions.js\" type=\"text/javascript\"/>");
			ret.append("</head>");
			ret.append("\n");
			ret.append("<body>");
			ret.append("\n");
			
			ret.append("<div class=\"header\">");
			ret.append("\n");
			ret.append("<h1>");//Service Info for: ");
			ret.append("Published Services Info");
			ret.append("</h1>");
			ret.append("\n");
			ret.append("</div>");
			ret.append("\n");

			ret.append("<div class=\"middle\">");
			ret.append("\n");
			
			Map<Tuple2<String, String>, Tuple2<String, IRequestHandler>> subhandlers = ph.getSubhandlers();
			for(Tuple2<String, String> key: subhandlers.keySet())
			{
				ret.append("<div class=\"method\">");
				String path = key.getSecondEntity();
				if(path.startsWith("/"+DEFAULT_APP))
					path = path.replaceFirst("/"+DEFAULT_APP, request.getContextPath());
				String url = getServletHost(request) + path;
				ret.append("Host: ").append(key.getFirstEntity()!=null? key.getFirstEntity(): "-").append(" Path: ").append(path).append("<br/>");
				ret.append("<a href=\"").append(url).append("\">").append(url).append("</a>");
				ret.append("</div>");
			}
			
			ret.append("</div>");
			ret.append("\n");
			
			ret.append("<div id=\"result\"></div>");
			
			ret.append("<div class=\"powered\"> <span class=\"powered\">powered by</span> <span class=\"jadex\">Jadex Active Components</span> <a class=\"jadexurl\" href=\"http://www.activecomponents.org\">http://www.activecomponents.org</a> </div>\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		ret.append("</body>\n</html>\n");

		return ret.toString();
	}
}
