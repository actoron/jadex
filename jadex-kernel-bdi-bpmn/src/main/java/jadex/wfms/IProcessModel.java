package jadex.wfms;

import jadex.bridge.ILoadableElementModel;

/**
 *  Interface for process models.
 */
public interface IProcessModel extends ILoadableElementModel
{
	/**
	 *  Get the package name.
	 *  @return The package name.
	 */
	public String getPackage();
	
	/**
	 *  Get the lastmodified date.
	 *  @return The lastmodified date.
	 */
	public long getLastModified();
}
