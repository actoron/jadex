package jadex.simulation.analysis.common.data.parameter.statistics;

/**
 * Code was created by the Author within a Simulation Project at the University of Hamburg
 * sum value
 * 
 * @author 5Haubeck
 */
public class Sum extends AbstractSingleStatistic
{

	private double nvalue;

	private double value;

	public Sum()
	{
		nvalue = 0.0;
		value = Double.NaN;
	}

	@Override
	public void addValue(final double d)
	{
		if (nvalue == 0)
		{
			value = d;
		} else
		{
			value += d;
		}
		nvalue++;
	}
	
	
	
	

	public double getNvalue() {
		return nvalue;
	}

	public void setNvalue(double nvalue) {
		this.nvalue = nvalue;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}


	@Override
	public Double getResult()
	{
		return new Double(value);
	}

	@Override
	public double getN()
	{
		return nvalue;
	}

	@Override
	public void clear()
	{
		value = Double.NaN;
		nvalue = 0;
	}
	
	@Override
	public synchronized String toString()
	{
		return "Sum(" + nvalue + " , "+ value +")";
	}
}
