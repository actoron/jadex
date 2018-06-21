package jadex.extension.envsupport.evaluation;

/**
 *  A table data provider is used for collecting data on demand,
 *  i.e. every time getTableData is called.
 */
public interface ITableDataProvider
{
	/**
	 *  Get the data from a data provider.
	 *  @param time The current time.
	 *  @param tick The current tick.
	 *  @return The data.
	 */
	public DataTable getTableData(long time, double tick);
}
