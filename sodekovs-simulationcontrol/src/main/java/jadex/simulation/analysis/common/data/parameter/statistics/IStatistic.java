package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * Code was created by the Author within a Simulation Project at the University of Hamburg
 * Interface for Statistic
 * @author 5Haubeck
 *
 */
public interface IStatistic
{
	 /**
     * Add a values
     * @param d  the new value.
     */
    public void addValue(double d);

    /**
     * Add a array of values
     *
     * @param values  array holding the new values to add
     */
    public void addValues(double[] values);

    /**
     * Returns the number of values
     * @return the number of values.
     */
    public double getN();
    
    /**
     * Clears the Statistic
     */
    public void clear();
}
