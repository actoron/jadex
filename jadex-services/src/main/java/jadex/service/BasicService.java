package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;

/**
 *  Basic service provide a simple default isValid() implementation
 *  that returns true after start service and false afterwards.
 */
public class BasicService implements IService
{
	//-------- attributes --------
	
	/** The valid state. */
	protected boolean valid;
	
	//-------- methods --------
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public boolean isValid()
	{
//		return true;
		return valid;
	}
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture	startService()
	{
		Future ret = new Future();
		if(isValid())
		{
			ret.setException(new RuntimeException("Already running."));
		}
		else
		{
			valid = true;
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture	shutdownService()
	{
		Future ret = new Future();
		if(!isValid())
		{
			ret.setException(new RuntimeException("Not running."));
		}
		else
		{
			valid = false;
			ret.setResult(null);
		}
		return ret;
	}
}
