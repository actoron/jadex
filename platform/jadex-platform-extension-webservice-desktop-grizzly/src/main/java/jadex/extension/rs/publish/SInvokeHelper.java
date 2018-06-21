package jadex.extension.rs.publish;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.glassfish.grizzly.CompletionHandler;
import org.glassfish.grizzly.ReadHandler;
import org.glassfish.grizzly.http.io.NIOReader;
import org.glassfish.grizzly.http.multipart.MultipartEntry;
import org.glassfish.grizzly.http.multipart.MultipartEntryHandler;
import org.glassfish.grizzly.http.multipart.MultipartScanner;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.util.Constants;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ResourceConfig;

import jadex.base.Starter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.rs.publish.annotation.MethodMapper;
import jadex.extension.rs.publish.annotation.ParametersMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;
import jadex.extension.rs.publish.mapper.DefaultParameterMapper;
import jadex.extension.rs.publish.mapper.IParameterMapper;
import jadex.extension.rs.publish.mapper.IValueMapper;
import jadex.javaparser.SJavaParser;

/**
 * 
 */
public class SInvokeHelper 
{
	/**
	 *  Convert a multimap to normal map, flattening single values, if any.
	 */
	public static Map<String, Object> convertMultiMap(MultivaluedMap<String, String> vals)
	{
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		
		for(Map.Entry<String, List<String>> e: vals.entrySet())
		{
			List<String> val = e.getValue();
			if(val!=null && val.size()==1)
			{
				ret.put(e.getKey(), val.get(0));
			}
			else
			{
				ret.put(e.getKey(), val);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Convert a map with arrays to normal map where single values are flattened.
	 */
	public static Map<String, Object> convertMultiMap(Map<String, String[]> vals)
	{
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		
		for(Map.Entry<String, String[]> e: vals.entrySet())
		{
			String[] val = e.getValue();
			if(val!=null && val.length==1)
			{
				ret.put(e.getKey(), val[0]);
			}
			else
			{
				ret.put(e.getKey(), val);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Convert a map with arrays to normal map when no multi values exist.
	 */
	public static MultivaluedMap<String, String> convertToMultiMap(Map<String, String[]> vals)
	{
		MultivaluedMap<String, String> ret = new MultivaluedHashMap<String, String>();
		
		for(Map.Entry<String, String[]> e: vals.entrySet())
		{
			String[] val = e.getValue();
			
			if(val!=null && val.length>0)
			{
				for(String v: val)
				{
					ret.add(e.getKey(), v);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Extract caller values like ip and browser.
	 *  @param request The requrest.
	 *  @param vals The values.
	 */
	public static Map<String, String> extractCallerValues(Object request)
	{
		Map<String, String> ret = new HashMap<String, String>();
		
		// add request to map as internal parameter
		// cannot put request into map because map is cloned via service call
		if(request!=null)
		{
			if(request instanceof Request)
			{
				Request greq = (Request)request;
				ret.put("ip", greq.getRemoteAddr());
				ret.put("browser", greq.getHeader("User-Agent"));
				ret.put("querystring", greq.getQueryString());
			}
			else if(request instanceof HttpServletRequest)
			{
				HttpServletRequest sreq = (HttpServletRequest)request;
				ret.put("ip", sreq.getRemoteAddr());
				ret.put("browser", sreq.getHeader("User-Agent"));
				ret.put("querystring", sreq.getQueryString());
			}
		}
		
		return ret;
	}
	
	/**
	 *  Method that is invoked when rest service is called.
	 * 
	 *  Functionality blueprint for all service methods.
	 *  @param params The parameters.
	 *  @return The result.
	 */
	public static Object invoke(Object[] params, String sig, Object object)
	{
		Object ret = null;
		
//		System.out.println("called invoke: "+sig+" "+Arrays.toString(params));
		
		Class<?> clazz = object.getClass();
		
		try
		{
			// find own method
			
			Method[] ms = clazz.getDeclaredMethods();
			Method method = null;
			for(Method m: ms)
			{
				if(RestMethodInfo.buildSignature(m.getName(), m.getParameterTypes()).equals(sig))
				{
					method = m;
					break;
				}
			}
			if(method==null)
			{
				System.out.println("methods: "+Arrays.toString(ms));
				throw new RuntimeException("No method '"+sig+"' on class: "+object.getClass());
			}
			
//			StackTraceElement[] s = Thread.currentThread().getStackTrace();
//			String name = s[2].getMethodName();
			
//			System.out.println("name is: "+name);

//			for(int i=0;i<s.length; i++)
//			{
//				System.out.println(s[i].getMethodName());
//			}
//			String name = SReflect.getMethodName();
//			Method[] methods = SReflect.getMethods(getClass(), name);
//		    Method method = null;
//			if(methods.length>1)
//			{
//			    for(int i=0; i<methods.length && method==null; i++)
//			    {
//			    	Class<?>[] types = methods[i].getParameterTypes();
//			    	if(types.length==params.length)
//			    	{
//			    		// check param types
//			    		method = methods[i];
//			    	}
//			    }
//			}
//			else if(methods.length==1)
//			{
//				method = methods[0];
//			}
//			else
//			{
//				throw new RuntimeException("No method '"+name+"' on class: "+getClass());
//			}
//			System.out.println("call: "+this+" "+method+" "+SUtil.arrayToString(params)+" "+name);
			
//			Request req = (Request)getClass().getDeclaredField("__greq").get(this);
////			System.out.println("call: "+this+" "+method+" "+req);
//			for(String name: req.getHeaderNames())
//			{
//				System.out.println("header: "+name+": "+req.getHeader(name));
//			}
			
			// check if mappers are there
			ResourceConfig rc = (ResourceConfig)getFieldValue("__rc", object);
			
//			Object service = rc.getProperty(JADEXSERVICE);
			Object service = rc.getProperty("jadexservice");
//			System.out.println("jadex service is: "+service);

			HttpServletRequest req = (HttpServletRequest)getFieldValue("__req", object);
			Request greq = (Request)getFieldValue("__greq", object);
			ContainerRequest creq = (ContainerRequest)getFieldValue("__creq", object);
			
			Method targetmethod = null;
			if(method.isAnnotationPresent(MethodMapper.class))
			{
				MethodMapper mm = method.getAnnotation(MethodMapper.class);
				targetmethod = SReflect.getMethod(service.getClass(), mm.value(), mm.parameters());
			}
			else
			{
				String mname = method.getName();
				if(mname.endsWith("XML"))
					mname = mname.substring(0, mname.length()-3);
				if(mname.endsWith("JSON"))
					mname = mname.substring(0, mname.length()-4);
				targetmethod = service.getClass().getMethod(mname, method.getParameterTypes());
			}
			
//			System.out.println("target: "+targetmethod);
			
			Object[] targetparams = params;
			if(method.isAnnotationPresent(ParametersMapper.class))
			{
//				System.out.println("foundmapper");
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
					
					targetparams = ((IParameterMapper)mapper).convertParameters(params, req!=null? req: greq);
				}
				else
				{
					// In case of GET autmap the query parameters
					if(method.isAnnotationPresent(GET.class))
					{
	//					System.out.println("automapping detected");
						Class<?>[] ts = targetmethod.getParameterTypes();
						targetparams = new Object[ts.length];
						if(ts.length==1)
						{
							if(SReflect.isSupertype(ts[0], Map.class))
							{
								UriInfo ui = (UriInfo)getFieldValue("__ui", object);
								MultivaluedMap<String, String> vals = ui.getQueryParameters();
								targetparams[0] = SInvokeHelper.convertMultiMap(vals);
								((Map)targetparams[0]).putAll(extractCallerValues(greq));
							}
							else if(SReflect.isSupertype(ts[0], MultivaluedMap.class))
							{
								UriInfo ui = (UriInfo)getFieldValue("__ui", object);
								targetparams[0] = SInvokeHelper.convertMultiMap(ui.getQueryParameters());
								((Map)targetparams[0]).putAll(extractCallerValues(greq));
							}
						}
					}
					else //if(method.isAnnotationPresent(POST.class))
					{
						Class<?>[] ts = targetmethod.getParameterTypes();
						targetparams = new Object[ts.length];
//						System.out.println("automapping detected: "+SUtil.arrayToString(ts));
						if(ts.length==1)
						{
							if(SReflect.isSupertype(ts[0], Map.class))
							{
								if(greq!=null)
								{
									if(greq.getContentType()!=null && greq.getContentType().startsWith("multipart/form-data"))
									{
										System.out.println("parsing multipart/form-data");
										// Todo: why doesn't work out of the box any more!?
										final Map<String, String>	map	= extractCallerValues(greq);//new LinkedHashMap<String, Object>();
										targetparams[0]	= map;
										final Future<Void>	done	= new Future<Void>(); 
										MultipartScanner.scan(greq, new MultipartEntryHandler()
										{
											public void handle(MultipartEntry me) throws Exception
											{
												final String	name	= me.getContentDisposition().getDispositionParamUnquoted("name");
												final StringBuffer	value	= new StringBuffer();
												final NIOReader	nr	= me.getNIOReader();
												nr.notifyAvailable(new ReadHandler()
												{
													public void onError(Throwable e)
													{
														System.err.println("error reading multipart entry: "+name+"="+value);
														e.printStackTrace();
													}
													
													public void onDataAvailable() throws Exception
													{
														char[]	buf	= new char[8192];
														while(nr.isReady())
														{
															value.append(buf, 0, nr.read(buf));
														}
													}
													
													public void onAllDataRead() throws Exception
													{
														char[]	buf	= new char[8192];
														while(nr.isReady())
														{
															value.append(buf, 0, nr.read(buf));
														}
//														System.out.println("entry: "+name+"="+value);
														map.put(name, value.toString());
													}
												});
											}
										}, new CompletionHandler<Request>()
										{
											public void updated(Request r)
											{
											}
											
											public void failed(Throwable e)
											{
												// Todo: why called multiple times?
												done.setExceptionIfUndone(new RuntimeException(e));
											}
											
											public void completed(Request r)
											{
												// Todo: why called multiple times?
												done.setResultIfUndone(null);
											}
											
											public void cancelled()
											{
												// Todo: why called multiple times?
												done.setResultIfUndone(null);
											}
										});
										done.get();
									}
									else
									{
	//									SInvokeHelper.debug(greq);
										// Hack to make grizzly allow parameter parsing
										// Jersey calls getInputStream() hindering grizzly parsing params
										try
										{
											Request r = (Request)greq;
											Field f = r.getClass().getDeclaredField("usingInputStream");
											f.setAccessible(true);
											f.set(r, Boolean.FALSE);
	//										System.out.println("params: "+r.getParameterNames());
										}
										catch(Exception e)
										{
											e.printStackTrace();
										}
										
										// Hack!!! Assume urlencoded when text/plain (required e.g. for XDomainRequest in IE <10).
										if(greq.getContentType()==null || greq.getContentType().startsWith("text/plain"))
										{
											greq.getRequest().setContentType(Constants.FORM_POST_CONTENT_TYPE);
										}
										// Hack!!! IE doesn't send char set in ajax request by default!?
										if(greq.getCharacterEncoding()==null)
										{
											greq.getRequest().setCharacterEncoding("UTF-8");
										}
										
										targetparams[0] = SInvokeHelper.convertMultiMap(greq.getParameterMap());
										((Map<String, String>)targetparams[0]).putAll(extractCallerValues(greq));
									}
								}
								else if(req!=null)
								{
									targetparams[0] = SInvokeHelper.convertMultiMap(req.getParameterMap());
									((Map<String, String>)targetparams[0]).putAll(extractCallerValues(greq));
								}
							}
							else if(SReflect.isSupertype(ts[0], MultivaluedMap.class))
							{
								targetparams[0] = SInvokeHelper.convertToMultiMap(req.getParameterMap());
								((Map<String, String>)targetparams[0]).putAll(extractCallerValues(greq));
							}
						}
					}
				}
			}
	
//			System.out.println("method: "+method.getName()+" "+method.getDeclaringClass().getName());
//			System.out.println("targetparams: "+SUtil.arrayToString(targetparams));
			System.out.println("call: "+targetmethod.getName()+" paramtypes: "+SUtil.arrayToString(targetmethod.getParameterTypes())+" on "+service+" "+Arrays.toString(targetparams));
//			
			ret = targetmethod.invoke(service, targetparams);
			if(ret instanceof IFuture)
				ret = ((IFuture<?>)ret).get(Starter.getLocalDefaultTimeout(null)); // todo: how to get platform context?
			System.out.println("call finished: "+targetmethod.getName()+" paramtypes: "+SUtil.arrayToString(targetmethod.getParameterTypes())+" on "+service+" "+Arrays.toString(targetparams));
			
			if(method.isAnnotationPresent(ResultMapper.class))
			{
				ResultMapper mm = method.getAnnotation(ResultMapper.class);
				Class<?> pclazz = mm.value().clazz();
				IValueMapper mapper;
//				System.out.println("res mapper: "+clazz);
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
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected static Object getFieldValue(String name, Object object) throws Exception
	{
		Field f = object.getClass().getDeclaredField(name);
		f.setAccessible(true);
		return f.get(object);
	}
	
	/**
	 * 
	 */
	public static void debug(Object req)
	{
		if(req instanceof Request)
		{
			try
			{
				Request r = (Request)req;
				Field f = r.getClass().getDeclaredField("usingInputStream");
				f.setAccessible(true);
				f.set(r, Boolean.FALSE);
				System.out.println("params: "+r.getParameterNames());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		System.out.println(req);
	}
}
