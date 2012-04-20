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
	public ASeriesParameter() {
	}
	
	/* values are ordered by key in this treemap*/
	TreeMap<Double, Double> values = new TreeMap<Double, Double>();

	public ASeriesParameter(String name)
	{
		super(name, Double.class, null);
	}
	
	

	public TreeMap<Double, Double> getValues() {
		return values;
	}



	public void setValues(TreeMap<Double, Double> values) {
		synchronized (mutex) {
			this.values = values;
		}
	}



	@Override
	public void update(Double time, Double value)
	{
		synchronized (mutex) {
		values.put(time, value);	
		}
	}

	@Override
	public TreeMap<Double, Double> getSeries()
	{
		return values;
	}
}
