package jadex.extension.rs.publish;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.IFuture;

/**
 *  Publish service without Jersey directly using Jetty container.
 *  
 *  todo: make abstract base class without Jetty deps
 */
@Service
public class JettyRestPublishService extends AbstractRestPublishService
{
	// Hack constant for enabling multi-part :-(
	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

    /** The servers per service id. */
    protected Map<IServiceIdentifier, Server> sidservers;

    /** The servers per port. */
    protected Map<Integer, Server> portservers;
    
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
            Server server = (Server)getHttpServer(uri, info);
            System.out.println("Adding http handler to server: "+uri.getPath());

            ContextHandlerCollection collhandler = (ContextHandlerCollection)server.getHandler();

            final MultiCollection<String, MappingInfo> mappings = evaluateMapping(service.getServiceIdentifier(), info);

            ContextHandler ch = new ContextHandler()
            {
                public void doHandle(String target, Request baseRequest, final HttpServletRequest request, final HttpServletResponse response)
                    throws IOException, ServletException
                {
                    // Hack to enable multi-part
                    // http://dev.eclipse.org/mhonarc/lists/jetty-users/msg03294.html
                    if(request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) 
                    	baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
                	
                	handleRequest(service, uri, mappings, request, response, new Object[]{target, baseRequest});
                	
//                  System.out.println("handler is: "+uri.getPath());
                    baseRequest.setHandled(true);
                }
            };
            ch.setContextPath(uri.getPath());
            collhandler.addHandler(ch);
            ch.start(); // must be started explicitly :-(((

            if(sidservers==null)
                sidservers = new HashMap<IServiceIdentifier, Server>();
            sidservers.put(service.getServiceIdentifier(), server);
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
        Server server = null;

        try
        {
//            URI baseuri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
            server = portservers==null? null: portservers.get(uri.getPort());

            if(server==null)
            {
                System.out.println("Starting new server: "+uri.getPort());
                server = new Server(uri.getPort());
                server.dumpStdErr();

                ContextHandlerCollection collhandler = new ContextHandlerCollection();
                server.setHandler(collhandler);

                server.start();
//                server.join();

                if(portservers==null)
                    portservers = new HashMap<Integer, Server>();
                portservers.put(uri.getPort(), server);
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
        throw new UnsupportedOperationException();
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
}

