package jadex.commons.xml.writer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import jadex.commons.SReflect;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;

/**
 *  Abstract base class for an object writer handler. Is object type agnostic and
 *  uses several abstract methods that have to be overridden by concrete handlers.
 */
public abstract class AbstractObjectWriterHandler implements IObjectWriterHandler
{
	//-------- attributes --------
	
	/** Control flag for generating container tags. */
	protected boolean gentypetags = true;
	
	//-------- methods --------
	
	/**
	 *  Get the object type
	 *  @param object The object.
	 *  @return The object type.
	 */
	public abstract Object getObjectType(Object object, Object context);
		
	/**
	 *  Get the tag name for an object.
	 */
	public abstract String getTagName(Object object, Object context);
	
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
						try
						{
							Object value = getValue(object, property, context, info);
							if(value!=null)
							{
								value = convertValue(info, value, classloader, context);
								wi.setComment(value.toString());
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
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
						try
						{
							Object value = getValue(object, property, context, info);
							if(value!=null)
							{
								value = convertValue(info, value, classloader, context);
								wi.setContent(value.toString());
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
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
							try
							{
								Object value = getValue(object, property, context, info);
								if(value!=null)
								{
									Object defval = getDefaultValue(info);
									
									if(!value.equals(defval))
									{
										String xmlattrname = null;
										if(info instanceof AttributeInfo)
											xmlattrname = ((AttributeInfo)info).getXMLAttributeName();
										if(xmlattrname==null)
											xmlattrname = getPropertyName(property);
										
										value = convertValue(info, value, classloader, context);
										wi.addAttribute(xmlattrname, value.toString());
									}
								}
							}
							catch(Exception e)
							{
								e.printStackTrace();
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
					try
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
									String xmlsoname = soinfo.getXMLPath()!=null? soinfo.getXMLPath(): getPropertyName(property);
									
									// Hack special case array, todo: support generically via typeinfo
//									if(SReflect.isIterable(value))
									if(soinfo.isMulti() || value.getClass().isArray()
										|| (property.equals(AttributeInfo.THIS) && SReflect.isIterable(value)))
									{
										Iterator it2 = SReflect.getIterator(value);
										if(it2.hasNext())
										{
											while(it2.hasNext())
											{
												Object val = it2.next();
												
												if(isTypeCompatible(val, sotypeinfo, context))
												{
													String pathname = gentypetags? xmlsoname+"/"+getTagName(val, context): xmlsoname;
													wi.addSubobject(pathname, val);
												}
											}
										}
									}
									else
									{
										if(isTypeCompatible(value, sotypeinfo, context))
										{
											String pathname = gentypetags? xmlsoname+"/"+getTagName(value, context): xmlsoname;
											wi.addSubobject(pathname, value);
										}
									}
								}
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		/*else if(isMultiObject(object, context))
		{
			Iterator it = handler.getIterator();
			{
				if(it.hasNext())
				{
					while(it.hasNext())
					{
						wi.addSubobject(gentypetags? propname+"/"+getTagName(val, context): propname, val);
					}
				}
			}
		}*/
			
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
					try
					{
						Object value = getValue(object, property, context, null);
		
						if(value!=null)
						{
							if(isBasicType(property, value))
							{
								if(!value.equals(getDefaultValue(property)))
									wi.addAttribute(propname, value.toString());
							}
							else
							{
								// todo: remove
								// Hack special case array, todo: support generically via typeinfo
								if(value.getClass().isArray())//SReflect.isIterable(value))
//								if(SReflect.isIterable(value))
								{
									Iterator it2 = SReflect.getIterator(value);
									if(it2.hasNext())
									{
										while(it2.hasNext())
										{
											Object val = it2.next();
											wi.addSubobject(gentypetags? propname+"/"+getTagName(val, context): propname, val);
										}
									}
								}
								else
								{
									wi.addSubobject(gentypetags? propname+"/"+getTagName(value, context): propname, value);
								}
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		// Special case that no info about object was found.
		// Hack?!
		if(wi.getAttributes()==null && wi.getSubobjects()==null && wi.getContent()==null)
		{
			// todo: use prewriter
			wi.setContent(object.toString());
		}
		
//		System.out.println("wi: "+object+" "+wi.getContent()+" "+wi.getSubobjects());
		
		return wi;
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
}
