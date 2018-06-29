package jadex.launch.test.servicecall;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Implementation of a service.
 */
@Service
public class RawServiceCallService extends BasicService	implements IServiceCallService
{
	/**
	 *  Basic service constructor.
	 */
	public RawServiceCallService(IComponentIdentifier providerid)
	{
		super(providerid, IServiceCallService.class, null);
	}
	
	/**
	 *  Dummy method for service call benchmark.
	 */
	public IFuture<Void> call()
	{
		return IFuture.DONE;
	}
}