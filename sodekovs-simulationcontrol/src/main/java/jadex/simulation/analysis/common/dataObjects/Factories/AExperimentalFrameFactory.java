package jadex.simulation.analysis.common.dataObjects.Factories;

import jadex.simulation.analysis.common.dataObjects.AExperimentalFrame;
import jadex.simulation.analysis.common.dataObjects.IAExperimentalFrame;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.parameter.AConstraintParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;

public class AExperimentalFrameFactory
{

	// test

	public static IAExperimentalFrame createTestAExperimentalFrame()
	{
		return createTestAExperimentalFrame("netLogo");
	}

	public static IAExperimentalFrame createTestAExperimentalFrame(String type)
	{
		IAModel testModel = AModelFactory.createTestAModel(type);

		return createDefaultExperimentalFrame(testModel);
	}

	// Default

	public static IAExperimentalFrame createDefaultExperimentalFrame(IAModel model)
	{
		IAParameterEnsemble expParameters = new AParameterEnsemble();
		expParameters.addParameter(new AConstraintParameter("replication", Integer.class, 1));
		expParameters.addParameter(new AConstraintParameter("visualisaiton", Boolean.class, Boolean.FALSE));

		return createExperimentalFrame(model, expParameters);
	}

	public static IAExperimentalFrame createExperimentalFrame(IAModel model, IAParameterEnsemble expParameters)
	{
		return createExperimentalFrame(model, expParameters, model.getInputParameters(), model.getOutputParameters());
	}

	public static IAExperimentalFrame createExperimentalFrame(IAModel model, IAParameterEnsemble expParameters, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters)
	{
		return new AExperimentalFrame(model, expParameters, inputParameters, outputParameters);
	}
}
