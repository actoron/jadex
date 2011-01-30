package jadex.simulation.analysis.common.dataObjects;

import java.util.Map;

public interface IAExperimentResult
{
	public IAModel getModel();
	
//	public void setModel(IAModel model);
	
	public IAParameterCollection getResultParameters();
	
	public IAParameter getResultParameter(String name);
	
	public void setResultParamtersValues(Map<String, Object> values);
	
	public void setResultParamterValue(String name, Object value);
	
	
	
	
	
	
	
}
