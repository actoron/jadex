package jadex.micro.testcases.nfmethodprop;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.MethodWaitingTimeProperty;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ITestService
{
	/**
	 * 
	 */
	@NFProperties(@NFProperty(type=MethodWaitingTimeProperty.class))
	public IFuture<Void> methodA(long wait);
	
//	/**
//	 * 
//	 */
//	@NFProperties(@NFProperty(type=MethodWaitingTimeProperty.class))
//	public IFuture<Void> methodB(String[] str, List<List<String>> tmp);
	
	/**
	 * 
	 */
	@NFProperties(@NFProperty(type=MethodWaitingTimeProperty.class))
	public IFuture<Void> methodB(String[] str);
}
