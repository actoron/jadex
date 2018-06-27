package jadex.commons.meta;

import jadex.commons.SimplePropertyObject;

public class TypedPropertyObject extends SimplePropertyObject implements ITypedPropertyObject 
{
	private final IPropertyMetaDataSet metaData;
	
	public TypedPropertyObject(IPropertyMetaDataSet metaData) 
	{
		this.metaData = metaData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see jadex.commons.meta.IEnhancedPropertyObject#getMetaData(java.lang.String)
	 */
	public IPropertyMetaData getMetaData(String name) 
	{
		if (metaData != null) 
		{
			return metaData.getProperty(name);
		} 
		else 
		{
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see jadex.commons.meta.IEnhancedPropertyObject#getMetaDatas()
	 */
	public IPropertyMetaDataSet getMetaDatas() 
	{
		return metaData;
	}

}
