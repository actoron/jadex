package jadex.micro.testcases.nfmethodprop;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.ExecutionTimeProperty;
import jadex.bridge.sensor.service.WaitqueueProperty;
import jadex.commons.future.IFuture;

/**
 * 
 */
@NFProperties(
{	
	@NFProperty(ExecutionTimeProperty.class),
	@NFProperty(WaitqueueProperty.class)
})
public interface ITestService
{
	/**
	 * 
	 */
	@NFProperties({@NFProperty(WaitqueueProperty.class), @NFProperty(ExecutionTimeProperty.class)})
	public IFuture<Void> methodA(long wait);
	
	/**
	 * 
	 */
	@NFProperties({@NFProperty(ExecutionTimeProperty.class), @NFProperty(WaitqueueProperty.class)})
	public IFuture<Void> methodB(long wait);
}
