package jadex.adapter.base.envsupport.evaluation;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.commons.SUtil;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

/**
 * 
 */
public class SpaceObjectDataProvider implements ITableDataProvider
{
	//-------- attributes --------
	
	/** The environment space. */
	protected AbstractEnvironmentSpace envspace;
	
	/** The space object selector. */
	protected IRowObjectProvider[] rowproviders;
	
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
	public SpaceObjectDataProvider(AbstractEnvironmentSpace envspace, IRowObjectProvider[] rowproviders,
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
	public DataTable getTableData(long time)
	{
		DataTable ret = new DataTable(tablename, columnnames);
		
		String[] names = new String[rowproviders.length];
		Object[] values = new Object[rowproviders.length];
		for(int i=0; i<rowproviders.length; i++)
		{
			names[i] = rowproviders[i].getVariableName();
			values[i]	=rowproviders[i].getRowObjects();
		}
		
		List res = SUtil.calculateCartesianProduct(names, values);
		

		SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$space", envspace);
		fetcher.setValue("$time", new Double(time));
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
		
//		List objects = rowprovider.getRowObjects();
//		String varname = rowprovider.getVariableName();
//		
//		if(objects!=null)
//		{
//			SimpleValueFetcher fetcher = new SimpleValueFetcher();
//			fetcher.setValue("$space", envspace);
//			fetcher.setValue("$time", new Double(time));
//			
//			for(int i=0; i<objects.size(); i++)
//			{
//				Object obj = objects.get(i);
//				fetcher.setValue(varname, obj);
//				
//				Object[] row = new Object[exps.length];
//				for(int j=0; j<exps.length; j++)
//				{
//					row[j] = exps[j].getValue(fetcher);
//				}
//				ret.addRow(row);
//			}
//		}
		
		return ret;
	}
}
