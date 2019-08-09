package jadex.bridge.sensor.unit;

/**
 *  Percent unit.
 */
public enum PercentUnit implements IConvertableUnit<Double>, IPrettyPrintUnit<Double>
{
	PERCENT;
	
	/**
	 *  Convert to a known unit.
	 */
	public Double convert(Double value)
	{
		int val = (int)(value*100);
		double ret = ((double)val)/100;
		return ret;
	}
	
	/**
	 *  Pretty print a value according to the underlying unit to a string.
	 *  @param value The value.
	 *  @return The pretty printed string.
	 */
	public String prettyPrint(Double value)
	{
		return ""+convert(value)+" %";
	}
}