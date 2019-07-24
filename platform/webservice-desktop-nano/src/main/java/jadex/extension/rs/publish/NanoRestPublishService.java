package jadex.extension.rs.publish;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jadex.base.JarAsDirectory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Publish service using Nano.
 */
@Service
public class NanoRestPublishService extends ExternalRestPublishService
{
	/** The servers per port. */
	protected Map<Integer, NanoWebsocketServer> portservers2;
	
	@ServiceStart
	public void start()
	{
		if(!inited)
    	{
    		super.init();
    		//System.out.println("Nano started: "+component.getId());
    	}
	}
  
	@ServiceShutdown
	public void stop()
	{
		if(portservers2 != null)
		{
			for(Map.Entry<Integer, NanoWebsocketServer> entry : portservers2.entrySet())
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
		
		NanoWebsocketServer server = null;
		 
        try
        {
        	server = portservers2==null? null: portservers2.get(uri.getPort());
 
        	if(server==null)
            {
//        		System.out.println("Starting new server: "+uri.getPort());
                server = new NanoWebsocketServer(uri.getPort(), component, this);
 
                server.start();
 
                if(portservers2==null)
                	portservers2 = new HashMap<Integer, NanoWebsocketServer>();
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
	
	/**
	 *  Get or start an api to the http server.
	 */
	public PathHandler getPathHandler(URI uri, PublishInfo info)
	{
		return (PathHandler)super.getHttpServer(uri, info);
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
    /**
     *  Publish file resources from the classpath.
     *  @param pid The publish url.
     *  @param rootpath The classpath root path where to look for the reources.
     */
	// example "[http://localhost:8081/]", "META-INF/resources";
    public IFuture<Void> publishResources(final String pid, final String rootpath)
    {
//    	String rp = root;
//    	if(rp.endsWith("/"))
//    		rp = rp.substring(0, rp.length()-1);
//    	final String rootpath = rp;
    	
    	System.out.println("publish resources: "+pid+" from "+rootpath);
    	
		final Future<Void>	ret	= new Future<Void>();
		
		IComponentIdentifier cid = ServiceCall.getLastInvocation()!=null && ServiceCall.getLastInvocation().getCaller()!=null? ServiceCall.getLastInvocation().getCaller() : component.getId();
		component.getDescription(cid)
			.addResultListener(new ExceptionDelegationResultListener<IComponentDescription, Void>(ret)
		{
			public void customResultAvailable(IComponentDescription desc)
			{
				ILibraryService	ls	= component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( ILibraryService.class, ServiceScope.PLATFORM));
				ls.getClassLoader(desc.getResourceIdentifier())
					.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
				{
					public void customResultAvailable(ClassLoader cl) throws Exception 
					{
						try
					    {
							URI uri = convertUri(pid);
							
//							String mpid = pid;
//														
//							if(mpid.startsWith("["))
//							{
//								mpid = mpid.substring(1);
//								mpid = mpid.replace("]", "");
//							}
//							
//							if(mpid.endsWith("/"))
//								mpid = mpid.substring(0, mpid.length()-1);
//							
//							uri = new URI(mpid);
							
//					    	if(mpid.startsWith("["))
//					    	{
//					    		mpid = mpid.substring(mpid.indexOf("]")+1);
//					    		uri = new URI(DEFAULT_COMPLETECONTEXT+mpid);
////						    		uri = new URI("http://DEFAULTHOST:0/DEFAULTAPP/"+pid);
//					    	}
//					    	else
//					    	{
//					    		uri = new URI(mpid);
//					    	}
					    	
					        component.getLogger().info("Adding http handler to server: "+uri.getPath());
					        
					        // cannot use DEFAULT_COMPLETECONTEXT because here it needs to start a server
					        getHttpServer(uri, null);
					        PathHandler ph = getPathHandler(uri, null);
					        
					        IRequestHandler rh = new IRequestHandler()
							{
								public void handleRequest(HttpServletRequest request, HttpServletResponse response, Object args) throws Exception
								{
									String url = request.getRequestURL().toString();
									
									// Remove the first part of the url containing the
									String purl = pid;
									if(purl.startsWith("["))
							    		purl = purl.substring(pid.indexOf("]")+1);
									int idx = url.indexOf(purl);
									if(idx!=-1)
										url = url.substring(idx+purl.length());
									
									if(url.equals("/") || url.length()==0)
										url = "/index.html";
									
									String rp = rootpath;
									if(rp.endsWith("/"))
										rp = rp.substring(0, rp.length()-1);
									
									String fp = rp+url;
									if(fp.startsWith("/"))
										fp = fp.substring(1);
									
									//System.out.println("handling: "+url+" "+fp);
									
									// All java variants do not work properly :-(
//									MimetypesFileTypeMap ftm = new MimetypesFileTypeMap();
//									String mime = ftm.getContentType(fp);
//									String mimeType = Files.probeContentType();
//									String mime = MimeTypes.getMimeType(fp);
									String mime = SUtil.guessContentTypeByFilename(fp);
									
									// add charset as UTF-8 :-(, for link tag otherwise some ISO charset is default
									if(mime!=null && mime.startsWith("text") && !mime.contains("charset"))
					        			mime = mime + ";charset=utf-8";
									
//									System.out.println("MIME: "+fp+" "+mime);
									
									if(mime!=null)
										response.setContentType(mime);
									
//									System.out.println("serve path: "+fp);
									
									File f = getFile(cl, fp);
									if(f!=null && f.exists())
									{
										response.setStatus(200);
										OutputStream os = response.getOutputStream();
										int size = SUtil.copyStream(getInputStream(f), os);
//										System.out.println("filesize: "+fp+" "+size);
									}
									else
									{
										System.out.println("file not found: "+fp);
										response.setStatus(404);
									}
								}
							};
							if(ph.containsSubhandlerForExactUri(null, uri.getPath()))
							{
								component.getLogger().info("The URL "+uri.getPath() + " is already published, unpublishing...");
								ph.removeSubhandler(null, uri.getPath());
							}
							
							ph.addSubhandler(null, uri.getPath(), rh);
							
							ret.setResult(null);
					    }
					    catch(Exception e)
					    {
					    	ret.setException(e);
					    }
					}
				});
			}
		});
			
	    return ret;
    }
    
    public static File getFile(ClassLoader cl, String path) throws IOException
	{
    	URL url = getURL(cl, path);
    	
    	if(url!=null)
    	{
			if("file".equals(getURL(cl, path).getProtocol()))
			{
				return SUtil.getFile(getURL(cl, path));
			}
			else if("jar".equals(getURL(cl, path).getProtocol()))
			{
				String jar = getURL(cl, path).getPath();
				String entry = null;
				if(jar.contains("!/"))
				{
					entry = jar.substring(jar.indexOf("!/")+2);
					jar	= jar.substring(0, jar.indexOf("!/"));
				}
				return new JarAsDirectory(new URL(jar).getPath(), new ZipEntry(entry));
			}
    	}
		
		return null;
	}
    
    public static java.io.InputStream getInputStream(File f) throws IOException
	{
		if(f instanceof JarAsDirectory)
		{
			return new JarFile(((JarAsDirectory)f).getJarPath()).getInputStream(((JarAsDirectory)f).getZipEntry());
		}
		else
		{
			return new FileInputStream(f);
		}
	}
    
    public static URL getURL(ClassLoader cl, String path)
	{
		URL ret = cl.getResource(path);
		//System.out.println("Loaded: "+path+" result: "+ret);
		return ret;
	}

    /**
	 *  Convert the publish id to uri.
	 */
	public URI convertUri(String pid)
	{
		try
		{
			return new URI(pid.replace("[", "").replace("]", ""));
		}
		catch(Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
}

