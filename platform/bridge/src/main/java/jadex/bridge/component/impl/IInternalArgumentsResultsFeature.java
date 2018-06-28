package jadex.bridge.component.impl;

/**
 *  Internal methods of argument/result feature.
 */
public interface IInternalArgumentsResultsFeature
{
	/**
	 *  Check if somebody was waiting for this component to finish.
	 *  Used to decide if a fatal error needs to be printed to the console.
	 */
	public boolean	exceptionNotified();
}
