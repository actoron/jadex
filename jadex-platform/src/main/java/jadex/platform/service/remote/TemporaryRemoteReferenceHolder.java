package jadex.platform.service.remote;

import jadex.bridge.IComponentIdentifier;

/**
 *  A temporary holder is a data struct which keeps track of
 *  open protocols. Whenever a proxy reference is created
 *  a temporary holder is added until the normal addRef message
 *  arrives from the receiver. 
 */
public class TemporaryRemoteReferenceHolder extends RemoteReferenceHolder
{
	//-------- attributes --------
	
	/** Number of open protocls. */
	protected int number;

	//-------- constructors --------

	/**
	 *  Create a new temporary holder.
	 */
	public TemporaryRemoteReferenceHolder(IComponentIdentifier holder, long expirydate)
	{
		super(holder, expirydate);
		this.number = 1;
	}

	//-------- methods --------
	
	/**
	 *  Get the number.
	 *  @return the number.
	 */
	public int getNumber()
	{
		return number;
	}

	/**
	 *  Set the number.
	 *  @param number The number to set.
	 */
	public void setNumber(int number)
	{
		this.number = number;
	}

	/**
	 *  Get the hashcode.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return 19 * holder.hashCode();
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "TemporaryRemoteReferenceHolder(holder=" + holder + ", number=" + number+ ")";
//		return "TemporaryRemoteReferenceHolder(holder=" + holder + ", number=" + number+ ", expirydate=" + expirydate+")";
	}
}