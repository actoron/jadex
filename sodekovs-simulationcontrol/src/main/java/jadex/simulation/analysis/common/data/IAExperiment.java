package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;

/**
 * Basic Experiment
 * @author 5Haubeck
 *
 */
public interface IAExperiment extends IADataObject
{
	/**
	 * Returns the Model
	 * @return the IAModel
	 */
	public IAModel getModel();

	/**
	 * Set the Model
	 * @param model the IAModel
	 */
	public void setModel(IAModel model);

	/**
	 * Get all input parameters
	 * @return input parameters
	 */
	public IAParameterEnsemble getConfigParameters();

	/**
	 * Get input parameter
	 * @param name parameter name
	 * @return input parameters
	 */
	public IAParameter getConfigParameter(String name);

	/**
	 * Set a input parameters
	 * @param parameters parameters to set
	 */
	public void setConfigParamters(IAParameterEnsemble parameters);

	/**
	 * Add a input parameter
	 * @param parameters parameter to add
	 */
	public void addConfigParamter(IAParameter parameter);

	/**
	 * Remove a input parameter
	 * @param parameters parameter to remove
	 */
	public void removeConfigParamter(String name);

	/**
	 * Get all output parameters
	 * @return output parameters
	 */
	public IAParameterEnsemble getResultParameters();

	/**
	 * Get output parameter
	 * @param name parameter name
	 * @return output parameters
	 */
	public IAParameter getResultParameter(String name);

	/**
	 * Set a output parameters
	 * @param parameters parameters to set
	 */
	public void setResultParamters(IAParameterEnsemble parameters);

	/**
	 * Add a output parameter
	 * @param parameters parameter to add
	 */
	public void addResultParamter(IAParameter parameter);

	/**
	 * Removes a output parameter
	 * @param parameters parameter to add
	 */
	public void removeResultParamter(String name);

	/**
	 * Get experiment parameter
	 * @param name parameter name
	 * @return experiment parameters
	 */
	public IAParameterEnsemble getExperimentParameters();

	/**
	 * Get experiment parameter
	 * @param name experiment name
	 * @return experiment parameters
	 */
	public IAParameter getExperimentParameter(String name);

	/**
	 * Set experiment parameters
	 * @param name experiments
	 */
	public void setExperimentParamters(IAParameterEnsemble parameters);

	/**
	 * Add experiment parameter
	 * @param parameter parameter
	 */
	public void addExperimentParamter(IAParameter parameter);

	/**
	 * Remove experiment parameter
	 * @param name parameter
	 */
	public void removeExperimentParamter(String name);
	
	/**
	 * Test to evaluated
	 * @return evaluated flag
	 */
	public Boolean isEvaluated();
	
	/**
	 * Set the evaluated flag
	 * @param evaluated flag
	 */
	public void setEvaluated(Boolean evaluated);
}
