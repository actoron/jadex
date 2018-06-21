package jadex.application;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.commons.SUtil;

/**
 *  Configuration with extensions.
 */
public class ApplicationConfigurationInfo extends ConfigurationInfo
{
	/** The list of extensions. */
	protected List<IExtensionInfo> extensions;
	
	/**
	 *  Get the extension names. 
	 */
	public IExtensionInfo[] getExtensions()
	{
		return extensions!=null? extensions.toArray(new IExtensionInfo[extensions.size()]): new IExtensionInfo[0];
	}
	
	/**
	 *  Set the extension types.
	 */
	public void setExtensions(IExtensionInfo[] extensions)
	{
		this.extensions = SUtil.arrayToList(extensions);
	}
	
	/**
	 *  Add a extension type.
	 *  @param extension The extension type.
	 */
	public void addExtension(IExtensionInfo extension)
	{
		if(extensions==null)
			extensions = new ArrayList<IExtensionInfo>();
		extensions.add(extension);
	}
}
