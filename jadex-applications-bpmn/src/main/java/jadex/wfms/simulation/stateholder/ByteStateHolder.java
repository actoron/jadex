package jadex.wfms.simulation.stateholder;

/**
 * State holder for Byte values.
 */
public class ByteStateHolder extends AbstractNumericStateHolder
{
	public ByteStateHolder()
	{
		super(Byte.MIN_VALUE, Byte.MAX_VALUE);
	}
	
	/**
	 * Returns the current state.
	 * @return the current state
	 */
	public Object getState()
	{
		return new Byte((byte) getStateAsLong());
	}
}
