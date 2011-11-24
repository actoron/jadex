package jadex.base.service.publish;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;

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
		Object pr = createProxy(service.getServiceIdentifier().getServiceType());
		Endpoint endpoint = Endpoint.publish(pid, pr);
		if(endpoints==null)
			endpoints = new HashMap<IServiceIdentifier, Endpoint>();
		endpoints.put(service.getServiceIdentifier(), endpoint);
		return IFuture.DONE;
	}
	
//	public static void main(String[] args)
//	{
//		try
//		{
//			Class type = ITestService.class;
//			ClassPool pool = ClassPool.getDefault();
//			CtClass ctclazz = pool.makeClass("Proxy");
//			ClassPath cp = new ClassClassPath(type);
//	        pool.insertClassPath(cp);
//			ctclazz.addInterface(pool.get(type.getName()));
//			Method[] ms = type.getMethods();
//			for(int i=0; i<ms.length; i++)
//			{
////				CtNewMethod.wrapped(ms[i].getReturnType(), ms[i].getName(), 
////					ms[i].getParameterTypes(), ms[i].getExceptionTypes(), body, constParam, declaring);
//			}
//	//		clazz.addMethod(CtNewMethod.make("public double eval (double x) { return (" + args[0] + ") ; }", clazz));
//			Class cl = ctclazz.toClass();
//			Object obj = cl.newInstance();
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
	
	protected Object createProxy(Class type)
	{
//		ClassPool pool = ClassPool.getDefault();
//		CtClass ctclazz = pool.makeClass("Proxy");
//		ClassPath cp = new ClassClassPath(type);
//        pool.insertClassPath(cp);
//		ctclazz.addInterface(pool.get(type.getName()));
//		Method[] ms = type.getMethods();
//		for(int i=0; i<ms.length; i++)
//		{
//			CtNewMethod.copy(src, declaring, map)
//		}
////		clazz.addMethod(CtNewMethod.make("public double eval (double x) { return (" + args[0] + ") ; }", clazz));
//		Class cl = clazz.toClass();
//		Object obj = clazz.newInstance();
//		Class[] formalParams = new Class[] { double.class };
//		Method meth = clazz.getDeclaredMethod("eval", formalParams);
//		Object[] actualParams = new Object[] { new Double(17) };
//		double result = ((Double) meth.invoke(obj, actualParams)).doubleValue();
//		System.out.println(result);
		return null;
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
	
}
