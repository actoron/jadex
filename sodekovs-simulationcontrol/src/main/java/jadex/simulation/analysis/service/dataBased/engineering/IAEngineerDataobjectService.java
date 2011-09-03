package jadex.simulation.analysis.service.dataBased.engineering;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.ADataObject;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.data.simulation.Modeltype;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

/**
 * Service to create a data object (like the factories)
 * 
 * @author 5Haubeck
 */
public interface IAEngineerDataobjectService extends IAnalysisSessionService
{
	// factory methods as service
	public IFuture createTestAModel();

	public IFuture createTestAModel(Modeltype type);

	public IFuture createAModel(String name, Modeltype type);

	public IFuture createAModel(String name, Modeltype type, String path, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters);

	public IFuture createTestAExperiment();

	public IFuture createTestAExperiment(Modeltype type);

	public IFuture createDefaultExperiment(IAModel model);

	public IFuture createExperiment(IAModel model, IAParameterEnsemble expParameters);

	public IFuture createExperiment(IAModel model, IAParameterEnsemble expParameters, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters);

}
