package jadex.extension.rs.publish;

import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.ErrorPageGenerator;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.http.util.Header;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ServiceCall;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;

/**
 *  The default web service publish service.
 *  Publishes web services using the Grizzly web server.
 */
@Service
public class GrizzlyRestServicePublishService extends AbstractRestServicePublishService
{
	//-------- constants --------
	
	/** The servers per service id. */
	protected Map<IServiceIdentifier, Tuple2<HttpServer, URI>> sidservers;
	
	/** The servers per port. */
	protected Map<Integer, Tuple2<MainHttpHandler, HttpServer>> portservers;
	
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
	public void internalPublishService(URI uri, ResourceConfig rc, IServiceIdentifier sid, PublishInfo info)
	{
		try
		{
			Tuple2<MainHttpHandler, HttpServer> servertuple = getHttpServer(uri, info);
			System.out.println("Adding http handler to server: "+uri.getPath());
			HttpHandler handler = ContainerFactory.createContainer(HttpHandler.class, rc);
//			ServerConfiguration sc = server.getServerConfiguration();
//			sc.addHttpHandler(handler, uri.getPath());
			servertuple.getFirstEntity().addSubhandler(null, uri.getPath(), handler);
			
			if(sidservers==null)
				sidservers = new HashMap<IServiceIdentifier, Tuple2<HttpServer, URI>>();
			sidservers.put(sid, new Tuple2<HttpServer, URI>(servertuple.getSecondEntity(), uri));
			
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
	public Tuple2<MainHttpHandler, HttpServer> getHttpServer(URI uri, PublishInfo info)
	{
		Tuple2<MainHttpHandler, HttpServer> servertuple = null;
		
		try
		{
//			URI baseuri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
			servertuple = portservers==null? null: portservers.get(uri.getPort());
			
			if(servertuple==null)
			{
				HttpServer server = startServer(uri, info, null);
				MainHttpHandler mainhandler = new MainHttpHandler();
				server.getServerConfiguration().addHttpHandler(mainhandler);
				servertuple = new Tuple2<MainHttpHandler, HttpServer>(mainhandler, server);
				portservers.put(uri.getPort(), servertuple);
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
		
		return servertuple;
	}
	
	/**
	 *  Mirror an existing http server.
	 *  @param sourceserveruri The URI of the server being mirrored.
	 *  @param targetserveruri The URI of the mirror server.
	 *  @param info Publish infos for the mirror server.
	 */
	public IFuture<Void> mirrorHttpServer(URI sourceserveruri, URI targetserveruri, PublishInfo info)
	{
		Future<Void> ret = new Future<Void>();
		Tuple2<MainHttpHandler, HttpServer> sourceservertuple = portservers==null? null: portservers.get(sourceserveruri.getPort());
		
		if (sourceservertuple != null)
		{
			try
			{
				String errorfallback = null;
				if ((sourceservertuple.getSecondEntity().getServerConfiguration().getDefaultErrorPageGenerator() instanceof RedirectErrorPageGenerator))
				{
					errorfallback = ((RedirectErrorPageGenerator) sourceservertuple.getSecondEntity().getServerConfiguration().getDefaultErrorPageGenerator()).getRedirectUrl();
				}
				HttpServer newserver = startServer(targetserveruri, info, errorfallback);
				newserver.getServerConfiguration().addHttpHandler(sourceservertuple.getFirstEntity());
				Tuple2<MainHttpHandler, HttpServer> newservertuple = new Tuple2<MainHttpHandler, HttpServer>(sourceservertuple.getFirstEntity(), newserver);
				if (!(newserver.getServerConfiguration().getDefaultErrorPageGenerator() instanceof RedirectErrorPageGenerator))
				{
					ErrorPageGenerator epg = sourceservertuple.getSecondEntity().getServerConfiguration().getDefaultErrorPageGenerator();
					System.out.println(epg);
					if (epg != null)
					{
						newserver.getServerConfiguration().setDefaultErrorPageGenerator(epg);
					}
				}
				portservers.put(targetserveruri.getPort(), newservertuple);
				
			}
			catch (Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			ret.setException(new RuntimeException("Server mirror source not found: " + sourceserveruri.toString()));
		}
		
		return ret;
	}
	
	/**
	 *  Explicitely terminated an existing http server.
	 *  @param uri URI of the server.
	 */
	public IFuture<Void> shutdownHttpServer(URI uri)
	{
		Tuple2<MainHttpHandler, HttpServer> servertuple = getHttpServer(uri, null);
		for(Iterator<Tuple2<MainHttpHandler, HttpServer>> servers=portservers.values().iterator(); servers.hasNext(); )
    	{
    		if(servers.next().getSecondEntity().equals(servertuple.getSecondEntity()))
    		{
    			servers.remove();
    			break;
    		}
    	}
		System.out.println("Terminating server: "+uri.getPort());
    	servertuple.getSecondEntity().shutdownNow();
    	return IFuture.DONE;
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
	 *  Publish permanent redirect.
	 */
	public IFuture<Void> publishRedirect(final URI uri, final String html)
	{
		Tuple2<MainHttpHandler, HttpServer> servertuple = getHttpServer(uri, null);
		
		if (servertuple.getFirstEntity().containsSubhandlerForExactUri(null, uri.getPath()))
		{
			return new Future<Void>(new IllegalArgumentException("Cannot redirect, URI already bound: " + uri.toString()));
		}
		
		HttpHandler redh	= new HttpHandler()
	    {
	    	public void service(Request request, Response response)
	    	{
	    		response.setStatus(HttpStatus.MOVED_PERMANENTLY_301);
	    		response.setHeader(Header.Location, html);
	    	}
	    };
	    
		servertuple.getFirstEntity().addSubhandler(null, uri.getPath(), redh);
		
		return IFuture.DONE;
	}
	
	/**
	 *  Publish an html page.x	
	 */
	public IFuture<Void> publishHMTLPage(String pid, String vhost, String html)
	{
		try
		{
			URI uri = new URI(pid);
			Tuple2<MainHttpHandler, HttpServer> servertuple = getHttpServer(uri, null);
			
	//        ServerConfiguration sc = server.getServerConfiguration();
	//        Map<HttpHandler, String[]>	handlers	= sc.getHttpHandlers();
	//        HtmlHandler	htmlh	= null;
	//        for(Map.Entry<HttpHandler, String[]> entry: handlers.entrySet())
	//        {
	//        	if(entry.getKey() instanceof HtmlHandler)
	//        	{
	//        		if(Arrays.asList(entry.getValue()).contains(uri.getPath()))
	//        		{
	//	        		htmlh	= (HtmlHandler)entry.getKey();
	//	        		break;
	//        		}
	//        	}
	//        }
			
			if (servertuple.getFirstEntity().containsSubhandlerForExactUri(vhost, uri.getPath()))
			{
				return new Future<Void>(new IllegalArgumentException("Cannot publish HTML, URI already bound: " + uri.toString()));
			}
	        
	//        if(htmlh==null)
	//        {
			HtmlHandler htmlh	= new HtmlHandler()
		    {
		    	public void service(Request request, Response response)
		    	{
		    		// Hack!!! required for investment planner
		    		// Todo: make accessible to outside
		    		response.addHeader("Access-Control-Allow-Origin", "*");
		    		// http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
		    		response.addHeader("Access-Control-Allow-Credentials", "true ");
		    		response.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
		    		response.addHeader("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
	
		    		super.service(request, response);
		    	}
		    };
		    htmlh.addMapping(vhost, html);
		    
	//		sc.addHttpHandler(htmlh, uri.getPath());
	//        }
	        
	       	servertuple.getFirstEntity().addSubhandler(vhost, uri.getPath(), htmlh);
			
	//		System.out.println("published at: "+uri.getPath());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
	public IFuture<Void> publishResources(final String uri, final String path)
	{
		final Future<Void>	ret	= new Future<Void>();
		IComponentManagementService	cms	= component.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
		IComponentIdentifier	cid	= ServiceCall.getLastInvocation()!=null && ServiceCall.getLastInvocation().getCaller()!=null ? ServiceCall.getLastInvocation().getCaller() : component.getComponentIdentifier();
		cms.getComponentDescription(cid)
			.addResultListener(new ExceptionDelegationResultListener<IComponentDescription, Void>(ret)
		{
			public void customResultAvailable(IComponentDescription desc)
			{
				ILibraryService	ls	= component.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM));
				ls.getClassLoader(desc.getResourceIdentifier())
					.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
				{
					public void customResultAvailable(ClassLoader cl) throws URISyntaxException
					{
						Tuple2<MainHttpHandler, HttpServer> servertuple = getHttpServer(new URI(uri), null);
//				        ServerConfiguration sc = server.getServerConfiguration();
//						sc.addHttpHandler(new CLStaticHttpHandler(cl, path.endsWith("/")? path: path+"/")
//					    {
//					    	public void service(Request request, Response response) throws Exception
//					    	{
//					    		// Hack!!! required for investment planner
//					    		// Todo: make accessible to outside
//				   	    		response.addHeader("Access-Control-Allow-Origin", "*");
//			    	    		// http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
//			    	    		response.addHeader("Access-Control-Allow-Credentials", "true ");
//			    	    		response.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
//			    	    		response.addHeader("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
//					    		super.service(request, response);
//					    	}
//						}, uri.getPath());
						
						servertuple.getFirstEntity().addSubhandler(null, new URI(uri).getPath(), new CLStaticHttpHandler(cl, path.endsWith("/")? path: path+"/")
					    {
					    	public void service(Request request, Response response) throws Exception
					    	{
					    		// Hack!!! required for investment planner
					    		// Todo: make accessible to outside
				   	    		response.addHeader("Access-Control-Allow-Origin", "*");
			    	    		// http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
			    	    		response.addHeader("Access-Control-Allow-Credentials", "true ");
			    	    		response.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
			    	    		response.addHeader("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
					    		super.service(request, response);
					    	}
						});
						
						System.out.println("Resource published at: "+new URI(uri).getPath());
						ret.setResult(null);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Publish file resources from the file system.
	 */
	public IFuture<Void> publishExternal(final URI uri, String rootpath)
	{		
		Tuple2<MainHttpHandler, HttpServer> servertuple = getHttpServer(uri, null);
	    StaticHttpHandler handler	= new StaticHttpHandler(rootpath)
	    {
	    	public void service(Request request, Response response) throws Exception
	    	{
	    		// Hack!!! required for investment planner
	    		// Todo: make accessible to outside
   	    		response.addHeader("Access-Control-Allow-Origin", "*");
	    		// http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
	    		response.addHeader("Access-Control-Allow-Credentials", "true ");
	    		response.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
	    		response.addHeader("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
	    		super.service(request, response);
	    	}
	    };
	    handler.setFileCacheEnabled(false);	// see http://stackoverflow.com/questions/13307489/grizzly-locks-static-served-resources
		
//        ServerConfiguration sc = server.getServerConfiguration();
//		sc.addHttpHandler(handler, uri.getPath());
	    servertuple.getFirstEntity().addSubhandler(null, uri.getPath(), handler);
		
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
			Tuple2<HttpServer, URI> tup = sidservers.remove(sid);
			if(tup!=null)
			{
				HttpServer server = tup.getFirstEntity();
//			    ServerConfiguration config = server.getServerConfiguration();
			    System.out.println("unpub: "+tup.getSecondEntity());
			    
//			    config.removeHttpHandler(tup.getSecondEntity());
//			    if(config.getHttpHandlers().size()==0)
			    Tuple2<MainHttpHandler, HttpServer> servertuple = getHttpServer(tup.getSecondEntity(), null);
				servertuple.getFirstEntity().removeSubhandler(null, tup.getSecondEntity().getPath());
			    if (servertuple.getFirstEntity().isEmpty())
			    {
			    	for(Iterator<Tuple2<MainHttpHandler, HttpServer>> servers=portservers.values().iterator(); servers.hasNext(); )
			    	{
			    		if(servers.next().getSecondEntity().equals(server))
			    		{
			    			servers.remove();
			    			break;
			    		}
			    	}
			    	server.shutdownNow();

			    }
			    stopped = true;
				ret.setResult(null);
			}
		}
		if(!stopped)
			ret.setException(new RuntimeException("Published service could not be stopped: "+sid));
		return ret;
	}
	
	/**
	 *  Unpublish an already-published handler.
	 *  @param vhost The virtual host, if any, null for general.
	 *  @param uti The uri being unpublished.
	 */
	public IFuture<Void> unpublish(String vhost, URI uri)
	{
		Tuple2<MainHttpHandler, HttpServer> servertuple = getHttpServer(uri, null);
		
		servertuple.getFirstEntity().removeSubhandler(vhost, uri.getPath());
		
		return IFuture.DONE;
	}
	
	/**
	 *  Test if a service is published.
	 */
	public boolean isPublished(IServiceIdentifier sid)
	{
		return sidservers!=null && sidservers.containsKey(sid);
	}
	
	/**
	 *  Starts a server.
	 *  
	 *  @param uri The server URI.
	 *  @param info Publish infos.
	 *  @param errorpagefallback Error page URL fallback if not provided in publish infos.
	 *  @return The server.
	 */
	protected HttpServer startServer(URI uri, PublishInfo info, String errorpagefallback) throws Exception
	{
		HttpServer server = null;
		
		System.out.println("Starting new server: "+uri.getPort());
		
		ErrorPageGenerator epg = null;
		
		String	keystore	= null;
		String	keystorepass	= null;
		if(info!=null)
		{
			for(UnparsedExpression upex: info.getProperties())
			{
//				System.out.println("found publish expression: "+upex.getName());
				
				if("sslkeystore".equals(upex.getName()))
				{
					keystore	= (String)SJavaParser.getParsedValue(upex, null,
						component!=null? component.getFetcher(): null, component!=null? component.getClassLoader(): null);
				}
				else if("sslkeystorepass".equals(upex.getName()))
				{
					keystorepass	= (String)SJavaParser.getParsedValue(upex, null,
						component!=null? component.getFetcher(): null, component!=null? component.getClassLoader(): null);
				}
				else if("errorpage".equals(upex.getName()))
				{
					String errpage = null;
					try
					{
						errpage = (String)SJavaParser.getParsedValue(upex, null,
							component!=null? component.getFetcher(): null, component!=null? component.getClassLoader(): null);
					}
					catch (Exception e)
					{
						errpage = upex.getValue();
					}
					
					if(errpage!=null)
					{
						
						epg = new RedirectErrorPageGenerator(errpage);
					}
				}
			}
		}
		
		if(keystore!=null)
		{
			SSLContextConfigurator sslContext = new SSLContextConfigurator();
			sslContext.setKeyStoreFile(keystore); // contains server keypair
			sslContext.setKeyStorePass(keystorepass);
//			sslContext.setTrustStoreFile("./truststore_server"); // contains client certificate
//			sslContext.setTrustStorePass("asdfgh");
			SSLEngineConfigurator sslConf = new SSLEngineConfigurator(sslContext).setClientMode(false);
			
			server = GrizzlyHttpServerFactory.createHttpServer(uri, (GrizzlyHttpContainer)null, true, sslConf, false);
		}
		else
		{
			server	= GrizzlyHttpServerFactory.createHttpServer(uri, false);
		}
		
		if(epg==null && errorpagefallback != null)
		{
			epg = new RedirectErrorPageGenerator(errorpagefallback);
		}
		
		if(epg!=null)
		{
			server.getServerConfiguration().setDefaultErrorPageGenerator(epg);
		}
		server.start();
		
		if(portservers==null)
			portservers = new HashMap<Integer, Tuple2<MainHttpHandler, HttpServer>>();
		
		return server;
	}
	
	//-------- helper classes --------
	
	/**
	 *  Main handler dealing with incoming request more intelligently than Grizzly does.
	 *
	 */
	public static class MainHttpHandler extends HttpHandler
	{
		/** Published subhandlers.
		 *  vhost+path -> path+httphandler
		 *  
		 *  Path needs to be preserved in the value since the cache does not preserve it.
		 */
		protected Map<Tuple2<String, String>, Tuple2<String, HttpHandler>> subhandlers;
		
		/** Published subhandler matching cache. */
		protected Map<Tuple2<String, String>, Tuple2<String, HttpHandler>> subhandlercache;
		
		/**
		 *  Create the handler.
		 */
		public MainHttpHandler()
		{
			subhandlers = Collections.synchronizedMap(new HashMap<Tuple2<String, String>, Tuple2<String, HttpHandler>>());
			subhandlercache = Collections.synchronizedMap(new HashMap<Tuple2<String, String>, Tuple2<String, HttpHandler>>());
		}
		
		/**
		 *  Service the request.
		 */
		public void service(Request request, Response resp) throws Exception
		{
			
			String path = request.getRequest().getRequestURIRef().getURI();
			String	host	= request.getHeader("host");
			int	idx	= host.indexOf(":");
			if(idx!=-1)
			{
				host	= host.substring(0, idx);
			}
			
			Tuple2<String, HttpHandler> subhandlertuple = subhandlercache.get(new Tuple2<String, String>(host, path));
			if (subhandlertuple == null)
			{
				subhandlertuple = subhandlercache.get(new Tuple2<String, String>(null, path));
			}
			
			int pidx = path.lastIndexOf('/');
			if (subhandlertuple == null && pidx > 0 && pidx <= path.length() - 1)
			{
				String cpath = path.substring(0, pidx);
				subhandlertuple = subhandlercache.get(new Tuple2<String, String>(host, cpath));
			}
			
			
			if (subhandlertuple == null)
			{
				subhandlertuple = findSubhandler(host, path);
				if (subhandlertuple == null)
				{
					subhandlertuple = findSubhandler(null, path);
				}
				
				if (subhandlertuple != null)
				{
					subhandlercache.put(new Tuple2<String, String>(host, path), subhandlertuple);
				}
			}
			
			if (subhandlertuple == null)
			{
				throw new RuntimeException("No handler found for path: " + path);
			}
			
			try
			{
				Method setcontextpath = Request.class.getDeclaredMethod("setContextPath", new Class<?>[] { String.class });
				setcontextpath.setAccessible(true);
				setcontextpath.invoke(request, subhandlertuple.getFirstEntity());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			subhandlertuple.getSecondEntity().service(request, resp);
		}
		
		/**
		 *  Adds a new subhandler.
		 *  
		 *  @param vhost Virtual host specification.
		 *  @param path Path being handled.
		 *  @param subhandler The subhandler.
		 */
		public void addSubhandler(String vhost, String path, HttpHandler subhandler)
		{
			subhandlers.put(new Tuple2<String, String>(vhost, path), new Tuple2<String, HttpHandler>(path, subhandler));
			synchronized (subhandlers)
			{
				subhandlercache = Collections.synchronizedMap(new HashMap<Tuple2<String,String>, Tuple2<String, HttpHandler>>(subhandlers));
			}
		}
		
		/**
		 *  Tests if a handler for the exact URI is currently published.
		 * 
		 *  @param vhost Virtual host specification.
		 *  @param path Path being handled.
		 *  @return True, if a handler was found.
		 */
		public boolean containsSubhandlerForExactUri(String vhost, String path)
		{
			return subhandlers.containsKey(new Tuple2<String, String>(vhost, path));
		}
		
		/**
		 *  Tests if the handler contains no subhandlers.
		 *  
		 *  @return True, if no subhandlers remain.
		 */
		public boolean isEmpty()
		{
			return subhandlers.isEmpty();
		}
		
		/**
		 * 
		 * @param vhost Virtual host specification.
		 *  @param path Path being handled.
		 */
		public void removeSubhandler(String vhost, String path)
		{
			subhandlers.remove(new Tuple2<String, String>(vhost, path));
			synchronized (subhandlers)
			{
				subhandlercache = Collections.synchronizedMap(new HashMap<Tuple2<String,String>, Tuple2<String, HttpHandler>>(subhandlers));
			}			
		}
		
		/**
		 *  Locates an appropriate subhandler that matches the requested resource closely.
		 *  
		 *  @param host The requested virtual host.
		 *  @param path The requested path
		 *  @return The subhandler or null if none is found for the host.
		 */
		protected Tuple2<String, HttpHandler> findSubhandler(String host, String path)
		{
			Tuple2<String, HttpHandler> ret = null;
			do
			{
				int pidx = path.lastIndexOf('/');
				if (pidx >= 0)
				{
					path = path.substring(0, pidx);
					ret = subhandlercache.get(new Tuple2<String, String>(host, path));
				}
				else
				{
					path = null;
				}
			}
			while (ret == null && path != null && path.length() > 0);
			return ret;
		}
	}
	
	/**
	 *	Allow responding with different htmls based on virtual host name. 
	 */
	// Hack!! only works if no different contexts are published at overlapping resources: e.g. hugo.com/foo/bar vs. dummy.com/foo.
	public static class HtmlHandler extends HttpHandler
	{
		//-------- attributes --------
		
		/** The html responses (host->response). */
		protected Map<String, String>	mapping;

		//-------- constructors --------
		
		/**
		 *  Create an html handler.
		 */
		public HtmlHandler()
		{
			this.mapping = new LinkedHashMap<String, String>();
		}
		
		//-------- methods --------
		
		/**
		 *  Add a mapping.
		 */
		public void	addMapping(String host, String html)
		{
			this.mapping.put(host, html);
		}
		
		//-------- HttpHandler methods --------

		/**
		 *  Service the request.
		 */
		public void service(Request request, Response resp) 
		{
			String	host	= request.getHeader("host");
			int	idx	= host.indexOf(":");
			if(idx!=-1)
			{
				host	= host.substring(0, idx);
			}
			String	html	= null;
			
			for(Map.Entry<String, String> entry: mapping.entrySet())
			{
				if(entry.getKey().equals(host))
				{
					html	= entry.getValue();
					break;
				}
				else if(html==null)
				{
					// Use first entry, when no other match is found.
					html	= entry.getValue(); 
				}
			}
			
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
	}
	
	public static class RedirectErrorPageGenerator implements ErrorPageGenerator
	{
		/** Redirect URL. */
		protected String redirecturl;
		
		/**
		 *  Creates the Generator. 
		 */
		public RedirectErrorPageGenerator(String redirecturl)
		{
			this.redirecturl = redirecturl;
		}
		
		/**
		 *  Returns the redirect URL.
		 *  @return The redirect URL.
		 */
		public String getRedirectUrl()
		{
			return redirecturl;
		}
		
		/**
		 *  Generates the error page with a redirect.
		 */
        public String generate(Request request, int status, String reasonPhrase, String description, Throwable exception) 
        {
       	 return "<html><html><head><meta http-equiv=\"refresh\" content=\"0;url=" + redirecturl + "\"><body></body></html>";
        }
	}
}
