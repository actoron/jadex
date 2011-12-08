package jadex.bridge;

import jadex.commons.SReflect;

/**
 * 
 */
public class ClassInfo
{
	/** The service interface type as string. */
	protected String typename;
	
	/** The service interface type. */
	protected Class<?> type;

	/**
	 * 
	 */
	public ClassInfo()
	{
	}

	/**
	 *  Create a new class info.
	 *  @param type The class info.
	 */
	public ClassInfo(Class<?> type)
	{
		if(type==null)
			throw new IllegalArgumentException("Must not null.");
		this.type = type;
	}
	
	/**
	 *  Create a new class info.
	 *  @param type The class info.
	 */
	public ClassInfo(String typename)
	{
		if(typename==null)
			throw new IllegalArgumentException("Must not null.");
		this.typename = typename;
	}

	/**
	 *  Get the type name.
	 *  @return the type name.
	 */
	public String getTypeName()
	{
		return typename!=null? typename: type!=null? SReflect.getClassName(type): null;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setTypeName(String typename)
	{
		this.typename = typename;
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public Class<?> getType(ClassLoader cl)
	{
		if(type==null && typename!=null)
		{
			type = SReflect.findClass0(typename, null, cl);
		}
		return type;
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public Class<?> getType(ClassLoader cl, String[] imports)
	{
		if(type==null && typename!=null)
		{
			type = SReflect.findClass0(typename, imports, cl);
		}
		return type;
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public Class<?> getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	// renamed to exclude from XML (@XMLExclude cannot be used due to module deps)
	public void setTheType(Class<?> type)
	{
		this.type = type;
	}
}
