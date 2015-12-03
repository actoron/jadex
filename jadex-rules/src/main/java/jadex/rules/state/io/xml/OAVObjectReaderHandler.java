package jadex.rules.state.io.xml;

import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.xml.AttributeInfo;
import jadex.xml.IPostProcessor;
import jadex.xml.SXML;
import jadex.xml.StackElement;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.bean.IBeanObjectCreator;
import jadex.xml.reader.AReadContext;
import jadex.xml.reader.IObjectReaderHandler;
import jadex.xml.reader.LinkData;
import jadex.xml.stax.QName;


/**
 *  Handler for reading XML into OAV objects.
 */
public class OAVObjectReaderHandler implements IObjectReaderHandler
{
	//-------- constants --------
	
	/** Key of the state in the user context map. */
	public static final String	CONTEXT_STATE	= "state";
	
	//-------- constructors --------
	
	/**
	 *  Create a new handler.
	 */
	public OAVObjectReaderHandler()
	{
	}
	
	//-------- methods --------
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public TypeInfo	getTypeInfo(Object object, QName[] fullpath, AReadContext context)
	{
		return null;
	}
	
	/**
	 *  Create an object for the current tag.
	 *  @param type The object type to create.
	 *  @param root Flag, if object should be root object.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(Object type, boolean root, AReadContext context, Map rawattributes) throws Exception
	{
		Object ret = null;
		IOAVState state = (IOAVState)((Map)context.getUserContext()).get(CONTEXT_STATE);
		
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
			ret = ((IBeanObjectCreator)type).createObject(context, rawattributes);
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
	 *  Get the object type
	 *  @param object The object.
	 *  @return The object type.
	 */
	public Object getObjectType(Object object, AReadContext context)
	{
		return ((IOAVState)context).getType(object);
	}
	
	/**
	 *  Convert an object to another type of object.
	 */
	public Object convertContentObject(String object, QName tag, AReadContext context) throws Exception
	{
		Object ret = object;
		if(tag.getNamespaceURI().startsWith(SXML.PROTOCOL_TYPEINFO))
		{
			String clazzname = tag.getNamespaceURI().substring(8)+"."+tag.getLocalPart();
			Class clazz = SReflect.classForName0(clazzname, context.getClassLoader());
			if(clazz!=null)
			{
				if(!BasicTypeConverter.isBuiltInType(clazz))
					throw new RuntimeException("No converter known for: "+clazz);
				ret = BasicTypeConverter.getBasicStringConverter(clazz).convertString(object, context);
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
		Object attrinfo, AReadContext context) throws Exception
	{
		// todo: implement idref!
		
//		if(xmlattrname!=null && xmlattrname.getLocalPart().equals("ref"))
//			System.out.println("ref: "+xmlattrname);
		
		// If attrval==null only set if default value available.
		if(attrval==null && !(attrinfo instanceof AttributeInfo && ((AttributeInfo)attrinfo).getAccessInfo().getDefaultValue()!=null))
			return;
		
		IOAVState state = (IOAVState)((Map)context.getUserContext()).get(CONTEXT_STATE);

		OAVAttributeType attrtype = null;
		Object val = attrval;
		
		if(attrinfo instanceof AttributeInfo)
		{
			AttributeInfo info = (AttributeInfo)attrinfo;
			attrtype = (OAVAttributeType)info.getAttributeIdentifier();
			if(val==null && info.getAccessInfo().getDefaultValue()!=null)
			{
				val = info.getAccessInfo().getDefaultValue();
			}
			else
			{				
				IStringObjectConverter conv = ((AttributeInfo)info).getConverter();
				if(conv!=null)
				{
					val = conv.convertString(attrval, null);
				}
			}
		}
		else if(attrinfo instanceof OAVAttributeType)
		{
			attrtype = (OAVAttributeType)attrinfo;
		}
		else if(attrinfo!=null)
		{
			StackElement	se	= context.getTopStackElement();
			context.getReporter().report("Unknown attribute info: "+attrinfo, "attribute error", context, se.getLocation());
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
			try
			{
				Object arg = val instanceof String && attrtype.getType() instanceof OAVJavaType 
					&& BasicTypeConverter.isBuiltInType(((OAVJavaType)attrtype.getType()).getClazz())?
					BasicTypeConverter.getBasicStringConverter((((OAVJavaType)attrtype.getType()).getClazz()))
						.convertString(attrval, null): val;
		
				setAttributeValue(state, object, attrtype, arg);
			}
			catch(Exception e)
			{
				StackElement	se	= context.getTopStackElement();
				context.getReporter().report(e.toString(), "attribute error", context, se.getLocation());
			}
		}
		else
		{
			StackElement	se	= context.getTopStackElement();
			context.getReporter().report("Unhandled attribute: "+object+", "+xmlattrname+", "+attrpath, "unhandled attribute", context, se.getLocation());
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
	public void linkObject(Object elem, Object parent, Object linkinfo, QName[] pathname, AReadContext context) throws Exception
	{
		IOAVState state = (IOAVState)((Map)context.getUserContext()).get(CONTEXT_STATE);
	
//		int idx = pathname.lastIndexOf("/");
//		String tagname = idx!=-1? pathname.substring(idx+1): pathname;
		String tagname = pathname[pathname.length-1].getLocalPart();
		
//		System.out.println("link: "+elem+" "+parent);
		
		// Find attribute where to set/add the child element.
		
		boolean linked = false;
		
		OAVAttributeType attrtype = null;

		if(linkinfo instanceof SubobjectInfo)
		{
			SubobjectInfo info = (SubobjectInfo)linkinfo;
//			if(!(info.getAccessInfo().getObjectIdentifier() instanceof OAVAttributeType))
//			{
//				System.out.println("kldg");
//			}
			attrtype = (OAVAttributeType)info.getAccessInfo().getObjectIdentifier();
			
			// todo:?
//			IStringObjectConverter conv = info.getConverter();
//			if(conv!=null)
//				elem = conv.convertString(elem, null);
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
		{
			context.getReporter().report("Could not link: "+elem+" "+parent, "Could not link", context, context.getLocation());
//			throw new RuntimeException("Could not link: "+elem+" "+parent);
		}
	}
	
	/**
	 *  Bulk link an object to its parent.
	 *  @param parent The parent object.
	 *  @param children The children objects (link datas).
	 *  @param context The context.
	 *  @param classloader The classloader.
	 *  @param rootobject The root object.
	 */
	public void bulkLinkObjects(Object parent, List children, AReadContext context) throws Exception
	{
//		System.out.println("bulk link for: "+parent+" "+children);
		for(int i=0; i<children.size(); i++)
		{
			LinkData linkdata = (LinkData)children.get(i);
			
			linkObject(linkdata.getChild(), parent, linkdata.getLinkinfo(), 
				linkdata.getPathname(), context);
		}
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
	
	/**
	 *  Get the post-processor.
	 *  @return The post-processor
	 */
	public IPostProcessor[] getPostProcessors(Object object, Object typeinfo)
	{
		IPostProcessor pp = typeinfo instanceof TypeInfo? ((TypeInfo)typeinfo).getPostProcessor(): null;
		return pp==null? null: new IPostProcessor[]{pp};
	}

}
