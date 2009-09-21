package jadex.wfms.simulation.stateholder;

public abstract class AbstractParameterStateSet implements IParameterStateSet
{
	protected String name;
	
	/** Returns the parameter name.
	 *  
	 *  @return parameter name
	 */
	public String getParameterName()
	{
		return name;
	}
}
