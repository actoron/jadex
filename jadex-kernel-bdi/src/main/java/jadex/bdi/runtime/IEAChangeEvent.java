package jadex.bdi.runtime;

import jadex.commons.IFuture;

/**
 *  A change event indicates (important) state changes.
 */
public interface IEAChangeEvent extends IEAElement
{
	/**
	 *  Get the element that caused the event.
	 *  @return The element.
	 */
	public IFuture getElement();
	
	/**
	 *  Get the changeevent value.
	 *  @return The value (can be null).
	 */
	public IFuture getValue();
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public IFuture getType();
}
