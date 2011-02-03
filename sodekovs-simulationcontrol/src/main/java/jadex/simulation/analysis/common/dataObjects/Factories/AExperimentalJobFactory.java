package jadex.simulation.analysis.common.dataObjects.Factories;

import jadex.simulation.analysis.common.dataObjects.AExperimentJob;
import jadex.simulation.analysis.common.dataObjects.IAExperimentJob;
import jadex.simulation.analysis.common.dataObjects.IAExperimentalFrame;
import jadex.simulation.analysis.common.dataObjects.IAModel;

public class AExperimentalJobFactory
{
	// Test

	public static IAExperimentJob createTestAExperimentalJob()
	{
		return createTestAExperimentalJob("netLogo");
	}

	public static IAExperimentJob createTestAExperimentalJob(String type)
	{
		IAModel testModel = AModelFactory.createTestAModel(type);

		return createAExperimentalJob(testModel);
	}

	// Default

	public static IAExperimentJob createAExperimentalJob(IAExperimentalFrame frame)
	{
		return createAExperimentalJob(frame.getModel(), frame);
	}

	public static IAExperimentJob createAExperimentalJob(IAModel model)
	{
		return createAExperimentalJob(model, AExperimentalFrameFactory.createDefaultExperimentalFrame(model));
	}

	public static IAExperimentJob createAExperimentalJob(IAModel model, IAExperimentalFrame frame)
	{
		return new AExperimentJob(model, frame);
	}
}
