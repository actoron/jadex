package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * One single statistic value
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