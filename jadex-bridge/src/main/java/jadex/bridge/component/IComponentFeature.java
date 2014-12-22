package jadex.bridge.component;

import jadex.commons.IParameterGuesser;
import jadex.commons.IValueFetcher;
import jadex.commons.future.IFuture;

/**
 *  A component feature is a pluggable part of the state and behavior
 *  of a component. This interface represents the internal view of the instance level of a feature.
 */
public interface IComponentFeature
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
	
	/**
	 *  The feature can inject parameters for expression evaluation
	 *  by providing an optional value fetcher. The fetch order is the reverse
	 *  init order, i.e., later features can override values from earlier features.
	 */
	public IValueFetcher	getValueFetcher();
	
	/**
	 *  The feature can add objects for field or method injections
	 *  by providing an optional parameter guesser. The selection order is the reverse
	 *  init order, i.e., later features can override values from earlier features.
	 */
	public IParameterGuesser	getParameterGuesser();
}
