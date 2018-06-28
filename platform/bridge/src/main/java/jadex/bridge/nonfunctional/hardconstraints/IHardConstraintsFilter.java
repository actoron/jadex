package jadex.bridge.nonfunctional.hardconstraints;

import java.util.Collection;
import java.util.Map;

import jadex.bridge.service.IService;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;

public interface IHardConstraintsFilter extends IFilter<Tuple2<IService, Map<String, Object>>>
{
	/** Returns the value names relevant to this filter */
	public Collection<String> getRelevantValueNames();
}
