package jadex.bridge.component.impl;

import java.util.Collections;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeature;
import jadex.commons.IParameterGuesser;
import jadex.commons.IValueFetcher;
import jadex.commons.SimpleParameterGuesser;
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
		
	/** The parameter guesser. */
	protected IParameterGuesser	guesser;
		
	//-------- constructors --------
	
//	/**
//	 *  Bean constructor for type level.
//	 */
//	public AbstractComponentFeature()
//	{
//	}
	
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
	public IFuture<Void> init()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Execute the main activity of the feature.
	 */
	public IFuture<Void> body()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Check if the feature potentially executed user code in body.
	 *  Allows blocking operations in user bodies by using separate steps for each feature.
	 *  Non-user-body-features are directly executed for speed.
	 *  If unsure just return true. ;-)
	 */
	public boolean hasUserBody()
	{
		// Return true by default so it works if forgotten to override.
		return true;
	}
	
	/**
	 *  Shutdown the feature.
	 */
	public IFuture<Void> shutdown()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Kill is only invoked, when shutdown of some (e.g. other) feature does not return due to timeout.
	 *  The feature should do any kind of possible cleanup, but no asynchronous operations.
	 */
	public void kill()
	{
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
	
	/**
	 *  The feature can inject parameters for expression evaluation
	 *  by providing an optional value fetcher. The fetch order is the reverse
	 *  init order, i.e., later features can override values from earlier features.
	 */
	public IValueFetcher getValueFetcher()
	{
		return null;
	}
	
	/**
	 *  The feature can add objects for field or method injections
	 *  by providing an optional parameter guesser. The selection order is the reverse
	 *  init order, i.e., later features can override values from earlier features.
	 */
	public IParameterGuesser getParameterGuesser()
	{
		if(guesser==null)
		{
			guesser	= new SimpleParameterGuesser(Collections.singleton(this));
		}
		return guesser;
	}	
}
