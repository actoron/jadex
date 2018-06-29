package jadex.micro.testcases.semiautomatic.nfpropreq;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.ExecutionTimeProperty;
import jadex.bridge.sensor.service.WaitqueueProperty;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IAService 
{
	/**
	 *  Test method.
	 */
	@NFProperties(
	{
		@NFProperty(ExecutionTimeProperty.class),
		@NFProperty(WaitqueueProperty.class)
	})
	public IFuture<String> test();
}
