package jadex.bridge;

import jadex.bridge.service.BasicService;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.Cloner;
import jadex.commons.ICloneProcessor;
import jadex.commons.SReflect;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JadexCloner
{
	/** The default cloner. */
	protected static Cloner instance;
	
	/** The processors. */
	protected static List processors;
	
	/**
	 *  Get the default cloner instance.
	 */
	public static Cloner getInstance()
	{
		if(instance==null)
		{
			synchronized(Cloner.class)
			{
				if(instance==null)
				{
					instance = new Cloner();
					instance.addImmutableType(URL.class);
					instance.addImmutableType(InetAddress.class);
					instance.addImmutableType(Inet4Address.class);
					instance.addImmutableType(Inet6Address.class);
					
					instance.addImmutableType(IComponentIdentifier.class);
					instance.addImmutableType(ComponentIdentifier.class);
				
					processors = new ArrayList();
					
					// Problem: if micro agent implements a service it cannot
					// be determined if the service or the agent should be transferred.
					// Per default a service is assumed.
					processors.add(new ICloneProcessor()
					{
						public Object process(Object object, List processors)
						{
							return BasicServiceInvocationHandler.getPojoServiceProxy(object);
						}
						
						public boolean isApplicable(Object object)
						{
							return object!=null && !(object instanceof BasicService) 
								&& object.getClass().isAnnotationPresent(Service.class);
						}
					});
				}
			}
		}
		return instance;
	}
	
	/**
	 *  Deep clone an object.
	 */
	public static Object deepCloneObject(Object object)
	{
		return getInstance().deepClone(object, null, new HashMap(), processors);
	}
	
}
