package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * the variance of values
 * 
 * @see Chan, T. F. and J. G. Lewis 1979, Communications of the ACM, vol. 22 no.
 *      9, pp. 526-531.
 */
public class Variance extends AbstractSingleStatistic
{

	/** sample size */
	protected double n;

	/** current varaince */
	protected double meanVar;

	/** current varaince */
	protected double value;

	/** mean of values */
	protected Mean mean;

	/** Constructs a Mean. */
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
