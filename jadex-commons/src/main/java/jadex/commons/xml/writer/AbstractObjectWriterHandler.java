package jadex.commons.xml.writer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import jadex.commons.SReflect;
import jadex.commons.xml.AbstractInfo;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.TypeInfoTypeManager;

/**
 *  Abstract base class for an object writer handler. Is object type agnostic and
 *  uses several abstract methods that have to be overridden by concrete handlers.
 */
public abstract class AbstractObjectWriterHandler implements IObjectWriterHandler
{
	//-------- attributes --------
	
	/** Control flag for generating container tags. */
	protected boolean gentypetags = true;
	
	/** The type info manager. */
	protected TypeInfoTypeManager titmanager;
	
	//-------- constructors --------
	
	/**
	 *  Create a new writer handler.
	 */
	public AbstractObjectWriterHandler(Set typeinfos)
	{
		this.titmanager = new TypeInfoTypeManager(typeinfos);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the most specific mapping info.
	 *  @param type The type.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public TypeInfo getTypeInfo(Object object, QName[] fullpath, Object context)
	{
		Object type = getObjectType(object, context);
		return titmanager.getTypeInfo(type, fullpath);
	}
	
	/**
	 *  Get the object type
	 *  @param object The object.
	 *  @return The object type.
	 */
	public abstract Object getObjectType(Object object, Object context);
	
	/**
	 *  Get write info for an object.
	 */
	public WriteObjectInfo getObjectWriteInfo(Object object, TypeInfo typeinfo, Object context, ClassLoader classloader)
	{
		// todo: conversion value to string
		
		WriteObjectInfo wi = new WriteObjectInfo();
		HashSet doneprops = new HashSet();
		
		if(typeinfo!=null)
		{
			// Comment
			
			Object info = typeinfo.getCommentInfo();
			if(info!=null)
			{
				Object property = getProperty(info);
				if(property!=null)
				{
					doneprops.add(getPropertyName(property));
					if(!(info instanceof AttributeInfo && ((AttributeInfo)info).isIgnoreWrite()))
					{
						Object value = getValue(object, property, context, info);
						if(value!=null)
						{
							value = convertValue(info, value, classloader, context);
							wi.setComment(value.toString());
						}
					}
				}
			}
			
			// Content
			
			info = typeinfo.getContentInfo();
			if(info!=null)
			{
				Object property = getProperty(info);
				if(property!=null)
				{
					doneprops.add(getPropertyName(property));
					if(!(info instanceof AttributeInfo && ((AttributeInfo)info).isIgnoreWrite()))
					{
						Object value = getValue(object, property, context, info);
						if(value!=null)
						{
							value = convertValue(info, value, classloader, context);
							wi.setContent(value.toString());
						}
					}
				}
			}
			
			// Attributes
			
			Collection attrinfos = typeinfo.getAttributeInfos();
			if(attrinfos!=null)
			{
				for(Iterator it=attrinfos.iterator(); it.hasNext(); )
				{
					info = it.next();
					Object property = getProperty(info);
					if(property!=null)
					{
						doneprops.add(getPropertyName(property));
						if(!(info instanceof AttributeInfo && ((AttributeInfo)info).isIgnoreWrite()))
						{	
							Object value = getValue(object, property, context, info);
							if(value!=null)
							{
								Object defval = getDefaultValue(info);
								
								if(!value.equals(defval))
								{
									String xmlattrname = null;
									if(info instanceof AttributeInfo)
										xmlattrname = ((AttributeInfo)info).getXMLAttributeName().getLocalPart();
									if(xmlattrname==null)
										xmlattrname = getPropertyName(property);
									
									value = convertValue(info, value, classloader, context);
									wi.addAttribute(xmlattrname, value.toString());
								}
							}
						}
					}
				}
			}
			
			// Subobjects 
			
			Collection subobsinfos = typeinfo.getSubobjectInfos();
			if(subobsinfos!=null)
			{
				for(Iterator it=subobsinfos.iterator(); it.hasNext(); )
				{
					SubobjectInfo soinfo = (SubobjectInfo)it.next();
					info = soinfo.getLinkInfo();
					TypeInfo sotypeinfo = soinfo.getTypeInfo();
					Object property = getProperty(info);
					if(property!=null)
					{
						doneprops.add(getPropertyName(property));
						if(!(info instanceof AttributeInfo && ((AttributeInfo)info).isIgnoreWrite()))
						{	
							String propname = getPropertyName(property);
							Object value = getValue(object, property, context, info);
							if(value!=null)
							{
//								String xmlsoname = soinfo.getXMLPath()!=null? soinfo.getXMLPath(): getPropertyName(property);
								QName[] xmlpath = soinfo.getXMLPathElements();
								if(xmlpath==null)
									xmlpath = new QName[]{QName.valueOf(getPropertyName(property))};
								
								// Fetch elements directly if it is a multi subobject
								if(soinfo.isMulti())
								{
									Iterator it2 = SReflect.getIterator(value);
									while(it2.hasNext())
									{
										Object val = it2.next();
										
										if(isTypeCompatible(val, sotypeinfo, context))
										{
											QName[] path = createPath(xmlpath, val, context);
											wi.addSubobject(path, val);
										}
									}
								}
								else
								{
									if(isTypeCompatible(value, sotypeinfo, context))
									{
										QName[] path = createPath(xmlpath, value, context);
										wi.addSubobject(path, value);
									}
								}
							}
						}
					}
				}
			}
		}
			
		// Get properties from type inspection.
		
		Collection props = getProperties(object, context);
		if(props!=null)
		{
			for(Iterator it=props.iterator(); it.hasNext(); )
			{
				Object property = it.next();
				String propname = getPropertyName(property);

				if(!doneprops.contains(propname))
				{
					doneprops.add(propname);
					Object value = getValue(object, property, context, null);
	
					if(value!=null)
					{
						// Make to an attribute when
						// a) it is a basic type
						// b) it can be decoded to the right object type
						if(isBasicType(property, value) && isDecodableToSameType(property, value, context))
						{
							if(!value.equals(getDefaultValue(property)))
								wi.addAttribute(propname, value.toString());
						}
						else
						{
							// todo: remove
							// Hack special case array, todo: support generically via typeinfo???
							QName[] xmlpath = new QName[]{QName.valueOf(propname)};
							
//							if(value.getClass().isArray())
//							{
//								Iterator it2 = SReflect.getIterator(value);
//								if(it2.hasNext())
//								{
//									while(it2.hasNext())
//									{
//										Object val = it2.next();
//										QName[] path = createPath(xmlpath, val, context);
//										wi.addSubobject(path, val);
//									}
//								}
//							}
//							else
							{
								QName[] path = createPath(xmlpath, value, context);
								wi.addSubobject(path, value);
							}
						}
					}
				}
			}
		}
		
		// Special case that no info about object was found.
		// Hack?!
		if(typeinfo==null && wi.getAttributes()==null && wi.getSubobjects()==null && wi.getContent()==null)
		{
			// todo: use prewriter
			wi.setContent(object.toString());
		}
		
//		System.out.println("wi: "+object+" "+wi.getContent()+" "+wi.getSubobjects());
		
		return wi;
	}
	
	/**
	 * 
	 */
	protected QName[] createPath(QName[] xmlpath, Object value, Object context)
	{
		QName[] ret = xmlpath;
		if(gentypetags)
		{
			ret = new QName[xmlpath.length+1];
			System.arraycopy(xmlpath, 0, ret, 0, xmlpath.length);
			QName tag = getTagName(value, context);
			ret[ret.length-1] = tag;
		}
		return ret;
	}
	
	/**
	 *  Convert a value before writing.
	 */
	protected Object convertValue(Object info, Object value, ClassLoader classloader, Object context)
	{
		Object ret = value;
		if(info instanceof AttributeInfo)
		{
			ITypeConverter conv = ((AttributeInfo)info).getConverterWrite();
			if(conv!=null)
			{
				ret = conv.convertObject(value, null, classloader, context);
			}
		}
		return ret;
	}
	
	/**
	 *  Get the default value.
	 */
	protected Object getDefaultValue(Object property)
	{
		Object ret = null;
		if(property instanceof AttributeInfo)
		{
			ret = ((AttributeInfo)property).getDefaultValue();
		}
		return ret;
	}
	
	/**
	 *  Get a value from an object.
	 */
	protected abstract Object getValue(Object object, Object attr, Object context, Object info);
	
	/**
	 *  Get the property.
	 */
	protected abstract Object getProperty(Object info);
	
	/**
	 *  Get the name of a property.
	 */
	protected abstract String getPropertyName(Object property);

	/**
	 *  Get the properties of an object. 
	 */
	protected abstract Collection getProperties(Object object, Object context);

	/**
	 *  Test is a value is a basic type (and can be mapped to an attribute).
	 */
	protected abstract boolean isBasicType(Object property, Object value);
	
	/**
	 *  Test if a value is compatible with the defined typeinfo.
	 */
	protected abstract boolean isTypeCompatible(Object object, TypeInfo info, Object context);
	
	/**
	 *  Test if a value is decodable to the same type.
	 */
	protected abstract boolean isDecodableToSameType(Object property, Object value, Object context); 
}
