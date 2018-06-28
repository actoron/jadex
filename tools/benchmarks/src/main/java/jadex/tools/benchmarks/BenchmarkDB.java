package jadex.tools.benchmarks;

import java.net.InetAddress;


/**
 *  DAO for benchmarks database.
 */
public class BenchmarkDB
{
	//-------- constants --------
	
	/** The package name of this class (required for finding declaring class of benchmark). */
	protected static final String	PACKAGE	= BenchmarkDB.class.getName().substring(0, BenchmarkDB.class.getName().lastIndexOf("."));
	
	/** The host name (cached for speed). */
	protected static final String	HOST;
	
	static
	{
		String	host;
		try
		{
			host	= InetAddress.getLocalHost().getHostName();
		}
		catch(Exception e)
		{
			host	= "unknown";
		}
		HOST	= host;
	}
	
	//-------- methods --------
	
	/**
	 *  Save a new entry.
	 */
	public void	saveEntry(String name, String description, double value)
	{
		// Node table.
		int	nodeid;
		String	nidname	= HOST;
		// Todo: node properties from sigar (cpuinfo, etc)
		
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
