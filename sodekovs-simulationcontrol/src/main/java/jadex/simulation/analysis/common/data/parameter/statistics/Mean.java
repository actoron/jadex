package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * Code was created by the Author within a Simulation Project at the University
 * of Hamburg. The arithmetic mean of values
 * 
 * @author 5Haubeck
 */
public class Mean extends AbstractSingleStatistic {

	protected double nValue;
	protected double value;
	protected double dev;
	protected double nDev;

	public Mean() {
		nValue = 0;
		value = Double.NaN;
	}

	@Override
	public synchronized void addValue(final double d) {
		if (nValue == 0) {
			value = 0.0;
		}
		if (!Double.isNaN(d)) {
			nValue++;
			dev = d - value;
			nDev = dev / nValue;
			value += nDev;
		}
	}

	public double getnValue() {
		return nValue;
	}

	public void setnValue(double nValue) {
		this.nValue = nValue;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getDev() {
		return dev;
	}

	public void setDev(double dev) {
		this.dev = dev;
	}

	public double getnDev() {
		return nDev;
	}

	public void setnDev(double nDev) {
		this.nDev = nDev;
	}

	@Override
	public synchronized void clear() {
		value = Double.NaN;
		nValue = 0.0;
	}

	@Override
	public synchronized Double getResult() {
		return new Double(value);
	}

	@Override
	public synchronized double getN() {
		return nValue;
	}

	@Override
	public synchronized String toString() {
		return "Mean(" + nValue + " , " + value + ")";
	}
}
