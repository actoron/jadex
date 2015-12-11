package jadex.platform.service.remote;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ITargetResolver;
import jadex.commons.MethodInfo;
import jadex.commons.SUtil;
import jadex.commons.transformation.annotations.Alias;

/**
 *  Info struct that holds all necessary model information to generate
 *  a proxy on the local platform. Is necessary because a proxy
 *  cannot be directly created on the remote side and then sent 
 *  per message to the calling side.
 */
@Alias("jadex.base.service.remote.ProxyInfo")
public class ProxyInfo
{
	//-------- attributes --------
	
	/** The target class. */
	protected List<Class<?>> targetinterfaces;
		
	/** The excluded methods. */
	protected Set<MethodInfo> excluded;
	
	/** The uncached methods. */
	protected Set<MethodInfo> uncached;

	/** The synchronous methods. */
	protected Set<MethodInfo> synchronous;
	
	/** The replacements for methods (method-info -> replacement method). */
	protected Map<MethodInfo, IMethodReplacement> replacements;
	
	/** The timeouts for methods (method-info -> long). */
	protected Map<MethodInfo, Long> timeouts;
	
	/** The secure transport methods. */
	protected Set<MethodInfo> secure;
	
	/** The target resolver (for intelligent proxies). */
	protected Class<ITargetResolver> trcl;
	
	//-------- constructors --------
	
	/**
	 *  Create a new proxy info.
	 */
	public ProxyInfo()
	{
	}

	/**
	 *  Create a new proxy info.
	 */
	public ProxyInfo(Class<?>[] targetinterfaces)
	{
		setTargetInterfaces(targetinterfaces);
	}
	
	//-------- methods --------
	
//	/**
//	 *  Get the remote reference.
//	 *  @return the remote reference.
//	 */
//	public RemoteReference getRemoteReference()
//	{
//		return rr;
//	}
//
//	/**
//	 *  Set the rr.
//	 *  @param rr The rr to set.
//	 */
//	public void setRemoteReference(RemoteReference rr)
//	{
//		this.rr = rr;
//	}
	
	
	/**
	 *  Get the timeouts
	 *  @return The timeouts.
	 */
	public Map<MethodInfo, Long> getMethodTimeouts()
	{
		return timeouts;
	}
	
	/**
	 *  Set the timeouts.
	 *  @param timeouts	The timeouts.
	 */
	public void setMethodTimeouts(Map<MethodInfo, Long> timeouts)
	{
		this.timeouts = timeouts;
	}
	
	/**
	 *  Get a timeout.
	 *  @param method	The method.
	 *  @return	The timeout or -1 if none.
	 */
	public long	getMethodTimeout(IComponentIdentifier platform, Method method)
	{
		long	ret	= Starter.getRemoteDefaultTimeout(platform);
		MethodInfo	key	= new MethodInfo(method);
		if(timeouts!=null && timeouts.containsKey(key))
		{
			ret	= ((Number)timeouts.get(key)).longValue();
		}
		return ret;
	}


	/**
	 *  Add a timeout for a method.
	 *  @param m	The method info.
	 *  @param timeout	The timeout.
	 */
	public void addMethodTimeout(MethodInfo m, long timeout)
	{
		if(timeouts==null)
			timeouts = new HashMap<MethodInfo, Long>();
		timeouts.put(m, Long.valueOf(timeout));
	}
		
	/**
	 *  Get the replacements
	 *  @return The replacements.
	 */
	public Map<MethodInfo, IMethodReplacement> getMethodReplacements()
	{
		return replacements;
	}
	
	/**
	 *  Set the replacements.
	 *  @param replacements	The replacements.
	 */
	public void setMethodReplacements(Map<MethodInfo, IMethodReplacement> replacements)
	{
		this.replacements = replacements;
	}
	
	/**
	 *  Add a replacement.
	 */
	public void addMethodReplacement(MethodInfo method, IMethodReplacement replacement)
	{
		if(replacements==null)
			replacements = new HashMap<MethodInfo, IMethodReplacement>();
		replacements.put(method, replacement);
	}
	
	/**
	 *  Get a replacements.
	 */
	public IMethodReplacement	getMethodReplacement(Method method)
	{
		IMethodReplacement	ret	= null;
		if(replacements!=null)
		{
			ret	= (IMethodReplacement)replacements.get(new MethodInfo(method));
		}
		return ret;
	}
	
	/**
	 *  Test if method is replaced.
	 *  @param m Method to test.
	 *  @return True, if is replaced.
	 */
	public boolean isReplaced(Method m)
	{
		return replacements!=null && replacements.containsKey(new MethodInfo(m));
	}
	
	/**
	 *  Get the target remote interfaces.
	 *  @return the target remote interfaces.
	 */
	public Class<?>[] getTargetInterfaces()
	{
		return targetinterfaces==null? SUtil.EMPTY_CLASS_ARRAY: (Class[])targetinterfaces.toArray(new Class[targetinterfaces.size()]);
	}

	/**
	 *  Set the target remote interfaces.
	 *  @param targetinterfaces The targetinterfaces to set.
	 */
	public void setTargetInterfaces(Class<?>[] targetinterfaces)
	{
		if(this.targetinterfaces!=null)
			this.targetinterfaces.clear();
		if(targetinterfaces!=null)
		{
			for(int i=0; i<targetinterfaces.length; i++)
			{
				addTargetInterface(targetinterfaces[i]);
			}
		}
	}
	
	/**
	 *  Add a target interface.
	 *  @param targetinterface The target interface.
	 */
	public void addTargetInterface(Class<?> targetinterface)
	{
		// Might be null, when class not available locally.
		if(targetinterface!=null)
		{
			if(targetinterfaces==null)
				targetinterfaces = new ArrayList<Class<?>>();
			targetinterfaces.add(targetinterface);
		}
	}

	/**
	 *  Get the excluded.
	 *  @return the excluded.
	 */
	public Set<MethodInfo> getExcludedMethods()
	{
		return excluded;
	}

	/**
	 *  Set the excluded.
	 *  @param excluded The excluded to set.
	 */
	public void setExcludedMethods(Set<MethodInfo> excluded)
	{
		this.excluded = excluded;
	}
	
	/**
	 *  Add an excluded method.
	 *  @param Method excluded.
	 */
	public void addExcludedMethod(MethodInfo m)
	{
		if(excluded==null)
			excluded = new HashSet<MethodInfo>();
		excluded.add(m);
	}
	
	/**
	 *  Test if method is excluded.
	 *  @param m Method to test.
	 *  @return True, if is excluded.
	 */
	public boolean isExcluded(Method m)
	{
		return excluded!=null && excluded.contains(new MethodInfo(m));
	}

	/**
	 *  Get the uncached.
	 *  @return the uncached.
	 */
	public Set<MethodInfo> getUncachedMethods()
	{
		return uncached;
	}

	/**
	 *  Set the uncached.
	 *  @param uncached The uncached to set.
	 */
	public void setUncachedMethods(Set<MethodInfo> uncached)
	{
		this.uncached = uncached;
	}
	
	/**
	 *  Add an uncached method.
	 *  @param Method excluded.
	 */
	public void addUncachedMethod(MethodInfo m)
	{
		if(uncached==null)
			uncached = new HashSet<MethodInfo>();
		uncached.add(m);
	}
	
	/**
	 *  Test if method is uncached.
	 *  @param m Method to test.
	 *  @return True, if is uncached.
	 */
	public boolean isUncached(Method m)
	{
		return uncached!=null && uncached.contains(new MethodInfo(m));
	}

	/**
	 *  Get the synchronous.
	 *  @return the synchronous.
	 */
	public Set<MethodInfo> getSynchronousMethods()
	{
		return synchronous;
	}

	/**
	 *  Set the synchronous.
	 *  @param synchronous The synchronous to set.
	 */
	public void setSynchronousMethods(Set<MethodInfo> synchronous)
	{
		this.synchronous = synchronous;
	}
	
	/**
	 *  Add an synchronous method.
	 *  @param Method excluded.
	 */
	public void addSynchronousMethod(MethodInfo m)
	{
		if(synchronous==null)
			synchronous = new HashSet<MethodInfo>();
		synchronous.add(m);
	}
	
	/**
	 *  Test if method is synchronous.
	 *  @param m Method to test.
	 *  @return True, if is synchronous.
	 */
	public boolean isSynchronous(Method m)
	{
		return synchronous!=null && synchronous.contains(new MethodInfo(m));
	}

	/**
	 *  Get the secure.
	 *  @return the secure.
	 */
	public Set<MethodInfo> getSecureMethods()
	{
		return secure;
	}

	/**
	 *  Set the secure.
	 *  @param secure The secure to set.
	 */
	public void setSecureMethods(Set<MethodInfo> secure)
	{
		this.secure = secure;
	}
	
	/**
	 *  Add an secure method.
	 *  @param m Method.
	 */
	public void addSecureMethod(MethodInfo m)
	{
		if(secure==null)
			secure = new HashSet<MethodInfo>();
		secure.add(m);
	}
	
	/**
	 *  Test if method is secure.
	 *  @param m Method to test.
	 *  @return True, if is secure.
	 */
	public boolean isSecure(Method m)
	{
		return secure!=null && secure.contains(new MethodInfo(m));
	}
	
	
	
	/**
	 *  Get the target determiner clazz.
	 *  @return The target determiner clazz
	 */
	public Class<ITargetResolver> getTargetResolverClazz() 
	{
		return trcl;
	}

	/**
	 *  Set the target determiner class.
	 *  @param tdcl The target determiner clazz to set
	 */
	public void setTargetResolverClazz(Class<ITargetResolver> tdcl) 
	{
		this.trcl = tdcl;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ProxyInfo(excluded=" + excluded + ", uncached=" + uncached
			+ ", synchronous=" + synchronous + ", replacements="
			+ replacements + ", targetinterfaces="
			+ targetinterfaces + ")";
	}
}
