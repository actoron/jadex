package jadex.bdi.model;


/**
 *  Interface for parameter element model.
 */
public interface IMParameterElement extends IMReferenceableElement
{
	/**
	 *  Get a parameter.
	 *  @return The parameter.
	 */
	public IMParameter getParameter(String name);
	
	/**
	 *  Get parameters.
	 *  @return The parameters.
	 */
	public IMParameter[] getParameters();
	
	/**
	 *  Get a parameter set.
	 *  @return The parameter set.
	 */
	public IMParameterSet getParameterSet(String name);
	
	/**
	 *  Get parameter sets.
	 *  @return The parameter sets.
	 */
	public IMParameterSet[] getParameterSets();
}
