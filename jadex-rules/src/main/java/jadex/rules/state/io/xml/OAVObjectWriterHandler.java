package jadex.rules.state.io.xml;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.Namespace;
import jadex.xml.ObjectInfo;
import jadex.xml.SXML;
import jadex.xml.stax.QName;
import jadex.xml.writer.AWriteContext;
import jadex.xml.writer.AbstractObjectWriterHandler;


/**
 *  OAV version for fetching write info for an object. 
 */
public class OAVObjectWriterHandler extends AbstractObjectWriterHandler
{
	//-------- constructors --------
	
	/**
	 *  Create a new writer.
	 */
	public OAVObjectWriterHandler(Set typeinfos)
	{
		this(false, typeinfos);
	}
	
	/**
	 *  Create a new writer.
	 */
	public OAVObjectWriterHandler(boolean gentypetags, Set typeinfos)
	{
		this(gentypetags, false, typeinfos);
	}
	
	/**
	 *  Create a new writer.
	 */
	public OAVObjectWriterHandler(boolean gentypetags, boolean prefertags, Set typeinfos)
	{
		this(gentypetags, prefertags, true, typeinfos);
	}
	
	/**
	 *  Create a new writer.
	 */
	public OAVObjectWriterHandler(boolean gentypetags, boolean prefertags, boolean flattening ,Set typeinfos)
	{
		super(gentypetags, prefertags, flattening, typeinfos);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the object type
	 *  @param object The object.
	 *  @return The object type.
	 */
	public Object getObjectType(Object object, IContext context)
	{
		return ((IOAVState)context).getType(object);
	}
	
	/**
	 *  Get the tag name for an object.
	 */
	public QName getTagName(Object object, IContext context)
	{
		QName ret;
		IOAVState state = (IOAVState)context;
		
		if(state.containsObject(object))
		{
			String typename = state.getType(object).getName();
			ret = new QName(typename);
		}
		else
		{
			String clazzname = SReflect.getClassName(object.getClass());
			Namespace ns;
			int idx = clazzname.lastIndexOf(".");
			String pck = SXML.PROTOCOL_TYPEINFO+clazzname.substring(0, idx);
			String tag = clazzname.substring(idx+1);
			
			AWriteContext wc = (AWriteContext)context;
			ns = wc.getNamespace(pck);
			ret = new QName(ns.getURI(), tag, ns.getPrefix());
		}
		
		return ret;
	}
	
	/**
	 *  Get the tag with namespace.
	 */
	public QName getTagWithPrefix(QName tag, IContext context)
	{
		AWriteContext wc = (AWriteContext)context;
		Namespace ns = wc.getNamespace(tag.getNamespaceURI());
		return new QName(ns.getURI(), tag.getLocalPart(), ns.getPrefix());
	}
	
//	/**
//	 *  Get or create a namespace.
//	 *  @param uri The namespace uri.
//	 */
//	protected Namespace getNamespace(String uri)
//	{
//		Namespace ns = (Namespace)namespacebypackage.get(uri);
//		if(ns==null)
//		{
//			String prefix = "p"+nscnt;
//			ns = new Namespace(prefix, uri);
//			namespacebypackage.put(uri, ns);
//			nscnt++;
//		}
//		return ns;
//	}
	
	/**
	 *  Get a value from an object.
	 */
	protected Object getValue(Object object, Object attr, IContext context, Object info)
	{
		Object ret;
		try
		{
			OAVAttributeType attribute = (OAVAttributeType)attr;
			IOAVState state = (IOAVState)context;
			if(((OAVAttributeType)attr).getMultiplicity().equals(OAVAttributeType.NONE))
			{
				ret = state.getAttributeValue(object, attribute);
			}
			else
			{
				ret = state.getAttributeValues(object, attribute);
			}
		}
		catch(Error e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	/**
	 *  Get the property.
	 */
	protected Object getProperty(Object info)
	{
		Object ret = null;
		if(info instanceof AttributeInfo)
		{
			ret = ((AttributeInfo)info).getAttributeIdentifier();
		}
		else if(info instanceof OAVAttributeType)
		{
			ret = info;
		}
		else
		{
			throw new RuntimeException("Unknown property type: "+info);
		}
		return ret;
	}
	
	/**
	 *  Get the name of a property.
	 *  Cuts off all before "_has_" (hack?!).
	 */
	protected String getPropertyName(Object property)
	{
		String ret = ((OAVAttributeType)property).getName();
		int idx = ret.indexOf("_has_");
		if(idx!=-1)
			ret = ret.substring(idx+5);
		return ret;
	}

	/**
	 *  Test if a value is a basic type.
	 */
	protected boolean isBasicType(Object property, Object value)
	{
		OAVObjectType atype = ((OAVAttributeType)property).getType();
		return atype instanceof OAVJavaType && BasicTypeConverter.isBuiltInType(((OAVJavaType)atype).getClazz());
	}
	
	/**
	 *  Get the properties of an object. 
	 */
	protected Collection getProperties(Object object, IContext context, boolean includemethods, boolean includefields)
	{
		Collection ret = new LinkedHashSet();
		IOAVState state = (IOAVState)context;
		OAVObjectType type = state.getType(object);
		
		
		while(type!=null && !(type instanceof OAVJavaType))
		{
			Collection props = type.getDeclaredAttributeTypes();
			ret.addAll(props);
			type = type.getSupertype();
		}
		
		return ret;
	}
	
	/**
	 *  Get the default value.
	 */
	protected Object getDefaultValue(Object property)
	{
		Object ret = null;
		if(property instanceof OAVAttributeType)
			ret = ((OAVAttributeType)property).getDefaultValue();
		else
		ret = super.getDefaultValue(property);
		return ret;
	}
	
	/**
	 *  Test if a value is compatible with the defined typeinfo.
	 */
	protected boolean isTypeCompatible(Object object, ObjectInfo info, IContext context)
	{
		boolean ret = true;
		if(info!=null && info.getTypeInfo() instanceof OAVObjectType)
		{
			OAVObjectType otype = (OAVObjectType)info.getTypeInfo();
			ret = ((IOAVState)context).getType(object).isSubtype(otype);
		}
		return ret;
	}
	
	/**
	 *  Test if a value is decodable to the same type.
	 *  Works for basic (final) types only and checks if the
	 *  two types are of same class.
	 */
	protected boolean isDecodableToSameType(Object property, Object value, IContext context)
	{
		boolean ret = true;
		if(value!=null)
		{
			OAVAttributeType attr = (OAVAttributeType)property;
			ret = value.getClass().equals(((OAVJavaType)attr.getType()).getClazz());
		}
		return ret;
	}
}

