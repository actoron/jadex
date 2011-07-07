package jadex.micro.testcases;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

/**
 *  Test if parameter call-by-copy and call-by-reference work (in local case).
 */
public interface ICService
{
	/**
	 *  Test if an argument can be passed by reference.
	 */
	public IFuture testArgumentReference(@Reference Object arg, int arghash);
	
	/**
	 *  Test if an argument can be passed by copy.
	 */
	public IFuture testArgumentCopy(Object arg, int arghash);
	
	/**
	 *  Test if result value can be passed by reference.
	 */
	public @Reference IFuture testResultReference(@Reference Object arg);
	
	/**
	 *  Test if result value can be passed by copy.
	 */
	public IFuture testResultCopy(@Reference Object arg);
}
