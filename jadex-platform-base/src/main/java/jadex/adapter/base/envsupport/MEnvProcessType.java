package jadex.adapter.base.envsupport;

/**
 *  Description of an process type. 
 */
public class MEnvProcessType
{
	//-------- attributes --------

	/** The name. */
	protected String name;
	
	/** The implementation class name. */
	protected String classname;

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
	 *  Get the class name.
	 *  @return The class name.
	 */
	public String getClassName()
	{
		return this.classname;
	}

	/**
	 *  Set the class name.
	 *  @param name The class name to set.
	 */
	public void setClassName(String classname)
	{
		this.classname = classname;
	}
	
}
