package jadex.commons.xml.bean;

import java.util.Map;


/** 
 *  Interface for Java bean introspectors.
 *  These collect data about Java beans.
 */
public interface IBeanIntrospector
{
	/** 
	 *  Get the bean properties for a class.
	 *  @param clazz The class to inspect.
	 *  @return The map of properties (name -> BeanProperty).
	 */
	public Map getBeanProperties(Class clazz);
}
