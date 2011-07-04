package jadex.micro.testcases;

import jadex.bridge.service.annotation.NoCopy;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ICService
{
	/**
	 *  
	 */
	public @NoCopy IFuture testNoCopy(@NoCopy Object arg, int arghash);
	
	/**
	 *  
	 */
	public IFuture testCopy(Object arg, int arghash);
}
