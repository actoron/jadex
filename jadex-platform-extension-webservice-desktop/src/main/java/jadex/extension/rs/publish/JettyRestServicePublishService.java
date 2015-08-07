package jadex.extension.rs.publish;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.servlet.AsyncContext;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
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
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.extension.rs.publish.JettyRestServicePublishService.MappingInfo.HttpMethod;
import jadex.extension.rs.publish.annotation.ParametersMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;
import jadex.extension.rs.publish.mapper.DefaultParameterMapper;
import jadex.extension.rs.publish.mapper.IParameterMapper;
import jadex.extension.rs.publish.mapper.IValueMapper;
import jadex.javaparser.SJavaParser;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

/**
 *  Publish service without Jersey directly using Jetty container.
 *  
 *  todo: make abstract base class without Jetty deps
 */
@Service
public class JettyRestServicePublishService implements IWebPublishService
{
	// Hack constant for enabling multi-part :-(
	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

	/** Some basic media types for service invocations. */
	public static List<String> PARAMETER_MEDIATYPES = Arrays.asList(new String[]{MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML});
	
    /** The component. */
    @ServiceComponent
    protected IInternalAccess component;

    /** The servers per service id. */
    protected Map<IServiceIdentifier, Server> sidservers;

    /** The servers per port. */
    protected Map<Integer, Server> portservers;
    
    /** The results per call. */
    protected MultiCollection<String, ResultInfo> resultspercall = new MultiCollection<String, ResultInfo>();
    
    /** The requests per call. */
    protected MultiCollection<String, AsyncContext> requestspercall = new MultiCollection<String, AsyncContext>();

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
            Server server = getHttpServer(uri, info);
            System.out.println("Adding http handler to server: "+uri.getPath());

            ContextHandlerCollection collhandler = (ContextHandlerCollection)server.getHandler();

            final MultiCollection<String, MappingInfo> mappings = evaluateMapping(service.getServiceIdentifier(), info);

            ContextHandler ch = new ContextHandler()
            {
                public void doHandle(String target, Request baseRequest, final HttpServletRequest request, final HttpServletResponse response)
                    throws IOException, ServletException
                {
                    System.out.println("handler is: "+uri.getPath());

                    // check if call is an intermediate result fetch
                    String callid = request.getHeader("x-jadex-callid");
                    if(resultspercall.containsKey(callid))
                    {
                    	Collection<ResultInfo> results = resultspercall.get(callid);
                    	if(results.size()>0)
                    	{
                    		ResultInfo result = results.iterator().next();
                    		resultspercall.removeObject(callid, result);
                    		writeResponse(result.getResult(), callid, result.getMappingInfo(), request, response);
                    	}
                    	else
                    	{
                    		AsyncContext ctx = request.startAsync();
                    		requestspercall.add(callid, ctx);
//                    		System.out.println("added context: "+callid+" "+ctx);
                    	}
                    }
                    else if(requestspercall.containsKey(callid))
                    {
                    	AsyncContext ctx = request.startAsync();
                		requestspercall.add(callid, ctx);
//                		System.out.println("added context: "+callid+" "+ctx);
                    }
                    else if(callid!=null)
                    {
                    	writeResponse("Unknown callid", null, null, request, response);
                    }
                    // handle new call
                    else
                    {
	                    // Hack to enable multi-part
	                    // http://dev.eclipse.org/mhonarc/lists/jetty-users/msg03294.html
	                    if(request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) 
	                    	baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
	                    
	                    String methodname = request.getPathInfo();
	
	                    if(methodname.startsWith("/"))
	                        methodname = methodname.substring(1);
	
	                    if(mappings.containsKey(methodname))
	                    {
	                        try
	                        {
	//                          out.println("<h1>" + "Found method - calling service: " + mappings.get(methodname).getMethod().getName() + "</h1>");
	                            Collection<MappingInfo> mis = mappings.get(methodname);
	                            
	                            // convert and map parameters
	                            Tuple2<MappingInfo, Object[]> tup = mapParameters(request, mis);
	                            final MappingInfo mi = tup.getFirstEntity();
	                            Object[] params = tup.getSecondEntity();
	                            
	                            // invoke the service method
	                            final Method method = mi.getMethod();
	                        	
	                        	Object ret = method.invoke(service, params);
	                        
	//                          if(ret instanceof IFuture)
	//                          	ret = ((IFuture<?>)ret).get(Starter.getLocalDefaultTimeout(null));
		                        if(ret instanceof IIntermediateFuture)
		                        {
		                        	final AsyncContext ctx = request.startAsync();
		                        	final String fcallid = SUtil.createUniqueId(methodname);
		                        	requestspercall.add(fcallid, ctx);
//		                        	System.out.println("added context: "+fcallid+" "+ctx);
		                        	
		                        	((IIntermediateFuture<Object>)ret).addIntermediateResultListener(new IIntermediateResultListener<Object>()
									{
		                        		boolean first = true;
		                        		List<String> sr = null;
		                        		public void resultAvailable(Collection<Object> result)
		                        		{
		                        			for(Object res: result)
		                        			{
		                        				intermediateResultAvailable(result);
		                        			}
		                        			ctx.complete();
		                        		}
		                        		
		                        		public void exceptionOccurred(Exception exception)
		                        		{
		                        			Object result = mapResult(method, exception);
		                        			writeResponse(result, null, mi, request, response);
		                        			requestspercall.remove(fcallid);
		                        		}
		                        		
		                        		public void intermediateResultAvailable(Object result)
		                        		{
		                        			result = mapResult(method, result);
		                        			
		                        			if(requestspercall.containsKey(fcallid))
		                        			{
		                        				AsyncContext ctx = requestspercall.get(fcallid).iterator().next();
		                        				requestspercall.removeObject(fcallid, ctx);
//		                        				System.out.println("removed context: "+fcallid+" "+ctx);
		                        				writeResponse(result, fcallid, mi, (HttpServletRequest)ctx.getRequest(), (HttpServletResponse)ctx.getResponse());
		                        				ctx.complete();
		                        			}
		                        			else
		                        			{
		                        				resultspercall.add(fcallid, new ResultInfo(result, fcallid, mi));
		                        			}
		                        			
		                        			if(first)
		                        				first = false;
		                        			
//		                        			if(first)
//		                        			{
//	//	                        		        response.addHeader("Transfer-encoding", "chunked");
//		                        				sr = writeResponseHeader(result, mi, request, response);
//		                        				first = false;
//		                        			}
//		                        			writeResponseContent(result, request, response, sr);
		                        		}
		                        		
		                        	    public void finished()
		                        	    {
		                        	    	requestspercall.remove(fcallid);
		                        	    }
									});
		                        }
		                        else if(ret instanceof IFuture)
		                        {
		                        	final AsyncContext ctx = request.startAsync();
		                        	
		                        	((IFuture)ret).addResultListener(new IResultListener<Object>()
									{
		                        		public void resultAvailable(Object ret)
		                        		{
		                        			ret = mapResult(method, ret);
		                        			writeResponse(ret, null, mi, request, response);
		                        			ctx.complete();
		                        		}
		
		                        		public void exceptionOccurred(Exception exception)
		                        		{
		                        			Object result = mapResult(method, exception);
		                        			writeResponse(exception, null, mi, request, response);
		                        			ctx.complete();
		                        		}
									});
		                            ret = ((IFuture<?>)ret).get(Starter.getLocalDefaultTimeout(null));
		                        }
		                        else
		                        {
	//	                        	System.out.println("call finished: "+method.getName()+" paramtypes: "+SUtil.arrayToString(method.getParameterTypes())+" on "+service+" "+Arrays.toString(params));
		                        	// map the result by user defined mappers
		                        	ret = mapResult(method, ret);
		                        	// convert content and write result to servlet response
		                        	writeResponse(ret, null, mi, request, response);
		                        }
	                        }
	                        catch(Exception e)
	                        {
	                        	writeResponse(e, null, null, request, response);
	                        }
	                    }
		                else
		                {
		                    PrintWriter out = response.getWriter();
		                    
		                    response.setContentType("text/html; charset=utf-8");
		                    response.setStatus(HttpServletResponse.SC_OK);
		                
		                    String info = getServiceInfo(service, uri, mappings);
		                    out.write(info);
		                    
		//                      out.println("<h1>" + "Found no method mapping, available are: " + "</h1>");
		//                      out.println("<ul>");
		//                      for(Map.Entry<String, Collection<MappingInfo>> entry: mappings.entrySet())
		//                      {
		//                      	for(MappingInfo mi: entry.getValue())
		//                      	{
		//                      		out.println(entry.getKey()+" -> "+mi.getMethod().getName()+"<br/>");
		//                      	}
		//                      }
		//                      out.println("</ul>");
		//                      System.out.println(mappings);
		                }
                    }
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
    public Server getHttpServer(URI uri, PublishInfo info)
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

    /**
     *  Map the incoming uri/post/multipart parameters to the service target parameter types.
     */
    protected Tuple2<MappingInfo, Object[]> mapParameters(HttpServletRequest request, Collection<MappingInfo> mis) 
    {
    	try
    	{
	    	Object[] targetparams = null;
	    
	        MultiCollection<String, String> inparamsmap = null;
	        
	        // parameters for query string (must be parsed to keep order) and 
	        // posted form data not for multi-part
	        if(request.getQueryString()!=null)
	        {
	            inparamsmap = splitQueryString(request.getQueryString());
	        }
	
	        if(request.getContentType()!=null && request.getContentType().startsWith(MediaType.MULTIPART_FORM_DATA) && request.getParts().size()>0)
	        {
	            if(inparamsmap==null)
	                inparamsmap = new MultiCollection<String, String>(new LinkedHashMap<String, Collection<String>>(), ArrayList.class);
	            for(Part part: request.getParts())
	            {
	                byte[] data = SUtil.readStream(part.getInputStream());
	                inparamsmap.add(part.getName(), new String(data));
	            }
	        }
	        
	        MappingInfo mi = null;
	        if(mis.size()==1)
	        {
	        	mi = mis.iterator().next();
	        }
	        else 
	        {
	        	int psize = inparamsmap==null? 0: inparamsmap.size();
	        	for(MappingInfo tst: mis)
	        	{
	        		if(psize==tst.getMethod().getParameterTypes().length)
	        		{
	        			mi = tst;
	        			break;
	        		}
	        	}
	        }
	        
	        if(mi==null)
	        	throw new RuntimeException("No method mapping found.");
	        
	        Method method = mi.getMethod();
	        // target method types
	        Class<?>[] types = mi.getMethod().getParameterTypes();
	        
	        // acceptable media types for input
	    	String mts = request.getHeader("Content-Type");
	        List<String> cl = parseMimetypes(mts);
	        List<String> sr = mi.getProducedMediaTypes();
	        if(sr==null || sr.size()==0)
	        {
	            sr = cl;
	        }
	        else
	        {
	            sr.retainAll(cl);
	        }
	
//	        if(sr.size()>0)
//	            System.out.println("found acceptable in types: "+sr);
	        if(sr.size()==0)
	            System.out.println("found no acceptable in types.");
	        
	        Object[] inparams = inparamsmap==null? SUtil.EMPTY_OBJECT_ARRAY: inparamsmap.getObjects();
	        
	        for(int i=0; i<inparams.length; i++)
	        {
	        	if(inparams[i] instanceof String)
	        		inparams[i] = convertParameter(sr, (String)inparams[i]);
	        }
	 
	        if(method.isAnnotationPresent(ParametersMapper.class))
	        {
	//          System.out.println("foundmapper");
	            ParametersMapper mm = method.getAnnotation(ParametersMapper.class);
	            if(!mm.automapping())
	            {
	                Class<?> pclazz = mm.value().clazz();
	                Object mapper;
	                if(!Object.class.equals(pclazz))
	                {
	                    mapper = pclazz.newInstance();
	                }
	                else
	                {
	                    mapper = SJavaParser.evaluateExpression(mm.value().value(), null);
	                }
	                if(mapper instanceof IValueMapper)
	                    mapper = new DefaultParameterMapper((IValueMapper)mapper);
	
	                targetparams = ((IParameterMapper)mapper).convertParameters(inparams, request);
	            }
	            else
	            {
	//           	System.out.println("automapping detected");
	                Class<?>[] ts = method.getParameterTypes();
	                targetparams = new Object[ts.length];
	                if(ts.length==1 && inparamsmap!=null)
	                {
	                    if(SReflect.isSupertype(ts[0], Map.class))
	                    {
	                        targetparams[0] = inparamsmap;
	                        ((Map)targetparams[0]).putAll(SInvokeHelper.extractCallerValues(request));
	                    }
	//                    else if(SReflect.isSupertype(ts[0], MultivaluedMap.class))
	//                    {
	//                        targetparams[0] = SInvokeHelper.convertMultiMap(inparamsmap);
	//                        ((Map)targetparams[0]).putAll(SInvokeHelper.extractCallerValues(request));
	//                    }
	                }
	            }
	        }
	        // natural auto map if there are in parameters
	        else 
	        {
	        	Class<?>[] ts = method.getParameterTypes();
	            targetparams = new Object[ts.length];
	            
	            for(int i=0; i<targetparams.length && i<inparams.length; i++)
	            {
	            	Object p = inparams[i];
	            	if(p!=null && SReflect.isSupertype(ts[i], p.getClass()))
	            	{
	            		targetparams[i] = p;
	            	}
	            	else if(p instanceof String && BasicTypeConverter.isBuiltInType(ts[i]))
	            	{
	            		targetparams[i] = BasicTypeConverter.getBasicStringConverter(ts[i]).convertString((String)p, null);
	            	}
	            }
	        }
	
	        return new Tuple2<MappingInfo, Object[]>(mi, targetparams);
    	}
    	catch(Exception e)
    	{
    		throw new RuntimeException(e);
    	}
    }

    /**
     *  Convert a parameter string to an object if is json or xml.
     *  @param sr The media types.
     *  @param val The string value.
     *  @return The decoded object.
     */
    protected Object convertParameter(List<String> sr, String val)
    {
    	Object ret = val;
        boolean done = false;
        
		if(sr!=null && sr.contains(MediaType.APPLICATION_JSON))
        {
        	try
        	{
        		ret = JsonTraverser.objectFromByteArray(val.getBytes(), component.getClassLoader(), null);
        		done = true;
        	}
        	catch(Exception e)
        	{
        	}
        }
        
        if(!done && sr!=null && sr.contains(MediaType.APPLICATION_XML))
        {
        	try
        	{
        		ret = JavaReader.objectFromByteArray(val.getBytes(), component.getClassLoader(), null);
        		done = true;
        	}
        	catch(Exception e)
        	{
        	}
        }
        
        return ret;
    }
    
    /**
     *  Map a result using the result mapper.
     */
    protected Object mapResult(Method method, Object ret)
    {
        if(method.isAnnotationPresent(ResultMapper.class))
        {
            try
            {
                ResultMapper mm = method.getAnnotation(ResultMapper.class);
                Class<?> pclazz = mm.value().clazz();
                IValueMapper mapper;
    //            System.out.println("res mapper: "+clazz);
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
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        }
//        else
//        {
//            NativeResponseMapper mapper = new NativeResponseMapper();
//            ret = mapper.convertValue(ret);
//        }

        return ret;
    }

   /**
    *
    */
   protected void writeResponse(Object result, String callid, MappingInfo mi, HttpServletRequest request, HttpServletResponse response) 
   {
	   List<String> sr = writeResponseHeader(result, callid, mi, request, response);
	   writeResponseContent(result, request, response, sr);
   }
    
    /**
     *
     */
    protected List<String> writeResponseHeader(Object ret, String callid, MappingInfo mi, HttpServletRequest request, HttpServletResponse response) 
    {
    	List<String> sr =  null;
    	
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
        	String mts = request.getHeader("Accept");
            List<String> cl = parseMimetypes(mts);
            sr = mi==null? null: mi.getProducedMediaTypes();
            if(sr==null || sr.size()==0)
            {
                sr = cl;
            }
            else
            {
                sr.retainAll(cl);
            }

            if(sr.size()==0)
                System.out.println("found no acceptable return types.");
            
            if(callid!=null)
            	response.addHeader("x-jadex-callid", callid);
            
            // todo: add option for CORS
            response.addHeader("Access-Control-Allow-Origin", "*");
            // http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
            response.addHeader("Access-Control-Allow-Credentials", "true ");
            response.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
        }
    	
    	return sr; 
    }
    
    /**
     *
     */
    protected void writeResponseContent(Object result, HttpServletRequest request, HttpServletResponse response, List<String> sr) 
    {
    	try
    	{
	        // handle content
	        PrintWriter out = response.getWriter();
	        if(result!=null)
	        {
	            // for testing with browser
	            // http://brockallen.com/2012/04/27/change-firefoxs-default-accept-header-to-prefer-json-over-xml/
	
	            if(sr!=null && sr.contains(MediaType.APPLICATION_JSON))
	            {
	                byte[] data = JsonTraverser.objectToByteArray(result, component.getClassLoader());
	                if(response.getHeader("Content-Type")==null)
	                	response.setHeader("Content-Type", MediaType.APPLICATION_JSON);
	                out.write(new String(data));
	            }
	            else if(sr!=null && sr.contains(MediaType.APPLICATION_XML))
	            {
	            	byte[] data = JavaWriter.objectToByteArray(result, component.getClassLoader());
	            	if(response.getHeader("Content-Type")==null)
	            		response.setHeader("Content-Type", MediaType.APPLICATION_XML);
	          
	            	// this code below writes <?xml... prolog only once>
//	            	byte[] data;
//	            	if(response.getHeader("Content-Type")==null)
//	            	{
//	            		response.setHeader("Content-Type", MediaType.APPLICATION_XML);
//	            		data = JavaWriter.objectToByteArray(result, component.getClassLoader());
//	            	}
//	            	else
//	            	{
//	            		// write without xml prolog
//	            		data = JavaWriter.objectToByteArray(result, null, component.getClassLoader(), null);
//	            	}
	            	out.write(new String(data));
	            }
	            else if(SReflect.isStringConvertableType(result.getClass()))
	            {
	            	if(response.getHeader("Content-Type")==null)
	            		response.setContentType("text/plain; charset=utf-8");
	                out.write(result.toString());
	            }
	            else if(sr!=null && sr.contains("*/*"))
	            {
	                // use json if all is allowed
	            	if(response.getHeader("Content-Type")==null)
	                 	response.setHeader("Content-Type", MediaType.APPLICATION_JSON);
	                byte[] data = JsonTraverser.objectToByteArray(result, component.getClassLoader());
	                out.write(new String(data));
	            }
	            else
	            {
	                System.out.println("cannot convert result: "+result);
	            }
	            
	            out.flush();
	        }
    	}
    	catch(Exception e)
    	{
    		throw new RuntimeException(e);
    	}
    }
    
    /**
     *  todo: make statically accessible
     *  Copied from Jadex ForwardFilter
     */
    public static List<String> parseMimetypes(String mts)
    {
//        List<String> mimetypes = null;
        List<String> mimetypes = new ArrayList<String>();
        if(mts!=null)
        {
//            mimetypes = new ArrayList<String>();
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
     *  Split the query and save the order.
     */
//    public static Map<String, String> splitQueryString(String query)
    public static MultiCollection<String, String> splitQueryString(String query) throws Exception
    {
        MultiCollection<String, String> ret = new MultiCollection<String, String>(new LinkedHashMap<String, Collection<String>>(), ArrayList.class);
//        Map<String, String> ret = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for(String pair : pairs)
        {
            int idx = pair.indexOf("=");
            ret.add(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return ret;
    }

    /**
     *  Evaluate the service interface and generate mappings.
     *  Return a multicollection in which for each path name the possible
     *  methods are contained (can be more than one due to different parameters).
     */
    public MultiCollection<String, MappingInfo> evaluateMapping(IServiceIdentifier sid, PublishInfo pi)
    {
        Class<?> mapcl = pi.getMapping()==null? null: pi.getMapping().getType(component.getClassLoader());
        if(mapcl==null)
            mapcl = sid.getServiceType().getType(component.getClassLoader());

        MultiCollection<String, MappingInfo> ret = new MultiCollection<String, MappingInfo>();
        MultiCollection<String, MappingInfo> natret = new MultiCollection<String, MappingInfo>();

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

//                // Jadex specific annotations
//                if(m.isAnnotationPresent(ResultMapper.class))
//                {
//
//                }

                mi.setMethod(m);
                ret.add(mi.getPath(), mi);
            }

            // Natural mapping using simply all declared methods
            natret.add(m.getName(), new MappingInfo(null, m, m.getName()));
        }

        return ret.size()>0? ret: natret;
    }

    /**
	 *  Functionality blueprint for get service info web method.
	 *  Creates a html page with css for style and javascript for ajax post requests.
	 *  The service info site contains a section for each published method. 
	 *  @param params The parameters.
	 *  @return The result.
	 */
	public String getServiceInfo(Object service, URI baseuri, MultiCollection<String, MappingInfo> mappings)
	{
		StringBuffer ret = new StringBuffer();
		
		try
		{
			String functionsjs;
			String stylecss;
			Scanner sc = null;
			try
			{
				InputStream is = SUtil.getResource0("jadex/extension/rs/publish/functions.js", 
					component.getClassLoader());
				sc = new Scanner(is);
				functionsjs = sc.useDelimiter("\\A").next();
//					System.out.println(functionsjs);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			finally
			{
				if(sc!=null)
				{
					sc.close();
				}
			}
			
			try
			{
				InputStream is = SUtil.getResource0("jadex/extension/rs/publish/style.css", 
					component.getClassLoader());
				sc = new Scanner(is);
				stylecss = sc.useDelimiter("\\A").next();
				
				String	stripes	= SUtil.loadBinary("jadex/extension/rs/publish/jadex_stripes.png");
				stylecss	= stylecss.replace("$stripes", stripes);
				
//				System.out.println(functionsjs);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			finally
			{
				if(sc!=null)
				{
					sc.close();
				}
			}
			
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
			String ifacename = ((IService)service).getServiceIdentifier().getServiceType().getTypeName();
			ret.append(SReflect.getUnqualifiedTypeName(ifacename));
			ret.append("</h1>");
			ret.append("\n");
			ret.append("</div>");
			ret.append("\n");

			ret.append("<div class=\"middle\">");
			ret.append("\n");
			
//			Class<?> clazz = service.getClass();
//			List<Method> methods = new ArrayList<Method>();
//			while(!clazz.equals(Object.class))
//			{
//				List<Method> l = SUtil.arrayToList(clazz.getDeclaredMethods());
//				methods.addAll(l);
//				clazz = clazz.getSuperclass();
//			}

//			Collections.sort(mappings, new MethodComparator());
			
			if(mappings!=null)
			{
				for(MappingInfo mi: (MappingInfo[])mappings.getObjects(MappingInfo.class))
				{
					Method method = mi.getMethod();
					HttpMethod restmethod = mi.getHttpMethod()!=null? mi.getHttpMethod(): guessRestType(method);
					
					String path = mi.getPath()!=null? mi.getPath(): method.getName();
					List<String> consumed = mi.getConsumedMediaTypes();
					List<String> produced = mi.getProducedMediaTypes();
					
					// Use defaults if nothing is given
					if(consumed==null)
						consumed = PARAMETER_MEDIATYPES;
					if(produced==null)
						produced = PARAMETER_MEDIATYPES;
					
					Class<?>[] ptypes = method.getParameterTypes();
					
					ret.append("<div class=\"method\">");
					ret.append("\n");
					
					ret.append("<div class=\"methodname\">");
//					ret.append("<i><b>");
					ret.append(method.getName());
//					ret.append("</b></i>");
					
					ret.append("(");
					if(ptypes!=null && ptypes.length>0)
					{
						for(int j=0; j<ptypes.length; j++)
						{
							ret.append(SReflect.getUnqualifiedClassName(ptypes[j]));
							if(j+1<ptypes.length)
								ret.append(", ");
						}
					}
					ret.append(")");
					ret.append("</div>");
					ret.append("\n");
//					ret.append("</br>");
					
					ret.append("<div class=\"restproperties\">");
					ret.append("<div id=\"httpmethod\">").append(restmethod).append("</div>");
					
					if(consumed!=null && consumed.size()>0) 
					{
						ret.append("<i>");
						
						if(consumed!=PARAMETER_MEDIATYPES)
							ret.append("Consumes: ");
						else
							ret.append("Consumes [not declared by the service]: ");
						ret.append("</i>");
						for(int j=0; j<consumed.size(); j++)
						{
							ret.append(consumed.get(j));
							if(j+1<consumed.size())
								ret.append(" ,");
						}
						ret.append(" ");
					}
					
					if(produced!=null && produced.size()>0)
					{
						ret.append("<i>");
						ret.append("Produces: ");
						if(produced!=PARAMETER_MEDIATYPES)
							ret.append("Produces: ");
						else
							ret.append("Produces [not declared by the service]: ");
						ret.append("</i>");
						for(int j=0; j<produced.size(); j++)
						{
							ret.append(produced.get(j));
							if(j+1<produced.size())
								ret.append(" ,");
						}
						ret.append(" ");
					}
//						ret.append("</br>");
					ret.append("</div>");
					ret.append("\n");

					String link = baseuri.toString();
					if(path!=null)
						link = link+"/"+path; 
//					System.out.println("path: "+link);
					
//					if(ptypes.length>0)
//					{
						ret.append("<div class=\"servicelink\">");
						ret.append(link);
						ret.append("</div>");
						ret.append("\n");
						
						// For post set the media type of the arguments.
						ret.append("<form class=\"arguments\" action=\"").append(link).append("\" method=\"")
							.append(restmethod).append("\" enctype=\"multipart/form-data\" ");
						
//						if(restmethod.equals(HttpMethod.POST))
							ret.append("onSubmit=\"return extract(this)\"");
						ret.append(">");
						ret.append("\n");
						
						for(int j=0; j<ptypes.length; j++)
						{
							ret.append("arg").append(j).append(": ");
							ret.append("<input name=\"arg").append(j).append("\" type=\"text\" />");
//							.append(" accept=\"").append(cons[0]).append("\" />");
						}
						
						ret.append("<select name=\"mediatype\">");
						if(consumed!=null && consumed.size()>0)
						{
//							ret.append("<select name=\"mediatype\">");
							for(int j=0; j<consumed.size(); j++)
							{
								// todo: hmm? what about others?
								if(!MediaType.MULTIPART_FORM_DATA.equals(consumed.get(j)) &&
									!MediaType.APPLICATION_FORM_URLENCODED.equals(consumed.get(j)))
								{
									ret.append("<option>").append(consumed.get(j)).append("</option>");
								}
							}
						}
						else
						{
							ret.append("<option>").append(MediaType.TEXT_PLAIN).append("</option>");
						}
						ret.append("</select>");
						ret.append("\n");
						
						ret.append("<input type=\"submit\" value=\"invoke\"/>");
						ret.append("</form>");
						ret.append("\n");
//					}
//					else
//					{
//						ret.append("<div class=\"servicelink\">");
//						ret.append("<a href=\"").append(link).append("\">").append(link).append("</a>");
//						ret.append("</div>");
//						ret.append("\n");
//					}
					
					ret.append("</div>");
					ret.append("\n");
				}
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
	
	/**
	 *  Guess the http type (GET, POST, PUT, DELETE, ...) of a method.
	 *  @param method The method.
	 *  @return  The rs annotation of the method type to use 
	 */
	public HttpMethod guessRestType(Method method)
	{
	    // Retrieve = GET (hasparams && hasret)
	    // Update = POST (hasparams && hasret)
	    // Create = PUT  return is pointer to new resource (hasparams? && hasret)
	    // Delete = DELETE (hasparams? && hasret?)

		HttpMethod ret = HttpMethod.GET;
		
		Class<?> rettype = SReflect.unwrapGenericType(method.getGenericReturnType());
		Class<?>[] paramtypes = method.getParameterTypes();
		
		boolean hasparams = paramtypes.length>0;
		boolean hasret = rettype!=null && !rettype.equals(Void.class) && !rettype.equals(void.class);
		
		// GET or POST if has both
		if(hasret)
		{
			if(hasparams)
			{
				if(hasStringConvertableParameters(method, rettype, paramtypes))
				{
					ret = HttpMethod.GET;
				}
				else
				{
					ret = HttpMethod.POST;
				}
			}
		}
		
		// todo: other types?
		
//		System.out.println("rest-type: "+ret.getName()+" "+method.getName()+" "+hasparams+" "+hasret);
		
		return ret;
//		return GET.class;
	}
	
	/**
	 *  Test if a method has parameters that are all convertible from string.
	 *  @param method The method.
	 *  @param rettype The return types (possibly unwrapped from future type).
	 *  @param paramtypes The parameter types.
	 *  @return True, if is convertible.
	 */
	public boolean hasStringConvertableParameters(Method method, Class<?> rettype, Class<?>[] paramtypes)
	{
		boolean ret = true;
		
		for(int i=0; i<paramtypes.length && ret; i++)
		{
			ret = SReflect.isStringConvertableType(paramtypes[i]);
		}
		
		return ret;
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
            return producedtypes;//==null? Collections.EMPTY_LIST: producedtypes;
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
            return consumedtypes;//==null? Collections.EMPTY_LIST: consumedtypes;
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
    
    public static class ResultInfo
    {
    	protected String callid;
    	protected Object result;
    	protected MappingInfo mappingInfo;
    	
    	/**
    	 * 
    	 */
		public ResultInfo(Object result, String callid, MappingInfo mappingInfo)
		{
			this.result = result;
			this.callid = callid;
			this.mappingInfo = mappingInfo;
		}
		
		/**
		 *  Get the result. 
		 *  @return The result
		 */
		public Object getResult()
		{
			return result;
		}
		
		/**
		 *  Set the result.
		 *  @param result The result to set
		 */
		public void setResult(Object result)
		{
			this.result = result;
		}

		/**
		 *  Get the callid. 
		 *  @return The callid
		 */
		public String getCallid()
		{
			return callid;
		}

		/**
		 *  Set the callid.
		 *  @param callid The callid to set
		 */
		public void setCallid(String callid)
		{
			this.callid = callid;
		}

		/**
		 *  Get the mappingInfo. 
		 *  @return The mappingInfo
		 */
		public MappingInfo getMappingInfo()
		{
			return mappingInfo;
		}

		/**
		 *  Set the mappingInfo.
		 *  @param mappingInfo The mappingInfo to set
		 */
		public void setMappingInfo(MappingInfo mappingInfo)
		{
			this.mappingInfo = mappingInfo;
		}
		
    }
    
}

