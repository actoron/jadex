package jadex.wfms.simulation.stateholder;

/**
 * State holder for Integer values.
 */
public class IntegerStateHolder extends AbstractNumericStateHolder
{
	public IntegerStateHolder()
	{
		super(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	/**
	 * Returns the current state.
	 * @return the current state
	 */
	public Object getState()
	{
		return new Integer((int) getStateAsLong());
	}
}
