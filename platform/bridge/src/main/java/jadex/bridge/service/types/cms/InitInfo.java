package jadex.bridge.service.types.cms;

import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.commons.future.Future;

/**
 *  Struct that stores information about initing components.
 */
public class InitInfo
{
	//-------- attributes --------
	
	/** The component. */
	protected IPlatformComponentAccess component;
	
	/** The creation info. */
	protected CreationInfo info;
	
	/** The init future. */
	protected Future<Void> initfuture;
	
	//-------- constructors --------
	
	/**
	 *  Create a new init info.
	 */
	public InitInfo(IPlatformComponentAccess component, CreationInfo info, Future<Void> initfuture)
	{
		this.component = component;
		this.info = info;
		this.initfuture = initfuture;
	}

	//-------- methods --------
	
	/**
	 *  Get the adapter.
	 *  @return The adapter.
	 */
	public IPlatformComponentAccess getComponent()
	{
		return component;
	}

	/**
	 *  Get the info.
	 *  @return The info.
	 */
	public CreationInfo getInfo()
	{
		return info;
	}

	/**
	 *  Get the initfuture.
	 *  @return The initfuture.
	 */
	public Future<Void> getInitFuture()
	{
		return initfuture;
	}
}

