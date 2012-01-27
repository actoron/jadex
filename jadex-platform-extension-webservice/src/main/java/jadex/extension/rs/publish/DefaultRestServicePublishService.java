package jadex.extension.rs.publish;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;

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

//			Object pr = createProxy(service, cl, pi.getServiceType().getType(cl));
//			cl.loadClass("com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory");
			
			Map<String, Object> props = new HashMap<String, Object>();
			props.put(pi.getPublishId(), service);
			String pack = pi.getServiceType().getType(cl).getPackage().getName();
			String jerseypck = "com.sun.jersey.config.property.packages";
			PackagesResourceConfig config = new PackagesResourceConfig(new String[]{jerseypck, pack});
			config.setPropertiesAndFeatures(props);
			
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
	 *  Create a service proxy.
	 *  @param service The Jadex service.
	 *  @param classloader The classloader.
	 *  @param type The web service interface type.
	 *  @return The proxy object.
	 */
	protected Object createProxy(IService service, ClassLoader classloader, Class type)
	{
//		System.out.println("createProxy: "+service.getServiceIdentifier());
		Object ret = null;
		try
		{
			ret = type.getConstructors()[0].newInstance(new Object[]{service});
//
//			Class clazz = getProxyClass(type, classloader);
////			System.out.println("proxy class: "+clazz.getPackage()+" "+clazz.getClassLoader()+" "+classloader);
//			ret = clazz.newInstance();
//			Method shm = clazz.getMethod("setHandler", new Class[]{InvocationHandler.class});
//			shm.invoke(ret, new Object[]{new WebServiceToJadexWrapperInvocationHandler(service)});
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ret;
	}
	
}
