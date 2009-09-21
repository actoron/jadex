package jadex.wfms.simulation.stateholder;

/**
 * State holder for Short values.
 */
public class ShortStateSet extends AbstractNumericStateHolder
{
	public ShortStateSet(String parameterName)
	{
		super(parameterName, Short.MIN_VALUE, Short.MAX_VALUE);
	}
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return new Short((short) getStateAsLong(index));
	}
}
