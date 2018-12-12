package jadex.extension.envsupport.evaluation;

import java.io.IOException;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

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
	public void consumeData(final long currenttime, final double tick)
	{
		ITableDataProvider provider = getTableDataProvider();
		final DataTable data = provider.getTableData(currenttime, tick);
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				List rows = data.getRows();
				
				if(rows!=null && rows.size()>0)
				{
					SimpleValueFetcher fetcher = new SimpleValueFetcher();
					fetcher.setValue("$object", data);

					for(int s=0; ;s++)
					{
						String serid;
						if(s==0 && getPropertyNames().contains("seriesid"))
							serid = (String)getProperty("seriesid");
						else
							serid = (String)getProperty("seriesid_"+s);
						
						if(serid!=null)
						{
							// For a multi-series each row has to be processed.

							for(int i=0; i<rows.size(); i++)
							{
								// Determine x, y values for series.
								fetcher.setValue("$rowcnt", Integer.valueOf(i));
								
								Object[] row = (Object[])rows.get(i);
								
								Object[] values = getValues(s, data, row, fetcher);
									
								// Add value to series
								
								Comparable sername = (Comparable)row[data.getColumnIndex(serid)];
												
								addValue(sername, values[0], values[1], data, row);
							}	
						}
						else
						{
							// For a named series only one (all should be the same) value will be processed.
							Comparable sername;
							if(s==0 && getPropertyNames().contains("seriesname"))
								sername = (String)getProperty("seriesname");
							else
								sername = (String)getProperty("seriesname_"+s);
							
							if(sername!=null)
							{
								// Determine x, y values for series.
								fetcher.setValue("$rowcnt", Integer.valueOf(0));
								
								Object[] row = (Object[])rows.get(0);
								
								Object[] values = getValues(s, data, row, fetcher);
									
								// Add value to series
								
								addValue(sername, values[0], values[1], data, row);
							}
							else
							{
								break;
							}
						}
					}
				}
			}
		});		
	}
	
	/**
	 *  Get the x/y values for a specific series number.
	 *  In case of a multi series the row array defines the specific data to read.
	 */
	protected Object[] getValues(int num, DataTable data, Object[] row, SimpleValueFetcher fetcher)
	{
		Object valuex;
		Object valuey;
		
		if(num==0 && getPropertyNames().contains("valuex"))
			valuex = getProperty("valuex");
		else
			valuex = getProperty("valuex_"+num);

		if(num==0 && getPropertyNames().contains("valuey"))
			valuey = getProperty("valuey");
		else
			valuey = getProperty("valuey_"+num);
		
		Object valx;
		if(valuex instanceof String)
		{
			valx = (Number)row[data.getColumnIndex((String)valuex)]; 
		}
		else //if(valuex instanceof IParsedExpression)
		{
			valx = ((IParsedExpression)valuex).getValue(fetcher);
		}
		
		Object valy;
		if(valuey instanceof String)
		{
			valy = (Number)row[data.getColumnIndex((String)valuey)]; 
		}
		else //if(valuey instanceof IParsedExpression)
		{
			valy = ((IParsedExpression)valuey).getValue(fetcher);
		}

		return new Object[]{valx, valy};
	}
	
	/**
	 *  Get the chart.
	 *  @return The chart.
	 */
	public JFreeChart getChart()
	{
		// Todo: should be swing thread?
//		assert SwingUtilities.isEventDispatchThread();
		if(chart==null)
			chart = createChart();
		return this.chart;
	}
	
	/**
	 *  Refresh the chart.
	 */
	public void refresh()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				getChart().fireChartChanged();
			}
		});
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
			throw new IOException("File "+name+" not found in imports");//: "+SUtil.arrayToString(imports));

		return ret;
	}

	/**
	 *  Get the chart panel.
	 *  @return The chart panel.
	 */
	public JPanel getChartPanel()
	{
		// Todo: should be swing thread?
//		assert SwingUtilities.isEventDispatchThread();
		ChartPanel panel = new ChartPanel(getChart(), false, false, false, false, false);
        panel.setFillZoomRectangle(true);
        return panel;
	}
	
	/**
	 *  Create a chart with the underlying dataset.
	 *  @return The chart.
	 */
	protected abstract JFreeChart createChart();
	
	/**
	 *  Add a value to a specific series of the chart.
	 *  @param seriesname The seriesname.
	 *  @param valx The x value.
	 *  @param valy The y value.
	 *  @param data The data table.
	 *  @param row The current data row. 
	 */
	protected abstract void addValue(Comparable seriesname, Object valx, Object valy, DataTable data, Object[] row);
}
