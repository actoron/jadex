package jadex.micro.testcases.nfmethodprop;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.WaitingTimeProperty;
import jadex.bridge.sensor.service.WaitqueueProperty;
import jadex.commons.future.IFuture;

/**
 * 
 */
@NFProperties(
{	
	@NFProperty(WaitingTimeProperty.class),
	@NFProperty(WaitqueueProperty.class)
})
public interface ITestService
{
	/**
	 * 
	 */
	@NFProperties({@NFProperty(WaitqueueProperty.class), @NFProperty(WaitingTimeProperty.class)})
	public IFuture<Void> methodA(long wait);
	
	/**
	 * 
	 */
	@NFProperties({@NFProperty(WaitingTimeProperty.class), @NFProperty(WaitqueueProperty.class)})
	public IFuture<Void> methodB(long wait);
}
