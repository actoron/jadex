package jadex.bridge;

import jadex.commons.future.IFuture;

/**
 *  Interface for a component step.
 *  
 *  For making steps in anonymous inner classes properly serializable
 *  the @XMLClassname annotation has to be provided or
 *  a static field for the name has to be declared:<br>
 *  public static final String XML_CLASSNAME = ...; 
 */
public interface IComponentStep<T>
{
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<T> execute(IInternalAccess ia);
}
