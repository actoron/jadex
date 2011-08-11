package jadex.simulation.analysis.service.highLevel;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;

public interface IAAllgemeinErgebnisAnalysierenService extends IAnalysisSessionService
{
	public IFuture auswerten(IAExperiment experiment);
	
	public IFuture visualisieren(IAExperiment experiment);

}
