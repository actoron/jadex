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
import org.eclipse.jetty.server.handler.ResourceHandler;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.rs.publish.AbstractRestPublishService.MappingInfo;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;

/**
 *  Publish service without Jersey directly using Jetty container.
 *  
 *  todo: make abstract base class without Jetty deps
 *  
 *  note: Jetty releases are Java 1.8 only.
 *  
 */
@Service
public class JettyRestPublishService extends AbstractRestPublishService
{
	// Jetty requires 1.8
//	static
//	{
//		String ver = System.getProperty("java.version");
//		
//		if(Float.parseFloat(ver.substring(0,3)) < 1.8f)
//		{
//			System.out.println("WARNING: Jetty requires Java 1.8");
//		}
//	}
	
	// Hack constant for enabling multi-part :-(
	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

    /** The servers per service id. */
    protected Map<IServiceIdentifier, Server> sidservers;

    /** The servers per port. */
    protected Map<Integer, Server> portservers;
    
    /** Infos for unpublishing. */
    protected Map<IServiceIdentifier, Tuple2<Server, ContextHandler>> unpublishinfos = new HashMap<IServiceIdentifier, Tuple2<Server,ContextHandler>>();
    
    @ServiceStart
    public void start()
    {
    	System.out.println("Jetty started");
    }
    
    @ServiceShutdown
    public void stop()
    {
    	if(portservers != null)
    	{
    		for (Map.Entry<Integer, Server> entry : portservers.entrySet())
    		{
    			try
				{
					entry.getValue().stop();
				}
				catch (Exception e)
				{
				}
    		}
    	}
    	System.out.println("Jetty stopped");
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
    	Future<Void> ret = new Future<>();
		
        IFuture<MultiCollection<String, MappingInfo>> fut = evaluateMapping(serviceid, info);
        
        fut.addResultListener(new ExceptionDelegationResultListener<MultiCollection<String, MappingInfo>, Void>(ret)
		{
        	@Override
        	public void customResultAvailable(MultiCollection<String, MappingInfo> mappings)
        	{
        		try
                {
                	//final IService service = (IService) component.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( serviceid)).get();
                	
                    final URI uri = new URI(getCleanPublishId(info.getPublishId()));
                    Server server = (Server)getHttpServer(uri, info);
                    System.out.println("Adding http handler to server (jetty): "+uri.getPath());

                    ContextHandlerCollection collhandler = (ContextHandlerCollection)server.getHandler();

                    ContextHandler ch = new ContextHandler()
                    {
                    	protected IService service = null;
                    	
                        public void doHandle(String target, Request baseRequest, final HttpServletRequest request, final HttpServletResponse response)
                            throws IOException, ServletException
                        {
                        	if(service==null)
                        		service = component.getExternalAccess().searchService( new ServiceQuery<>((Class<IService>)null).setServiceIdentifier(serviceid)).get();
                        	
                            // Hack to enable multi-part
                            // http://dev.eclipse.org/mhonarc/lists/jetty-users/msg03294.html
                            if(request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) 
                            	baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
                        	
                        	handleRequest(service, mappings, request, response, new Object[]{target, baseRequest});
                        	
//                          System.out.println("handler is: "+uri.getPath());
                            baseRequest.setHandled(true);
                        }
                    };
                    ch.setContextPath(uri.getPath());
                    collhandler.addHandler(ch);
                    unpublishinfos.put(serviceid, new Tuple2<Server,ContextHandler>(server, ch));
                    ch.start(); // must be started explicitly :-(((

                    if(sidservers==null)
                        sidservers = new HashMap<IServiceIdentifier, Server>();
                    sidservers.put(serviceid, server);
                    ret.setResult(null);
                }
                catch(Exception e)
                {
                	ret.setException(e);
                }
        	}
		});
        
        return ret;
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
//              server.join();

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
    	Tuple2<Server,ContextHandler> unpublish = unpublishinfos.remove(sid);
    	if (unpublish != null)
    		((ContextHandlerCollection)unpublish.getFirstEntity().getHandler()).removeHandler(unpublish.getSecondEntity());
//        throw new UnsupportedOperationException();
    	return IFuture.DONE;
    }

    /**
     *  Publish a static page (without ressources).
     */
    public IFuture<Void> publishHMTLPage(String pid, String vhost, final String html)
    {
    	try
        {
    		String clpid = pid.replace("[", "").replace("]", "");
    		URI uri = new URI(clpid);
        	//final IService service = (IService) component.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( serviceid)).get();
        	
            Server server = (Server)getHttpServer(uri, null);
            System.out.println("Adding http handler to server (jetty): "+uri.getPath());

            ContextHandlerCollection collhandler = (ContextHandlerCollection)server.getHandler();

            ContextHandler ch = new ContextHandler()
            {
                public void doHandle(String target, Request baseRequest, final HttpServletRequest request, final HttpServletResponse response)
                    throws IOException, ServletException
                {
                	response.getWriter().write(html);
                	
//                  System.out.println("handler is: "+uri.getPath());
                    baseRequest.setHandled(true);
                }
            };
            ch.setContextPath(uri.getPath());
            collhandler.addHandler(ch);
            ch.start(); // must be started explicitly :-(((
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
    public IFuture<Void> publishResources(final String pid, final String rootpath)
    {
		final Future<Void>	ret	= new Future<Void>();
		try
		{
//		IComponentIdentifier	cid	= ServiceCall.getLastInvocation()!=null && ServiceCall.getLastInvocation().getCaller()!=null ? ServiceCall.getLastInvocation().getCaller() : component.getId();
//		component.getDescription(cid)
//			.addResultListener(new ExceptionDelegationResultListener<IComponentDescription, Void>(ret)
//		{
//			public void customResultAvailable(IComponentDescription desc)
//			{
//				ILibraryService	ls	= component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( ILibraryService.class, ServiceScope.PLATFORM));
//				ls.getClassLoader(desc.getResourceIdentifier())
//					.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
//				{
//					public void customResultAvailable(ClassLoader cl1) throws Exception 
//					{
			    		String clpid = pid.replace("[", "").replace("]", "");
			    		URI uri = new URI(clpid);
			        	//final IService service = (IService) component.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( serviceid)).get();
			        	
			            Server server = (Server)getHttpServer(uri, null);
			            System.out.println("Adding http handler to server (jetty): "+uri.getPath());

			            ContextHandlerCollection collhandler = (ContextHandlerCollection)server.getHandler();
			            
			            ResourceHandler	rh	= new ResourceHandler();
			            ContextHandler	ch	= new ContextHandler()
			            {
			            	@Override
			            	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
			            	{
			            		super.doHandle(target, baseRequest, request, response);
			            	}
			            };
//			            ch.setBaseResource(Resource.newClassPathResource(rootpath));
			            ch.setBaseResource(new UniversalClasspathResource(rootpath));
			            ch.setHandler(rh);
			            ch.setContextPath(uri.getPath());
			            collhandler.addHandler(ch);
			            ch.start(); // must be started explicitly :-(((
						
						System.out.println("Resource published at: "+uri.getPath());
						ret.setResult(null);
//					}
//				});
//			}
//		});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
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

