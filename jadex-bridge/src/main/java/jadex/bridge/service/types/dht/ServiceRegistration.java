package jadex.bridge.service.types.dht;

import jadex.bridge.service.IServiceIdentifier;

import java.util.Calendar;

/**
 * This class represents a registration of a Service. 
 */
public class ServiceRegistration
{
	/** Lease time in ms */
	public static final int LEASE_TIME = 120 * 1000;
	
	/** The sid */
	private IServiceIdentifier sid;
	/** The timestamp of last renewal */
	private long timestamp = System.currentTimeMillis();
	
	/**
	 * Empty Constructor for Bean-serialization.
	 */
	public ServiceRegistration()
	{
	}
	
	/**
	 * Get the SID
	 * @return the sid
	 */
	public IServiceIdentifier getSid()
	{
		return sid;
	}
	
	/**
	 * Set the sid
	 * @param sid
	 */
	public void setSid(IServiceIdentifier sid)
	{
		this.sid = sid;
	}
	
	/**
	 * Get the timestamp.
	 */
	public long getTimestamp()
	{
		return timestamp;
	}
	
	/**
	 * Set the timestamp.
	 */
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		return sid.toString() + " [lease: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sid == null) ? 0 : sid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ServiceRegistration other = (ServiceRegistration)obj;
		if(sid == null)
		{
			if(other.sid != null)
				return false;
		}
		else if(!sid.equals(other.sid))
			return false;
		return true;
	}
	
}
