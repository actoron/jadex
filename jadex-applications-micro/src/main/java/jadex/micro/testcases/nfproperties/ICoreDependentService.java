package jadex.micro.testcases.nfproperties;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.commons.future.IFuture;

/**
 *  Empty Test Service for non-functional properties.
 */
@NFProperties(@NFProperty(name="cores", value=CoreNumberProperty.class))
public interface ICoreDependentService
{
	public IFuture<Void> testMethod();
}
