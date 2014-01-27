package jadex.extension.rs.publish;

import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.Writer;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *  The default web service publish service.
 *  Publishes web services using the Grizzly web server.
 */
@Service
public class GrizzlyRestServicePublishService extends AbstractRestServicePublishService
{
	//-------- constants --------
	
	/** The servers per service id. */
	protected Map<IServiceIdentifier, Tuple2<HttpServer, HttpHandler>> sidservers;
	
	/** The servers per uri. */
	protected Map<URI, HttpServer> uriservers;
	
	//-------- constructors --------
	
	/**
	 *  Create a new publish service.
	 */
	public GrizzlyRestServicePublishService()
	{
	}
	
	/**
	 *  Create a new publish service.
	 */
	public GrizzlyRestServicePublishService(IRestMethodGenerator generator)
	{
		super(generator);
	}
	
	//-------- methods --------
	
	/**
	 * 
	 */
	public void internalPublishService(URI uri, ResourceConfig rc, IServiceIdentifier sid)
	{
		try
		{
			HttpServer server = getHttpServer(uri);
			System.out.println("Adding http handler to server: "+uri.getPath());
			HttpHandler handler = ContainerFactory.createContainer(HttpHandler.class, rc);
			ServerConfiguration sc = server.getServerConfiguration();
			sc.addHttpHandler(handler, uri.getPath());
			
			if(sidservers==null)
				sidservers = new HashMap<IServiceIdentifier, Tuple2<HttpServer, HttpHandler>>();
			sidservers.put(sid, new Tuple2<HttpServer, HttpHandler>(server, handler));
			
	//		Map<HttpHandler, String[]> handlers = server.getServerConfiguration().getHttpHandlers();
	//		for(HttpHandler hand: handlers.keySet())
	//		{
	//			Set<String> set = SUtil.arrayToSet(handlers.get(hand));
	//			if(set.contains(uri.getPath()))
	//			{
	//				handler = hand;
	//			}
	//		}
	//		if(handler==null)
	//		{
	//			ret.setException(new RuntimeException("Publication error, failed to get http handler: "+uri.getPath()));
	//		}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Get or start an api to the http server.
	 */
	public HttpServer getHttpServer(URI uri)
	{
		HttpServer server = null;
		
		try
		{
			URI baseuri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
			server = uriservers==null? null: uriservers.get(baseuri);
			
			if(server==null)
			{
				System.out.println("Starting new server: "+uri.getPath());
				
				server = GrizzlyHttpServerFactory.createHttpServer(uri);
				server.start();
				
				if(uriservers==null)
					uriservers = new HashMap<URI, HttpServer>();
				uriservers.put(baseuri, server);
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
	
//	/**
//	 * 
//	 */
//	public IFuture<Void> publishServet(URI uri, Object servlet)
//	{
//		HttpServer server = getHttpServer(uri, createConfig());
//		ServletConfigImpl conf = new ServletConfigImpl();
//		ServletHandler handler = new ServletHandler(conf);
//		handler.setContextPath(uri.getPath());
//        ServerConfiguration sc = server.getServerConfiguration();
//		sc.addHttpHandler(handler, uri.getPath());
//		
//		WebappContext ctx = new WebappContext(uri.getPath());
//		ServletRegistration reg = ctx.addServlet(SReflect.getInnerClassName(servlet.getClass()), servlet);
//		reg.addMapping(alias);
//		ctx.deploy(server);
//		
//		return IFuture.DONE;
//	}
	
	/**
	 *  Publish an html page.
	 */
	public IFuture<Void> publishHMTLPage(URI uri, final String html)
	{
		HttpServer server = getHttpServer(uri);
		
        ServerConfiguration sc = server.getServerConfiguration();
		sc.addHttpHandler(new HttpHandler() 
		{
            public void service(Request request, Response resp) 
            {
            	try
            	{
	            	// Set response content type
	                resp.setContentType("text/html");
	
	                // Actual logic goes here.
	                Writer out = resp.getWriter();
	                out.write(html);
            	}
            	catch(Exception e)
            	{
            		e.printStackTrace();
            	}
            }
        }, uri.getPath());
		
//		System.out.println("published at: "+uri.getPath());
		
		return IFuture.DONE;
	}
	
//	/**
//	 *  Publish a resource.
//	 */
////	public IFuture<Void> publishResource(URI uri, final ResourceInfo ri)
//	public IFuture<Void> publishResource(URI uri, final String type, final String filename)
//	{
//		HttpServer server = getHttpServer(uri, createConfig());
//		
//        ServerConfiguration sc = server.getServerConfiguration();
//		sc.addHttpHandler(new HttpHandler() 
//		{
//            public void service(Request request, Response resp) 
//            {
//            	try
//            	{
//	            	// Set response content type
//	                resp.setContentType(type!=null? type: "text/html");
//	
//	                InputStream is = null;
//	                try
//	        		{
//	        			is = SUtil.getResource0(filename, null);
//	        			OutputStream os = resp.getOutputStream();
//	        			SUtil.copyStream(is, os);
//	        		}
//	        		finally
//	        		{
//	        			try
//	        			{
//	        				if(is!=null)
//	        					is.close();
//	        			}
//	        			catch(Exception e)
//	        			{
//	        			}
//	        		}
//            	}
//            	catch(Exception e)
//            	{
//            		e.printStackTrace();
//            	}
//            }
//        }, uri.getPath());
//		
//		return IFuture.DONE;
//	}
	
	/**
	 *  Publish resources via a rel jar path.
	 *  The resources are searched with respect to the
	 *  component classloader (todo: allow for specifiying RID).
	 */
	public IFuture<Void> publishResources(URI uri, String path)
	{
		HttpServer server = getHttpServer(uri);
        ServerConfiguration sc = server.getServerConfiguration();
        path = path.endsWith("/")? path: path+"/";
		sc.addHttpHandler(new CLStaticHttpHandler(component.getClassLoader(), path)
		{
			public void service(Request request, Response resp) throws Exception
			{
				super.service(request, resp);
			}
		}, uri.getPath());
		
//		System.out.println("published at: "+uri.getPath());
		
		return IFuture.DONE;
	}
	
	/**
	 *  Unpublish a service.
	 *  @param sid The service identifier.
	 */
	public IFuture<Void> unpublishService(IServiceIdentifier sid)
	{
		Future<Void> ret = new Future<Void>();
		boolean stopped = false;
		if(sidservers!=null)
		{
			Tuple2<HttpServer, HttpHandler> tup = sidservers.remove(sid);
			if(tup!=null)
			{
				HttpServer server = tup.getFirstEntity();
			    ServerConfiguration config = server.getServerConfiguration();
			    config.removeHttpHandler(tup.getSecondEntity());
			    if(config.getHttpHandlers().size()==0)
			    	server.shutdownNow();
			    stopped = true;
				ret.setResult(null);
			}
		}
		if(!stopped)
			ret.setException(new RuntimeException("Published service could not be stopped: "+sid));
		return ret;
	}
}
