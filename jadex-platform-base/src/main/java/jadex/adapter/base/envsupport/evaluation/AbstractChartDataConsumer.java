package jadex.adapter.base.envsupport.evaluation;

import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.SimplePropertyObject;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.io.IOException;
import java.util.List;

import org.jfree.chart.JFreeChart;

/**
 *  Abstract base class for chart consumers.
 */
public abstract class AbstractChartDataConsumer extends SimplePropertyObject implements ITableDataConsumer
{
	//-------- attributes --------
	
	/** The chart. */
	protected JFreeChart chart;
	
	//-------- constructors --------

	/**
	 *  Create a new chart consumer.
	 */
	public AbstractChartDataConsumer()
	{
	}
		
	//-------- methods --------
	
	/**
	 *  Consume data from the provider.
	 */
	public void consumeData(long currenttime, double tick)
	{
		if(chart==null)
			chart = createChart();
		
		ITableDataProvider provider = getTableDataProvider();
		DataTable data = provider.getTableData(currenttime, tick);
		List rows = data.getRows();
		
		if(rows!=null)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$object", data);
			
			for(int i=0; i<rows.size(); i++)
			{
				// Determine x, y values for series.
				fetcher.setValue("$rowcnt", new Integer(i));
				
				Object[] row = (Object[])rows.get(i);
				
				Object valuex = getProperty("valuex");
				Object valuey = getProperty("valuey");
				
				Object valx;
				if(valuex instanceof String)
				{
					valx = (Double)row[data.getColumnIndex((String)valuex)]; 
				}
				else //if(valuex instanceof IParsedExpression)
				{
					valx = ((IParsedExpression)valuex).getValue(fetcher);
				}
				
				Object valy;
				if(valuey instanceof String)
				{
					valy = (Double)row[data.getColumnIndex((String)valuey)]; 
				}
				else //if(valuey instanceof IParsedExpression)
				{
					valy = ((IParsedExpression)valuey).getValue(fetcher);
				}
				
				// Add value to series
				
				addValue(valx, valy, data, row);
			}
		}
	}
	
	/**
	 *  Get the chart.
	 *  @return The chart.
	 */
	public JFreeChart getChart()
	{
		return this.chart;
	}
	
	/**
	 *  Get the space.
	 *  @return The space.
	 */
	public AbstractEnvironmentSpace getSpace()
	{
		return (AbstractEnvironmentSpace)getProperty("envspace");
	}
	
	/**
	 *  Get the table data provider.
	 *  @return The table data provider.
	 */
	protected ITableDataProvider getTableDataProvider()
	{
		String providername = (String)getProperty("dataprovider");
		ITableDataProvider provider = getSpace().getDataProvider(providername);
		if(provider==null)
			throw new RuntimeException("Data provider nulls: "+providername);
		return provider;
	}
	
	/**
	 *  Find the file for a given name.
	 *  @param name	The filename or logical name (resolved via imports and extension).
	 *  @param extension	The required extension.
	 *  @param imports	The imports, if any.
	 *  @return The resource info identifying the file.
	 */
	protected ResourceInfo	getResourceInfo(String name, String[] imports, ClassLoader classloader) throws Exception
	{
		// Try to find directly as absolute path.
		String resstr = name;
		ResourceInfo ret = SUtil.getResourceInfo0(resstr, classloader);

		if(ret==null || ret.getInputStream()==null)
		{
			// Try to find in imports.
			for(int i=0; (ret==null || ret.getInputStream()==null) && imports!=null && i<imports.length; i++)
			{
				// Package import
				if(imports[i].endsWith(".*"))
				{
					resstr = SUtil.replace(imports[i].substring(0,
						imports[i].length()-1), ".", "/") + name;
					ret	= SUtil.getResourceInfo0(resstr, classloader);
				}
				// Direct import not important for images
			}
		}

		if(ret==null || ret.getInputStream()==null)
			throw new IOException("File "+name+" not found in imports: "+SUtil.arrayToString(imports));

		return ret;
	}

	/**
	 *  Create a chart with the underlying dataset.
	 *  @return The chart.
	 */
	protected abstract JFreeChart createChart();
	
	/**
	 *  Add a value to a specific series of the chart.
	 *  @param valx The x value.
	 *  @param valy The y value.
	 *  @param data The data table.
	 *  @param row The current data row. 
	 */
	protected abstract void addValue(Object valx, Object valy, DataTable data, Object[] row);
}
