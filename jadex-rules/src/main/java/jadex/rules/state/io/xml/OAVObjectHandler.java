package jadex.rules.state.io.xml;

import jadex.commons.xml.BasicTypeConverter;
import jadex.commons.xml.IObjectHandler;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import javax.xml.stream.XMLStreamReader;

/**
 *  Handler for reading XML into OAV objects.
 */
public class OAVObjectHandler implements IObjectHandler
{
	//-------- methods --------
	
	/**
	 *  Create an object for the current tag.
	 *  @param parser The parser.
	 *  @param comment The preceding xml comment.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(XMLStreamReader parser, Object type, boolean root, Object context) throws Exception
	{
		Object ret = null;
		IOAVState state = (IOAVState)context;
		
		Object	object	= null;
		
		if(type instanceof OAVJavaType && BasicTypeConverter.isBuiltInType(((OAVJavaType)type).getClazz()))
		{
			String strval;
			if(parser.getAttributeCount()==1)
			{	
				strval = parser.getAttributeValue(0);
				ret = BasicTypeConverter.convertBuiltInTypes(((OAVJavaType)type).getClazz(), strval);
			}
		}
		else
		{
			if(root)
			{
				object	= state.createRootObject((OAVObjectType)type);
				ret	= object;
			}
			else if(type!=null)
			{
				object	= state.createObject((OAVObjectType)type);
				ret	= object;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create an object for the current tag.
	 *  @param parser The parser.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public void handleAttributeValue(Object object, String attrname, String attrval, Object attrinfo, Object context) throws Exception
	{
		IOAVState state = (IOAVState)context;

		OAVAttributeType attrtype = (OAVAttributeType)attrinfo;
			
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
	}
	
	/**
	 *  Create an object for the current tag.
	 *  @param parser The parser.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public void handleComment(Object object, String comment, Object commentinfo, Object context) throws Exception
	{
		IOAVState state = (IOAVState)context;
		OAVAttributeType comattr = (OAVAttributeType)commentinfo;
		state.setAttributeValue(object, comattr, comment);
	}
	
	/**
	 *  Handle content for an object.
	 *  @param parser The parser.
	 *  @param comment The comment.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public void handleContent(Object elem, String content, Object contentinfo, Object context) throws Exception
	{
		IOAVState state = (IOAVState)context;
		OAVAttributeType attrtype = (OAVAttributeType)contentinfo;
		setAttributeValue(state, elem, attrtype, content);
	}
	
	/**
	 *  Link an object to its parent.
	 *  @param parser The parser.
	 *  @param elem The element.
	 *  @param paranet The parent element.
	 */
	public void linkObject(Object elem, Object parent, Object linkinfo, String tagname, Object context) throws Exception
	{
		IOAVState state = (IOAVState)context;
		
		// Find attribute where to set/add the child element.
		
		boolean linked = false;
		
		OAVAttributeType attrtype = null;

		if(attrtype!=null)
		{
			setAttributeValue(state, parent, attrtype, elem);
			linked= true;
		}
		
		if(!linked)
		{
			linked = internalLinkObjects(tagname, elem, parent, state);
		}
		
		if(!linked && !(state.getType(elem) instanceof OAVJavaType 
			&& BasicTypeConverter.isBuiltInType(((OAVJavaType)state.getType(elem)).getClazz())))
		{
			linked = internalLinkObjects(state.getType(elem).getName(), elem, parent, state);	
		}	
		
		if(!linked)
			throw new RuntimeException("Could not link: "+elem+" "+parent);
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
	

	

}