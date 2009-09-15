package jadex.wfms.simulation.stateholder;

/**
 * State holder for Long values.
 */
public class LongStateHolder extends AbstractNumericStateHolder
{
	public LongStateHolder()
	{
		super(Long.MIN_VALUE, Long.MAX_VALUE);
	}
	
	/**
	 * Returns the current state.
	 * @return the current state
	 */
	public Object getState()
	{
		return new Long(getStateAsLong());
	}
}
