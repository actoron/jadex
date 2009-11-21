package jadex.wfms.simulation.stateholder;

/**
 * State holder for Long values.
 */
public class LongStateSet extends AbstractNumericStateSet
{
	public LongStateSet(String parameterName)
	{
		super(parameterName, Long.MIN_VALUE, Long.MAX_VALUE);
	}
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return new Long(getStateAsLong(index));
	}
}
