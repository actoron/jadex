package jadex.wfms.simulation.stateholder;

import jadex.tools.ontology.CurrentState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractNumericStateHolder implements IParameterStateHolder
{
	protected List ranges;
	
	protected long lowerBound;
	
	protected long upperBound;
	
	protected int currentRange;
	
	protected long currentIndex;
	
	protected AbstractNumericStateHolder(long lowerBound, long upperBound)
	{
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
			if (range.overlaps(existingRange))
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
		reset();
	}
	
	/**
	 * Removes a range.
	 * @param index index of the range
	 */
	public void removeRange(int index)
	{
		ranges.remove(index);
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
	 * Resets the state holder to the first available state.
	 */
	public void reset()
	{
		currentIndex = 0;
		currentRange = 0;
	}
	
	/**
	 * Switches to the next available state.
	 */
	public void nextState()
	{
		currentIndex++;
		if ((currentIndex + 1) >= ((NumberRange) ranges.get(currentRange)).getValueCount());
			currentRange = (currentRange + 1) % ranges.size();
	}
	
	/**
	 * Returns the current state
	 * @return current state
	 */
	public long getCurrentState()
	{
		long ret = 0;
		for (int i = 0; i < currentRange; ++i)
		{
			NumberRange range = (NumberRange) ranges.get(i);
			ret += range.getValueCount();
		}
		ret += currentIndex;
		return ret;
	}
	
	/**
	 * Test if the holder is in the final state.
	 * @return true, if in the final state
	 */
	public boolean finalState()
	{
		return (((currentRange + 1) == ranges.size()) &&
				((currentIndex + 1) == ((NumberRange) ranges.get(currentRange)).getValueCount()));
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
	 * Returns the current state.
	 * @return the current state
	 */
	public abstract Object getState();
	
	/**
	 * Returns the current state as a long value.
	 * @return the current state as a long value
	 */
	protected long getStateAsLong()
	{
		return ((NumberRange) ranges.get(currentRange)).getValue(currentIndex);
	}
}
