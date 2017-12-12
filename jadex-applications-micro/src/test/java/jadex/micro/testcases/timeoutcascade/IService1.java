package jadex.micro.testcases.timeoutcascade;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;


/**
 *  Service 1.
 */
@Service
@Timeout(value = Timeout.NONE)
public interface IService1
{

//	@Timeout(Timeout.NONE)
	public IFuture<Void> service();
}
