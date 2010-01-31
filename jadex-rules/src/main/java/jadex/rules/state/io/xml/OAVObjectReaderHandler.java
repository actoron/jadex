package jadex.rules.state.io.xml;

import jadex.commons.SReflect;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.xml.AttributeInfo;
import jadex.xml.BasicTypeConverter;
import jadex.xml.ITypeConverter;
import jadex.xml.ObjectInfo;
import jadex.xml.SXML;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.TypeInfoTypeManager;
import jadex.xml.bean.IBeanObjectCreator;
import jadex.xml.reader.IObjectReaderHandler;
import jadex.xml.reader.LinkData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;


/**
 *  Handler for reading XML into OAV objects.
 */
public class OAVObjectReaderHandler implements IObjectReaderHandler
{
	//-------- attributes --------
	
	/** The type info path manager. */
	protected TypeInfoPathManager tipmanager;
	
	/** The type info manager. */
	// For special case that an object is created via the built-in 
	// tag mechanism and there is a type info for that kind of created
	// object. Allows specifying generic type infos with interfaces.
	protected TypeInfoTypeManager titmanager;
	
	/** No type infos. */
	protected Set no_typeinfos;
	
	//-------- constructors --------
	
	/**
	 *  Create a new handler.
	 */
	public OAVObjectReaderHandler(Set typeinfos)
	{
		this.tipmanager = new TypeInfoPathManager(typeinfos);
		this.titmanager = new TypeInfoTypeManager(typeinfos);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public TypeInfo getTypeInfo(QName tag, QName[] fullpath, Map rawattributes)
	{
		return tipmanager.getTypeInfo(tag, fullpath, rawattributes);
	}
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public TypeInfo getTypeInfo(Object object, QName[] fullpath, Object context)
	{
		Object type = getObjectType(object, context);
		if(no_typeinfos!=null && no_typeinfos.contains(type))
			return null;
			
		TypeInfo ret = titmanager.getTypeInfo(type, fullpath);
		// Hack! due to HashMap.Entry is not visible as class
		if(ret==null)
		{
			if(type instanceof Class)
			{
				// Class name not necessary no more
//				Class clazz = (Class)type;
//				type = SReflect.getClassName(clazz);
//				ret = findTypeInfo((Set)typeinfos.get(type), fullpath);
//				if(ret==null)
//				{
				
				// Try if interface or supertype is registered
				List tocheck = new ArrayList();
				tocheck.add(type);
				
				for(int i=0; i<tocheck.size() && ret==null; i++)
				{
					Class clazz = (Class)tocheck.get(i);
					Set tis = titmanager.getTypeInfosByType(clazz);
					ret = titmanager.findTypeInfo(tis, fullpath);
					if(ret==null)
					{
						Class[] interfaces = clazz.getInterfaces();
						for(int j=0; j<interfaces.length; j++)
							tocheck.add(interfaces[j]);
						clazz = clazz.getSuperclass();
						if(clazz!=null)
							tocheck.add(clazz);
					}
				}
				
				// Special case array
				// Requires Object[].class being registered 
				if(ret==null && ((Class)type).isArray())
				{
					ret = titmanager.findTypeInfo(titmanager.getTypeInfosByType(Object[].class), fullpath);
				}
				
				// Add concrete class for same info if it is used
				if(ret!=null)
				{
					ObjectInfo cri =ret.getObjectInfo();
					ObjectInfo cricpy = cri!=null? new ObjectInfo(type, cri.getPostProcessor()): new ObjectInfo(type);
					
					TypeInfo ti = new TypeInfo(ret.getXMLInfo(),
						cricpy, ret.getMappingInfo(), ret.getLinkInfo());
					
//					TypeInfo ti = new TypeInfo(ret.getSupertype(), ret.getXMLPath(), 
//						type, ret.getCommentInfo(), ret.getContentInfo(), 
//						ret.getDeclaredAttributeInfos(), ret.getPostProcessor(), ret.getFilter(), 
//						ret.getDeclaredSubobjectInfos());
					
					titmanager.addTypeInfo(ti);
				}
				else
				{
					if(no_typeinfos==null)
						no_typeinfos = new HashSet();
					no_typeinfos.add(type);
				}
			}
		}
		
		return ret;
	}
	
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
	 *  Get the object type
	 *  @param object The object.
	 *  @return The object type.
	 */
	public Object getObjectType(Object object, Object context)
	{
		return ((IOAVState)context).getType(object);
	}
	
	/**
	 *  Convert an object to another type of object.
	 */
	public Object convertContentObject(Object object, QName tag, Object context, ClassLoader classloader)
	{
		Object ret = object;
		if(tag.getNamespaceURI().startsWith(SXML.PROTOCOL_TYPEINFO))
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
		Object attrinfo, Object context, ClassLoader classloader, Object root, Map readobjects) throws Exception
	{
		// todo: implement idref!
		
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
	
	/**
	 *  Bulk link an object to its parent.
	 *  @param parent The parent object.
	 *  @param children The children objects (link datas).
	 *  @param context The context.
	 *  @param classloader The classloader.
	 *  @param root The root object.
	 */
	public void bulkLinkObjects(Object parent, List children, Object context, 
		ClassLoader classloader, Object root) throws Exception
	{
//		System.out.println("bulk link for: "+parent+" "+children);
		for(int i=0; i<children.size(); i++)
		{
			LinkData linkdata = (LinkData)children.get(i);
			
			linkObject(linkdata.getChild(), parent, linkdata.getLinkinfo(), 
				linkdata.getPathname(), context, classloader, root);
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

}