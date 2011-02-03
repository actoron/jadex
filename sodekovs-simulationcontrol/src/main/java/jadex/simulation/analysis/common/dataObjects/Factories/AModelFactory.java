package jadex.simulation.analysis.common.dataObjects.Factories;

import jadex.simulation.analysis.common.dataObjects.AModel;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.parameter.AConstraintParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;

public class AModelFactory
{
	// Test

	public static IAModel createTestAModel()
	{
		return createTestAModel("netLogo");
	}

	public static IAModel createTestAModel(String type)
	{
		String modelName = null;
		IAParameterEnsemble inputParameters = new AParameterEnsemble();
		IAParameterEnsemble outputParameters = new AParameterEnsemble();

		if (type.equals("netLogo"))
		{
			modelName = "AntsStop";
			inputParameters.addParameter(new AConstraintParameter("population", Double.class, 100d));
			inputParameters.addParameter(new AConstraintParameter("diffusion-rate", Double.class, 40d));
			inputParameters.addParameter(new AConstraintParameter("evaporation-rate", Double.class, 10d));
			outputParameters.addParameter(new AConstraintParameter("ticks", Double.class));
		}
		else if (type.equals("desmoJ"))
		{
			modelName = "VancarrierModel";
			inputParameters.addParameter(new AConstraintParameter("vcNumber", Integer.class, 2));
			outputParameters.addParameter(new AConstraintParameter("ticks", String.class));
		}
		else
		{
			new RuntimeException(type + " model not supported");
		}

		return createAModel(modelName, type, inputParameters, outputParameters);
	}

	// Default

	public static IAModel createAModel(String name, String type)
	{
		return createAModel(name, type, new AParameterEnsemble(), new AParameterEnsemble());
	}

	public static IAModel createAModel(String name, String type, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters)
	{
		return new AModel(name, type, inputParameters, outputParameters);
	}
}
