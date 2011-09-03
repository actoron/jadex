package jadex.simulation.analysis.common.data.parameter;

import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.data.IADataView;

import java.util.Map;
import java.util.Set;

public interface IAParameterEnsemble extends IADataObject
{
	/**
	 * Sets the name
	 * @param name of parameter
	 */
	public void setName(String name);

	/**
	 * Return the name
	 * @param name of parameter
	 */
	public String getName();

	/**
	 * Tests if object is feasible
	 * @return flag, indicating if parameter is feasible 
	 */
	public Boolean isFeasible();

	/**
	 * Tests if Ensemble contains Parameter
	 * @param name the parameter to test
	 * @return true, if Ensemble contains Parameter
	 */
	public Boolean containsParameter(String name);

	/**
	 * Add the Parameter
	 * @param name the parameter to add
	 */
	public void addParameter(IAParameter parameter);

	/**
	 * Remove the Parameter
	 * @param name the parameter to remove
	 */
	public void removeParameter(String name);

	/**
	 * Get all Parameters
	 * @return the map of Parameters
	 */
	public Map<String, IAParameter> getParameters();

	/**
	 * Get the given Parameter
	 * @param name parameter to get
	 * @return the parameter
	 */
	public IAParameter getParameter(String name);

	/**
	 * Removes all Parameters
	 */
	public void clearParameters();

	/**
	 * Get the number of parameters
	 * @return the number of parameters
	 */
	public Integer numberOfParameters();

	/**
	 * Test if ensemble contains parameters
	 * @return true, if contains parameters
	 */
	public Boolean hasParameters();

	

}
