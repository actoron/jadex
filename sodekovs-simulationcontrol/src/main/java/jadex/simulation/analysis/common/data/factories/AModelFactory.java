package jadex.simulation.analysis.common.data.factories;

import jadex.simulation.analysis.common.data.AModel;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.parameter.ABasicParameter;
import jadex.simulation.analysis.common.data.parameter.AConstraintParameter;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.ASummaryParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.data.simulation.Modeltype;

/**
 * Factoryclass for IAModel
 * @author 5Haubeck
 *
 */
public class AModelFactory
{
	// Test
	public static IAModel createTestAModel()
	{
		return createTestAModel(Modeltype.DesmoJ);
	}

	public static IAModel createTestAModel(Modeltype type)
	{
		String modelName = null;
		String path = "";
		IAParameterEnsemble inputParameters = new AParameterEnsemble("Input Parameter");
		IAParameterEnsemble outputParameters = new AParameterEnsemble("Output Parameter");

		if (type == Modeltype.NetLogo)
		{
			modelName = "AntsStop5";
			inputParameters.addParameter(new ABasicParameter("population", Double.class, 100.0));
			inputParameters.addParameter(new ABasicParameter("diffusion-rate",Double.class, 40.0));
			inputParameters.addParameter(new ABasicParameter("evaporation-rate",Double.class, 10.0));
			outputParameters.addParameter(new ASummaryParameter("ticks"));
			path = "/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/application/netLogo/models/";
		}
		else if (type == Modeltype.DesmoJ)
		{
			modelName = "VancarrierModel";
			inputParameters.addParameter(new AConstraintParameter("vcNumber", 2.0));
			inputParameters.addParameter(new ABasicParameter("SimTime", Double.class, 15000.0));
			outputParameters.addParameter(new ASummaryParameter("Truck Wait Times"));
			path = "/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/application/desmoJ/models/";
		} else if (type == Modeltype.Jadex)
		{
			modelName = "DisasterManagement";
			inputParameters.addParameter(new ABasicParameter("firePositionx",Double.class, 0.5));
			inputParameters.addParameter(new ABasicParameter("firePositiony", Double.class, 0.5));
			outputParameters.addParameter(new ASummaryParameter("Chemicals"));
			outputParameters.addParameter(new ASummaryParameter("Fire"));
			outputParameters.addParameter(new ASummaryParameter("Victims"));
			path = "/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/application/Jadex/model/disastermanagement/DisasterManagement.application.xml";
		}
		else
		{
			new RuntimeException(type + " model not supported");
		}

		return createAModel(modelName, type, path, inputParameters, outputParameters);
	}

	// Default
	public static IAModel createAModel(String name, Modeltype type)
	{
		String path = "";
		if (type == Modeltype.NetLogo)
		{
			path = "/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/application/netLogo/models/";
		}
		else if (type == Modeltype.DesmoJ)
		{
			path = "/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/application/desmoJ/models/";
		}
		return createAModel(name, type, path, new AParameterEnsemble("Input Parameter"), new AParameterEnsemble("Output Parameter"));
	}

	public static IAModel createAModel(String name, Modeltype type, String path, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters)
	{
		return new AModel(name, type, path, inputParameters, outputParameters);
	}
}
