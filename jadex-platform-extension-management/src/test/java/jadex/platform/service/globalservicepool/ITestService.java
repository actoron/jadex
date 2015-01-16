package jadex.platform.service.globalservicepool;

import jadex.bridge.service.annotation.TargetResolver;
import jadex.commons.future.IFuture;

/**
 *
 */
@TargetResolver(ServicePoolTargetResolver.class)
public interface ITestService
{
	/**
	 *  A test method.
	 */
	public IFuture<Void> methodA();
}
