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
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Rest publish service that works with an external web server.
 */
@Service
public class ExternalRestPublishService extends AbstractRestPublishService implements IRequestHandlerService
{
	/** The servers per service id. */
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
    	
    		IProvidedServicesFeature psf = component.getComponentFeature(IProvidedServicesFeature.class);
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
		System.out.println("service received: "+request.getRequestURL().toString()+" "+request.getParameterMap());
		
		final Future<Void> ret = new Future<Void>();
		
		String err = null;
		if(portservers!=null)
		{
			PathHandler ph = portservers.get(Integer.valueOf(request.getLocalPort()));
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
		        out.flush();
		        
		        // hack? todo: where to handle this complete?
		        if(request.isAsyncStarted())
		        	request.getAsyncContext().complete();
		        
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
	public IFuture<Void> publishService(ClassLoader cl, final IService service, final PublishInfo info)
	{
	    try
	    {
	        final URI uri = new URI(info.getPublishId());
	        System.out.println("Adding http handler to server: "+uri.getPath());
	        PathHandler ph = (PathHandler)getHttpServer(uri, info);
	        
	        final MultiCollection<String, MappingInfo> mappings = evaluateMapping(service.getServiceIdentifier(), info);
	
	        IRequestHandler rh = new IRequestHandler()
			{
				public void handleRequest(HttpServletRequest request, HttpServletResponse response, Object args) throws Exception
				{
					ExternalRestPublishService.this.handleRequest(service, mappings, request, response, null);
				}
			};
			ph.addSubhandler(null, uri.getPath(), rh);
	        
	        if(sidservers==null)
	            sidservers = new HashMap<IServiceIdentifier, Tuple2<PathHandler, URI>>();
	        sidservers.put(service.getServiceIdentifier(), new Tuple2<PathHandler, URI>(ph, uri));
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
			tup.getFirstEntity().removeSubhandler(null, tup.getSecondEntity().toString());
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Publish a static page (without ressources).
	 */
	public IFuture<Void> publishHMTLPage(URI uri, String vhost, String html)
	{
	    throw new UnsupportedOperationException();
	}
	
	/**
	 *  Publish file resources from the classpath.
	 */
	public IFuture<Void> publishResources(URI uri, String rootpath)
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
				ret.append("Host: ").append(key.getFirstEntity()!=null? key.getFirstEntity(): "-").append(" Path: ").append(key.getSecondEntity()).append("<br/>");
				String url = getServletHost(request)+key.getSecondEntity();
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
