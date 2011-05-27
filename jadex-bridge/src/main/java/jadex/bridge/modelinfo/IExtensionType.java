package jadex.bridge.modelinfo;

/**
 *  Interface for kernel extension models.
 */
public interface IExtensionType
{
	/**
	 *  Get the extension name.
	 */
	public String getName();
	
	/**
	 *  Get the instance class name.
	 */
	public String getClassName();
}
