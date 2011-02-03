package jadex.simulation.analysis.common.dataObjects;

public class AExperimentJob extends ABasicDataObject implements IAExperimentJob
{
	private IAModel model;
	private IAExperimentalFrame experimentalFrame;

	/**
	 * Creates a experiment job with given model and frame. Can be executed by a ExperiementService
	 * 
	 * @param model
	 *            the {@link IAModel}
	 * @param experimentalFrame
	 *            the {@link IAExperimentalFrame}
	 */
	public AExperimentJob(IAModel model, IAExperimentalFrame frame)
	{
		super();
		this.model = model;
		this.experimentalFrame = frame;
	}

	// ------ Interface IAExperimentJob -------

	// Model

	@Override
	public void setModel(IAModel model)
	{
		synchronized (mutex)
		{
			this.model = model;
		}
	}

	@Override
	public IAModel getModel()
	{
		return model;
	}

	// Frame

	@Override
	public IAExperimentalFrame getExperimentalFrame()
	{
		return experimentalFrame;
	}

	@Override
	public void setExperimentalFrame(IAExperimentalFrame experimentalFrame)
	{
		synchronized (mutex)
		{
			this.experimentalFrame = experimentalFrame;
		}
	}

}
