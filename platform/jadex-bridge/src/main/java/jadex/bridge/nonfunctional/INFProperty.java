package jadex.bridge.nonfunctional;

import jadex.commons.future.IFuture;

/**
 *  A non-functional property.
 *  
 *  NOTE: Implementing classes must implement a constructor with
 *  the signature INFProperty(String name) to allow the service
 *  to initialize the property during creation.
 */
public interface INFProperty<T, U>
{
	public static enum Target{Self, Root} // todo: support COMPONENT, Parent
	
	/**
	 *  Gets the name of the property.
	 *  @return The name of the property.
	 */
	public String getName();
	
	/**
	 *  Returns the meta information about the property.
	 *  @return The meta information about the property.
	 */
	public INFPropertyMetaInfo getMetaInfo();
	
	/**
	 *  Returns the current value of the property.
	 *  @return The current value of the property.
	 */
	public IFuture<T> getValue();
	
	/**
	 *  Returns the current value of the property, performs unit conversion if necessary.
	 *  @param unit Unit of the returned value.
	 *  @return The current value of the property.
	 */
//	public IFuture<T> getValue(Class<U> unit);
	public IFuture<T> getValue(U unit);
	
	/**
	 *  Property was removed and should be disposed.
	 */
	public IFuture<Void> dispose();
}
