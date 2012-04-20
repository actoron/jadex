package jadex.simulation.analysis.common.data.parameter;

import java.util.List;
import java.util.Set;

/**
 * Super Class for Parameters with more than one double value
 * @author 5Haubeck
 *
 */
public interface IASummaryParameter extends IAParameter
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
	public abstract Double getSumValue();

	/**
	 * Returns the mean of the values that have been added.
	 * 
	 * @return the mean
	 */
	public abstract Double getMeanValue();

	/**
	 * Returns the standard deviation of the values that have been added.
	 * 
	 * @return the standard deviation
	 */
	public abstract Double getStandardDeviationValue();

	/**
	 * Returns the variance of the values that have been added.
	 * 
	 * @return the variance
	 */
	public abstract Double getVarianceValue();

	/**
	 * Returns the maximum of the values that have been added.
	 * 
	 * @return the maximum
	 */
	public abstract Double getMaxValue();

	/**
	 * Returns the minimum of the values that have been added.
	 * 
	 * @return the minimum
	 */
	public abstract Double getMinValue();

	/**
	 * test if summary is empty false if n <= 0.
	 */
	public abstract boolean isEmpty();

	/**
	 * Resets object
	 */
	public abstract void clear();

}