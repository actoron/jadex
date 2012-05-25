package jadex.bridge.service.types.awareness;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;

/**
 *  Local information about discovered platforms.
 */
public class DiscoveryInfo
{
	//-------- constants --------
	
//	/** Constant for time now. */
//	public static long NOW = 0;
	
	//-------- attributes --------
	
	/** The component identifier of the remote component. */
	public IComponentIdentifier cid;
	
	/** Component id of local proxy (if any). */
	public IFuture<IComponentIdentifier> proxy;
	
	/** Time when last awareness info was received. */
	public long time;
	
	/** The current send delay time. */
	public long delay;

	/** Flag indicating that the remote component has excluded our local component. */
	public boolean	remoteexcluded;

	//-------- constructors --------
	
	/**
	 *  Create a new discovery info.
	 */
	public DiscoveryInfo()
	{
	}
	
	/**
	 *  Create a new discovery info.
	 */
	public DiscoveryInfo(IComponentIdentifier cid, IFuture<IComponentIdentifier> proxy, long time, long delay, boolean remoteexcluded)
	{
		this.cid = cid;
		this.proxy = proxy;
		this.time = time;
		this.delay = delay;
		this.remoteexcluded = remoteexcluded;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the component identifier.
	 *  @return the component identifier.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return cid;
	}

	/**
	 *  Set the component identifier.
	 *  @param component identifier The component identifier to set.
	 */
	public void setComponentIdentifier(IComponentIdentifier componentIdentifier)
	{
		this.cid = componentIdentifier;
	}
	
	/**
	 *  Get the proxy.
	 *  @return the proxy.
	 */
	public IFuture<IComponentIdentifier> getProxy()
	{
		return proxy;
	}

	/**
	 *  Set the proxy.
	 *  @param proxy The proxy to set.
	 */
	public void setProxy(IFuture<IComponentIdentifier> proxy)
	{
		this.proxy = proxy;
	}

	/**
	 *  Get the time.
	 *  @return the time.
	 */
	public long getTime()
	{
		return time;
	}

	/**
	 *  Set the time.
	 *  @param time The time to set.
	 */
	public void setTime(long time)
	{
		this.time = time;
	}

	/**
	 *  Get the delay.
	 *  @return the delay.
	 */
	public long getDelay()
	{
		return delay;
	}

	/**
	 *  Set the delay.
	 *  @param delay The delay to set.
	 */
	public void setDelay(long delay)
	{
		this.delay = delay;
	}

	/**
	 *  Is the local platform excluded by the remote platform?
	 *  @return The remote excluded flag.
	 */
	public boolean	isRemoteExcluded()
	{
		return remoteexcluded;
	}

	/**
	 *  Set the remote excluded flag.
	 *  @param remoteexcluded Is the local platform excluded by the remote platform?
	 */
	public void setRemoteExcluded(boolean remoteexcluded)
	{
		this.remoteexcluded = remoteexcluded;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cid == null) ? 0 : cid.hashCode());
//		result = prime * result + (int)(delay ^ (delay >>> 32));
//		result = prime * result + ((proxy == null) ? 0 : proxy.hashCode());
//		result = prime * result + (int)(time ^ (time >>> 32));
		return result;
	}

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof DiscoveryInfo)
		{
			DiscoveryInfo other = (DiscoveryInfo)obj;
			ret = SUtil.equals(cid, other.cid);
//				&& delay==other.delay && SUtil.equals(proxy, other.proxy) && time==other.time;
		}
		return ret;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "DiscoveryInfo(cid=" + cid + ", proxy=" + proxy
			+ ", time=" + time + ", delay=" + delay + ")";
	}
}
