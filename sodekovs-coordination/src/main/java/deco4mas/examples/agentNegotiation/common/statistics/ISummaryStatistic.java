package deco4mas.examples.agentNegotiation.common.statistics;

/**
 * Statistic for a summary
 */
public interface ISummaryStatistic extends IStatistic
{
	/**
	 * Returns the arithmetic mean
	 * 
	 * @return The mean or Double.NaN if no values present
	 */
	Double getMean();

	/**
	 * Returns the variance
	 * 
	 * @return The variance, Double.NaN if no values present
	 */
	Double getVariance();

	/**
	 * Returns the standard deviation
	 * 
	 * @return The standard deviation, Double.NaN if no values present
	 */
	Double getStandardDeviation();

	/**
	 * Returns the maximum
	 * 
	 * @return The max or Double.NaN if no values present
	 */
	Double getMax();

	/**
	 * Returns the minimum
	 * 
	 * @return The min or Double.NaN if no values present
	 */
	Double getMin();

	/**
	 * Returns the number of values
	 * 
	 * @return The number of values
	 */
	Double getN();

	/**
	 * Returns the sum
	 * 
	 * @return The sum or Double.NaN if no valuespresent
	 */
	Double getSum();

}