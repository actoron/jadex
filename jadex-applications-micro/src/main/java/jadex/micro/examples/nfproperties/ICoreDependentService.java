package jadex.micro.examples.nfproperties;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;

/**
 *  Empty Test Service for non-functional properties.
 */
@NFProperties(@NFProperty(name="cores", type=CoreNumberProperty.class))
public interface ICoreDependentService
{

}
