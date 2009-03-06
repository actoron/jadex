package jadex.adapter.base.agr;

/**
 * 
 */
public class MGroupInstance
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The type name. */
	protected String typename;
	
	//-------- methods --------
	
	/**
	 *  Set the name of the group type.
	 *  @param name	The name of the group type.
	 */
	public void	setName(String name)
	{
		this.name	= name;
	}
	
	/**
	 *  Get the name of the group type.
	 *  @return The name of the group type.
	 */
	public String	getName()
	{
		return this.name;
	}

	/**
	 * @return the typename
	 */
	public String getTypeName()
	{
		return this.typename;
	}

	/**
	 * @param typename the typename to set
	 */
	public void setTypeName(String typename)
	{
		this.typename = typename;
	}
	
	
}
