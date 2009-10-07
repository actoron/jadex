package jadex.adapter.base.envsupport.evaluation;

import java.util.List;

import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

/**
 * 
 */
public class SpaceObjectDataProvider implements ITableDataProvider
{
	/** The environment space. */
	protected AbstractEnvironmentSpace envspace;
	
	/** The space object selector. */
	protected IRowObjectProvider rowprovider;
	
	/** The table name. */
	protected String tablename;
	
	/** The column names. */
	protected String[] columnnames;
	
	/** The expressions. */
	protected IParsedExpression[] exps;
	
	/**
	 * 
	 */
	public SpaceObjectDataProvider(AbstractEnvironmentSpace envspace, IRowObjectProvider rowprovider,
		String tablename, String[] columnnames, IParsedExpression[] exps)
	{
		this.envspace = envspace;
		this.rowprovider = rowprovider;
		this.tablename = tablename;
		this.columnnames = columnnames;
		this.exps = exps;
	}
	
	/**
	 *  Get the data from a data provider.
	 *  @return The data.
	 */
	public DataTable getTableData(long time)
	{
		DataTable ret = new DataTable(tablename, columnnames);
		
		// todo: allow more than one source
		List objects = rowprovider.getRowObjects();
		String varname = rowprovider.getVariableName();
		
		if(objects!=null)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$space", envspace);
			fetcher.setValue("$time", new Double(time));
			
			for(int i=0; i<objects.size(); i++)
			{
				Object obj = objects.get(i);
				fetcher.setValue(varname, obj);
				
				Object[] row = new Object[exps.length];
				for(int j=0; j<exps.length; j++)
				{
					row[j] = exps[j].getValue(fetcher);
				}
				ret.addRow(row);
			}
		}
		
		return ret;
	}
}
