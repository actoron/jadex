package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * Code was created by the Author within a Simulation Project at the University of Hamburg
 * One Single Statistic
 * @author 5Haubeck
 *
 */
public interface ISingleStatistic extends IStatistic
{

	/**
	 * Returns the current value
	 * 
	 * @return value of the value
	 */
	public Double getResult();
}