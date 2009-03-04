package jadex.rules.state.io.xml;

import jadex.commons.xml.BasicTypeConverter;
import jadex.commons.xml.IObjectHandler;
import jadex.commons.xml.StackElement;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.stream.XMLStreamReader;

/**
 *  Handler for reading XML into OAV objects.
 */
public class OAVObjectHandler implements IObjectHandler
{
	//-------- static part --------
	
	/** The debug flag. */
	public static boolean DEBUG = false;
	
	//-------- attributes --------
	
	/** The type mappings. */
	protected Map typeinfos;
	
	/** The link object infos. */
	protected Map linkinfos;
	
	/** The ignored attribute types. */
	protected Set ignoredattrs;
	
	//-------- constructors --------

	/**
	 *  Create a new bean object handler.
	 */
	public OAVObjectHandler(Set typeinfos, Set linkinfos, Set ignoredattrs)
	{
		this.typeinfos = typeinfos!=null? createTypeInfos(typeinfos): Collections.EMPTY_MAP;
		this.linkinfos = linkinfos!=null? createLinkInfos(linkinfos): Collections.EMPTY_MAP;
		this.ignoredattrs = ignoredattrs!=null? ignoredattrs: Collections.EMPTY_SET;
	}
	
	//-------- methods --------
	
	/**
	 *  Create an object for the current tag.
	 *  @param parser The parser.
	 *  @param comment The preceding xml comment.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(XMLStreamReader parser, String comment, Object context, List stack) throws Exception
	{
		Object ret = null;
		IOAVState state = (IOAVState)context;
		
		String fullpath = getXMLPath(stack)+"/"+parser.getLocalName();
		OAVMappingInfo mapinfo = getMappingInfo(parser.getLocalName(), fullpath);
		
		if(mapinfo!=null)
		{
			Object	object	= null;
			OAVObjectType type = mapinfo.getType();
			
			if(type instanceof OAVJavaType && BasicTypeConverter.isBuiltInType(((OAVJavaType)type).getClazz()))
			{
				String strval;
				if(parser.getAttributeCount()==1)
				{	
					strval = parser.getAttributeValue(0);
					ret = BasicTypeConverter.convertBuiltInTypes(((OAVJavaType)type).getClazz(), strval);
				}
//				else 
//				{	
//					strval = parser.getElementText();
//				}
			}
			else
			{
				boolean root = stack.size()==0;
				
				if(root)
				{
					object	= state.createRootObject(type);
					ret	= object;
				}
				else if(type!=null)
				{
					object	= state.createObject(type);
					ret	= object;
				}
				else	// If no type use last element from stack to map attributes.
				{
					int	i	= stack.size()-1;
					while(i>=0 && object==null)
						object	= ((StackElement)stack.get(i)).getObject();
					
					if(object==null)
						throw new RuntimeException("No element on stack for "+mapinfo);
						
				}
				
				// Handle attributes
				for(int i=0; i<parser.getAttributeCount(); i++)
				{
					String attrname = parser.getAttributeLocalName(i);
					
					if(!ignoredattrs.contains(attrname))
					{
						String attrval = parser.getAttributeValue(i);
						
						OAVAttributeType attrtype = mapinfo.getAttributeType(attrname);
						
						// Search attribute in type and supertypes.
						OAVObjectType tmptype = state.getType(object);
						while(attrtype==null && tmptype!=null)
						{
							String tmpnamesin = tmptype.getName()+"_has_"+attrname;
							
							attrtype = tmptype.getDeclaredAttributeType0(tmpnamesin);
							
							if(attrtype==null)
								tmptype = tmptype.getSupertype();
						}
						
						if(attrtype!=null)
						{
							Object arg = attrtype.getType() instanceof OAVJavaType?
								BasicTypeConverter.convertBuiltInTypes(((OAVJavaType)attrtype.getType()).getClazz(), attrval): attrval;
					
							if(attrtype.getMultiplicity().equals(OAVAttributeType.NONE))
							{
								state.setAttributeValue(object, attrtype, arg);
							}
							else
							{
								state.addAttributeValue(object, attrtype, arg);
							}
						}
						else
						{
							if(DEBUG)
								System.out.println("Could not find attribute: "+attrname);
						}
					}
				}
				
				// If comment method name is set, set the comment.
				if(comment!=null)
				{
					OAVAttributeType comattr = mapinfo.getComment();
					if(comattr!=null)
					{
						state.setAttributeValue(object, comattr, comment);
					}
				}
			}
		}
		else
		{
			if(DEBUG)
				System.out.println("No mapping found: "+parser.getLocalName());
		}
		
		return ret;
	}
	
	/**
	 *  Handle content for an object.
	 *  @param parser The parser.
	 *  @param comment The comment.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public void handleContent(XMLStreamReader parser, Object elem, String content, Object context, List stack) throws Exception
	{
		IOAVState state = (IOAVState)context;
		String fullpath = getXMLPath(stack);
		OAVMappingInfo mapinfo = getMappingInfo(parser.getLocalName(), fullpath);
		
		if(mapinfo==null)
			throw new RuntimeException("No information for handling content: "+parser.getLocalName());

		OAVAttributeType attrtype = mapinfo.getContent();
		
		if(attrtype==null)
			throw new RuntimeException("No content mapping: "+parser.getLocalName());
		
		setAttributeValue(state, elem, attrtype, content);
	}
	
	/**
	 *  Link an object to its parent.
	 *  @param parser The parser.
	 *  @param elem The element.
	 *  @param paranet The parent element.
	 */
	public void linkObject(XMLStreamReader parser, Object elem, Object parent, Object context, List stack) throws Exception
	{
		IOAVState state = (IOAVState)context;
		
		// Call post-processor if any.
		String fullpath = getXMLPath(stack);
		OAVMappingInfo mapinfo = getMappingInfo(parser.getLocalName(), fullpath);
		if(mapinfo!=null && mapinfo.getPostProcessor()!=null)
		{
			mapinfo.getPostProcessor().postProcess(state, elem, ((StackElement)stack.get(0)).getObject());
		}

		// Find attribute where to set/add the child element.
		
		boolean linked = false;
		
		OAVAttributeType attrtype = null;
		OAVLinkInfo	linkinfo = getLinkInfo(parser.getLocalName(), fullpath);
		if(linkinfo!=null)
			attrtype	= linkinfo.getLinkAttribute();

		if(attrtype!=null)
		{
			setAttributeValue(state, parent, attrtype, elem);
			linked= true;
		}
		
		if(!linked)
		{
			linked = internalLinkObjects(parser.getLocalName(), elem, parent, state);
		}
		
		if(!linked && !(state.getType(elem) instanceof OAVJavaType 
			&& BasicTypeConverter.isBuiltInType(((OAVJavaType)state.getType(elem)).getClazz())))
		{
			linked = internalLinkObjects(state.getType(elem).getName(), elem, parent, state);	
		}	
		
		if(!linked)
			throw new RuntimeException("Could not link: "+elem+" "+parent+" "+getXMLPath(stack));
	}
	
	/**
	 *  Internal method for linking objects.
	 */
	protected boolean internalLinkObjects(String attrname, Object elem, Object parent, IOAVState state)
	{
		boolean ret = false;
		OAVAttributeType attrtype = null;
		OAVObjectType tmptype = state.getType(parent);
		
		String attrnameplu = attrname.endsWith("y")? attrname.substring(0, attrname.length()-1)+"ies": attrname+"s"; 
		
		while(attrtype==null && tmptype!=null)
		{
			String tmpnamesin = tmptype.getName()+"_has_"+attrname;
			String tmpnameplu = tmptype.getName()+"_has_"+attrnameplu;
			
			attrtype = tmptype.getDeclaredAttributeType0(tmpnamesin);
			if(attrtype==null)
				attrtype = tmptype.getDeclaredAttributeType0(tmpnameplu);
			
			if(attrtype==null)
				tmptype = tmptype.getSupertype();
		}
		
		if(attrtype!=null)
		{
			setAttributeValue(state, parent, attrtype, elem);
			ret = true;
		}
		
		return ret;
	}
	
	/**
	 *  Set/add an attribute value.
	 */
	protected void setAttributeValue(IOAVState state, Object parent, OAVAttributeType attrtype, Object elem)
	{
		if(attrtype.getMultiplicity().equals(OAVAttributeType.NONE))
		{
			state.setAttributeValue(parent, attrtype, elem);
		}
		else
		{
			try
			{
				state.addAttributeValue(parent, attrtype, elem);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *  Create type infos for each tag sorted by specificity.
	 *  @param linkinfos The mapping infos.
	 *  @return Map of mapping infos.
	 */
	protected Map createTypeInfos(Set typeinfos)
	{
		Map ret = new HashMap();
		
		for(Iterator it=typeinfos.iterator(); it.hasNext(); )
		{
			OAVMappingInfo mapinfo = (OAVMappingInfo)it.next();
			TreeSet maps = (TreeSet)ret.get(mapinfo.getXMLTag());
			if(maps==null)
			{
				maps = new TreeSet(new AbstractOAVInfo.SpecificityComparator());
				ret.put(mapinfo.getXMLTag(), maps);
			}
			maps.add(mapinfo);
		}
		
		return ret;
	}
	
	/**
	 *  Create link infos for each tag sorted by specificity.
	 *  @param linkinfos The link infos.
	 *  @return Map of link infos.
	 */
	protected Map createLinkInfos(Set linkinfos)
	{
		Map ret = new HashMap();
		
		for(Iterator it=linkinfos.iterator(); it.hasNext(); )
		{
			OAVLinkInfo linkinfo = (OAVLinkInfo)it.next();
			TreeSet links = (TreeSet)ret.get(linkinfo.getXMLTag());
			if(links==null)
			{
				links = new TreeSet(new AbstractOAVInfo.SpecificityComparator());
				ret.put(linkinfo.getXMLTag(), links);
			}
			links.add(linkinfo);
		}
		
		return ret;
	}
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	protected OAVMappingInfo getMappingInfo(String tag, String fullpath)
	{
		OAVMappingInfo ret = null;
		Set maps = (Set)typeinfos.get(tag);
		if(maps!=null)
		{
			for(Iterator it=maps.iterator(); ret==null && it.hasNext(); )
			{
				OAVMappingInfo tmp = (OAVMappingInfo)it.next();
				if(fullpath.endsWith(tmp.getXMLPath()))
					ret = tmp;
			}
		}
		return ret;
	}
	
	/**
	 *  Get the most specific link info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific link info.
	 */
	protected OAVLinkInfo getLinkInfo(String tag, String fullpath)
	{
		OAVLinkInfo ret = null;
		Set links = (Set)linkinfos.get(tag);
		if(links!=null)
		{
			for(Iterator it=links.iterator(); ret==null && it.hasNext(); )
			{
				OAVLinkInfo tmp = (OAVLinkInfo)it.next();
				if(fullpath.endsWith(tmp.getXMLPath()))
					ret = tmp;
			}
		}
		return ret;
	}
	
	/**
	 *  Get the xml path for a stack.
	 *  @param stack The stack.
	 *  @return The string representig the xml stack (e.g. tag1/tag2/tag3)
	 */
	protected String getXMLPath(List stack)
	{
		StringBuffer ret = new StringBuffer();
		for(int i=0; i<stack.size(); i++)
		{
			ret.append(((StackElement)stack.get(i)).getTag());
			if(i<stack.size()-1)
				ret.append("/");
		}
		return ret.toString();
	}
}