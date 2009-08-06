package jadex.commons.xml.bean;

import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.BasicTypeConverter;
import jadex.commons.xml.writer.AbstractObjectWriterHandler;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 *  Java bean version for fetching write info for an object. 
 */
public class BeanObjectWriterHandler extends AbstractObjectWriterHandler
{
	//-------- attributes --------
	
	/** The bean introspector. */
	protected IBeanIntrospector introspector = new BeanReflectionIntrospector();
	
	//-------- methods --------
	
	/**
	 *  Get the object type
	 *  @param object The object.
	 *  @return The object type.
	 */
	public Object getObjectType(Object object, Object context)
	{
		return object.getClass();
	}

	/**
	 *  Get a value from an object.
	 */
	protected Object getValue(Object object, Object attr, Object context)
	{
		Object value = null;
		try
		{
			Method method;
			if(attr instanceof BeanProperty)
				method = ((BeanProperty)attr).getGetter();
			else if(attr instanceof String)
				method = findGetMethod(object, (String)attr, new String[]{"get", "is"});
			else
				throw new RuntimeException("Unknown attribute type: "+attr);
			
			value = method.invoke(object, new Object[0]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return value;
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
		else if(info instanceof String)
		{
			ret = info;
		}
		return ret;
	}
	
	/**
	 *  Get the name of a property.
	 */
	protected String getPropertyName(Object property)
	{
		String ret;
		if(property instanceof BeanProperty)
			ret = ((BeanProperty)property).getName();
		else if(property instanceof String)
			ret = (String)property;
		else
			throw new RuntimeException("Unknown property type: "+property);
		return ret;
	}

	/**
	 *  Test if a value is a basic type.
	 */
	protected boolean isBasicType(Object property, Object value)
	{
		return BasicTypeConverter.isBuiltInType(value.getClass());
	}
	
	/**
	 *  Get the properties of an object. 
	 */
	protected Collection getProperties(Object object, Object context)
	{
		return introspector.getBeanProperties(object.getClass()).values();
	}

	/**
	 *  Find a get method with some prefix.
	 *  @param object The object.
	 *  @param name The name.
	 *  @param prefixes The prefixes to test.
	 */
	protected Method findGetMethod(Object object, String name, String[] prefixes)
	{
		Method method = null;
		for(int i=0; i<prefixes.length && method==null; i++)
		{
			String methodname = prefixes[i]+name.substring(0, 1).toUpperCase()+name.substring(1);
			try
			{
				method = object.getClass().getMethod(methodname, new Class[0]);
			}
			catch(Exception e)
			{
				// nop
			}
		}
		if(method==null)
			throw new RuntimeException("No getter found for: "+name);
		
		return method;
	}
	
	/**
	 *  Get write info for an object.
	 * /
	public WriteObjectInfo getObjectWriteInfo(Object object, TypeInfo typeinfo, Object context)
	{
		// todo: handle proper attribute -> string conversion
		
		WriteObjectInfo wi = new WriteObjectInfo();
		HashSet doneprops = new HashSet();
		
		if(typeinfo!=null)
		{
			// Comment
			
			Object info = typeinfo.getCommentInfo();
			String propname = getPropertyName(info);
			doneprops.add(propname);
			if(info!=null && !(info instanceof AttributeInfo && ((AttributeInfo)info).isIgnoreWrite()))
			{
				try
				{
					Method method = findGetMethod(object, propname, new String[]{"get", "is"});
					Object value = method.invoke(object, new Object[0]);
					if(value!=null)
					{
						wi.setComment(""+value);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			// Content
			
			info = typeinfo.getContentInfo();
			propname = getPropertyName(info);
			doneprops.add(propname);
			if(info!=null && !(info instanceof AttributeInfo && ((AttributeInfo)info).isIgnoreWrite()))
			{
				try
				{
					Method method = findGetMethod(object, propname, new String[]{"get", "is"});
					Object value = method.invoke(object, new Object[0]);
					if(value!=null)
					{
						wi.setContent(""+value);
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
					propname = getPropertyName(info);
					doneprops.add(propname);
					if(info!=null && !(info instanceof AttributeInfo && ((AttributeInfo)info).isIgnoreWrite()))
					{
						try
						{
							Method method = findGetMethod(object, propname, new String[]{"get", "is"});
							
							Object value = method.invoke(object, new Object[0]);
							if(value!=null)
							{
								String xmlattrname = null;
								if(info instanceof AttributeInfo)
									xmlattrname = ((AttributeInfo)info).getXMLAttributeName();
								if(xmlattrname==null)
									xmlattrname = propname;
								wi.addAttribute(xmlattrname, ""+value);
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
						propname = getPropertyName(info);
						doneprops.add(propname);
						if(info!=null && !(info instanceof AttributeInfo && ((AttributeInfo)info).isIgnoreWrite()))
						{
							Method method = findGetMethod(object, propname, new String[]{"get"});
						
							Object value = method.invoke(object, new Object[0]);
							if(value!=null)
							{
								String xmlsoname = soinfo.getXMLTag()!=null? soinfo.getXMLTag(): propname;
								wi.addSubobject(xmlsoname, value);
								doneprops.add(propname);
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
			
		Map props = introspector.getBeanProperties(object.getClass());
		if(props!=null)
		{
			for(Iterator it=props.keySet().iterator(); it.hasNext(); )
			{
				String propname = (String)it.next();
				
				if(!doneprops.contains(propname))
				{
					BeanProperty bp = (BeanProperty)props.get(propname);
					try
					{
						Object value = bp.getGetter().invoke(object, new Object[0]);
		
						if(value!=null)
						{
							if(BasicTypeConverter.isBuiltInType(bp.getType()))
							{
								wi.addAttribute(propname, ""+value);
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
		
		return wi;
	}*/
	
	/**
	 *  Get the property name.
	 *  @param info The info.
	 *  @return The property name.
	 * /
	protected String getPropertyName(Object info)
	{
		String ret;
		if(info instanceof AttributeInfo)
		{
			ret = (String)((AttributeInfo)info).getAttributeIdentifier();
		}
		else if(info instanceof String)
		{
			ret = (String)info;
		}
		else
		{
			throw new RuntimeException("Unknown info type: "+info);
		}
		return ret;
	}*/
}

