package jadex.simulation.analysis.process.validation;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.service.AServiceEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.ASubProcessService;
import jadex.simulation.analysis.common.superClasses.service.view.session.subprocess.ASubProcessView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.highLevel.IAValidationProcessService;

import java.util.UUID;

public class AValidationProcessService extends ASubProcessService implements IAValidationProcessService
{
	public AValidationProcessService(IExternalAccess access)
	{
		super(access, IAValidationProcessService.class);
	}
	
	@Override
	public IFuture validate(String session)
	{
		return startSubprocess(session, "Validation", "jadex/simulation/analysis/process/validation/Validation.bpmn", null);
	}
}
