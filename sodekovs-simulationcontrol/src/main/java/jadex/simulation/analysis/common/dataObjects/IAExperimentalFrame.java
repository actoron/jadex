package jadex.simulation.analysis.common.dataObjects;

import java.util.Map;

public interface IAExperimentalFrame {
	
	public IAModel getModel();
	
//	public void setModel(IAModel model);
	
	public IAParameterCollection getInputParameters();
	
	public IAParameter getInputParameter(String name);
	
	public void setInputParamtersValues(Map<String, Object> values);
	
	public void setInputParamterValue(String name, Object value);
	
	public IAParameterCollection getExperimentParameters();
	
	public IAParameter getExperimentParameter(String name);
	
//	public void addExperimentParamter(IAParameter paramter);
	
//	public void removeExperimentParamter(String name);
}
