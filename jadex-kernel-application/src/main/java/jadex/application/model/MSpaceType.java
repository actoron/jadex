package jadex.application.model;

import jadex.bridge.modelinfo.IExtensionType;

/**
 *  Space type representation.
 */
public class MSpaceType implements IExtensionType
{
	//-------- attributes --------

	/** The name. */
	protected String name;

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
}