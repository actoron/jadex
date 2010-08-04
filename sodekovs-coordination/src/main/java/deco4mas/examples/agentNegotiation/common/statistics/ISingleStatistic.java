package deco4mas.examples.agentNegotiation.common.statistics;

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