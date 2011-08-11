package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.service.simulation.Modeltype;

public interface IAModel extends IADataObject
{

	public String getName();

	public void setName(String name);

	public Modeltype getType();

	public void setType(Modeltype type);
	
	public String getModelpath();
	
	public void setModelpath(String path);

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
