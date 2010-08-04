package deco4mas.examples.agentNegotiation.common.statistics;

/**
 * the arithmetic mean of values
 * 
 */
public class Mean extends AbstractSingleStatistic{
	
	/** sample size */
    protected double n;

    /** current mean */
    protected double value;

    protected double dev;

    protected double nDev;

    /** Constructs a Mean. */
    public Mean() {
    	n = 0;
        value = Double.NaN;
    }

    @Override
    public synchronized void addValue(final double d) {
    	if (n == 0) {
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
    public synchronized void clear() {
        value = Double.NaN;
        n = 0.0;
    }

    @Override
    public synchronized Double getResult() {
        return new Double(value);
    }

	@Override
    public synchronized Double getN() {
    	return new Double(n);
    }
    
	@Override
	public synchronized String toString()
	{
		return "Mean(" + n + " , "+ value +")";
	}
}
