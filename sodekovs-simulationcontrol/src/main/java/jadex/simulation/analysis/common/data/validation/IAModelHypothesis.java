package jadex.simulation.analysis.common.data.validation;

import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.data.parameter.IAParameter;

/**
 * A Hypothesis about correlation
 * @author 5Haubeck
 *
 */
public interface IAModelHypothesis extends IADataObject
{
	/**
	 * Returns the name of the model hypothesis
	 * @return name of model hypothesis
	 */
	public String getName();

	/**
	 * Set the name of the model hypothesis
	 * @param name of model hypothesis
	 */
	public void setName(String name);
	
	/**
	 * Set the correlation. true if positiv. false if negativ
	 * @param the correlation
	 */
	public void setCorrelation(Boolean correlation);
	
	/**
	 * Set the correlation. 
	 * @param the correlation. true if positiv. false if negativ
	 */
	public Boolean getCorrelation();

	/**
	 * Set the output Parameter
	 * @param parameters the output parameter to set
	 */
	public void setOutputParameter(IAParameter parameters);

	/**
	 * Returns the output Parameter
	 * @result the output parameter
	 */
	public IAParameter getOutputParameter();

	/**
	 * Set the output Parameter
	 * @param parameters the output parameter to set
	 */
	public void setInputParameter(IAParameter parameters);

	/**
	 * Returns the output Parameter
	 * @result the output parameter
	 */
	public IAParameter getInputParameter();
}
