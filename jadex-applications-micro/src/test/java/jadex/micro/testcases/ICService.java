package jadex.micro.testcases;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Test if parameter call-by-copy and call-by-reference work (in local case).
 */
public interface ICService
{
	/**
	 *  Test if an argument can be passed by reference.
	 */
	public IFuture<Boolean> testArgumentReference(@Reference Object arg, int arghash);
	
	/**
	 *  Test if an argument can be passed by copy.
	 */
	public IFuture<Boolean> testArgumentCopy(Object arg, int arghash);
	
	/**
	 *  Test if result value can be passed by reference.
	 */
	public @Reference IFuture<Object> testResultReference(@Reference Object arg);
	
	/**
	 *  Test if result value can be passed by copy.
	 */
	public IFuture<Object> testResultCopy(@Reference Object arg);
	
	/**
	 *  Test if result value can be passed by reference.
	 */
	public @Reference IIntermediateFuture<Object> testResultReferences(@Reference Object[] args);
	
	/**
	 *  Test if result value can be passed by copy.
	 */
	public IIntermediateFuture<Object> testResultCopies(@Reference Object[] args);
}
