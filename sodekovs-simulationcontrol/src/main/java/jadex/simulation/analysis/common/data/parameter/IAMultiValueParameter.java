package jadex.simulation.analysis.common.data.parameter;

import java.util.List;
import java.util.Set;

public interface IAMultiValueParameter extends IAParameter
{

	/**
	 * Add a value
	 * 
	 * @param value
	 *            value to add
	 */
	public abstract void addValue(Double value);

	/**
	 * Adds all values by calling addValue(Double d)
	 * 
	 * @param values
	 *            array of values
	 */
	public abstract void addValues(Double[] values);
	
	/**
	 * Get all values
	 * 
	 */
	public abstract List<Double> getValues();

	/**
	 * Returns the number of available values
	 * 
	 * @return The number of available values
	 */
	public abstract Double getN();

	/**
	 * Returns the sum of the values that have been added
	 * 
	 * @return The sum or <code>Double.NaN</code> if no values have been added
	 */
	public abstract Double getSum();

	/**
	 * Returns the mean of the values that have been added.
	 * 
	 * @return the mean
	 */
	public abstract Double getMean();

	/**
	 * Returns the standard deviation of the values that have been added.
	 * 
	 * @return the standard deviation
	 */
	public abstract Double getStandardDeviation();

	/**
	 * Returns the variance of the values that have been added.
	 * 
	 * @return the variance
	 */
	public abstract Double getVariance();

	/**
	 * Returns the maximum of the values that have been added.
	 * 
	 * @return the maximum
	 */
	public abstract Double getMax();

	/**
	 * Returns the minimum of the values that have been added.
	 * 
	 * @return the minimum
	 */
	public abstract Double getMin();

	/**
	 * test if summary is empty false if n <= 0.
	 */
	public abstract boolean isEmpty();

	/**
	 * Resets object
	 */
	public abstract void clear();

}