package jadex.commons;

import jadex.commons.concurrent.IResultListener;

/**
 *  Result listener with additional notifications in case 
 */
public interface IIntermediateResultListener extends IResultListener
{
	/**
	 *  Called when an intermediate result is available.
	 * @param result The result.
	 */
	public void intermediateResultAvailable(Object result);
	
	/**
     *  Declare that the future is finished.
     */
    public void setFinished();
}
