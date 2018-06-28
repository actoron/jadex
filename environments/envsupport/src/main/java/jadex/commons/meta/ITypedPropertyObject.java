package jadex.commons.meta;

import jadex.commons.IPropertyObject;

/**
 * 
 */
public interface ITypedPropertyObject extends IPropertyObject 
{	
	/**
	 * Returns for the given property name the specific metadata information
	 * @param propertyName the name of the property
	 * @return the meta data information for the given property name
	 */
	public IPropertyMetaData getMetaData(String propertyName);
	
	/**
	 * retrives a map wich contains all property meta 
	 * data information given to this Property object
	 * @return a map with all meta data information
	 */
	public IPropertyMetaDataSet getMetaDatas();
	
}
