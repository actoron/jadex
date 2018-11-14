package jadex.platform.servicecall;

import jadex.commons.future.IFuture;

/**
 *  Service interface for service call benchmark.
 */
public interface IServiceCallService
{
	/**
	 *  Dummy method for service call benchmark.
	 */
	public IFuture<Void>	call();
}
