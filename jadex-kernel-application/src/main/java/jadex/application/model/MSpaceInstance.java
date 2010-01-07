package jadex.application.model;


/**
 *  Space instance representation.
 */
public abstract class MSpaceInstance
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The space type name. */
	protected String type;
	
	/** The space type (resolved during loading). */
	protected MSpaceType	spacetype;
	
	//-------- constructors --------

	/**
	 *  Create a new space type.
	 */
	public MSpaceInstance()
	{
	}

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
	 *  Get the type name.
	 *  @return The type name. 
	 */
	public String getTypeName()
	{
		return this.type;
	}

	/**
	 *  Set the type name.
	 *  @param type The type name to set.
	 */
	public void setTypeName(String type)
	{
		this.type = type;
	}
	
	/**
	 *  Get the type of this element.
	 *  @return The structure type.
	 */
	public MSpaceType getType()
	{
		return spacetype;
	}

	/**
	 *  Set the type of this element.
	 *  @return The structure type.
	 */
	public void	setType(MSpaceType spacetype)
	{
		this.spacetype	= spacetype;
	}

	/**
	 *  Get the implementation class of the space.
	 */
	public abstract Class	getClazz();
}