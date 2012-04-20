package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.data.allocation.AllocationStrategyWorkload;
import jadex.simulation.analysis.common.data.allocation.IAllocationStrategy;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisService;
import jadex.simulation.analysis.common.util.AConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * AExperimentBatch Implementation
 * 
 * @author 5Haubeck
 * 
 */
public class AExperimentBatch extends ADataObject implements IAExperimentBatch {
	IAllocationStrategy strategy = new AllocationStrategyWorkload();

	private Map<String, IAExperiment> experimentMap;

	private Map<IAExperiment, IAnalysisService> allocation;

	public AExperimentBatch() {
		synchronized (mutex) {
//			view = new AExperimentBatchView(this);
			allocation = Collections
					.synchronizedMap(new HashMap<IAExperiment, IAnalysisService>());
		}
	}

	public AExperimentBatch(String name) {
		super(name);
		synchronized (mutex) {
			experimentMap = Collections
					.synchronizedMap(new HashMap<String, IAExperiment>());
//			view = new AExperimentBatchView(this);
			allocation = Collections
					.synchronizedMap(new HashMap<IAExperiment, IAnalysisService>());
		}
	}

	public IAllocationStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(IAllocationStrategy strategy) {
		synchronized (mutex) {
			this.strategy = strategy;
		}
	}

	public Map<String, IAExperiment> getExperimentMap() {
		return experimentMap;
	}

	public void setExperimentMap(Map<String, IAExperiment> experimentMap) {
		synchronized (mutex) {
			this.experimentMap = experimentMap;
		}
	}

	public Map<IAExperiment, IAnalysisService> getAllocation() {
		return allocation;
	}

	@Override
	public Map<String, IAExperiment> getExperiments() {
		return experimentMap;
	}

	@Override
	public IAExperiment getExperiment(String experiment) {
		return experimentMap.get(experiment);
	}

	@Override
	public void addExperiment(IAExperiment experiment) {
		synchronized (mutex) {
			experimentMap.put(experiment.getName(), experiment);
			experiment.addListener(view);
		}
	}

	@Override
	public void removeExperiment(IAExperiment experiment) {
		synchronized (mutex) {
			experimentMap.remove(experiment);
		}
	}

	@Override
	public Boolean isEvaluated() {
		Boolean evaluated = true;
		for (IAExperiment experiment : experimentMap.values()) {
			if (experiment.isEvaluated() == false)
				evaluated = false;
		}
		notify(new ADataEvent(this, AConstants.EXPBATCH_EVA, evaluated));
		return evaluated;
	}

	@Override
	public IAllocationStrategy getAllocationStrategy() {
		return strategy;
	}

	@Override
	public void setAllocationStrategy(IAllocationStrategy strategy) {
		synchronized (mutex) {
			this.strategy = strategy;
		}
	}

	@Override
	public void setAllocation(Map<IAExperiment, IAnalysisService> allocations) {
		synchronized (mutex) {
			allocation.clear();
			for (IAExperiment exp : allocations.keySet()) {
				allocation.put(exp, allocations.get(exp));
			}
			notify(new ADataEvent(this, AConstants.EXPBATCH_ALLO, allocation));

		}
	}

	@Override
	public IAnalysisService getAllocation(IAExperiment exp) {
		synchronized (mutex) {
			return allocation.get(exp);
		}
	}

	@Override
	public Map<IAExperiment, IAnalysisService> getAllocations() {
		return allocation;
	}

	@Override
	public ADataObject clonen() {
		AExperimentBatch clone = new AExperimentBatch(name);
		clone.setEditable(editable);
		clone.setAllocationStrategy(strategy);
		Map<IAExperiment, IAnalysisService> allocations = new HashMap<IAExperiment, IAnalysisService>();
		for (IAExperiment exp : getExperiments().values()) {
			IAExperiment expClone = (IAExperiment) exp.clonen();
			clone.addExperiment(expClone);
			allocations.put(expClone, getAllocation(exp));
		}
		clone.setAllocation(allocations);

		return clone;
	}

}
