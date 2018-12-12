package jadex.bdi.testcases.misc;

/**
 *  Simple person class with a name and an address.
 */
public class Person
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The address. */
	protected String address;

	//-------- constructors --------

	/**
	 *  Create a new person.
	 */
	public Person(String name, String address)
	{
		this.name = name;
		this.address = address;
	}

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the address.
	 *  @return The address.
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 *  Set the address.
	 *  @param address The address.
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}

	//-------- Equals and hashcode --------

	/**
	 *  Test if two persons are equal.
	 * @param o
	 * @return	True if the object is an equal person.
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		if(o instanceof Person)
		{
			final Person person = (Person)o;
			if(address.equals(person.address) && name.equals(person.name))
				ret = true;
		}
		return ret;
	}

	/**
	 *  Calculate the hashcode.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return name.hashCode()^address.hashCode();
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return name+":"+address;
	}
}
