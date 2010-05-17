package jadex.commons.meta;

public interface IPropertyMetaDataSet extends Iterable {
	
	/**
	 * Returns the Meta information for the specified property
	 * @param propertyName 
	 * @return
	 */
	public IPropertyMetaData getProperty(String propertyName);
	
}
