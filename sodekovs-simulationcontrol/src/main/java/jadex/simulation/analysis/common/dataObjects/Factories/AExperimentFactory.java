package jadex.simulation.analysis.common.dataObjects.Factories;

import jadex.simulation.analysis.common.dataObjects.AExperiment;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.parameter.AConstraintParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;

public class AExperimentFactory
{

	// test

	public static IAExperiment createTestAExperiment()
	{
		return createTestAExperiment("netLogo");
	}

	public static IAExperiment createTestAExperiment(String type)
	{
		IAModel testModel = AModelFactory.createTestAModel(type);

		return createDefaultExperiment(testModel);
	}

	// Default

	public static IAExperiment createDefaultExperiment(IAModel model)
	{
		IAParameterEnsemble expParameters = new AParameterEnsemble();
		expParameters.addParameter(new AConstraintParameter("replication", Integer.class, 1));
		expParameters.addParameter(new AConstraintParameter("visualisation", Boolean.class, Boolean.FALSE));

		return createExperiment(model, expParameters);
	}

	public static IAExperiment createExperiment(IAModel model, IAParameterEnsemble expParameters)
	{
		return createExperiment(model, expParameters, model.getInputParameters(), model.getOutputParameters());
	}

	public static IAExperiment createExperiment(IAModel model, IAParameterEnsemble expParameters, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters)
	{
		return new AExperiment(model, expParameters, inputParameters, outputParameters);
	}
}
