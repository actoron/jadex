package jadex.commons.concurrent;

/**
 *  A token is initially available can be acquired once.
 *  The thread safe implementation allows multiple threads
 *  to try to acquire the token and guarantees that one and only one
 *  thread will get the token. 
 */
public class Token
{
	//-------- attributes --------
	
	/** True, if the token is acquired. */
	protected boolean	acquired;
	
	//-------- methods --------
	
	/**
	 *  Try to acquire the token.
	 *  @return True, if the token was acquired by the caller.
	 */
	public synchronized boolean	acquire()
	{
		boolean	ret	= !acquired;
		acquired	= true;
		return ret;
	}
}
