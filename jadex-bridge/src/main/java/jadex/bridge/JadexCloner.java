package jadex.bridge;

import jadex.commons.Cloner;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;

public class JadexCloner
{
	/** The default cloner. */
	protected static Cloner instance;
	
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
		return getInstance().deepClone(object, null, new HashMap());
	}
}
