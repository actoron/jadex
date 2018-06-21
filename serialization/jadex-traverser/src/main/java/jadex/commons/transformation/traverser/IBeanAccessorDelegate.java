package jadex.commons.transformation.traverser;

public interface IBeanAccessorDelegate
{
	/**
	 *  Retrieves a bean property value.
	 * 
	 *  @param object The object being accessed.
	 *  @param property The name of the property.
	 *  @return Result of calling the getter
	 */
	public Object getPropertyValue(Object object, String property);
	
	/**
	 *  Sets a bean property value.
	 *  
	 *  @param object The object being accessed.
	 *  @param property Name of the property.
	 *  @param value The value passed to the setter method.
	 */
	public void setPropertyValue(Object object, String property, Object value);
}
