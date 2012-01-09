package jadex.base.service.message.transport.httprelaymtp.benchmark;

import jadex.commons.SReflect;

/**
 *  Base class for benchmark implementations.
 */
public abstract class AbstractBenchmark
{
	//-------- template methods --------
	
	/**
	 *  Called once, before benchmark runs are performed.
	 */
	protected void	setUp()	throws Exception
	{
	}
	
	/**
	 *  Called once, after benchmark runs are finished and results printed.
	 */
	protected void tearDown() throws Exception
	{
	}
	
	/**
	 *  Called after benchmark runs are finished and before tearDown().
	 *  @param time	The time for all runs.
	 *  @param count	The number of runs performed.
	 */
	protected void printResults(long time, int count)
	{
		System.out.println("Benchmark took: "+(time*100/count)/100.0+" ms per item");
	}
	
	/**
	 *  Called for each benchmark run.
	 */
	protected abstract void	doSingleRun()	throws Exception;
	
	//-------- methods --------
	
	/**
	 *  Run the benchmark.
	 *  @param millis	The number of milliseconds after which to stop the benchmark.
	 */
	public void runBenchmark(long millis)	throws Exception
	{
		// Perform dry run first for better accuracy.
		System.out.println(SReflect.getInnerClassName(getClass())+" setup...");
		runBenchmark(millis/10, true);
		
		// Now perform actual benchmark.
		System.out.println("Running "+SReflect.getInnerClassName(getClass())+"...");
		runBenchmark(millis, false);
	}
	
	/**
	 *  Run the benchmark.
	 *  @param millis	The number of milliseconds after which to stop the benchmark.
	 *  @param dryrun	Do not print results.
	 */
	public void runBenchmark(long millis, boolean dryrun)	throws Exception
	{
		setUp();
		long	start	= System.currentTimeMillis();
		long	timeout	= start+millis;
		int	count	= 0;
		while(System.currentTimeMillis()<timeout)
		{
			doSingleRun();
			count++;
		}
		
		if(!dryrun)
		{
			long	time	= System.currentTimeMillis() - start;
			printResults(time, count);
		}
		
		tearDown();
	}
	
	//-------- main --------
	
	/**
	 *  Start some benchmarks supplied as fully qualified names.
	 */
	public static void main(String[] args)	throws Exception
	{
		for(int i=0; i<args.length; i++)
		{
			Class<?>	clazz	= SReflect.findClass(args[i], null, AbstractBenchmark.class.getClassLoader());
			AbstractBenchmark	benchmark	= (AbstractBenchmark)clazz.newInstance();
			benchmark.runBenchmark(10000);
		}
	}
}
