package jadex.wfms.simulation.stateholder;

/**
 * State holder for Integer values.
 */
public class IntegerStateSet extends AbstractNumericStateSet
{
	public IntegerStateSet(String parameterName)
	{
		super(parameterName, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return new Integer((int) getStateAsLong(index));
	}
}
