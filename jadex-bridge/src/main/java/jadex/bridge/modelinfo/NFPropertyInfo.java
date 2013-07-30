/**
 * 
 */
package jadex.bridge.modelinfo;

import jadex.bridge.ClassInfo;

/**
 *
 */
public class NFPropertyInfo
{
	/** The property name. */
	protected String name;
	
	/** The property class. */
	protected ClassInfo clazz;

	/**
	 *  Create a new property.
	 *  @param name The name.
	 *  @param clazz The clazz.
	 */
	public NFPropertyInfo(String name, ClassInfo clazz)
	{
		this.name = name;
		this.clazz = clazz;
	}

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
	public ClassInfo getClazz()
	{
		return clazz;
	}

	/**
	 *  Set the clazz.
	 *  @param clazz The clazz to set.
	 */
	public void setClazz(ClassInfo clazz)
	{
		this.clazz = clazz;
	}
	
}
