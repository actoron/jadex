package jadex.simulation.analysis.service.simulation;

import java.util.Set;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;

public interface ISimulationService  extends IAnalysisSessionService
{
	/**
	 * Return the model which the service support
	 * @return Set<Modeltype>
	 */
	public Set<Modeltype> supportedModels();
}
