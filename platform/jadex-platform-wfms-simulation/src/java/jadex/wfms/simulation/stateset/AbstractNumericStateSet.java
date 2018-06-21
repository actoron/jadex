package jadex.wfms.simulation.stateset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractNumericStateSet extends AbstractParameterStateSet
{
	protected List ranges;
	
	protected long lowerBound;
	
	protected long upperBound;
	
	protected AbstractNumericStateSet(String parameterName, long lowerBound, long upperBound)
	{
		this.name = parameterName;
		ranges = new ArrayList();
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	/**
	 * Gets the lower bound for ranges in this state holder.
	 * @return lower bound
	 */
	public long getLowerBound()
	{
		return lowerBound;
	}
	/**
	 * Gets the upper bound for ranges in this state holder.
	 * @return upper bound
	 */
	public long getUpperBound()
	{
		return upperBound;
	}
	
	/**
	 * Adds a range. If it overlaps with an existing range, the ranges will be merged.
	 * @param range the range
	 */
	public void addRange(NumberRange range)
	{
		if ((range.getLowerBound() < lowerBound) || (range.getUpperBound() > upperBound))
			throw new IllegalArgumentException("Range out bounds for type.");
		List mergableRanges = new ArrayList();
		for (Iterator it = ranges.iterator(); it.hasNext(); )
		{
			NumberRange existingRange = (NumberRange) it.next();
			if (range.isTangent(existingRange))
				mergableRanges.add(existingRange);
		}
		
		for (Iterator it = mergableRanges.iterator(); it.hasNext(); )
		{
			NumberRange mergeRange = (NumberRange) it.next();
			ranges.remove(mergeRange);
			range.merge(mergeRange);
		}
		
		ranges.add(range);
		Collections.sort(ranges, new Comparator()
		{
			
			public int compare(Object o1, Object o2)
			{
				long diff = (((NumberRange) o1).getLowerBound() - ((NumberRange) o2).getLowerBound());
				return (int) (diff / Math.abs(diff));
			}
		});
		fireStateChange(range);
	}
	
	/**
	 * Removes a range.
	 * @param index index of the range
	 */
	public void removeRange(int index)
	{
		fireStateChange(ranges.remove(index));
	}
	
	/**
	 * Gets the number of states in this holder.
	 * @return number of states
	 */
	public long getStateCount()
	{
		long ret = 0;
		for (Iterator it = ranges.iterator(); it.hasNext(); )
		{
			NumberRange range = (NumberRange) it.next();
			ret += range.getValueCount();
		}
		return ret;
	}
	
	/**
	 * Returns all ranges
	 * @return all ranges
	 */
	public List getRanges()
	{
		return ranges;
	}
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public abstract Object getState(long index);
	
	/**
	 * Returns a specific state as a long value.
	 * 
	 * @param index the index of the state
	 * @return the specified state as a long value
	 */
	protected long getStateAsLong(long index)
	{
		int i = 0;
		while ((i < ranges.size()) && (((NumberRange) ranges.get(0)).getValueCount() < index))
		{
			index -= ((NumberRange) ranges.get(0)).getValueCount();
			++i;
		}
		return ((NumberRange) ranges.get(i)).getValue(index);
	}
}
