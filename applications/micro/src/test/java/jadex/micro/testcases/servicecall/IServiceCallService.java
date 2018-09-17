package jadex.micro.testcases.servicecall;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.annotation.Raw;
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
	public IFuture<Void> call();
	
	/**
	 *  Dummy method for service call benchmark.
	 */
	@Raw
	public IFuture<Void> rawcall();
}
