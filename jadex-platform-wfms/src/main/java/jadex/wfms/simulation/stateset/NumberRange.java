package jadex.wfms.simulation.stateset;

/**
 * Class representing a range of numbers.
 */
public class NumberRange
{
	private long lowerBound;
	private long upperBound;
	
	public NumberRange(long bound0, long bound1)
	{
		this.lowerBound = Math.min(bound0, bound1);
		this.upperBound = Math.max(bound0, bound1);
	}
	
	/**
	 * Tests if a range is tangent with this range.
	 * @param other the other number range
	 * @return true if the ranges are tangent
	 */
	public boolean isTangent(NumberRange other)
	{
		return (((lowerBound < other.lowerBound) && (upperBound + 1 >= other.lowerBound)) ||
				((lowerBound - 1 <= other.upperBound) && (upperBound > other.upperBound)));
	}
	
	/**
	 * Merges this range with another, tangent range.
	 * @param other the other range
	 */
	public void merge(NumberRange other)
	{
		this.lowerBound = Math.min(lowerBound, other.lowerBound);
		this.upperBound = Math.max(upperBound, other.upperBound);
	}
	
	/**
	 * Returns the lower bound of the range.
	 * @return lower bound
	 */
	public long getLowerBound()
	{
		return lowerBound;
	}
	
	/**
	 * Returns the upper bound of the range.
	 * @return upper bound
	 */
	public long getUpperBound()
	{
		return upperBound;
	}
	
	/**
	 * Returns the number of values within the range.
	 * @return number of values within the range
	 */
	public long getValueCount()
	{
		return (upperBound - lowerBound) + 1;
	}
	
	/**
	 * Returns a specific value within the range.
	 * @param index index of the value within the range
	 * @return the value
	 */
	public long getValue(long index)
	{
		return ((lowerBound + index) % (upperBound + 1));
	}
	
	public String toString()
	{
		if (lowerBound == upperBound)
			return String.valueOf(lowerBound);
		else
			return String.valueOf(lowerBound) + " - " + String.valueOf(upperBound);
	}
}
