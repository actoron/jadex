package jadex.bridge;

import jadex.commons.SReflect;


/**
 *  The class info struct serves for saving class information.
 *  A class info may hold a class itself or the class name for
 *  resolving the class. This struct should be used at all places
 *  where classes are meant to be send to remote nodes. In this
 *  case the class info will only transfer the class name forcing
 *  the receiver to lookup the class itself. The class loader
 *  for resolving a class info can be found by using the corresponding
 *  resource identifier (rid) of the component or service that uses
 *  the class.
 */
public class ClassInfo
{
	//-------- attributes --------
	
	/** The service interface type as string. */
	protected String typename;
	
	/** The service interface type. */
	protected Class<?> type;

	//-------- constructors --------
	
	/**
	 *  Create a new class info.
	 */
	public ClassInfo()
	{
		// Bean constructor, do not delete.
	}

	//-------- methods --------
	
	/**
	 *  Create a new class info.
	 *  @param type The class info.
	 */
	public ClassInfo(Class<?> type)
	{
		if(type==null)
			throw new IllegalArgumentException("Must not null.");
		this.type = type; // remember only classname to avoid classloader dependencies
//		this.typename = SReflect.getClassName(type);
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
		if(type==null && typename!=null && cl!=null)
		{
			type = SReflect.classForName0(typename, cl);
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
	
//	/**
//	 *  Get the type.
//	 *  @return The type.
//	 */
//	public Class<?> getType()
//	{
//		return type;
//	}

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
