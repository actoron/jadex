package jadex.bridge.nonfunctional;

import jadex.bridge.service.IService;
import jadex.commons.IFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class MultiServiceSearchConstraints implements IServiceSearchConstraints
{
	protected List<IServiceSearchConstraints> constraints;
	
	protected MultiConstraintsFilter filter = new MultiConstraintsFilter();
	
	protected MultiConstraintsComparator comparator = new MultiConstraintsComparator();
	
	public MultiServiceSearchConstraints()
	{
		constraints = new ArrayList<IServiceSearchConstraints>();
	}
	
	public MultiServiceSearchConstraints(IServiceSearchConstraints... constraints)
	{
		this.constraints = new ArrayList<IServiceSearchConstraints>(Arrays.asList(constraints));
	}
	
	/**
	 *  Get the filter.
	 */
	public IFilter<IService> getFilter()
	{
		return filter;
	}
	
	/**
	 *  Get the comparator.
	 */
	public Comparator<IService> getComparator()
	{
		return comparator;
	}
	
	/**
	 *  Is starting to compare allowed.
	 */
	public boolean isCompareStart(Collection<IService> services)
	{
		return constraints.get(0).isCompareStart(services);
	}
	
	/**
	 *  Test if finished.
	 */
	public boolean isFinished(Collection<IService> services)
	{
		return true;
	}
	
	/**
	 * 
	 */
	protected class MultiConstraintsFilter implements IFilter<IService>
	{
		/**
		 *  Test if an object passes the filter.
		 *  @return True, if passes the filter.
		 */
		public boolean filter(IService service)
		{
			boolean ret = true;
			for (IServiceSearchConstraints cts : constraints)
			{
				IFilter<IService> f = cts.getFilter();
				if (f != null)
				{
					ret &= f.filter(service);
				}
			}
			return ret;
		}
	}
	
	/**
	 * 
	 */
	protected class MultiConstraintsComparator implements Comparator<IService>
	{
		public int compare(IService s1, IService s2)
		{
			int ret = 0;
			for (int i = 0; i < constraints.size() && ret == 0; ++i)
			{
				Comparator<IService> comp = constraints.get(i).getComparator();
				if (comp != null)
				{
					ret = comp.compare(s1, s2);
				}
			}
			
			return ret;
		}
	}
}
