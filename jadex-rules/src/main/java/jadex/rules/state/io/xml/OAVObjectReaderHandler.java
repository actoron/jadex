package jadex.rules.state.io.xml;

import jadex.commons.SReflect;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.BasicTypeConverter;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.bean.IBeanObjectCreator;
import jadex.commons.xml.reader.IObjectReaderHandler;
import jadex.commons.xml.reader.Reader;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 *  Handler for reading XML into OAV objects.
 */
public class OAVObjectReaderHandler implements IObjectReaderHandler
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
		
		if(type instanceof TypeInfo)
			type =  ((TypeInfo)type).getTypeInfo();
		
		if(type instanceof OAVObjectType)
		{
			ret	= root? state.createRootObject((OAVObjectType)type): state.createObject((OAVObjectType)type);
		}
		else if(type instanceof Class)
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
		else if(type instanceof QName)
		{
//			System.out.println("here: "+typeinfo);
			QName tag = (QName)type;
			OAVObjectType oavtype = state.getTypeModel().getObjectType(tag.getLocalPart());
			if(oavtype!=null)
			{
				ret = root? state.createRootObject(oavtype): state.createObject(oavtype);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Convert an object to another type of object.
	 */
	public Object convertContentObject(Object object, QName tag, Object context, ClassLoader classloader)
	{
		Object ret = object;
		if(tag.getNamespaceURI().startsWith(Reader.PACKAGE_PROTOCOL))
		{
			String clazzname = tag.getNamespaceURI().substring(8)+"."+tag.getLocalPart();
			Class clazz = SReflect.classForName0(clazzname, classloader);
			if(clazz!=null)
			{
				if(!BasicTypeConverter.isBuiltInType(clazz))
					throw new RuntimeException("No converter known for: "+clazz);
				ret = BasicTypeConverter.getBasicConverter(clazz).convertObject(object, null, classloader, context);
			}
		}
		
		// todo: also support OAVObjectTypes as tagname for conversion? 
//		else
//		{
//			IOAVState state = (IOAVState)context;
//			
//			OAVObjectType type = state.getTypeModel().getObjectType(tag.getLocalPart());
//			if(type!=null)
//			{
//				ret = state.createObject(type);
//				Collection attrs = type.getDeclaredAttributeTypes();
//			}
//		}
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
	public void handleAttributeValue(Object object, QName xmlattrname, List attrpath, String attrval, 
		Object attrinfo, Object context, ClassLoader classloader, Object root) throws Exception
	{
		// If attrval==null only set if default value available.
		if(attrval==null && !(attrinfo instanceof AttributeInfo && ((AttributeInfo)attrinfo).getDefaultValue()!=null))
			return;
		
		IOAVState state = (IOAVState)context;

		OAVAttributeType attrtype = null;
		Object val = attrval;
		
		if(attrinfo instanceof AttributeInfo)
		{
			AttributeInfo info = (AttributeInfo)attrinfo;
			attrtype = (OAVAttributeType)info.getAttributeIdentifier();
			if(val==null && ((AttributeInfo)attrinfo).getDefaultValue()!=null)
				val = ((AttributeInfo)attrinfo).getDefaultValue();
			
			if(info instanceof AttributeInfo)
			{
				ITypeConverter conv = ((AttributeInfo)info).getConverterRead();
				if(conv!=null)
					val = conv.convertObject(attrval, root, classloader, null);
			}
		}
		else if(attrinfo instanceof OAVAttributeType)
		{
			attrtype = (OAVAttributeType)attrinfo;
		}
		else if(attrinfo!=null)
		{
			throw new RuntimeException("Unknown attribute info: "+attrinfo);
		}
		
		// Search attribute in type and supertypes.
		if(attrtype==null)
		{
			int	pathidx	= 0;
			String	tmpname	= xmlattrname.getLocalPart();
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
					tmpname	= ((QName)attrpath.get(pathidx)).getLocalPart();
				pathidx++;
			}
			while(attrtype==null && attrpath!=null && attrpath.size()>=pathidx);
		}
		
		if(attrtype!=null)
		{
			Object arg = val instanceof String && attrtype.getType() instanceof OAVJavaType 
				&& BasicTypeConverter.isBuiltInType(((OAVJavaType)attrtype.getType()).getClazz())?
				BasicTypeConverter.getBasicConverter((((OAVJavaType)attrtype.getType()).getClazz())).convertObject(attrval, root, classloader, null):
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
	public void linkObject(Object elem, Object parent, Object linkinfo, QName[] pathname, Object context, ClassLoader classloader, Object root) throws Exception
	{
		IOAVState state = (IOAVState)context;
	
//		int idx = pathname.lastIndexOf("/");
//		String tagname = idx!=-1? pathname.substring(idx+1): pathname;
		String tagname = pathname[pathname.length-1].getLocalPart();
		
//		System.out.println("link: "+elem+" "+parent);
		
		// Find attribute where to set/add the child element.
		
		boolean linked = false;
		
		OAVAttributeType attrtype = null;

		if(linkinfo instanceof AttributeInfo)
		{
			AttributeInfo info = (AttributeInfo)linkinfo;
			attrtype = (OAVAttributeType)info.getAttributeIdentifier();
			ITypeConverter conv = info.getConverterRead();
			if(conv!=null)
				elem = conv.convertObject(elem, root, classloader, null);
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