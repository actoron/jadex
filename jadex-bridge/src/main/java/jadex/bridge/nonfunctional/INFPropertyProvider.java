/**
 * 
 */
package jadex.bridge.nonfunctional;

import jadex.commons.future.IFuture;

/**
 *  Interface for non-functional property providers such
 *  as services and components.
 */
public interface INFPropertyProvider
{
	/**
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNonFunctionalPropertyNames();
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<INFPropertyMetaInfo> getNfPropertyMetaInfo(String name);
	
	/**
	 *  Returns the current value of a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public <T> IFuture<T> getNonFunctionalPropertyValue(String name, Class<T> type);
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public <T, U> IFuture<T> getNonFunctionalPropertyValue(String name, Class<T> type, Class<U> unit);

}
