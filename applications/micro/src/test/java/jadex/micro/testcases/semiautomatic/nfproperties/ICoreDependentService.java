package jadex.micro.testcases.semiautomatic.nfproperties;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.commons.future.IFuture;

/**
 *  Empty Test Service for non-functional properties.
 */
@NFProperties(@NFProperty(CoreNumberProperty.class))
public interface ICoreDependentService
{
	/**
	 *  Service method for test purposes.
	 *  
	 */
	public IFuture<Void> testMethod();
}
