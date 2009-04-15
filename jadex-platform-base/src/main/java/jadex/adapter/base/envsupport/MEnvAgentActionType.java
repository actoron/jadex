package jadex.adapter.base.envsupport;

/**
 *  Description of an action type. 
 */
public class MEnvAgentActionType
{
	//-------- attributes --------

	/** The name. */
	protected String name;
	
	/** The implementation clazz. */
	protected Class clazz;

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
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public Class getClazz()
	{
		return this.clazz;
	}

	/**
	 *  Set the class name.
	 *  @param name The class name to set.
	 */
	public void setClazz(Class clazz)
	{
		this.clazz = clazz;
	}
	
}
