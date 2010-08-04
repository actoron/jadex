package deco4mas.examples.agentNegotiation.common.statistics;

public class BasicSummary extends AbstractSummaryStatistic
{

	/** number of values */
	protected Double n = 0.0;

	/** Minimum statistic */
	private ISingleStatistic min = new Min();

	/** Maximum statistic */
	private ISingleStatistic max = new Max();

	/** Mean statistic */
	private ISingleStatistic mean = new Mean();

	/** Sum statistic */
	private ISingleStatistic sum = new Sum();

	/** Variance statistic */
	private ISingleStatistic variance = new Variance();

	/**
	 * Construct a SummaryStatistics instance
	 */
	public BasicSummary()
	{
	}

	/**
	 * Add a value
	 * 
	 * @param value
	 *            value to add
	 */
	public synchronized void addValue(double value)
	{
		sum.addValue(value);
		min.addValue(value);
		max.addValue(value);
		mean.addValue(value);
		variance.addValue(value);
		n++;
	}

	/**
	 * Returns the number of available values
	 * 
	 * @return The number of available values
	 */
	public synchronized Double getN()
	{
		return n;
	}

	/**
	 * Returns the sum of the values that have been added
	 * 
	 * @return The sum or <code>Double.NaN</code> if no values have been added
	 */
	public synchronized Double getSum()
	{
		return sum.getResult();
	}

	/**
	 * Returns the mean of the values that have been added.
	 * 
	 * @return the mean
	 */
	public synchronized Double getMean()
	{
		return mean.getResult();
	}

	/**
	 * Returns the standard deviation of the values that have been added.
	 * 
	 * @return the standard deviation
	 */
	public synchronized Double getStandardDeviation()
	{
		double stdDev = Double.NaN;
		if (getN() > 0)
		{
			if (getN() > 1)
			{
				stdDev = Math.sqrt(getVariance());
			} else
			{
				stdDev = 0.0;
			}
		}
		return stdDev;
	}

	/**
	 * Returns the variance of the values that have been added.
	 * 
	 * @return the variance
	 */
	public synchronized Double getVariance()
	{
		return variance.getResult();
	}

	/**
	 * Returns the maximum of the values that have been added.
	 * 
	 * @return the maximum
	 */
	public synchronized Double getMax()
	{
		return max.getResult();
	}

	/**
	 * Returns the minimum of the values that have been added.
	 * 
	 * @return the minimum
	 */
	public synchronized Double getMin()
	{
		return min.getResult();
	}

	/**
	 * test if summary is empty false if n <= 0.
	 */
	public synchronized boolean isEmpty()
	{
		if (n <= 0)
		{
			return true;
		} else
		{
			return false;
		}
	}

	/**
	 * Resets object
	 */
	public synchronized void clear()
	{
		this.n = 0.0;
		min.clear();
		max.clear();
		sum.clear();
		mean.clear();
		variance.clear();
	}

	@Override
	public synchronized String toString()
	{
		return "SummaryStatistics(" + n + " , " + mean + " , " + min + " , " + max + " , " + sum + " , " + variance + "StandardDeviation("
			+ n + " , " + getStandardDeviation() + ")" + ")";
	}
}
