package jadex.platform.service.cron;

import jadex.commons.IFilter;

import java.util.Set;

/**
 * 
 */
public class SetFilter implements IFilter<Integer>
{
	/** The allowed values. */
	protected Set<Integer> vals;
	
	/**
	 * 
	 */
	public SetFilter(Set<Integer> vals)
	{
		this.vals = vals;
	}
	
	/**
	 * 
	 */
	public boolean filter(Integer obj)
	{
		return vals.contains(obj);
	}
}