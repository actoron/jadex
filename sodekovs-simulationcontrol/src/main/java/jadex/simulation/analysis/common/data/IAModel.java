package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.data.simulation.Modeltype;

/**
 * The model class of a simulation model
 * @author 5Haubeck
 *
 */
public interface IAModel extends IADataObject
{
	/**
	 * Return the model name
	 * @return the name
	 */
	public String getName();

	/**
	 * Set the model name
	 * @param the name
	 */
	public void setName(String name);

	/**
	 * Returns the model type
	 * @result the type as Modeltype
	 */
	public Modeltype getType();

	/**
	 * Set the model type
	 * @param type the Modeltype
	 */
	public void setType(Modeltype type);
	
	/**
	 * Returns the model path
	 * @return model path
	 */
	public String getModelpath();
	
	/**
	 * Set the model path
	 * @param path the model path
	 */
	public void setModelpath(String path);

	/**
	 * Returns the input Parameters
	 * @return input parameters
	 */
	public IAParameterEnsemble getInputParameters();

	/**
	 * Returns a input Parameter for given name
	 * @return input parameter
	 */
	public IAParameter getInputParameter(String name);

	/**
	 * Set the input Parameters
	 * @param input parameters
	 */
	public void setInputParameters(IAParameterEnsemble parameters);

	/**
	 * Add a input Parameter
	 * @param the input parameter to get
	 */
	public void addInputParameter(IAParameter parameter);

	/**
	 * Remove a input Parameter
	 * @param the input parameter to remove
	 */
	public void removeInputParameter(String name);

	/**
	 * Returns the output Parameters
	 * @return output parameters
	 */
	public IAParameterEnsemble getOutputParameters();

	/**
	 * Returns a output Parameter for given name
	 * @return output parameter
	 */
	public IAParameter getOutputParameter(String name);

	/**
	 * Set the output Parameters
	 * @param output parameters
	 */
	public void setOutputParameters(IAParameterEnsemble parameters);

	/**
	 * Set the output Parameter
	 * @param output parameter name to get
	 */
	public void addOutputParameter(IAParameter parameter);

	/**
	 * Remove the output Parameter
	 * @param output parameter name to remove
	 */
	public void removeOutputParameter(String name);
}
