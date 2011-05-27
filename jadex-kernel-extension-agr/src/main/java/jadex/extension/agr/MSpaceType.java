package jadex.extension.agr;

import jadex.bridge.modelinfo.IExtensionType;

/**
 *  Space type representation.
 */
public class MSpaceType implements IExtensionType
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The class name. */
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
	 *  Get the classname.
	 *  @return the classname.
	 */
	public String getClassName()
	{
		return classname;
	}

	/**
	 *  Set the classname.
	 *  @param classname The classname to set.
	 */
	public void setClassName(String classname)
	{
		this.classname = classname;
	}
}