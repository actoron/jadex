package jadex.micro.testcases.semiautomatic.nfpropvis;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.WaitqueueProperty;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ICryptoService 
{
//	/**
//	 *  Test method.
//	 */
//	@NFProperties(
//	{
//		@NFProperty(ExecutionTimeProperty.class),
//		@NFProperty(WaitqueueProperty.class)
//	})
//	public IFuture<String> test();
	
	/**
	 *  Method for encrypting a text snippet.
	 */
	@NFProperties({@NFProperty(WaitqueueProperty.class)})
	public IFuture<String> encrypt(String cleartext);
}
