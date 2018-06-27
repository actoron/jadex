package jadex.wfms.simulation.stateset;

/**
 * State holder for Integer values.
 */
public class IntegerStateSet extends AbstractNumericStateSet
{
	public IntegerStateSet(String parameterName)
	{
		super(parameterName, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	/** Returns the parameter type.
	 *  
	 *  @return parameter type
	 */
	public Class getParameterType()
	{
		return Integer.class;
	}
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return Integer.valueOf((int) getStateAsLong(index));
	}
}
