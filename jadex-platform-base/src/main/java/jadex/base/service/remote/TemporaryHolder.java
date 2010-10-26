package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;

/**
 *  A temporary holder is a data struct which keeps track of
 *  open protocols. Whenever a proxy reference is created
 *  a temporary holder is added until the normal addRef message
 *  arrives from the receiver. 
 */
public class TemporaryHolder
{
	//-------- attributes --------
	
	/** The holder cid (of the rms). */
	protected IComponentIdentifier holder;
	
	/** Number of open protocls. */
	protected int number;

	//-------- constructors --------

	/**
	 *  Create a new temporary holder.
	 */
	public TemporaryHolder(IComponentIdentifier holder)
	{
		this.holder = holder;
		this.number = 1;
	}

	//-------- methods --------
	
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
		return 31 * holder.hashCode();
	}

	/**
	 *  Test for equality.
	 *  @param obj The object to test.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof TemporaryHolder)
		{
			TemporaryHolder other = (TemporaryHolder)obj;
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
		return "TemporarayHolder(holder=" + holder + ", number=" + number+ ")";
	}
}