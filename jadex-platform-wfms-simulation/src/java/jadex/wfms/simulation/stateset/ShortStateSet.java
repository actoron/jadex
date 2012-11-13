package jadex.wfms.simulation.stateset;

/**
 * State holder for Short values.
 */
public class ShortStateSet extends AbstractNumericStateSet
{
	public ShortStateSet(String parameterName)
	{
		super(parameterName, Short.MIN_VALUE, Short.MAX_VALUE);
	}
	
	/** Returns the parameter type.
	 *  
	 *  @return parameter type
	 */
	public Class getParameterType()
	{
		return Short.class;
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
