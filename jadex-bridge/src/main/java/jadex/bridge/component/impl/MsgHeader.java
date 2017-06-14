package jadex.bridge.component.impl;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.component.IMsgHeader;

/**
 *   Message header with message meta information.
 *
 */
public class MsgHeader implements IMsgHeader
{
	/** Map containing the link-level security properties. */
	protected Map<String, Object> linktolinkmap;
	
	/** Map containing the end-to-end-level security properties. */
	protected Map<String, Object> endtoendmap;
	
	/**
	 *  Creates the header.
	 */
	public MsgHeader()
	{
	}
	
	/**
	 *  Gets a property stored in the header.
	 *  
	 *  @param propertyname The name of the property.
	 *  @return Property value.
	 */
	public Object getProperty(String propertyname)
	{
		if (endtoendmap != null && endtoendmap.containsKey(propertyname))
			return endtoendmap.get(propertyname);
		
		if (linktolinkmap != null && linktolinkmap.containsKey(propertyname))
			return linktolinkmap.get(propertyname);
		
		return null;
	}
	
	/**
	 *  Gets a property stored in the end-to-end header.
	 *  
	 *  @param propertyname The name of the property.
	 *  @return Property value.
	 */
	public Object getEndToEndProperty(String propertyname)
	{
		Object ret = null;
		
		if (endtoendmap != null)
			ret = endtoendmap.get(propertyname);
		
		return ret;
	}

	/**
	 *  Gets the link-to-link map.
	 *
	 *  @return The link-to-link map.
	 */
	public Map<String, Object> getLinkToLinkMap()
	{
		return linktolinkmap;
	}

	/**
	 *  Sets the link-to-link map.
	 *
	 *  @param linktolinkmap The link-to-link map.
	 */
	public void setLinkToLinkMap(Map<String, Object> linktolinkmap)
	{
		this.linktolinkmap = linktolinkmap;
	}
	
	/**
	 *  Adds an openly-available and modifiable header property to the header.
	 *  
	 *  @param propname The property name.
	 *  @param propval The property value.
	 */
	public void addOpenProperty(String propname, Object propval)
	{
		if (linktolinkmap == null)
			linktolinkmap = new HashMap<String, Object>();
		linktolinkmap.put(propname, propval);
	}
	
	/**
	 *  Adds an openly-available header property to the header
	 *  that is protected against modification with regard to the receiver.
	 *  
	 *  @param propname The property name.
	 *  @param propval The property value.
	 */
	public void addProtectedProperty(String propname, Object propval)
	{
		if (linktolinkmap == null)
			linktolinkmap = new HashMap<String, Object>();
		linktolinkmap.put(propname, propval);
		
		if (endtoendmap == null)
			endtoendmap = new HashMap<String, Object>();
		endtoendmap.put(propname, propval);
	}
	
	/**
	 *  Adds a header property that is only visible by the receiver to the header.
	 *  
	 *  @param propname The property name.
	 *  @param propval The property value.
	 */
	public void addShadowProperty(String propname, Object propval)
	{
		if (endtoendmap == null)
			endtoendmap = new HashMap<String, Object>();
		endtoendmap.put(propname, propval);
	}
	
	/**
	 *  Removes the end-to-end map from the header.
	 *  
	 *  @return The end-to-end map.
	 */
	public Map<String, Object> removeEndToEndMap()
	{
		Map<String, Object> ret = endtoendmap;
		endtoendmap = null;
		return ret;
	}
	
	/**
	 *  Restores the end-to-end map to the header.
	 *  
	 *  @param endtoendmap The end-to-end map.
	 */
	public void restoreEndToEndMap(Map<String, Object> endtoendmap)
	{
		this.endtoendmap = endtoendmap;
	}
}
