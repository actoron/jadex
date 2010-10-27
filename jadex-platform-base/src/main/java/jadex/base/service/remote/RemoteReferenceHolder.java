package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;

/**
 * 
 */
public class RemoteReferenceHolder
{
	/** The holder (cid of rms). */
	protected IComponentIdentifier holder;

	/** The expiry date. */
	protected long expirydate;
	
	/**
	 *  Create a new holder.
	 */
	public RemoteReferenceHolder(IComponentIdentifier holder, long expirydate)
	{
		this.holder = holder;
		this.expirydate = expirydate;
	}

	/**
	 *  Get the holder.
	 *  @return the holder.
	 */
	public IComponentIdentifier getHolder()
	{
		return holder;
	}

	/**
	 *  Set the holder.
	 *  @param holder The holder to set.
	 */
	public void setHolder(IComponentIdentifier holder)
	{
		this.holder = holder;
	}
	
	/**
	 *  Get the expirydate.
	 *  @return the expirydate.
	 */
	public long getExpiryDate()
	{
		return expirydate;
	}

	/**
	 *  Set the expirydate.
	 *  @param expirydate The expirydate to set.
	 */
	public void setExpiryDate(long expirydate)
	{
		this.expirydate = expirydate;
	}
	
	/**
	 *  Get the hashcode.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return 31 * holder.hashCode();
	}

	/**
	 *  Test for equality.
	 *  @param obj The object to test.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof RemoteReferenceHolder && getClass().equals(obj.getClass()))
		{
			RemoteReferenceHolder other = (RemoteReferenceHolder)obj;
			ret = holder.equals(other.holder);
		}
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RemoteReferenceHolder(holder=" + holder +")";
//		return "RemoteReferenceHolder(holder=" + holder + ", expirydater=" + expirydate+ ")";
	}
}
