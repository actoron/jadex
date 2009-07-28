package jadex.rules.state.io.xml;

import jadex.commons.xml.BasicTypeConverter;
import jadex.commons.xml.BeanAttributeInfo;
import jadex.commons.xml.IBeanObjectCreator;
import jadex.commons.xml.IObjectHandler;
import jadex.commons.xml.ITypeConverter;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import java.util.List;
import java.util.Map;

/**
 *  Handler for reading XML into OAV objects.
 */
public class OAVObjectHandler implements IObjectHandler
{
	//-------- methods --------
	
	/**
	 *  Create an object for the current tag.
	 *  @param type The object type to create.
	 *  @param root Flag, if object should be root object.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(Object type, boolean root, Object context, Map rawattributes, ClassLoader classloader) throws Exception
	{
		Object ret = null;
		IOAVState state = (IOAVState)context;
		
		if(type instanceof OAVObjectType)
		{
			if(root)
			{
				ret	= state.createRootObject((OAVObjectType)type);
			}
			else if(type!=null)
			{
				ret	= state.createObject((OAVObjectType)type);
			}
		}
		if(type instanceof Class)
		{
			Class clazz = (Class)type;
			if(!BasicTypeConverter.isBuiltInType(clazz))
			{
				// Must have empty constructor.
				ret = clazz.newInstance();
			}
		}
		else if(type instanceof IBeanObjectCreator)
		{
			ret = ((IBeanObjectCreator)type).createObject(context, rawattributes, classloader);
		}
		
		return ret;
	}
	
	/**
	 *  Handle the attribute of an object.
	 *  @param object The object.
	 *  @param attrname The attribute name.
	 *  @param attrval The attribute value.
	 *  @param attrinfo The attribute info.
	 *  @param context The context.
	 */
	public void handleAttributeValue(Object object, String xmlattrname, List attrpath, String attrval, 
		Object attrinfo, Object context, ClassLoader classloader, Object root) throws Exception
	{
		// If attrval==null only set if default value available.
		if(attrval==null && !(attrinfo instanceof OAVAttributeInfo && ((OAVAttributeInfo)attrinfo).getDefaultValue()!=null))
			return;
		
		IOAVState state = (IOAVState)context;

		OAVAttributeType attrtype = null;
		Object val = attrval;
		
		if(attrinfo instanceof OAVAttributeInfo)
		{
			OAVAttributeInfo info = (OAVAttributeInfo)attrinfo;
			attrtype = info.getAttribute();
			if(val==null && ((OAVAttributeInfo)attrinfo).getDefaultValue()!=null)
				val = ((OAVAttributeInfo)attrinfo).getDefaultValue();
			ITypeConverter conv = info.getConverter();
			if(conv!=null)
				val = conv.convertObject(attrval, root, classloader);
		}
		else if(attrinfo instanceof OAVAttributeType)
		{
			attrtype = (OAVAttributeType)attrinfo;
		}
		
		// Search attribute in type and supertypes.
		if(attrtype==null)
		{
			int	pathidx	= 0;
			String	tmpname	= xmlattrname;
			do
			{
//				System.out.println("tmpname: "+tmpname);
				String attrnameplu = tmpname.endsWith("y")? tmpname.substring(0, tmpname.length()-1)+"ies": tmpname+"s"; 
				
				// Search in object type and all super types
				OAVObjectType tmptype = state.getType(object);
				while(attrtype==null && tmptype!=null)
				{
					String tmpnamesin = tmptype.getName()+"_has_"+tmpname;
					String tmpnameplu = tmptype.getName()+"_has_"+attrnameplu;
					
					attrtype = tmptype.getDeclaredAttributeType0(tmpnamesin);
					if(attrtype==null)
						attrtype = tmptype.getDeclaredAttributeType0(tmpnameplu);
					
					if(attrtype==null)
						tmptype = tmptype.getSupertype();
				}
				
				// Search for outer tags
				if(attrpath!=null && attrpath.size()>pathidx)
					tmpname	= (String)attrpath.get(pathidx);
				pathidx++;
			}
			while(attrtype==null && attrpath!=null && attrpath.size()>=pathidx);
		}
		
		if(attrtype!=null)
		{
			Object arg = val instanceof String && attrtype.getType() instanceof OAVJavaType 
				&& BasicTypeConverter.isBuiltInType(((OAVJavaType)attrtype.getType()).getClazz())?
				BasicTypeConverter.getBasicConverter((((OAVJavaType)attrtype.getType()).getClazz())).convertObject(attrval, root, classloader):
					val;
	
			setAttributeValue(state, object, attrtype, arg);
		}
		else
		{
			System.out.println("Unhandled attribute: "+object+", "+xmlattrname+", "+attrpath);
		}
	}
	
	/**
	 *  Link an object to its parent.
	 *  @param object The object.
	 *  @param parent The parent object.
	 *  @param linkinfo The link info.
	 *  @param tagname The current tagname (for name guessing).
	 *  @param context The context.
	 */
	public void linkObject(Object elem, Object parent, Object linkinfo, String tagname, Object context, ClassLoader classloader, Object root) throws Exception
	{
		IOAVState state = (IOAVState)context;
		
//		System.out.println("link: "+elem+" "+parent);
		
		// Find attribute where to set/add the child element.
		
		boolean linked = false;
		
		OAVAttributeType attrtype = null;

		if(linkinfo instanceof OAVAttributeInfo)
		{
			OAVAttributeInfo info = (OAVAttributeInfo)linkinfo;
			attrtype = info.getAttribute();
			ITypeConverter conv = info.getConverter();
			if(conv!=null)
				elem = conv.convertObject(elem, root, classloader);
		}
		else if(linkinfo instanceof OAVAttributeType)
		{
			attrtype = (OAVAttributeType)linkinfo;
		}
		
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
	
	//-------- helper methods --------
	
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
	protected void setAttributeValue(IOAVState state, Object object, OAVAttributeType attrtype, Object elem)
	{
		if(attrtype.getMultiplicity().equals(OAVAttributeType.NONE))
		{
			state.setAttributeValue(object, attrtype, elem);
		}
		else
		{
			state.addAttributeValue(object, attrtype, elem);
		}
	}
	

	

}