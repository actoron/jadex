package jadex.micro.testcases.timeoutcascade;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;

/**
 *  Service 2.
 */
@Service
@Timeout(Timeout.NONE)
public interface IService2 
{
//	@Timeout(Timeout.NONE)
	public IFuture<Void> service();
}
