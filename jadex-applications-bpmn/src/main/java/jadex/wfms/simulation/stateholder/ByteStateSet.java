package jadex.wfms.simulation.stateholder;

/**
 * State holder for Byte values.
 */
public class ByteStateSet extends AbstractNumericStateHolder
{
	public ByteStateSet(String parameterName)
	{
		super(parameterName, Byte.MIN_VALUE, Byte.MAX_VALUE);
	}
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return new Byte((byte) getStateAsLong(index));
	}
}
