package jadex.simulation.analysis.common.data.factories;

import jadex.simulation.analysis.common.data.AExperiment;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.parameter.ABasicParameter;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.data.simulation.Modeltype;

/**
 * Factoryclass for IAExperiment
 * @author 5Haubeck
 *
 */
public class AExperimentFactory
{

	// test
	public static IAExperiment createTestAExperiment()
	{
		return createTestAExperiment(Modeltype.DesmoJ);
	}

	public static IAExperiment createTestAExperiment(Modeltype type)
	{
		IAModel testModel = AModelFactory.createTestAModel(type);

		return createDefaultExperiment(testModel);
	}

	// Default
	public static IAExperiment createDefaultExperiment(IAModel model)
	{
		IAParameterEnsemble expParameters = new AParameterEnsemble("Experiment Parameter");
		expParameters.addParameter(new ABasicParameter("Wiederholungen", Integer.class, 3));
		expParameters.addParameter(new ABasicParameter("Visualisierung", Boolean.class, Boolean.TRUE));
		expParameters.addParameter(new ABasicParameter("Mittelwert Prozent", Double.class, 10.0));
		expParameters.addParameter(new ABasicParameter("alpha", Double.class, 95.0));

		return createExperiment(model, expParameters);
	}
	
	public static IAExperiment createExperiment(IAModel model, IAParameterEnsemble expParameters)
	{
		return createExperiment(model, expParameters, ((AParameterEnsemble) model.getInputParameters().clonen()),  ((AParameterEnsemble) model.getOutputParameters().clonen()));
	}

	public static IAExperiment createExperiment(IAModel model, IAParameterEnsemble expParameters, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters)
	{
		return new AExperiment("Default Experiment", model, expParameters, inputParameters, outputParameters);
	}
}
