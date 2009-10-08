package jadex.adapter.base.envsupport.evaluation;

import jadex.commons.IPropertyObject;

/**
 * 
 */
public interface ITableDataConsumer extends IPropertyObject
{
	/**
	 *  Consume data from the provider.
	 */
	public void consumeData(long currenttime);
}
