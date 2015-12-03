package jadex.bridge.service.types.awareness;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
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
	protected ITransportComponentIdentifier cid;
	
	/** Component id of local proxy (if any). */
	protected IFuture<IComponentIdentifier> proxy;
	
//	/** Time when last awareness info was received. */
//	protected long time;
	
	/** The current send delay time. */
//	protected long delay;
	// The times per source, i.e. awa mechanism. */
	protected Map<String, long[]> timedelays;
	
	// todo: save time and delay for each entry :-(

	/** Flag indicating that the remote component has excluded our local component. */
	protected boolean	remoteexcluded;
	
	/** Platform properties (if any). */
	protected Map<String, String>	properties;
	
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
	public DiscoveryInfo(ITransportComponentIdentifier cid, IFuture<IComponentIdentifier> proxy, //long time, //long delay, 
			boolean remoteexcluded, Map<String, String> properties)
	{
		this.cid = cid;
		this.proxy = proxy;
//		this.time = time;
//		this.delay = delay;
		this.remoteexcluded = remoteexcluded;
		this.properties	= properties;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the component identifier.
	 *  @return the component identifier.
	 */
	public ITransportComponentIdentifier getComponentIdentifier()
	{
		return cid;
	}

	/**
	 *  Set the component identifier.
	 *  @param component identifier The component identifier to set.
	 */
	public void setComponentIdentifier(ITransportComponentIdentifier componentIdentifier)
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
		return getMaxEntry()[0];
	}
//
//	/**
//	 *  Set the time.
//	 *  @param time The time to set.
//	 */
//	public void setTime(long time)
//	{
//		this.time = time;
//	}
	
	/**
	 *  Get the time.
	 *  @return the time.
	 */
	public long[] getMaxEntry()
	{
		long ret[] = new long[2];
		
		if(timedelays!=null)
		{
			for(long[] td :timedelays.values())
			{
				// unlimited immediately wins
				if(td[1]==-1)
				{
					ret = td;
					break;
				}
				else if(td[0]+td[1]>ret[0]+ret[1])
				{
					ret = td;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Get the time.
	 *  @return the time.
	 */
	public long getDelay()
	{
		return getMaxEntry()[1];
	}
	
	/**
	 *  Get the time.
	 *  @return the time.
	 */
	public long getDelay(String src)
	{
		long ret = 0;
		if(timedelays!=null && timedelays.containsKey(src))
		{
			ret = timedelays.get(src)[1];
		}
		return ret;
	}
	
	/**
	 *  Get the time.
	 *  @return the time.
	 */
	public long getTime(String src)
	{
		long ret = 0;
		if(timedelays!=null && timedelays.containsKey(src))
		{
			ret = timedelays.get(src)[0];
		}
		return ret;
	}
	
	/**
	 *  Add a new time support.
	 */
	public void setTimeDelay(String src, long time, long delay)
	{
		if(timedelays==null)
			timedelays = new HashMap<String, long[]>();
		timedelays.put(src, new long[]{time, delay});
	}
	
	/**
	 *  Remove a time support.
	 */
	public void removeTimeDelay(String src)
	{
		if(timedelays!=null)
			timedelays.remove(src);
	}

//	/**
//	 *  Get the delay.
//	 *  @return the delay.
//	 */
//	public long getDelay()
//	{
//		return delay;
//	}
//
//	/**
//	 *  Set the delay.
//	 *  @param delay The delay to set.
//	 */
//	public void setDelay(long delay)
//	{
//		this.delay = delay;
//	}
	
	/**
	 *  Get the delays.
	 *  @return The delays.
	 */
	public Map<String, long[]> getTimeDelays()
	{
		return timedelays==null? Collections.EMPTY_MAP: timedelays;
	}

	/**
	 *  Set the delays.
	 *  @param delays The delays to set.
	 */
	public void setDelays(Map<String, long[]> timedelays)
	{
		this.timedelays = timedelays;
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
	 *  Get the properties.
	 *  @return The properties, if any.
	 */
	public Map<String, String>	getProperties()
	{
		return properties;
	}
	
	/**
	 *  Set the properties.
	 *  @param props The properties.
	 */
	public void	setProperties(Map<String, String> props)
	{
		this.properties	= props;
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
	 *  Check, if the platform is still alive.
	 *  The liveness is calculated based on current time and delay and last received update.
	 */
	public boolean	isAlive()
	{
		boolean ret = false;
		
		if(timedelays!=null)
		{
			for(String key : timedelays.keySet().toArray(new String[0]))
			{
				long[] td = timedelays.get(key);
				// unlimited immediately wins
				if(td[1]==-1)
				{
					ret = true;
				}
				else //if(td[0]+td[1]>ret[0]+ret[1])
				{
					// Allow three missed updates and some time buffer before delete
					long t = (long)(td[0]+td[1]*3.2);
					if(t>System.currentTimeMillis())
					{
						ret = true;
					}
					else
					{
						// remove outtimed entry
						timedelays.remove(key);
					}
				}
			}
		}

		return ret;
		
//		long[] max = getMaxEntry();
//		return max[0]>0 && (max[1]==-1 || max[0]+max[1]*3.2>System.currentTimeMillis()); 
		// Allow three missed updates and some time buffer before delete
//		return getTime()>0 && (getDelay()==-1 || getTime()+getDelay()*3.2 > System.currentTimeMillis());
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "DiscoveryInfo(cid=" + cid + ", proxy=" + proxy
			+ ", time=" + getTimeDelays() + ")";
	}
}
