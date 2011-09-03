package jadex.simulation.analysis.common.data.parameter;

import java.util.TreeMap;

public interface IASeriesParameter extends IAParameter
{
	/**
	 * Update the series with the given doubles. first double ist the time, second is the value
	 * @param time time double
	 * @param value value double
	 */
	public void update(Double time, Double value);
	
	/**
	 * Returns the series
	 * @return ordered TreeMap of the series
	 */
	public TreeMap<Double, Double> getSeries();

}
