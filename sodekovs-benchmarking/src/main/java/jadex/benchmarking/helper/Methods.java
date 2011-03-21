/**
 * 
 */
package jadex.benchmarking.helper;

import jadex.benchmarking.model.Property;

import java.util.HashMap;
import java.util.List;

/**
 * @author vilenica
 *
 */
public class Methods {

	/**
	 * Turn a list of properties into a hash map.
	 * @param list
	 * @return
	 */
	public static HashMap<String,String> propertyListToHashMap(List<Property> list){
		HashMap<String,String> map = new HashMap<String,String>();
		
		for(Property property : list){
			map.put(property.getName(), property.getValue());
		}		
		return map;
	}
}
