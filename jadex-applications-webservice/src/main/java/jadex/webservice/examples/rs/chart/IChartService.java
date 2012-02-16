package jadex.webservice.examples.rs.chart;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IChartService
{
	/**
	 *  Get a bar chart.
	 */
	public IFuture<byte[]> getBarChart(int width, int height, double[] data, String[] labels);

	/**
	 *  Get a line chart.
	 */
	public IFuture<byte[]> getLineChart(int width, int height, double[] data, String[] labels);
	
	/**
	 *  Get a pie chart.
	 */
	public IFuture<byte[]> getPieChart(int width, int height, double[] data, String[] labels);

}
