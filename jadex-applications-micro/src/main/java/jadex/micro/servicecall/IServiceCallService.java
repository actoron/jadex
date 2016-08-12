package jadex.micro.servicecall;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.TagProperty;
import jadex.commons.future.IFuture;

/**
 *  Service interface for service call benchmark.
 */
@NFProperties(@NFProperty(value=TagProperty.class)) 
public interface IServiceCallService
{
	/**
	 *  Dummy method for service call benchmark.
	 */
	public IFuture<Void>	call();
}
