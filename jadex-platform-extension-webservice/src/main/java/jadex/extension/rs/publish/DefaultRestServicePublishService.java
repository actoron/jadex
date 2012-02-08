package jadex.extension.rs.publish;

import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 *  The default web service publish service.
 *  Publishes web services using the JDK Endpoint class.
 */
@Service
public class DefaultRestServicePublishService implements IPublishService
{
	//-------- constants --------
	
	/** Constant for boolean flag if automatic generation should be used.*/ 
	public static String GENERATE = "generate";
	
	/** Constant for String[] for supported parameter media types.*/ 
	public static String FORMATS = "formats";
	
	/** The default media formats. */
	public static String[] DEFAULT_FORMATS = new String[]{"xml", "json"};

	/** The format -> media type mapping. */
	public static Map<String, String> formatmap = SUtil.createHashMap(DEFAULT_FORMATS, 
		new String[]{MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON});
	
	/** The service constant. */
	public static String JADEXSERVICE = "jadexservice"; 
	
	//-------- attributes --------
	
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;
	
	/** The servers per service id. */
	protected Map<IServiceIdentifier, Tuple2<HttpServer, HttpHandler>> sidservers;
	
	/** The servers per uri. */
	protected Map<URI, HttpServer> uriservers;

	
	//-------- methods --------
	
	/**
	 *  Test if publishing a specific type is supported (e.g. web service).
	 *  @param publishtype The type to test.
	 *  @return True, if can be published.
	 */
	public IFuture<Boolean> isSupported(String publishtype)
	{
		return new Future<Boolean>(IPublishService.PUBLISH_RS.equals(publishtype));
	}
	
	/**
	 *  Publish a service.
	 *  @param cl The classloader.
	 *  @param service The original service.
	 *  @param pid The publish id (e.g. url or name).
	 */
	public IFuture<Void> publishService(ClassLoader cl, IService service, PublishInfo pi)
	{
		final Future<Void> ret = new Future<Void>();
		
		try
		{
			// Jaxb seems to use the context classloader so it needs to be set :-(
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(cl);
			URI uri = new URI(pi.getPublishId());
						
			// Note: the expression evaluation is done on another component so that no original imports and classes can be used 
			// Should not be a problem because only basic properties are used (String, boolean)
			Map<String, Object> mapprops = new HashMap<String, Object>();
			if(pi.getProperties()!=null)
			{
				for(int i=0; i<pi.getProperties().size(); i++)
				{
					Object val = UnparsedExpression.getParsedValue(pi.getProperties().get(i), null, component.getFetcher(), component.getClassLoader());
					mapprops.put(pi.getProperties().get(i).getName(), val);
				}
			}
			
			// If no service type was specified it has to be generated.
			Class<?> rsimpl = null;
			Boolean gen = (Boolean)mapprops.get(GENERATE);
			if(gen!=null && !gen.booleanValue())
			{
				rsimpl = pi.getServiceType().getType(cl);
			}
			else
			{
				rsimpl = createProxyClass(service, cl, uri.getPath(), pi.getServiceType()!=null? 
					pi.getServiceType().getType(cl): null, mapprops);
			}
			
			Map<String, Object> props = new HashMap<String, Object>();
			String jerseypack = "com.sun.jersey.config.property.packages";
			String pack = rsimpl.getPackage().getName();
//			props.put(uri.toString(), service);
			StringBuilder strb = new StringBuilder("jadex.extension.rs.publish"); // Add Jadex XML body reader/writer
			// Must not add package because a baseclass could be contained with the same path
			// This leads to an error in jersey
//			strb.append(pack);
//			strb.append(", ");
			props.put(jerseypack, strb.toString());
			props.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			props.put(JADEXSERVICE, service);
			PackagesResourceConfig config = new PackagesResourceConfig(props);
			config.getClasses().add(rsimpl);
			
//			URI baseuri = uri;
			URI baseuri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
			
			HttpServer server = uriservers==null? null: uriservers.get(baseuri);
			HttpHandler handler;
			if(server==null)
			{
				System.out.println("Starting new server: "+uri.getPath());
				server = GrizzlyServerFactory.createHttpServer(uri.toString(), config);
				server.start();
				handler = server.getHttpHandler();
				
				if(uriservers==null)
					uriservers = new HashMap<URI, HttpServer>();
				uriservers.put(baseuri, server);
			}
			else
			{
				System.out.println("Adding http handler to server: "+uri.getPath());
				handler = ContainerFactory.createContainer(HttpHandler.class, config);
				ServerConfiguration sc = server.getServerConfiguration();
				sc.addHttpHandler(handler, uri.getPath());
//				Map h = sc.getHttpHandlers();
//				System.out.println("handlers: "+h);
			}
			
			if(sidservers==null)
				sidservers = new HashMap<IServiceIdentifier, Tuple2<HttpServer, HttpHandler>>();
			sidservers.put(service.getServiceIdentifier(), new Tuple2<HttpServer, HttpHandler>(server, handler));

			Thread.currentThread().setContextClassLoader(ccl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ret.setException(e);
		}
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
			    	server.stop();
			    stopped = true;
				ret.setResult(null);
			}
		}
		if(!stopped)
			ret.setException(new RuntimeException("Published service could not be stopped: "+sid));
		return ret;
	}
	
	/**
	 *  Create a service proxy class.
	 *  @param service The Jadex service.
	 *  @param classloader The classloader.
	 *  @param type The web service interface type.
	 *  @return The proxy object.
	 */
	protected Class<?> createProxyClass(IService service, ClassLoader classloader, 
		String apppath, Class<?> baseclass, Map<String, Object> mapprops) throws Exception
	{
		Class<?> ret = null;

		String[] formats = mapprops.get(FORMATS)==null? DEFAULT_FORMATS: (String[])mapprops.get(FORMATS);
		
		Class iface = service.getServiceIdentifier().getServiceType().getType(classloader);
		
		// The name of the class has to ensure that it represents the different class properties:
		// - the package+"Proxy"+name of the baseclass or (if not available) interface
		// - the hashcode of the properties
		// - only same implclass name + same props => same generated classname
		
		StringBuilder builder = new StringBuilder();
		Class nameclazz = baseclass!=null? baseclass: iface;
		if(nameclazz.getPackage()!=null)
			builder.append(nameclazz.getPackage().getName());
		builder.append(".Proxy");
		builder.append(nameclazz.getSimpleName());
		if(mapprops!=null && mapprops.size()>0)
			builder.append(mapprops.hashCode());
		String name = builder.toString();

		try
		{
			ret = classloader.loadClass(name);
//			ret = SReflect.classForName0(name, classloader); // does not work because SReflect cache saves that not found!
		}
		catch(Exception e)
		{
			System.out.println("Not found, creating new: "+name);
			ClassPool pool = new ClassPool(null);
			pool.appendSystemPath();
//			ClassPool pool = ClassPool.getDefault();
//			CtClass proxyclazz = pool.makeClass(name, getCtClass(jadex.extension.ws.publish.Proxy.class, pool));
			
			Class newbaseclass = baseclass;
			
//			CtClass proxyclazz = pool.makeClass(name, baseclass==null? null: ctbc);
			CtClass proxyclazz = pool.makeClass(name, newbaseclass==null? null: getCtClass(newbaseclass, pool));
//			CtClass proxyclazz = pool.makeClass(name, baseclass==null? null: getCtClass(baseclass, pool));
			ClassFile cf = proxyclazz.getClassFile();
			ConstPool constpool = cf.getConstPool();
	
			CtField rc = new CtField(getCtClass(ResourceConfig.class, pool), "__rc", proxyclazz);
			AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			Annotation annot = new Annotation(constpool, getCtClass(Context.class, pool));
			attr.addAnnotation(annot);
			rc.getFieldInfo().addAttribute(attr);
			proxyclazz.addField(rc);
			
			proxyclazz.addInterface(getCtClass(iface, pool));
			Method[] ms = iface.getMethods();
			
			CtMethod invoke = getCtClass(Proxy.class, pool).getDeclaredMethod("invoke");
			
			Set<String> paths = new HashSet<String>();
	
			for(int i=0; i<ms.length; i++)
			{
				Type rt = ms[i].getGenericReturnType();
				
				if(rt instanceof ParameterizedType)
				{
					ParameterizedType pt = (ParameterizedType)rt;
					Type[] pts = pt.getActualTypeArguments();
					if(pts.length>1)
						throw new RuntimeException("Cannot unwrap futurized method due to more than one generic type: "+SUtil.arrayToString(pt.getActualTypeArguments()));
					rt = (Class<?>)pts[0];
				}
//				System.out.println("rt: "+pt.getRawType()+" "+SUtil.arrayToString(pt.getActualTypeArguments()));
				
				String methodname = ms[i].getName();
				
				// Do not generate method if user has implemented it by herself
				
				CtClass rettype = getCtClass((Class)rt, pool);
				CtClass[] paramtypes = getCtClasses(ms[i].getParameterTypes(), pool);
				CtClass[] exceptions = getCtClasses(ms[i].getExceptionTypes(), pool);
				
				// todo: what about pure string variants?
				// todo: what about mixed variants (in json out xml or plain)
				for(int j=0; j<formats.length; j++)
				{
					String mtname = formats.length>1? methodname+formats[j].toUpperCase(): methodname;
					
					if(baseclass==null || SReflect.getMethod(baseclass, mtname, ms[i].getParameterTypes())==null)
					{
						String path = mtname;
						for(int k=1; paths.contains(path); k++)
						{
							path = mtname+"#"+k;
						}
						paths.add(path);
							
						CtMethod m = CtNewMethod.wrapped(rettype, mtname, 
							paramtypes, exceptions, invoke, null, proxyclazz);
						
						attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
						annot = new Annotation(constpool, getCtClass(getHttpType(ms[i], (Class)rt, ms[i].getParameterTypes()), pool));
						attr.addAnnotation(annot);
						annot = new Annotation(constpool, getCtClass(Path.class, pool));
						annot.addMemberValue("value", new StringMemberValue(path, constpool));
						attr.addAnnotation(annot);
						annot = new Annotation(constpool, getCtClass(Consumes.class, pool));
						ArrayMemberValue vals = new ArrayMemberValue(new StringMemberValue(constpool), constpool);
						vals.setValue(new MemberValue[]{new StringMemberValue(formatmap.get(formats[j]), constpool)});
						annot.addMemberValue("value", vals);
						attr.addAnnotation(annot);
						annot = new Annotation(constpool, getCtClass(Produces.class, pool));
						annot.addMemberValue("value", vals);
						attr.addAnnotation(annot);
						
						m.getMethodInfo().addAttribute(attr);
	//					System.out.println("m: "+m.getName());
						
						proxyclazz.addMethod(m);
					}
				}
			}
			
			attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			annot = new Annotation(constpool, getCtClass(Path.class, pool));
			annot.addMemberValue("value", new StringMemberValue("/", constpool));
			attr.addAnnotation(annot);
			cf.addAttribute(attr);
			
			ret = proxyclazz.toClass(classloader, iface.getProtectionDomain());
			proxyclazz.freeze();
			System.out.println("create proxy class: "+ret.getName()+" "+apppath);
		}
		
		return ret;
	}
	
	/**
	 *  Guess the http type (GET, POST, PUT, DELETE, ...) of a method.
	 *  @param method The method.
	 *  @return  The rs annotation of the method type to use 
	 */
	public Class getHttpType(Method method, Class rettype, Class[] paramtypes)
	{
	    // Retrieve = GET (!hasparams && hasret)
	    // Update = POST (hasparams && hasret)
	    // Create = PUT  return is pointer to new resource (hasparams? && hasret)
	    // Delete = DELETE (hasparams? && hasret?)

		Class ret = GET.class;
		
		boolean hasparams = paramtypes.length>0;
		boolean hasret = !rettype.equals(Void.class) && !rettype.equals(void.class);
		
		if(hasparams)// && hasret)
		{
			ret = POST.class;
		}
		
//		System.out.println("http-type: "+ret.getName()+" "+method.getName()+" "+hasparams+" "+hasret);
		
		return ret;
//		return GET.class;
	}
	
	/**
	 *  Get a ctclass for a Java class from the pool.
	 *  @param clazz The Java class.
	 *  @param pool The class pool.
	 *  @return The ctclass.
	 */
	protected static CtClass getCtClass(Class clazz, ClassPool pool)
	{
		CtClass ret = null;
		try
		{
			ret = pool.get(clazz.getName());
		}
		catch(Exception e)
		{
			try
			{
				ClassPath cp = new ClassClassPath(clazz);
				pool.insertClassPath(cp);
				ret = pool.get(clazz.getName());
			}
			catch(Exception e2)
			{
				throw new RuntimeException(e2);
			}
		}
		return ret;
	}
	
	/**
	 *  Get a ctclass array for a class array.
	 *  @param classes The classes.
	 *  @param pool The pool.
	 *  @return The ctclass array.
	 */
	protected static CtClass[] getCtClasses(Class[] classes, ClassPool pool)
	{
		CtClass[] ret = new CtClass[classes.length];
		for(int i=0; i<classes.length; i++)
		{
			ret[i] = getCtClass(classes[i], pool);
		}
		return ret;	
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args) throws Exception
	{
		URI uri = new URI("http://localhost:8080/bank");
//		URI newuri = new URI(uri.getScheme(), uri.getAuthority(), null);
		URI newuri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
		System.out.println(newuri);
	}
}
	
	// Code that removes an annotation
//	if(baseclass!=null)
//	{
//		CtClass ctbc = getCtClass(baseclass, pool);
//		List attrs = ctbc.getClassFile().getAttributes();
//		boolean done = false;
//		for(int i=0; i<attrs.size() && !done; i++)
//		{
//			AttributeInfo attr = (AttributeInfo)attrs.get(i);
//			if(attr instanceof AnnotationsAttribute)
//			{
//				AnnotationsAttribute anattr = (AnnotationsAttribute)attr;
//				Annotation[] ans = anattr.getAnnotations();
//				for(int j=0; j<ans.length && !done; j++)
//				{
//					if(ans[j].getTypeName().equals(Path.class.getName()))
//					{
//						attrs.remove(attr);
//						done = true;
//					}
//				}
//			}
//		}
//		ctbc.setName(ctbc.getName()+"New");
//		newbaseclass = ctbc.toClass(classloader, iface.getProtectionDomain());
//	}
	
//	boolean pathpresent = false;
//	Class<?> test = newbaseclass;
//	while(test!=null && !pathpresent)
//	{
//		pathpresent = test.isAnnotationPresent(Path.class);
//		test = test.getSuperclass();
//	}
//	System.out.println("found anno: "+pathpresent);

//	// If no explicit url path extract last name from package
//	if(apppath==null || apppath.length()==0 || apppath.equals("/"))
//	{
//		if(iface.getPackage()!=null)
//		{
//			String pck = iface.getPackage().getName();
//			int idx = pck.lastIndexOf(".");
//			if(idx>0)
//			{
//				apppath = pck.substring(idx+1);
//			}
//			else
//			{
//				apppath = pck;
//			}
//		}
//	}
//	annot.addMemberValue("value", new StringMemberValue(apppath, constpool));
