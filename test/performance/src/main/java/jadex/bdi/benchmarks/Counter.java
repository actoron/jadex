package jadex.bdi.benchmarks;

import jadex.bridge.service.annotation.Reference;


/**
 *  A thread safe counter to determine when all agents are running.  
 */
@Reference(local=true)
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