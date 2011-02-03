package jadex.simulation.analysis.common.dataObjects;

public interface IAExperimentJob extends IADataObject
{

	public IAModel getModel();

	public void setModel(IAModel model);

	public IAExperimentalFrame getExperimentalFrame();

	public void setExperimentalFrame(IAExperimentalFrame experimentalFrame);
}
