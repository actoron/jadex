package jadex.wfms;

import jadex.bridge.ILoadableComponentModel;

/**
 *  Interface for process models.
 */
public interface IProcessModel extends ILoadableComponentModel
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
