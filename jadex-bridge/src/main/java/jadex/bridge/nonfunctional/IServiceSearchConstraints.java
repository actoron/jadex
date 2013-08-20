package jadex.bridge.nonfunctional;

import jadex.bridge.service.IService;
import jadex.commons.IFilter;

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
	public boolean isCompareStart();
	
	/**
	 *  Test if finished.
	 */
	public boolean isFinished();
}
