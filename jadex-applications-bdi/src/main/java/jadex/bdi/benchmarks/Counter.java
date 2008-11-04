package jadex.bdi.benchmarks;


/**
 *  A thread safe counter to determine when all agents are running.  
 */
public class Counter
{
	//-------- attributes --------
	
	/** The counter. */
	protected int	cnt	= 0;
	
	//-------- constructors --------
	
	/**
	 *  Increment and return the counter value. 
	 */
	public synchronized int	increment()
	{
		return ++cnt;
	}
}