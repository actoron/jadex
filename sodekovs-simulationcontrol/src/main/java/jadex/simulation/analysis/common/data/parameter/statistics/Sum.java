package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * Code was created by the Author within a Simulation Project at the University of Hamburg
 * sum value
 * 
 * @author 5Haubeck
 */
public class Sum extends AbstractSingleStatistic
{

	private double n;

	private double value;

	public Sum()
	{
		n = 0.0;
		value = Double.NaN;
	}

	@Override
	public void addValue(final double d)
	{
		if (n == 0)
		{
			value = d;
		} else
		{
			value += d;
		}
		n++;
	}

	@Override
	public Double getResult()
	{
		return new Double(value);
	}

	@Override
	public Double getN()
	{
		return new Double(n);
	}

	@Override
	public void clear()
	{
		value = Double.NaN;
		n = 0;
	}
	
	@Override
	public synchronized String toString()
	{
		return "Sum(" + n + " , "+ value +")";
	}
}
