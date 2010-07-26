package jadex.bdi.model;

/**
 *  Interace for config parameter model element.
 */
public interface IMConfigParameterElement extends IMElement
{
	/**
	 *  Get parameters.
	 *  @return The parameters.
	 */
	public IMConfigParameter[] getParameters();
	
	/**
	 *  Get parameter sets.
	 *  @return The parameter sets.
	 */
	public IMConfigParameterSet[] getParameterSets();
}
