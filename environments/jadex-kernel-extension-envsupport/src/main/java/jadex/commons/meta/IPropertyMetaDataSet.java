package jadex.commons.meta;

/**
 * 
 */
public interface IPropertyMetaDataSet extends Iterable 
{
	/**
	 * Returns the Meta information for the specified property
	 */
	public IPropertyMetaData getProperty(String propertyName);
}
