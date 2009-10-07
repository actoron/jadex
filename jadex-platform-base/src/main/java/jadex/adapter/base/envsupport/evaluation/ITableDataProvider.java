package jadex.adapter.base.envsupport.evaluation;

/**
 * 
 */
public interface ITableDataProvider
{
	/**
	 *  Get the data from a data provider.
	 *  @return The data.
	 */
	public DataTable getTableData(long time);
}
