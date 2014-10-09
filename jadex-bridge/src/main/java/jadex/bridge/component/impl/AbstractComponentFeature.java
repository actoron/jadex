package jadex.bridge.component.impl;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Abstract feature that implements basic feature methods. 
 */
public abstract class AbstractComponentFeature	implements IComponentFeature
{
	//-------- attributes --------
	
	/** The component. */
	protected IInternalAccess	component;
	
	/** The creation info. */
	protected ComponentCreationInfo	cinfo;
		
	//-------- constructors --------
	
	/**
	 *  Bean constructor for type level.
	 */
	public AbstractComponentFeature()
	{
	}
	
	/**
	 *  Factory method constructor for instance level.
	 */
	protected AbstractComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		this.component	= component;
		this.cinfo	= cinfo;
	}
	
	/**
	 *  Get the component access.
	 */
	public IInternalAccess getComponent()
	{
		return component;
	}
	
	//-------- IComponentFeature interface / instance level --------
	
	/**
	 *  Initialize the feature.
	 *  Empty implementation that can be overridden.
	 */
	public IFuture<Void>	init()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Execute the main activity of the feature.
	 */
	public IFuture<Void>	body()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Shutdown the feature.
	 */
	public IFuture<Void>	shutdown()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Get external feature facade.
	 */
//	public <T> IFuture<T> getExternalFacade(Object context)
	public <T> T getExternalFacade(Object context)
	{
		return (T)this;
	}
	
	/**
	 * 
	 */
	public <T> Class<T> getExternalFacadeType(Object context)
	{
		return null;
	}
	
	//-------- IValueFetcher interface --------
	
	/**
	 *  Fetch a value via its name.
	 *  Empty default method to be overridden by subclasses. 
	 *  @param name The name.
	 *  @return The value.
	 */
	public Object fetchValue(String name)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Fetch a value via its name from an object.
	 *  Empty default method to be overridden by subclasses. 
	 *  @param name The name.
	 *  @param object The object.
	 *  @return The value.
	 */
	public Object fetchValue(String name, Object object)
	{
		throw new UnsupportedOperationException();
	}

}
