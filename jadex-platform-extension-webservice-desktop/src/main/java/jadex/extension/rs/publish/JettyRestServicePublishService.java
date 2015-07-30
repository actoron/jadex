package jadex.extension.rs.publish;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.extension.rs.publish.JettyRestServicePublishService.MappingInfo.HttpMethod;
import jadex.extension.rs.publish.annotation.ResultMapper;
import jadex.extension.rs.publish.mapper.IValueMapper;
import jadex.extension.rs.publish.mapper.NativeResponseMapper;
import jadex.javaparser.SJavaParser;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.xml.bean.JavaWriter;

/**
 * 
 */
@Service
public class JettyRestServicePublishService implements IWebPublishService
{
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;
	
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
	public IFuture<Void> publishService(ClassLoader cl, IService service, final PublishInfo pi)
	{
		final Future<Void> ret = new Future<Void>();
		
		System.out.println("start publish: "+pi.getPublishId());
		ret.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				System.out.println("end publish: "+pi.getPublishId());
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
		
		try
		{
			URI uri = new URI(pi.getPublishId());
			internalPublishService(uri, service, pi);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ret.setException(e);
		}
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	public void internalPublishService(final URI uri, final IService service, PublishInfo info)
	{
		try
		{
			Server server = getHttpServer(uri, info);
			System.out.println("Adding http handler to server: "+uri.getPath());
			
			ContextHandlerCollection collhandler = (ContextHandlerCollection)server.getHandler();
			
			final Map<String, MappingInfo> mappings = evaluateMapping(service.getServiceIdentifier(), info);
			
			ContextHandler ch = new ContextHandler()
			{
				 public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) 
				    throws IOException, ServletException
			    {
			    	System.out.println("handler is: "+uri.getPath());
			    	
			    	String methodname = request.getPathInfo();
			    	
			    	if(methodname.startsWith("/"))
			    		methodname = methodname.substring(1);
			    	
			    	if(mappings.containsKey(methodname))
			    	{
//			    		out.println("<h1>" + "Found method - calling service: " + mappings.get(methodname).getMethod().getName() + "</h1>");
			    		try
			    		{
			    			MappingInfo mi = mappings.get(methodname);
			    			Object[] params = computeParameters(mi);
			    			Method method = mi.getMethod();
			    			Object ret = method.invoke(service, params);
			    			
			    			if(ret instanceof IFuture)
			    				ret = ((IFuture<?>)ret).get(Starter.getLocalDefaultTimeout(null)); 
			    			System.out.println("call finished: "+method.getName()+" paramtypes: "+SUtil.arrayToString(method.getParameterTypes())+" on "+service+" "+Arrays.toString(params));
			    			
			    			if(method.isAnnotationPresent(ResultMapper.class))
			    			{
			    				ResultMapper mm = method.getAnnotation(ResultMapper.class);
			    				Class<?> pclazz = mm.value().clazz();
			    				IValueMapper mapper;
//			    				System.out.println("res mapper: "+clazz);
			    				if(!Object.class.equals(pclazz))
			    				{
			    					mapper = (IValueMapper)pclazz.newInstance();
			    				}
			    				else
			    				{
			    					mapper = (IValueMapper)SJavaParser.evaluateExpression(mm.value().value(), null);
			    				}
			    				
			    				ret = mapper.convertValue(ret);
			    			}
//			    			else
//			    			{
//			    				NativeResponseMapper mapper = new NativeResponseMapper();
//			    				ret = mapper.convertValue(ret);
//			    			}

			    			// handle rs-response object by copying to response
//			    			Response resp = ret instanceof Response? (Response)ret: null;
//			    			if(resp==null)
//			    			{
//				    			ResponseBuilder rb = Response.ok(ret);
//						        rb.type(MediaType.APPLICATION_JSON_TYPE);
//						        rb.encoding("utf-8");
////						        ContentType("text/html; charset=utf-8");
//						        resp = rb.build();
//			    			}
			    			
			    			Object content = ret;
			    			List<String> sr =  null;
			    			
			    			// copy values from response to http response
			    			if(ret instanceof Response)
			    			{
			    				Response resp = (Response)ret;
			    					
			    				response.setStatus(resp.getStatus());
		    				
			    				for(String name: resp.getStringHeaders().keySet())
			    				{
			    					response.addHeader(name, resp.getHeaderString(name));
			    				}
			    				
			    				ret = resp.getEntity();
			    				if(resp.getMediaType()!=null)
			    				{
			    					sr = new ArrayList<String>();
			    					sr.add(resp.getMediaType().toString());
			    				}
			    			}
			    			else
			    			{
				    			// acceptable media types for response
			    				List<String> cl = parseMimetypes(request);
			    				sr = mi.getProducedMediaTypes(); 
			    				if(sr==null || sr.size()==0)
			    				{
			    					sr = cl;
			    				}
			    				else
			    				{
			    					sr.retainAll(cl);
			    				}
			    				
			    				if(sr.size()>0)
			    				{
			    					System.out.println("found acceptable return types: "+sr);
			    				}
			    				else
			    				{
			    					System.out.println("found no acceptable return types.");
			    				}
			    				
			    				// todo: add option for CORS
			    				response.addHeader("Access-Control-Allow-Origin", "*");
			 		    		// http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
			 					response.addHeader("Access-Control-Allow-Credentials", "true ");
			 					response.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
			 					response.addHeader("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
			    			}
			    			
			    			// handle content 
			    			PrintWriter out = response.getWriter();
		    				if(content!=null)
		    				{
			    				if(sr!=null && sr.contains(MediaType.APPLICATION_JSON))
			    				{
			    					byte[] data = JsonTraverser.objectToByteArray(content, component.getClassLoader());
			    					out.write(new String(data));
			    				}
			    				else if(sr!=null && sr.contains(MediaType.APPLICATION_XML))
			    				{
			    					byte[] data = JavaWriter.objectToByteArray(content, component.getClassLoader());
			    					out.write(new String(data));
			    				}
			    				else if(SReflect.isStringConvertableType(content.getClass()))
			    				{
			    					response.setContentType("text/plain; charset=utf-8");
			    					out.write(content.toString());
			    				}
			    				else if(sr!=null && sr.contains("*/*"))
			    				{
			    					// use json if all is allowed
			    					byte[] data = JsonTraverser.objectToByteArray(content, component.getClassLoader());
			    					out.write(new String(data));
			    				}
			    				else
			    				{
			    					System.out.println("cannot convert result: "+ret);
			    				}
		    				}
			    		}
			    		catch(Exception e)
			    		{
			    			e.printStackTrace();
			    		}
			    	}
			    	else
			    	{
			    		PrintWriter out = response.getWriter();
			    		out.println("<h1>" + "Found no method mapping, available are: " + "</h1>");
			    		out.println("<ul>");
			    		for(Map.Entry<String, MappingInfo> entry: mappings.entrySet())
			    		{
			    			out.println(entry.getKey()+" -> "+entry.getValue().getMethod().getName()+"<br/>");
			    		}
			    		out.println("</ul>");
			    		System.out.println(mappings);
			    		response.setContentType("text/html; charset=utf-8");
			    		response.setStatus(HttpServletResponse.SC_OK);
			    	}
			    	
			    	baseRequest.setHandled(true);
			    	
//			    	Map<String, String[]> params = request.getParameterMap();
//			    	
//			    	if(params!=null && params.containsKey("names"))
//			    	{
//			    		String[] names = params.get("names"); // seems to autoconvert the string array
//			    		
////					    ObjectMapper mapper = new ObjectMapper();
//			    		
//			    		String name = params.get("name")[0];
////					    Car car = mapper.readValue(params.get("car")[0], Car.class);
//	    		
////					    for(String name: names)
////					    {
////					    	Object names = mapper.readValue(params.get(name), .class);
////					    }
//			    		
//			    		System.out.println(names);
//			    	}
//			    	
//			    	// http://apache-sling.73963.n3.nabble.com/Lost-parameter-order-for-form-POSTs-td4030212.html
//			    	Enumeration<String> names = request.getParameterNames();
//			    	while(names!=null && names.hasMoreElements())
//			    		System.out.println(names.nextElement());
//			    	
//			        response.setContentType("text/html; charset=utf-8");
//					
//			        response.addHeader("Access-Control-Allow-Origin", "*");
//		    		// http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
//					response.addHeader("Access-Control-Allow-Credentials", "true ");
//					response.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
//					response.addHeader("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
//
//					response.setStatus(HttpServletResponse.SC_OK);
//			 
//			        PrintWriter out = response.getWriter();
//			 
//			        out.println("<h1>" + "Hello World" + "</h1>");
//			 
//			        baseRequest.setHandled(true);
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
	}
	
	/**
	 *  Get or start an api to the http server.
	 */
	public Server getHttpServer(URI uri, PublishInfo info)
	{
		Server server = null;
		
		try
		{
//			URI baseuri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
			server = portservers==null? null: portservers.get(uri.getPort());
			
			if(server==null)
			{
				System.out.println("Starting new server: "+uri.getPort());
				server = new Server(uri.getPort());
			    server.dumpStdErr();
			    
			    ContextHandlerCollection collhandler = new ContextHandlerCollection();
			    server.setHandler(collhandler);
			  
				server.start();
//			    server.join();
				
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
	
	/**
	 * 
	 */
	protected Object[] computeParameters(MappingInfo mi)
	{
		Class<?>[] types = mi.getMethod().getParameterTypes();
		Object[] ret = new Object[types.length];
		return ret;
	}
	
	/**
	 *  todo: make statically accessible
	 *  Copied from Jadex ForwardFilter
	 */
	protected List<String> parseMimetypes(HttpServletRequest request)
	{
		List<String> mimetypes = null;
		String mts = request.getHeader("Accept");
		if(mts!=null)
		{
			mimetypes = new ArrayList<String>();
			StringTokenizer stok = new StringTokenizer(mts, ",");
			while(stok.hasMoreTokens())
			{
				String tok = stok.nextToken();
				StringTokenizer substok = new StringTokenizer(tok, ";");
				String mt = substok.nextToken();
				if(mimetypes==null)
				{
					mimetypes = new ArrayList<String>();
				}
				mimetypes.add(mt);
			}
		}
		return mimetypes;
	}
	
	/**
	 * 
	 */
	public Map<String, MappingInfo> evaluateMapping(IServiceIdentifier sid, PublishInfo pi)
	{
		Class<?> mapcl = pi.getMapping()==null? null: pi.getMapping().getType(component.getClassLoader());
		if(mapcl==null)
			mapcl = sid.getServiceType().getType(component.getClassLoader());
		
		Map<String, MappingInfo> ret = new HashMap<String, MappingInfo>(); 
		Map<String, MappingInfo> natret = new HashMap<String, MappingInfo>(); 
		
		for(Method m: SReflect.getAllMethods(mapcl))
		{
			MappingInfo mi = new MappingInfo();
			if(m.isAnnotationPresent(GET.class))
			{
				mi.setHttpMethod(HttpMethod.GET);
			}
			else if(m.isAnnotationPresent(POST.class))
			{
				mi.setHttpMethod(HttpMethod.POST);
			}
			else if(m.isAnnotationPresent(PUT.class))
			{
				mi.setHttpMethod(HttpMethod.PUT);
			}
			else if(m.isAnnotationPresent(DELETE.class))
			{
				mi.setHttpMethod(HttpMethod.DELETE);
			}
			else if(m.isAnnotationPresent(OPTIONS.class))
			{
				mi.setHttpMethod(HttpMethod.OPTIONS);
			}
			else if(m.isAnnotationPresent(HEAD.class))
			{
				mi.setHttpMethod(HttpMethod.HEAD);
			}
			
			if(m.isAnnotationPresent(Path.class))
			{
				Path path = m.getAnnotation(Path.class);
				mi.setPath(path.value());
			}
			else if(!mi.isEmpty())
			{
				mi.setPath(m.getName());
			}
			
			if(!mi.isEmpty())
			{
				if(m.isAnnotationPresent(Consumes.class))
				{
					Consumes con = (Consumes)m.getAnnotation(Consumes.class);
					String[] types = con.value();
					for(String type: types)
					{
						mi.addConsumedMediaType(type);
					}
				}
				
				if(m.isAnnotationPresent(Produces.class))
				{
					Produces prod = (Produces)m.getAnnotation(Produces.class);
					String[] types = prod.value();
					for(String type: types)
					{
						mi.addProducedMediaType(type);
					}
				}
				
				// Jadex specific annotations
				
				if(m.isAnnotationPresent(ResultMapper.class))
				{
					
				}
				
				mi.setMethod(m);
				ret.put(mi.getPath(), mi);
			}
			
			// Natural mapping using simply all declared methods
			natret.put(m.getName(), new MappingInfo(null, m, m.getName()));
		}
		
		return ret.size()>0? ret: natret;
	}
	
	/**
	 * 
	 */
	public static class MappingInfo
	{
		public enum HttpMethod
		{
			GET,
			POST,
			PUT,
			DELETE,
			OPTIONS,
			HEAD
		}
		
		/** The http method. */
		protected HttpMethod httpmethod;

		/** The target method. */
		protected Method method;
		
		/** The url path. */
		protected String path;
		
		/** The accepted media types for the response. */
		protected List<String> producedtypes;
		
		/** The accepted media types for consumption. */
		protected List<String> consumedtypes;

		/**
		 *  Create a new mapping info.
		 */
		public MappingInfo()
		{
		}
		
		/**
		 *  Create a new mapping info.
		 */
		public MappingInfo(HttpMethod httpMethod, Method method, String path)
		{
			this.httpmethod = httpMethod;
			this.method = method;
			this.path = path;
		}
		
		/**
		 *  Get the httpMethod. 
		 *  @return The httpMethod
		 */
		public HttpMethod getHttpMethod()
		{
			return httpmethod;
		}

		/**
		 *  Set the httpMethod.
		 *  @param httpMethod The httpMethod to set
		 */
		public void setHttpMethod(HttpMethod httpMethod)
		{
			this.httpmethod = httpMethod;
		}
		
		/**
		 *  Get the method. 
		 *  @return The method
		 */
		public Method getMethod()
		{
			return method;
		}

		/**
		 *  Set the method.
		 *  @param method The method to set
		 */
		public void setMethod(Method method)
		{
			this.method = method;
		}

		/**
		 *  Get the path. 
		 *  @return The path
		 */
		public String getPath()
		{
			return path;
		}

		/**
		 *  Set the path.
		 *  @param path The path to set
		 */
		public void setPath(String path)
		{
			this.path = path;
		}
		
		/**
		 *  Get the respmediatypes. 
		 *  @return The respmediatypes
		 */
		public List<String> getProducedMediaTypes()
		{
			return producedtypes==null? Collections.EMPTY_LIST: producedtypes;
		}

		/**
		 *  Set the response mediatypes.
		 *  @param respmediatypes The response mediatypes to set
		 */
		public void setProducedMediaTypes(List<String> respmediatypes)
		{
			this.producedtypes = respmediatypes;
		}
		
		/**
		 * 
		 */
		public void addProducedMediaType(String type)
		{
			if(producedtypes==null)
				producedtypes = new ArrayList<String>();
			producedtypes.add(type);
		}
		
		/**
		 *  Get the consumedmediatypes. 
		 *  @return The consumedtypes
		 */
		public List<String> getConsumedMediaTypes()
		{
			return consumedtypes==null? Collections.EMPTY_LIST: consumedtypes;
		}

		/**
		 *  Set the respmediatypes.
		 *  @param consumedtypes The consumedtypes to set
		 */
		public void setConsumedMediaTypes(List<String> respmediatypes)
		{
			this.consumedtypes = respmediatypes;
		}
		
		/**
		 * 
		 */
		public void addConsumedMediaType(String type)
		{
			if(consumedtypes==null)
				consumedtypes = new ArrayList<String>();
			consumedtypes.add(type);
		}

		/**
		 *  Test if has no settings.
		 */
		public boolean isEmpty()
		{
			return path==null && method==null && httpmethod==null;
		}
	}
}
