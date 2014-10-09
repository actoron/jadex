package jadex.bridge.component;

import jadex.commons.IValueFetcher;
import jadex.commons.future.IFuture;

/**
 *  A component feature is a pluggable part of the state and behavior
 *  of a component.
 *  This interface follows the prototype-instance-pattern and represents
 *  the type level (i.e. factory) as well as the instance level.
 *  
 *  The feature can inject parameters for expression evaluation
 *  using the value fetcher super interface. The fetch order is the reverse
 *  init order, i.e., later features can override values from earlier features.
 */
public interface IComponentFeature	extends IValueFetcher
{
	/**
	 *  Initialize the feature.
	 */
	public IFuture<Void>	init();
	
	/**
	 *  Execute the main activity of the feature.
	 */
	public IFuture<Void>	body();
	
	/**
	 *  Shutdown the feature.
	 */
	public IFuture<Void>	shutdown();
	
	/**
	 *  Get external feature facade.
	 */
//	public <T> IFuture<T> getExternalFacade(Class<T> type, Object context);
//	public <T> IFuture<T> getExternalFacade(Object context);
	public <T> T getExternalFacade(Object context);
	
//	/**
//	 *  Get external feature facade.
//	 */
////	public <T> IFuture<Class<T>> getExternalFacadeType(Object context);
//	public <T> Class<T> getExternalFacadeType(Object context);
}
