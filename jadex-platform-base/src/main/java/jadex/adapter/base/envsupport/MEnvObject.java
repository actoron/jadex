package jadex.adapter.base.envsupport;

/**
 *  Representation of an environment object.
 */
public class MEnvObject
{
	//-------- attributes --------

	/** The name. */
	protected String name;
	
	/** The type. */
	protected String type;
	
	/** The owner. */
	protected String owner;

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type.
	 *  @param name The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	
	/**
	 *  Get the owner.
	 *  @return The owner.
	 */
	public String getOwner()
	{
		return this.owner;
	}

	/**
	 *  Set the owner.
	 *  @param name The owner to set.
	 */
	public void setOwner(String owner)
	{
		this.owner = owner;
	}
}
