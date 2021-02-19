package jadex.extension.ws.publish;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.SJavassist;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

/**
 *  The default web service publish service.
 *  Publishes web services using the JDK Endpoint class.
 */
@Service
public class DefaultWebServicePublishService implements IPublishService
{
	//-------- attributes --------
	
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;
	
	/** The published endpoints. */
	protected Map<IServiceIdentifier, Endpoint> endpoints;
	
	//-------- methods --------
	
	/**
	 *  Test if publishing a specific type is supported (e.g. web service).
	 *  @param publishtype The type to test.
	 *  @return True, if can be published.
	 */
	public IFuture<Boolean> isSupported(String publishtype)
	{
		return IPublishService.PUBLISH_WS.equals(publishtype) ? IFuture.TRUE : IFuture.FALSE;
	}
	
	/**
	 *  Publish a service.
	 *  @param cl The classloader.
	 *  @param service The original service.
	 *  @param pid The publish id (e.g. url or name).
	 */
	public IFuture<Void> publishService(IServiceIdentifier serviceid, PublishInfo pi)
	{
		// Java dynamic proxy cannot be used as @WebService annotation cannot be added.
		
//		Object pr = Proxy.newProxyInstance(cl, new Class[]{service.getId().getServiceType()}, 
//			new WebServiceToJadexWrapperInvocationHandler(service));
		
		IService service = (IService) component.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(serviceid.getServiceType(), pi.getPublishScope(), null)).get();
		
		ClassLoader cl = null;
		ILibraryService ls = component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( ILibraryService.class, ServiceScope.PLATFORM));
		if (serviceid.getProviderId().getPlatformName().equals(component.getId().getPlatformName()))
		{
			// Local publish, get the component's classloader.
			IComponentDescription desc = component.getDescription(serviceid.getProviderId()).get();
			cl = ls.getClassLoader(desc.getResourceIdentifier()).get();
		}
		else
		{
			// Remote, use ALL classloader.
			cl = ls.getClassLoader(ls.getRootResourceIdentifier()).get();
		}
		
		Object pr = createProxy(service, cl, pi.getMapping().getType(cl));
		
		// Jaxb seems to use the context classloader so it needs to be set :-(
		ClassLoader ccl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(cl);
		Endpoint endpoint = Endpoint.publish(pi.getPublishId(), pr);
		Thread.currentThread().setContextClassLoader(ccl);
		
		if(endpoints==null)
			endpoints = new HashMap<IServiceIdentifier, Endpoint>();
		endpoints.put(service.getServiceId(), endpoint);
		return IFuture.DONE;
		
//		try
//		{
//		}
//		catch(Throwable e)
//		{
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
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
			Endpoint ep = endpoints.remove(sid);
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
	 *  Create a service proxy.
	 *  @param service The Jadex service.
	 *  @param classloader The classloader.
	 *  @param type The web service interface type.
	 *  @return The proxy object.
	 */
	protected Object createProxy(IService service, ClassLoader classloader, Class<?> type)
	{
//		System.out.println("createProxy: "+service.getId());
		Object ret = null;
		try
		{
			Class<?> clazz = getProxyClass(type, classloader);
//			System.out.println("proxy class: "+clazz.getPackage()+" "+clazz.getClassLoader()+" "+classloader);
			ret = clazz.newInstance();
			Method shm = clazz.getMethod("setHandler", new Class[]{InvocationHandler.class});
			shm.invoke(ret, new Object[]{new WebServiceToJadexWrapperInvocationHandler(service)});
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	/**
	 *  Get the proxy class for a web service interface type.
	 *  The method generates a new proxy class if called the 
	 *  first time for a specific service interface.
	 *  @param type The interface service type.
	 *  @param classloader The class loader.
	 *  @return The (possibly newly generated) proxy class.
	 */
	protected Class<?> getProxyClass(Class<?> type, ClassLoader classloader)
	{
		Class<?> ret = null;

		ClassPool pool = ClassPool.getDefault();
//		String pck = type.getPackage().getName();
//		pool.importPackage(pck);
		String name = type.getPackage().getName()+".Proxy"+type.getSimpleName();
		
		try
		{
			ret = classloader.loadClass(name);
//			ret = SReflect.classForName0(name, classloader); // does not work because SReflect cache saves that not found!
		}
		catch(Exception e)
		{
			try
			{
				CtClass proxyclazz = pool.makeClass(name, SJavassist.getCtClass(jadex.extension.ws.publish.Proxy.class, pool));
				proxyclazz.addInterface(SJavassist.getCtClass(type, pool));
				Method[] ms = type.getMethods();
				CtMethod invoke = SJavassist.getCtClass(jadex.extension.ws.publish.Proxy.class, pool).getDeclaredMethod("invoke");
				for(int i=0; i<ms.length; i++)
				{
					CtMethod m = CtNewMethod.wrapped(SJavassist.getCtClass(ms[i].getReturnType(), pool), ms[i].getName(), 
						SJavassist.getCtClasses(ms[i].getParameterTypes(), pool), SJavassist.getCtClasses(ms[i].getExceptionTypes(), pool),
						invoke, null, proxyclazz);
//					System.out.println("m: "+m.getName()+" "+getCtClasses(ms[i].getParameterTypes(), pool).length);
					proxyclazz.addMethod(m);
				}
				
				ClassFile cf = proxyclazz.getClassFile();
				
				ConstPool constpool = cf.getConstPool();
				AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
				Annotation annot = new Annotation(constpool, SJavassist.getCtClass(WebService.class, pool));
				// Must be set due to bug in javassit, package nulls. package is used for namespace.
				String ns = "http://"+type.getPackage().getName()+"/";
				annot.addMemberValue("targetNamespace", new StringMemberValue(ns, constpool));
//				annot.addMemberValue("serviceName", new StringMemberValue("WSBankingServiceService", constpool));
//				annot.addMemberValue("portName", new StringMemberValue("WSBankingServicePort", constpool));
				attr.addAnnotation(annot);
				cf.addAttribute(attr);
				proxyclazz.getClassFile().addAttribute(attr);
				ret = proxyclazz.toClass(classloader, type.getProtectionDomain());
				proxyclazz.freeze();
//				System.out.println("name: "+ret.getName()+" "+ret.getPackage()+" "+proxyclazz.getPackageName());
			}
			catch(Exception e2)
			{
				e2.printStackTrace();
				throw new RuntimeException(e2);
			}
		}
		
		return ret;
	}
	
//	/**
//	 *  Get a ctclass for a Java class from the pool.
//	 *  @param clazz The Java class.
//	 *  @param pool The class pool.
//	 *  @return The ctclass.
//	 */
//	protected static CtClass getCtClass(Class clazz, ClassPool pool)
//	{
//		CtClass ret = null;
//		try
//		{
//			ret = pool.get(clazz.getName());
//		}
//		catch(Exception e)
//		{
//			try
//			{
//				
//				ClassPath cp = new ClassClassPath(clazz);
//				pool.insertClassPath(cp);
//				ret = pool.get(clazz.getName());
//			}
//			catch(Exception e2)
//			{
//				throw new RuntimeException(e2);
//			}
//		}
//		return ret;
//	}
	
//	/**
//	 *  Get a ctclass array for a class array.
//	 *  @param classes The classes.
//	 *  @param pool The pool.
//	 *  @return The ctclass array.
//	 */
//	protected static CtClass[] getCtClasses(Class[] classes, ClassPool pool)
//	{
//		CtClass[] ret = new CtClass[classes.length];
//		for(int i=0; i<classes.length; i++)
//		{
//			ret[i] = getCtClass(classes[i], pool);
//		}
//		return ret;	
//	}
	
	/**
	 *  Main for testing.
	 */
//	public static void main(String[] args)
//	{
//		// Bug in javassist: created package is null.
//		try
//		{
//			ClassPool cp = ClassPool.getDefault();
//			CtClass bean = cp.get("jadex.platform.service.publish.TestBean");
//			CtClass called = cp.get("jadex.platform.service.publish.DefaultWebServicePublishService");
////			MyCodeConverter cc = new MyCodeConverter();
////			cc.replaceFieldWrite(bean.getDeclaredField("name"), called, "writeField");
////			cc.replaceFieldRead(bean.getDeclaredField("name"), called, "readField");
////			bean.instrument(cc);
//
//			bean.instrument(new ExprEditor() 
//			{
//				public void edit(FieldAccess f) throws CannotCompileException
//				{
//					if(f.isWriter())
//					{
////						f.replace("System.out.println(\"new: \"+$1); $proceed($$);");
////						f.replace("jadex.platform.service.publish.DefaultWebServicePublishService.writeField($0, $1,\""+f.getFieldName()+"\");");
//						f.replace("$0.writeField($1,\""+f.getFieldName()+"\");");
//					}
//				}
//				
////				public void edit(MethodCall m) throws CannotCompileException 
////				{
////					if(m.getClassName().equals("Point") && m.getMethodName().equals("move"))
////						m.replace("{ System.out.println(\"move\"); $_ = $proceed($$); }");
////				}
//			});
//			
//			
//			Class bc = bean.toClass();
//			TestBean tb = new TestBean("Willy");
//			tb.setName("Willy2");
//			System.out.println(tb.getName());
//			
////			ClassPool pool = ClassPool.getDefault();
////			CtClass ctcl = pool.makeClass("a.b.C");
////			Class cl = ctcl.toClass();
////			System.out.println("pck: "+ctcl.getPackageName()+" "+cl.getPackage());
////			Object obj = cl.newInstance();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
	
//	protected static void writeField(Object o, Object val, String fieldname)
//	{
//		try
//		{
//			System.out.println("write: "+o+" "+val+" "+fieldname);
//			Field f = o.getClass().getDeclaredField(fieldname);
//			f.setAccessible(true);
//			f.set(o, val);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
//	
//	protected static Object readField(Object o)
//	{
//		try
//		{
//			System.out.println("read: "+o);
//			Field f = o.getClass().getDeclaredField("name");
//			f.setAccessible(true);
//			return (String)f.get(o);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
}
