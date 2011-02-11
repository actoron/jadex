package jadex.base.service.awareness;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;

/**
 *  Info about a discovered component.
 */
public class DiscoveryInfo
{
	//-------- constants --------
	
//	/** Constant for time now. */
//	public static long NOW = 0;
	
	//-------- attributes --------
	
	/** The component identifier of the remote component. */
	public IComponentIdentifier cid;
	
	/** Flag if a proxy exists. */
	public boolean proxy;
	
	/** Time when last awareness info was received. */
	public long time;
	
	/** The current send delay time. */
	public long delay;

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
	public DiscoveryInfo(IComponentIdentifier cid, boolean proxy, long time, long delay)
	{
		this.cid = cid;
		this.proxy = proxy;
		this.time = time;
		this.delay = delay;
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
	public boolean isProxy()
	{
		return proxy;
	}

	/**
	 *  Set the proxy.
	 *  @param proxy The proxy to set.
	 */
	public void setProxy(boolean proxy)
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
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cid == null) ? 0 : cid.hashCode());
		result = prime * result + (int)(delay ^ (delay >>> 32));
		result = prime * result + (proxy ? 1231 : 1237);
		result = prime * result + (int)(time ^ (time >>> 32));
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
			ret = SUtil.equals(cid, other.cid) && delay==other.delay
				&& proxy==other.proxy && time==other.time;
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
