/**
 * 
 */
package sodekovs.benchmarking.helper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sodekovs.benchmarking.model.Property;

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
	public static HashMap<String,String> propertyListToHashMapforString(List<Property> list){
		HashMap<String,String> map = new HashMap<String,String>();
		
		for(Property property : list){
			map.put(property.getName(), property.getValue());
		}		
		return map;
	}
	
	/**
	 * Turn a list of properties into a hash map.
	 * @param list
	 * @return
	 */
	public static HashMap<String,Object> propertyListToHashMapforObject(List<Property> list){
		HashMap<String,Object> map = new HashMap<String,Object>();
		
		for(Property property : list){
			map.put(property.getName(), property.getValue());
		}		
		return map;
	}
	
	 /**
	  * Return the values, initially "only" a string of values, as an ArrayList of separated values.
	  * @param values
	  * @return
	  */
   	public static ArrayList<String> getValuesAsList(String values) {   		
   			ArrayList<String> valuesAsList = new ArrayList<String>();   			
   			
   			while(values.indexOf(";") != -1){
   				valuesAsList.add(values.substring(0, values.indexOf(";")));
   				values = values.substring(values.indexOf(";")+1);
   			}		
   		
   		return valuesAsList;	
   	}
}
