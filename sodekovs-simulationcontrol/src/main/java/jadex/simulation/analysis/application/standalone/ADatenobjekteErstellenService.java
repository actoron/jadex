package jadex.simulation.analysis.application.standalone;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.ADataObject;
import jadex.simulation.analysis.common.data.IADataView;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.factories.AExperimentFactory;
import jadex.simulation.analysis.common.data.factories.AModelFactory;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.service.basic.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.basic.view.session.IASessionView;
import jadex.simulation.analysis.service.dataBased.engineering.IADatenobjekteErstellenService;
import jadex.simulation.analysis.service.simulation.Modeltype;

import java.util.UUID;

public class ADatenobjekteErstellenService extends ABasicAnalysisSessionService implements IADatenobjekteErstellenService
{

	public ADatenobjekteErstellenService(IExternalAccess instance)
	{
		super(instance, IADatenobjekteErstellenService.class, true);
	}

	@Override
	public IFuture engineerGuiDataObject(UUID sessionId, ADataObject dataObject)
	{
		IFuture res = new Future();
		
//		IADatenobjekteErstellenService service = (IADatenobjekteErstellenService) SServiceProvider.getServices(instance.getServiceProvider(), IADatenobjekteErstellenService.class,  RequiredServiceInfo.SCOPE_GLOBAL).get(new ThreadSuspendable(this));
		dataObject.setEditable(true);
		IADataView view =dataObject.getView();

		sessionViews.put(sessionId, (IASessionView) view.getComponent());
		return res;
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
