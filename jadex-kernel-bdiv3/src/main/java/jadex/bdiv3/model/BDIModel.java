package jadex.bdiv3.model;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.micro.MicroModel;

/**
 * 
 */
public class BDIModel extends MicroModel
{
	protected MCapability mcapa;

	/**
	 *  Create a new model.
	 */
	public BDIModel(IModelInfo modelinfo, MCapability mcapa)
	{
		super(modelinfo);
		this.mcapa = mcapa;
	}

	/**
	 *  Get the mcapa.
	 *  @return The mcapa.
	 */
	public MCapability getCapability()
	{
		return mcapa;
	}

	/**
	 *  Set the mcapa.
	 *  @param mcapa The mcapa to set.
	 */
	public void setCapability(MCapability mcapa)
	{
		this.mcapa = mcapa;
	}
}
