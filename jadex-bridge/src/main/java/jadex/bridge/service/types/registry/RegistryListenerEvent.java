package jadex.bridge.service.types.registry;

import jadex.bridge.ClassInfo;
import jadex.bridge.service.IService;

/**
 * 
 */
public class RegistryListenerEvent
{
	public enum Type
	{
		ADDED, REMOVED
	}
	
	/** The type. */
	protected Type type;
	
	/** The class info. */
	protected ClassInfo classInfo;
	
	/** The service. */
	protected IService service;

	/**
	 *  Create a new listener event.
	 *  @param type The type.
	 *  @param classInfo The class info.
	 *  @param service The service.
	 */
	public RegistryListenerEvent(Type type, ClassInfo classInfo, IService service)
	{
		this.type = type;
		this.classInfo = classInfo;
		this.service = service;
	}

	/**
	 *  Get the type.
	 *  @return The type
	 */
	public Type getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set
	 */
	public void setType(Type type)
	{
		this.type = type;
	}

	/**
	 *  Get the classInfo.
	 *  @return The classInfo
	 */
	public ClassInfo getClassInfo()
	{
		return classInfo;
	}

	/**
	 *  Set the classInfo.
	 *  @param classInfo The classInfo to set
	 */
	public void setClassInfo(ClassInfo classInfo)
	{
		this.classInfo = classInfo;
	}

	/**
	 *  Get the service.
	 *  @return The service
	 */
	public IService getService()
	{
		return service;
	}

	/**
	 *  Set the service.
	 *  @param service The service to set
	 */
	public void setService(IService service)
	{
		this.service = service;
	}
}
