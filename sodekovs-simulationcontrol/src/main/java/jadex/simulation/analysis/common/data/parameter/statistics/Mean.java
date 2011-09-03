package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * Code was created by the Author within a Simulation Project at the University of Hamburg.
 * The arithmetic mean of values
 * 
 * @author 5Haubeck
 */
public class Mean extends AbstractSingleStatistic
{

	protected double n;
	protected double value;
	protected double dev;
	protected double nDev;

	public Mean()
	{
		n = 0;
		value = Double.NaN;
	}

	@Override
	public synchronized void addValue(final double d)
	{
		if (n == 0)
		{
			value = 0.0;
		}
		if (!Double.isNaN(d))
		{
			n++;
			dev = d - value;
			nDev = dev / n;
			value += nDev;
		}
	}

	@Override
	public synchronized void clear()
	{
		value = Double.NaN;
		n = 0.0;
	}

	@Override
	public synchronized Double getResult()
	{
		return new Double(value);
	}

	@Override
	public synchronized Double getN()
	{
		return new Double(n);
	}

	@Override
	public synchronized String toString()
	{
		return "Mean(" + n + " , " + value + ")";
	}
}
