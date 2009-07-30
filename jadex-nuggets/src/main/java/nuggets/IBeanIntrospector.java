package nuggets;

import java.util.Map;


/** 
 *  IBeanIntrospector 
 */
public interface IBeanIntrospector
{

	/** 
	 * @param clazz
	 * @return an array of bean properties
	 */
	Map getBeanProperties(Class clazz);
}
