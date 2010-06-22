package jadex.bdi.runtime;

import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;

/**
 *  A change event indicates (important) state changes.
 */
public interface IChangeEvent extends IElement
{
	/**
	 *  Get the element that caused the event.
	 *  @return The element.
	 */
	public ElementFlyweight getElement();
	
	/**
	 *  Get the changeevent value.
	 *  @return The value (can be null).
	 */
	public Object getValue();
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType();
}
