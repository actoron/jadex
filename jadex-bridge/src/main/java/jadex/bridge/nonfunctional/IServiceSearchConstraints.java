package jadex.bridge.nonfunctional;

import jadex.bridge.service.IService;
import jadex.commons.IFilter;

import java.util.Collection;
import java.util.Comparator;

/**
 *  Hard constraints are represented as filter and
 *  soft constraints are represented as comparator
 */
public interface IServiceSearchConstraints //extends IIntermediateResultListener<IService>
{
	/**
	 *  Get the filter.
	 */
	public IFilter<IService> getFilter();
	
	/**
	 *  Get the comparator.
	 */
	public Comparator<IService> getComparator();
	
	/**
	 *  Is starting to compare allowed.
	 */
	public boolean isCompareStart(Collection<IService> services);
	
	/**
	 *  Test if finished.
	 */
	public boolean isFinished(Collection<IService> services);
}
