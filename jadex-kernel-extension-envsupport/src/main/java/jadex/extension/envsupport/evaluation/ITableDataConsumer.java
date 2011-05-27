package jadex.extension.envsupport.evaluation;

import jadex.commons.IPropertyObject;

/**
 *  A table data consumer is triggered to consume data.
 *  Consumption can e.g. be writing to a file or showing the data as a graph.
 *  It uses a data provider to access the data.
 */
public interface ITableDataConsumer extends IPropertyObject
{
	/**
	 *  Consume data from the provider.
	 *  @param time The current time.
	 *  @param tick The current tick.
	 */
	public void consumeData(long time, double tick);
}
