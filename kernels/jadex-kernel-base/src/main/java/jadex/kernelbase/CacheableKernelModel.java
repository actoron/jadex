package jadex.kernelbase;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.ICacheableModel;

/**
 *  Base class for cacheable kernel model.
 */
public class CacheableKernelModel implements ICacheableModel
{
	//-------- attributes --------
	
	/** The last modified date. */
	protected long lastmodified;
	
	/** The last check date. */
	protected long lastchecked;

	/** The model info. */
	protected IModelInfo modelinfo;
	
	//-------- methods --------

	/**
	 *  Create a new model.
	 */
	public CacheableKernelModel(IModelInfo modelinfo)
	{
		this.modelinfo = modelinfo;
	}
	
	/**
	 *  Get the modelinfo.
	 *  @return the modelinfo.
	 */
	public IModelInfo getModelInfo()
	{
		return modelinfo;
	}
	
	/**
	 *  Get the lastmodified.
	 *  @return the lastmodified.
	 */
	public long getLastModified()
	{
		return lastmodified;
	}

	/**
	 *  Set the lastmodified.
	 *  @param lastmodified The lastmodified to set.
	 */
	public void setLastModified(long lastmodified)
	{
		this.lastmodified = lastmodified;
	}

	/**
	 *  Get the lastchecked.
	 *  @return the lastchecked.
	 */
	public long getLastChecked()
	{
		return lastchecked;
	}

	/**
	 *  Set the lastchecked.
	 *  @param lastchecked The lastchecked to set.
	 */
	public void setLastChecked(long lastchecked)
	{
		this.lastchecked = lastchecked;
	}
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return modelinfo.getFilename();
	}
}
