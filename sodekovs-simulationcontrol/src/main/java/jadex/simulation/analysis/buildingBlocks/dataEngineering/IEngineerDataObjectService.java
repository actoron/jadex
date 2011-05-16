package jadex.simulation.analysis.buildingBlocks.dataEngineering;

import java.util.UUID;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.dataObjects.ADataObject;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.services.IAnalysisSessionService;

public interface IEngineerDataObjectService extends IAnalysisSessionService
{
	public IFuture engineerGuiDataObject(UUID sessionId, ADataObject dataObject);
	
	//factory methods as service
	public IFuture createTestAModel();
	public IFuture createTestAModel(String type);
	public IFuture createAModel(String name, String type);
	public IFuture createAModel(String name, String type, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters);
	
	public IFuture createTestAExperiment();
	public IFuture createTestAExperiment(String type);
	public IFuture createDefaultExperiment(IAModel model);
	public IFuture createExperiment(IAModel model, IAParameterEnsemble expParameters);
	public IFuture createExperiment(IAModel model, IAParameterEnsemble expParameters, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters);

}
