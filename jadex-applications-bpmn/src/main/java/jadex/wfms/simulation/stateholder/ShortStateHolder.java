package jadex.wfms.simulation.stateholder;

/**
 * State holder for Short values.
 */
public class ShortStateHolder extends AbstractNumericStateHolder
{
	public ShortStateHolder()
	{
		super(Short.MIN_VALUE, Short.MAX_VALUE);
	}
	
	/**
	 * Returns the current state.
	 * @return the current state
	 */
	public Object getState()
	{
		return new Short((short) getStateAsLong());
	}
}
