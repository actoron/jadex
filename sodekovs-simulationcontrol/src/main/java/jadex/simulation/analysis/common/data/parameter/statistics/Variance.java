package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * /**
 * Code was created by the Author within a Simulation Project at the University of Hamburg
 * the variance of values
 * 
 * @author 5Haubeck
 */
public class Variance extends AbstractSingleStatistic
{
	protected double n;

	protected double meanVar;

	protected double value;

	protected Mean mean;

	public Variance()
	{
		n = 0.0;
		meanVar = Double.NaN;
		mean = new Mean();
	}

	@Override
	public synchronized void addValue(final double d)
	{
		if (n == 0)
		{
			meanVar = 0.0;
		}
		mean.addValue(d);
		n++;
		meanVar += ((double) n - 1) * mean.dev * mean.nDev;
		value = meanVar / (mean.n - 1.0);
	}

	@Override
	public synchronized void clear()
	{
		meanVar = Double.NaN;
		n = 0.0;
		mean.clear();
	}

	@Override
	public synchronized Double getResult()
	{
		if (mean.n == 0)
		{
			return Double.NaN;
		} else if (mean.n == 1)
		{
			return 0.0;
		} else
		{
			return value;
		}
	}

	@Override
	public synchronized Double getN()
	{
		return new Double(n);
	}

	@Override
	public synchronized String toString()
	{
		return "Variance(" + n + " , " + value + ")";
	}
}
