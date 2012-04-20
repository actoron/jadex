package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * /** Code was created by the Author within a Simulation Project at the
 * University of Hamburg the variance of values
 * 
 * @author 5Haubeck
 */
public class Variance extends AbstractSingleStatistic {
	protected double nValue;

	protected double meanVar;

	protected double value;

	protected Mean mean;

	public Variance() {
		nValue = 0.0;
		meanVar = Double.NaN;
		mean = new Mean();
	}

	public double getnValue() {
		return nValue;
	}

	public void setnValue(double nValue) {
		this.nValue = nValue;
	}

	public double getMeanVar() {
		return meanVar;
	}

	public void setMeanVar(double meanVar) {
		this.meanVar = meanVar;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Mean getMean() {
		return mean;
	}

	public void setMean(Mean mean) {
		this.mean = mean;
	}

	@Override
	public synchronized void addValue(final double d) {
		if (nValue == 0) {
			meanVar = 0.0;
		}
		mean.addValue(d);
		nValue++;
		meanVar += ((double) nValue - 1) * mean.dev * mean.nDev;
		value = meanVar / (mean.nValue - 1.0);
	}

	@Override
	public synchronized void clear() {
		meanVar = Double.NaN;
		nValue = 0.0;
		mean.clear();
	}

	@Override
	public synchronized Double getResult() {
		if (mean.nValue == 0) {
			return Double.NaN;
		} else if (mean.nValue == 1) {
			return 0.0;
		} else {
			return value;
		}
	}

	@Override
	public synchronized double getN() {
		return nValue;
	}

	@Override
	public synchronized String toString() {
		return "Variance(" + nValue + " , " + value + ")";
	}
}
