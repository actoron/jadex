package jadex.webservice.examples.rs.chart;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IChartService
{
	/**
	 *  Get a chart.
	 */
	public IFuture<byte[]> getPieChart(int width, int height, double[] data, String[] labels);

}
