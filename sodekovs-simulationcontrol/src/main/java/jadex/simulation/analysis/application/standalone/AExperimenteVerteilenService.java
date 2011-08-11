package jadex.simulation.analysis.application.standalone;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.ADataObject;
import jadex.simulation.analysis.common.data.IADataView;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.allocation.IAllocationStrategy;
import jadex.simulation.analysis.common.data.factories.AExperimentFactory;
import jadex.simulation.analysis.common.data.factories.AModelFactory;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.service.basic.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;
import jadex.simulation.analysis.service.basic.view.session.IASessionView;
import jadex.simulation.analysis.service.dataBased.engineering.IADatenobjekteErstellenService;
import jadex.simulation.analysis.service.simulation.Modeltype;
import jadex.simulation.analysis.service.simulation.allocation.IAExperimenteVerteilenService;
import jadex.simulation.analysis.service.simulation.execution.IAExperimentAusfuehrenService;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

public class AExperimenteVerteilenService extends ADatabasedService implements IAExperimenteVerteilenService
{

	public AExperimenteVerteilenService(IExternalAccess access)
	{
		super(access, IAExperimenteVerteilenService.class, true);
	}

	@Override
	public IFuture allocateExperiment(UUID sessionId, IAExperimentBatch experiments)
	{
		if (sessionId == null ) sessionId = (UUID) createSession(null).get(susThread);
		ADataSessionView view = (ADataSessionView) sessionViews.get(sessionId);
		view.startGUI(experiments);
		
		Collection<IAExperimentAusfuehrenService> services = (Collection<IAExperimentAusfuehrenService>) SServiceProvider.getServices(access.getServiceProvider(), IAExperimentAusfuehrenService.class).get(susThread);
		if (experiments.getAllocationStrategy() != null)
		{
			IAllocationStrategy strategy = experiments.getAllocationStrategy();
			Map<IAExperiment, IAnalysisService> alloMap = new HashMap<IAExperiment, IAnalysisService>();
			for (IAExperiment experiment : experiments.getExperiments().values())
			{
				Set<IAExperimentAusfuehrenService> rightServices = new HashSet<IAExperimentAusfuehrenService>();
				for (IAExperimentAusfuehrenService service : services)
				{
					if (service.supportedModels().contains(experiment.getModel().getType())) rightServices.add(service);
				}
				SortedSet<IAExperimentAusfuehrenService> sortedSet = (SortedSet<IAExperimentAusfuehrenService>) strategy.orderService(rightServices);
				alloMap.put(experiment, sortedSet.first());
			}
			experiments.setAllocation(alloMap);
		}
		
		return new Future(experiments);
	}


}
