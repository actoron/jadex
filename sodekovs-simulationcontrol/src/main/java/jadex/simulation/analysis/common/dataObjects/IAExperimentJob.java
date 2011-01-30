package jadex.simulation.analysis.common.dataObjects;

import java.util.Map;

public interface IAExperimentJob {
	
	public IAModel getModel();
	
//	public void setModel(IAModel model);
	
	public Integer getID();
	
	public IAExperimentalFrame getExperimentalFrame();
	
//	public void setExperimentalFrame(IAExperimentalFrame experimentalFrame);
	
	public IAExperimentResult getExperimentResult();
	
//	public void setExperimentResult(IAExperimentResult experimentResult);
	
	public void setExperimentResultValues(Map<String, Object> values);
	
	public void setExperimentResultValue(String name, Object value);

}
