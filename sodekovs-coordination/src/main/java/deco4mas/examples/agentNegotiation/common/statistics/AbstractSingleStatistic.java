package deco4mas.examples.agentNegotiation.common.statistics;


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
	
	@Override
	public String getSerializationString()
	{
		return getResult() + " ";
	}
}
