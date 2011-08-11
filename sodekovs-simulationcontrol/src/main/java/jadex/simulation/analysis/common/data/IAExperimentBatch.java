package jadex.simulation.analysis.common.data;

import java.util.Map;

import jadex.bridge.service.IServiceIdentifier;
import jadex.simulation.analysis.common.data.allocation.IAllocationStrategy;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;

public interface IAExperimentBatch extends IADataObject
{
	public Map<String, IAExperiment> getExperiments();

	public IAExperiment getExperiment(String experiment);

	public void addExperiment(IAExperiment experiment);

	public void removeExperiment(IAExperiment experiment);
	
	public Boolean isEvaluated();
	
	public IAllocationStrategy getAllocationStrategy();
	
	public void setAllocationStrategy(IAllocationStrategy strategy);

	void setAllocation(Map<IAExperiment, IAnalysisService> allocations);

	IAnalysisService getAllocation(IAExperiment exp);

	public Map<IAExperiment, IAnalysisService> getAllocations();
}
