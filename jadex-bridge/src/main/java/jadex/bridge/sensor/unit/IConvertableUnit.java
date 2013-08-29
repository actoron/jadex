package jadex.bridge.sensor.unit;

/**
 *  Unit interface supporting conversions.
 */
public interface IConvertableUnit<T>
{
	/**
	 *  Convert a value according to the underlying unit.
	 */
	public T convert(T value);
}
