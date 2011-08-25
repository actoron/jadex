package jadex.simulation.analysis.application.standalone;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.allocation.IAllocationStrategy;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;
import jadex.simulation.analysis.service.simulation.allocation.IAAllocateExperimentsService;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

public class AAllocateExperimentsService extends ADatabasedService implements IAAllocateExperimentsService
{

	public AAllocateExperimentsService(IExternalAccess access)
	{
		super(access, IAAllocateExperimentsService.class, true);
	}

	@Override
	public IFuture allocateExperiment(UUID sessionId, IAExperimentBatch experiments)
	{
		if (sessionId == null ) sessionId = (UUID) createSession(null).get(susThread);
		ADataSessionView view = (ADataSessionView) sessionViews.get(sessionId);
		view.startGUI(experiments);
		
		Collection<IAExecuteExperimentsService> services = (Collection<IAExecuteExperimentsService>) SServiceProvider.getServices(access.getServiceProvider(), IAExecuteExperimentsService.class).get(susThread);
		if (experiments.getAllocationStrategy() != null)
		{
			IAllocationStrategy strategy = experiments.getAllocationStrategy();
			Map<IAExperiment, IAnalysisService> alloMap = new HashMap<IAExperiment, IAnalysisService>();
			for (IAExperiment experiment : experiments.getExperiments().values())
			{
				Set<IAExecuteExperimentsService> rightServices = new HashSet<IAExecuteExperimentsService>();
				for (IAExecuteExperimentsService service : services)
				{
					if (service.supportedModels().contains(experiment.getModel().getType())) rightServices.add(service);
				}
				SortedSet<IAExecuteExperimentsService> sortedSet = (SortedSet<IAExecuteExperimentsService>) strategy.orderService(rightServices);
				alloMap.put(experiment, sortedSet.first());
			}
			experiments.setAllocation(alloMap);
		}
		
		return new Future(experiments);
	}


}
