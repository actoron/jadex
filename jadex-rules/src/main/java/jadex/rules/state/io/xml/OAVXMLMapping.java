package jadex.rules.state.io.xml;

import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.io.xml.OAVXMLHelper.Expansion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  An object describing a mapping between XML elements
 *  and OAV types and attributes.
 */
public class OAVXMLMapping implements IOAVXMLMapping
{
	//-------- attributes --------
	
	/** The object types. */
	protected Map	objecttypes;
	
	/** The attribute types. */
	protected Map	attributetypes;
	
	/** The content attribute types. */
	protected Map	contentattributes;
	
	/** The value converters. */
	protected Map	converters;
	
	/** The type prefix. */
	protected String	typeprefix;
	
	/** The expansions. */
	protected OAVXMLHelper	expansions;
	
	//-------- constructors --------
	
	/**
	 *  Create an XML mapping.
	 *  @param roottypes	The OAV object types allowed as XML root elements.
	 *  @param expansions	Mapping from attribute type to XML prefix (e.g. capability_has_imports to "imports/import").
	 *  @param converters	Mapping for value converters (attribute or object type to IValueConverter).
	 *  @param typeprefix	A prefix to be stripped from OAV type names for the XML mapping.
	 */
	public OAVXMLMapping(Set roottypes, OAVXMLHelper expansions, Map converters, String typeprefix)
	{
		this.expansions	= expansions;
		this.objecttypes	= new HashMap();
		this.attributetypes	= new HashMap();
		this.contentattributes	= new HashMap();
		this.converters	= new HashMap();
		this.converters.putAll(converters);
		this.typeprefix	= typeprefix;
		for(Iterator it=roottypes.iterator(); it.hasNext(); )
			buildMapping(null, (OAVObjectType)it.next(), expansions);
	}
	
	//-------- IOAVXMLMapping interface --------
	
	/**
	 *  Get the OAV object type corresponding
	 *  to an XML path.
	 *  @param path	The XML path (element/attribute names separated by "/").
	 *  @return The corresponding OAV object type;
	 */
	public OAVObjectType	getObjectType(String path)
	{
		return (OAVObjectType)objecttypes.get(path);
	}

	/**
	 *  Get the OAV attribute type corresponding
	 *  to an XML path. The attribute type defines the attribute
	 *  of the parent object, to which the object
	 *  corresponding to the XML path is connected.  
	 *  @param path	The XML path (element/attribute names separated by "/").
	 *  @return The corresponding OAV attribute type;
	 */
	public OAVAttributeType	getAttributeType(String path)
	{
		return (OAVAttributeType)attributetypes.get(path);
	}

	/**
	 *  Get the OAV attribute type corresponding
	 *  to the content of an XML element.  
	 *  @param path	The XML path (element names separated by "/").
	 *  @return The corresponding OAV attribute type;
	 */
	public OAVAttributeType	getContentAttributeType(String path)
	{
		return (OAVAttributeType)contentattributes.get(path);
	}
	
	/**
	 *  Get the OAV attribute type corresponding
	 *  to the content of an XML element.  
	 *  @param type	The OAV object type to which the content should be added.
	 *  @return The corresponding OAV attribute type;
	 */
	public OAVAttributeType getContentAttributeType(OAVObjectType type)
	{
		OAVAttributeType	ret	= null;
		while(ret==null && type!=null)
		{
			ret	= (OAVAttributeType)contentattributes.get(type);
			type	= type.getSupertype();
		}
		return ret;
	}

	/**
	 *  Get the value converter for an attribute type.  
	 *  @param type	The OAV attribute type.
	 *  @return The corresponding value converter;
	 */
	public IValueConverter	getValueConverter(OAVAttributeType type)
	{
		IValueConverter	ret	= (IValueConverter)converters.get(type);
		if(ret==null)
			ret	= (IValueConverter)converters.get(type.getType());
		return ret;
	}
	
	/**
	 *  Check if an XML path is ignored in the OAV state.
	 *  @param path	The path to check.
	 */
	public boolean	isIgnored(String path)
	{
		// ignored == contained but mapped to null.
		return objecttypes.containsKey(path) && objecttypes.get(path)==null;
	}
	
	//-------- methods --------
	
	/**
	 *  Ignore a path.
	 *  @param path	The XML path.
	 */
	public void	ignorePath(String path)
	{
		objecttypes.put(path, null);
	}
	
	/**
	 *  Add a custom element mapping.
	 *  @param path	The XML path.
	 *  @param type	The OAV object type.
	 *  @param attr	The OAV attribute type.
	 */
	public void	addElementMapping(String path, OAVObjectType type, OAVAttributeType attr)
	{
//		System.out.println("Mapping "+path+" to "+attr+":"+type);
		objecttypes.put(path, type);
		attributetypes.put(path, attr);
		buildMapping(path, type, expansions);
	}
	
	/**
	 *  Add a custom content mapping.
	 *  @param path	The XML path.
	 *  @param attr	The OAV attribute type.
	 */
	public void	addContentMapping(String path, OAVAttributeType attr)
	{
//		System.out.println("Mapping content of "+path+" to "+attr);
		if(!objecttypes.containsKey(path))	// Avoid destroying existing element mapping.
		{
			objecttypes.put(path, null);
			attributetypes.put(path, attr);
		}
		contentattributes.put(path, attr);
	}
	
	/**
	 *  Add a custom content mapping.
	 *  @param type	The OAV object type.
	 *  @param attr	The OAV attribute type.
	 */
	public void	addContentMapping(OAVObjectType type, OAVAttributeType attr)
	{
		contentattributes.put(type, attr);
	}
	
	//-------- helper methods --------

	/**
	 *  Recursively build the mapping table.
	 */
	protected void	buildMapping(String path, OAVObjectType type, OAVXMLHelper expansions)
	{
    	if(path==null)
		{
    		String	typename	= type.getName();
    		if(typename.startsWith(typeprefix))
    			typename	= typename.substring(typeprefix.length());
			path	= typename;
			objecttypes.put(path, type);
//			System.out.println("Mapping "+path+" to "+type);
		}
		
    	if(type.getSupertype()!=null)
			buildMapping(path, type.getSupertype(), expansions);

		for(Iterator it=type.getDeclaredAttributeTypes().iterator(); it.hasNext();)
		{
			OAVAttributeType	attr	= (OAVAttributeType)it.next();
			OAVObjectType	attrtype	= attr.getType();

			String	attrpath;
			List	exps	= (List)expansions.expansions.get(attr);
			if(exps!=null)
			{
				for(int i=0; i<exps.size(); i++)
				{
					Expansion	exp	= (Expansion)exps.get(i);
					attrpath	= exp.path.equals("") ? path : path+"/"+exp.path;
					attrtype	= exp.type;
					// Map skipped XML elements to null.
					// E.g. if mapping is "agent/agent_has_beliefs" to "agent/beliefs/belief",
					// prefix "agent/beliefs" can be ignored which is indicated by a null mapping.
					String	prefix	= attrpath.substring(0, attrpath.lastIndexOf('/'));
					if(!objecttypes.containsKey(prefix) && prefix.startsWith(path) && !prefix.equals(path))
					{
						objecttypes.put(prefix, null);
					}
	
					// Java types use content mapping instead of element mapping.
					if(attr.getType() instanceof OAVJavaType)
					{
						addContentMapping(attrpath, attr);
					}
					else
					{
						addElementMapping(attrpath, attrtype, attr);
					}
				}
			}
			else
			{
				String name	= attr.getName();
//				if(name.startsWith(typeprefix))
//					name	= name.substring(typeprefix.length());
				String	typename	= type.getName();
				String	prefix	= typename+"_has_";
				if(name.startsWith(prefix))
					name	= name.substring(prefix.length());
				if(!OAVAttributeType.NONE.equals(attr.getMultiplicity()) && name.endsWith("s"))
					name	= name.substring(0, name.length()-1);
				attrpath	= path+"/"+name;

				// Java types use content mapping instead of element mapping.
				if(attr.getType() instanceof OAVJavaType)
				{
					addContentMapping(attrpath, attr);
				}
				else
				{
					addElementMapping(attrpath, attrtype, attr);
				}
			}
		}
	}
}
