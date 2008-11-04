package jadex.rules.state.io.xml;

import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  An object holding information needed for building
 *  an XML mapping.
 */
public class OAVXMLHelper
{
	//-------- attributes -------
	
	/** The expansions (attribute -> list of expansions). */
	protected Map	expansions	= new HashMap();
	
	//-------- methods --------
	
	/**
	 *  Add an expansion.
	 *  @param attriubte	The OAV attribute type.
	 *  @param path	The XML path fragment corresponding to the attribute.
	 */
	public void	addExpansion(OAVAttributeType attribute, String path)
	{
		addExpansion(attribute, path, null);
	}
	
	/**
	 *  Add an expansion.
	 *  @param attribute	The OAV attribute type.
	 *  @param path	The XML path fragment corresponding to the attribute.
	 *  @param type	The concrete OAV object type corresponding to the value of the attribute at the XML location (only needed, when differing from attribute type).
	 */
	public void	addExpansion(OAVAttributeType attribute, String path, OAVObjectType type)
	{
		List	list	= (List)expansions.get(attribute);
		if(list==null)
		{
			list	= new ArrayList();
			expansions.put(attribute, list);
		}

		Expansion	exp	= new Expansion();
		exp.path	= path;
		exp.type	= type!=null ? type : attribute.getType();
		list.add(exp);
	}
	
	/**
	 *  Ignore the given attribute.
	 */
	public void addIgnoreAttribute(OAVAttributeType attribute)
	{
		if(expansions.containsKey(attribute))
			throw new RuntimeException("Mapping for ignored attribute!?");
		
		expansions.put(attribute, Collections.EMPTY_LIST);
	}

	//-------- helper classes --------
	
	/**
	 *  An expansion defines how an attribute is represented in XML.
	 */
	protected static class	Expansion
	{
		/** The path fragment. */
		protected String path;
		
		/** The OAV object type. */
		protected OAVObjectType type;
	}
}
