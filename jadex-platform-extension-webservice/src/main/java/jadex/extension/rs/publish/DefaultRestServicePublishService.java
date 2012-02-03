package jadex.extension.rs.publish;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
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

import org.glassfish.grizzly.http.server.HttpServer;

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
	//-------- attributes --------
	
	/** The published endpoints. */
	protected Map<IServiceIdentifier, HttpServer> endpoints;
	
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

			Map<String, Object> props = new HashMap<String, Object>();
			String jerseypack = "com.sun.jersey.config.property.packages";
			String pack = pi.getServiceType().getType(cl).getPackage().getName();
			props.put(pi.getPublishId(), service);
			StringBuilder strb = new StringBuilder("jadex.extension.rs.publish"); // Add Jadex XML body reader/writer
			strb.append(", ");
			strb.append(pack);
			props.put(jerseypack, strb.toString());
			props.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			props.put("__service", service);
			PackagesResourceConfig config = new PackagesResourceConfig(props);
			
			// If no service type was specified it has to be generated.
			Class proxy = null;
			if(pi.getServiceType().getType(cl).equals(Object.class))
			{
				proxy = createProxyClass(service, cl);
				config.getClasses().add(proxy);
			}
			
			HttpServer endpoint = GrizzlyServerFactory.createHttpServer(new URI(pi.getPublishId()), config);
			endpoint.start();
	
			Thread.currentThread().setContextClassLoader(ccl);
			
			if(endpoints==null)
				endpoints = new HashMap<IServiceIdentifier, HttpServer>();
			endpoints.put(service.getServiceIdentifier(), endpoint);
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
		if(endpoints!=null)
		{
			HttpServer ep = endpoints.remove(sid);
			if(ep!=null)
			{
				ep.stop();
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
	protected Class createProxyClass(IService service, ClassLoader classloader) throws Exception
	{
		Class ret = null;
		
		ClassPool pool = ClassPool.getDefault();
//		String pck = type.getPackage().getName();
//		pool.importPackage(pck);
		
		Class type = service.getServiceIdentifier().getServiceType().getType(classloader);
		String name = type.getPackage().getName()+".Proxy"+type.getSimpleName();
		
		try
		{
			ret = classloader.loadClass(name);
//			ret = SReflect.classForName0(name, classloader); // does not work because SReflect cache saves that not found!
		}
		catch(Exception e)
		{
			CtClass proxyclazz = pool.makeClass(name, getCtClass(jadex.extension.ws.publish.Proxy.class, pool));
			ClassFile cf = proxyclazz.getClassFile();
			ConstPool constpool = cf.getConstPool();
	
			CtField rc = new CtField(getCtClass(ResourceConfig.class, pool), "__rc", proxyclazz);
			AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			Annotation annot = new Annotation(constpool, getCtClass(Context.class, pool));
			attr.addAnnotation(annot);
			rc.getFieldInfo().addAttribute(attr);
			proxyclazz.addField(rc);
			
			proxyclazz.addInterface(getCtClass(type, pool));
			Method[] ms = type.getMethods();
			
			CtMethod invoke = getCtClass(jadex.extension.rs.publish.Proxy.class, pool).getDeclaredMethod("invoke");
			
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
					rt = (Class)pts[0];
				}
	//					System.out.println("rt: "+pt.getRawType()+" "+SUtil.arrayToString(pt.getActualTypeArguments()));
				
				String methodname = ms[i].getName();
				CtClass rettype = getCtClass((Class)rt, pool);
				CtClass[] paramtypes = getCtClasses(ms[i].getParameterTypes(), pool);
				CtClass[] exceptions = getCtClasses(ms[i].getExceptionTypes(), pool);
				
				// todo: what about pure string variants?
				// todo: what about mixed variants (in json out xml or plain)
				String[] exts = new String[]{"XML", "JSON"};
				String[] mtypes = new String[]{MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON};
				
				for(int j=0; j<exts.length; j++)
				{
					String mtname = methodname+exts[j];
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
					vals.setValue(new MemberValue[]{new StringMemberValue(mtypes[j], constpool)});
					annot.addMemberValue("value", vals);
					attr.addAnnotation(annot);
					annot = new Annotation(constpool, getCtClass(Produces.class, pool));
					annot.addMemberValue("value", vals);
					attr.addAnnotation(annot);
					
					m.getMethodInfo().addAttribute(attr);
	//						System.out.println("m: "+m.getName());
					
					proxyclazz.addMethod(m);
				}
			}
			
			attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			annot = new Annotation(constpool, getCtClass(Path.class, pool));
			
			//todo: extract name from package
			annot.addMemberValue("value", new StringMemberValue("hello", constpool));
			attr.addAnnotation(annot);
			cf.addAttribute(attr);
			
			ret = proxyclazz.toClass(classloader, type.getProtectionDomain());
			proxyclazz.freeze();
			System.out.println("create proxy class: "+ret.getName()+" "+ret.getPackage()+" "+proxyclazz.getPackageName());
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
}
