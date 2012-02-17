package jadex.webservice.examples.rs.chart;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IChartService
{
	/**
	 *  Get a bar chart.
	 */
	public @Reference(local=true) IFuture<byte[]> getBarChart(int width, int height, 
		double[] data, String[] labels);

	/**
	 *  Get a line chart.
	 */
	public @Reference(local=true) IFuture<byte[]> getLineChart(int width, int height, 
		double[] data, String[] labels);
	
	/**
	 *  Get a pie chart.
	 */
	public @Reference(local=true) IFuture<byte[]> getPieChart(int width, int height, 
		double[] data, String[] labels);

}
