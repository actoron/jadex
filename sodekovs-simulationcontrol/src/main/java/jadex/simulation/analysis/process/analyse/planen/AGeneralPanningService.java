package jadex.simulation.analysis.process.analyse.planen;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.superClasses.service.analysis.ASubProcessService;
import jadex.simulation.analysis.service.highLevel.IAGeneralPlanningService;

import java.util.UUID;

/**
 * GeneralPlanning Service Implementation
 * @author 5Haubeck
 *
 */
public class AGeneralPanningService extends ASubProcessService implements IAGeneralPlanningService
{
	public AGeneralPanningService(IExternalAccess access)
	{
		super(access, IAGeneralPlanningService.class);
	}

	@Override
	public IFuture plan(String session)
	{
		return startSubprocess(session, "AllgemeinPlanen", "jadex/simulation/analysis/process/analyse/planen/AllgemeinPlanen.bpmn", null);
	}
}
