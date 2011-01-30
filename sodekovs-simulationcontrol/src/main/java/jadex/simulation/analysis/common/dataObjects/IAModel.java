package jadex.simulation.analysis.common.dataObjects;

public interface IAModel {
	
	public String getName();
	
	public String getType();
	
//	public void setInputParameter(IAParameter parameter);
	
//	public void setInputParameters(IAParameterCollection parameters);
	
	public IAParameterCollection getInputParameters();
	
	public IAParameter getInputParameter(String name);
	
//	public void setOutputParameter(IAParameter parameter);
	
//	public void setOutputParameters(IAParameterCollection parameters);
	
	public IAParameterCollection getOutputParameters();
	
	public IAParameter getOutputParameter(String name);
	
	public IAExperimentalFrame createExperimentalFrame(IAParameterCollection inputParameter);
	
	public IAExperimentResult createExperimentResult();
}
