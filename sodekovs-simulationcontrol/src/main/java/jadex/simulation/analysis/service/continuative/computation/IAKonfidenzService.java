package jadex.simulation.analysis.service.continuative.computation;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.IAMultiValueParameter;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;

public interface IAKonfidenzService extends IAnalysisSessionService
{
	public IFuture computeTTest(IAMultiValueParameter parameter, Double intervallPercent);

	public IFuture testTTest(IAMultiValueParameter parameter, Double alpha, Double intervallPercent);
}
