package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * Code was created by the Author within a Simulation Project at the University
 * of Hamburg Maximal Value
 * 
 * @author 5Haubeck
 * 
 */
public class Max extends AbstractSingleStatistic {

	/** Number of values */
	private Double nValue;

	/** Current value (min) */
	private double value;

	/**
	 * Construct a Min
	 */
	public Max() {
		nValue = 0.0;
		value = Double.NaN;
	}

	public Double getnValue() {
		return nValue;
	}

	public void setnValue(Double nValue) {
		this.nValue = nValue;
	}

	@Override
	public synchronized void addValue(final double d) {
		if (d > value || Double.isNaN(value)) {
			value = d;
		}
		nValue++;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
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
		return "Max(" + nValue + " , " + value + ")";
	}
}
