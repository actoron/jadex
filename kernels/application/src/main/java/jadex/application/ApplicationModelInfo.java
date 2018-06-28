package jadex.application;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.modelinfo.ModelInfo;
import jadex.commons.SUtil;

/**
 *  Representation of application models.
 */
public class ApplicationModelInfo extends ModelInfo
{
	/** The extensions. */
	protected List<Object> extensions;
	
	/**
	 *  Get the extension names. 
	 */
	public Object[] getExtensionTypes()
	{
		return extensions!=null? extensions.toArray(): new Object[0];
	}
	
	// written with small 'types' to exclude from xml 
	/**
	 *  Set the extension types.
	 */
	public void setExtensiontypes(Object[] extensions)
	{
		this.extensions = SUtil.arrayToList(extensions);
	}
	
	/**
	 *  Add a extension type.
	 *  @param extension The extension type.
	 */
	public void addExtensiontype(Object extension)
	{
		if(extensions==null)
			extensions = new ArrayList<Object>();
		extensions.add(extension);
	}
}
