package jadex.extension.envsupport.evaluation;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.commons.SUtil;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

/**
 *  Provides data on basis of the environment space. 
 */
public class DefaultDataProvider implements ITableDataProvider
{
	//-------- attributes --------
	
	/** The environment space. */
	protected AbstractEnvironmentSpace envspace;
	
	/** The space object selector. */
	protected IObjectSource[] rowproviders;
	
	/** The table name. */
	protected String tablename;
	
	/** The column names. */
	protected String[] columnnames;
	
	/** The expressions. */
	protected IParsedExpression[] exps;
	
	//-------- constructors --------

	/**
	 *  Create a new space object table data provider.
	 */
	public DefaultDataProvider(AbstractEnvironmentSpace envspace, IObjectSource[] rowproviders,
		String tablename, String[] columnnames, IParsedExpression[] exps)
	{
		this.envspace = envspace;
		this.rowproviders = rowproviders;
		this.tablename = tablename;
		this.columnnames = columnnames;
		this.exps = exps;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the data from a data provider.
	 *  @return The data.
	 */
	public DataTable getTableData(long time, double tick)
	{
		DataTable ret = new DataTable(tablename, columnnames);
		
		String[] names = new String[rowproviders.length];
		Object[] values = new Object[rowproviders.length];
		for(int i=0; i<rowproviders.length; i++)
		{
			names[i] = rowproviders[i].getSourceName();
			values[i] = rowproviders[i].getObjects();
		}
		
		List res = SUtil.calculateCartesianProduct(names, values);
		
		SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$space", envspace);
		fetcher.setValue("$time", Double.valueOf(time));
		fetcher.setValue("$tick", Double.valueOf(tick));
		for(int i=0; i<res.size(); i++)
		{
			Map binding = (Map)res.get(i);
			
			for(Iterator it=binding.keySet().iterator(); it.hasNext(); )
			{
				String key = (String)it.next();
				Object val = binding.get(key);
				fetcher.setValue(key, val);
			}

			Object[] row = new Object[exps.length];
			for(int j=0; j<exps.length; j++)
			{
				row[j] = exps[j].getValue(fetcher);
			}
			ret.addRow(row);
		}
		
		return ret;
	}
}
