package jadex.platform.service.servicepool;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Example service b.
 */
@Service
public interface IBService
{
	/**
	 *  Example method 1.
	 */
	public IFuture<String> mb1(String str); 
}
