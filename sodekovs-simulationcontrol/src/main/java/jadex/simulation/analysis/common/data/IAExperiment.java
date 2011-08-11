package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;

public interface IAExperiment extends IADataObject
{
	public IAModel getModel();

	public void setModel(IAModel model);

	public IAParameterEnsemble getInputParameters();

	public IAParameter getInputParameter(String name);

	public void setInputParamters(IAParameterEnsemble parameters);

	public void addInputParamter(IAParameter parameter);

	public void removeInputParamter(String name);

	public IAParameterEnsemble getOutputParameters();

	public IAParameter getOutputParameter(String name);

	public void setOutputParamters(IAParameterEnsemble parameters);

	public void addOutputParamter(IAParameter parameter);

	public void removeOutputParamter(String name);

	public IAParameterEnsemble getExperimentParameters();

	public IAParameter getExperimentParameter(String name);

	public void setExperimentParamters(IAParameterEnsemble parameters);

	public void addExperimentParamter(IAParameter parameter);

	public void removeExperimentParamter(String name);
	
	public Boolean isEvaluated();
	
	public void setEvaluated(Boolean evaluated);
}
