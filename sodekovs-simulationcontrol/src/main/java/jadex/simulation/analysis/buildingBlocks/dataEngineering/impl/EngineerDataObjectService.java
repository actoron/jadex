package jadex.simulation.analysis.buildingBlocks.dataEngineering.impl;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.ExternalAccess;
import jadex.simulation.analysis.buildingBlocks.dataEngineering.IEngineerDataObjectService;
import jadex.simulation.analysis.common.dataObjects.ADataObject;
import jadex.simulation.analysis.common.dataObjects.IADataView;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.factories.ADataViewFactory;
import jadex.simulation.analysis.common.dataObjects.factories.AExperimentFactory;
import jadex.simulation.analysis.common.dataObjects.factories.AModelFactory;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.services.ABasicAnalysisSessionService;

import java.util.Map;
import java.util.UUID;

public class EngineerDataObjectService extends ABasicAnalysisSessionService implements IEngineerDataObjectService
{
	private IExternalAccess instance;

	public EngineerDataObjectService(ExternalAccess instance)
	{
		// TODO: Hack??? No Micro Access?
		super(instance, IEngineerDataObjectService.class);
		this.instance = instance;
		Map prop = getPropertyMap();
		prop.put(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, "jadex.simulation.analysis.common.services.defaultView.DefaultServiceViewerPanel");
		setPropertyMap(prop);
	}

	@Override
	public IFuture engineerGuiDataObject(UUID sessionId, ADataObject dataObject)
	{
		IFuture res = new Future();
		
		IEngineerDataObjectService service = (IEngineerDataObjectService) SServiceProvider.getServices(instance.getServiceProvider(), IEngineerDataObjectService.class,  RequiredServiceInfo.SCOPE_GLOBAL).get(new ThreadSuspendable(this));
		dataObject.setEditable(true);
		IADataView view = ADataViewFactory.createView(dataObject);

		sessionViews.put(sessionId, view.getComponent());
		return res;
	}

	@Override
	public IFuture createAModel(String name, String type)
	{
		return new Future(AModelFactory.createAModel(name, type));
	}

	@Override
	public IFuture createAModel(String name, String type, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters)
	{
		return new Future(AModelFactory.createAModel(name, type,inputParameters,outputParameters));
	}

	@Override
	public IFuture createTestAModel()
	{
		return new Future(AModelFactory.createTestAModel());
	}

	@Override
	public IFuture createTestAModel(String type)
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
	public IFuture createTestAExperiment(String type)
	{
		return new Future(AExperimentFactory.createTestAExperiment(type));
	}
}
