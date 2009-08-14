package jadex.commons.xml.bean;

import jadex.commons.SReflect;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.BasicTypeConverter;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.writer.AbstractObjectWriterHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *  Java bean version for fetching write info for an object. 
 */
public class BeanObjectWriterHandler extends AbstractObjectWriterHandler
{
	//-------- attributes --------
	
	/** The bean introspector. */
	protected IBeanIntrospector introspector = new BeanReflectionIntrospector();
		
	//-------- constructors --------
	
	/**
	 *  Create a new writer.
	 */
	public BeanObjectWriterHandler(Set typeinfos)
	{
		this(false, typeinfos);
	}
	
	/**
	 *  Create a new writer.
	 */
	public BeanObjectWriterHandler(boolean gentypetags, Set typeinfos)
	{
		super(typeinfos);
		this.gentypetags = gentypetags;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public TypeInfo getTypeInfo(Object object, String[] fullpath, Object context)//, Map rawattributes)
	{
		TypeInfo ret = super.getTypeInfo(object, fullpath, context);
		
		// Hack! due to HashMap.Entry is not visible as class
		if(ret==null)
		{
			Object type = getObjectType(object, context);
			if(type instanceof Class)
			{
				Class clazz = (Class)type;
				type = SReflect.getClassName(clazz);
				ret = findTypeInfo((Set)typeinfos.get(type), fullpath);
				
				if(ret==null)
				{
					// Try if one! interface is registered
					
					while(clazz!=null && ret==null)
					{
						Class[] interfaces = clazz.getInterfaces();
						for(int i=0; i<interfaces.length && ret==null ; i++)
						{
							ret = findTypeInfo((Set)typeinfos.get(interfaces[i]), fullpath);
//							throw new RuntimeException("Multiple interfaces matching a given type found: "+tmp+" "+ret+" "+object);
						}
						
						clazz = clazz.getSuperclass();
					}
				}
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
		if(object==null)
			System.out.println("here");
		return object.getClass();
	}
	
	/**
	 *  Get the tag name for an object.
	 */
	public String getTagName(Object object, Object context)
	{
//		return SReflect.getInnerClassName(object.getClass());
		return SReflect.getClassName(object.getClass());
	}

	/**
	 *  Get a value from an object.
	 */
	protected Object getValue(Object object, Object attr, Object context, Object info)
	{
		if(attr==AttributeInfo.THIS)
			return object;
		
		Object value = null;
		try
		{
			Method method;
			if(info instanceof BeanAttributeInfo && ((BeanAttributeInfo)info).getReadMethod()!=null)
				method = ((BeanAttributeInfo)info).getReadMethod();
			else if(attr instanceof BeanProperty)
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
	 *  Test if a value is compatible with the defined typeinfo.
	 */
	protected boolean isTypeCompatible(Object object, TypeInfo info, Object context)
	{
		boolean ret = true;
		if(info!=null)
		{
			Class clazz = (Class)info.getTypeInfo();
			ret = clazz.isAssignableFrom(object.getClass());
		}
		return ret;
	}
}

