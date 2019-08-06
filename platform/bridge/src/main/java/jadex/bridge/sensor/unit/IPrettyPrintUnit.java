package jadex.bridge.sensor.unit;

/**
 *  Pretty print a value of unit of type T.
 */
public interface IPrettyPrintUnit<T>
{
	/**
	 *  Pretty print a value according to the underlying unit to a string.
	 *  @param value The value.
	 *  @return The pretty printed string.
	 */
	public String prettyPrint(T value);
}
