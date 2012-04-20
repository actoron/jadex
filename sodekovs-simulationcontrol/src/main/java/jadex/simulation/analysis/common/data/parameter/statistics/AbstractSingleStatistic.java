package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * Code was created by the Author within a Simulation Project at the University of Hamburg
 * Abstract Single Statistic
 * @author 5Haubeck
 *
 */
public abstract class AbstractSingleStatistic implements ISingleStatistic
{
	public abstract Double getResult();

	public abstract void clear();

	public abstract void addValue(final double d);
	

	/**
	 * Adds all values by calling addValue(Double d)
	 * 
	 * @param values
	 *            array of values
	 */
	public synchronized void addValues(double[] values)
	{
		for (double d : values)
		{
			addValue(d);
		}
	}
}
