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
	public IFuture<Void> init();
	
	/**
	 *  Execute the main activity of the feature.
	 */
	public IFuture<Void> body();
	
	/**
	 *  Shutdown the feature.
	 */
	public IFuture<Void> shutdown();

	/**
	 *  Kill is only invoked, when shutdown does not return due to timeout.
	 *  The feature should do any kind of possible cleanup, but no asynchronous operations.
	 */
	public void kill();

	/**
	 *  The feature can inject parameters for expression evaluation
	 *  by providing an optional value fetcher. The fetch order is the reverse
	 *  init order, i.e., later features can override values from earlier features.
	 */
	public IValueFetcher getValueFetcher();
	
	/**
	 *  The feature can add objects for field or method injections
	 *  by providing an optional parameter guesser. The selection order is the reverse
	 *  init order, i.e., later features can override values from earlier features.
	 */
	public IParameterGuesser getParameterGuesser();

	/**
	 *  Check if the feature potentially executed user code in body.
	 *  Allows blocking operations in user bodies by using separate steps for each feature.
	 *  Non-user-body-features are directly executed for speed.
	 *  If unsure just return true. ;-)
	 */
	public boolean hasUserBody();
}
