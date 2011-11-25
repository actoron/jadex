package jadex.base.service.publish;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
	public IFuture<Void> publishService(ClassLoader cl, IService service, String pid)
	{
//		Object pr = Proxy.newProxyInstance(cl, new Class[]{service.getServiceIdentifier().getServiceType()}, 
//			new WebServiceToJadexWrapperInvocationHandler(service));
		try
		{
			Object pr = createProxy(service, cl);
			Endpoint endpoint = Endpoint.publish(pid, pr);
			if(endpoints==null)
				endpoints = new HashMap<IServiceIdentifier, Endpoint>();
			endpoints.put(service.getServiceIdentifier(), endpoint);
			return IFuture.DONE;
		}
		catch(Exception e)
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
	 * @param service
	 * @return
	 */
	protected Object createProxy(IService service, ClassLoader classloader)
	{
		Object ret = null;
		Class type = service.getServiceIdentifier().getServiceType();
		try
		{
			ClassPool pool = ClassPool.getDefault();
			String pck = service.getServiceIdentifier().getServiceType().getPackage().getName();
			CtClass proxyclazz = pool.makeClass("ExtendedProxy", getCtClass(jadex.base.service.publish.Proxy.class, pool));
			proxyclazz.addInterface(getCtClass(type, pool));
			Method[] ms = type.getMethods();
			CtMethod invoke = getCtClass(jadex.base.service.publish.Proxy.class, pool).getDeclaredMethod("invoke");
			for(int i=0; i<ms.length; i++)
			{
	//			CtMethod method = proxyclazz.getDeclaredMethod(ms[i].getName(), getCtClasses(ms[i].getParameterTypes(), pool));
				CtMethod m = CtNewMethod.wrapped(getCtClass(ms[i].getReturnType(), pool), ms[i].getName(), 
					getCtClasses(ms[i].getParameterTypes(), pool), getCtClasses(ms[i].getExceptionTypes(), pool),
					invoke, null, proxyclazz);
				proxyclazz.addMethod(m);
			}
	//		clazz.addMethod(CtNewMethod.make("public double eval (double x) { return (" + args[0] + ") ; }", clazz));
			
			ClassFile cf = proxyclazz.getClassFile();
			ConstPool constpool = cf.getConstPool();
			AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			Annotation annot = new Annotation(constpool, getCtClass(WebService.class, pool));
			annot.addMemberValue("targetNamespace", new StringMemberValue(pck, constpool));
//			annot.addMemberValue("value", new IntegerMemberValue(ccFile.getConstPool(), 0));
			attr.addAnnotation(annot);
			cf.addAttribute(attr);
			proxyclazz.getClassFile().addAttribute(attr);
			
			Class cl = proxyclazz.toClass(classloader, service.getClass().getProtectionDomain());
			ret = cl.newInstance();
			Method shm = cl.getMethod("setHandler", new Class[]{InvocationHandler.class});
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
	
//	public static void main(String[] args)
//	{
//		try
//		{
//			Class type = ITestService.class;
//			ClassPool pool = ClassPool.getDefault();
//			CtClass proxyclazz = pool.makeClass("ExtendedProxy");
//			proxyclazz.setSuperclass(getCtClass(jadex.base.service.publish.Proxy.class, pool));
//			proxyclazz.addInterface(getCtClass(type, pool));
//			Method[] ms = type.getMethods();
//			CtMethod invoke = getCtClass(jadex.base.service.publish.Proxy.class, pool).getDeclaredMethod("invoke");
//			for(int i=0; i<ms.length; i++)
//			{
////				CtMethod method = proxyclazz.getDeclaredMethod(ms[i].getName(), getCtClasses(ms[i].getParameterTypes(), pool));
//				CtMethod m = CtNewMethod.wrapped(getCtClass(ms[i].getReturnType(), pool), ms[i].getName(), 
//					getCtClasses(ms[i].getParameterTypes(), pool), getCtClasses(ms[i].getExceptionTypes(), pool),
//					invoke, null, proxyclazz);
//				proxyclazz.addMethod(m);
//			}
//	//		clazz.addMethod(CtNewMethod.make("public double eval (double x) { return (" + args[0] + ") ; }", clazz));
//			Class cl = proxyclazz.toClass();
//			Object obj = cl.newInstance();
//			Method shm = cl.getMethod("setHandler", new Class[]{InvocationHandler.class});
//			shm.invoke(obj, new Object[]{new WebServiceToJadexWrapperInvocationHandler(null)});
//			
//	//		Class[] formalParams = new Class[] { double.class };
//	//		Method meth = clazz.getDeclaredMethod("eval", formalParams);
//	//		Object[] actualParams = new Object[] { new Double(17) };
//	//		double result = ((Double) meth.invoke(obj, actualParams)).doubleValue();
//	//		System.out.println(result);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
	
}
