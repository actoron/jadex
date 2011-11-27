package jadex.base.service.publish;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 *  The default web service publish service.
 *  Publishes web services using the JDK Endpoint class.
 */
@Service
public class DefaultWebServicePublishService implements IPublishService
{
	/** The published endpoints. */
	protected Map<IServiceIdentifier, Endpoint> endpoints;
	
	/**
	 *  Test if publishing a specific type is supported (e.g. web service).
	 *  @param publishtype The type to test.
	 *  @return True, if can be published.
	 */
	public IFuture<Boolean> isSupported(String publishtype)
	{
		return new Future<Boolean>(IPublishService.PUBLISH_WS.equals(publishtype));
	}
	
	/**
	 *  Publish a service.
	 *  @param cl The classloader.
	 *  @param service The original service.
	 *  @param pid The publish id (e.g. url or name).
	 */
	public IFuture<Void> publishService(ClassLoader cl, IService service, PublishInfo pi)
	{
		// Java dynamic proxy cannot be used as @WebService annotation cannot be added.
//		Object pr = Proxy.newProxyInstance(cl, new Class[]{service.getServiceIdentifier().getServiceType()}, 
//			new WebServiceToJadexWrapperInvocationHandler(service));
		
		try
		{
			Object pr = createProxy(service, cl, pi.getServiceType());
			
			// Jaxb seems to use the context classloader :-(
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(cl);
			Endpoint endpoint = Endpoint.publish(pi.getPublishId(), pr);
			Thread.currentThread().setContextClassLoader(ccl);
			
			if(endpoints==null)
				endpoints = new HashMap<IServiceIdentifier, Endpoint>();
			endpoints.put(service.getServiceIdentifier(), endpoint);
			return IFuture.DONE;
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
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
	 * 
	 */
	protected static CtClass[] getCtClasses(Class[] clazzes, ClassPool pool)
	{
		CtClass[] ret = new CtClass[clazzes.length];
		for(int i=0; i<clazzes.length; i++)
		{
			ret[i] = getCtClass(clazzes[i], pool);
		}
		return ret;	
	}
	
	/**
	 * 
	 */
	protected Object createProxy(IService service, ClassLoader classloader, Class type)
	{
//		System.out.println("createProxy: "+service.getServiceIdentifier());
		Object ret = null;
		try
		{
			Class clazz = getProxyClass(type, classloader);
//			System.out.println("proxy class: "+clazz.getPackage()+" "+clazz.getClassLoader()+" "+classloader);
			ret = clazz.newInstance();
			Method shm = clazz.getMethod("setHandler", new Class[]{InvocationHandler.class});
			shm.invoke(ret, new Object[]{new WebServiceToJadexWrapperInvocationHandler(service)});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 * 
	 */
	protected Class getProxyClass(Class type, ClassLoader classloader)
	{
		Class ret = null;

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
				CtClass proxyclazz = pool.makeClass(name, getCtClass(jadex.base.service.publish.Proxy.class, pool));
				proxyclazz.addInterface(getCtClass(type, pool));
				Method[] ms = type.getMethods();
				CtMethod invoke = getCtClass(jadex.base.service.publish.Proxy.class, pool).getDeclaredMethod("invoke");
				for(int i=0; i<ms.length; i++)
				{
					CtMethod m = CtNewMethod.wrapped(getCtClass(ms[i].getReturnType(), pool), ms[i].getName(), 
						getCtClasses(ms[i].getParameterTypes(), pool), getCtClasses(ms[i].getExceptionTypes(), pool),
						invoke, null, proxyclazz);
//					System.out.println("m: "+m.getName()+" "+getCtClasses(ms[i].getParameterTypes(), pool).length);
					proxyclazz.addMethod(m);
				}
				
				ClassFile cf = proxyclazz.getClassFile();
				
				ConstPool constpool = cf.getConstPool();
				AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
				Annotation annot = new Annotation(constpool, getCtClass(WebService.class, pool));
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
	
	public static void main(String[] args)
	{
		// Bug in javassist: created package is null.
		try
		{
			ClassPool pool = ClassPool.getDefault();
			CtClass ctcl = pool.makeClass("a.b.C");
			Class cl = ctcl.toClass();
			System.out.println("pck: "+ctcl.getPackageName()+" "+cl.getPackage());
			Object obj = cl.newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
