package jadex.extension.rs.publish;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.future.Future;

/**
 *  Publish service using Nano.
 */
@Service
public class NanoRestPublishService extends ExternalRestPublishService
{
	public class Server extends NanoHTTPD 
	{
		public Server(int port) 
		{
			super(port);
		}
		
		@Override 
		public Response serve(IHTTPSession session) 
		{
			System.out.println("serve called: "+session.getUri());
			
			Response[] ret = new Response[1];
			
			NanoHttpServletRequestWrapper req = new NanoHttpServletRequestWrapper(session);
			NanoHttpServletResponseWrapper resp = new NanoHttpServletResponseWrapper(session);
			
			// todo: make handle request use async context return 
			handleRequest(req, resp, null).get();
			
			Future<Void> wait = new Future<>();
			
			Runnable run = new Runnable()
			{
				@Override
				public void run()
				{
					IStatus status = Response.Status.lookup(resp.getStatus());
					String mimetype = resp.getContentType();
					String txt = resp.getOutbuf().toString();
					ret[0] = newFixedLengthResponse(status, mimetype, txt);
				}
			};
			
			if(req.isAsyncStarted())
			{
				req.getAsyncContext().addListener(new AsyncListener()
				{
					@Override
					public void onTimeout(AsyncEvent event) throws IOException
					{
					}
					
					@Override
					public void onStartAsync(AsyncEvent event) throws IOException
					{
					}
					
					@Override
					public void onError(AsyncEvent event) throws IOException
					{
					}
					
					@Override
					public void onComplete(AsyncEvent event) throws IOException
					{
						run.run();
						wait.setResult(null);
					}
				});
			}
			else
			{
				run.run();
			}
			
			wait.get();
			
			return ret[0];
		}
	}
	
	/** The servers per port. */
	protected Map<Integer, Server> portservers2;
	
	@ServiceStart
	public void start()
	{
		super.init();
		
		System.out.println("Nano started");
	}
  
	@ServiceShutdown
	public void stop()
	{
		if(portservers2 != null)
		{
			for(Map.Entry<Integer, Server> entry : portservers2.entrySet())
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
		System.out.println("Nano stopped");
	}
	
	/**
	 *  Get or start an api to the http server.
	 */
	public Object getHttpServer(URI uri, PublishInfo info)
	{
		Object ps = super.getHttpServer(uri, info);
		
		Server server = null;
		 
        try
        {
        	server = portservers2==null? null: portservers2.get(uri.getPort());
 
        	if(server==null)
            {
        		System.out.println("Starting new server: "+uri.getPort());
                server = new Server(uri.getPort());
 
                server.start();
 
                if(portservers2==null)
                	portservers2 = new HashMap<Integer, Server>();
                portservers2.put(uri.getPort(), server);
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
	
//    /**
//     *  Publish a static page (without ressources).
//     */
//    public IFuture<Void> publishHMTLPage(String pid, String vhost, final String html)
//    {
//    	try
//        {
//    		String clpid = pid.replace("[", "").replace("]", "");
//    		URI uri = new URI(clpid);
//        	//final IService service = (IService) component.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( serviceid)).get();
//        	
//            Server server = (Server)getHttpServer(uri, null);
//            System.out.println("Adding http handler to server: "+uri.getPath());
//
//            ContextHandlerCollection collhandler = (ContextHandlerCollection)server.getHandler();
//
//            ContextHandler ch = new ContextHandler()
//            {
//                public void doHandle(String target, Request baseRequest, final HttpServletRequest request, final HttpServletResponse response)
//                    throws IOException, ServletException
//                {
//                	response.getWriter().write(html);
//                	
////                  System.out.println("handler is: "+uri.getPath());
//                    baseRequest.setHandled(true);
//                }
//            };
//            ch.setContextPath(uri.getPath());
//            collhandler.addHandler(ch);
//            ch.start(); // must be started explicitly :-(((
//        }
//        catch(Exception e)
//        {
//            throw new RuntimeException(e);
//        }
//        
//        return IFuture.DONE;
//    }
//
//    /**
//     *  Publish file resources from the classpath.
//     */
//    public IFuture<Void> publishResources(final String pid, final String rootpath)
//    {
//		final Future<Void>	ret	= new Future<Void>();
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
//					public void customResultAvailable(ClassLoader cl) throws Exception 
//					{
//			    		String clpid = pid.replace("[", "").replace("]", "");
//			    		URI uri = new URI(clpid);
//			        	//final IService service = (IService) component.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( serviceid)).get();
//			        	
//			            Server server = (Server)getHttpServer(uri, null);
//			            System.out.println("Adding http handler to server: "+uri.getPath());
//
//			            ContextHandlerCollection collhandler = (ContextHandlerCollection)server.getHandler();
//			            
//			            ResourceHandler	rh	= new ResourceHandler();
//			            ContextHandler	ch	= new ContextHandler()
//			            {
//			            	@Override
//			            	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
//			            	{
//			            		// TODO Auto-generated method stub
//			            		super.doHandle(target, baseRequest, request, response);
//			            	}
//			            };
////			            ch.setBaseResource(Resource.newClassPathResource(rootpath));
//			            ch.setBaseResource(new UniversalClasspathResource(rootpath));
//			            ch.setHandler(rh);
//			            ch.setContextPath(uri.getPath());
//			            collhandler.addHandler(ch);
//			            ch.start(); // must be started explicitly :-(((
//						
//						System.out.println("Resource published at: "+uri.getPath());
//						ret.setResult(null);
//					}
//				});
//			}
//		});
//		
//		return ret;
//    }
//	
//	public IFuture<Void> mirrorHttpServer(URI sourceserveruri, URI targetserveruri, PublishInfo info)
//	{
//        throw new UnsupportedOperationException();
//	}
//
//
//	public IFuture<Void> shutdownHttpServer(URI uri)
//	{
//        throw new UnsupportedOperationException();
//	}
}

