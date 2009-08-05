package jadex.rules.state.io.xml;

import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.BasicTypeConverter;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.writer.IObjectWriterHandler;
import jadex.commons.xml.writer.WriteObjectInfo;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 *  OAV version for fetching write info for an object. 
 */
public class OAVObjectWriterHandler implements IObjectWriterHandler
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
	 *  Get write info for an object.
	 */
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
			if(info!=null)
			{
				OAVAttributeType property = getProperty(info);
				doneprops.add(property);
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
			if(info!=null)
			{
				OAVAttributeType property = getProperty(info);
				doneprops.add(property);
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
					OAVAttributeType property = getProperty(info);
					doneprops.add(property);
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
			
			// Subobjects 
			
			Collection subobsinfos = typeinfo.getSubobjectInfos();
			if(subobsinfos!=null)
			{
				for(Iterator it=subobsinfos.iterator(); it.hasNext(); )
				{
					try
					{
						SubobjectInfo soinfo = (SubobjectInfo)it.next();
						info = soinfo.getLinkInfo().getAttributeIdentifier();
						OAVAttributeType property = getProperty(info);
						doneprops.add(property);
						Object value = getValue(object, property, state);
						if(value!=null)
						{
							String xmlsoname = soinfo.getXMLPath()!=null? soinfo.getXMLPath(): getPropertyName(property);
							wi.addSubobject(xmlsoname, value);
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
	}
	
	/**
	 *  Get a value from an object.
	 */
	protected Object getValue(Object object, OAVAttributeType attr, IOAVState state)
	{
		Object ret;
		try
		{
			if(attr.getMultiplicity().equals(OAVAttributeType.NONE))
			{
				ret = state.getAttributeValue(object, attr);
			}
			else
			{
				ret = state.getAttributeValues(object, attr);
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
	 *  Get the name of a property.
	 *  Cuts off all before "_has_" (hack?!).
	 */
	protected String getPropertyName(OAVAttributeType property)
	{
		String ret = property.getName();
		int idx = ret.indexOf("_has_");
		if(idx!=-1)
			ret = ret.substring(idx+5);
		return ret;
	}
	
	/**
	 *  Get the property.
	 */
	protected OAVAttributeType getProperty(Object info)
	{
		OAVAttributeType ret = null;
		if(info instanceof OAVAttributeType)
		{
			ret = (OAVAttributeType)info;
		}
		else if(info instanceof AttributeInfo)
		{
			ret = (OAVAttributeType)((AttributeInfo)info).getAttributeIdentifier();
		}
		return ret;
	}
}

