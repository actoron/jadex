package jadex.bridge.service;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.SReflect;


/**
 *  Info for provided services.
 */
public class ProvidedServiceInfo
{
	//-------- attributes --------

	/** The name (used for referencing). */
	protected String name;
	
	/** The service interface type as string. */
	protected String typename;
	
	/** The service interface type. */
	protected Class<?> type;
	
	/** The service implementation. */
	protected ProvidedServiceImplementation implementation;
		
	//-------- constructors --------
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo()
	{
		// bean constructor
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(String name, Class<?> type, ProvidedServiceImplementation implementation)
	{
		this.name = name;
		this.implementation = implementation;
		setType(type);
	}
	
//	/**
//	 *  Create a new service info.
//	 */
//	public ProvidedServiceInfo(ProvidedServiceInfo orig)
//	{
//		this(orig.getType(), new ProvidedServiceImplementation(orig.getImplementation()));
//	}
	
	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return the name.
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
	 *  Get the type name.
	 *  @return the type name.
	 */
	public String getTypeName()
	{
		return typename;
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
	public Class<?> getType(IModelInfo info, ClassLoader cl)
	{
		if(type==null && typename!=null)
		{
			type = SReflect.findClass0(typename, info.getAllImports(), cl);
		}
//		else if(type==null)
//		{
//			System.out.println("Type is null: "+this);
//		}
			
		
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(Class<?> type)
	{
		this.type = type;
		this.typename	= type.getName();
	}

	/**
	 *  Get the implementation.
	 *  @return The implementation.
	 */
	public ProvidedServiceImplementation getImplementation()
	{
		return implementation;
	}

	/**
	 *  Set the implementation.
	 *  @param implementation The implementation to set.
	 */
	public void setImplementation(ProvidedServiceImplementation implementation)
	{
		this.implementation = implementation;
	}
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ProvidedServiceInfo(name="+name+", type="+ type + ", implementation="+ implementation + ")";
	}
}
