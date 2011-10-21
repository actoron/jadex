package jadex.simulation.analysis.application.standalone;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.factories.AExperimentFactory;
import jadex.simulation.analysis.common.data.factories.AModelFactory;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.data.simulation.Modeltype;
import jadex.simulation.analysis.common.superClasses.service.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.dataBased.engineering.IAEngineerDataobjectService;

public class AEngineerDataobjectService extends ABasicAnalysisSessionService implements IAEngineerDataobjectService
{

	public AEngineerDataobjectService(IExternalAccess instance)
	{
		super(instance, IAEngineerDataobjectService.class, true);
	}

	@Override
	public IFuture createAModel(String name, Modeltype type)
	{
		return new Future(AModelFactory.createAModel(name, type));
	}

	@Override
	public IFuture createAModel(String name, Modeltype type, String path, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters)
	{
		return new Future(AModelFactory.createAModel(name, type,path,inputParameters,outputParameters));
	}

	@Override
	public IFuture createTestAModel()
	{
		return new Future(AModelFactory.createTestAModel());
	}

	@Override
	public IFuture createTestAModel(Modeltype type)
	{
		return new Future(AModelFactory.createTestAModel(type));
	}
	
	@Override
	public IFuture createDefaultExperiment(IAModel model)
	{
		return new Future(AExperimentFactory.createDefaultExperiment(model));
	}

	@Override
	public IFuture createExperiment(IAModel model, IAParameterEnsemble expParameters)
	{
		return new Future(AExperimentFactory.createExperiment(model, expParameters));
	}

	@Override
	public IFuture createExperiment(IAModel model, IAParameterEnsemble expParameters, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters)
	{
		return new Future(AExperimentFactory.createExperiment(model, expParameters, inputParameters, outputParameters));

	}

	@Override
	public IFuture createTestAExperiment()
	{
		return new Future(AExperimentFactory.createTestAExperiment());
	}

	@Override
	public IFuture createTestAExperiment(Modeltype type)
	{
		return new Future(AExperimentFactory.createTestAExperiment(type));
	}
}
