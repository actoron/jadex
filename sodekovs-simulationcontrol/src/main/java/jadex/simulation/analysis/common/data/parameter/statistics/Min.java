package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * Code was created by the Author within a Simulation Project at the University
 * of Hamburg Minimal value
 * 
 * @author 5Haubeck
 */
public class Min extends AbstractSingleStatistic {
	private Double nValue;
	private double value;

	public Min() {
		nValue = 0.0;
		value = Double.NaN;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public synchronized void addValue(final double d) {
		if (d < value || Double.isNaN(value)) {
			value = d;
		}
		nValue++;
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
		return "Min(" + nValue + " , " + value + ")";
	}
}
