package jadex.simulation.analysis.common.data.parameter;

import jadex.simulation.analysis.common.data.parameter.statistics.ISingleStatistic;
import jadex.simulation.analysis.common.data.parameter.statistics.Max;
import jadex.simulation.analysis.common.data.parameter.statistics.Mean;
import jadex.simulation.analysis.common.data.parameter.statistics.Min;
import jadex.simulation.analysis.common.data.parameter.statistics.Sum;
import jadex.simulation.analysis.common.data.parameter.statistics.Variance;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class ASeriesParameter extends ABasicParameter implements IASeriesParameter
{
	/* values are ordered by key in this treemap*/
	TreeMap<Double, Double> values = new TreeMap<Double, Double>();

	public ASeriesParameter(String name)
	{
		super(name, Double.class, null);
	}

	@Override
	public void update(Double time, Double value)
	{
		values.put(time, value);	
	}

	@Override
	public TreeMap<Double, Double> getSeries()
	{
		return values;
	}
}
