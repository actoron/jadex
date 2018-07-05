package jadex.extension.rs.publish;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.MultipartConfigElement;

import org.glassfish.grizzly.http.server.ErrorPageGenerator;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.server.util.MappingData;
import org.glassfish.grizzly.servlet.HttpServletRequestImpl;
import org.glassfish.grizzly.servlet.HttpServletResponseImpl;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;

import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;

/**
 *  Publish service without Jersey directly using Grizly container.
 */
@Service
public class GrizzlyRestPublishService extends AbstractRestPublishService
{
	// Hack constant for enabling multi-part :-(
	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

    /** The servers per service id. */
    protected Map<IServiceIdentifier, HttpServer> sidservers;

    /** The servers per port. */
    protected Map<Integer, HttpServer> portservers;
    
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
        	final IService service = component.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>((Class<IService>)null).setServiceIdentifier(serviceid)).get();
        	
            final URI uri = new URI(getCleanPublishId(info.getPublishId()));
            HttpServer server = (HttpServer)getHttpServer(uri, info);
            System.out.println("Adding http handler to server: "+uri.getPath());

            final MultiCollection<String, MappingInfo> mappings = evaluateMapping(service.getServiceIdentifier(), info);

        	HttpHandler handler = new HttpHandler()
			{
				public void service(Request request, Response response) throws Exception
				{
					// Hack to enable multi-part
                    // http://dev.eclipse.org/mhonarc/lists/jetty-users/msg03294.html
//                    if(request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) 
//                    	baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
                	
					HttpServletResponseImpl res = HttpServletResponseImpl.create();
					HttpServletRequestImpl req = HttpServletRequestImpl.create();
					req.initialize(request, res, new WebappContext(uri.getPath(), uri.getPath())); 
					res.initialize(response, req);
					
					MappingData data = request.obtainMappingData();
					
					String pi = request.getPathInfo();
					
					if(pi==null)
					{
						String ctx = request.getContextPath();
						String full = request.getRequestURL().toString();
						if(ctx!=null && full!=null)
						{
							int start = full.indexOf(ctx)+ctx.length();
							int end = full.indexOf("?");
							if(start>0 && full.length()>start)
							{
								pi = full.substring(start, end>0? end: full.length());
							}
						}
						if("/".equals(pi))
							pi = null;
					}
					if(pi!=null)
					{
						req.setServletPath(data.wrapperPath.toString());
						Method m = req.getClass().getDeclaredMethod("setPathInfo", new Class[]{String.class});
						m.setAccessible(true);
						m.invoke(req, new Object[]{pi});
					}
					
				    Method m = req.getClass().getDeclaredMethod("setContextPath", new Class[]{String.class});
				    m.setAccessible(true);
				    m.invoke(req, new Object[]{data.contextPath.toString()});
			            
//			        request.setNote(SERVLET_REQUEST_NOTE, servletRequest);
//			        request.setNote(SERVLET_RESPONSE_NOTE, servletResponse);

                	handleRequest(service, mappings, req, res, null);
                	
//                  System.out.println("handler is: "+uri.getPath());
				}
			};

			ServerConfiguration sc = server.getServerConfiguration();
			sc.addHttpHandler(handler, uri.getPath());

            if(sidservers==null)
                sidservers = new HashMap<IServiceIdentifier, HttpServer>();
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
		HttpServer server = null;
		
		try
		{
//			URI baseuri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
			server = portservers==null? null: portservers.get(uri.getPort());
			
			if(server==null)
			{
				System.out.println("Starting new server: "+uri.getPort());
				
				ErrorPageGenerator epg = null;
				
				String	keystore	= null;
				String	keystorepass	= null;
				if(info!=null)
				{
					for(UnparsedExpression upex: info.getProperties())
					{
//						System.out.println("found publish expression: "+upex.getName());
						
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
							String errpage = (String)SJavaParser.getParsedValue(upex, null,
								component!=null? component.getFetcher(): null, component!=null? component.getClassLoader(): null);
							
							if(errpage!=null)
							{
								final String errp = SUtil.readFile(errpage);
								
//								System.out.println("errorpage path: "+errpage);
//								System.out.println("errorpage: "+errp);
								
								epg = new ErrorPageGenerator()
								{
						             public String generate(Request request, int status, String reasonPhrase, String description, Throwable exception) 
						             {
						            	 return errp;
						             }
								};
							}
						}
					}
				}
				
				if(keystore!=null)
				{
					SSLContextConfigurator sslContext = new SSLContextConfigurator();
					sslContext.setKeyStoreFile(keystore); // contains server keypair
					sslContext.setKeyStorePass(keystorepass);
//					sslContext.setTrustStoreFile("./truststore_server"); // contains client certificate
//					sslContext.setTrustStorePass("asdfgh");
					SSLEngineConfigurator sslConf = new SSLEngineConfigurator(sslContext).setClientMode(false);
					
					server = new HttpServer();

//			        final ServerConfiguration config = server.getServerConfiguration();
//			        config.addHttpHandler(handler, uri.getPath());
//			        config.setPassTraceRequest(true);
				}
				else
				{
					server = new HttpServer();
//					server	= GrizzlyHttpServerFactory.createHttpServer(uri, false);
				}
				
				NetworkListener listener = new NetworkListener("lis", 
					uri.getHost()!=null? uri.getHost(): "0.0.0.0", uri.getPort()!=-1? uri.getPort(): 80);
			    server.addListener(listener);
				
				if(epg!=null)
				{
					server.getServerConfiguration().setDefaultErrorPageGenerator(epg);
				}
				
				ServerConfiguration sc = server.getServerConfiguration();
//				sc.addHttpHandler(new HttpHandler()
//				{
//					public void service(Request request, Response response) throws Exception
//					{
//						Writer w = response.getWriter();
//						w.write("ende");
//					}
//				}, "/test");
				
				server.start();
				
				if(portservers==null)
					portservers = new HashMap<Integer, HttpServer>();
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
    public IFuture<Void> publishHMTLPage(String uri, String vhost, String html)
    {
        throw new UnsupportedOperationException();
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
}

