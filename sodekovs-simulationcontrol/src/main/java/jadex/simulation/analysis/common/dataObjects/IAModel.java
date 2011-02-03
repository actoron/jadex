package jadex.simulation.analysis.common.dataObjects;

import jadex.simulation.analysis.common.dataObjects.parameter.IAParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;

public interface IAModel extends IADataObject
{

	public String getName();

	public void setName(String name);

	public String getType();

	public void setType(String type);

	public IAParameterEnsemble getInputParameters();

	public IAParameter getInputParameter(String name);

	public void setInputParameters(IAParameterEnsemble parameters);

	public void addInputParameter(IAParameter parameter);

	public void removeInputParameter(String name);

	public IAParameterEnsemble getOutputParameters();

	public IAParameter getOutputParameter(String name);

	public void setOutputParameters(IAParameterEnsemble parameters);

	public void addOutputParameter(IAParameter parameter);

	public void removeOutputParameter(String name);
}
