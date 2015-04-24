package jadex.base.benchmarks;


/**
 *  DAO for benchmarks database.
 */
public class BenchmarkDB
{
	//-------- constants --------
	
	/** The package name of this class (required for finding declaring class of benchmark). */
	protected final String	PACKAGE	= BenchmarkDB.class.getName().substring(0, BenchmarkDB.class.getName().lastIndexOf("."));
	
	//-------- methods --------
	
	/**
	 *  Save a new entry.
	 */
	public void	saveEntry(String name, String description, double value)
	{
		// Node table.
		int	nodeid;
		String	nidname;
		
		// Benchmark table.
		int benchmarkid;
		String	clazz	= null;
		for(StackTraceElement ste: Thread.currentThread().getStackTrace())
		{
			if(!ste.getClassName().startsWith(PACKAGE) && !ste.getClassName().equals("java.lang.Thread"))
			{
				clazz	= ste.getClassName();
				break;
			}
		}
		
		// Entry (double) table
		
	}

	

	public static void main(String[] args)
	{
		new BenchmarkDB().saveEntry("test", null, 0);
	}
}
