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
	@NFProperty(type=WaitingTimeProperty.class),
	@NFProperty(type=WaitqueueProperty.class)
})
public interface ITestService
{
	/**
	 * 
	 */
	@NFProperties(@NFProperty(type=WaitingTimeProperty.class))
	public IFuture<Void> methodA(long wait);
	
//	/**
//	 * 
//	 */
//	@NFProperties(@NFProperty(type=MethodWaitingTimeProperty.class))
//	public IFuture<Void> methodB(String[] str, List<List<String>> tmp);
	
	/**
	 * 
	 */
	@NFProperties(@NFProperty(type=WaitingTimeProperty.class))
	public IFuture<Void> methodB(long wait);
}
