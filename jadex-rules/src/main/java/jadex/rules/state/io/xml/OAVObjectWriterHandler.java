package jadex.rules.state.io.xml;

import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.BasicTypeConverter;
import jadex.commons.xml.writer.AbstractObjectWriterHandler;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 *  OAV version for fetching write info for an object. 
 */
public class OAVObjectWriterHandler extends AbstractObjectWriterHandler
{
	//-------- methods --------
	
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
	 *  Get a value from an object.
	 */
	protected Object getValue(Object object, Object attr, Object context)
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
		if(property instanceof String)
			System.out.println("test");
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
	protected Collection getProperties(Object object, Object context)
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
	 *  Get write info for an object.
	 * /
	public WriteObjectInfo getObjectWriteInfo(Object object, TypeInfo typeinfo, Object context)
	{
		// todo: conversion value to string
		
		WriteObjectInfo wi = new WriteObjectInfo();
		HashSet doneprops = new HashSet();
		IOAVState state = (IOAVState)context;
		
		if(typeinfo!=null)
		{
			// Comment
			
			Object info = typeinfo.getCommentInfo();
			OAVAttributeType property = getProperty(info);
			doneprops.add(property);
			if(info!=null && !(info instanceof AttributeInfo && ((AttributeInfo)info).isIgnoreWrite()))
			{
				try
				{
					Object value = getValue(object, property, state);
					if(value!=null)
					{
						wi.setComment(value.toString());
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			// Content
			
			info = typeinfo.getContentInfo();
			property = getProperty(info);
			doneprops.add(property);
			if(info!=null && !(info instanceof AttributeInfo && ((AttributeInfo)info).isIgnoreWrite()))
			{
				try
				{
					Object value = getValue(object, property, state);
					if(value!=null)
					{
						wi.setContent(value.toString());
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			// Attributes
			
			Collection attrinfos = typeinfo.getAttributeInfos();
			if(attrinfos!=null)
			{
				for(Iterator it=attrinfos.iterator(); it.hasNext(); )
				{
					info = it.next();
					property = getProperty(info);
					doneprops.add(property);
					if(!(info instanceof AttributeInfo && ((AttributeInfo)info).isIgnoreWrite()))
					{	
						try
						{
							Object value = getValue(object, property, state);
							if(value!=null)
							{
								Object defval = null;
								if(info instanceof OAVAttributeInfo)
									defval = ((OAVAttributeInfo)info).getDefaultValue();
								
								if(!value.equals(defval))
								{
									String xmlattrname = null;
									if(info instanceof AttributeInfo)
										xmlattrname = ((AttributeInfo)info).getXMLAttributeName();
									if(xmlattrname==null)
										xmlattrname = getPropertyName(property);
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
						property = getProperty(info);
						doneprops.add(property);
						if(!(info instanceof AttributeInfo && ((AttributeInfo)info).isIgnoreWrite()))
						{	
							Object value = getValue(object, property, state);
							if(value!=null)
							{
								String xmlsoname = soinfo.getXMLPath()!=null? soinfo.getXMLPath(): getPropertyName(property);
								wi.addSubobject(xmlsoname, value);
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
			
		OAVObjectType type = state.getType(object);
		while(type!=null && !(type instanceof OAVJavaType))
		{
			Collection props = type.getDeclaredAttributeTypes();
			if(props!=null)
			{
				for(Iterator it=props.iterator(); it.hasNext(); )
				{
					OAVAttributeType property = (OAVAttributeType)it.next();
					
					if(!doneprops.contains(property))
					{
						doneprops.add(property);
						try
						{
							Object value = getValue(object, property, state);
			
							if(value!=null)
							{
								String propname = getPropertyName(property);
								OAVObjectType atype = property.getType();
								if(atype instanceof OAVJavaType && BasicTypeConverter.isBuiltInType(((OAVJavaType)atype).getClazz()))
								{
									if(!value.equals(property.getDefaultValue()))
										wi.addAttribute(propname, value.toString());
								}
								else
								{
									wi.addSubobject(propname, value);
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
			
			type = type.getSupertype();
		}
		
//		if(typeinfo!=null && typeinfo.getTypeInfo().toString().indexOf("belief")!=-1)
//			System.out.println("here");
		return wi;
	}*/

}

