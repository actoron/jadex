package jadex.wfms.simulation.stateset;

/**
 * State holder for Byte values.
 */
public class ByteStateSet extends AbstractNumericStateSet
{
	public ByteStateSet(String parameterName)
	{
		super(parameterName, Byte.MIN_VALUE, Byte.MAX_VALUE);
	}
	
	/** Returns the parameter type.
	 *  
	 *  @return parameter type
	 */
	public Class getParameterType()
	{
		return Byte.class;
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
