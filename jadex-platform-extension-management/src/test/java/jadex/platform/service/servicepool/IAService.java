package jadex.platform.service.servicepool;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Example service a.
 */
@Service
public interface IAService
{
	/**
	 *  Example method 1.
	 */
	public IFuture<String> ma1(String str); 
	
	/**
	 *  Example method 2.
	 */
	public IIntermediateFuture<Integer> ma2();
	
}
