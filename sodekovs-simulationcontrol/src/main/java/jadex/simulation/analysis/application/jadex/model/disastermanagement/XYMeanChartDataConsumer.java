package jadex.simulation.analysis.application.jadex.model.disastermanagement;

import jadex.extension.envsupport.evaluation.DataTable;
import jadex.extension.envsupport.evaluation.XYChartDataConsumer;
import jadex.simulation.analysis.common.data.parameter.statistics.Mean;
import jadex.simulation.analysis.common.data.parameter.statistics.Variance;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;

public class XYMeanChartDataConsumer extends XYChartDataConsumer
{
	Map<String, Mean> means = new HashMap<String, Mean>();
	Map<String, Variance> variances = new HashedMap<String, Variance>();
	
	public XYMeanChartDataConsumer()
	{
		super();
		means.put("Victims", new Mean());
		means.put("Fire", new Mean());
		means.put("Chemicals", new Mean());
		
		variances.put("Victims", new Variance());
		variances.put("Fire", new Variance());
		variances.put("Chemicals", new Variance());
	}
	
	
	@Override
	protected void addValue(Comparable seriesname, Object valx, Object valy, DataTable data, Object[] row)
	{
		super.addValue(seriesname, valx, valy, data, row);
		
		means.get(seriesname.toString()).addValue(((Number)valy).doubleValue());
		variances.get(seriesname.toString()).addValue(((Number)valy).doubleValue());
	}
	
	public Double getMean(String name)
	{
		return means.get(name).getResult();
	}
	
	public Double getVariance(String name)
	{
		return  variances.get(name).getResult();
	}
	

}
